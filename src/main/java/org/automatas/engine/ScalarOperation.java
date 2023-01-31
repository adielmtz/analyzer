package org.automatas.engine;

public final class ScalarOperation {
    public static Scalar add(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            String result = a.asString().concat(b.asString());
            return Scalar.fromString(result);
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.fromFloat(a.asFloat() + b.asFloat());
            case BOOLEAN, INTEGER -> Scalar.fromInteger(a.asInteger() + b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    public static Scalar subtract(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            throwIncompatibleType(ScalarType.STRING);
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.fromFloat(a.asFloat() - b.asFloat());
            case BOOLEAN, INTEGER -> Scalar.fromInteger(a.asInteger() - b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    public static Scalar multiply(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            throwIncompatibleType(ScalarType.STRING);
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.fromFloat(a.asFloat() * b.asFloat());
            case BOOLEAN, INTEGER -> Scalar.fromInteger(a.asInteger() * b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    public static Scalar pow(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            throwIncompatibleType(ScalarType.STRING);
        }

        double result = Math.pow(a.asFloat(), b.asFloat());
        if (a.isFloat() || b.isFloat()) {
            return Scalar.fromFloat(result);
        }

        // Bool or int
        return Scalar.fromInteger((long) result);
    }

    public static Scalar divide(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            throwIncompatibleType(ScalarType.STRING);
        }

        // Do integer division
        if ((a.isBoolean() || a.isInteger()) && (b.isBoolean() || b.isInteger())) {
            long result = a.asInteger() / b.asInteger();
            return Scalar.fromInteger(result);
        }

        // Do float division
        double result = a.asFloat() / b.asFloat();
        return Scalar.fromFloat(result);
    }

    public static Scalar modulus(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleType(ScalarType.ARRAY);
        }

        if (a.isString() || b.isString()) {
            throwIncompatibleType(ScalarType.STRING);
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.fromFloat(a.asFloat() % b.asFloat());
            case BOOLEAN, INTEGER -> Scalar.fromInteger(a.asInteger() % b.asInteger());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private static void throwIncompatibleType(ScalarType type) {
        throw new RuntimeException("Incompatible type '%s' cannot be used as operand.".formatted(type));
    }

    private ScalarOperation() {
    }
}
