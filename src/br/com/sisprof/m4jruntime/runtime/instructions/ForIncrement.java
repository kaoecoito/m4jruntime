package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 11/09/16.
 */
public class ForIncrement extends AbstractInstruction {

    private ForIncrement(int indent, int line) {
        super(indent, line);
    }

    public static ForIncrement create(int indent, int line) {
        return new ForIncrement(indent, line);
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.FOR_INCREMENT;
    }

    @Override
    public CallAction execute(Frame frame) {
        LoopBlock loopBlock = frame.currentLoop();
        if (loopBlock.getVarName()!=null && !loopBlock.getItems().isEmpty()) {
            if (loopBlock.getCurrentItem()+1>=loopBlock.getItems().size()) {
                frame.next();
            } else {
                String varName = loopBlock.getVarName();
                int item = loopBlock.getCurrentItem()+1;
                loopBlock.setCurrentItem(item);

                Variable variable = frame.getLocalScope().getVariable(varName);
                variable.setValue(loopBlock.getItems().get(item));
            }
        }
        return CallAction.None;
    }

}
