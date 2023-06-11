package org.automatas.engine;

/**
 * An AST is an abstract representation of a part of the input code.
 */
public final class Ast {
    private static final Ast[] EMPTY_CHILD = new Ast[0];

    public final AstKind kind;
    public final Object value;
    public final ScalarType type;
    public final Ast[] child;

    /**
     * Creates an Ast instance for Ast nodes.
     *
     * @param kind  The kind of the Ast instance.
     * @param child The children Ast nodes.
     * @return The Ast instance.
     */
    public static Ast make(AstKind kind, Ast... child) {
        return new Ast(kind, null, null, child);
    }

    /**
     * Creates an AST_AS node for type cast.
     *
     * @param expr The expression to cast.
     * @param type The type to cast.
     * @return The Ast instance.
     */
    public static Ast typeCast(Ast expr, Ast type) {
        return make(AstKind.AST_AS, expr, type);
    }

    /**
     * Creates an AST_IS node for type check.
     *
     * @param expr The expression to test.
     * @param type The type to check.
     * @return The Ast instance.
     */
    public static Ast typeCheck(Ast expr, Ast type) {
        return make(AstKind.AST_IS, expr, type);
    }

    /**
     * Creates an Ast instance for literal values.
     *
     * @param value The literal value.
     * @param type  The type of the value.
     * @return The Ast instance.
     */
    public static Ast scalar(Object value, ScalarType type) {
        return new Ast(AstKind.AST_SCALAR, value, type, EMPTY_CHILD);
    }

    /**
     * Creates an identifier Ast node.
     *
     * @param name The name of the identifier.
     * @return The Ast instance.
     */
    public static Ast identifier(String name) {
        return new Ast(AstKind.AST_IDENTIFIER, name, ScalarType.STRING, EMPTY_CHILD);
    }

    /**
     * Ast constructor.
     *
     * @param kind  The kind of the Ast instance.
     * @param value The value of the Ast (scalar).
     * @param type  The type of the value.
     * @param child The array of child Ast nodes.
     */
    private Ast(AstKind kind, Object value, ScalarType type, Ast[] child) {
        this.kind = kind;
        this.value = value;
        this.type = type;
        this.child = child;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int depth) {
        var sb = new StringBuilder();
        String padding = " ".repeat(4 * depth);
        String innerPadding = " ".repeat(4 * (depth + 1));

        sb.append(padding).append("Ast {\n")
                .append(innerPadding).append("kind:  ").append(kind).append("\n")
                .append(innerPadding).append("value: ").append(value).append("\n")
                .append(innerPadding).append("type: ").append(type).append("\n")
                .append(innerPadding).append("child: {\n");

        for (Ast ast : child) {
            if (ast != null) {
                sb.append(ast.toString(depth + 2)).append("\n");
            }
        }

        sb.append(innerPadding).append("}\n");
        sb.append(padding).append("}");

        return sb.toString();
    }
}
