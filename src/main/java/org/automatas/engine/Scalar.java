package org.automatas.engine;

import java.util.ArrayList;
import java.util.List;

public final class Scalar implements Comparable<Scalar> {
    private final Object value;
    private final ScalarType type;

    public static Scalar fromArray(List<Scalar> list) {
        return new Scalar(list, ScalarType.ARRAY);
    }

    public static Scalar fromBoolean(boolean value) {
        return new Scalar(value, ScalarType.BOOLEAN);
    }

    public static Scalar fromFloat(double value) {
        return new Scalar(value, ScalarType.FLOAT);
    }

    public static Scalar fromInteger(long value) {
        return new Scalar(value, ScalarType.INTEGER);
    }

    public static Scalar fromString(String value) {
        return new Scalar(value, ScalarType.STRING);
    }

    private Scalar(Object value, ScalarType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        if (isArray()) {
            List<Scalar> list = asArray();
            int size = list.size();

            var sb = new StringBuilder();
            sb.append('[');

            for (int i = 0; i < size; i++) {
                sb.append(list.get(i));
                if (i + 1 < size) {
                    sb.append(", ");
                }
            }

            return sb.append(']').toString();
        }

        return asString();
    }

    public ScalarType getType() {
        return type;
    }

    public boolean isArray() {
        return type == ScalarType.ARRAY;
    }

    public boolean isBoolean() {
        return type == ScalarType.BOOLEAN;
    }

    public boolean isFloat() {
        return type == ScalarType.FLOAT;
    }

    public boolean isInteger() {
        return type == ScalarType.INTEGER;
    }

    public boolean isString() {
        return type == ScalarType.STRING;
    }

    @Override
    public int compareTo(Scalar o) {
        return switch (type) {
            case ARRAY -> Integer.compare(asArray().size(), o.asArray().size());
            case BOOLEAN, INTEGER -> Long.compare(asInteger(), o.asInteger());
            case FLOAT -> Double.compare(asFloat(), o.asFloat());
            case STRING -> asString().compareTo(o.asString());
        };
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Scalar s && value.equals(s.value);
    }

    public List<Scalar> asArray() {
        if (isArray()) {
            return (List<Scalar>) value;
        }

        // Create single-element array
        var array = new ArrayList<Scalar>(1);
        array.add(this);
        return array;
    }

    public boolean asBoolean() {
        return switch (type) {
            case ARRAY -> asArray().size() > 0;
            case BOOLEAN -> (boolean) value;
            case FLOAT -> asFloat() != 0d;
            case INTEGER -> asInteger() != 0;
            case STRING -> asString().length() > 0;
        };
    }

    public double asFloat() {
        return switch (type) {
            case ARRAY, BOOLEAN -> asBoolean() ? 1d : 0d;
            case FLOAT -> (double) value;
            case INTEGER -> (double) ((long) value);
            case STRING -> tryParseDouble(asString());
        };
    }

    public long asInteger() {
        return switch (type) {
            case ARRAY, BOOLEAN -> asBoolean() ? 1 : 0;
            case FLOAT -> (long) ((double) value);
            case INTEGER -> (long) value;
            case STRING -> tryParseLong(asString());
        };
    }

    public String asString() {
        return switch (type) {
            case ARRAY -> throw new RuntimeException("Array to string conversion.");
            case STRING -> (String) value;
            default -> value.toString();
        };
    }

    private double tryParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long tryParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
