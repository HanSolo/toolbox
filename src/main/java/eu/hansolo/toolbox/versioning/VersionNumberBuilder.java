package eu.hansolo.toolbox.versioning;

public class VersionNumberBuilder {
    private VersionNumber versionNumber;


    protected VersionNumberBuilder(final Integer featureNumber) {
        versionNumber = new VersionNumber(featureNumber);
    }


    public static VersionNumberBuilder create(final Integer featureNumber) throws IllegalArgumentException {
        if (null == featureNumber) { throw new IllegalArgumentException("Feature version cannot be null"); }
        if (0 >= featureNumber) { throw new IllegalArgumentException("Feature version cannot be smaller than 0 (" + featureNumber + ")"); }
        return new VersionNumberBuilder(featureNumber);
    }

    public VersionNumberBuilder interimNumber(final Integer interimNumber) throws IllegalArgumentException {
        if (null != interimNumber && 0 > interimNumber) { throw new IllegalArgumentException("Interim version cannot be smaller than 0"); }
        versionNumber.setInterim(interimNumber);
        return this;
    }

    public VersionNumberBuilder updateNumber(final Integer updateNumber) throws IllegalArgumentException {
        if (null != updateNumber && 0 > updateNumber) { throw new IllegalArgumentException("Update version cannot be smaller than 0"); }
        versionNumber.setUpdate(updateNumber);
        return this;
    }

    public VersionNumberBuilder patchNumber(final Integer patchNumber) throws IllegalArgumentException {
        if (null != patchNumber && 0 > patchNumber) { throw new IllegalArgumentException("Patch version cannot be smaller than 0"); }
        versionNumber.setPatch(patchNumber);
        return this;
    }

    public VersionNumberBuilder fifthNumber(final Integer fifthNumber) throws IllegalArgumentException {
        if (null != fifthNumber && 0 > fifthNumber) { throw new IllegalArgumentException("Fifth version cannot be smaller than 0"); }
        versionNumber.setFifth(fifthNumber);
        return this;
    }

    public VersionNumberBuilder sixthNumber(final Integer sixthNumber) throws IllegalArgumentException {
        if (null != sixthNumber && 0 > sixthNumber) { throw new IllegalArgumentException("Sixth version cannot be smaller than 0"); }
        versionNumber.setSixth(sixthNumber);
        return this;
    }

    public VersionNumberBuilder buildNumber(final Integer buildNumber) throws IllegalArgumentException {
        if (null != buildNumber && 0 > buildNumber) { throw new IllegalArgumentException("Build version cannot be smaller than 0"); }
        versionNumber.setBuild(buildNumber);
        return this;
    }

    public VersionNumberBuilder releaseStatus(final ReleaseStatus releaseStatus) {
        if (null == releaseStatus) { throw new IllegalArgumentException("Release status cannot be null"); }
        versionNumber.setReleaseStatus(releaseStatus);
        return this;
    }

    public VersionNumber build() {
        return versionNumber;
    }
}
