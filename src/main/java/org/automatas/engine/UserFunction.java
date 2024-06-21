package org.automatas.engine;

public class UserFunction {
    private final String name;
    private final String[] parameters;
    private final Ast body;

    public UserFunction(String name, String[] parameters, Ast body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Ast getBody() {
        return body;
    }
}
