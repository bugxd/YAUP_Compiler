package yapl.impl.symboltable;

import yapl.interfaces.Symbol;
import yapl.interfaces.SymbolKind;
import yapl.interfaces.Symboltable;

import java.util.Optional;
import java.util.Stack;

public class SymbolTableImpl implements Symboltable {

    private Stack<Scope> symbolTable;
    private boolean enableDebugOutput;

    public SymbolTableImpl() {
        symbolTable = new Stack<>();
        enableDebugOutput = false;

        // Open Scope of Predefined Procedures
        openScope(true);
        addSymbol(
                new SymbolImpl(SymbolKind.PROCEDURE, "writeint")
        );
        addSymbol(
                new SymbolImpl(SymbolKind.PROCEDURE, "writebool")
        );
        addSymbol(
                new SymbolImpl(SymbolKind.PROCEDURE, "writeln")
        );
        addSymbol(
                new SymbolImpl(SymbolKind.PROCEDURE, "readint")
        );
    }

    @Override
    public void openScope(boolean isGlobal) {
        Scope parentScope;
        if (symbolTable.isEmpty()) {
            parentScope = null;
        } else {
            parentScope = symbolTable.peek();
        }

        symbolTable.push(new Scope(true, parentScope));
    }

    @Override
    public void closeScope() {
        symbolTable.pop();
    }

    @Override
    public void addSymbol(Symbol s) {//throws YAPLException {
        /*if (s == null || s.getName() == null) {
            throw new YAPLException("NAME IS NULL", 0, 0, 0);
        }*/

        Scope currentScope = symbolTable.peek();
        /*if (currentScope.containsSymbol(s.getName())) {
            throw new YAPLException("SYMBOL ALREADY EXISTS!!", 0,0,0);
        }*/

        s.setGlobal(currentScope.isGlobal());
        currentScope.put(s);
    }

    @Override
    public Symbol lookup(String name) {//throws YAPLException {
        /*if (name == null) {
            throw new YAPLException("NAME IS NULL", 0, 0, 0);
        }*/
        if (symbolTable.isEmpty()) {
            return null;
        }

        Scope currentScope = symbolTable.peek();
        while (currentScope != null) {
            Optional<Symbol> symbolOptional = currentScope.getSymbol(name);
            if (symbolOptional.isPresent()) {
                return symbolOptional.get();
            }
            currentScope = currentScope.getParentScope();
        }
        return null;
    }

    @Override
    public void setParentSymbol(Symbol sym) {
        symbolTable.peek().setParentSymbol(sym);
    }

    @Override
    public Symbol getNearestParentSymbol(SymbolKind kind) {
        Scope currentScope = symbolTable.peek();
        while (currentScope != null) {
            Symbol parentSymbol = currentScope.getParentSymbol();
            if (parentSymbol != null && parentSymbol.getKind().equals(kind)) {
                return parentSymbol;
            }
            currentScope = currentScope.getParentScope();
        }
        return null;
    }

    @Override
    public void setDebug(boolean on) {
        enableDebugOutput = on;
    }

    @Override
    public boolean containsIdentifierInCurrentScope(String ident) {
        Scope currentScope = symbolTable.peek();
        return currentScope.containsSymbol(ident);
    }
}
