package yapl.impl.codegen;

import yapl.compiler.Token;
import yapl.compiler.YAPLException;
import yapl.impl.typecheck.AttribImpl;
import yapl.interfaces.*;
import yapl.lib.*;

import java.util.List;
import java.util.UUID;

import static yapl.compiler.CA2_3Constants.*;

public class CodeGenImpl implements CodeGen {

    private BackendAsmRM backend;

    public CodeGenImpl(BackendAsmRM backend) {
        this.backend = backend;
    }

    @Override
    public String newLabel() {
        return UUID.randomUUID().toString();        // TODO: 12.06.2018 CHECK IF THIS ACTUALLY WORKS
    }

    @Override
    public void assignLabel(String label) {
        backend.emitLabel(label, "");
    }

    @Override
    public byte loadValue(Attrib attr) throws YAPLException {
        byte register = backend.allocReg();
        //backend.loadConst(attr.);

        attr.setRegister(register);
        attr.setKind(Attrib.RegValue);
        return register;
    }

    @Override
    public byte loadAddress(Attrib attr) throws YAPLException {
        byte register = backend.allocReg();

        backend.loadAddress(register, attr.getOffset(), attr.isGlobal());
        attr.setKind(Attrib.RegAddress);
        attr.setRegister(register);
        return attr.getRegister();
    }

    @Override
    public void freeReg(Attrib attr) {
        if (attr.getKind() != Attrib.RegValue && attr.getKind() != Attrib.RegAddress) {
            return;
        }

        backend.freeReg(attr.getRegister());
        attr.setKind(Attrib.Invalid);
    }

    @Override
    public void allocVariable(Symbol sym) throws YAPLException {
        int addr;
        if (sym.isGlobal()) {
            addr = backend.allocStaticData(4, "");
            sym.setOffset(addr);
        } else {
            addr = backend.allocStack(4, "");
            sym.setOffset(addr);
        }

        if (sym.isReadonly()) {
            byte register = backend.allocReg();
            if (sym.getType() instanceof IntType) {
                IntType t = (IntType) sym.getType();
                backend.loadConst(register, t.getValue());
            }
            if (sym.getType() instanceof BoolType) {
                BoolType t = (BoolType) sym.getType();
                backend.loadConst(register, backend.boolValue(t.getValue()));
            }
            backend.storeWord(register, addr, sym.isGlobal());
            backend.freeReg(register);
        }
    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YAPLException {
        //backend.storeArrayDim(dim, length.);
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

        if (expr.getType() instanceof IntType) {
            IntType t = (IntType) expr.getType();
            if (t.getValue() != null) {
                byte register = backend.allocReg();
                backend.loadConst(register, t.getValue());
                backend.storeWord(register, lvalue.getOffset(), lvalue.isGlobal());
                backend.freeReg(register);
            }
            if (expr.getKind() == Attrib.RegValue) {
                backend.storeWord(expr.getRegister(), lvalue.getOffset(), lvalue.isGlobal());
                freeReg(expr);
            }
        }

        if (expr.getType() instanceof BoolType) {
            BoolType t = (BoolType) expr.getType();
            if (t.getValue() != null) {
                byte register = backend.allocReg();
                backend.loadConst(register, backend.boolValue(t.getValue()));
                backend.storeWord(register, lvalue.getOffset(), lvalue.isGlobal());
                backend.freeReg(register);
            }
            if (expr.getKind() == Attrib.RegValue) {
                backend.storeWord(expr.getRegister(), lvalue.getOffset(), lvalue.isGlobal());
                freeReg(expr);
            }
        }
    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YAPLException {
        if (!(x.getType() instanceof IntType) && op != null) {
            throw new YAPLException(YAPLException.IllegalOp1Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
            x.setRegister(regX);
            IntType t = (IntType) x.getType();
            if (t.getValue() != null) {
                backend.loadConst(regX, t.getValue());
            }
        }

        switch (op.kind) {
            case PLUS:
                break;
            case MINUS:
                backend.neg(x.getRegister(), x.getRegister());
                break;
        }

        x.setKind(Attrib.RegValue);
        x.setConstant(false);
        x.setType(new IntType());
        return x;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
            x.setRegister(regX);
            IntType t = (IntType) x.getType();
            if (t.getValue() != null) {
                backend.loadConst(regX, t.getValue());
            }
            if (x.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regX, x.getOffset(), x.isGlobal());
            }
        }
        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setKind(Attrib.RegValue);
            y.setRegister(regY);
            IntType t = (IntType) y.getType();;
            if (t.getValue() != null) {
                backend.loadConst(regY, t.getValue());
            }
            if (y.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regY, y.getOffset(), y.isGlobal());
            }
        }

