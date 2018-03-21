package yapl.impl;

public final class Register {

  private final String name;
  private final byte registerNumber;
  private boolean inUse;
  private boolean writeable;

  public Register(byte registerNumber, String name) {
    this(registerNumber, name, false);
    this.writeable = true;
  }

  public Register(byte registerNumber, String name, boolean inUse) {
    this.name = name;
    this.registerNumber = registerNumber;
    this.inUse = inUse;
    this.writeable = false;
  }

  public String getName() {
    return name;
  }

  public byte getRegisterNumber() {
    return registerNumber;
  }

  public boolean isInUse() {
    return inUse;
  }

  public boolean isWriteable() { return writeable; }

  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }
}
