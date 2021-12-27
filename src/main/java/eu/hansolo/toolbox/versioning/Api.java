package eu.hansolo.toolbox.versioning;

public interface Api {
    String getUiString();

    String getApiString();

    Api getDefault();

    Api getNotFound();

    Api[] getAll();

    String toString(OutputFormat outputFormat);

    static Api fromText(final String text) { return null; }
}
