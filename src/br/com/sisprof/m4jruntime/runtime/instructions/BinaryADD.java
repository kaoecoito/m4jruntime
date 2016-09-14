package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class BinaryADD extends AbstractInstruction {

    private BinaryADD(int indent, int line) {
        super(indent, line);
    }

    public static BinaryADD create(int indent, int line) {
        return new BinaryADD(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BINARY_ADD;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        MValue v2 = frame.pop();
        frame.push(new MValueNumber(NumberOperations.add(v1.toNumber(), v2.toNumber())));
        return CallAction.None;
    }

}
