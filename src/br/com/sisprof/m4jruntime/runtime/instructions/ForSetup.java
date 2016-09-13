package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaoe on 11/09/16.
 */
public class ForSetup extends AbstractInstruction {

    private final int params;

    private ForSetup(int indent, int line, int params) {
        super(indent, line);
        this.params = params;
    }

    public static ForSetup create(int indent, int line, int params) {
        return new ForSetup(indent, line, params);
    }

    public static ForSetup create(int indent, int line) {
        return new ForSetup(indent, line, 0);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.FOR_SETUP;
    }

    @Override
    public int getParam() {
        return params;
    }

    @Override
    public CallAction execute(Frame frame) {
        String varName = null;
        List<MValue> items = new ArrayList<>();
        if (params>0) {
            varName = frame.pop().getValue().toString();
            int loops = params;
            while (loops-->0) {
                items.add(frame.pop());
            }
        }

        if (varName!=null && !items.isEmpty()) {
            Variable variable = frame.getLocalScope().getVariable(varName);
            if (variable==null) {
                variable = frame.getGlobalScope().newVariable(varName);
            }
            MValue value = items.get(0);
            if (value instanceof MValueMultiVar) {
                variable.setValue(((MValueMultiVar)value).next());
            } else {
                variable.setValue(value);
            }
        }

        MValueLoopSetup setup = new MValueLoopSetup();
        frame.push(setup);
        LoopBlock loopBlock = LoopBlock.create(this.getIndent(), varName, items);
        if (!items.isEmpty()) {
            loopBlock.setCurrentValue(items.get(0));
            loopBlock.setCurrentItem(0);
        }
        frame.pushLoop(loopBlock);

        return CallAction.None;
    }
}
