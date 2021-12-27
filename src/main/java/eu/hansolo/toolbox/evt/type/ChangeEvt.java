package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;


public class ChangeEvt extends Evt {
    public static final EvtType<ChangeEvt> ANY = new EvtType<>(Evt.ANY, "CHANGE_EVT");


    // ******************** Constructors **************************************
    public ChangeEvt(final EvtType<? extends ChangeEvt> evtType) {
        super(evtType);
    }
    public ChangeEvt(final Object src, final EvtType<? extends ChangeEvt> evtType) {
        super(src, evtType);
    }
    public ChangeEvt(final Object src, final EvtType<? extends ChangeEvt> evtType, final EvtPriority priority) {
        super(src, evtType, priority);
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends ChangeEvt> getEvtType() {
        return (EvtType<? extends ChangeEvt>) super.getEvtType();
    }
}
