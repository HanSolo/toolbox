package eu.hansolo.toolbox;

import java.util.Arrays;
import java.util.List;


public enum Architecture {
    AARCH32("AARCH32", "aarch32", Bitness.BIT_32), ARM("ARM", "arm", Bitness.BIT_32), ARMEL("ARMEL", "armel", Bitness.BIT_32), ARMHF("ARMHF", "armhf", Bitness.BIT_32), AARCH64("AARCH64", "aarch64", Bitness.BIT_64),
    ARM64("ARM64", "arm64", Bitness.BIT_64), MIPSEL("MIPS EL", "mipsel", Bitness.BIT_32), MIPS("MIPS", "mips", Bitness.BIT_32), PPC("Power PC", "ppc", Bitness.BIT_32), PPC64("PPC64", "ppc64", Bitness.BIT_64),
    PPC64LE("PPC64LE", "ppc64le", Bitness.BIT_64), RISCV64("RISCv64", "riscv64", Bitness.BIT_64), S390X("S390X", "s390x", Bitness.BIT_64), SPARC("Sparc", "sparc", Bitness.BIT_32), SPARCV9("Sparc V9", "sparcv9", Bitness.BIT_64),
    X64("X64", "x64", Bitness.BIT_64), X86("X86", "x86", Bitness.BIT_32), AMD64("AMD64", "amd64", Bitness.BIT_64), IA64("IA-64", "ia64", Bitness.BIT_64), NONE("-", "", Bitness.NONE), NOT_FOUND("", "", Bitness.NOT_FOUND);

    private final String  uiString;
    private final String  apiString;
    private final Bitness bitness;

    private Architecture(String uiString, String apiString, Bitness bitness) {
        this.uiString  = uiString;
        this.apiString = apiString;
        this.bitness   = bitness;
    }

    public String getUiString() {
        return this.uiString;
    }

    public String getApiString() {
        return this.apiString;
    }

    public Architecture getDefault() {
        return NONE;
    }

    public Architecture getNotFound() {
        return NOT_FOUND;
    }

    public Architecture[] getAll() {
        return values();
    }

