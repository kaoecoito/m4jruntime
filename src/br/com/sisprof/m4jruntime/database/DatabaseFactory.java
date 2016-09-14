package br.com.sisprof.m4jruntime.database;

/**
 * Created by kaoe on 14/09/16.
 */
public interface DatabaseFactory {

    DatabaseStorage create();

    void close();

}
