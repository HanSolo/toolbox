package eu.hansolo.toolbox;

import java.util.Arrays;
import java.util.List;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public enum OperatingSystem {
    ALPINE_LINUX("Alpine Linux", "linux", LibCType.MUSL) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(OperatingSystem.LINUX); }
    },
    LINUX("Linux", "linux", LibCType.GLIBC) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    MACOS("Mac OS", "macos", LibCType.LIBC) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    WINDOWS("Windows", "windows", LibCType.C_STD_LIB) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    SOLARIS("Solaris", "solaris", LibCType.LIBC) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    QNX("QNX", "qnx", LibCType.LIBC) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    AIX("AIX", "aix", LibCType.LIBC) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    NONE("-", "", LibCType.NONE) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    },
    NOT_FOUND("", "", LibCType.NOT_FOUND) {
        @Override public List<OperatingSystem> getSynonyms() { return List.of(); }
    };

    private final String   uiString;
    private final String   apiString;
    private final LibCType libCType;


    OperatingSystem(final String uiString, final String apiString, final LibCType libCType) {
        this.uiString  = uiString;
        this.apiString = apiString;
        this.libCType  = libCType;
    }


    public String getUiString() { return uiString; }

    public String getApiString() { return apiString; }

    public OperatingSystem getDefault() { return OperatingSystem.NONE; }

    public OperatingSystem getNotFound() { return OperatingSystem.NOT_FOUND; }

    public OperatingSystem[] getAll() { return values(); }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(name()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("ui_string").append(QUOTES).append(COLON).append(QUOTES).append(uiString).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("api_string").append(QUOTES).append(COLON).append(QUOTES).append(apiString).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("lib_c_type").append(QUOTES).append(COLON).append(QUOTES).append(libCType.getApiString()).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE).toString();
    }

    public static OperatingSystem fromText(final String text) {
        if (null == text) { return NOT_FOUND; }
        switch (text) {
            case "-linux":
            case "linux":
            case "Linux":
            case "LINUX":
            case "unix":
            case "UNIX":
            case "Unix":
            case "-unix":
                return LINUX;
            case "-linux-musl":
            case "-linux_musl":
            case "Linux-Musl":
            case "linux-musl":
            case "Linux_Musl":
            case "LINUX_MUSL":
            case "linux_musl":
            case "alpine":
            case "ALPINE":
            case "Alpine":
            case "alpine-linux":
            case "ALPINE-LINUX":
            case "alpine_linux":
            case "Alpine_Linux":
            case "ALPINE_LINUX":
            case "Alpine Linux":
            case "alpine linux":
            case "ALPINE LINUX":
                return ALPINE_LINUX;
            case "-solaris":
            case "solaris":
            case "SOLARIS":
            case "Solaris":
                return SOLARIS;
            case "-qnx":
            case "qnx":
            case "QNX":
                return QNX;
            case"-aix":
            case "aix":
            case "AIX":
                return AIX;
            case "darwin":
            case "-darwin":
            case "-macosx":
            case "-MACOSX":
            case "MacOS":
            case "Mac OS":
            case "mac_os":
            case "Mac_OS":
            case "mac-os":
            case "Mac-OS":
            case "mac":
            case "MAC":
            case "macos":
            case "MACOS":
            case "osx":
            case "OSX":
            case "macosx":
            case "MACOSX":
            case "Mac OSX":
            case "mac osx":
                return MACOS;
            case "-win":
            case "windows":
            case "Windows":
            case "WINDOWS":
            case "win":
            case "Win":
            case "WIN":
                return WINDOWS;
            default:
                return NOT_FOUND;
        }
    }

    public LibCType getLibCType() { return libCType; }

    public static List<OperatingSystem> getAsList() { return Arrays.asList(values()); }

    public abstract List<OperatingSystem> getSynonyms();
}
