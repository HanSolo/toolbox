package eu.hansolo.toolbox.versioning;

public enum Comparison {
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    EQUAL("="),
    GREATER_THAN_OR_EQUAL(">="),
    GREATER_THAN(">"),
    RANGE_INCLUDING("..."),
    RANGE_EXCLUDING_TO("..<"),
    RANGE_EXCLUDING_FROM(">.."),
    RANGE_EXCLUDING(">.<");

    private final String operator;

    Comparison(final String operator) {
        this.operator = operator;
    }

    public String getOperator() { return operator; }

    public static Comparison fromText(final String text) {
        switch (text) {
            case "<"  : return LESS_THAN;
            case "<=" : return LESS_THAN_OR_EQUAL;
            case "="  : return EQUAL;
            case ">=" : return GREATER_THAN_OR_EQUAL;
            case ">"  : return GREATER_THAN;
            case "...": return RANGE_INCLUDING;
            case "..<": return RANGE_EXCLUDING_TO;
            case ">..": return RANGE_EXCLUDING_FROM;
            case ">.<": return RANGE_EXCLUDING;
            default   : return EQUAL;
        }
    }
}
