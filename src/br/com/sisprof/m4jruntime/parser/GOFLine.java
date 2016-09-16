package br.com.sisprof.m4jruntime.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by kaoe on 15/09/16.
 */
public class GOFLine implements Iterator<GOFRecord> {

    private final BufferedReader reader;

    public GOFLine(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.ready();
        } catch (IOException e) {
        }
        return false;
    }

    @Override
    public GOFRecord next() {
        String line;
        try {
            line = reader.readLine();
            return (line==null?null:new GOFRecord(line));
        } catch (IOException e) {
        }
        return null;
    }

}
