package yapl.lib;

import yapl.interfaces.Symbol;

import java.util.ArrayList;
import java.util.List;

public class RecordType  implements NonArrayType {

    private String typeName;
    private List<Symbol> fields;

    public RecordType(String typeName) {
        this(typeName, new ArrayList<>());
    }

    public RecordType(String typeName, List<Symbol> fields) {
        this.typeName = typeName;
        this.fields = fields;
    }

    public void addField(Symbol symbol) {
        fields.add(symbol);
    }

    @Override
    public boolean isCompatibleWith(Type type) {
        if (!(type instanceof RecordType)) {
            return false;
        }
        RecordType other = (RecordType) type;

        if (fields.size() != other.fields.size()) {
            return false;
        }

        for (int i = 0; i < fields.size(); i++) {
            Symbol thisSymbol = fields.get(i);
            Symbol otherSymbol = other.fields.get(i);

            if (thisSymbol.getName().compareTo(otherSymbol.getName()) != 0) {
                return false;
            }

            if (thisSymbol.getType() instanceof RecordType) {
                RecordType thisRec = (RecordType) thisSymbol.getType();
                if (thisRec.typeName.compareTo(this.typeName) != 0) {
                    if (!thisSymbol.getType().isCompatibleWith(otherSymbol.getType())) {
                        return false;
                    }
                }
            } else {
                if (!thisSymbol.getType().isCompatibleWith(otherSymbol.getType())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean equals (Type type) {
        if (!(type instanceof RecordType)) {
            return false;
        }
        RecordType other = (RecordType) type;
        if (other.typeName.compareTo(typeName) != 0) {
            return false;
        }

        if (other.fields.size() != fields.size()) {
            return false;
        }

        for (int i = 0; i < fields.size(); i++) {
            Symbol thisCurrent = fields.get(i);
            Symbol otherCurrent = fields.get(i);

            if (thisCurrent.getName().compareTo(otherCurrent.getName()) != 0) {
                return false;
            }

            if (thisCurrent.getType() instanceof RecordType) {
                RecordType thisRec = (RecordType) thisCurrent.getType();
                if (thisRec.typeName.compareTo(this.typeName) != 0) {
                    if (!thisCurrent.getType().equals(otherCurrent.getType())) {
                        return false;
                    }
                }
            } else {
                if (!thisCurrent.getType().equals(otherCurrent.getType())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public Symbol getFieldByName(String name) {
        for (Symbol symbol : fields) {
            if (symbol.getName().compareTo(name) == 0) {
                return symbol;
            }
        }
        return null;
    }
}
