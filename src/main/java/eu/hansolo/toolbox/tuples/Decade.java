package eu.hansolo.toolbox.tuples;

import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Decade<A,B,C,D,E,F,G,H,I,J> implements Tuple {
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;
    private F f;
    private G g;
    private H h;
    private I i;
    private J j;


    // ******************** Constructors **************************************
    public Decade(final A a, final B b, final C c, final D d, final E e, final F f, final G g, final H h, final I i, final J j) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
    }


    // ******************** Methods *******************************************
    public A getA() { return a; }
    public void setA(final A a) { this.a = a; }

    public B getB() { return b; }
    public void setB(final B b) { this.b = b; }

    public C getC() { return c; }
    public void setC(final C c) { this.c = c; }

    public D getD() { return d; }
    public void setD(final D d) { this.d = d; }

    public E getE() { return e; }
    public void setE(final E e) { this.e = e; }

    public F getF() { return f; }
    public void setF(final F f) { this.f = f; }

    public G getG() { return g; }
    public void setG(final G g) { this.g = g; }

    public H getH() { return h; }
    public void setH(final H h) { this.h = h; }

    public I getI() { return i; }
    public void setI(final I i) { this.i = i; }

    public J getJ() { return j; }
    public void setJ(final J j) { this.j = j; }

    @Override public int size() { return 10; }

    @Override public Object getValueAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a; }
            case 1  -> { return this.b; }
            case 2  -> { return this.c; }
            case 3  -> { return this.d; }
            case 4  -> { return this.e; }
            case 5  -> { return this.f; }
            case 6  -> { return this.g; }
            case 7  -> { return this.h; }
            case 8  -> { return this.i; }
            case 9  -> { return this.j; }
            default -> { return null; }
        }
    }

    @Override public Class getTypeAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a.getClass(); }
            case 1  -> { return this.b.getClass(); }
            case 2  -> { return this.c.getClass(); }
            case 3  -> { return this.d.getClass(); }
            case 4  -> { return this.e.getClass(); }
            case 5  -> { return this.f.getClass(); }
            case 6  -> { return this.g.getClass(); }
            case 7  -> { return this.h.getClass(); }
            case 8  -> { return this.i.getClass(); }
            case 9  -> { return this.j.getClass(); }
            default -> { return null; }
        }
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("a").append(QUOTES).append(COLON).append(QUOTES).append(a).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("b").append(QUOTES).append(COLON).append(QUOTES).append(b).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("c").append(QUOTES).append(COLON).append(QUOTES).append(c).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("d").append(QUOTES).append(COLON).append(QUOTES).append(d).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("e").append(QUOTES).append(COLON).append(QUOTES).append(e).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("f").append(QUOTES).append(COLON).append(QUOTES).append(f).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("g").append(QUOTES).append(COLON).append(QUOTES).append(g).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("h").append(QUOTES).append(COLON).append(QUOTES).append(h).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("i").append(QUOTES).append(COLON).append(QUOTES).append(i).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("j").append(QUOTES).append(COLON).append(QUOTES).append(j).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Decade<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> decade = (Decade<?, ?, ?, ?, ?, ?, ?, ?, ?, ?>) o;
        return a.equals(decade.a) && b.equals(decade.b) && c.equals(decade.c) && d.equals(decade.d) && e.equals(decade.e) && f.equals(decade.f) && g.equals(decade.g) && h.equals(decade.h) && i.equals(decade.i) && j.equals(decade.j);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b, c, d, e, f, g, h, i, j);
    }
}