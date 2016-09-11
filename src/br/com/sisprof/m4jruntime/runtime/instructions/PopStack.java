package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 10/09/16.
 */
public class PopStack extends AbstractInstruction {

    private final int items;

    public PopStack(int indent, int line, int items) {
        super(indent, line);
        this.items = items;
    }

    public static PopStack create(int indent, int line) {
        return new PopStack(indent,line, 1);
    }

    public static PopStack create(int indent, int line, int items) {
        return new PopStack(indent,line, items);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.POP_STACK;
    }

    @Override
    public int getParam() {
        return items;
    }

    @Override
    public CallAction execute(Frame frame) {
        int loops = items;
        while (loops-->0) {
            if (frame.isEmptyStack()) {
                break;
            }
            frame.pop();
        }
        return CallAction.None;
    }
}
