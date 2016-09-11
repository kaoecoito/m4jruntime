package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 10/09/16.
 */
public class Block extends AbstractInstruction {

    private Block(int indent, int line) {
        super(indent, line);
    }

    public static Block create(int indent, int line) {
        return new Block(indent, line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.BLOCK;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        frame.incIndent();
        return CallAction.None;
    }
}
