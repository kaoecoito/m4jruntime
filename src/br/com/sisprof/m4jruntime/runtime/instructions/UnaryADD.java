package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class UnaryADD extends AbstractInstruction {

    private UnaryADD(int indent, int line) {
        super(indent, line);
    }

    public static UnaryADD create(int indent, int line) {
        return new UnaryADD(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.UNARY_ADD;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        frame.push(new MValueNumber(v1.toNumber()));
        return CallAction.None;
    }

}
