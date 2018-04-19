package yapl.impl;

import java.util.Stack;

public final class StackPointerRegister extends Register{

  // FIXME: 22.03.18 find out maxvalue
  private static final int MAXIMUM_OFFSET = -10_000;
  private Stack<Integer> framePointerOffsetStack;

  public StackPointerRegister() {
    super((byte) 29, "$sp", true);
    this.framePointerOffsetStack = new Stack<>();
  }

  public int allocateBytes(int numberOfBytes, int wordSize) {
    numberOfBytes = doWordAlignment(numberOfBytes, wordSize);

    int currentMinusOffset = framePointerOffsetStack.peek();
    if (currentMinusOffset - numberOfBytes >= MAXIMUM_OFFSET) {
      currentMinusOffset -= numberOfBytes;
      this.framePointerOffsetStack.pop();
      this.framePointerOffsetStack.push(currentMinusOffset);

      return currentMinusOffset;
    }

    throw new IllegalArgumentException("No space left in static data area!");
  }

  public int doWordAlignment(int numberOfBytes, int wordSize) {
    int rest = numberOfBytes % wordSize;
    if (rest != 0) {
      numberOfBytes = numberOfBytes + (wordSize - numberOfBytes % wordSize);
    }
    return  numberOfBytes;
  }

  public void freeBytes(int numberOfBytes, int wordSize) {
    int wordAlignedBytes = doWordAlignment(numberOfBytes, wordSize);
    int currentOffset = framePointerOffsetStack.pop();
    currentOffset += wordAlignedBytes;
    framePointerOffsetStack.push(currentOffset);
  }

  /* actually never used
  public int getCurrentOffset() {
    return framePointerOffsetStack.peek();
  }
  */

  public void requestNewOffset() {
    framePointerOffsetStack.push(0);
  }

  public void deleteCurrentOffset() {
    framePointerOffsetStack.pop();
  }
}
