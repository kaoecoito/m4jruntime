package br.com.sisprof.m4jruntime.runtime;

import br.com.sisprof.m4jruntime.database.DatabaseFactory;
import br.com.sisprof.m4jruntime.database.DatabaseStorage;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kaoe on 09/09/16.
 */
public class VirtualMachine {

    private static final AtomicLong JOB_GENERATOR = new AtomicLong(0);

    private static final ThreadLocal<VirtualMachine> CURRENT_VIRTUAL_MACHINE = new ThreadLocal<>();

    private final Map<String,Method> commandsAndFunctions = new HashMap<>();

    private final long job;
    private final DatabaseFactory databaseFactory;
    private final Deque<Frame> frames = new LinkedList<>();
    public final VariableScope globalScope = new VariableScope(null);

    private DatabaseStorage storage;
    private Frame frame;

    private VirtualMachine(long job, DatabaseFactory databaseFactory) {
        this.job = job;
        this.databaseFactory = databaseFactory;
        this.init();
    }

    public static VirtualMachine newVirtualMachine(DatabaseFactory databaseFactory) {
        return new VirtualMachine(JOB_GENERATOR.incrementAndGet(), databaseFactory);
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

    public DatabaseStorage getStorage() {
        return storage;
    }

    private void init() {
        this.storage = databaseFactory.create();

        loadCommands(DefaultCommands.class);
        loadFunctions(DefaultFunctions.class);
    }

    public void close() {
        if (storage!=null) {
            storage.close();
        }
    }

    public void loadFunctions(Class clazz) {
        loadCommands(clazz);
    }

    public void loadCommands(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method:methods) {
            if (method.isAnnotationPresent(MumpsCommand.class) && Modifier.isStatic(method.getModifiers())) {
                MumpsCommand cmdAnnotation = method.getAnnotation(MumpsCommand.class);
                Class param = null;
                if (method.getParameterCount()==1) {
                    param = method.getParameterTypes()[0];
                }
                Class ret = method.getReturnType();
                if (param!=null &&
                        param.isAssignableFrom(MValue[].class) &&
                        cmdAnnotation.value()!=null &&
                        cmdAnnotation.value().length>0 &&
                        ret.equals(Void.TYPE)) {
                    for (String cmd:cmdAnnotation.value()) {
                        commandsAndFunctions.put(cmd.toUpperCase(), method);
                    }
                }
            } else  if (method.isAnnotationPresent(MumpsFunction.class) && Modifier.isStatic(method.getModifiers())) {
                MumpsFunction funcAnnotation = method.getAnnotation(MumpsFunction.class);
                Class param = null;
                if (method.getParameterCount()==1) {
                    param = method.getParameterTypes()[0];
                }
                Class ret = method.getReturnType();
                if (param!=null &&
                        param.isAssignableFrom(MValue[].class) &&
                        funcAnnotation.value()!=null &&
                        funcAnnotation.value().length>0 &&
                        ret.equals(MValue.class)) {
                    for (String cmd:funcAnnotation.value()) {
                        if (cmd.startsWith("$")) {
                            commandsAndFunctions.put(cmd.toUpperCase(), method);
                        }
                    }
                }
            }

        }
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

    public boolean existsCommandOrFunction(String name) {
        return commandsAndFunctions.containsKey(name.toUpperCase());
    }

    public Method getCommandOrFunction(String name) {
        return commandsAndFunctions.get(name.toUpperCase());
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

