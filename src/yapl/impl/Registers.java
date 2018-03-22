package yapl.impl;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Registers {

  private Register zeroRegister;
  private StackPointerRegister stackPointerRegister;
  private GlobalPointerRegister globalPointerRegister;
  private Register framePointerRegister;
  private Register returnAddressRegister;
  private Register atRegister;
  private Register kernelRegister0;
  private Register kernelRegister1;

  private List<Register> returnValueRegisters;
  private List<Register> argumentRegisters;
  private List<Register> temporaryRegisters;
  private List<Register> savedRegisters;

  public Registers() {
    returnValueRegisters = new ArrayList<>();
    argumentRegisters = new ArrayList<>();
    temporaryRegisters = new ArrayList<>();
    savedRegisters = new ArrayList<>();

    zeroRegister = new Register((byte) 0, "$zero", true);
    atRegister = new Register((byte) 1, "$at", true);
    kernelRegister0 = new Register((byte) 26, "$k0", true);
    kernelRegister1 = new Register((byte) 27, "$k1", true);
    globalPointerRegister = new GlobalPointerRegister();
    stackPointerRegister = new StackPointerRegister();
    framePointerRegister = new Register((byte) 30, "$fp", true);
    returnAddressRegister = new Register((byte) 31, "$ra", true);

    returnValueRegisters.add(new Register((byte) 2, "$v0"));
    returnValueRegisters.add(new Register((byte) 3, "$v1"));
    argumentRegisters.add(new Register((byte) 4, "$a0"));
    argumentRegisters.add(new Register((byte) 5, "$a1"));
    argumentRegisters.add(new Register((byte) 6, "$a2"));
    argumentRegisters.add(new Register((byte) 7, "$a3"));

    temporaryRegisters.add(new Register((byte) 8, "$t0"));
    temporaryRegisters.add(new Register((byte) 9, "$t1"));
    temporaryRegisters.add(new Register((byte) 10, "$t2"));
    temporaryRegisters.add(new Register((byte) 11, "$t3"));
    temporaryRegisters.add(new Register((byte) 12, "$t4"));
    temporaryRegisters.add(new Register((byte) 13, "$t5"));
    temporaryRegisters.add(new Register((byte) 14, "$t6"));
    temporaryRegisters.add(new Register((byte) 15, "$t7"));
    temporaryRegisters.add(new Register((byte) 16, "$t8"));
    temporaryRegisters.add(new Register((byte) 17, "$t9"));

    savedRegisters.add(new Register((byte) 18, "$s0"));
    savedRegisters.add(new Register((byte) 19, "$s1"));
    savedRegisters.add(new Register((byte) 20, "$s2"));
    savedRegisters.add(new Register((byte) 21, "$s3"));
    savedRegisters.add(new Register((byte) 22, "$s4"));
    savedRegisters.add(new Register((byte) 23, "$s5"));
    savedRegisters.add(new Register((byte) 24, "$s6"));
    savedRegisters.add(new Register((byte) 25, "$s7"));
  }

  public Register getZeroRegister() {
    return zeroRegister;
  }

  public StackPointerRegister getStackPointerRegister() {
    return stackPointerRegister;
  }

  public GlobalPointerRegister getGlobalPointerRegister() {
    return globalPointerRegister;
  }

  public Register getFramePointerRegister() {
    return framePointerRegister;
  }

  public Register getReturnAddressRegister() {
    return returnAddressRegister;
  }

  public Optional<Register> getUnusedRegister() {
    for (Register register : temporaryRegisters) {
      if (!register.isInUse()) {
        return of(register);
      }
    }

    for (Register register : savedRegisters) {
      if (!register.isInUse()) {
        return of(register);
      }
    }
    return empty();
  }

  public Register getRegisterByNumber(byte registerNumber) {
    switch (registerNumber) {
      case 0:
        return zeroRegister;
      case 1:
        return atRegister;
      case 26:
        return kernelRegister0;
      case 27:
        return kernelRegister1;
      case 28:
        return globalPointerRegister;
      case 29:
        return stackPointerRegister;
      case 30:
        return framePointerRegister;
      case 31:
        return returnAddressRegister;
    }

    for (Register register : temporaryRegisters) {
      if (register.getRegisterNumber() == registerNumber) {
        return register;
      }
    }

    for (Register register : savedRegisters) {
      if (register.getRegisterNumber() == registerNumber) {
        return register;
      }
    }

    for (Register register : returnValueRegisters) {
      if (register.getRegisterNumber() == registerNumber) {
        return register;
      }
    }

    for (Register register : argumentRegisters) {
      if (register.getRegisterNumber() == registerNumber) {
        return register;
      }
    }

    throw new IllegalArgumentException("registerNumber must be between 0 and 31");
  }

  public void freeReqister(byte reg) {
    for (Register register : temporaryRegisters) {
      if (register.getRegisterNumber() == reg) {
        register.setInUse(false);
        return;
      }
    }

    for (Register register : savedRegisters) {
      if (register.getRegisterNumber() == reg) {
        register.setInUse(false);
        return;
      }
    }

    throw new IllegalArgumentException(
        format("registerNumber {0}does not belong to either temporary or saved register!", reg)
    );
  }

  public void freeAllRegister() {
    for (Register register : temporaryRegisters) {
      register.setInUse(false);
    }

    for (Register register : savedRegisters) {
      register.setInUse(false);
    }
  }
}
