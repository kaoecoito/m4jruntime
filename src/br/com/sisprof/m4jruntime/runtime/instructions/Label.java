package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.AbstractInstruction;
import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 09/09/16.
 */
public class Label extends AbstractInstruction {

    private final int nameIndex;

    private Label(int line, int nameIndex) {
        super(0, line);
        this.nameIndex = nameIndex;
    }

    public static Label create(int line, int nameIndex) {
        return new Label(line, nameIndex);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.LABEL;
    }

    @Override
    public int getParam() {
        return nameIndex;
    }

    @Override
    public CallAction execute(Frame frame) {
        return CallAction.None;
    }

}
