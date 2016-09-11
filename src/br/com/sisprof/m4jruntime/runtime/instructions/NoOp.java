package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

/**
 * Created by kaoe on 10/09/16.
 */
public class NoOp extends AbstractInstruction {

    private NoOp(int indent, int line) {
        super(indent, line);
    }

    public static NoOp create() {
        return new NoOp(0, 0);
    }

    public static boolean isNoOp(Instruction instruction) {
        return (instruction instanceof NoOp);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.NOOP;
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
