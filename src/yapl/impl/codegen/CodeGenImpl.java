package yapl.impl.codegen;


import yapl.compiler.Token;
import yapl.compiler.YAPLException;
import yapl.impl.typecheck.AttribImpl;
import yapl.interfaces.*;
import yapl.lib.*;

import java.util.ArrayList;
import java.util.List;

import static yapl.compiler.CA2_3Constants.*;

public class CodeGenImpl implements CodeGen {

    private BackendAsmRM backend;
    private int labelcount = 0;

    private List<Symbol> globalSymbolBuffer = new ArrayList<>();

    public CodeGenImpl(BackendAsmRM backend) {
        this.backend = backend;
    }

    @Override
    public String newLabel() {
        String label = "newLabel_" + labelcount;
        labelcount++;
        return label;
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
        if (attr.getRegister() == -1) {
            return;
        }

        backend.freeReg(attr.getRegister());
        attr.setKind(Attrib.Invalid);
    }

    @Override
    public void allocVariable(Symbol sym) throws YAPLException {
        int addr;
        if (sym.isGlobal()) {
            globalSymbolBuffer.add(sym);
            addr = backend.allocStaticData(4, sym.getName());
            sym.setOffset(addr);
        } else {
            addr = backend.allocStack(4, "");
            sym.setOffset(addr);

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
    }

    private void writeGlobalSymbols() {
        for (Symbol sym : globalSymbolBuffer) {
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
                backend.storeWord(register, sym.getOffset(), sym.isGlobal());
                backend.freeReg(register);
            }
        }
    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YAPLException {
        IntType t = (IntType) length.getType();
        if (length.getKind() == Attrib.MemoryOperand) {
            byte reg = backend.allocReg();
            backend.loadWord(reg, length.getOffset(), length.isGlobal());
            backend.storeArrayDim(dim, reg);
            backend.freeReg(reg);
        }
        if (length.getKind() == Attrib.RegValue) {
            backend.storeArrayDim(dim, length.getRegister());
        }
        if (length.getKind() == Attrib.Constant) {
            byte reg = backend.allocReg();
            backend.loadConst(reg, t.getValue());
            backend.storeArrayDim(dim, reg);
            backend.freeReg(reg);
        }
    }

    @Override
    public Attrib allocArray(ArrayType arrayType, Token token) throws YAPLException {
        byte baseAddrReg = backend.allocReg();
        backend.allocArray(baseAddrReg);

        AttribImpl attrib = new AttribImpl(Attrib.RegAddress, arrayType, token);
        attrib.setRegister(baseAddrReg);
        return attrib;
    }

    @Override
    public Attrib allocRecord(RecordType recordType, Token token) throws YAPLException {
        return new AttribImpl(Attrib.Constant, recordType, token);
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {
        sym.setOffset(sym.getOffset() + pos * 4);
    }

    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YAPLException {
        ArrayType type = (ArrayType) arr.getType();

        arr.setKind(Attrib.ArrayElement);
        arr.setType(type.subarray());

        byte offsetReg = backend.allocReg();
        byte baseAddrReg = backend.allocReg();
        backend.loadWord(baseAddrReg, arr.getOffset(), arr.isGlobal());


        byte indexReg = backend.allocReg();
        if (index.getKind() == Attrib.Constant) {
            IntType t = (IntType) index.getType();
            backend.loadConst(indexReg, t.getValue());
        }
        else if (index.getKind() == Attrib.RegValue) {
            indexReg = index.getRegister();
        }

        backend.arrayOffset(offsetReg, baseAddrReg, indexReg);
        backend.freeReg(indexReg);
        backend.freeReg(baseAddrReg);
        index.setKind(Attrib.Invalid);
        arr.setRegister(offsetReg);
    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YAPLException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YAPLException {
        if (!(arr.getType() instanceof ArrayType)) {
            throw new YAPLException(CompilerError.ArrayLenNotArray, arr.getToken(), null);
        }

        byte reg = backend.allocReg();
        byte baseAddr = backend.allocReg();
        backend.loadWord(baseAddr, arr.getOffset(), arr.isGlobal());
        backend.arrayLength(reg, baseAddr);
        backend.freeReg(baseAddr);

        arr.setRegister(reg);
        arr.setType(new IntType());
        arr.setKind(Attrib.RegValue);
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

        if (lvalue.getKind() == Attrib.ArrayElement) {
            if (expr.getKind() == Attrib.Constant) {
                if (expr.getType() instanceof IntType) {
                    IntType t = (IntType) expr.getType();
                    if (t.getValue() != null) {
                        byte register = backend.allocReg();
                        backend.loadConst(register, t.getValue());
                        backend.storeWordReg(register, lvalue.getRegister());
                        backend.freeReg(register);
                    }
                }

                if (expr.getType() instanceof BoolType) {
                    BoolType t = (BoolType) expr.getType();
                    if (t.getValue() != null) {
                        byte register = backend.allocReg();
                        backend.loadConst(register, backend.boolValue(t.getValue()));
                        backend.storeWordReg(register, lvalue.getRegister());
                        backend.freeReg(register);
                    }
                }
                freeReg(lvalue);
                return;
            }

            if (expr.getKind() == Attrib.MemoryOperand) {
                byte register = backend.allocReg();
                backend.loadWord(register, expr.getOffset(), expr.isGlobal());
                backend.storeWordReg(register, lvalue.getRegister());
                backend.freeReg(register);
                freeReg(lvalue);
                return;
            }

            if (expr.getKind() == Attrib.ArrayElement) {
                byte register = backend.allocReg();
                backend.loadWordReg(register, expr.getRegister());
                backend.storeWordReg(register, lvalue.getRegister());
                backend.freeReg(register);
                freeReg(lvalue);
                freeReg(expr);
                return;
            }

            if (expr.getKind() == Attrib.RegValue) {
                backend.storeWordReg(expr.getRegister(), lvalue.getRegister());
                freeReg(expr);
                freeReg(lvalue);
                return;
            }

            if (expr.getKind() == Attrib.RegAddress) {
                backend.storeWordReg(expr.getRegister(), lvalue.getRegister());
                freeReg(expr);
                freeReg(lvalue);
                return;
            }
            System.out.println("could not handle attrib of kind " + expr.getKind() + " in assign method!");
        } else {
            if (expr.getKind() == Attrib.Constant) {
                if (expr.getType() instanceof IntType) {
                    IntType t = (IntType) expr.getType();
                    if (t.getValue() != null) {
                        byte register = backend.allocReg();
                        backend.loadConst(register, t.getValue());
                        backend.storeWord(register, lvalue.getOffset(), lvalue.isGlobal());
                        backend.freeReg(register);
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
                }
                return;
            }

            if (expr.getKind() == Attrib.MemoryOperand) {
                byte register = backend.allocReg();
                backend.loadWord(register, expr.getOffset(), expr.isGlobal());
                backend.storeWord(register, lvalue.getOffset(), lvalue.isGlobal());
                backend.freeReg(register);
                freeReg(lvalue);
                return;
            }

            if (expr.getKind() == Attrib.RegValue) {
                backend.storeWord(expr.getRegister(), lvalue.getOffset(), lvalue.isGlobal());
                freeReg(expr);
                freeReg(lvalue);
                return;
            }

            if (expr.getKind() == Attrib.ArrayElement) {
                byte register = backend.allocReg();
                backend.loadWordReg(register, expr.getRegister());
                backend.storeWord(register, lvalue.getOffset(), lvalue.isGlobal());
                backend.freeReg(register);
                freeReg(expr);
                freeReg(lvalue);
            }

            if (expr.getKind() == Attrib.RegAddress) {
                backend.storeWord(expr.getRegister(), lvalue.getOffset(), lvalue.isGlobal());
                freeReg(expr);
                freeReg(lvalue);
                return;
            }
        }

        System.out.println("Could not handle expr of kind '" + expr.getKind() + "' in assign method");
    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YAPLException {
        if (!(x.getType() instanceof IntType) && op != null) {
            throw new YAPLException(YAPLException.IllegalOp1Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
        }
        if (x.getKind() == Attrib.Constant) {
            IntType t = (IntType) x.getType();
            backend.loadConst(regX, t.getValue());
        } else if (x.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regX, x.getOffset(), x.isGlobal());
        } else if (x.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegValue) {
            backend.move(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regX, x.getRegister());
        }

        switch (op.kind) {
            case PLUS:
                break;
            case MINUS:
                backend.neg(regX, regX);
                break;
        }

        Attrib returnAttrib = new AttribImpl(Attrib.RegValue, new IntType(), null);
        returnAttrib.setRegister(regX);
        return returnAttrib;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
        }
        if (x.getKind() == Attrib.Constant) {
            IntType t = (IntType) x.getType();
            backend.loadConst(regX, t.getValue());
        } else if (x.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regX, x.getOffset(), x.isGlobal());
        } else if (x.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegValue) {
            backend.move(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regX, x.getRegister());
        }

        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setRegister(regY);
        }
        if (y.getKind() == Attrib.Constant) {
            IntType t = (IntType) y.getType();
            backend.loadConst(regY, t.getValue());
        } else if (y.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regY, y.getOffset(), y.isGlobal());
        } else if (y.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegValue) {
            backend.move(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regY, y.getRegister());
        }

        switch (op.kind) {
            case PLUS:
                backend.add(regX, regX, regY);
                break;
            case MINUS:
                byte reg = backend.allocReg();
                backend.move(reg, regY);
                backend.neg(reg, reg);
                backend.add(regX, regX, reg);
                backend.freeReg(reg);
                break;
            case MULT:
                backend.mul(regX, regX, regY);
                break;
            case DIV:
                backend.div(regX, regX, regY);
                break;
            case MODULO:
                backend.mod(regX, regX, regY);
                break;
        }

        freeReg(y);
        Attrib returnAttrib = new AttribImpl(Attrib.RegValue, new IntType(), null);
        returnAttrib.setRegister(regX);
        return returnAttrib;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
        }
        if (x.getKind() == Attrib.Constant) {
            IntType t = (IntType) x.getType();
            backend.loadConst(regX, t.getValue());
        } else if (x.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regX, x.getOffset(), x.isGlobal());
        } else if (x.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegValue) {
            backend.move(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regX, x.getRegister());
        }

        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setRegister(regY);
        }
        if (y.getKind() == Attrib.Constant) {
            IntType t = (IntType) y.getType();
            backend.loadConst(regY, t.getValue());
        } else if (y.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regY, y.getOffset(), y.isGlobal());
        } else if (y.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegValue) {
            backend.move(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regY, y.getRegister());
        }

        switch (op.kind) {
            case LESS:
                backend.isLess(regX, regX, regY);
                break;
            case GREATER_EQUAL:
                backend.isLessOrEqual(regX, regY, regX);
                break;
            case LESS_EQUAL:
                backend.isLessOrEqual(regX, regX, regY);
                break;
            case GREATER:
                backend.isLess(regX, regY, regX);
                break;
            default:
                throw new YAPLException(CompilerError.Internal, op, null);
        }

        freeReg(y);
        Attrib returnAttrib = new AttribImpl(Attrib.RegValue, new BoolType(), null);
        returnAttrib.setRegister(regX);
        return returnAttrib;
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
        }
        if (x.getKind() == Attrib.Constant) {
            if (x.getType() instanceof IntType) {
                IntType t = (IntType) x.getType();
                backend.loadConst(regX, t.getValue());
            } else if (x.getType() instanceof BoolType) {
                BoolType t = (BoolType) x.getType();
                backend.loadConst(regX, backend.boolValue(t.getValue()));
            }
        } else if (x.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regX, x.getOffset(), x.isGlobal());
        } else if (x.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegValue) {
            backend.move(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regX, x.getRegister());
        }

        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setRegister(regY);
        }
        if (y.getKind() == Attrib.Constant) {
            if (y.getType() instanceof IntType) {
                IntType t = (IntType) y.getType();
                backend.loadConst(regY, t.getValue());
            } else if (y.getType() instanceof BoolType) {
                BoolType t = (BoolType) y.getType();
                backend.loadConst(regY, backend.boolValue(t.getValue()));
            }
        } else if (y.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regY, y.getOffset(), y.isGlobal());
        } else if (y.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegValue) {
            backend.move(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regY, y.getRegister());
        }

        switch (op.kind) {
            case EQUAL:
                backend.isEqual(regX, regX, regY);
                break;
            case NOT_EQUAL:
                backend.isEqual(regX, regX, regY);
                backend.not(regX, regX);
                break;
        }

        freeReg(y);
        Attrib returnAttrib = new AttribImpl(Attrib.RegValue, new BoolType(), null);
        returnAttrib.setRegister(regX);
        return returnAttrib;
    }

    @Override
    public Attrib logicOp(Attrib x, Token op, Attrib y) throws YAPLException {
        if (!(x.getType() instanceof BoolType && y.getType() instanceof BoolType)){
            throw new YAPLException(YAPLException.IllegalOp2Type, op, null);
        }

        byte regX = x.getRegister();
        if (regX == -1) {
            regX = backend.allocReg();
        }
        if (x.getKind() == Attrib.Constant) {
            BoolType t = (BoolType) x.getType();
            backend.loadConst(regX, backend.boolValue(t.getValue()));
        } else if (x.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regX, x.getOffset(), x.isGlobal());
        } else if (x.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegValue) {
            backend.move(regX, x.getRegister());
        } else if (x.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regX, x.getRegister());
        }

        byte regY = y.getRegister();
        if (regY == -1) {
            regY = backend.allocReg();
            y.setRegister(regY);
        }
        if (y.getKind() == Attrib.Constant) {
            BoolType t = (BoolType) y.getType();
            backend.loadConst(regY, backend.boolValue(t.getValue()));
        } else if (y.getKind() == Attrib.MemoryOperand) {
            backend.loadWord(regY, y.getOffset(), y.isGlobal());
        } else if (y.getKind() == Attrib.ArrayElement) {
            backend.loadWordReg(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegValue) {
            backend.move(regY, y.getRegister());
        } else if (y.getKind() == Attrib.RegAddress) {
            backend.loadWordReg(regY, y.getRegister());
        }

        switch (op.kind) {
            case AND:
                backend.and(regX, regX, regY);
                break;
            case OR:
                backend.or(regX, regX, regY);
                break;
            default:
                throw new YAPLException(CompilerError.Internal, op, null);
        }

        freeReg(y);
        Attrib returnAttrib = new AttribImpl(Attrib.RegValue, new BoolType(), null);
        returnAttrib.setRegister(regX);
        return returnAttrib;
    }

    @Override
    public void enterProc(Symbol proc) throws YAPLException {
        if (!(proc.getType() instanceof ProcedureType)) {
            throw new YAPLException(CompilerError.Internal, null, proc);
        }

        ProcedureType procType = (ProcedureType) proc.getType();
        backend.enterProc(proc.getName(), procType.getParameterList().size());

        int i = 0;
        for (Symbol sym : procType.getParameterList()) {
            sym.setGlobal(false);
            sym.setOffset(backend.paramOffset(i));
            i++;
        }
    }

    @Override
    public void exitProc(Symbol proc) throws YAPLException {
        backend.exitProc(proc.getName() +"_end");
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YAPLException {
        if (returnVal.getRegister() == -1) {
            returnVal.setRegister(backend.allocReg());
        }

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
            } else if (arg.getKind() == Attrib.ArrayElement) {
                byte register = backend.allocReg();
                backend.loadWordReg(register, arg.getRegister());
                backend.passArg(i, register);
                backend.freeReg(register);
                freeReg(arg);
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
        writeGlobalSymbols();
    }

    @Override
    public void exitMain() {
        backend.exitMain("main_end");
    }
}
