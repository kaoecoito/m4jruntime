package br.com.sisprof.m4jruntime.parser;

import java.io.*;
import java.util.Iterator;

/**
 * Created by kaoe on 15/09/16.
 */
public class GOFImport {

    private final File file;
    private BufferedReader reader;

    public GOFImport(File file) {
        this.file = file;
    }

    public void open() throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
    }

    public Iterator<GOFRecord> lines() {
        return new GOFLine(reader);
    }

    public void close() throws IOException {
        if (reader!=null) {
            reader.close();
        }
    }

}
