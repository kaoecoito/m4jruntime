package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public abstract class MValue<T> implements Comparable<MValue> {

    public static final MValue NULL = new MValueString("");

    public abstract T getValue();

    public abstract MValue cloneValue();

    public abstract Number toNumber();

    public static MValue create(String value) {
        return new MValueString(value);
    }

    public static MValue create(double value) {
        return new MValueNumber(value);
    }

    public static MValue create(long value) {
        return new MValueNumber(value);
    }

    public static MValue create(int value) {
        return new MValueNumber(Long.valueOf(value));
    }

}
