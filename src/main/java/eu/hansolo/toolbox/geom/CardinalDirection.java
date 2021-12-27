package eu.hansolo.toolbox.geom;

public enum CardinalDirection {
    N("North", 348.75, 11.25),
    NNE("North North-East", 11.25, 33.75),
    NE("North-East", 33.75, 56.25),
    ENE("East North-East", 56.25, 78.75),
    E("East", 78.75, 101.25),
    ESE("East South-East", 101.25, 123.75),
    SE("South-East", 123.75, 146.25),
    SSE("South South-East", 146.25, 168.75),
    S("South", 168.75, 191.25),
    SSW("South South-West", 191.25, 213.75),
    SW("South-West", 213.75, 236.25),
    WSW("West South-West", 236.25, 258.75),
    W("West", 258.75, 281.25),
    WNW("West North-West", 281.25, 303.75),
    NW("North-West", 303.75, 326.25),
    NNW("North North-West", 326.25, 348.75);

    public String direction;
    public double from;
    public double to;

    CardinalDirection(final String DIRECTION, final double FROM, final double TO) {
        direction = DIRECTION;
        from      = FROM;
        to        = TO;
    }
}
