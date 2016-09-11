package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class Variable {

    private String name;
    private MValue value = MValue.NULL;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, MValue value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MValue getValue() {
        return value;
    }

    public void setValue(MValue value) {
        this.value = value;
    }
}
