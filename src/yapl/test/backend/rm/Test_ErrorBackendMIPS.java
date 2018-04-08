package backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Test_ErrorBackendMIPS {
    private static BackendAsmRM backend;

    public static void main(String[] args) throws Exception {
        backend = new BackendMIPS(null);
        backend.enterMain();
    }
}
