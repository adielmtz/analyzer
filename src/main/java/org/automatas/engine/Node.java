package org.automatas.engine;

import java.util.Optional;

public final class Node {
    private NodeType type;
    private Scalar value;

    private ArrayReference arrayReference;

    public Node() {
        type = NodeType.NONE;
        value = null;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public Scalar getValue() {
        return value;
    }

    public void setValue(Scalar value) {
        this.value = value;
    }

    public boolean hasArrayRef() {
        return arrayReference != null;
    }

    public ArrayReference getArrayRef() {
        return arrayReference;
    }

    public void setArrayRef(Scalar array, Scalar index) {
        arrayReference = new ArrayReference(array, index);
    }

    public void setArrayRef(ArrayReference reference) {
        arrayReference = reference;
    }
}
