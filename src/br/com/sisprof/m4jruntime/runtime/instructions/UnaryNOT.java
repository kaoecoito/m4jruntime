package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class UnaryNOT extends AbstractInstruction {

    private UnaryNOT(int indent, int line) {
        super(indent, line);
    }

    public static UnaryNOT create(int indent, int line) {
        return new UnaryNOT(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.UNARY_NOT;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        boolean result = !NumberOperations.isFalse(v1);
        frame.push(new MValueNumber(result?1:0));
        return CallAction.None;
    }

}
