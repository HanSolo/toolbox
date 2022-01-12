package eu.hansolo.toolbox;

import java.util.Arrays;
import java.util.List;


public enum Bitness {
    BIT_32("32 Bit", "32", 32), BIT_64("64 Bit", "64", 64), NONE("-", "", 0), NOT_FOUND("", "", 0);

    private final String uiString;
    private final String apiString;
    private final int    bits;

    private Bitness(String uiString, String apiString, int bits) {
        this.uiString  = uiString;
        this.apiString = apiString;
        this.bits      = bits;
    }

    public String getUiString() {
        return this.uiString;
    }

    public String getApiString() {
        return this.apiString;
    }

    public Bitness getDefault() {
        return NONE;
    }

    public Bitness getNotFound() {
        return NOT_FOUND;
    }

    public Bitness[] getAll() {
        return values();
    }

    public int getAsInt() {
        return this.bits;
    }

    public String getAsString() {
        return Integer.toString(this.bits);
    }

    public static Bitness fromText(String text) {
        if (null == text) {
            return NOT_FOUND;
        } else {
            byte var2 = -1;
            switch (text.hashCode()) {
                case 1631:
                    if (text.equals("32")) {
                        var2 = 0;
                    }
                    break;
                case 1726:
                    if (text.equals("64")) {
                        var2 = 4;
                    }
                    break;
                case 48654894:
                    if (text.equals("32BIT")) {
                        var2 = 3;
                    }
                    break;
                case 48655918:
                    if (text.equals("32Bit")) {
                        var2 = 2;
                    }
                    break;
                case 48686670:
                    if (text.equals("32bit")) {
                        var2 = 1;
                    }
                    break;
                case 51485039:
                    if (text.equals("64BIT")) {
                        var2 = 7;
                    }
                    break;
                case 51486063:
                    if (text.equals("64Bit")) {
                        var2 = 6;
                    }
                    break;
                case 51516815:
                    if (text.equals("64bit")) {
                        var2 = 5;
                    }
            }

            switch (var2) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return BIT_32;
                case 4:
                case 5:
                case 6:
                case 7:
                    return BIT_64;
                default:
                    return NOT_FOUND;
            }
        }
    }

    public static Bitness fromInt(Integer bits) {
        switch (bits) {
            case 32:
                return BIT_32;
            case 64:
                return BIT_64;
            default:
                return NOT_FOUND;
        }
    }

    public static List<Bitness> getAsList() {
        return Arrays.asList(values());
    }

    public String toString() {
        return this.uiString;
    }
}
