package br.com.sisprof.m4jruntime.runtime;

import java.util.Collections;
import java.util.List;

/**
 * Created by kaoe on 12/09/16.
 */
public class MValueMultiVar extends MValue<Integer> {

    private final List<MValue> items;
    private Number currentValue;

    public MValueMultiVar(List<MValue> items) {
        this.items = Collections.unmodifiableList(items);
    }

    @Override
    public Integer getValue() {
        return this.items.size();
    }

    @Override
    public MValue cloneValue() {
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
            } else if (NumberOperations.compareAsNumber(currentValue.toString(), items.get(2).getValue().toString())<0) {
                ret = true;
            }
        }
        return ret;
    }

    public MValue next() {
        if (currentValue==null) {
            currentValue = items.get(0).toNumber();
        } else {
            Number valInc = items.get(1).toNumber();
            if (valInc instanceof Long) {
                currentValue = currentValue.longValue()+valInc.longValue();
            } else {
                currentValue = currentValue.doubleValue()+valInc.doubleValue();
            }
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
