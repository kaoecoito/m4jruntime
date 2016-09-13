package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 13/09/16.
 */
public class BinaryLT extends AbstractInstruction {

    private BinaryLT(int indent, int line) {
        super(indent, line);
    }

    public static BinaryLT create(int indent, int line) {
        return new BinaryLT(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BINARY_LT;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        MValue v2 = frame.pop();
        boolean result = (v1.compareTo(v2)<0);
        frame.push(new MValueNumber(result?1:0));
        return CallAction.None;
    }

}
