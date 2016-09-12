package br.com.sisprof.m4jruntime.runtime;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * Created by kaoe on 09/09/16.
 */
public class ConstantValueOperator extends AbstractConstantValue<String> {

    private final String value;

    public ConstantValueOperator(String value) {
        this.value = value;
    }

    public ConstantValueOperator(List<TerminalNode> opers) {
        StringBuilder builder = new StringBuilder();
        for (TerminalNode oper:opers) {
            builder.append(oper.getText());
        }
        this.value = builder.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

}
