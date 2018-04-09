package backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test_If_Exception_GlobalPointerReg {

    public static void main(String[] args) throws Exception {

        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        BackendAsmRM backend = new BackendMIPS(out);
        backend.enterMain();

        /* To test the second if in GlobalPointerRegister */
        String longWord = "FirstWord";
        for(int i = 0; i < 4300; i++){
            longWord = longWord + "NextWord";
        }
        int addr = backend.allocStringConstant(longWord);
        backend.writeString(addr);

        /* To test Exception */
        String toLong = "LongWord";
        for(int i = 0; i < 4300; i++){
            toLong = toLong + "LongWord";
        }
        int addr2 = backend.allocStringConstant(toLong);

        backend.exitMain("main_end");
    }
}
