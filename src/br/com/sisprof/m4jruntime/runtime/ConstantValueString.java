package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class ConstantValueString extends AbstractConstantValue<String> {

    private final String value;

    public ConstantValueString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
