package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public class MValueString extends MValue<String> {

    private final String value;

    public MValueString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public MValue cloneValue() {
        return new MValueString(value);
    }

    @Override
    public Number toNumber() {
        return NumberOperations.toNumber(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MValueString that = (MValueString) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(MValue o) {
        if (value==null && o==null) return 0;
        if (value==null) return -1;
        if (o==null || o.getValue()==null) return 1;
        if (NumberOperations.isNumber(value) && NumberOperations.isNumber(o.getValue().toString())) {
            return NumberOperations.compareAsNumber(value.toString(), o.getValue().toString());
        } else {
            return value.compareTo(o.getValue().toString());
        }
    }

}
