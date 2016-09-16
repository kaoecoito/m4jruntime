package br.com.sisprof.m4jruntime.database;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by kaoe on 14/09/16.
 */
class PostgresqlDatabaseStorage implements DatabaseStorage {

    private enum SearchDirection {
        FORWARD,
        BACKWARD
    }

    private static final int[] ESPECIAL_CHARS = {
            'ç','Ç',
            'á','Á','â','Â','ã','Ã','à','À','ä','Ä',
            'é','É','ê','Ê','ẽ','Ẽ','è','È','ë','Ë',
            'í','Í','ì','Ì','ï','Ï',
            'ó','Ó','ô','Ô','õ','Õ','ò','Ò','ö','Ö',
            'ú','Ú','ù','Ù','ü','Ü'
    };
    private static final Set<Integer> ESPECIAL_TABLE = new HashSet<>();
    static {
        for (int item: ESPECIAL_CHARS) {
            ESPECIAL_TABLE.add(item);
        }
    }

    private final Connection connection;

    private Deque<Integer> transactionStack = new LinkedList<>();

    PostgresqlDatabaseStorage(Connection connection) {
        this.connection = connection;
        this.init();
    }

    @Override
    public void close() {
        if (this.connection!=null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private PreparedStatement selectItem;
    private PreparedStatement selectAll;
    private PreparedStatement selectNext;
    private PreparedStatement selectPrev;

    private PreparedStatement insertItem;
    private PreparedStatement mergeItem;

    private PreparedStatement deleteItem;
    private PreparedStatement deleteAll;

    private PreparedStatement lock;
    private PreparedStatement unlock;

    private void init() {
        try {
            selectItem = connection.prepareStatement("select value from global_dataset where key=?");
            selectAll = connection.prepareStatement("select key, value from global_dataset where key>? and key<?");
            selectNext = connection.prepareStatement("select key from global_dataset where key>? order by key limit 1");
            selectPrev = connection.prepareStatement("select key from global_dataset where key<? order by key desc limit 1");

            insertItem = connection.prepareStatement("insert into global_dataset (key,value) values (?,?) ON CONFLICT (key) DO UPDATE SET value=EXCLUDED.value");
            mergeItem = connection.prepareStatement("insert into global_dataset select ? || substring(q.key from ?), q.value from global_dataset as q where q.key>=? and q.key<? ON CONFLICT (key) DO UPDATE SET value=EXCLUDED.value");

            deleteItem = connection.prepareStatement("delete from global_dataset where key=?");
            deleteAll = connection.prepareStatement("delete from global_dataset where key>? and key<?");

            lock = connection.prepareStatement("select pg_try_advisory_lock(?) as result");
            unlock = connection.prepareStatement("select pg_advisory_unlock(?) as result");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private byte[] toBytea(DatabaseKey key) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            List<Object> subscript = key.getSubscripts();
            output.write(key.getGlobal().getBytes("UTF-8"));
            for (Object item:subscript) {
                output.writeByte(0x00);
                if (item instanceof Number) {
                    output.writeByte(0x01);
                    output.writeDouble(((Number)item).doubleValue());
                } else if (item.toString().isEmpty()) {
                    output.writeByte(0xF0);
                } else {
                    String str = item.toString();
                    int chr = str.substring(0, 1).toCharArray()[0];
                    if (('a'<=chr && chr<='z') || ('A'<=chr && chr<='Z') || ESPECIAL_TABLE.contains(chr)) {
                        output.writeByte(0xFA);
                    } else {
                        output.writeByte(0xFF);
                    }
                    output.write(item.toString().getBytes("UTF-8"));
                }
            }
            output.flush();
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    private byte[] toByteaSearch(DatabaseKey key, SearchDirection direction) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        try {
            List<Object> subscript = key.getSubscripts();
            output.write(key.getGlobal().getBytes("UTF-8"));
            for (Object item:subscript) {
                if (item instanceof Number) {
                    output.writeByte(0x00);
                    output.writeByte(0x01);
                    output.writeDouble(((Number)item).doubleValue());
                } else if (item.toString().isEmpty()) {
                    if (SearchDirection.FORWARD.equals(direction)) {
                        output.writeByte(0x00);
                    } else {
                        output.writeByte(0xFF);
                        output.writeByte(0xFF);
                    }
                } else {
                    output.writeByte(0x00);
                    String str = item.toString();
                    int chr = str.substring(0, 1).toCharArray()[0];
                    if (('a'<=chr && chr<='z') || ('A'<=chr && chr<='Z') || ESPECIAL_TABLE.contains(chr)) {
                        output.writeByte(0xFA);
                    } else {
                        output.writeByte(0xFF);
                    }
                    output.write(item.toString().getBytes("UTF-8"));
                }
            }
            output.flush();
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    private DatabaseKey toKey(byte[] bs) {
        DatabaseKey key = null;
        if (bs!=null && bs.length>0) {
            String globalName = null;
            List<Object> items = new ArrayList<>();
            try {
                boolean nullExists;
                ByteArrayInputStream stream = new ByteArrayInputStream(bs);
                DataInputStream input = new DataInputStream(stream);
                StringBuilder builder = new StringBuilder();
                nullExists = readUTFString(input, builder);
                globalName = builder.toString();
                while (true) {
                    if (input.available()==0) break;
                    if (!nullExists) {
                        input.readByte();
                    } else {
                        nullExists = false;
                    }
                    int type = input.readUnsignedByte();
                    if (type==0x01) {
                        Double value = input.readDouble();
                        if (value-value.longValue()==0d) {
                            items.add(value.longValue());
                        } else {
                            items.add(value);
                        }
                    } else if (type==0xF0) {
                        items.add("");
                    } else if (type==0xFA || type==0xFF) {
                        nullExists = readUTFString(input, builder);
                        items.add(builder.toString());
                        builder.setLength(0);
                    }
                }
                input.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (items.isEmpty()) {
                key = DatabaseKey.create(globalName);
            } else {
                key = DatabaseKey.create(globalName, items.toArray());
            }

        }
        return key;
    }

    private boolean readUTFString(DataInputStream input, StringBuilder builder) throws IOException {
        boolean nullExists = false;
        builder.setLength(0);
        while (true) {
            if (input.available()==0) break;
            int bt = input.readUnsignedByte();
            if (bt==0x00) {
                nullExists = true;
                break;
            }
            builder.append((char)bt);
        }
        return nullExists;
    }

    @Override
    public void set(DatabaseKey key, String value) {
        try {
            insertItem.setBytes(1, toBytea(key));
            insertItem.setString(2, value);
            insertItem.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int merge(DatabaseKey from, DatabaseKey to) {
        int total = 0;
        try {
            mergeItem.setBytes(1, toBytea(to));
            mergeItem.setInt(2, toBytea(from).length+1);
            mergeItem.setBytes(3, toByteaSearch(from.nextSubscript(), SearchDirection.FORWARD));
            mergeItem.setBytes(4, toByteaSearch(from.nextSubscript(), SearchDirection.BACKWARD));
            total = mergeItem.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    @Override
    public String get(DatabaseKey key) {
        String value = null;
        try {
            selectItem.setBytes(1, toBytea(key));
            ResultSet result = selectItem.executeQuery();
            if (result.next()) {
                value = result.getString(1);
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public DatabaseKey next(DatabaseKey key) {
        DatabaseKey next = null;
        try {
            if (key.isNullSubscript()) {
                selectNext.setBytes(1, toByteaSearch(key, SearchDirection.FORWARD));
            } else {
                selectNext.setBytes(1, toByteaSearch(key.nextSubscript(), SearchDirection.BACKWARD));
            }
            ResultSet result = selectNext.executeQuery();
            if (result.next()) {
                DatabaseKey tmpNext = toKey(result.getBytes("key")).toSubscriptIndex(key.size());
                if (tmpNext.size()>=key.size() && tmpNext.equalParent(key)) {
                    next = tmpNext;
                }
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return next;
    }

    @Override
    public DatabaseKey prev(DatabaseKey key) {
        DatabaseKey prev = null;
        try {
            if (key.isNullSubscript()) {
                selectPrev.setBytes(1, toByteaSearch(key, SearchDirection.BACKWARD));
            } else {
                selectPrev.setBytes(1, toByteaSearch(key, SearchDirection.FORWARD));
            }
            ResultSet result = selectPrev.executeQuery();
            if (result.next()) {
                DatabaseKey tmpPrev = toKey(result.getBytes("key")).toSubscriptIndex(key.size());
                if (tmpPrev.size()>=key.size() && tmpPrev.equalParent(key)) {
                    prev = tmpPrev;
                }
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prev;
    }

    @Override
    public boolean delete(DatabaseKey key) {
        boolean result = false;
        try {
            deleteItem.setBytes(1, toBytea(key));
            result = (deleteItem.executeUpdate()>0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int deleteAll(DatabaseKey key) {
        int result = 0;
        try {
            deleteItem.setBytes(1, toBytea(key));
            result = deleteItem.executeUpdate();

            DatabaseKey nextKey = key.nextSubscript();
            deleteAll.setBytes(1, toByteaSearch(nextKey, SearchDirection.FORWARD));
            deleteAll.setBytes(2, toByteaSearch(nextKey, SearchDirection.BACKWARD));
            result += deleteAll.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void getAll(DatabaseKey key, BiConsumer<DatabaseKey, String> callback) {
        try {
            String value = get(key);
            if (value!=null) {
                callback.accept(key, value);
            }
            DatabaseKey nextKey = key.nextSubscript();
            selectAll.setBytes(1, toByteaSearch(nextKey, SearchDirection.FORWARD));
            selectAll.setBytes(2, toByteaSearch(nextKey, SearchDirection.BACKWARD));
            ResultSet result = selectAll.executeQuery();
            while (result.next()) {
                DatabaseKey foundKey = toKey(result.getBytes("key"));
                String foundValue = result.getString("value");
                callback.accept(foundKey, foundValue);
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartTransaction() {
        if (transactionStack.isEmpty()) return; // TODO Implement Exception
        rollback();
        startTransaction();
    }

    @Override
    public void startTransaction() {
        try {
            if (transactionStack.isEmpty()) {
                connection.setAutoCommit(false);
                connection.prepareCall("BEGIN TRANSACTION").execute();
                transactionStack.addFirst(1);
            } else {
                int level = transactionStack.size()+1;
                transactionStack.addFirst(level);
                connection.prepareCall("SAVEPOINT TLEVEL"+level).execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        if (transactionStack.isEmpty()) return; // TODO Implement Exception
        try {
            int level = transactionStack.removeFirst();
            if (level==1) {
                connection.prepareCall("COMMIT").execute();
                connection.setAutoCommit(true);
            } else {
                connection.prepareCall("RELEASE SAVEPOINT TLEVEL"+level).execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        rollback(0);
    }

    @Override
    public void rollback(int levels) {
        int loops;

        if (levels==0) loops = transactionStack.size();
        else if (levels<0) loops = levels * -1;
        else loops = transactionStack.size() - levels;

        while (loops-->0) {
            if (transactionStack.isEmpty()) return; // TODO Implement Exception
            try {
                int level = transactionStack.removeFirst();
                if (level == 1) {
                    connection.prepareCall("ROLLBACK").execute();
                    connection.setAutoCommit(true);
                } else {
                    connection.prepareCall("ROLLBACK TO SAVEPOINT TLEVEL" + level).execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public boolean lock(long lockId) {
        boolean result = false;
        try {
            lock.setLong(1, lockId);
            ResultSet rs = lock.executeQuery();
            result = (rs.next() && rs.getBoolean("result"));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void unlock(long lockId) {
        try {
            unlock.setLong(1, lockId);
            boolean result;
            do {
                ResultSet rs = unlock.executeQuery();
                result = (rs.next() && rs.getBoolean("result"));
                rs.close();
            } while (result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlockAll() {
        try {
            connection.prepareCall("SELECT pg_advisory_unlock_all()").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getTransactionLevel() {
        return transactionStack.size();
    }

    @Override
    public int getStatus(DatabaseKey key) {
        boolean contemValue = (get(key)!=null);

        DatabaseKey nextKey = next(key.nextSubscript());
        boolean contemSub = (nextKey!=null && nextKey.equalParent(key));
        int ret = 0;
        if (contemValue && contemSub) {
            ret = 11;
        } else if (contemSub) {
            ret = 10;
        } else if (contemValue) {
            ret = 1;
        }
        return ret;
    }


}
