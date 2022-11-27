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

    public Scalar(Object value, ScalarType type) {
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
        return getString();
    }

    @Override
    public int compareTo(Scalar o) {
        return switch (type) {
            case IS_BOOL, IS_INTEGER -> Long.compare(getInteger(), o.getInteger());
            case IS_FLOAT -> Double.compare(getFloat(), o.getFloat());
            case IS_STRING -> getString().compareTo(o.getString());
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scalar s) {
            return switch (type) {
                case IS_BOOL -> getBool() == s.getBool();
                case IS_FLOAT -> getFloat() == s.getFloat();
                case IS_INTEGER -> getInteger() == s.getInteger();
                case IS_STRING -> getString().contentEquals(s.getString());
            };
        }

        return false;
    }

    public ScalarType getType() {
        return type;
    }

    public boolean getBool() {
        return switch (type) {
            case IS_BOOL -> (boolean) value;
            case IS_FLOAT -> (double) value != 0.d;
            case IS_INTEGER -> (long) value != 0L;
            case IS_STRING -> ((String) value).length() > 0;
        };
    }

    public double getFloat() {
        return switch (type) {
            case IS_BOOL -> (boolean) value ? 1 : 0;
            case IS_FLOAT-> (double) value;
            case IS_INTEGER -> (double) ((long) value);
            case IS_STRING -> Double.parseDouble((String) value);
        };
    }

    public long getInteger() {
        return switch (type) {
            case IS_BOOL -> (boolean) value ? 1 : 0;
            case IS_FLOAT -> (long) ((double) value);
            case IS_INTEGER -> (long) value;
            case IS_STRING -> Long.parseLong((String) value);
        };
    }

    public String getString() {
        return String.valueOf(value);
    }
}
