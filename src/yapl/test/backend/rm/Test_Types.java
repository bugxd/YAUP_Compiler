package yapl.test.backend.rm;

import yapl.impl.backend.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test_Types {
    private static BackendAsmRM backend;

    /**
     * Emit single-argument procedure call code.
     * @param name   procedure name.
     * @param reg    argument register, will be deallocated.
     */
    private static void callProc(String name, byte reg)
    {
        backend.prepareProcCall(1);
        backend.passArg(0, reg);
        //backend.freeReg(reg);
        backend.callProc((byte) -1, name);
    }

    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        backend.enterMain();

        String separator = " : ";
        int addrSeparator = backend.allocStringConstant(separator);

        /* R-Types */
        byte R1 = backend.allocReg();
        byte R2 = backend.allocReg();
        backend.addConst(R2, backend.zeroReg(), 5);
        callProc("writeint", R2);
        backend.writeString(addrSeparator);
        backend.neg(R1,R2);
        callProc("writeint", R1);
        backend.writeString(addrSeparator);

        byte R3 = backend.allocReg();
        backend.addConst(R1, backend.zeroReg(), 10);
        backend.div(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);

        backend.addConst(R1, backend.zeroReg(), 10);
        backend.mod(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);
        backend.not(R3, R1);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);
        backend.and(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);
        backend.or(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);

        /* I-Types */
        backend.isLess(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);
        backend.isEqual(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);
        backend.isLessOrEqual(R3, R1, R2);
        callProc("writeint", R3);
        backend.writeString(addrSeparator);

        byte R4 = backend.allocReg();
        backend.loadConst(R4, 4);
        backend.storeArrayDim(0, R4);
        byte R5 = backend.allocReg();
        backend.allocArray(R5);
        backend.arrayLength(R4, R5);

        callProc("writeint", R4);

        backend.exitMain("exit");
    }
}
