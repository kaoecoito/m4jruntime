package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public interface MValue<T> {

    MValue NULL = new MValueString("");

    T getValue();

}
