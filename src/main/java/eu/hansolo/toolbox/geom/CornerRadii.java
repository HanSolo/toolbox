package eu.hansolo.toolbox.geom;

import eu.hansolo.toolbox.Helper;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class CornerRadii {
    private double topLeft;
    private double topRight;
    private double bottomRight;
    private double bottomLeft;


    // ******************** Constructors **************************************
    public CornerRadii() {
        this(0, 0, 0, 0);
    }
    public CornerRadii(final double radius) {
        this(radius, radius, radius, radius);
    }
    public CornerRadii(final double topLeft, final double topRight,
                       final double bottomRight, final double bottomLeft) {
        this.topLeft     = topLeft;
        this.topRight    = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft  = bottomLeft;
    }


    // ******************** Methods *******************************************
    public double getTopLeft() { return topLeft; }
    public void setTopLeft(final double value) { topLeft = Helper.clamp(0, Double.MAX_VALUE, value); }

    public double getTopRight() { return topRight; }
    public void setTopRight(final double value) { topRight = Helper.clamp(0, Double.MAX_VALUE, value); }

    public double getBottomRight() { return bottomRight; }
    public void setBottomRight(final double value) { bottomRight = Helper.clamp(0, Double.MAX_VALUE, value); }

    public double getBottomLeft() { return bottomLeft; }
    public void setBottomLeft(final double value) { bottomLeft = Helper.clamp(0, Double.MAX_VALUE, value); }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("topLeft").append(QUOTES).append(COLON).append(getTopLeft()).append(COMMA)
                                  .append(QUOTES).append("topRight").append(QUOTES).append(COLON).append(getTopRight()).append(COMMA)
                                  .append(QUOTES).append("bottomRight").append(QUOTES).append(COLON).append(getBottomRight()).append(COMMA)
                                  .append(QUOTES).append("bottomLeft").append(QUOTES).append(COLON).append(getBottomLeft())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
