package eu.hansolo.toolbox.geom;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Rectangle {
    public double x;
    public double y;
    public double width;
    public double height;


    public Rectangle() { }
    public Rectangle(final double w, final double h) {
        this(0, 0, w, h);
    }
    public Rectangle(final double x, final double y, final double w, final double h) {
        set(x, y, w, h);
    }

    public double getX() { return x; }
    public void setX(final double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(final double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(final double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(final double height) { this.height = height; }

    public void set(final Rectangle rect) { set(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()); }
    public void set(final double x, final double y, final double w, final double h) {
        this.x      = x;
        this.y      = y;
        this.width  = w;
        this.height = h;
    }

    public double getCenterX() { return x + (width / 2); }
    public double getCenterY() { return y + (height / 2); }

    public boolean isEmpty() { return (Double.compare(width, 0) <= 0 || Double.compare(height, 0) <= 0); }

    public Bounds getBounds() { return new Bounds(x, y, x + width, y + height); }

    public boolean intersects(final double x, final double y, final double width, final double height) {
        return getBounds().intersects(x, y, width, height);
    }

    public Rectangle createIntersection(final Rectangle rect) {
        Rectangle dst = new Rectangle();
        Rectangle.intersect(Rectangle.this, rect, dst);
        return dst;
    }

    public Rectangle createUnion(final Rectangle rect) {
        Rectangle dst = new Rectangle();
        Rectangle.union(Rectangle.this, rect, dst);
        return dst;
    }

    public boolean contains(final double x, final double y) {
        return (Double.compare(x, this.x) >= 0 &&
                Double.compare(y, this.y) >= 0 &&
                Double.compare(x, (this.x + this.width)) <= 0 &&
                Double.compare(y, (this.y + this.height)) <= 0);
    }
    public boolean contains(double x, double y, double width, double height) {
        return (contains(x, y) &&
                contains(x + width, y) &&
                contains(x, y + height) &&
                contains(x + width, y + height));
    }

    public static void union(final Rectangle src1, final Rectangle src2, final Rectangle dst) {
        final Bounds bounds1 = src1.getBounds();
        final Bounds bounds2 = src2.getBounds();
        final double x1 = Math.min(bounds1.getMinX(), bounds2.getMinX());
        final double y1 = Math.min(bounds1.getMinY(), bounds2.getMinY());
        final double x2 = Math.max(bounds1.getMaxX(), bounds2.getMaxX());
        final double y2 = Math.max(bounds1.getMaxY(), bounds2.getMaxY());
        dst.set(x1, y1, x2 - x1, y2 - y1);
    }

    public static void intersect(final Rectangle src1, final Rectangle src2, final Rectangle dst) {
        final Bounds bounds1 = src1.getBounds();
        final Bounds bounds2 = src2.getBounds();
        final double x1 = Math.max(bounds1.getMinX(), bounds2.getMinX());
        final double y1 = Math.max(bounds1.getMinY(), bounds2.getMinY());
        final double x2 = Math.min(bounds1.getMaxX(), bounds2.getMaxX());
        final double y2 = Math.min(bounds1.getMaxY(), bounds2.getMaxY());
        dst.set(x1, y1, x2 - x1, y2 - y1);
    }

    public Rectangle copy() { return new Rectangle(x, y, width, height); }

    @Override public boolean equals(final Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) obj;
            return (Double.compare(x, rectangle.x) == 0 && Double.compare(y, rectangle.y) == 0 &&
                    Double.compare(width, rectangle.width) == 0 && Double.compare(height, rectangle.height) == 0);
        }
        return false;
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("x").append(QUOTES).append(COLON).append(x).append(COMMA)
                                  .append(QUOTES).append("y").append(QUOTES).append(COLON).append(y).append(COMMA)
                                  .append(QUOTES).append("w").append(QUOTES).append(COLON).append(width).append(COMMA)
                                  .append(QUOTES).append("h").append(QUOTES).append(COLON).append(height)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
