package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaoe on 12/09/16.
 */
public class MultiVariable extends AbstractInstruction {

    private final int vars;

    private MultiVariable(int indent, int line, int vars) {
        super(indent, line);
        this.vars = vars;
    }

    public static MultiVariable create(int indent, int line, int vars) {
        return new MultiVariable(indent, line, vars);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.MULTI_VAR;
    }

    @Override
    public int getParam() {
        return vars;
    }

    @Override
    public CallAction execute(Frame frame) {
        List<MValue> items = new ArrayList<>();
        int loops = vars;
        while (loops-->0) {
            items.add(frame.pop());
        }
        frame.push(new MValueMultiVar(items));
        return CallAction.None;
    }
}
