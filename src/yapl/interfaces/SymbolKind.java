package yapl.interfaces;

/**
 * Created by Dominic on 28.04.2018.
 */
public enum SymbolKind {
    PROGRAM("program"),
    PROCEDURE("procedure"),
    VARIABLE("variable"),
    CONSTANT("constant"),
    TYPENAME("typename"),
    FIELD("field"),
    PARAMETER("parameter");

    private final String name;

    SymbolKind(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
