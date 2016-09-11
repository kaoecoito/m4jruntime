package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 09/09/16.
 */
public class Command extends AbstractInstruction {

    private final int args;

    private Command(int indent, int line, int args) {
        super(indent, line);
        this.args = args;
    }

    public static Command create(int indent, int line, int args) {
        return new Command(indent,line,args);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.CMD;
    }

    @Override
    public int getParam() {
        return args;
    }

    @Override
    public CallAction execute(Frame frame) {

        String commandName = ((MValueString)frame.pop()).getValue();
        System.out.println("Executando "+commandName+" com "+args+" parametros");

        return CallAction.None;
    }
}
