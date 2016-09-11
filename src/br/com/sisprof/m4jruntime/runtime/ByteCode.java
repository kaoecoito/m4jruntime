package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public enum ByteCode {

    NOOP(false),
    LABEL(false), // TODO Parametros???
    BLOCK(false),
    RETURN(true),
    CONST(true),
    NEW_VAR(true),
    STORE_VAR(true),
    LOAD_VAR(true),
    JUMP_IF_TRUE(true),
    JUMP_IF_FALSE(true),
    JUMP(true),
    DUP_STACK(false),
    POP_STACK(true),
    CMD(true),
    CMD_WRITE(true),
    ;

    private final boolean parameters;

    ByteCode(boolean parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameters() {
        return parameters;
    }

}
