package org.automatas.engine;

import java.util.HashMap;

/**
 * Manages the scope of variables (symbols).
 */
public final class ScopeManager {
    private static class Scope {
        public final Scope parent;
        public final HashMap<String, Scalar> symbols;

        public Scope(Scope parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
        }
    }

    private Scope current;

    /**
     * Tests if the ScopeManager has a valid scope.
     *
     * @return If there's a valid scope; false otherwise.
     */
    public boolean hasActiveScope() {
        return current != null;
    }

    /**
     * Initializes a new nested scope.
     */
    public void beginBlock() {
        var parent = current;
        current = new Scope(parent);
    }

    /**
     * Finalizes a nested scope and returns the previous scope if any.
     */
    public void endBlock() {
        current = current.parent;
    }

    /**
     * Tests if the symbol exists within any scope. Lookup begins from the innermost scope.
     *
     * @param name The name of the symbol to lookup.
     * @return True if the symbol exists; false otherwise.
     */
    public boolean hasSymbol(String name) {
        return findScopeForSymbol(name) != null;
    }

    /**
     * Tests if the symbol name exists within the current scope.
     *
     * @param name The name of the symbol to lookup.
     * @return True if the symbol exists in the current scope; false otherwise.
     */
    public boolean hasLocalSymbol(String name) {
        return current != null && current.symbols.containsKey(name);
    }

    /**
     * Adds a new symbol to the current scope.
     *
     * @param name  The name of the symbol.
     * @param value The value of the symbol.
     */
    public void addSymbol(String name, Scalar value) {
        if (current != null) {
            current.symbols.put(name, value);
        }
    }

    /**
     * Gets the value of the symbol that matches the name. Lookup begins from the innermost scope.
     *
     * @param name The name of the symbol to lookup.
     * @return The Scalar value of the symbol if exists; otherwise null is returned.
     */
    public Scalar getSymbol(String name) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            return scope.symbols.get(name);
        }

        return null;
    }

    /**
     * Sets the value of the symbol that matches the name. Lookup begins from the innermost scope.
     *
     * @param name  The name of the symbol to lookup.
     * @param value The value to set.
     */
    public void setSymbol(String name, Scalar value) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            scope.symbols.put(name, value);
        }
    }

    /**
     * Removes the symbol that matches the name. Lookup begins from the innermost scope.
     *
     * @param name The name of the symbol to remove.
     */
    public void removeSymbol(String name) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            scope.symbols.remove(name);
        }
    }

    /**
     * Lookups the symbol that matches the name from the current scope to the root.
     *
     * @param symbol The name of the symbol to lookup.
     * @return The symbol if found; otherwise null is returned.
     */
    private Scope findScopeForSymbol(String symbol) {
        var scope = current;
        while (scope != null) {
            if (scope.symbols.containsKey(symbol)) {
                return scope;
            }

            scope = scope.parent;
        }

        return null;
    }
}
