package org.automatas.engine;

public enum ScalarType {
    ARRAY("array"),
    BOOLEAN("bool"),
    FLOAT("float"),
    INTEGER("int"),
    STRING("string");

    private final String displayName;

    ScalarType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
