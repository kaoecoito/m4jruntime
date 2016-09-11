package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 11/09/16.
 */
public class ForEnd extends AbstractInstruction {

    public ForEnd(int indent, int line) {
        super(indent, line);
    }

    public static ForEnd create(int indent, int line) {
        return new ForEnd(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.FOR_END;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue value;
        do {
            if (frame.isEmptyStack()) {
                break;
            }
            value = frame.pop();
        } while (!(value instanceof MValueLoopSetup));
        frame.popLoop();
        return CallAction.None;
    }
}
