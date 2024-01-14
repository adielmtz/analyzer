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
}
