package org.automatas.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a dynamic list of Ast nodes.
 */
public final class AstList {
    private final List<Ast> list;

    /**
     * Initializes a new AstList that results in a single AST_STATEMENT_LIST tree.
     *
     * @param child The child Ast objects to append to the list.
     * @return The AstList instance.
     */
    public static AstList beginList(Ast... child) {
        return new AstList(child);
    }

    private AstList(Ast[] child) {
        list = new ArrayList<>(child.length);
        Collections.addAll(list, child);
    }

    /**
     * Adds the object to the list and returns the AstList object.
     *
     * @param ast The Ast object to add.
     * @return The AstList itself.
     */
    public AstList add(Ast ast) {
        list.add(ast);
        return this;
    }

    /**
     * Finalizes the list as an AST_STATEMENT_LIST kind.
     *
     * @return The Ast instance of kind AST_STATEMENT_LIST.
     */
    public Ast makeList() {
        Ast[] child = list.toArray(new Ast[0]);
        return Ast.make(AstKind.AST_STATEMENT_LIST, child);
    }

    /**
     * Finalizes the list as an AST_ARRAY kind.
     *
     * @return The Ast instance of kind AST_ARRAY.
     */
    public Ast makeArray() {
        int index = list.size() - 1;
        if (index >= 0 && list.get(index) == null) {
            // Remove trailing NULL value
            list.remove(index);
        }

        Ast[] child = list.toArray(new Ast[0]);
        return Ast.make(AstKind.AST_ARRAY, child);
    }
}
