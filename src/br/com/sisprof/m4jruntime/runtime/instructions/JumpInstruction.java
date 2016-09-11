package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;

/**
 * Created by kaoe on 11/09/16.
 */
public abstract class JumpInstruction extends AbstractInstruction {

    protected int jump;

    public JumpInstruction(int indent, int line, int jump) {
        super(indent, line);
        this.jump = jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

}
