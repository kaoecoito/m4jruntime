package br.com.sisprof.m4jruntime.parser;

import br.com.sisprof.m4jruntime.database.DatabaseKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaoe on 15/09/16.
 */
public class GOFRecord {

    private final String line;
    private DatabaseKey database;
    private String content;

    public GOFRecord(String line) {
        this.line = line;
        this.parse();
    }

    private void parse() {
        StringBuilder builder = new StringBuilder();
        String globalName = null;
        String content = null;
        List<Object> subscript = new ArrayList<>();
        int status = 0;

        char[] data = line.toCharArray();
        for (int i=0;i<data.length;i++) {
            char chr = data[i];
            if (i==0 && chr!='^') break;
            if (status==0) {
                builder.append(chr);
                status = 1;
            } else if (status==1 && chr=='(') {
                globalName = builder.toString();
                builder.setLength(0);
                status = 2;
            } else if (status==2 && chr==',') {
                createValue(builder, subscript);
            } else if (status==2 && chr==')') {
                createValue(builder, subscript);
                status = 9;
                i++;
            } else if (status==9 && chr=='"') {
                content = line.substring(i+1, line.length()-1);
                i = data.length;
            } else if (status==2 && chr=='"') {
                builder.append(chr);
                status = 3;
            } else if (status==3 && chr=='"') {
                builder.append(chr);
                if (data[i+1]=='"') {
                    i++;
                } else {
                    status = 2;
                }
            } else {
                builder.append(chr);
            }
        }
        if (builder.length()>0) {
            createValue(builder, subscript);
        }
        if (subscript.isEmpty()) {
            this.database = DatabaseKey.create(globalName.substring(1));
        } else {
            this.database = DatabaseKey.create(globalName.substring(1), subscript.toArray());
        }
        this.content = (content!=null?content:"");
    }

    private void createValue(StringBuilder builder, List<Object> subscript) {
        String value = builder.toString();
        builder.setLength(0);
        if (value.startsWith("\"")) {
            subscript.add(value.substring(1, value.length()-1));
        } else {
            Double num = Double.parseDouble(value);
            if (num-num.longValue()==0d) {
                subscript.add(num.longValue());
            } else {
                subscript.add(num);
            }
        }
    }

    public DatabaseKey getDatabase() {
        return database;
    }

    public String getContent() {
        return content;
    }
}
