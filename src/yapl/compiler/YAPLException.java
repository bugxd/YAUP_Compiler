package yapl.compiler;

import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.interfaces.SymbolKind;

/**
 * Created by Dominic on 28.04.2018.
 */
public class YAPLException extends Exception implements CompilerError {

    private int errorNumber;
    private Token currentToken;
    private Symbol symbol;

    public YAPLException(int errorNumber, Token token, Symbol symbol) {
        this.errorNumber = errorNumber;
        this.currentToken = token;
        this.symbol = symbol;
    }

    @Override
    public int errorNumber() {
        return errorNumber;
    }

    @Override
    public String getMessage() {
        switch (errorNumber) {
            case CompilerError.SymbolExists:
                return "symbol '" + currentToken.image + "' already declared in current scope (as '" + symbol.getKind() + "')";
            case CompilerError.IdentNotDecl:
                return "identifier '" +currentToken.image + "' not declared";
            case CompilerError.SymbolIllegalUse:
                return "illegal use of " + symbol.getKind() + "' " + symbol.getName() +"'";
            case CompilerError.EndIdentMismatch: {
                if (symbol.getKind().equals(SymbolKind.PROCEDURE)) {
                    return "End '"+ currentToken.image +"' does not match Procedure '" + symbol.getName() + "'";
                }
                else if (symbol.getKind().equals(SymbolKind.PROGRAM)) {
                    return "End '"+ currentToken.image +"' does not match Program '" + symbol.getName() + "'";
                }
            }
        }
        return "";
    }

    public int line() {
        return currentToken.beginLine;
    }

    public int column() {
        return currentToken.beginColumn;
    }
}
