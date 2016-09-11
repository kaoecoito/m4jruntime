package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 10/09/16.
 */
public class Write extends AbstractInstruction {

    private final int params;

    private Write(int indent, int line, int params) {
        super(indent, line);
        this.params = params;
    }

    public static Write create(int indent, int line, int params) {
        return new Write(indent, line, params);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.CMD_WRITE;
    }

    @Override
    public int getParam() {
        return params;
    }

    @Override
    public CallAction execute(Frame frame) {
        int loops = params;
        while (loops-->0) {
            MValue value = frame.pop();
            System.out.print(value.getValue().toString());
        }
        return CallAction.None;
    }
}
