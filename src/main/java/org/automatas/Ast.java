package org.automatas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ast {
    public final AstKind kind;
    public final Scalar value;
    public final Ast[] child;

    public static Ast create(AstKind kind, Ast... child) {
        return new Ast(kind, null, child);
    }

    public static Ast createWithValue(AstKind kind, Scalar value) {
        return new Ast(kind, value, new Ast[0]);
    }

    public static List<Ast> createList(Ast... child) {
        return new ArrayList<>(Arrays.asList(child));
    }

    public static Ast createFromList(AstKind kind, List<Ast> list) {
        var child = new Ast[list.size()];
        list.toArray(child);
        return new Ast(kind, null, child);
    }

    private Ast(AstKind kind, Scalar value, Ast[] child) {
        this.kind = kind;
        this.value = value;
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
            sb.append(ast.toString(depth + 2)).append("\n");
        }

        sb.append(innerPadding).append("}\n");
        sb.append(padding).append("}");

        return sb.toString();
    }
}
