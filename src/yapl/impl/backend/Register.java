package yapl.impl.backend;

public class Register {

  private final String name;
  private final byte registerNumber;
  private boolean inUse;

  public Register(byte registerNumber, String name) {
    this(registerNumber, name, false);
  }

  public Register(byte registerNumber, String name, boolean inUse) {
    this.name = name;
    this.registerNumber = registerNumber;
    this.inUse = inUse;
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

  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }
}
