package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 12/09/16.
 */
public class MValueOperator implements MValue<String> {

    private final String value;

    public MValueOperator(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public MValue clone() {
        return new MValueOperator(value);
    }

    @Override
    public Number toNumber() {
        return 0l;
    }
}
