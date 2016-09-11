package br.com.sisprof.m4jruntime.runtime;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kaoe on 09/09/16.
 */
public abstract class AbstractInstruction implements Instruction {

    private final int indent;
    private final int line;

    public AbstractInstruction(int indent, int line) {
        this.indent = indent;
        this.line = line;
    }

    public int getIndent() {
        return indent;
    }

    public int getLine() {
        return line;
    }

    @Override
    public void write(Routine routine, DataOutputStream stream) throws IOException {
        ByteCode code = getByteCode();
        byte bval = (byte)code.ordinal();
        stream.writeByte(bval);
        stream.writeInt(getLine());
        stream.writeInt(getIndent());
        if (code.hasParameters()) {
            stream.writeInt(getParam());
        }
    }

}
