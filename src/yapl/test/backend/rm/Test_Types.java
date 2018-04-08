package backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test_Types {
    private static BackendAsmRM backend;

    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        backend.enterMain();

        /* R-Types */
        byte R1 = backend.allocReg();
        byte R2 = backend.allocReg();
        backend.addConst(R2, backend.zeroReg(), 5);
        backend.neg(R1,R2);

        byte R3 = backend.allocReg();
        backend.addConst(R1, backend.zeroReg(), 10);
        backend.div(R3, R1, R2);

        backend.addConst(R1, backend.zeroReg(), 10);
        backend.mod(R3, R1, R2);
        backend.not(R3, R1);
        backend.and(R3, R1, R2);
        backend.or(R3, R1, R2);

        /* I-Types */
        backend.isLess(R3, R1, R2);
        backend.isEqual(R3, R1, R2);
        backend.isLessOrEqual(R3, R1, R2);
        backend.exitMain("exit");

        /* Method-Call*/
        backend.arrayLength(R3, R2);
    }
}
