package br.com.sisprof.m4jruntime.runtime;

import java.util.Collections;
import java.util.List;

/**
 * Created by kaoe on 12/09/16.
 */
public class MValueMultiVar implements MValue<Integer> {

    private final List<MValue> items;
    private Long currentValue;

    public MValueMultiVar(List<MValue> items) {
        this.items = Collections.unmodifiableList(items);
    }

    @Override
    public Integer getValue() {
        return this.items.size();
    }

    @Override
    public MValue clone() {
        return new MValueMultiVar(items);
    }

    public List<MValue> getItems() {
        return items;
    }

    public boolean hasNext() {
        boolean ret = false;
        if (items.size()==2) {
            ret = true;
        } else if (items.size()==3) {
            if (currentValue==null) {
                ret = true;
            } else if (currentValue.compareTo((Long)items.get(2).toNumber())<0) {
                ret = true;
            }
        }
        return ret;
    }

    public MValue next() {
        if (currentValue==null) {
            currentValue = (Long)items.get(0).toNumber();
        } else {
            Long incr = (Long)items.get(1).toNumber();
            currentValue += incr;
        }
        return new MValueNumber(currentValue);
    }

    @Override
    public Number toNumber() {
        return (currentValue==null?0l:currentValue);
    }

    @Override
    public int compareTo(MValue o) {
        throw new IllegalArgumentException("Impossivel comparar valor tipo MultiVar");
    }
}