        switch (op.kind) {
            case PLUS:
                backend.add(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case MINUS:
                byte reg = backend.allocReg();
                backend.move(reg, y.getRegister());
                backend.neg(reg, reg);
                backend.add(x.getRegister(), x.getRegister(), reg);
                backend.freeReg(reg);
                break;
            case MULT:
                backend.mul(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case DIV:
                backend.div(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case MODULO:
                backend.mod(x.getRegister(), x.getRegister(), y.getRegister());
                break;
        }

        freeReg(y);
        x.setKind(Attrib.RegValue);
        x.setConstant(false);
        x.setType(new IntType());
        return x;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
            x.setRegister(regX);
            IntType t = (IntType) x.getType();
            if (t.getValue() != null) {
                backend.loadConst(regX, t.getValue());
            }
            if (x.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regX, x.getOffset(), x.isGlobal());
            }
        }
        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setKind(Attrib.RegValue);
            y.setRegister(regY);
            IntType t = (IntType) y.getType();;
            if (t.getValue() != null) {
                backend.loadConst(regY, t.getValue());
            }
            if (y.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regY, y.getOffset(), y.isGlobal());
            }
        }

        switch (op.kind) {
            case LESS:
                backend.isLess(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case GREATER_EQUAL:
                backend.isLessOrEqual(x.getRegister(), y.getRegister(), x.getRegister());
                break;
            case LESS_EQUAL:
                backend.isLessOrEqual(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case GREATER:
                backend.isLess(x.getRegister(), y.getRegister(), x.getRegister());
                break;
            default:
                throw new YAPLException(CompilerError.Internal, op, null);
        }


        freeReg(y);
        x.setKind(Attrib.RegValue);
        x.setConstant(false);
        x.setType(new BoolType());
        return x;
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
        //return new AttribImpl(Attrib.Constant, new BoolType(), op);
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)
            && !(x.getType() instanceof BoolType && y.getType() instanceof BoolType)){
            throw new YAPLException(YAPLException.IllegalEqualOpType, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
            x.setRegister(regX);
            Type type = x.getType();
            if (type instanceof IntType) {
                IntType t = (IntType) type;
                if (t.getValue() != null) {
                    backend.loadConst(regX, t.getValue());
                }
                if (x.getKind() == Attrib.MemoryOperand) {
                    backend.loadWord(regX, x.getOffset(), x.isGlobal());
                }
            }
            if (type instanceof BoolType) {
                BoolType t = (BoolType) type;
                if (t.getValue() != null) {
                    backend.loadConst(regX, backend.boolValue(t.getValue()));
                }
                if (x.getKind() == Attrib.MemoryOperand) {
                    backend.loadWord(regX, x.getOffset(), x.isGlobal());
                }
            }
        }
        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setKind(Attrib.RegValue);
            y.setRegister(regY);
            Type type = y.getType();
            if (type instanceof IntType) {
                IntType t = (IntType) type;
                if (t.getValue() != null) {
                    backend.loadConst(regY, t.getValue());
                }
                if (x.getKind() == Attrib.MemoryOperand) {
                    backend.loadWord(regY, y.getOffset(), y.isGlobal());
                }
            }
            if (type instanceof BoolType) {
                BoolType t = (BoolType) type;
                if (t.getValue() != null) {
                    backend.loadConst(regY, backend.boolValue(t.getValue()));
                }
                if (y.getKind() == Attrib.MemoryOperand) {
                    backend.loadWord(regY, y.getOffset(), y.isGlobal());
                }
            }
        }

        switch (op.kind) {
            case EQUAL:
                backend.isEqual(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case NOT_EQUAL:
                backend.isEqual(x.getRegister(), x.getRegister(), y.getRegister());
                backend.not(x.getRegister(), x.getRegister());
                break;
        }

        freeReg(y);
        x.setKind(Attrib.RegValue);
        x.setConstant(false);
        x.setType(new BoolType());
        return x;
        //return new AttribImpl(Attrib.Constant, new BoolType(), op);
    }

    @Override
    public Attrib logicOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof BoolType && y.getType() instanceof BoolType)){
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
            x.setRegister(regX);
            BoolType t = (BoolType) x.getType();
            if (t.getValue() != null) {
                backend.loadConst(regX, backend.boolValue(t.getValue()));
            }
            if (x.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regX, x.getOffset(), x.isGlobal());
            }
        }
        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setKind(Attrib.RegValue);
            y.setRegister(regY);
            BoolType t = (BoolType) y.getType();;
            if (t.getValue() != null) {
                backend.loadConst(regY, backend.boolValue(t.getValue()));
            }
            if (y.getKind() == Attrib.MemoryOperand) {
                backend.loadWord(regY, y.getOffset(), y.isGlobal());
            }
        }

        switch (op.kind) {
            case AND:
                backend.and(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            case OR:
                backend.or(x.getRegister(), x.getRegister(), y.getRegister());
                break;
            default:
                throw new YAPLException(CompilerError.Internal, op, null);
        }

        freeReg(y);
        x.setType(new BoolType());
        x.setConstant(false);
        x.setKind(Attrib.RegValue);
        return x;
        //return new AttribImpl(Attrib.RegValue, new BoolType(), op);
    }

    @Override
    public void enterProc(Symbol proc) throws YAPLException {
        if (!(proc.getType() instanceof ProcedureType)) {
            throw new YAPLException(CompilerError.Internal, null, proc);
        }

        ProcedureType procType = (ProcedureType) proc.getType();
        backend.enterProc(proc.getName(), procType.getParameterList().size());
    }

    @Override
    public void exitProc(Symbol proc) throws YAPLException {
        backend.exitProc(proc.getName() +"_end");
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YAPLException {
        backend.returnFromProc(proc.getName()+"_end", returnVal.getRegister());
    }

    @Override
    public Attrib callProc(Symbol proc, List<Attrib> args, Token tok) throws YAPLException {
        ProcedureType type = (ProcedureType) proc.getType();
        type.checkArgumentListValid(args, tok);

        backend.prepareProcCall(args.size());
        for (int i = 0; i < args.size(); i++) {
            Attrib arg = args.get(i);
            if (arg.isConstant()) {
                byte register = backend.allocReg();

                if (arg.getType() instanceof IntType) {
                    IntType t = (IntType) arg.getType();
                    backend.loadConst(register, t.getValue());
                }
                if (arg.getType() instanceof BoolType) {
                    BoolType t = (BoolType) arg.getType();
                    backend.loadConst(register, backend.boolValue(t.getValue()));
                }
                backend.passArg(i, register);
                backend.freeReg(register);
            } else if (arg.getKind() == Attrib.RegValue) {
                backend.passArg(i, arg.getRegister());
                backend.freeReg(arg.getRegister());
            } else if (arg.getKind() == Attrib.RegAddress || arg.getKind() == Attrib.MemoryOperand) {
                byte register = backend.allocReg();
                backend.loadWord(register, arg.getOffset(), arg.isGlobal());
                backend.passArg(i, register);
                backend.freeReg(register);
            }
        }
        backend.callProc((byte) -1, proc.getName());

        return new AttribImpl(Attrib.Constant, type.getReturnType(), tok);
    }

    @Override
    public void writeString(String string) throws YAPLException {
        string = string.substring(1, string.length() -1);   // remove unnecessary " in beginning and end of string.

        int addr = backend.allocStringConstant(string);
        backend.writeString(addr);
    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YAPLException {
        if (!(condition.getType() instanceof BoolType)) {
            throw new YAPLException(CompilerError.CondNotBool, condition.getToken(), null);
        }
        backend.branchIf(condition.getRegister(), false, label);
    }

    @Override
    public void jump(String label) {
        backend.jump(label);
    }

    @Override
    public void enterMain() {
        backend.enterMain();
    }

    @Override
    public void exitMain() {
        backend.exitMain("main_end");
    }
}
