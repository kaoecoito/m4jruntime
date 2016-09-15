package br.com.sisprof.m4jruntime.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kaoe on 14/09/16.
 */
public class PostgresqlDatabaseStorage implements DatabaseStorage {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final byte[] SEP = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
    
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

    private void init() {
        try {
            selectItem = connection.prepareStatement("select value from global_dataset where key=?");
            selectNext = connection.prepareStatement("select key from global_dataset where key>? order by key limit 1");
            selectPrev = connection.prepareStatement("select key from global_dataset where key<? order by key desc limit 1");
            insertItem = connection.prepareStatement("insert into global_dataset (key,value) values (?,?) ON CONFLICT (key) DO UPDATE SET value=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isSep(byte[] bs, int start) {
        boolean ret = true;
        if (start>=bs.length || start+SEP.length>=bs.length) return false;
        for (int i=0;i<SEP.length;i++) {
            if (bs[start+i]!=SEP[i]) {
                ret = false;
                break;
            }
        }
        return ret;
    }


    private byte[] toBytea(DatabaseKey key) {
        return toBytea(key, false);
    }

    public byte[] toBytea(DatabaseKey key, boolean desc) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            List<Object> subscript = key.getSubscripts();
            stream.write(key.getGlobal().getBytes(DEFAULT_CHARSET));
            if (!subscript.isEmpty()) {
                for (Object item:subscript) {
                    stream.write(SEP);
                    if (item instanceof String && item.toString().isEmpty()) {
                        stream.write(desc?0xFF:0x00);
                    } else if (item instanceof Number) {
                        stream.write(0x01);
                        byte[] bs = ByteBuffer.allocate(8).putDouble(((Number)item).doubleValue()).array();
                        stream.write(bs);
                    } else {
                        stream.write(0x02);
                        stream.write(item.toString().getBytes(DEFAULT_CHARSET));
                    }
                }
            }
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    private DatabaseKey toKey(byte[] bs) {
        DatabaseKey key = null;
        if (bs!=null && bs.length>0) {
            List<byte[]> blocks = new ArrayList<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i=0;i<bs.length;i++) {
                if (isSep(bs, i)) {
                    blocks.add(out.toByteArray());
                    out.reset();
                    i += (SEP.length-1);
                } else {
                    out.write(bs[i]);
                }
            }
            if (out.size()>0) {
                blocks.add(out.toByteArray());
                out.reset();
            }
            if (!blocks.isEmpty()) {
                String global = new String(blocks.get(0));
                List<Object> subscripts = new ArrayList<>();
                for (int i=1;i<blocks.size();i++) {
                    byte[] block = blocks.get(i);
                    byte[] content = null;
                    if (block.length>1) {
                        content = Arrays.copyOfRange(block, 1, block.length);
                    }
                    int type = block[0];
                    if (type==0x00) {
                        subscripts.add("");
                    } else if (type==0x01) {
                        ByteBuffer buffer = ByteBuffer.wrap(content);
                        Double num = buffer.getDouble();
                        if (num-num.longValue()>0) {
                            subscripts.add(num);
                        } else {
                            subscripts.add(num.longValue());
                        }
                    } else if (type==0x02) {
                        subscripts.add(new String(content, DEFAULT_CHARSET));
                    }
                }
                if (subscripts.isEmpty()) {
                    key = DatabaseKey.create(global);
                } else {
                    key = DatabaseKey.create(global, subscripts.toArray());
                }
            }
        }
        return key;
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

            byte[] bs = toBytea(key);
            byte[] buffer = new byte[bs.length+SEP.length+1];
            System.arraycopy(bs, 0, buffer, 0, bs.length);
            for (int i=0;i<SEP.length;i++) {
                buffer[bs.length+i] = (byte) 0xFF;
            }
            buffer[buffer.length-1] = (byte) 0xFF;
            selectNext.setBytes(1, buffer);
            ResultSet result = selectNext.executeQuery();
            if (result.next()) {
                DatabaseKey nextKey = toKey(result.getBytes(1));
                if (nextKey.size()==key.size() && nextKey.equalLevels(key)) {
                    next = nextKey.toSubscriptIndex(key.size());
                } else if (nextKey.size()>key.size()) {
                    next = nextKey.toSubscriptIndex(key.size());
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
            byte[] bs = toBytea(key, true);
            byte[] buffer = new byte[bs.length];
            System.arraycopy(bs, 0, buffer, 0, bs.length);
            selectPrev.setBytes(1, buffer);
            ResultSet result = selectPrev.executeQuery();
            if (result.next()) {
                DatabaseKey prevKey = toKey(result.getBytes(1));
                if (prevKey.size()==key.size() && prevKey.equalLevels(key)) {
                    prev = prevKey.toSubscriptIndex(key.size());
                } else if (prevKey.size()>key.size()) {
                    prev = prevKey.toSubscriptIndex(key.size());
                }
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prev;
    }

    @Override
    public int getStatus(DatabaseKey key) {
        boolean contemValue = (get(key)!=null);

        DatabaseKey nextKey = next(key.nextSubscript());
        boolean contemSub = (nextKey!=null && nextKey.equalLevels(key));
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
