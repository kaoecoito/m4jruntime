package br.com.sisprof.m4jruntime.compiler;

import br.com.sisprof.m4jruntime.runtime.instructions.JumpInstruction;

/**
 * Created by kaoe on 11/09/16.
 */
public class BlockJump implements BlockStack {


    private final JumpInstruction instruction;

    public BlockJump(JumpInstruction instruction) {
        this.instruction = instruction;
    }

    public JumpInstruction getInstruction() {
        return instruction;
    }

}
