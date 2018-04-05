package yapl.impl;

import static java.text.MessageFormat.format;

import java.io.PrintStream;
import java.util.Optional;

public final class BackendMIPS implements yapl.interfaces.BackendAsmRM {

  private final PrintStream outputStream;
  private       Registers   registers;
  private       Segment currentSegment;

  private final int wordAlignmentParameter = 2;

  int arrayDimSize = 0;

  public BackendMIPS(PrintStream outputStream) {
    this.outputStream = outputStream;
    this.registers = new Registers();
    this.currentSegment = null;

    if (outputStream == null) {
      throw new IllegalArgumentException("outputStream must not be null!");
    }
  }

  private void changeSegment(Segment segment) {
    if (currentSegment == null || !currentSegment.equals(segment)) {
      outputStream.println(segment.toString());
      currentSegment = segment;
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
    } else {
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
    return registers.getZeroRegister().getRegisterNumber();
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
    changeSegment(Segment.TEXT);

    outputStream.append(format("{0}:\t\t\t", label));
    comment(comment);
  }

  @Override
  public int allocStaticData(int bytes, String comment) {

    changeSegment(Segment.DATA);
    GlobalPointerRegister globalPointerRegister = registers.getGlobalPointerRegister();

    outputStream.println(format(".space {0}", String.valueOf(bytes)));
    outputStream.append(format(".align {0}", wordAlignmentParameter));
    comment(comment);

    return globalPointerRegister.allocateBytes(bytes, wordSize());
  }

  // TODO: 05.04.18 FIXME: Escaped stuff - like "\n" - is not printed correctly
  @Override
  public int allocStringConstant(String string) {

    changeSegment(Segment.DATA);

    int size = string.length();
    int bytesToAllocate = (size + 1);

    outputStream.println(format(".asciiz\t\"{0}\"", string));
    outputStream.println(format(".align {0}", wordAlignmentParameter));
    return registers.getGlobalPointerRegister().allocateBytes(bytesToAllocate, 4);
  }

  // TODO: 05.04.18 TEST
  @Override
  public int allocStack(int bytes, String comment) {

    changeSegment(Segment.TEXT);

    StackPointerRegister stackPointerRegister = registers.getStackPointerRegister();
    int offset = stackPointerRegister.allocateBytes(bytes, wordSize());
    outputStream.println(
        format(
            "\taddi\t{0}\t{1}\t-{2}",
            registers.getStackPointerRegister().getName(),
            registers.getStackPointerRegister().getName(),
            String.valueOf(stackPointerRegister.doWordAlignment(bytes, wordSize()))
        )
    );

    comment(comment);
    return offset;
  }

  // TODO: 05.04.18 TEST THIS
  @Override
  public void allocHeap(byte destReg, int bytes) {

    changeSegment(Segment.TEXT);

    Register register = registers.getRegisterByNumber(destReg);
    outputStream.append(
        format("\tli\t$a0,\t{0}\n", bytes)
    );
    // TODO: 22.03.18 ENUM for SYSCALL CODES!
    // AND WRITE SYSCALL METHOD
    outputStream.append("\tli\t$v0,\t9\n");
    outputStream.append("syscall\n");
    outputStream.append(format("\tmove\t{0},\t$v0\n", register.getName()));
  }

  @Override
  public void storeArrayDim(int dim, byte lenReg) {
    if (dim != 0) {
      return;
    }

    arrayDimSize = lenReg;
  }

  @Override
  public void allocArray(byte destReg) {

    changeSegment(Segment.TEXT);
    allocHeap(destReg, (arrayDimSize + 1)*wordSize()); // + 1 because 1 word is needed for dimsize


    //Register destination = registers.getRegisterByNumber(destReg);
    //outputStream.append(
    //    format("\tadd\t{0},\t{1},\t{2}\n",
    //           destination.getName(), zeroReg(),
    //           registers.getGlobalPointerRegister().getName()
    //    )
    //);
  }

  @Override
  public void loadConst(byte reg, int value) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(reg);
    outputStream.append(format("\tli\t{0},\t{1}\n", destination.getName(), String.valueOf(value)));
  }

