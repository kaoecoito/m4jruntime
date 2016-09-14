package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class BinaryAND extends AbstractInstruction {

    private BinaryAND(int indent, int line) {
        super(indent, line);
    }

    public static BinaryAND create(int indent, int line) {
        return new BinaryAND(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BINARY_AND;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        MValue v2 = frame.pop();
        boolean result = (!NumberOperations.isFalse(v1) && !NumberOperations.isFalse(v2));
        frame.push(new MValueNumber(result?1:0));
        return CallAction.None;
    }

}
