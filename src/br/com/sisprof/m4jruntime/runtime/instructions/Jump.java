package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.ByteCode;
import br.com.sisprof.m4jruntime.runtime.CallAction;
import br.com.sisprof.m4jruntime.runtime.Frame;

/**
 * Created by kaoe on 09/09/16.
 */
public class Jump extends JumpInstruction {

    private Jump(int indent, int line, int jump) {
        super(indent, line, jump);
    }

    public static Jump create(int indent, int line, int jump) {
        return new Jump(indent, line, jump);
    }

    public static Jump create(int indent, int line) {
        return new Jump(indent, line, 0);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.JUMP;
    }

    @Override
    public int getParam() {
        return jump;
    }

    @Override
    public CallAction execute(Frame frame) {
        frame.jump(jump);
        return CallAction.None;
    }

}
