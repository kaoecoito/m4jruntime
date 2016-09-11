package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

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
        MValueLoopSetup setup = new MValueLoopSetup();
        frame.push(setup);
        frame.pushLoop(LoopBlock.create(this.getIndent()));
        return CallAction.None;
    }
}
