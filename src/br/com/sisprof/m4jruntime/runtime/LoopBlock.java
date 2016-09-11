package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 11/09/16.
 */
public class LoopBlock {

    private final int indent;

    private LoopBlock(int indent) {
        this.indent = indent;
    }

    public static LoopBlock create(int indent) {
        return new LoopBlock(indent);
    }

    public int getIndent() {
        return indent;
    }

}
