package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class MValueString implements MValue<String> {

    private final String value;

    public MValueString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public MValue clone() {
        return new MValueString(value);
    }

    @Override
    public Number toNumber() {
        return MumpsUtil.toNumber(value);
    }

}
