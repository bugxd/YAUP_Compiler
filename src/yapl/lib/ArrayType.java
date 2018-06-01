package yapl.lib;

public class ArrayType implements Type {

    private final NonArrayType elem;
    private final int dimension;

    public ArrayType(NonArrayType elem, int dimension) {
        this.elem = elem;

        assert dimension >= 1;
        this.dimension = dimension;
    }

    public NonArrayType elem() {
        return elem;
    }

    public int dim () {
        return dimension;
    }

    public Type subarray() {
        if (dimension == 1) {
            return elem;
        }

        return new ArrayType(elem, dimension - 1);
    }

    @Override
    public boolean isCompatibleWith(Type type) {
        if (!(type instanceof ArrayType)) {
            return false;
        }

        ArrayType other = (ArrayType) type;

        // TODO: IMPLEMENT PROPER EQUALS METHOD
        if (!elem.equals(other.elem)) {
            return false;
        }

        if (dimension != other.dimension) {
            return false;
        }

        return true;
    }

    public boolean equals(Type type) {
        return isCompatibleWith(type);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
