package yapl.lib;

/**
 * Created by Dominic on 28.04.2018.
 */
public interface Type {

    boolean isCompatibleWith(Type type);

    boolean equals(Type type);

    boolean isReadOnly();
}
