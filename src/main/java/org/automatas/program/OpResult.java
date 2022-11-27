package org.automatas.program;

import org.automatas.Scalar;

public class OpResult {
    public enum ResultType {
        NONE,
        CONSTANT,
    }

    private ResultType type;
    private Scalar value;

    public OpResult() {
        this.type = ResultType.NONE;
        this.value = null;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public Scalar getValue() {
        return value;
    }

    public void setValue(Scalar value) {
        this.value = value;
    }
}
