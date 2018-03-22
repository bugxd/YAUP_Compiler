package yapl.impl;

public final class GlobalPointerRegister extends Register {

  private static final int MAXIMUM_OFFSET = 64000;

  private int currentPlusOffset;
  private int currentMinusOffset;

  public GlobalPointerRegister( ) {
    super((byte) 28, "$gp", true);
    this.currentMinusOffset = 0;
    this.currentPlusOffset = 0;
  }

  public int allocateBytes(int numberOfBytes, int wordSize) {
    numberOfBytes = doWordAlignment(numberOfBytes, wordSize);

    if (currentPlusOffset + numberOfBytes <= MAXIMUM_OFFSET) {
      int returnedOffset = currentPlusOffset;
      currentPlusOffset += numberOfBytes;
      return returnedOffset;
    }

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
