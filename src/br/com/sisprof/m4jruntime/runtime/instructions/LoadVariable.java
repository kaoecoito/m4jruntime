package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 10/09/16.
 */
public class LoadVariable extends AbstractInstruction {

    private final int nameIndex;

    private LoadVariable(int indent, int line, int nameIndex) {
        super(indent, line);
        this.nameIndex = nameIndex;
    }

    public static LoadVariable create(int indent, int line, int nameIndex) {
        return new LoadVariable(indent, line, nameIndex);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.LOAD_VAR;
    }

    @Override
    public int getParam() {
        return nameIndex;
    }

    @Override
    public CallAction execute(Frame frame) {
        String varName = ((ConstantValueString)frame.getRoutine().getConstantValue(nameIndex)).getValue();
        Variable var = frame.getLocalScope().getVariable(varName);
        if (var==null) {
            frame.push(MValue.NULL);
        } else {
            frame.push(var.getValue());
        }
        return CallAction.None;
    }
}
