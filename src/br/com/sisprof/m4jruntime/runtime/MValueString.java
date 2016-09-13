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

    @Override
    public int compareTo(MValue o) {
        if (value==null && o==null) return 0;
        if (value==null) return -1;
        if (o==null || o.getValue()==null) return 1;
        if (MumpsUtil.isNumber(value) && MumpsUtil.isNumber(o.getValue().toString())) {
            return MumpsUtil.compareAsNumber(value.toString(), o.getValue().toString());
        } else {
            return value.compareTo(o.getValue().toString());
        }
    }

}
