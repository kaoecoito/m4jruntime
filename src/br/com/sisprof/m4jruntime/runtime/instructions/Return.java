package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 11/09/16.
 */
public class Return extends AbstractInstruction {

    private final int value;

    private Return(int indent, int line, int value) {
        super(indent, line);
        this.value = value;
    }

    public static Return create(int indent, int line, int value) {
        return new Return(indent, line, value);
    }

    public static Return create(int indent, int line) {
        return new Return(indent, line, 0);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.RETURN;
    }

    @Override
    public int getParam() {
        return value;
    }

    @Override
    public CallAction execute(Frame frame) {
        if (value==0) {
            frame.decIndent();
        }
        return (value==0?CallAction.None:CallAction.Return);
    }
}