    public static Architecture fromText(String text) {
        if (null == text) {
            return NOT_FOUND;
        } else {
            byte var2 = -1;
            switch (text.hashCode()) {
                case -2020640378:
                    if (text.equals("MIPSEL")) {
                        var2 = 26;
                    }
                    break;
                case -2011717608:
                    if (text.equals("sparcv9")) {
                        var2 = 44;
                    }
                    break;
                case -1722231147:
                    if (text.equals("X86-64")) {
                        var2 = 49;
                    }
                    break;
                case -1722183097:
                    if (text.equals("X86_64")) {
                        var2 = 51;
                    }
                    break;
                case -1482271369:
                    if (text.equals("_amd64")) {
                        var2 = 4;
                    }
                    break;
                case -1409295825:
                    if (text.equals("armv7l")) {
                        var2 = 11;
                    }
                    break;
                case -1293734888:
                    if (text.equals("SPARCV9")) {
                        var2 = 45;
                    }
                    break;
                case -1221096234:
                    if (text.equals("aarch32")) {
                        var2 = 5;
                    }
                    break;
                case -1221096139:
                    if (text.equals("aarch64")) {
                        var2 = 0;
                    }
                    break;
                case -1073969786:
                    if (text.equals("mipsel")) {
                        var2 = 25;
                    }
                    break;
                case -806098410:
                    if (text.equals("x86-32")) {
                        var2 = 58;
                    }
                    break;
                case -806098315:
                    if (text.equals("x86-64")) {
                        var2 = 48;
                    }
                    break;
                case -806050265:
                    if (text.equals("x86_64")) {
                        var2 = 50;
                    }
                    break;
                case -503112522:
                    if (text.equals("AARCH32")) {
                        var2 = 6;
                    }
                    break;
                case -503112427:
                    if (text.equals("AARCH64")) {
                        var2 = 1;
                    }
                    break;
                case -379247416:
                    if (text.equals("ppc64el")) {
                        var2 = 31;
                    }
                    break;
                case -379247206:
                    if (text.equals("ppc64le")) {
                        var2 = 33;
                    }
                    break;
                case 65084:
                    if (text.equals("ARM")) {
                        var2 = 16;
                    }
                    break;
                case 79427:
                    if (text.equals("PPC")) {
                        var2 = 30;
                    }
                    break;
                case 86294:
                    if (text.equals("X64")) {
                        var2 = 47;
                    }
                    break;
                case 86358:
                    if (text.equals("X86")) {
                        var2 = 53;
                    }
                    break;
                case 96860:
                    if (text.equals("arm")) {
                        var2 = 15;
                    }
                    break;
                case 111203:
                    if (text.equals("ppc")) {
                        var2 = 29;
                    }
                    break;
                case 117046:
                    if (text.equals("x64")) {
                        var2 = 46;
                    }
                    break;
                case 117110:
                    if (text.equals("x86")) {
                        var2 = 52;
                    }
                    break;
                case 2238934:
                    if (text.equals("IA64")) {
                        var2 = 60;
                    }
                    break;
                case 2366623:
                    if (text.equals("MIPS")) {
                        var2 = 28;
                    }
                    break;
                case 3178856:
                    if (text.equals("i386")) {
                        var2 = 54;
                    }
                    break;
                case 3179817:
                    if (text.equals("i486")) {
                        var2 = 55;
                    }
                    break;
                case 3180778:
                    if (text.equals("i586")) {
                        var2 = 56;
                    }
                    break;
                case 3181739:
                    if (text.equals("i686")) {
                        var2 = 57;
                    }
                    break;
                case 3222998:
                    if (text.equals("ia64")) {
                        var2 = 59;
                    }
                    break;
                case 3351711:
                    if (text.equals("mips")) {
                        var2 = 27;
                    }
                    break;
                case 3476791:
                    if (text.equals("s390")) {
                        var2 = 39;
                    }
                    break;
                case 62389846:
                    if (text.equals("AMD64")) {
                        var2 = 3;
                    }
                    break;
                case 62547355:
                    if (text.equals("ARM32")) {
                        var2 = 8;
                    }
                    break;
                case 62547450:
                    if (text.equals("ARM64")) {
                        var2 = 22;
                    }
                    break;
                case 62547939:
                    if (text.equals("ARMEL")) {
                        var2 = 18;
                    }
                    break;
                case 62548026:
                    if (text.equals("ARMHF")) {
                        var2 = 20;
                    }
                    break;
                case 62548444:
                    if (text.equals("ARMV6")) {
                        var2 = 10;
                    }
                    break;
                case 62548445:
                    if (text.equals("ARMV7")) {
                        var2 = 14;
                    }
                    break;
                case 62548446:
                    if (text.equals("ARMV8")) {
                        var2 = 24;
                    }
                    break;
                case 69398419:
                    if (text.equals("IA-64")) {
                        var2 = 62;
                    }
                    break;
                case 76331073:
                    if (text.equals("PPC64")) {
                        var2 = 36;
                    }
                    break;
                case 78227937:
                    if (text.equals("S390X")) {
                        var2 = 41;
                    }
                    break;
                case 79100597:
                    if (text.equals("SPARC")) {
                        var2 = 43;
                    }
                    break;
                case 92926582:
                    if (text.equals("amd64")) {
                        var2 = 2;
                    }
                    break;
                case 93084091:
                    if (text.equals("arm32")) {
                        var2 = 7;
                    }
                    break;
                case 93084186:
                    if (text.equals("arm64")) {
                        var2 = 21;
                    }
                    break;
                case 93085699:
                    if (text.equals("armel")) {
                        var2 = 17;
                    }
                    break;
                case 93085786:
                    if (text.equals("armhf")) {
                        var2 = 19;
                    }
                    break;
                case 93086172:
                    if (text.equals("armv6")) {
                        var2 = 9;
                    }
                    break;
                case 93086173:
                    if (text.equals("armv7")) {
                        var2 = 13;
                    }
                    break;
                case 93086174:
                    if (text.equals("armv8")) {
                        var2 = 23;
                    }
                    break;
                case 99904403:
                    if (text.equals("ia-64")) {
                        var2 = 61;
                    }
                    break;
                case 106867809:
                    if (text.equals("ppc64")) {
                        var2 = 35;
                    }
                    break;
                case 107780641:
                    if (text.equals("s390x")) {
                        var2 = 40;
                    }
                    break;
                case 109638357:
                    if (text.equals("sparc")) {
                        var2 = 42;
                    }
                    break;
                case 339719336:
                    if (text.equals("PPC64EL")) {
                        var2 = 32;
                    }
                    break;
                case 339719546:
                    if (text.equals("PPC64LE")) {
                        var2 = 34;
                    }
                    break;
                case 1211534733:
                    if (text.equals("riscv64")) {
                        var2 = 37;
                    }
                    break;
                case 1929518445:
                    if (text.equals("RISCV64")) {
                        var2 = 38;
                    }
                    break;
                case 1939001871:
                    if (text.equals("ARMV7L")) {
                        var2 = 12;
                    }
            }

            switch (var2) {
                case 0:
                case 1:
                    return AARCH64;
                case 2:
                case 3:
                case 4:
                    return AMD64;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                    return ARM;
                case 17:
                case 18:
                    return ARMEL;
                case 19:
                case 20:
                    return ARMHF;
                case 21:
                case 22:
                case 23:
                case 24:
                    return ARM64;
                case 25:
                case 26:
                    return MIPSEL;
                case 27:
                case 28:
                    return MIPS;
                case 29:
                case 30:
                    return PPC;
                case 31:
                case 32:
                case 33:
                case 34:
                    return PPC64LE;
                case 35:
                case 36:
                    return PPC64;
                case 37:
                case 38:
                    return RISCV64;
                case 39:
                case 40:
                case 41:
                    return S390X;
                case 42:
                case 43:
                    return SPARC;
                case 44:
                case 45:
                    return SPARCV9;
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                    return X64;
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                    return X86;
                case 59:
                case 60:
                case 61:
                case 62:
                    return IA64;
                default:
                    return NOT_FOUND;
            }
        }
    }

    public Bitness getBitness() {
        return this.bitness;
    }

    public static List<Architecture> getAsList() {
        return Arrays.asList(values());
    }

    public String toString() {
        return this.uiString;
    }
}
