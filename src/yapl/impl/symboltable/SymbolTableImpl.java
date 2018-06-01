package yapl.impl.symboltable;

import yapl.interfaces.Symbol;
import yapl.interfaces.SymbolKind;
import yapl.interfaces.Symboltable;
import yapl.lib.BoolType;
import yapl.lib.IntType;
import yapl.lib.ProcedureType;
import yapl.lib.VoidType;

import java.util.ArrayList;
import java.util.List;
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

        // writeint
        Symbol writeint = new SymbolImpl(SymbolKind.PROCEDURE, "writeint");
        Symbol writeIntParam = new SymbolImpl(SymbolKind.PARAMETER, "x");
        writeIntParam.setType(new IntType());
        List<Symbol> paramList = new ArrayList<>();
        paramList.add(writeIntParam);

        writeint.setType(new ProcedureType(paramList, new VoidType(), "writeint"));
        addSymbol(writeint);

        // writebool
        Symbol writebool = new SymbolImpl(SymbolKind.PROCEDURE, "writebool");
        Symbol writeboolParam = new SymbolImpl(SymbolKind.PARAMETER, "x");
        writeboolParam.setType(new BoolType());
        List<Symbol> writeboolParamList = new ArrayList<>();
        writeboolParamList.add(writeboolParam);

        writebool.setType(new ProcedureType(writeboolParamList, new VoidType(), "writebool"));
        addSymbol(writebool);

        // writeln
        Symbol writeln = new SymbolImpl(SymbolKind.PROCEDURE, "writeln");
        List<Symbol> writelnParamList = new ArrayList<>();

        writeln.setType(new ProcedureType(writelnParamList, new VoidType(), "writeln"));
        addSymbol(writeln);

        // readint
        Symbol readint = new SymbolImpl(SymbolKind.PROCEDURE, "readint");
        List<Symbol> readintParamList = new ArrayList<>();

        readint.setType(new ProcedureType(readintParamList, new IntType(), "readint"));
        addSymbol(readint);
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
