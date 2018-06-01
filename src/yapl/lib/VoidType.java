package yapl.lib;

public class VoidType implements Type {
    @Override
    public boolean isCompatibleWith(Type type) {
        if (type instanceof VoidType) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Type type) {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
