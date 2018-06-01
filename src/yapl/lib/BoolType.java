package yapl.lib;

public class BoolType  implements NonArrayType  {

    private final Boolean value;

    public BoolType() {
        this(null);
    }
    public BoolType(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public boolean isCompatibleWith(Type type) {
        if (type instanceof BoolType) {
            return true;
        }
        return false;
    }

    public boolean equals(Type type) {
        return isCompatibleWith(type);
    }

    public boolean isReadOnly() {
        return value != null;
    }
}
