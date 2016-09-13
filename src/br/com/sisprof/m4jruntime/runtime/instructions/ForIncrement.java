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
            String varName = loopBlock.getVarName();
            MValue currentItem = loopBlock.getCurrentValue();
            if (currentItem instanceof MValueMultiVar && ((MValueMultiVar)currentItem).hasNext()) {
                setValue(frame, varName, currentItem);
            } else if (loopBlock.getCurrentItem()+1>=loopBlock.getItems().size()) {
                frame.next();
            } else {
                int item = loopBlock.getCurrentItem()+1;
                loopBlock.setCurrentItem(item);
                setValue(frame, varName, loopBlock.getItems().get(item));
            }
        }
        return CallAction.None;
    }

    private void setValue(Frame frame, String varName, MValue value) {
        Variable variable = frame.getLocalScope().getVariable(varName);
        if (value instanceof MValueMultiVar) {
            variable.setValue(((MValueMultiVar)value).next());
        } else {
            variable.setValue(value);
        }
    }

}
