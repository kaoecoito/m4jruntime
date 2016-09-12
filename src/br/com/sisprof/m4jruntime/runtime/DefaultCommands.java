package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 12/09/16.
 */
public class DefaultCommands {

    @MumpsCommand({"WRITE","W"})
    public static void write(MValue[] args) {
        for (MValue value:args) {
            if (value instanceof MValueOperator) {
                System.out.print(getOperator(value.getValue().toString()));
            } else {
                System.out.print(value.getValue().toString());
            }
        }
    }

    private static String getOperator(String op) {
        // TODO Implementar operações de WRITE
        return "\n";
    }


}
