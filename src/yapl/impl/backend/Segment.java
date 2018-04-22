package yapl.impl.backend;

public final class Segment {

  private final String name;

  public static final Segment DATA = new Segment(".data");
  public static final Segment TEXT = new Segment(".text");

  private Segment(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
