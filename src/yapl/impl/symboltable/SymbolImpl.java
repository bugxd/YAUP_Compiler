package yapl.impl.symboltable;

import yapl.interfaces.Symbol;
import yapl.interfaces.SymbolKind;
import yapl.lib.Type;

/**
 * Created by Dominic on 28.04.2018.
 */
public class SymbolImpl implements Symbol {

    private SymbolKind symbolKind;
    private String identifier;
    private Type dataType;
    private boolean isGlobal;
    private boolean isReadonly;
    private boolean isReference;
    private int offset;


    public SymbolImpl(SymbolKind symbolKind, String identifier) {
        this.symbolKind = symbolKind;
        this.identifier = identifier;
        this.isReference = false;
        this.isGlobal = false;
        this.isReadonly = false;
        this.dataType = null;
    }

    @Override
    public SymbolKind getKind() {
        return symbolKind;
    }

    @Override
    public String getKindString() {
        return symbolKind.getName();
    }

    @Override
    public void setKind(SymbolKind symbolKind) {
        this.symbolKind = symbolKind;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public Type getType() {
        return dataType;
    }

    @Override
    public void setType(Type type) {
        this.dataType = type;
    }

    @Override
    public boolean isReference() {
        return isReference;
    }

    @Override
    public void setReference(boolean isReference) {
        this.isReference = isReference;
    }

    @Override
    public boolean isReadonly() {
        return isReadonly;
    }

    @Override
    public void setReadonly(boolean isReadonly) {
        this.isReadonly = isReadonly;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public Symbol getNextSymbol() {
        return null;
    }

    @Override
    public void setNextSymbol(Symbol symbol) {

    }

    @Override
    public boolean getReturnSeen() {
        return false;
    }

    @Override
    public void setReturnSeen(boolean seen) {

    }
}
