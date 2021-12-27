package eu.hansolo.toolbox.versioning;

import java.util.function.Predicate;


public class SemVerParsingResult {
    private Semver            semVer1;
    private Error             error1;
    private Semver            semVer2;
    private Error             error2;
    private Predicate<Semver> filter;


    // ******************** Constructors **************************************
    public SemVerParsingResult() {
        semVer1 = null;
        error1  = null;
        semVer2 = null;
        error2  = null;
        filter  = null;
    }


    // ******************** Methods *******************************************
    public Semver getSemVer1()                            { return semVer1; }
    public void setSemVer1(final Semver semVer)           { semVer1 = semVer; }

    public Error getError1() { return error1; }
    public void setError1(final Error error) { error1 = error; }

    public Semver getSemVer2()                            { return semVer2; }
    public void setSemVer2(final Semver semVer)           { semVer2 = semVer; }

    public Error getError2() { return error2; }
    public void setError2(final Error error) { error2 = error; }

    public Predicate<Semver> getFilter()                  { return filter; }
    public void setFilter(final Predicate<Semver> filter) { this.filter = filter; }
}
