package eu.hansolo.toolbox;

import java.util.Arrays;
import java.util.List;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public enum LibCType {
    GLIBC("glibc", "glibc"),
    MUSL("musl", "musl"),
    LIBC("libc", "libc"),
    C_STD_LIB("c std. lib", "c_std_lib"),
    NONE("-", ""),
    NOT_FOUND("", "");

    private final String uiString;
    private final String apiString;


    LibCType(final String uiString, final String apiString) {
        this.uiString  = uiString;
        this.apiString = apiString;
    }


    public String getUiString() { return uiString; }

    public String getApiString() { return apiString; }

    public LibCType getDefault() { return LibCType.NONE; }

    public LibCType getNotFound() { return LibCType.NOT_FOUND; }

    public LibCType[] getAll() { return values(); }

    public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(name()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("ui_string").append(QUOTES).append(COLON).append(QUOTES).append(uiString).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("api_string").append(QUOTES).append(COLON).append(QUOTES).append(apiString).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE).toString();
    }

    public static LibCType fromText(final String text) {
        if (null == text) { return NOT_FOUND; }
        switch (text) {
            case "musl":
            case "MUSL":
            case "linux_musl":
            case "linux-musl":
            case "alpine_linux":
            case "alpine":
            case "alpine-linux":
                return MUSL;
            case "glibc":
            case "GLIBC":
            case "linux":
            case "Linux":
            case "LINUX":
                return GLIBC;
            case "c_std_lib":
            case "C_STD_LIB":
            case "c-std-lib":
            case "C-STD-LIB":
            case "windows":
            case "Windows":
            case "win":
            case "Win":
                return C_STD_LIB;
            case "libc":
            case "LIBC":
            case "macos":
            case "MACOS":
            case "macosx":
            case "MACOSX":
            case "aix":
            case "AIX":
            case "qnx":
            case "QNX":
            case "solaris":
            case "SOLARIS":
            case "darwin":
            case "DARWIN":
                return LIBC;
            default:
                return NOT_FOUND;
        }
    }

    public static List<LibCType> getAsList() { return Arrays.asList(values()); }
}
