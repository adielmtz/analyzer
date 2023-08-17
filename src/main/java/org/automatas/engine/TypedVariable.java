package org.automatas.engine;

public class TypedVariable {
    private final String name;
    private final ScalarType type;
    private final String structName;

    private Scalar value;

    public TypedVariable(String name, ScalarType type, String structName) {
        this.name = name;
        this.type = type;
        this.structName = structName;
    }

    public TypedVariable(String name, ScalarType type) {
        this(name, type, null);
    }

    public String getName() {
        return name;
    }

    public ScalarType getType() {
        return type;
    }

    public String getStructName() {
        return structName;
    }

    public Scalar getValue() {
        return value;
    }

    public void setValue(Scalar value) {
        this.value = value;
    }
}
