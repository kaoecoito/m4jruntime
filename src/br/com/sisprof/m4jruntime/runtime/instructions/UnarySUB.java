package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 12/09/16.
 */
public class UnarySUB extends AbstractInstruction {

    private UnarySUB(int indent, int line) {
        super(indent, line);
    }

    public static UnarySUB create(int indent, int line) {
        return new UnarySUB(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.UNARY_SUB;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue v1 = frame.pop();
        Number number = v1.toNumber();
        if (number instanceof Long || number instanceof Integer) {
            number = number.longValue() * -1;
        } else {
            number = number.doubleValue() * -1d;
        }
        frame.push(new MValueNumber(number));
        return CallAction.None;
    }

}
