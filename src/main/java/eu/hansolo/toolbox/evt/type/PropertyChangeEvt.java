package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;


public class PropertyChangeEvt<T> extends ChangeEvt {
    public static final EvtType<PropertyChangeEvt> ANY     = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<PropertyChangeEvt> CHANGED = new EvtType<>(PropertyChangeEvt.ANY, "CHANGED");

    private final T oldValue;
    private final T value;


    // ******************** Constructors **************************************
    public PropertyChangeEvt(final EvtType<? extends PropertyChangeEvt<T>> evtType, final T oldValue, final T value) {
        super(evtType);
        this.value    = value;
        this.oldValue = oldValue;
    }
    public PropertyChangeEvt(final ReadOnlyProperty src, final EvtType<? extends PropertyChangeEvt<T>> evtType, final T oldValue, final T value) {
        super(src, evtType);
        this.value    = value;
        this.oldValue = oldValue;
    }
    public PropertyChangeEvt(final ReadOnlyProperty src, final EvtType<? extends PropertyChangeEvt<T>> evtType, final EvtPriority priority, final T oldValue, final T value) {
        super(src, evtType, priority);
        this.value    = value;
        this.oldValue = oldValue;
    }


    // ******************** Methods *******************************************
    public EvtType<? extends PropertyChangeEvt<T>> getEvtType() { return (EvtType<? extends PropertyChangeEvt<T>>) super.getEvtType(); }

    public T getOldValue() { return oldValue; }

    public T getValue() { return value; }
}
