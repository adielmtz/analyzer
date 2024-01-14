package org.automatas.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a literal value of any type (Array, Bool, Float, Int, String).
 */
public final class Scalar implements Comparable<Scalar> {
    private final Object value;
    private final ScalarType type;

    public static Scalar make(Object value, ScalarType type) {
        // TODO: Add strict type-checking here.
        return new Scalar(value, type);
    }

    /**
     * Creates an array scalar.
     *
     * @param list The list to copy into the array scalar.
     * @return The Scalar of type array.
     */
    public static Scalar makeArray(List<Scalar> list) {
        return new Scalar(list, ScalarType.ARRAY);
    }

    /**
     * Creates a bool scalar.
     *
     * @param value The boolean value to copy into the Scalar.
     * @return The Scalar of type bool.
     */
    public static Scalar makeBool(boolean value) {
        return new Scalar(value, ScalarType.BOOL);
    }

    /**
     * Creates a float scalar.
     *
     * @param value The double value to copy into the Scalar.
     * @return The Scalar of type float.
     */
    public static Scalar makeFloat(double value) {
        return new Scalar(value, ScalarType.FLOAT);
    }

    /**
     * Creates an int scalar.
     *
     * @param value The integer value to copy into the Scalar.
     * @return The Scalar of type int.
     */
    public static Scalar makeInt(long value) {
        return new Scalar(value, ScalarType.INT);
    }

    /**
     * Creates a string scalar.
     *
     * @param value The string value to copy into the Scalar.
     * @return The Scalar of type string.
     */
    public static Scalar makeString(String value) {
        return new Scalar(value, ScalarType.STRING);
    }

    public static Scalar makeObject(String name, HashMap<String, Scalar> members) {
        return new Scalar(new StructInstance(name, members), ScalarType.OBJECT);
    }

    /**
     * Scalar constructor.
     *
     * @param value The value of the scalar.
     * @param type  The type of the value.
     */
    private Scalar(Object value, ScalarType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public int compareTo(Scalar o) {
        return switch (type) {
            case ARRAY -> Integer.compare(toList().size(), o.toList().size());
            case BOOL, INT -> Long.compare(toLong(), o.toLong());
            case FLOAT -> Double.compare(toDouble(), o.toDouble());
            case STRING -> toString().compareTo(o.toString());
            case OBJECT -> -1; // TODO: Implement object comparison
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scalar scalar) {
            return value.equals(scalar.value);
        }

        return false;
    }

    /**
     * Returns the ScalarType of this instance.
     *
     * @return The type of the Scalar.
     */
    public ScalarType getType() {
        return type;
    }

    /**
     * Tests if the Scalar is of type array.
     *
     * @return True if the value is of type Array; false otherwise.
     */
    public boolean isArray() {
        return type == ScalarType.ARRAY;
    }

    /**
     * Tests if the Scalar is of type bool.
     *
     * @return True if the value is of type bool; false otherwise.
     */
    public boolean isBoolean() {
        return type == ScalarType.BOOL;
    }

    /**
     * Tests if the Scalar is of type float.
     *
     * @return True if the value is of type float; false otherwise.
     */
    public boolean isFloat() {
        return type == ScalarType.FLOAT;
    }

    /**
     * Tests if the Scalar is of type int.
     *
     * @return True if the value is of type int; false otherwise.
     */
    public boolean isInteger() {
        return type == ScalarType.INT;
    }

    /**
     * Tests if the Scalar is of type string.
     *
     * @return True if the value is of type string; false otherwise.
     */
    public boolean isString() {
        return type == ScalarType.STRING;
    }

    public boolean isObject() {
        return type == ScalarType.OBJECT;
    }

    /**
     * Returns the Scalar value as a List.
     *
     * @return If the Scalar is an array, the internal List is returned; otherwise a new list is created
     * and returning containing this instance.
     */
    @SuppressWarnings("unchecked")
    public List<Scalar> toList() {
        if (isArray()) {
            return (List<Scalar>) value;
        }

        // Create a single item list
        List<Scalar> list = new ArrayList<>(1);
        list.add(this);
        return list;
    }

    /**
     * Returns the Scalar value as boolean.
     *
     * @return The boolean value.
     */
    public boolean toBoolean() {
        return switch (type) {
            case ARRAY -> toList().size() > 0;
            case BOOL -> (boolean) value;
            case FLOAT -> toDouble() != 0d;
            case INT -> toLong() != 0;
            case STRING -> toString().length() > 0;
            case OBJECT -> true; // TODO: Implement proper object->toBoolean()
        };
    }

    /**
     * Returns the Scalar value as double.
     *
     * @return The double value.
     */
    public double toDouble() {
        return switch (type) {
            case ARRAY, BOOL -> toBoolean() ? 1d : 0d;
            case FLOAT -> (double) value;
            case INT -> (double) toLong();
            case STRING -> tryParseDouble();
            case OBJECT -> 1.d; // TODO: Implement proper object->toDouble()
        };
    }

    /**
     * Returns the Scalar value as long integer.
     *
     * @return The long integer value.
     */
    public long toLong() {
        return switch (type) {
            case ARRAY, BOOL -> toBoolean() ? 1 : 0;
            case FLOAT -> (long) toDouble();
            case INT -> (long) value;
            case STRING -> tryParseLong();
            case OBJECT -> 1; // TODO: Implement proper object->toLong()
        };
    }

    /**
     * Returns the Scalar value as String.
     *
     * @return the String value.
     */
    @Override
    public String toString() {
        if (isString()) {
            return (String) value;
        } else if (isArray()) {
            return arrayToString();
        } else if (isObject()) {
            return objectToString();
        } else {
            return value.toString();
        }
    }

    public StructInstance toObject() {
        if (isObject()) {
            return (StructInstance) value;
        }

        return null;
    }

    private String arrayToString() {
        List<Scalar> list = toList();
        var builder = new StringBuilder();
        int size = list.size();

        builder.append('[');

        for (int i = 0; i < size; i++) {
            builder.append(list.get(i).toString());

            if (i + 1 < size) {
                builder.append(", ");
            }
        }

        builder.append(']');

        return builder.toString();
    }

    private String objectToString() {
        return "";
    }

    /**
     * Tries to parse the String value of the Scalar as double.
     *
     * @return The double value. If the string could not be parsed, 0 is returned.
     */
    private double tryParseDouble() {
        try {
            return Double.parseDouble(toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Tries to parse the String value of the Scalar as long integer.
     *
     * @return The long integer value. If the string could not be parsed, 0 is returned.
     */
    private long tryParseLong() {
        try {
            return Long.parseLong(toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
