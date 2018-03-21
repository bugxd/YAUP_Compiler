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

  /**
   * label    #comment
   */
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

  /**
   * addi $sp,  $zero,   +/-<bytes>   #comment
   */
  @Override
  public int allocStack(int bytes, String comment) {
    int bits = bytes*wordSize()
    outputStream.append(format("addi\t$sp,\t{0},\t{1}\t{2}",zeroReg(),bits,comment));
    return bits;  //TODO: Dont know what to return
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
    Register destination = registers.getRegisterByNumber(reg);
    outputStream.append(format("li\t{0},\t{1}",destination.getName(),value));
  }

  @Override
  public void loadAddress(byte reg, int addr, boolean isStatic) {
    Register destination = registers.getRegisterByNumber(reg);
    if(isStatic)
      outputStream.append(format("la\t{0},\t{1}($gp)",destination.getName(),addr));
    else
      outputStream.append(format("la\t{0},\t{1}($sp)",destination.getName(),addr));
  }

  @Override
  public void loadWord(byte reg, int addr, boolean isStatic) {
    Register destination = registers.getRegisterByNumber(reg);
    if(isStatic)
      outputStream.append(format("lw\t{0},\t{1}($gp)",destination.getName(),addr));
    else
      outputStream.append(format("lw\t{0},\t{1}($sp)",destination.getName(),addr));
  }

  @Override
  public void storeWord(byte reg, int addr, boolean isStatic) {
    Register source = registers.getRegisterByNumber(reg);
    if(isStatic)
      outputStream.append(format("sw\t{0},\t{1}($gp)",source.getName(),addr));
    else
      outputStream.append(format("sw\t{0},\t{1}($sp)",source.getName(),addr));
  }

  @Override
  public void loadWordReg(byte reg, byte addrReg) {
    Register destination = registers.getRegisterByNumber(reg);
    Register address = registers.getRegisterByNumber(addrReg);
    outputStream.append(format("lw\t{0},\t({1})",destination.getName(),address.getName()));
  }

  @Override
  public void loadWordReg(byte reg, byte addrReg, int offset) {
    Register destination = registers.getRegisterByNumber(reg);
    Register address = registers.getRegisterByNumber(addrReg);
    outputStream.append(format("lw\t{0},\t{1}({2})",destination.getName(),offset,address.getName()));
  }

  @Override
  public void storeWordReg(byte reg, int addrReg) {
    Register source = registers.getRegisterByNumber(reg);
    Register address = registers.getRegisterByNumber(addrReg);
    outputStream.append(format("sw\t{0},\t({1})",source.getName(),address.getName()));
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
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register zeroRegister = registers.getRegisterByNumber((byte)0);

    outputStream.append(format("nor {0}, {1}, {2}\n", destination.getName(), sourceX.getName(),
            zeroRegister ));
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
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("div {0}, {1}\n", sourceX.getName(), sourceY.getName() ));
    outputStream.append(format("mfhi {0}\n", destination.getName()));
    //mfhi = move from high register (high = mod)
    //mflo = move from low register (low = div)
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

  /**
   * .globl main
   * main:
   */
  @Override
  public void enterMain() {
    outputStream.append(".globl main\n");
    emitLabel("main","main function entry point");
  }

  /**
   * main_epilogue:
   * li $v0,  10
   * syscall
   */
  @Override
  public void exitMain(String label) {
    emitLabel(label, "main_epilogue");
    outputStream.append("li $v0, 10\n");
    outputStream.append("syscall\n");
    registers.freeAllRegister();
  }

  /**
   * label:
   * lw	<register 1>,	0($sp)
   * lw	<register 2>,	4($sp)
   * lw	<register 3>,	8($sp)
   * ...
   * lw <register n>,    <nParms*4>-4($sp)
   * addi	$sp,	$sp,	<nParms*4>
   */
  @Override
  public void enterProc(String label, int nParams) {
    emitLabel(label,"");    //no comment
    for(int i = 0; i < nParams*4; i += 4){

      outputStream.append("lw\t");
    }
  }

  @Override
  public void exitProc(String label) {
    //TODO: some other stuff
    registers.freeAllRegister();
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
