package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 09/09/16.
 */
public class NewVariable extends AbstractInstruction {

    private final int varItem;

    private NewVariable(int indent, int line, int varItem) {
        super(indent, line);
        this.varItem = varItem;
    }

    public static NewVariable create(int indent, int line, int varItem) {
        return new NewVariable(indent, line, varItem);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.NEW_VAR;
    }

    @Override
    public int getParam() {
        return varItem;
    }

    @Override
    public CallAction execute(Frame frame) {
        ConstantValueString constant = (ConstantValueString)frame.getRoutine().getConstantValue(varItem);
        frame.getLocalScope().newVariable(constant.getValue());
        return CallAction.None;
    }
}
