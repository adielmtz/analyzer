package org.automatas;

public final class Scalar implements Comparable<Scalar> {
    public final Object value;
    public final ScalarType type;

    public static Scalar fromBool(boolean value) {
        return new Scalar(value, ScalarType.IS_BOOL);
    }

    public static Scalar fromFloat(double value) {
        return new Scalar(value, ScalarType.IS_FLOAT);
    }

    public static Scalar fromInteger(long value) {
        return new Scalar(value, ScalarType.IS_INTEGER);
    }

    public static Scalar fromString(String value) {
        return new Scalar(value, ScalarType.IS_STRING);
    }

    private Scalar(Object value, ScalarType type) {
        this.value = value;
        this.type = type;
    }

    public boolean isBool() {
        return type == ScalarType.IS_BOOL;
    }

    public boolean isFloat() {
        return type == ScalarType.IS_FLOAT;
    }

    public boolean isInteger() {
        return type == ScalarType.IS_INTEGER;
    }

    public boolean isString() {
        return type == ScalarType.IS_STRING;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int compareTo(Scalar o) {
        return switch (type) {
            case IS_BOOL, IS_INTEGER -> Long.compare(asInteger(), o.asInteger());
            case IS_FLOAT -> Double.compare(asFloat(), o.asFloat());
            case IS_STRING -> asString().compareTo(o.asString());
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scalar s) {
            return switch (type) {
                case IS_BOOL -> asBool() == s.asBool();
                case IS_FLOAT -> asFloat() == s.asFloat();
                case IS_INTEGER -> asInteger() == s.asInteger();
                case IS_STRING -> asString().contentEquals(s.asString());
            };
        }

        return false;
    }

    public ScalarType getType() {
        return type;
    }

    public boolean asBool() {
        return switch (type) {
            case IS_BOOL -> (boolean) value;
            case IS_FLOAT -> (double) value != 0.d;
            case IS_INTEGER -> (long) value != 0L;
            case IS_STRING -> ((String) value).length() > 0;
        };
    }

    public double asFloat() {
        return switch (type) {
            case IS_BOOL -> (boolean) value ? 1 : 0;
            case IS_FLOAT-> (double) value;
            case IS_INTEGER -> (double) ((long) value);
            case IS_STRING -> Double.parseDouble((String) value);
        };
    }

    public long asInteger() {
        return switch (type) {
            case IS_BOOL -> (boolean) value ? 1 : 0;
            case IS_FLOAT -> (long) ((double) value);
            case IS_INTEGER -> (long) value;
            case IS_STRING -> Long.parseLong((String) value);
        };
    }

    public String asString() {
        if (type == ScalarType.IS_STRING) {
            return (String) value;
        }

        return value.toString();
    }
}
