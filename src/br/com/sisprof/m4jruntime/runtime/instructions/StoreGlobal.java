package br.com.sisprof.m4jruntime.runtime.instructions;

import br.com.sisprof.m4jruntime.database.DatabaseKey;
import br.com.sisprof.m4jruntime.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaoe on 16/09/16.
 */
public class StoreGlobal extends AbstractInstruction {

    private final int params;

    private StoreGlobal(int indent, int line, int params) {
        super(indent, line);
        this.params = params;
    }

    public static StoreGlobal create(int indent, int line, int params) {
        return new StoreGlobal(indent, line, params);
    }

    @Override
    public ByteCode getByteCode() {
        return ByteCode.STORE_GLOBAL;
    }

    @Override
    public int getParam() {
        return params;
    }

    @Override
    public CallAction execute(Frame frame) {
        List<Object> paramList = new ArrayList<>();
        int loops = params;

        String globalName = frame.pop().getValue().toString();
        while (loops-->0) {
            MValue value = frame.pop();
            if (value instanceof MValueNumber) {
                paramList.add(value.getValue());
            } else {
                paramList.add(value.getValue().toString());
            }
        }
        String value = frame.pop().getValue().toString();

        VirtualMachine vm = VirtualMachine.getCurrent();
        DatabaseKey key;
        if (paramList.isEmpty()) {
            key = DatabaseKey.create(globalName.substring(1));
        } else {
            key = DatabaseKey.create(globalName.substring(1), paramList.toArray());
        }
        vm.getStorage().set(key, value);

        return CallAction.None;
    }
}
