package org.automatas.engine;

import java.util.HashMap;

public class ScopeController {
    private static class Scope {
        public final Scope parent;
        public final HashMap<String, Scalar> symbols;

        public Scope(Scope parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
        }
    }

    private Scope current;

    public boolean hasActiveScope() {
        return current != null;
    }

    public void beginBlock() {
        var parent = current;
        current = new Scope(parent);
    }

    public void endBlock() {
        current = current.parent;
    }

    public boolean hasSymbol(String name) {
        return findScopeForSymbol(name) != null;
    }

    public boolean hasLocalSymbol(String name) {
        return current != null && current.symbols.containsKey(name);
    }

    public void addSymbol(String name, Scalar value) {
        if (current != null) {
            current.symbols.put(name, value);
        }
    }

    public Scalar getSymbol(String name) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            return scope.symbols.get(name);
        }

        return null;
    }

    public void setSymbol(String name, Scalar value) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            scope.symbols.put(name, value);
        }
    }

    public void removeSymbol(String name) {
        Scope scope = findScopeForSymbol(name);
        if (scope != null) {
            scope.symbols.remove(name);
        }
    }

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
