package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 11/09/16.
 */
public class ForIncrement extends AbstractInstruction {

    private ForIncrement(int indent, int line) {
        super(indent, line);
    }

    public static ForIncrement create(int indent, int line) {
        return new ForIncrement(indent, line);
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.FOR_INCREMENT;
    }

    @Override
    public CallAction execute(Frame frame) {
        return CallAction.None;
    }

}
