package br.com.sisprof.m4jruntime.compiler;

/**
 * Created by kaoe on 12/09/16.
 */
public class LabelInfo {

    private final String name;
    private final int stack;
    private final int params;

    public LabelInfo(String name, int stack, int params) {
        this.name = name;
        this.stack = stack;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public int getStack() {
        return stack;
    }

    public int getParams() {
        return params;
    }
}
