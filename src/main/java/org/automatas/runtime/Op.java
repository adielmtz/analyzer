package org.automatas.runtime;

public final class Op {
    public final OpCode code;
    public final int op1;
    public final int op2;

    public Op(OpCode code) {
        this(code, 0, 0);
    }

    public Op(OpCode code, int op1) {
        this(code, op1, 0);
    }

    public Op(OpCode code, int op1, int op2) {
        this.code = code;
        this.op1 = op1;
        this.op2 = op2;
    }
}
