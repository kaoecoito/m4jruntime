package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class MValueNumber implements MValue<Number> {

    private final Number value;

    public MValueNumber(Number value) {
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }
    
}
