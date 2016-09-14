package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 10/09/16.
 */
public class Duplicate extends AbstractInstruction {

    private Duplicate(int indent, int line) {
        super(indent, line);
    }

    public static Duplicate create(int indent, int line) {
        return new Duplicate(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.DUP_STACK;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue value = frame.pop();
        frame.push(value);
        frame.push(value.cloneValue());
        return CallAction.None;
    }
}
