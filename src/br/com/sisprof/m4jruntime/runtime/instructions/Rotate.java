package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaoe on 12/09/16.
 */
public class Rotate extends AbstractInstruction {

    private final int params;

    private Rotate(int indent, int line, int params) {
        super(indent, line);
        this.params = params;
    }

    public static Rotate create(int indent, int line, int params) {
        return new Rotate(indent, line, params);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.ROTATE;
    }

    @Override
    public int getParam() {
        return params;
    }

    @Override
    public CallAction execute(Frame frame) {
        if (params>1) {
            List<MValue> vars = new ArrayList<>();
            int loops = params;
            while (loops-->0) {
                vars.add(frame.pop());
            }
            for (MValue value:vars) {
                frame.push(value);
            }
        }
        return CallAction.None;
    }
}
