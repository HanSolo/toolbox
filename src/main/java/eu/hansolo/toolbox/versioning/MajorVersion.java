package eu.hansolo.toolbox.versioning;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class MajorVersion {
    private final int           majorVersion;
    private       ReleaseStatus releaseStatus;
    private       TermOfSupport termOfSupport;
    private       boolean       maintained;


    // ******************** Constructors **************************************
    public MajorVersion(final int majorVersion) {
        this(majorVersion, ReleaseStatus.GA, TermOfSupport.NONE, true);
    }
    public MajorVersion(final int majorVersion, final TermOfSupport termOfSupport) {
        this(majorVersion, ReleaseStatus.GA, termOfSupport, true);
    }
    public MajorVersion(final int majorVersion, final ReleaseStatus releaseStatus, final TermOfSupport termOfSupport, final boolean maintained) {
        if (majorVersion <= 0) { throw new IllegalArgumentException("Major version cannot be <= 0"); }
        this.majorVersion  = majorVersion;
        this.releaseStatus = releaseStatus;
        this.termOfSupport = termOfSupport;
        this.maintained    = maintained;
    }


    // ******************** Methods *******************************************
    public int getAsInt() { return majorVersion; }

    public ReleaseStatus getReleaseStatus() { return releaseStatus; }
    public void setReleaseStatus(final ReleaseStatus releaseStatus) { this.releaseStatus = releaseStatus; }

    public TermOfSupport getTermOfSupport() { return termOfSupport; }
    public void setTermOfSupport(final TermOfSupport termOfSupport) {
        this.termOfSupport = termOfSupport;
    }

    public boolean isMaintained() { return maintained; }
    public void setMaintained(final boolean maintained) { this.maintained = maintained; }

    public VersionNumber getVersionNumber() { return new VersionNumber(majorVersion); }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("major_version").append(QUOTES).append(COLON).append(majorVersion).append(COMMA)
                                  .append(QUOTES).append("term_of_support").append(QUOTES).append(COLON).append(QUOTES).append(termOfSupport.name()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("maintained").append(QUOTES).append(COLON).append(isMaintained()).append(COMMA)
                                  .append(QUOTES).append("release_status").append(QUOTES).append(COLON).append(QUOTES).append(getReleaseStatus().getApiString()).append(QUOTES).append(COMMA)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
