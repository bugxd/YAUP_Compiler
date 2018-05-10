package yapl.impl.symboltable;

import yapl.interfaces.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Scope {

    private Map<String, Symbol> symbolTable;
    private boolean isGlobal;
    private Scope parentScope;
    private Symbol parentSymbol;

    public Scope(boolean isGlobal, Scope parentScope) {
        this.symbolTable = new HashMap<>();
        this.isGlobal = isGlobal;
        this.parentScope = parentScope;
        this.parentSymbol = null;
    }

    public Symbol put(Symbol symbol) {
        return symbolTable.put(symbol.getName(), symbol);
    }

    public boolean hasParent() {
        if (parentScope == null) {
            return false;
        }
        return true;
    }

    public Scope getParentScope() {
        return parentScope;
    }

    public boolean containsSymbol(String name) {
        return symbolTable.containsKey(name);
    }

    public Optional<Symbol> getSymbol(String name) {
        if (containsSymbol(name)) {
            return of(symbolTable.get(name));
        }
        return empty();
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setParentSymbol(Symbol parentSymbol) {
        this.parentSymbol = parentSymbol;
    }

    public Symbol getParentSymbol() {
        return parentSymbol;
    }
}
