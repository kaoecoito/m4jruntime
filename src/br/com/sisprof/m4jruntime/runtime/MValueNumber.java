package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class MValueNumber extends MValue<Number> {

    private final Number value;

    public MValueNumber(Number value) {
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MValueNumber that = (MValueNumber) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public MValue cloneValue() {
        if (value instanceof Long) {
            return new MValueNumber(value.longValue());
        } else if (value instanceof Integer) {
            return new MValueNumber(value.intValue());
        } else if (value instanceof Double) {
            return new MValueNumber(value.doubleValue());
        }
        return new MValueNumber(value);
    }

    @Override
    public Number toNumber() {
        return value;
    }

    @Override
    public int compareTo(MValue o) {
        if (value==null && o==null) return 0;
        if (value==null) return -1;
        if (o==null || o.getValue()==null) return 1;
        return NumberOperations.compareAsNumber(value.toString(), o.getValue().toString());
    }
}
