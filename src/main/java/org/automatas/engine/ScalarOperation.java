package org.automatas.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs arithmetic operations with Scalar objects.
 */
public final class ScalarOperation {
    /**
     * Adds two scalar values.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar add(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            return mergeArrayList(a, b);
        }

        if (a.isString() || b.isString()) {
            return concatenateString(a, b);
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.makeFloat(a.toDouble() + b.toDouble());
            case BOOL, INT -> Scalar.makeInt(a.toLong() + b.toLong());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    /**
     * Merges two scalar arrays.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    private static Scalar mergeArrayList(Scalar a, Scalar b) {
        if (!a.isArray() || !b.isArray()) {
            throwIncompatibleTypes("+", a.getType(), b.getType());
        }

        List<Scalar> op1 = a.toList();
        List<Scalar> op2 = b.toList();
        List<Scalar> result = new ArrayList<>(op1.size() + op2.size());

        // Copy values
        result.addAll(op1);
        result.addAll(op2);

        return Scalar.makeArray(result);
    }

    /**
     * Concatenates two string scalars.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    private static Scalar concatenateString(Scalar a, Scalar b) {
        if (a.isArray() || b.isArray()) {
            throwIncompatibleTypes("+", a.getType(), b.getType());
        }

        String op1 = a.toString();
        String op2 = b.toString();
        return Scalar.makeString(op1 + op2);
    }

    /**
     * Subtracts two scalar values.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar subtract(Scalar a, Scalar b) {
        if (a.isArray() || a.isString() || b.isArray() || b.isString()) {
            throwIncompatibleTypes("-", a.getType(), b.getType());
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.makeFloat(a.toDouble() - b.toDouble());
            case BOOL, INT -> Scalar.makeInt(a.toLong() - b.toLong());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    /**
     * Multiplies two scalar values.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar multiply(Scalar a, Scalar b) {
        if (a.isArray() || a.isString() || b.isArray() || b.isString()) {
            throwIncompatibleTypes("*", a.getType(), b.getType());
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.makeFloat(a.toDouble() * b.toDouble());
            case BOOL, INT -> Scalar.makeInt(a.toLong() * b.toLong());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    /**
     * Elevates the first scalar to the power of the value of the second scalar.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar pow(Scalar a, Scalar b) {
        if (a.isArray() || a.isString() || b.isArray() || b.isString()) {
            throwIncompatibleTypes("**", a.getType(), b.getType());
        }

        double result = Math.pow(a.toDouble(), b.toDouble());
        if (a.isFloat() || b.isFloat()) {
            return Scalar.makeFloat(result);
        }

        // Bool or Int
        return Scalar.makeInt((long) result);
    }

    /**
     * Divides two scalar values.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar divide(Scalar a, Scalar b) {
        if (a.isArray() || a.isString() || b.isArray() || b.isString()) {
            throwIncompatibleTypes("/", a.getType(), b.getType());
        }

        // Integer division
        if ((a.isBoolean() || a.isInteger()) && (b.isBoolean() || b.isInteger())) {
            long op1 = a.toLong();
            long op2 = b.toLong();
            return Scalar.makeInt(op1 / op2);
        }

        // Float division
        double op1 = a.toDouble();
        double op2 = b.toDouble();
        return Scalar.makeFloat(op1 / op2);
    }

    /**
     * Calculates the modulus of two scalar values.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @return The result scalar.
     */
    public static Scalar modulus(Scalar a, Scalar b) {
        if (a.isArray() || a.isString() || b.isArray() || b.isString()) {
            throwIncompatibleTypes("%", a.getType(), b.getType());
        }

        return switch (a.getType()) {
            case FLOAT -> Scalar.makeFloat(a.toDouble() % b.toDouble());
            case BOOL, INT -> Scalar.makeInt(a.toLong() % b.toLong());
            default -> throw new IllegalStateException("Unexpected value: " + a.getType());
        };
    }

    private static void throwIncompatibleTypes(String op, ScalarType a, ScalarType b) {
        String message = "Unsupported operand types: %s %s %s".formatted(a, op, b);
        throw new RuntimeException(message);
    }

    /**
     * Static class.
     */
    private ScalarOperation() {
    }
}
