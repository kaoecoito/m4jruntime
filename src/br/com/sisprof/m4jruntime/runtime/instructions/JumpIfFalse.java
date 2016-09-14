package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 09/09/16.
 */
public class JumpIfFalse extends JumpInstruction {

    private JumpIfFalse(int indent, int line, int jump) {
        super(indent, line, jump);
    }

    public static JumpIfFalse create(int indent, int line, int jump) {
        return new JumpIfFalse(indent, line, jump);
    }

    public static JumpIfFalse create(int indent, int line) {
        return new JumpIfFalse(indent, line, 0);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.JUMP_IF_FALSE;
    }

    @Override
    public int getParam() {
        return jump;
    }

    @Override
    public CallAction execute(Frame frame) {
        MValue test = frame.pop();
        if (NumberOperations.isFalse(test)) {
            frame.jump(jump);
        }
        return CallAction.None;
    }

}
