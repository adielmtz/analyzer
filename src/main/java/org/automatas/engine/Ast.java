package org.automatas.engine;

import java.util.ArrayList;
import java.util.List;

public class Ast {
    private static final Ast[] EMPTY_CHILD = new Ast[0];

    public final AstKind kind;
    public final Scalar value;
    public final Object extra;
    public final Ast[] child;

    public static Ast scalar(AstKind kind, Scalar scalar) {
        return new Ast(kind, scalar, null, EMPTY_CHILD);
    }

    public static Ast make(AstKind kind, Ast... child) {
        return new Ast(kind, null, null, child);
    }

    public static Ast makeTypeCast(ScalarType type, Ast expr) {
        return new Ast(AstKind.AST_TYPE_CAST, null, type, new Ast[] { expr });
    }

    public static Ast makeTypeCheck(Ast expr, ScalarType type) {
        return new Ast(AstKind.AST_IS, null, type, new Ast[] { expr });
    }

    public static Ast makeArray(List<Ast> list) {
        int index = list.size() - 1;
        if (index >= 0 && list.get(index) == null) {
            // Remove trailing NULL value
            list.remove(index);
        }

        return makeFromList(AstKind.AST_ARRAY, list);
    }

    public static List<Ast> makeList(Ast... child) {
        List<Ast> list = new ArrayList<>(child.length);
        for (Ast ast : child) {
            if (ast == null) {
                break;
            }

            list.add(ast);
        }

        return list;
    }

    public static Ast makeFromList(AstKind kind, List<Ast> list) {
        return new Ast(kind, null, null, list.toArray(Ast[]::new));
    }

    protected Ast(AstKind kind, Scalar value, Object extra, Ast[] child) {
        this.kind = kind;
        this.value = value;
        this.extra = extra;
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
