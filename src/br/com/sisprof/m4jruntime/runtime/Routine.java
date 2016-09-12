package br.com.sisprof.m4jruntime.runtime;

import br.com.sisprof.m4jruntime.compiler.LabelInfo;
import br.com.sisprof.m4jruntime.runtime.instructions.ForEnd;
import br.com.sisprof.m4jruntime.runtime.instructions.NoOp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaoe on 06/09/16.
 */
public class Routine {

    private static final byte[] MAGIC = new byte[]{'M','4','J','R'};
    private static final byte[] VERSION = new byte[]{1, 0, 0};
    private static final int BUILD = 2016090900;

    private final List<LabelInfo> labels = new ArrayList<>();

    private final List<ConstantValue> constants = new ArrayList<>();
    private final Map<ConstantValue,Integer> constantMap = new HashMap<>();

    private final List<Instruction> stack = new ArrayList<>();

    public int add(ConstantValue constant) {
        if (!constantMap.containsKey(constant)) {
            constantMap.put(constant, constants.size());
            constants.add(constant);
        }
        return constantMap.get(constant);
    }

    public void add(LabelInfo info) {
        labels.add(info);
    }

    public void add(Instruction instruction) {
        stack.add(instruction);
    }

    public int getStackSize() {
        return stack.size();
    }

    public void writeFile(File file) throws IOException {
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        stream.write(MAGIC);
        stream.write(VERSION);
        stream.writeInt(BUILD);

        stream.writeInt(labels.size());
        for (LabelInfo label:labels) {
            stream.writeUTF(label.getName());
            stream.writeInt(label.getParams());
            stream.writeInt(label.getStack());
        }

        stream.writeInt(constants.size());
        for (ConstantValue constant:constants) {
            if (constant instanceof ConstantValueString) {
                stream.writeByte(1);
                stream.writeUTF(((ConstantValueString)constant).getValue());
            } else if (constant instanceof ConstantValueNumber) {
                Number number = ((ConstantValueNumber)constant).getValue();
                if (number instanceof Long) {
                    stream.writeByte(2);
                    stream.writeLong(number.longValue());
                } else if (number instanceof Double) {
                    stream.writeByte(3);
                    stream.writeDouble(number.doubleValue());
                }
            } else if (constant instanceof ConstantValueOperator) {
                stream.writeByte(4);
                stream.writeUTF(((ConstantValueOperator)constant).getValue());
            }
        }

        for (Instruction instruction:stack) {
            instruction.write(this, stream);
        }
        stream.close();
    }

    public ConstantValue getConstantValue(int index) {
        return constants.get(index);
    }

    public void run(VirtualMachine machine) {

        Frame frame = machine.getFrame();

        // TODO Este código deveria ser na VM?

        while (true) {
            int execPoint = frame.getExecPoint();
            if (stack.size()<=execPoint) break;

            Instruction instruction = stack.get(execPoint);
            if (!NoOp.isNoOp(instruction) &&
                    instruction.getIndent()<=frame.getIndent()) {

                if (instruction.getIndent()<frame.getIndent()) {
                    frame.setIndent(instruction.getIndent());
                }

                CallAction action = instruction.execute(frame);

                if (CallAction.Break.equals(action)) {
                    exitLoop(frame);
                } else if (CallAction.Return.equals(action)) {
                    break;
                }
            }

            frame.next();
        }

    }

    private void exitLoop(Frame frame) {
        int execPoint = frame.getExecPoint()+1;
        while (execPoint<stack.size()) {
            Instruction instruction = stack.get(execPoint);
            if (instruction instanceof ForEnd) {
                frame.jump(execPoint-1);
                break;
            }
            execPoint++;
        }
    }

}
