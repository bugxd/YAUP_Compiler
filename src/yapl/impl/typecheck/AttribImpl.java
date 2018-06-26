package yapl.impl.typecheck;

import yapl.compiler.Token;
import yapl.compiler.YAPLException;
import yapl.interfaces.Attrib;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.interfaces.SymbolKind;
import yapl.lib.Type;

public class AttribImpl implements Attrib {

    private byte kind;
    private Type type;
    private Token token;
    private byte register = -1;
    private int offset = 0;
    private boolean isGlobal;
    private boolean isConstant;
    private boolean isReadOnly;
    private Symbol symbol = null;

    public AttribImpl(byte kind, Type type, Token token) {
        this.kind = kind;
        this.type = type;
        this.token = token;
        this.isGlobal = false;
        this.isConstant = false;
        this.isReadOnly = false;
    }



    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public byte getKind() {
        return kind;
    }

    @Override
    public void setKind(byte kind) {
        this.kind = kind;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    @Override
    public boolean isReadonly() {
        return isReadOnly;
    }

    @Override
    public void setReadonly(boolean isReadonly) {
        this.isReadOnly = isReadonly;
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
    public byte getRegister() {
        return register;
    }

    @Override
    public void setRegister(byte register) {
        this.register = register;
    }

    public void checkReturnTypeCompatibleWith(Type other, Symbol parent) throws YAPLException {
        if (!type.isCompatibleWith(other)) {
            if (parent.getKind().equals(SymbolKind.PROCEDURE)) {
                throw new YAPLException(CompilerError.InvalidReturnType, token, parent);
            }
            //if (parent.getKind().equals(SymbolKind.PROGRAM)) {
            //    throw new YAPLException(CompilerError.IllegalRetValMain, token, parent);
            //}
        }
    }

    public void setSymbol(Symbol sym) {
        this.symbol = sym;
        setGlobal(sym.isGlobal());
        setReadonly(sym.isReadonly());
        setOffset(sym.getOffset());
    }

    public Symbol getSymbol() {
        return this.symbol;
    }
}
