package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 11/09/16.
 */
public class MValueLoopSetup implements MValue<Integer> {

    @Override
    public Integer getValue() {
        return 0;
    }

    @Override
    public MValue clone() {
        return new MValueLoopSetup();
    }

    @Override
    public Number toNumber() {
        return 0l;
    }

    @Override
    public int compareTo(MValue o) {
        throw new IllegalArgumentException("Impossivel comparar valor tipo LoopSetup");
    }

}
