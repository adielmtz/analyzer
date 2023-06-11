package org.automatas.engine;

/**
 * Represents the execution result obtained after executing Ast nodes.
 */
public final class Node {
    private NodeType type;
    private Scalar value;

    private ArrayReference reference;

    /**
     * Node constructor.
     */
    public Node() {
        type = NodeType.NONE;
        value = null;
    }

    /**
     * Gets the node type.
     *
     * @return The NodeType value.
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Sets the node type.
     *
     * @param type The node type.
     */
    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * Gets the Scalar value set by the execution node.
     *
     * @return The scalar value.
     */
    public Scalar getValue() {
        return value;
    }

    /**
     * Sets a Scalar value to pass to the parent execution node.
     *
     * @param value The scalar value to set.
     */
    public void setValue(Scalar value) {
        this.value = value;
    }

    /**
     * Tests if the node contains an ArrayReference instance.
     *
     * @return True if the node contains an ArrayReference instance; false otherwise.
     */
    public boolean hasArrayReference() {
        return reference != null;
    }

    /**
     * Gets the ArrayReference instance.
     *
     * @return The ArrayReference instance.
     */
    public ArrayReference getArrayReference() {
        return reference;
    }

    /**
     * Sets the ArrayReference instance.
     *
     * @param reference The ArrayReference instance to set.
     */
    public void setArrayReference(ArrayReference reference) {
        this.reference = reference;
    }

    /**
     * Creates and sets the ArrayReference instance.
     *
     * @param array The array scalar.
     * @param index The index scalar.
     */
    public void setArrayReference(Scalar array, Scalar index) {
        reference = new ArrayReference(array, index);
    }
}
