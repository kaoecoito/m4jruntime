package br.com.sisprof.m4jruntime.compiler;

import br.com.sisprof.m4jruntime.runtime.instructions.ForSetup;

/**
 * Created by kaoe on 11/09/16.
 */
public class BlockFor implements BlockStack {

    private final ForSetup setup;
    private final int gotoStack;

    public BlockFor(ForSetup setup, int gotoStack) {
        this.setup = setup;
        this.gotoStack = gotoStack;
    }

    public ForSetup getSetup() {
        return setup;
    }

    public int getGotoStack() {
        return gotoStack;
    }

}
