package yapl.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Registers {

  private List<Register> registers;
  
  public Registers() {
    this.registers = new ArrayList<>();
    // FIXME: 14.03.18 NOT ALL REGISTERS ARE USABLE!!!
    // TODO: 14.03.18 möglicherweise müssen wir noch Registerkategorien einfügen
    registers.add(new Register((byte)0, "$zero", true));
    registers.add(new Register((byte)1, "$at", true));
    registers.add(new Register((byte)2, "$v0"));
    registers.add(new Register((byte)3, "$v1"));
    registers.add(new Register((byte)4, "$a0"));
    registers.add(new Register((byte)5, "$a1"));
    registers.add(new Register((byte)6, "$a2"));
    registers.add(new Register((byte)7, "$a3"));
    registers.add(new Register((byte)8, "$t0"));
    registers.add(new Register((byte)9, "$t1"));
    registers.add(new Register((byte)10, "$t2"));
    registers.add(new Register((byte)11, "$t3"));
    registers.add(new Register((byte)12, "$t4"));
    registers.add(new Register((byte)13, "$t5"));
    registers.add(new Register((byte)14, "$t6"));
    registers.add(new Register((byte)15, "$t7"));
    registers.add(new Register((byte)16, "$t8"));
    registers.add(new Register((byte)17, "$t9"));
    registers.add(new Register((byte)18, "$s0"));
    registers.add(new Register((byte)19, "$s1"));
    registers.add(new Register((byte)20, "$s2"));
    registers.add(new Register((byte)21, "$s3"));
    registers.add(new Register((byte)22, "$s4"));
    registers.add(new Register((byte)23, "$s5"));
    registers.add(new Register((byte)24, "$s6"));
    registers.add(new Register((byte)25, "$s7"));
    registers.add(new Register((byte)26, "$k0", true));
    registers.add(new Register((byte)27, "$k1", true));
    registers.add(new Register((byte)28, "$gp", true));
    registers.add(new Register((byte)29, "$sp", true));
    registers.add(new Register((byte)30, "$fp", true));
    registers.add(new Register((byte)31, "$ra", true));
  }

  public Optional<Register> getUnusedRegister() {
    for (Register register : registers) {
      if (!register.isInUse()) {
        return of(register);
      }
    }
    return empty();
  }

  public Register getRegisterByNumber (byte registerNumber) {
    for (Register register : registers) {
      if (register.getRegisterNumber() == registerNumber) {
        return register;
      }
    }

    throw new IllegalArgumentException("registerNumber must be between 0 and 31");
  }

  public void freeReqister(byte reg) {
    for (Register register : registers) {
      if (register.getRegisterNumber() == reg)  {
        register.setInUse(false);
      }
    }
  }

  public void freeAllRegister(){
    for (Register r: Registers) {
      if(r.isWriteable)
        r.setInUse(false);
    }
  }
}
