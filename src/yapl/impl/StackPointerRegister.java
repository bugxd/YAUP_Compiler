package yapl.impl;

public final class StackPointerRegister extends Register{

  // FIXME: 22.03.18 find out maxvalue
  private static final int MAXIMUM_OFFSET = 10_000;
  private int currentMinusOffset;

  public StackPointerRegister() {
    super((byte) 29, "$sp", true);
    this.currentMinusOffset = 0;
  }

  public int allocateBytes(int numberOfBytes, int wordSize) {
    numberOfBytes = doWordAlignment(numberOfBytes, wordSize);

    if (currentMinusOffset + numberOfBytes <= MAXIMUM_OFFSET) {
      currentMinusOffset += numberOfBytes;
      return -currentMinusOffset;
    }

    throw new IllegalArgumentException("No space left in static data area!");
  }

  private int doWordAlignment(int numberOfBytes, int wordSize) {
    int rest = numberOfBytes % wordSize;
    if (rest != 0) {
      numberOfBytes = numberOfBytes + (wordSize - numberOfBytes % wordSize);
    }
    return  numberOfBytes;
  }
}