  @Override
  public void loadAddress(byte reg, int addr, boolean isStatic) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(reg);
    if (isStatic) {
      outputStream.append(
          format("\tla\t{0},\t{1}($gp)\n", destination.getName(), String.valueOf(addr)));
    } else {
      outputStream.append(
          format("\tla\t{0},\t{1}($sp)\n", destination.getName(), String.valueOf(addr)));
    }
  }

  @Override
  public void loadWord(byte reg, int addr, boolean isStatic) {

    changeSegment(Segment.TEXT);

    Register destination = registers.getRegisterByNumber(reg);
    if (isStatic) {
      outputStream.append(
          format("\tlw\t{0},\t{1}($gp)\n", destination.getName(), String.valueOf(addr)));
    } else {
      outputStream.append(
          format("\tlw\t{0},\t{1}($sp)\n", destination.getName(), String.valueOf(addr)));
    }
  }

  @Override
  public void storeWord(byte reg, int addr, boolean isStatic) {

    changeSegment(Segment.TEXT);
    Register source = registers.getRegisterByNumber(reg);
    if (isStatic) {
      outputStream.append(
          format("\tsw\t{0},\t{1}($gp)\n", source.getName(), String.valueOf(addr)));
    } else {
      outputStream.append(
          format("\tsw\t{0},\t{1}($sp)\n", source.getName(), String.valueOf(addr)));
    }
  }

  @Override
  public void loadWordReg(byte reg, byte addrReg) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(reg);
    Register address = registers.getRegisterByNumber(addrReg);
    outputStream.append(format("\tlw\t{0},\t({1})\n", destination.getName(), address.getName()));
  }

  @Override
  public void loadWordReg(byte reg, byte addrReg, int offset) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(reg);
    Register address = registers.getRegisterByNumber(addrReg);
    outputStream.append(format("\tlw\t{0},\t{1}({2})\n",
                               destination.getName(),
                               String.valueOf(offset),
                               address.getName()));
  }

  @Override
  public void storeWordReg(byte reg, int addrReg) {

    changeSegment(Segment.TEXT);
    Register source = registers.getRegisterByNumber(reg);
    //TOFIX: addrReg should be byte not int
    Register address = registers.getRegisterByNumber((byte) addrReg);
    outputStream.append(format("\tsw\t{0},\t({1})\n", source.getName(), address.getName()));
  }

  @Override
  public void arrayOffset(byte dest, byte baseAddr, byte index) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(dest);
    Register baseAddrReg = registers.getRegisterByNumber(baseAddr);
    Register indexReg = registers.getRegisterByNumber(index);

    outputStream.append(format("\tmul\t{0},\t{0},\t4\n", indexReg.getName()));       //index *4
    //TODO: la t0, index(base) ? add t0,base,index
    outputStream.append(format("\tadd\t{0},\t{1},\t{2}\n",
                               destination.getName(),
                               baseAddrReg.getName(),
                               indexReg.getName()));
    outputStream.append(format("\tdiv\t{0},\t{0},\t4\n",
                               indexReg.getName()));       //index /4        old value
  }

  @Override
  public void arrayLength(byte dest, byte baseAddr) {
    loadWordReg(dest, baseAddr);
  }

  @Override
  public void writeString(int addr) {

    changeSegment(Segment.TEXT);
    outputStream.append("\tli\t$v0,\t4\n");
    outputStream.append(format("\tla\t$a0,\t{0}($gp)\n", String.valueOf(addr)));
    outputStream.append("\tsyscall\n");
  }

  @Override
  public void neg(byte regDest, byte regX) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);

    outputStream.append(
        format("\tnor\t{0},\t{1},\t{2}\n",
               destination.getName(),
               sourceX.getName(),
               registers.getZeroRegister().getName()
        )
    );
  }

  @Override
  public void add(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tadd\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void addConst(byte regDest, byte regX, int value) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);

    outputStream.append(
        format("\tadd\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(), String.valueOf(value))
    );
  }

  @Override
  public void sub(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tsub\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void mul(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tmul\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void div(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tdiv\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void mod(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tdiv\t{0},\t{1}\n", sourceX.getName(), sourceY.getName()));
    outputStream.append(format("\tmfhi\t{0}\n", destination.getName()));
    //mfhi = move from high register (high = mod)
    //mflo = move from low register (low = div)
  }

  @Override
  public void isLess(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tslt\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void isLessOrEqual(byte regDest, byte regX, byte regY) {


    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tsle\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void isEqual(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tseq\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void not(byte regDest, byte regSrc) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register source = registers.getRegisterByNumber(regSrc);

    outputStream.append(format("\tnot\t{0},\t{1}\n", destination.getName(), source.getName()));
  }

  @Override
  public void and(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tand\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void or(byte regDest, byte regX, byte regY) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);
    Register sourceY = registers.getRegisterByNumber(regY);

    outputStream.append(format("\tor\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(),
                               sourceY.getName()));
  }

  @Override
  public void branchIf(byte reg, boolean value, String label) {

    changeSegment(Segment.TEXT);
    Register register = registers.getRegisterByNumber(reg);

    outputStream.append(format("\tbeq\t{0},\t{1},\t{2}\n",
                               register.getName(),
                               this.boolValue(value),
                               label));
  }

  @Override
  public void jump(String label) {

    changeSegment(Segment.TEXT);
    outputStream.append(format("j {0}\n", label));
  }

  /**
   * .globl main
   * main:
   */
  @Override
  public void enterMain() {

    changeSegment(Segment.TEXT);
    outputStream.append(".globl main\n");
    emitLabel("main", "main function entry point");
  }

  /**
   * main_epilogue:
   * li $v0,  10
   * syscall
   */
  @Override
  public void exitMain(String label) {

    changeSegment(Segment.TEXT);
    emitLabel(label, "main_epilogue");
    outputStream.append("\tli\t$v0,\t10\n");
    outputStream.append("\tsyscall\n");
    registers.freeAllRegister();
  }


  @Override
  public void enterProc(String label, int nParams) {
    emitLabel(label, "");    //no comment
    //for (int i = 0; i < nParams * 4; i += 4) {
    //  //TODO: finish for
    //  outputStream.append("\tsw\t");
    //}


  }

  @Override
  public void exitProc(String label) {
    jump(label);
    //TODO: some other stuff
  }

  @Override
  public void returnFromProc(String label, byte reg) {
  }



  @Override
  public void prepareProcCall(int numArgs) {
    int bytes = numArgs * wordSize();
    int bytesForReturnValueRegisterAndReturnAddressRegister = 2 * wordSize();
    int totalBytes = bytes + bytesForReturnValueRegisterAndReturnAddressRegister;

    addConst(registers.getStackPointerRegister().getRegisterNumber(),
             registers.getStackPointerRegister().getRegisterNumber(),
             -totalBytes);
  }

  private void jumpAndLink(String label) {
    outputStream.println(format("\tjal\t{0}", label));
    storeWord(registers.getReturnAddressRegister().getRegisterNumber(), 4, false);
    // store return address at 4(sp)
  }

  @Override
  public void passArg(int arg, byte reg) {
    Register register = registers.getRegisterByNumber(reg);
    storeWord(reg, (arg + 2) *wordSize(), false); // arg + 2, because arg *wordsize() is
    // reserved for register number of return value register and arg + 1 is reserved for return
    // address
  }

  @Override
  public void callProc(byte reg, String name) {
    storeWord(reg, 0, false); // return register number is stored at lowest stack address
    jumpAndLink(name);
  }

  @Override
  public int paramOffset(int index) {
    return (index + 2) * wordSize(); // + 2 because 0 and 1 is reserved for retAddr and retReg respectively
  }
}
