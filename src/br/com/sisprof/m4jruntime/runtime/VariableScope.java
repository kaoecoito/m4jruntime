package br.com.sisprof.m4jruntime.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaoe on 09/09/16.
 */
public class VariableScope {

    private final Map<String,Variable> vars = new HashMap<>();
    private final VariableScope parentScope;

    public VariableScope(VariableScope parentScope) {
        this.parentScope = parentScope;
    }

    public Variable getVariable(String name) {
        if (vars.containsKey(name)) {
            return vars.get(name);
        } else if (parentScope!=null) {
            return parentScope.getVariable(name);
        }
        return null;
    }

    public Variable newVariable(String name) {
        Variable var = new Variable(name);
        vars.put(name, var);
        return var;
    }

}
