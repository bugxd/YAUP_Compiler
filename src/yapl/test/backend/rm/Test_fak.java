package backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
    static int fak(int n)
    {
        if (n>1)
            return n*fak(n-1);
        else
            return 1;
    }
 */

public class Test_fak {
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
        backend.freeReg(reg);
        backend.callProc((byte) -1, name);
    }

    /**
     * Emit single-argument function call code.
     * @param name    function name.
     * @param reg     argument register.
     * @return        register holding the function result;
     *                is same register as <code>reg</code> here.
     */
    private static byte callFunc(String name, byte reg)
    {
        backend.prepareProcCall(1);
        backend.passArg(0, reg);
        backend.callProc(reg, name);
        return reg;
    }

    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        backend.enterMain();

        byte nReg = backend.allocReg();                           //reg holding n
        backend.addConst(nReg,backend.zeroReg(),5);         //n = 5
        nReg = callFunc("fak",nReg);                        //call fak
        callProc("writeint", nReg);
        backend.freeReg(nReg);
        backend.exitMain("main_end");


        backend.enterProc("fak",1);                  //fak{
        nReg = backend.allocReg();



        backend.freeReg(nReg);
        backend.exitProc("fak_end");                    //}
    }
}
