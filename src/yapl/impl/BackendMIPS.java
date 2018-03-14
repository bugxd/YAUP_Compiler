package yapl.impl;

import static java.text.MessageFormat.format;

import java.io.PrintStream;
import java.util.Optional;

public final class BackendMIPS implements yapl.interfaces.BackendAsmRM {

  private final PrintStream outputStream;
  private Registers registers;

  public BackendMIPS (PrintStream outputStream) {
    this.outputStream = outputStream;
    this.registers = new Registers();

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
    Optional<Register> registerOptional = registers.getUnusedRegister();
    if (!registerOptional.isPresent()) {
      return -1;
    }

    Register register = registerOptional.get();
    register.setInUse(true);
    return register.getRegisterNumber();
  }

  @Override
  public void freeReg(byte reg) {
    registers.freeReqister(reg);
  }

  @Override
  public byte zeroReg() {
    Register zeroRegister = registers.getRegisterByNumber((byte)0);
    return zeroRegister.getRegisterNumber();
  }

  @Override
  public void comment(String comment) {
    outputStream.append(format("# {0}\n", comment));
  }

  @Override
  public void emitLabel(String label, String comment) {
    outputStream.append(format("{0}:\t\t# {1}\n", label, comment));
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
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("add {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void addConst(byte regDest, byte regX, int value) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);

    outputStream.append(format("add {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               value));
  }

  @Override
  public void sub(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("sub {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void mul(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("mul {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void div(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("div {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void mod(byte regDest, byte regX, byte regY) {

  }

  @Override
  public void isLess(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("slt {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void isLessOrEqual(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("sle {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void isEqual(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("seq {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void not(byte regDest, byte regSrc) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register source = registers.getRegisterByNumber(regSrc);

    outputStream.append(format("not {0}, {1}\n", destination.getName(), source.getName()));
  }

  @Override
  public void and(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("and {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void or(byte regDest, byte regX, byte regY) {
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("or {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName() ));
  }

  @Override
  public void branchIf(byte reg, boolean value, String label) {
    Register register = registers.getRegisterByNumber(reg);

    outputStream.append(format("beq {0}, {1}, {2}\n", register.getName(), this.boolValue(value),
                                   label));
  }

  @Override
  public void jump(String label) {
    outputStream.append(format("j {0}\n", label));
  }

  @Override
  public void enterMain() {

  }

  @Override
  public void exitMain(String label) {
    emitLabel(label, "main_epilogue");
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
