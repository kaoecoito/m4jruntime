package br.com.sisprof.m4jruntime.runtime;

import br.com.sisprof.m4jruntime.runtime.instructions.*;

/**
 * Created by kaoe on 13/09/16.
 */
public class OperatorFactory {


    public static Instruction createBinary(int indent, int line, String name) {
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
        } else if ("+".equals(name)) {
            op = BinaryADD.create(indent, line);
        } else if ("-".equals(name)) {
            op = BinarySUB.create(indent, line);
        } else if ("*".equals(name)) {
            op = BinaryMULTI.create(indent, line);
        } else if ("**".equals(name)) {
            op = BinaryEXPR.create(indent, line);
        } else if ("/".equals(name)) {
            op = BinaryDIV.create(indent, line);
        } else if ("\\".equals(name)) {
            op = BinaryIDIV.create(indent, line);
        } else if ("#".equals(name)) {
            op = BinaryMOD.create(indent, line);
        }
        return op;
    }

    public static Instruction createUnary(int indent, int line, String name) {
        Instruction op = null;
        if ("'".equals(name)) {
            op = UnaryNOT.create(indent, line);
        } else if ("+".equals(name)) {
            op = UnaryADD.create(indent, line);
        } else if ("-".equals(name)) {
            op = UnarySUB.create(indent, line);
        }
        return op;
    }


}
