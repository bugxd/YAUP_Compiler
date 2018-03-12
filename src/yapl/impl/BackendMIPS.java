package yapl.impl;

import static java.text.MessageFormat.format;

import java.io.PrintStream;

public final class BackendMIPS implements yapl.interfaces.BackendAsmRM {

  private final PrintStream outputStream;

  public BackendMIPS (PrintStream outputStream) {
    this.outputStream = outputStream;

    if (outputStream == null) {
      throw new IllegalArgumentException("outputStream must not be null!");
    }
  }

  @Override
  public int wordSize() {
    return 4; // always 4 bytes in MIPS
  }

  @Override
  public int boolValue(boolean value) {
    if (value == true) {
      return 1;
    }
    else {
      return 0;
    }
  }

  @Override
  public byte allocReg() {
    return 0;
  }

  @Override
  public void freeReg(byte reg) {

  }

  @Override
  public byte zeroReg() {
    return 0;
  }

  @Override
  public void comment(String comment) {
    outputStream.append(format("% {0}\n", comment));
  }

  @Override
  public void emitLabel(String label, String comment) {
    outputStream.append(format("{0}:\t\t% {1}\n", label, comment));
  }

  @Override
  public int allocStaticData(int bytes, String comment) {
    return 0;
  }

  @Override
  public int allocStringConstant(String string) {
    return 0;
  }

  @Override
  public int allocStack(int bytes, String comment) {
    return 0;
  }

  @Override
  public void allocHeap(byte destReg, int bytes) {

  }

  @Override
  public void storeArrayDim(int dim, byte lenReg) {

  }

  @Override
  public void allocArray(byte destReg) {

  }

  @Override
  public void loadConst(byte reg, int value) {

  }

  @Override
  public void loadAddress(byte reg, int addr, boolean isStatic) {

  }

  @Override
  public void loadWord(byte reg, int addr, boolean isStatic) {

  }

  @Override
  public void storeWord(byte reg, int addr, boolean isStatic) {

  }

  @Override
  public void loadWordReg(byte reg, byte addrReg) {

  }

  @Override
  public void loadWordReg(byte reg, byte addrReg, int offset) {

  }

  @Override
  public void storeWordReg(byte reg, int addrReg) {

  }

  @Override
  public void arrayOffset(byte dest, byte baseAddr, byte index) {

  }

  @Override
  public void arrayLength(byte dest, byte baseAddr) {

  }

  @Override
  public void writeString(int addr) {

  }

  @Override
  public void neg(byte regDest, byte regX) {

  }

  @Override
  public void add(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void addConst(byte regDest, byte regX, int value) {

  }

  @Override
  public void sub(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void mul(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void div(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void mod(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void isLess(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void isLessOrEqual(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void isEqual(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void not(byte regDest, byte regSrc) {

  }

  @Override
  public void and(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void or(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void branchIf(byte reg, boolean value, String label) {

  }

  @Override
  public void jump(String label) {
    outputStream.append(format("jr {0}\n", label));
  }

  @Override
  public void enterMain() {

  }

  @Override
  public void exitMain(String label) {
    emitLabel(label, "main epilogue");
    outputStream.append("li $v0 10\n");
    outputStream.append("syscall\n");
  }

  @Override
  public void enterProc(String label, int nParams) {

  }

  @Override
  public void exitProc(String label) {

  }

  @Override
  public void returnFromProc(String label, byte reg) {

  }

  @Override
  public void prepareProcCall(int numArgs) {

  }

  @Override
  public void passArg(int arg, byte reg) {

  }

  @Override
  public void callProc(byte reg, String name) {

  }

  @Override
  public int paramOffset(int index) {
    return 0;
  }
}
