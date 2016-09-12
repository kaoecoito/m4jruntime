package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

import java.lang.reflect.Method;

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
        MValue[] params = new MValue[args];
        for (int i=0;i<params.length;i++) {
            params[i] = frame.pop();
        }
        VirtualMachine vm = VirtualMachine.getCurrent();
        if (vm.existsCommandOrFunction(commandName)) {
            Method method = vm.getCommandOrFunction(commandName);
            try {
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(null, new Object[]{params});
                } else {
                    MValue ret = (MValue)method.invoke(null, new Object[]{params});
                    if (ret==null) {
                        frame.push(MValue.NULL);
                    } else {
                        frame.push(ret);
                    }
                }
            } catch (Exception ex) {
                // TODO Implementar log de erro
                ex.printStackTrace();
            }
        } else {
            // TODO Implementar comando inválido
            if (commandName.startsWith("$")) {
                System.err.println("Função "+commandName+" não existe");
            } else {
                System.err.println("Comando "+commandName+" não existe");
            }
        }
        return CallAction.None;
    }
}
