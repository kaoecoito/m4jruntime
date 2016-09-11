package br.com.sisprof.m4jruntime.runtime;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kaoe on 09/09/16.
 */
public interface Instruction {

    ByteCode getByteCode();
    int getParam();

    int getIndent();

    int getLine();

    CallAction execute(Frame frame);

    void write(Routine routine, DataOutputStream stream) throws IOException;

}
