package org.automatas.engine;

public enum NodeType {
    NONE,      /* No return value */
    CONSTANT,  /* Returns a value to user-land code */
    TMP_VALUE, /* Holds an internal value that must not be return to user-land code. */
    RETURN,    /* Function returning. */
    ERROR,     /* Function raised an error that must be handled. */
}
