package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 09/09/16.
 */
public enum ByteCode {

    NOOP(false),
    LABEL(false),
    BLOCK(false),
    RETURN(true),
    CONST(true),
    NEW_VAR(true),
    STORE_VAR(true),
    LOAD_VAR(true),
    JUMP_IF_TRUE(true),
    JUMP_IF_FALSE(true),
    JUMP(true),
    FOR_SETUP(true),
    FOR_INCREMENT(true),
    FOR_END(false),
    DUP_STACK(false),
    POP_STACK(true),
    ROTATE(true),
    BINARY_OR(false),
    CMD(true)
    ;

    private final boolean parameters;

    ByteCode(boolean parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameters() {
        return parameters;
    }

}
