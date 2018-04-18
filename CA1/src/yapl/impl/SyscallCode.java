package yapl.impl;

/**
 * Created by Dominic on 06.04.2018.
 */
public enum SyscallCode {
    PRINT_INT(1),
    PRINT_FLOAT(2),
    PRINT_DOUBLE(3),
    PRINT_STRING(4),
    READ_INT(5),
    READ_FLOAT(6),
    READ_DOUBLE(7),
    READ_STRING(8),
    ALLOCATE_HEAP(9),
    EXIT(10);

    private final int value;

    private SyscallCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /* actually never used
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    */
}
