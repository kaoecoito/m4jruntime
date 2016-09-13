package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class BinaryConcat extends AbstractInstruction {

    private BinaryConcat(int indent, int line) {
        super(indent, line);
    }

    public static BinaryConcat create(int indent, int line) {
        return new BinaryConcat(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BINARY_CONCAT;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        MValue v2 = frame.pop();
        frame.push(new MValueString(v1.getValue().toString()+v2.getValue().toString()));
        return CallAction.None;
    }

}
