package eu.hansolo.toolbox.evt;

import java.util.EventObject;
import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Evt extends EventObject implements Comparable<Evt> {
    public    static final EvtType<Evt>           ANY = EvtType.ROOT;
    protected        final EvtType<? extends Evt> evtType;
    private          final EvtPriority            priority;


    // ******************** Constructors **************************************
    public Evt(final EvtType<? extends Evt> evtType) {
        this(null, evtType, EvtPriority.NORMAL);
    }
    public Evt(final Object source, final EvtType<? extends Evt> evtType) {
        this(source, evtType, EvtPriority.NORMAL);
    }
    public Evt(final Object source, final EvtType<? extends Evt> evtType, final EvtPriority priority) {
        super(source);
        this.evtType  = evtType;
        this.priority = priority;
    }


    // ******************** Methods *******************************************
    public Object getSource() { return source; }

    public EvtType<? extends Evt> getEvtType() {  return evtType; }

    public EvtPriority getPriority() { return priority; }

    public int compareTo(final Evt evt) {
        return (evt.getPriority().getValue() - this.priority.getValue());
    }

    @Override public int hashCode() {
        return Objects.hash(source, evtType, priority);
    }

    @Override public boolean equals(final Object obj) {
        if (this == obj) { return true; }
        if (null == obj) { return false; }
        if (this.getClass() != obj.getClass()) { return false; }
        Evt evt = (Evt) obj;
        return (evt.getEvtType().equals(this.getEvtType()) &&
                evt.getPriority().getValue() == this.getPriority().getValue() &&
                evt.getSource().equals(this.getSource()));
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("class").append(QUOTES).append(COLON).append(QUOTES).append(getClass().getName()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("type").append(QUOTES).append(COLON).append(QUOTES).append(getEvtType().getClass().getName()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("priority").append(QUOTES).append(COLON).append(getPriority().getValue()).append(COMMA)
                                  .append(QUOTES).append("source").append(QUOTES).append(COLON).append(QUOTES).append(null == getSource() ? "null" : getSource().getClass().getName()).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
