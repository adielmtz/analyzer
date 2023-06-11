package org.automatas.engine;

/**
 * Represents the type of the Scalar object.
 */
public enum ScalarType {
    ARRAY(),
    BOOL(),
    FLOAT(),
    INT(),
    STRING();

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static ScalarType getType(String name) {
        return switch (name.toLowerCase()) {
            case "array" -> ARRAY;
            case "bool" -> BOOL;
            case "float" -> FLOAT;
            case "int" -> INT;
            case "string" -> STRING;
            default -> null;
        };
    }
}
