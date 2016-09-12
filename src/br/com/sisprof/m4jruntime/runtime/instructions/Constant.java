package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 09/09/16.
 */
public class Constant extends AbstractInstruction {

    private final int constantItem;

    private Constant(int indent, int line, int constantItem) {
        super(indent, line);
        this.constantItem = constantItem;
    }

    public static Constant create(int indent, int line, int constantItem) {
        return new Constant(indent, line, constantItem);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.CONST;
    }

    @Override
    public int getParam() {
        return constantItem;
    }

    @Override
    public CallAction execute(Frame frame) {
        ConstantValue constant = frame.getRoutine().getConstantValue(constantItem);
        MValue value;
        if (constant instanceof ConstantValueString) {
            value = new MValueString(((ConstantValueString)constant).getValue());
        } else if (constant instanceof ConstantValueOperator) {
            value = new MValueOperator(((ConstantValueOperator)constant).getValue());
        } else {
            value = new MValueNumber(((ConstantValueNumber)constant).getValue());
        }
        frame.push(value);
        return CallAction.None;
    }

}
