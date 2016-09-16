package br.com.sisprof.m4jruntime;

import br.com.sisprof.m4jruntime.compiler.MumpsCompiler;
import br.com.sisprof.m4jruntime.database.DatabaseKey;
import br.com.sisprof.m4jruntime.database.DatabaseStorage;
import br.com.sisprof.m4jruntime.database.PostgresqlDatabaseFactory;
import br.com.sisprof.m4jruntime.parser.GOFImport;
import br.com.sisprof.m4jruntime.runtime.Routine;
import br.com.sisprof.m4jruntime.runtime.VirtualMachine;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by kaoe on 06/09/16.
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("mumps");
        dataSource.setUser("postgres");

        PostgresqlDatabaseFactory databaseFactory = PostgresqlDatabaseFactory.newFactory(dataSource);
        DatabaseStorage storage = databaseFactory.create();

        //testGOF(storage);
        testRead(storage);
        //storage.deleteAll(DatabaseKey.create("^GPB"));
        //testInsert(storage);


        /*
        storage.deleteAll(DatabaseKey.create("^B"));
        storage.set(DatabaseKey.create("^A",1),"1");
        storage.set(DatabaseKey.create("^A",1, 2),"2");
        storage.set(DatabaseKey.create("^A",1, 3),"3");
        System.out.println("Merge: "+storage.merge(DatabaseKey.create("^A", 1), DatabaseKey.create("^B",5,4)));

        System.out.println("^A(1)="+storage.get(DatabaseKey.create("^A",1)));
        System.out.println("^A(1,2)="+storage.get(DatabaseKey.create("^A",1, 2)));
        System.out.println("^B(5,4,2)="+storage.get(DatabaseKey.create("^B",5,4,2)));
        System.out.println("^B(5,4,3)="+storage.get(DatabaseKey.create("^B",5,4,3)));
        System.out.println(storage.getStatus(DatabaseKey.create("^B")));
        System.out.println(storage.next(DatabaseKey.create("^B","")));
        */

        storage.close();

    }

    private static void testRead(DatabaseStorage storage) {
        long start = System.currentTimeMillis();
        DatabaseKey key = DatabaseKey.create("^GPB","");
        while (true) {
            key = storage.next(key);
            if (key==null) break;
            System.out.println(key);
        }
        long end = System.currentTimeMillis()-start;
        System.out.println("Loop positivo em "+end+"ms\n");

        start = System.currentTimeMillis();
        key = DatabaseKey.create("^GPB","");
        while (true) {
            key = storage.prev(key);
            if (key==null) break;
            System.out.println(key);
        }
        end = System.currentTimeMillis()-start;
        System.out.println("Loop negativo em "+end+"ms\n");

    }

    private static void testGOF(DatabaseStorage storage) throws IOException {
        GOFImport gof = new GOFImport(new File("/home/kaoe/Downloads/GPB.GO"));
        gof.open();

        storage.startTransaction();
        long start = System.currentTimeMillis();
        gof.lines().forEachRemaining((record) -> {
            DatabaseKey key = record.getDatabase();
            System.out.println(key);
            storage.set(key, record.getContent());
        });
        storage.commit();

        long end = System.currentTimeMillis()-start;
        System.out.println("Importado dados em "+end+"ms");
        gof.close();
    }

    private static void testCompiler() throws IOException {
        MumpsCompiler compiler = new MumpsCompiler(new File("/home/kaoe/Downloads/TESTE.m"));
        compiler.compile();
        Routine routine = compiler.getRoutine();

        routine.writeFile(new File("/home/kaoe/Downloads/TESTE.mclass"));

        VirtualMachine machine = VirtualMachine.newVirtualMachine();
        machine.run(routine);

    }

    private static void testInsert(DatabaseStorage storage) {
        DatabaseKey key1 = DatabaseKey.create("^tmp","N1");
        DatabaseKey key2 = DatabaseKey.create("^tmp","N1","N11");
        DatabaseKey key3 = DatabaseKey.create("^tmp",1);
        DatabaseKey key4 = DatabaseKey.create("^tmp",2);
        DatabaseKey key5 = DatabaseKey.create("^tmp","N2","N21");

        storage.set(DatabaseKey.create("^tmp",2, 1),"2.1");
        storage.set(DatabaseKey.create("^tmp",3, 1),"3.1");
        storage.set(DatabaseKey.create("^tmp",2.5),"2.5");
        storage.set(DatabaseKey.create("^tmp","N12"),"N12");
        storage.set(DatabaseKey.create("^tmp","a"),"a");
        storage.set(DatabaseKey.create("^tmp","b"),"b");
        storage.set(DatabaseKey.create("^tmp","c"),"c");
        storage.set(DatabaseKey.create("^tmp","}"),"}");
        storage.set(DatabaseKey.create("^tmp","}}"),"}}");
        storage.set(DatabaseKey.create("^tmp","}}}"),"}}}");

        storage.set(key1,"N1");
        storage.set(key2,"N1,N11");
        storage.set(key3,"1");
        storage.set(key4,"2");
        storage.set(key5,"N2,N21");

        System.out.println("Teste 1: "+storage.get(key1));
        System.out.println("Teste 2: "+storage.get(key2));
        System.out.println("Teste 3: "+storage.get(key3));
        System.out.println("Teste 4: "+storage.get(key4));
        System.out.println("Teste 5: "+storage.get(key5));

        System.out.println("Order Next");
        DatabaseKey item = DatabaseKey.create("^tmp","");
        while (true) {
            item = storage.next(item);
            if (item==null) break;
            System.out.println("Order Next Key: "+item.toString());
        }

        System.out.println("Order Prev");
        item = DatabaseKey.create("^tmp","");
        while (true) {
            item = storage.prev(item);
            if (item==null) break;
            System.out.println("Order Prev Key: "+item.toString());
        }

        System.out.println("Data 1: "+storage.getStatus(key1)+" => "+key1.toString());
        System.out.println("Data 2: "+storage.getStatus(key2)+" => "+key2.toString());
        System.out.println("Data 3: "+storage.getStatus(key3)+" => "+key3.toString());
        System.out.println("Data 4: "+storage.getStatus(key4)+" => "+key4.toString());
        System.out.println("Data 5: "+storage.getStatus(key5)+" => "+key5.toString());

    }

}
