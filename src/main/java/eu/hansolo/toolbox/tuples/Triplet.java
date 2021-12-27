package eu.hansolo.toolbox.tuples;

import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Triplet<A,B,C> implements Tuple {
    private A a;
    private B b;
    private C c;


    // ******************** Constructors **************************************
    public Triplet(final A a, final B b, final C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    // ******************** Methods *******************************************
    public A getA() { return a; }
    public void setA(final A a) { this.a = a; }

    public B getB() { return b; }
    public void setB(final B b) { this.b = b; }

    public C getC() { return c; }
    public void setC(final C c) { this.c = c; }

    @Override public int size() { return 3; }

    @Override public Object getValueAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return a;    }
            case 1  -> { return b;    }
            case 2  -> { return c;    }
            default -> { return null; }
        }
    }

    @Override public Class getTypeAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return a.getClass(); }
            case 1  -> { return b.getClass(); }
            case 2  -> { return c.getClass(); }
            default -> { return null; }
        }
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("a").append(QUOTES).append(COLON).append(QUOTES).append(a).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("b").append(QUOTES).append(COLON).append(QUOTES).append(b).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("c").append(QUOTES).append(COLON).append(QUOTES).append(c).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return a.equals(triplet.a) && b.equals(triplet.b) && c.equals(triplet.c);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b, c);
    }
}
