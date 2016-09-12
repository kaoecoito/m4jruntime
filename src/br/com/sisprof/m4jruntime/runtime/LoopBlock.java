package br.com.sisprof.m4jruntime.runtime;

import java.util.Collections;
import java.util.List;

/**
 * Created by kaoe on 11/09/16.
 */
public class LoopBlock {

    private final int indent;
    private final String varName;
    private final List<MValue> items;

    private MValue currentValue;
    private int currentItem;

    public LoopBlock(int indent, String varName, List<MValue> items) {
        this.indent = indent;
        this.varName = varName;
        this.items = items;
    }

    public static LoopBlock create(int indent, String varName, List<MValue> items) {
        return new LoopBlock(indent, varName, items);
    }

    public void setCurrentValue(MValue currentValue) {
        this.currentValue = currentValue;
    }

    public MValue getCurrentValue() {
        return currentValue;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public int getIndent() {
        return indent;
    }

    public String getVarName() {
        return varName;
    }

    public List<MValue> getItems() {
        return Collections.unmodifiableList(items);
    }
}
