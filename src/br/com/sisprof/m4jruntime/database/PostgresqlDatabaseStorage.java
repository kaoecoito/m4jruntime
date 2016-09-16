package br.com.sisprof.m4jruntime.database;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kaoe on 14/09/16.
 */
public class PostgresqlDatabaseStorage implements DatabaseStorage {

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

    public PostgresqlDatabaseStorage(Connection connection) {
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
    private PreparedStatement selectNext;
    private PreparedStatement selectPrev;
    private PreparedStatement insertItem;

    private PreparedStatement deleteItem;
    private PreparedStatement deleteAll;

    private void init() {
        try {
            selectItem = connection.prepareStatement("select value from global_dataset where key=?");
            selectNext = connection.prepareStatement("select key from global_dataset where key>? order by key limit 1");
            selectPrev = connection.prepareStatement("select key from global_dataset where key<? order by key desc limit 1");
            insertItem = connection.prepareStatement("insert into global_dataset (key,value) values (?,?) ON CONFLICT (key) DO UPDATE SET value=?");
            deleteItem = connection.prepareStatement("delete from global_dataset where key=?");
            deleteAll = connection.prepareStatement("delete from global_dataset where key>? and key<?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] toBytea(DatabaseKey key) {
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

    public byte[] toByteaSearch(DatabaseKey key, SearchDirection direction) {
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
            insertItem.setString(3, value);
            insertItem.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                DatabaseKey tmpNext = toKey(result.getBytes(1)).toSubscriptIndex(key.size());
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
                DatabaseKey tmpPrev = toKey(result.getBytes(1)).toSubscriptIndex(key.size());
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
