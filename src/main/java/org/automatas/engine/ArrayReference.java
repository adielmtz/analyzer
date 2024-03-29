package org.automatas.engine;

import java.util.List;

/**
 * Holds a reference to an array index within a Scalar object.
 */
public final class ArrayReference implements Reference {
    private final Scalar array;
    private final Scalar index;

    /**
     * ArrayReference constructor.
     *
     * @param array The base array.
     * @param index The index within array.
     */
    public ArrayReference(Scalar array, Scalar index) {
        assert array.isArray();
        this.array = array;
        this.index = index;
    }

    /**
     * Gets the array scalar.
     *
     * @return The Scalar instance which contains the array.
     */
    public Scalar getArray() {
        return array;
    }

    /**
     * Gets the index scalar.
     *
     * @return The Scalar instance which contains the array index.
     */
    public Scalar getIndex() {
        return index;
    }

    /**
     * Dereferences the array at the position specified by the index and gets the scalar value.
     *
     * @return The Scalar instance within array located at the position in index.
     */
    @Override
    public Scalar getValue() {
        List<Scalar> list = array.toList();
        int pos = (int) index.toLong();
        return list.get(pos);
    }

    /**
     * Dereferences the array at the position specified by the index and sets the scalar value.
     *
     * @param value The value to set at the position specified by the index.
     */
    @Override
    public void setValue(Scalar value) {
        List<Scalar> list = array.toList();
        int pos = (int) index.toLong();
        list.set(pos, value);
    }

    @Override
    public void remove() {
        List<Scalar> list = array.toList();
        int pos = (int) index.toLong();
        list.remove(pos);
    }
}
