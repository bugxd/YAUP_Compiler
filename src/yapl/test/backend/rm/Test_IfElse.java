package backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test_IfElse {
    private static BackendAsmRM backend;

    public static void main(String[] args) throws Exception {
        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        backend.enterMain();

        Byte R1 = 0;
        while(R1 != -1){
            R1 = backend.allocReg();
        }

        backend.storeArrayDim(5, backend.zeroReg());

        backend.loadAddress(backend.zeroReg(), 5, false);

    }
}
