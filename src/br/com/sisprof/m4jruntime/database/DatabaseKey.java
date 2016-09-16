package br.com.sisprof.m4jruntime.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaoe on 14/09/16.
 */
public class DatabaseKey {

    private final String global;
    private final List<Object> subscripts = new ArrayList<>();

    private DatabaseKey(String global) {
        this.global = global;
    }

    public static DatabaseKey create(String global,Object... subscripts) {
        DatabaseKey key = new DatabaseKey(global);
        if (subscripts!=null && subscripts.length!=0) {
            for (Object item:subscripts) {
                if (item instanceof Number) {
                    key.subscripts.add(item);
                } else {
                    key.subscripts.add(item.toString());
                }
            }
        }
        return key;
    }

    public String getGlobal() {
        return global;
    }

    public int size() {
        return subscripts.size();
    }

    public boolean isNullSubscript() {
        if (subscripts.isEmpty()) return true;
        return subscripts.get(subscripts.size()-1).toString().isEmpty();
    }

    public boolean equalLevels(DatabaseKey key) {
        boolean ret = true;
        if (!this.global.equals(key.global)) return false;
        int len = Math.min(this.size(), key.size());
        for (int i=0;i<len;i++) {
            if (!this.subscripts.get(i).equals(key.subscripts.get(i))) {
                ret = false;
                break;
            }
        }
        return ret;
    }
    public List<Object> getSubscripts() {
        return Collections.unmodifiableList(subscripts);
    }

    public DatabaseKey nextSubscript() {
        if (subscripts.isEmpty()) {
            return DatabaseKey.create(global,"");
        } else {
            DatabaseKey key = DatabaseKey.create(global, subscripts.toArray());
            key.subscripts.add("");
            return key;
        }
    }

    public DatabaseKey toSubscriptIndex(int i) {
        if (i==0) {
            return DatabaseKey.create(global);
        } else if (i<subscripts.size()) {
            return DatabaseKey.create(global, Arrays.copyOf(subscripts.toArray(), i));
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(global);
        if (!subscripts.isEmpty()) {
            builder.append("(");
            for (int i=0;i<subscripts.size();i++) {
                Object item = subscripts.get(i);
                if (i>0) {
                    builder.append(",");
                }
                if (item instanceof Number) {
                    builder.append(item.toString());
                } else {
                    builder.append("\"").append(item.toString()).append("\"");
                }
            }
            builder.append(")");
        }
        return builder.toString();
    }
}
