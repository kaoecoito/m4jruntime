package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class Variable {

    private final String name;
    private MValue value = MValue.NULL;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MValue getValue() {
        return value;
    }

    public void setValue(MValue value) {
        this.value = value;
    }
}
