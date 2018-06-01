package yapl.lib;

public class IntType implements NonArrayType {

    private final Integer value;

    public IntType() {
        this(null);
    }

    public IntType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean isCompatibleWith(Type type) {
        if (type instanceof IntType) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Type type) {
        return isCompatibleWith(type);
    }

    public boolean isReadOnly() {
        return value != null;
    }
}
