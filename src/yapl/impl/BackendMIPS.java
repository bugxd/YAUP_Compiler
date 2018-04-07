package yapl.impl;

import static java.text.MessageFormat.format;

import java.io.PrintStream;
import java.util.Optional;

public final class BackendMIPS implements yapl.interfaces.BackendAsmRM {

  private final PrintStream outputStream;
  private       Registers   registers;
  private       Segment currentSegment;

  private final int wordAlignmentParameter = 2;

  public BackendMIPS(PrintStream outputStream) {
    this.outputStream = outputStream;
    this.registers = new Registers();
    this.currentSegment = null;

    if (outputStream == null) {
      throw new IllegalArgumentException("outputStream must not be null!");
    }

    writePredefinedProcedures();
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
    outputStream.append(format("\t# {0}\n", comment));
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

    outputStream.append(format(".space {0}", String.valueOf(bytes)));
    comment(comment);
    outputStream.println(format(".align {0}", wordAlignmentParameter));

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

    comment(comment);
    int offset = registers.getStackPointerRegister().allocateBytes(bytes, wordSize());
    outputStream.println(
        format(
            "\taddi\t{0}\t{1}\t-{2}",
            registers.getStackPointerRegister().getName(),
            registers.getStackPointerRegister().getName(),
            String.valueOf(registers.getStackPointerRegister().doWordAlignment(bytes, wordSize()))
        )
    );

    return offset;
  }

  // TODO: 05.04.18 TEST THIS
  @Override
  public void allocHeap(byte destReg, int bytes) {

    changeSegment(Segment.TEXT);

    Register register = registers.getRegisterByNumber(destReg);
    loadConst(registers.getA0().getRegisterNumber(), bytes);
    loadConst(registers.getV0().getRegisterNumber(), SyscallCode.ALLOCATE_HEAP.getValue());

    // SAVE REGISTERS ON STACK BEFORE SYSCALL
    syscall();
    move(register.getRegisterNumber(), registers.getV0().getRegisterNumber());

    // RESTORE REGISTER VALUES
  }

  private void move(byte destReg, byte srcReg) {
    Register destinationRegister = registers.getRegisterByNumber(destReg);
    Register sourceRegister = registers.getRegisterByNumber(srcReg);

    outputStream.append(
            format("\tmove\t{0},\t{1}\n", destinationRegister.getName(), sourceRegister.getName())
    );
  }

  @Override
  public void storeArrayDim(int dim, byte lenReg) {
    if (dim != 0) { // WE ONLY SUPPORT 1-DIMENSIONAL ARRAYS
      return;
    }

    move(registers.getArrayLengthRegister().getRegisterNumber(), lenReg);
  }

