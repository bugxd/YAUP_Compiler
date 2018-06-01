package yapl.impl.codegen;

import yapl.compiler.Token;
import yapl.compiler.YAPLException;
import yapl.impl.typecheck.AttribImpl;
import yapl.interfaces.Attrib;
import yapl.interfaces.CodeGen;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.lib.*;

import java.util.List;

public class CodeGenImpl implements CodeGen {
    @Override
    public String newLabel() {
        return null;
    }

    @Override
    public void assignLabel(String label) {

    }

    @Override
    public byte loadValue(Attrib attr) throws YAPLException {
        return 0;
    }

    @Override
    public byte loadAddress(Attrib attr) throws YAPLException {
        return 0;
    }

    @Override
    public void freeReg(Attrib attr) {

    }

    @Override
    public void allocVariable(Symbol sym) throws YAPLException {

    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YAPLException {

    }

    @Override
    public Attrib allocArray(ArrayType arrayType, Token token) throws YAPLException {
        return new AttribImpl(Attrib.Constant, arrayType, token);
    }

    @Override
    public Attrib allocRecord(RecordType recordType, Token token) throws YAPLException {
        return new AttribImpl(Attrib.Constant, recordType, token);
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {

    }

    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YAPLException {

    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YAPLException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YAPLException {
        if (!(arr.getType() instanceof ArrayType)) {
            throw new YAPLException(CompilerError.ArrayLenNotArray, arr.getToken(), null);
        }
        ArrayType arrtype = (ArrayType) arr.getType();

        //byte reg = loadReg(arr);   // arr represents an address operand, so load its value!
        //backend.arrayLength(reg, reg);
        arr.setType(new IntType());
        return arr;
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YAPLException {
        if (expr.getType() instanceof VoidType) {
            throw new YAPLException(CompilerError.ProcNotFuncExpr, expr.getToken(), null);
        }

        if (lvalue.isReadonly()) {
            throw new YAPLException(CompilerError.ReadonlyAssign, lvalue.getToken(), null);
        }

        if (!lvalue.getType().isCompatibleWith(expr.getType())) {
            throw new YAPLException(CompilerError.TypeMismatchAssign, lvalue.getToken(), null);
        }


    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YAPLException {
        if (!(x.getType() instanceof IntType) && op != null) {
            throw new YAPLException(YAPLException.IllegalOp1Type, op, null);
        }

        return x;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        return x;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op, null);
        }
        /*byte xReg = loadReg(x);
        byte yReg = loadReg(y);
        switch(op.getKind()) {
            case LESS:
                backend.isLess(xReg, xReg, yReg);
                break;
            default:
                throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }
        x.setType(new BoolType());
        x.setConstant(x.isConstant() && y.isConstant());
        freeReg(y);*/
        return new AttribImpl(Attrib.Constant, new BoolType(), op);
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)
            && !(x.getType() instanceof BoolType && y.getType() instanceof BoolType)){
            throw new YAPLException(YAPLException.IllegalEqualOpType, op, null);
        }

        return new AttribImpl(Attrib.Constant, new BoolType(), op);
    }

    @Override
    public Attrib logicOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof BoolType && y.getType() instanceof BoolType)){
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        return new AttribImpl(Attrib.Constant, new BoolType(), op);
    }

    @Override
    public void enterProc(Symbol proc) throws YAPLException {

    }

    @Override
    public void exitProc(Symbol proc) throws YAPLException {

    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YAPLException {

    }

    @Override
    public Attrib callProc(Symbol proc, List<Attrib> args, Token tok) throws YAPLException {
        ProcedureType type = (ProcedureType) proc.getType();
        type.checkArgumentListValid(args, tok);
        return new AttribImpl(Attrib.Constant, type.getReturnType(), tok);
    }

    @Override
    public void writeString(String string) throws YAPLException {

    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YAPLException {
        if (!(condition.getType() instanceof BoolType)) {
            throw new YAPLException(CompilerError.CondNotBool, condition.getToken(), null);
        }
    }

    @Override
    public void jump(String label) {

    }
}
