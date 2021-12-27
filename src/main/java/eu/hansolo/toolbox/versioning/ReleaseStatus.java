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


public enum ReleaseStatus implements Api {
    GA("General Access", "ga",""),
    EA("Early Access", "ea", "-ea"),
    NONE("-", "", ""),
    NOT_FOUND("", "", "");

    private final String uiString;
    private final String apiString;
    private final String preReleaseId;


    ReleaseStatus(final String uiString, final String apiString, final String preReleaseId) {
        this.uiString     = uiString;
        this.apiString    = apiString;
        this.preReleaseId = preReleaseId;
    }


    @Override public String getUiString() { return uiString; }

    @Override public String getApiString() { return apiString; }

    @Override public ReleaseStatus getDefault() { return ReleaseStatus.NONE; }

    @Override public ReleaseStatus getNotFound() { return ReleaseStatus.NOT_FOUND; }

    @Override public ReleaseStatus[] getAll() { return values(); }

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

    public String getPreReleaseId() { return preReleaseId; }

    public static ReleaseStatus fromText(final String text) {
        if (null == text) { return NOT_FOUND; }
        switch (text) {
            case "-ea":
            case "-EA":
            case "_ea":
            case "_EA":
            case "ea":
            case "EA":
            case "ea_":
            case "EA_":
                return EA;
            case "-ga":
            case "-GA":
            case "_ga":
            case "_GA":
            case "ga":
            case "GA":
            case "ga_":
            case "GA_":
                return GA;
            default:
                return NOT_FOUND;
        }
    }

    public static List<ReleaseStatus> getAsList() { return Arrays.asList(values()); }
}
