package br.com.sisprof.m4jruntime.runtime;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kaoe on 09/09/16.
 */
public class VirtualMachine {

    private static final AtomicLong JOB_GENERATOR = new AtomicLong(0);

    private static final ThreadLocal<VirtualMachine> CURRENT_VIRTUAL_MACHINE = new ThreadLocal<>();

    private final long job;
    private final Deque<Frame> frames = new LinkedList<>();
    public final VariableScope globalScope = new VariableScope(null);
    private Frame frame;

    private VirtualMachine(long job) {
        this.job = job;
    }

    public static VirtualMachine newVirtualMachine() {
        return new VirtualMachine(JOB_GENERATOR.incrementAndGet());
    }

    public static VirtualMachine getCurrent() {
        return CURRENT_VIRTUAL_MACHINE.get();
    }

    public long getJob() {
        return job;
    }

    public Frame getFrame() {
        return frame;
    }

    public void addFrame(Frame frame) {
        frames.addFirst(frame);
        this.frame = frame;
    }

    public Frame popFrame() {
        if (frames.isEmpty()) return null;
        Frame item = frames.removeFirst();
        if (frames.isEmpty()) {
            this.frame = null;
        } else {
            this.frame = frames.peekFirst();
        }
        return item;
    }

    private Frame createFrame(Routine routine) {
        Frame frame;
        if (this.frame==null) {
            frame = new Frame(null, globalScope, globalScope, routine);
        } else {
            frame = new Frame(this.frame, globalScope, this.frame.getLocalScope(), routine);
        }
        return frame;
    }

    public void run(Routine routine) {
        Frame frame = createFrame(routine);
        this.addFrame(frame);
        this.resume(routine, frame);
        this.popFrame();
    }

    public void resume(Routine routine, Frame frame) {
        CURRENT_VIRTUAL_MACHINE.set(this);

        Frame oldFrame = this.frame;
        this.frame = frame;

        routine.run(this);

        MValue returnValue = null;
        if (!frame.isEmptyStack()) {
            returnValue = frame.pop();
        }
        if (returnValue!=null && frame.getParentFrame()!=null) {
            frame.getParentFrame().push(returnValue);
        }

        this.frame = oldFrame;

        CURRENT_VIRTUAL_MACHINE.remove();
    }

}

