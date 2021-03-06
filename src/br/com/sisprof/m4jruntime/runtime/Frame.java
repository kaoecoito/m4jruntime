package br.com.sisprof.m4jruntime.runtime;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by kaoe on 09/09/16.
 */
public class Frame {

    private final Frame parentFrame;
    private final VariableScope globalScope;
    private final VariableScope localScope;
    private final Routine routine;

    private final Deque<LoopBlock> loops = new LinkedList<>();

    private int execPoint;
    private int currentIndent;

    public Frame(Frame parentFrame, VariableScope globalScope, VariableScope parentScope, Routine routine) {
        this.parentFrame = parentFrame;
        this.globalScope = globalScope;
        this.localScope = new VariableScope(parentScope);
        this.routine = routine;
    }

    public VariableScope getLocalScope() {
        return localScope;
    }

    public VariableScope getGlobalScope() {
        return globalScope;
    }

    public Routine getRoutine() {
        return routine;
    }

    private final Deque<MValue> dataStack = new LinkedList<>();

    public int getExecPoint() {
        return execPoint;
    }

    public void next() {
        execPoint++;
    }

    public void jump(int execPoint) {
        this.execPoint = execPoint;
    }

    public Deque<MValue> getDataStack() {
        return dataStack;
    }

    public void push(MValue value) {
        dataStack.addFirst(value);
    }

    public boolean isEmptyStack() {
        return dataStack.isEmpty();
    }

    public MValue pop() {
        return dataStack.removeFirst();
    }

    public MValue top() {
        return dataStack.getFirst();
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public int getIndent() {
        return currentIndent;
    }

    public void setIndent(int currentIndent) {
        this.currentIndent = currentIndent;
    }

    public void incIndent() {
        this.currentIndent++;
    }

    public void decIndent() {
        if (this.currentIndent==0) {
            return;
        }
        this.currentIndent--;
    }

    public void pushLoop(LoopBlock block) {
        loops.addFirst(block);
    }

    public LoopBlock popLoop() {
        return loops.removeFirst();
    }

    public LoopBlock currentLoop() {
        return loops.getFirst();
    }

    public boolean inLoop(int indent) {
        if (loops.isEmpty()) {
            return false;
        }
        return  (loops.getFirst().getIndent()==indent);
    }

}
