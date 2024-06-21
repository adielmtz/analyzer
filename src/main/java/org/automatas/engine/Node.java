package org.automatas.engine;

/**
 * Represents the execution result obtained after executing Ast nodes.
 */
public final class Node {
    private NodeType type;
    private Scalar value;
    private Reference reference;

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

    public boolean hasValue() {
        return value != null;
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
     * Tests if the node contains a Reference instance.
     *
     * @return True if the node contains a Reference instance; false otherwise.
     */
    public boolean hasReference() {
        return reference != null;
    }

    /**
     * Gets the Reference instance.
     *
     * @return The Reference instance.
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * Sets the Reference instance.
     *
     * @param reference The Reference instance to set.
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public boolean mustReturn() {
        return type == NodeType.RETURN || type == NodeType.ERROR;
    }

    public void propagateTo(Node node) {
        node.type = type;
        node.value = value;
        node.reference = reference;
    }

    public boolean hasError() {
        return type == NodeType.ERROR;
    }

    public void raiseError(String fmt, Object ...args) {
        type = NodeType.ERROR;
        value = Scalar.makeString(String.format(fmt, args));
    }

    public void fnReturn() {
        fnReturn(null);
    }

    public void fnReturn(Scalar value) {
        this.type = NodeType.RETURN;
        this.value = value;
    }
}
