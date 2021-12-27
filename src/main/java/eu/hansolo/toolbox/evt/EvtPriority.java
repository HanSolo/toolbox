package eu.hansolo.toolbox.evt;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public enum EvtPriority {
    LOW(0), NORMAL(1), HIGH(2);

    private int value;


    // ******************** Constructor ***************************************
    EvtPriority(final int value) {
        this.value = value;
    }


    // ******************** Methods *******************************************
    public int getValue() { return value; }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("class").append(QUOTES).append(COLON).append(QUOTES).append(getClass().getName()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("value").append(QUOTES).append(COLON).append(QUOTES).append(getValue())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
