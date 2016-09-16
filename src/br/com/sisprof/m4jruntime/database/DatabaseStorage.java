package br.com.sisprof.m4jruntime.database;

/**
 * Created by kaoe on 14/09/16.
 */
public interface DatabaseStorage {

    void set(DatabaseKey key, String value);
    String get(DatabaseKey key);

    DatabaseKey next(DatabaseKey key);
    DatabaseKey prev(DatabaseKey key);
    int getStatus(DatabaseKey key);
    boolean delete(DatabaseKey key);
    int deleteAll(DatabaseKey key);

    void startTransaction();
    void commit();
    void rollback();
    int getTransactionLevel();


    void close();

}
