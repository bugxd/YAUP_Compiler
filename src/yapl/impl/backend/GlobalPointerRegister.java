package yapl.impl.backend;

public final class GlobalPointerRegister extends Register {

  private static final int MAXIMUM_OFFSET = 65563;

  private int currentPlusOffset;
  private int currentMinusOffset;

  public GlobalPointerRegister( ) {
    super((byte) 28, "$gp", true);
    this.currentMinusOffset = 0;
    this.currentPlusOffset = 32768; // 32768 is the byte offset between the address, $gp points to (0x8000), and the address where the first entry of the data segment is written to (0x10000).
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
