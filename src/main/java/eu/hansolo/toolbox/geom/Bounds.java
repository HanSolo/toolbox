package eu.hansolo.toolbox.geom;

import eu.hansolo.toolbox.Helper;

import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Bounds {
    private double x;
    private double y;
    private double width;
    private double height;


    // ******************** Constructors **************************************
    public Bounds() {
        this(0, 0, 0, 0);
    }
    public Bounds(final double width, final double height) {
        this(0, 0, width, height);
    }
    public Bounds(final double x, final double y, final double width, final double height) {
        set(x, y, width, height);
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public void setX(final double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(final double y) { this.y = y; }

    public double getMinX() { return x; }
    public double getMaxX() { return x + width; }

    public double getMinY() { return y; }
    public double getMaxY() { return y + height; }

    public double getWidth() { return width; }
    public void setWidth(final double width) { this.width = Helper.clamp(0, Double.MAX_VALUE, width); }

    public double getHeight() { return height; }
    public void setHeight(final double height) { this.height = Helper.clamp(0, Double.MAX_VALUE, height); }

    public double getCenterX() { return x + width * 0.5; }
    public double getCenterY() { return y + height * 0.5; }

    public void set(final Bounds bounds) {
        set(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    public void set(final double x, final double y, final double width, final double height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    public boolean contains(final double x, final double y) {
        return (Double.compare(x, getMinX()) >= 0 && Double.compare(x, getMaxX()) <= 0 &&
                Double.compare(y, getMinY()) >= 0 && Double.compare(y, getMaxY()) <= 0);
    }

    public boolean intersects(final Bounds other) {
        return (other.getMaxX() >= getMinX() && other.getMaxY() >= getMinY() &&
                other.getMinX() <= getMaxX() && other.getMinY() <= getMaxY());
    }
    public boolean intersects(final double x, final double y, final double width, final double height) {
        return (x + width >= getMinX() && y + height >= getMinY() &&
                x <= getMaxX() && y <= getMaxY());
    }

    public Bounds copy() { return new Bounds(x, y, width, height); }

    @Override public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj instanceof Bounds) {
            Bounds other = (Bounds) obj;
            return getX() == other.getX() &&
                   getY() == other.getY() &&
                   getWidth() == other.getWidth() &&
                   getHeight() == other.getHeight();
        } else return false;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("x").append(QUOTES).append(COLON).append(getX()).append(COMMA)
                                  .append(QUOTES).append("y").append(QUOTES).append(COLON).append(getY()).append(COMMA)
                                  .append(QUOTES).append("w").append(QUOTES).append(COLON).append(getWidth()).append(COMMA)
                                  .append(QUOTES).append("h").append(QUOTES).append(COLON).append(getHeight())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
