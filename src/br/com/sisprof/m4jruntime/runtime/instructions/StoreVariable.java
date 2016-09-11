package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 09/09/16.
 */
public class StoreVariable extends AbstractInstruction {

    private final int nameIndex;

    private StoreVariable(int indent, int line, int nameIndex) {
        super(indent, line);
        this.nameIndex = nameIndex;
    }

    public static StoreVariable create(int indent, int line, int nameIndex) {
        return new StoreVariable(indent, line, nameIndex);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.STORE_VAR;
    }

    @Override
    public int getParam() {
        return nameIndex;
    }

    @Override
    public CallAction execute(Frame frame) {
        String varName = ((ConstantValueString)frame.getRoutine().getConstantValue(nameIndex)).getValue();
        if (!frame.isEmptyStack()) {
            MValue varValue = frame.pop();
            Variable var = frame.getLocalScope().getVariable(varName);
            if (var == null) {
                var = frame.getGlobalScope().newVariable(varName);
            }
            var.setValue(varValue);
        }
        return CallAction.None;
    }
}
