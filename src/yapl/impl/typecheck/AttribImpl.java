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

    public AttribImpl(byte kind, Type type, Token token) {
        this.kind = kind;
        this.type = type;
        this.token = token;
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
        return false;
    }

    @Override
    public void setConstant(boolean isConstant) {

    }

    @Override
    public boolean isReadonly() {
        return type.isReadOnly();
    }

    @Override
    public void setReadonly(boolean isReadonly) {
        //this.readonly = isReadonly;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public void setGlobal(boolean isGlobal) {

    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void setOffset(int offset) {

    }

    @Override
    public byte getRegister() {
        return 0;
    }

    @Override
    public void setRegister(byte register) {

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
}
