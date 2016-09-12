package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 09/09/16.
 */
public class Label extends AbstractInstruction {

    private Label(int line) {
        super(0, line);
    }

    public static Label create(int line) {
        return new Label(line);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.LABEL;
    }

    @Override
    public int getParam() {
        return 0;
    }

    @Override
    public CallAction execute(Frame frame) {
        return CallAction.None;
    }

}
