package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public interface MValue<T> extends Comparable<MValue> {

    MValue NULL = new MValueString("");

    T getValue();

    MValue clone();

    Number toNumber();

}
