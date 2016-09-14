package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class BinaryDIV extends AbstractInstruction {

    private BinaryDIV(int indent, int line) {
        super(indent, line);
    }

    public static BinaryDIV create(int indent, int line) {
        return new BinaryDIV(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BINARY_DIV;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        MValue v2 = frame.pop();
        frame.push(new MValueNumber(NumberOperations.div(v1.toNumber(), v2.toNumber())));
        return CallAction.None;
    }

}
