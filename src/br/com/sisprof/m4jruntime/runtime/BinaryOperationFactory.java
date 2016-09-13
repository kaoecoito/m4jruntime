package br.com.sisprof.m4jruntime.runtime;

import br.com.sisprof.m4jruntime.runtime.instructions.*;

/**
 * Created by kaoe on 13/09/16.
 */
public class BinaryOperationFactory {


    public static Instruction create(int indent, int line, String name) {
        Instruction op = null;
        if ("=".equals(name)) {
            op = BinaryEQ.create(indent, line);
        } else if ("'=".equals(name)) {
            op = BinaryNEQ.create(indent, line);
        } else if (">".equals(name) || "'<".equals(name)) {
            op = BinaryGT.create(indent, line);
        } else if ("<".equals(name) || "'>".equals(name)) {
            op = BinaryLT.create(indent, line);
        } else if (">=".equals(name)) {
            op = BinaryGTE.create(indent, line);
        } else if ("<=".equals(name)) {
            op = BinaryLTE.create(indent, line);
        } else if ("_".equals(name)) {
            op = BinaryConcat.create(indent, line);
        } else if ("!".equals(name)) {
            op = BinaryOR.create(indent, line);
        } else if ("&".equals(name)) {
            op = BinaryAND.create(indent, line);
        }
        return op;
    }
}
