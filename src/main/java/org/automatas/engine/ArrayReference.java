package org.automatas.engine;

public class ArrayReference {
    private final Scalar array;
    private final Scalar index;

    public ArrayReference(Scalar array, Scalar index) {
        this.array = array;
        this.index = index;
    }

    public Scalar getArray() {
        return array;
    }

    public Scalar getIndex() {
        return index;
    }

    public Scalar getDerefScalar() {
        return array.asArray().get((int) index.asInteger());
    }
}
