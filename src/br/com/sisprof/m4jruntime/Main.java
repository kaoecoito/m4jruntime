package br.com.sisprof.m4jruntime;

import br.com.sisprof.m4jruntime.database.DatabaseKey;
import br.com.sisprof.m4jruntime.database.DatabaseStorage;
import br.com.sisprof.m4jruntime.database.PostgresqlDatabaseFactory;
import org.postgresql.ds.PGSimpleDataSource;

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

        System.out.println(storage.getStatus(key3));

        System.out.println("Data 1: "+storage.getStatus(key1)+" => "+key1.toString());
        System.out.println("Data 2: "+storage.getStatus(key2)+" => "+key2.toString());
        System.out.println("Data 3: "+storage.getStatus(key3)+" => "+key3.toString());
        System.out.println("Data 4: "+storage.getStatus(key4)+" => "+key4.toString());
        System.out.println("Data 5: "+storage.getStatus(key5)+" => "+key5.toString());

        storage.close();

        /*
        MumpsCompiler compiler = new MumpsCompiler(new File("/home/kaoe/Downloads/TESTE.m"));
        compiler.compile();
        Routine routine = compiler.getRoutine();

        routine.writeFile(new File("/home/kaoe/Downloads/TESTE.mclass"));

        VirtualMachine machine = VirtualMachine.newVirtualMachine();
        machine.run(routine);
        */

    }

}
