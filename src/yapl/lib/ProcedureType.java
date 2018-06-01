package yapl.lib;

import yapl.compiler.Token;
import yapl.compiler.YAPLException;
import yapl.interfaces.Attrib;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;

import java.util.List;

public class ProcedureType implements Type{

    private List<Symbol> parameterList;
    private Type returnType;
    private String procedureName;

    public ProcedureType(List<Symbol> parameterList, Type returnType, String procedureName) {
        this.parameterList = parameterList;
        this.returnType = returnType;
        this.procedureName = procedureName;
    }

    public void checkArgumentListValid(List<Attrib> argumentList, Token tok) throws YAPLException {
        if (argumentList.size() < this.parameterList.size()) {
            throw new YAPLException(CompilerError.TooFewArgs, tok, null);
        }

        for (int i = 0; i < argumentList.size(); i++) {
            Attrib otherCurrent = argumentList.get(i);

            if (i >= parameterList.size()) {
                throw new YAPLException(CompilerError.ArgNotApplicable, otherCurrent.getToken(), null);
            }
            Symbol thisCurrent = parameterList.get(i);
            if (!otherCurrent.getType().isCompatibleWith(thisCurrent.getType())) {
                throw new YAPLException(CompilerError.ArgNotApplicable, otherCurrent.getToken(), null);
            }
        }
    }

    public List<Symbol> getParameterList() {
        return parameterList;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setParameterList(List<Symbol> parameterList) {
        this.parameterList = parameterList;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    @Override
    public boolean isCompatibleWith(Type type) {
        return false;
    }

    @Override
    public boolean equals(Type type) {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
