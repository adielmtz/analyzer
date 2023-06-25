package org.automatas.runtime;

public enum OpCode {
    NOP,

    /* Register related */
    MOVE,

    /* Memory related */
    LOAD,
    STORE,
    PUSH,
    POP,
    PEEK,

    /* Comparator & Jumps */
    COMPARE,
    JMP,
    JMP_EQ,
    JMP_NEQ,
    JMP_LT,
    JMP_GT,

    /* I/O */
    INPUT,
    OUTPUT,
}