  @Override
  public void allocArray(byte destReg) {

    changeSegment(Segment.TEXT);

    Register destinationRegister = registers.getRegisterByNumber(destReg);
    move(registers.getA0().getRegisterNumber(), registers.getArrayLengthRegister().getRegisterNumber());
    addConst(registers.getA0().getRegisterNumber(), registers.getA0().getRegisterNumber(), 1);  // add 1 to leave place for the array header
    mulConst(registers.getA0().getRegisterNumber(), registers.getA0().getRegisterNumber(), wordSize());  // multiply by wordSize() to get correct number of bytes

    loadConst(registers.getV0().getRegisterNumber(), SyscallCode.ALLOCATE_HEAP.getValue());
    //SAVE ALL REGISTERS BEFORE SYSCALL
    syscall();
    move(destinationRegister.getRegisterNumber(), registers.getV0().getRegisterNumber());
    storeWordReg(registers.getArrayLengthRegister().getRegisterNumber(), destinationRegister.getRegisterNumber());
    // store as first value of the array the length of the array

    //RESTORE PREVIOUS REGISTER VALUES
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
          format("\tla\t{0},\t{1}({2})\n",
                  destination.getName(),
                  String.valueOf(addr),
                  registers.getGlobalPointerRegister().getName()
          )
      );
    } else {
      outputStream.append(
          format("\tla\t{0},\t{1}({2})\n",
                  destination.getName(),
                  String.valueOf(addr),
                  registers.getFramePointerRegister().getName()
          )
      );
    }
  }

  @Override
  public void loadWord(byte reg, int addr, boolean isStatic) {

    changeSegment(Segment.TEXT);

    Register destination = registers.getRegisterByNumber(reg);
    if (isStatic) {
      outputStream.append(
          format("\tlw\t{0},\t{1}({2})\n",
                  destination.getName(),
                  String.valueOf(addr),
                  registers.getGlobalPointerRegister().getName()
          )
      );
    } else {
      outputStream.append(
          format("\tlw\t{0},\t{1}({2})\n",
                  destination.getName(),
                  String.valueOf(addr),
                  registers.getFramePointerRegister().getName()
          )
      );
    }
  }

  @Override
  public void storeWord(byte reg, int addr, boolean isStatic) {

    changeSegment(Segment.TEXT);
    Register source = registers.getRegisterByNumber(reg);
    if (isStatic) {
      outputStream.append(
          format("\tsw\t{0},\t{1}({2})\n",
                  source.getName(),
                  String.valueOf(addr),
                  registers.getGlobalPointerRegister().getName()
          )
      );
    } else {
      outputStream.append(
          format("\tsw\t{0},\t{1}({2})\n",
                  source.getName(),
                  String.valueOf(addr),
                  registers.getFramePointerRegister().getName()
          )
      );
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
    Register address = registers.getRegisterByNumber((byte) addrReg);
    outputStream.append(
            format("\tsw\t{0},\t({1})\n", source.getName(), address.getName()));
  }

  @Override
  public void arrayOffset(byte dest, byte baseAddr, byte index) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(dest);
    Register baseAddrReg = registers.getRegisterByNumber(baseAddr);
    Register indexReg = registers.getRegisterByNumber(index);

    mulConst(indexReg.getRegisterNumber(), indexReg.getRegisterNumber(), wordSize());
    add(destination.getRegisterNumber(), baseAddrReg.getRegisterNumber(), indexReg.getRegisterNumber());
    addConst(destination.getRegisterNumber(), destination.getRegisterNumber(), wordSize()); // shift index by 1 element because array[0] holds the array's length
    divConst(indexReg.getRegisterNumber(), indexReg.getRegisterNumber(), wordSize());  //index /4       restore old value
  }

  @Override
  public void arrayLength(byte dest, byte baseAddr) {
    loadWordReg(dest, baseAddr);
  }

  @Override
  public void writeString(int addr) {

    changeSegment(Segment.TEXT);

    //saveRegisters!!

    loadConst(registers.getV0().getRegisterNumber(), SyscallCode.PRINT_STRING.getValue());
    loadAddress(registers.getA0().getRegisterNumber(), addr, true);
    syscall();
  }

  private void syscall() {
    changeSegment(Segment.TEXT);
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

    outputStream.println(
        format("\tadd\t{0},\t{1},\t{2}", destination.getName(), sourceX.getName(), String.valueOf(value))
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


  private void mulConst(byte regDest, byte regX, int value) {
    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);

    outputStream.append(
            format("\tmul\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(), String.valueOf(value))
    );
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

  private void divConst(byte regDest, byte regX, int value) {

    changeSegment(Segment.TEXT);
    Register destination = registers.getRegisterByNumber(regDest);
    Register sourceX = registers.getRegisterByNumber(regX);

    outputStream.append(
            format("\tdiv\t{0},\t{1},\t{2}\n", destination.getName(), sourceX.getName(), String.valueOf(value))
    );
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
    outputStream.append(format("\tj {0}\n", label));
  }

  /**
   * .globl main
   * main:
   */
  @Override
  public void enterMain() {

    registers.getStackPointerRegister().requestNewOffset();

    changeSegment(Segment.TEXT);
    outputStream.append(".globl main\n");
    emitLabel("main", "main function entry point");
    comment("Set $fp to content of $sp which is top of the stack frame");
    move(registers.getFramePointerRegister().getRegisterNumber(), registers.getStackPointerRegister().getRegisterNumber());
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
    loadConst(registers.getV0().getRegisterNumber(), SyscallCode.EXIT.getValue());
    syscall();
    registers.freeAllRegister();
  }

  @Override
  public void enterProc(String label, int nParams) {
    registers.getStackPointerRegister().requestNewOffset();

    changeSegment(Segment.TEXT);
    emitLabel(label, "");
  }

  @Override
  public void exitProc(String label) {
    changeSegment(Segment.TEXT);

    emitLabel(label, "procedure_epilogue");
    byte helperRegister = allocReg();
    assert helperRegister != -1;
    //loadWord(helperRegister, -4, false); // LOAD NUMBER OF ARGUMENTS
    //freeStackReg(helperRegister);
    move(registers.getStackPointerRegister().getRegisterNumber(), registers.getFramePointerRegister().getRegisterNumber());
    addConst(registers.getStackPointerRegister().getRegisterNumber(), registers.getStackPointerRegister().getRegisterNumber(), wordSize()); // eliminate place on stack where old framepointer was stored
    loadWord(registers.getFramePointerRegister().getRegisterNumber(), 0, false);  // LOAD old framepointer
    jumpRegister(registers.getReturnAddressRegister().getRegisterNumber());

    registers.getStackPointerRegister().deleteCurrentOffset();
  }

  private void jumpRegister(byte reg) {
    changeSegment(Segment.TEXT);

    outputStream.println(format("\tjr\t{0}\n", registers.getRegisterByNumber(reg).getName()));
  }

  @Override
  public void returnFromProc(String label, byte reg) {
    changeSegment(Segment.TEXT);

    if (reg != -1) {
      move(registers.getV0().getRegisterNumber(), reg);
    }
    jump(label);
  }

  @Override
  public void prepareProcCall(int numArgs) {
    changeSegment(Segment.TEXT);

    int oldFramePointerStorage = allocStack(wordSize(), "place for old frame pointer");
    storeWord(registers.getFramePointerRegister().getRegisterNumber(), oldFramePointerStorage, false);
    move(registers.getFramePointerRegister().getRegisterNumber(), registers.getStackPointerRegister().getRegisterNumber());

    int bytes = numArgs * wordSize();
    allocStack(bytes, "push stack frame");
  }

  private void jumpAndLink(String label) {
    changeSegment(Segment.TEXT);

    outputStream.println(format("\tjal\t{0}", label));
  }

  @Override
  public void passArg(int arg, byte reg) {
    changeSegment(Segment.TEXT);

    storeWord(reg, paramOffset(arg), false);
  }

  @Override
  public void callProc(byte reg, String name) {
    changeSegment(Segment.TEXT);

    jumpAndLink(name);

    if (reg != -1) {
      move(reg, registers.getV0().getRegisterNumber());
    }
    registers.getStackPointerRegister().freeBytes(wordSize()*2, wordSize());
  }

  @Override
  public int paramOffset(int index) {
    return -(index+1) * wordSize();
  }

  private void writePredefinedProcedures() {
    comment("PREDEFINED PROCEDURES START");
    writeProcedure_writeint();
    comment("PREDEFINED PROCEDURES END");
  }

  private void writeProcedure_writeint() {
    changeSegment(Segment.TEXT);
    enterProc("writeint", 1);
    byte r = allocReg();
    loadWord(r, paramOffset(0), false);
    move(registers.getA0().getRegisterNumber(), r);
    freeReg(r);
    loadConst(registers.getV0().getRegisterNumber(), SyscallCode.PRINT_INT.getValue());
    syscall();
    returnFromProc("writeint_end", (byte) -1);
    exitProc("writeint_end");
  }

}
