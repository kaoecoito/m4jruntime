package br.com.sisprof.m4jruntime;

import br.com.sisprof.m4jruntime.compiler.MumpsCompiler;
import br.com.sisprof.m4jruntime.runtime.Routine;
import br.com.sisprof.m4jruntime.runtime.VirtualMachine;

import java.io.File;
import java.io.IOException;

/**
 * Created by kaoe on 06/09/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        MumpsCompiler compiler = new MumpsCompiler(new File("/home/kaoe/Downloads/TESTE.m"));
        compiler.compile();
        Routine routine = compiler.getRoutine();

        routine.writeFile(new File("/home/kaoe/Downloads/TESTE.mclass"));

        VirtualMachine machine = VirtualMachine.newVirtualMachine();
        machine.run(routine);

    }

}
