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
    int merge(DatabaseKey from, DatabaseKey to);

    void startTransaction();
    void restartTransaction();
    void commit();
    void rollback(int levels);
    void rollback();
    int getTransactionLevel();

    boolean lock(long lockId);
    void unlock(long lockId);
    void unlockAll();

    void close();

}
