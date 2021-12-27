package eu.hansolo.toolbox.versioning;

import java.util.Arrays;
import java.util.List;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.COMMA_NEW_LINE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.INDENTED_QUOTES;
import static eu.hansolo.toolbox.Constants.NEW_LINE;
import static eu.hansolo.toolbox.Constants.QUOTES;


public enum TermOfSupport implements Api {
    STS("short term stable", "sts"),
    MTS("mid term stable", "mts"),
    LTS("long term stable", "lts"),
    NONE("-", ""),
    NOT_FOUND("", "");

    private final String uiString;
    private final String apiString;


    // ******************** Constructors **************************************
    TermOfSupport(final String uiString, final String apiString) {
        this.uiString = uiString;
        this.apiString = apiString;
    }


    // ******************** Methods *******************************************
    @Override public String getUiString() { return uiString; }

    @Override public String getApiString() { return apiString; }

    @Override public TermOfSupport getDefault() { return TermOfSupport.NONE; }

    @Override public TermOfSupport getNotFound() { return TermOfSupport.NOT_FOUND; }

    @Override public TermOfSupport[] getAll() { return values(); }

    @Override public String toString(final OutputFormat outputFormat) {
        StringBuilder msgBuilder = new StringBuilder();
        switch(outputFormat) {
            case FULL, REDUCED, REDUCED_ENRICHED -> {
                msgBuilder.append(CURLY_BRACKET_OPEN).append(NEW_LINE)
                          .append(INDENTED_QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(name()).append(QUOTES).append(COMMA_NEW_LINE)
                          .append(INDENTED_QUOTES).append("ui_string").append(QUOTES).append(COLON).append(QUOTES).append(uiString).append(QUOTES).append(COMMA_NEW_LINE)
                          .append(INDENTED_QUOTES).append("api_string").append(QUOTES).append(COLON).append(QUOTES).append(apiString).append(QUOTES).append(NEW_LINE)
                          .append(CURLY_BRACKET_CLOSE);
            }
            case FULL_COMPRESSED, REDUCED_COMPRESSED, REDUCED_ENRICHED_COMPRESSED -> {
                msgBuilder.append(CURLY_BRACKET_OPEN)
                          .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(name()).append(QUOTES).append(COMMA)
                          .append(QUOTES).append("ui_string").append(QUOTES).append(COLON).append(QUOTES).append(uiString).append(QUOTES).append(COMMA)
                          .append(QUOTES).append("api_string").append(QUOTES).append(COLON).append(QUOTES).append(apiString).append(QUOTES)
                          .append(CURLY_BRACKET_CLOSE);
            }
        }
        return msgBuilder.toString();
    }

    @Override public String toString() { return toString(OutputFormat.FULL_COMPRESSED); }

    public static TermOfSupport fromText(final String text) {
        if (null == text) { return NOT_FOUND; }
        switch(text) {
            case "long_term_stable":
            case "LongTermStable":
            case "lts":
            case "LTS":
            case "Lts":
                return LTS;
            case "mid_term_stable":
            case "MidTermStable":
            case "mts":
            case "MTS":
            case "Mts":
                return MTS;
            case "short_term_stable":
            case "ShortTermStable":
            case "sts":
            case "STS":
            case "Sts":
                return STS;
            default: return NOT_FOUND;

        }
    }

    public static List<TermOfSupport> getAsList() { return Arrays.asList(values()); }
}
