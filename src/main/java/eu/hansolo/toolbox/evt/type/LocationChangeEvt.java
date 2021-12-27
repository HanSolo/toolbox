package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.geom.Location;


public class LocationChangeEvt extends ChangeEvt {
    public static final EvtType<LocationChangeEvt> ANY               = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<LocationChangeEvt> LOCATION_CHANGED  = new EvtType<>(LocationChangeEvt.ANY, "LOCATION_CHANGED");
    public static final EvtType<LocationChangeEvt> ALTITUDE_CHANGED  = new EvtType<>(LocationChangeEvt.ANY, "ALTITUDE_CHANGED");
    public static final EvtType<LocationChangeEvt> ACCURACY_CHANGED  = new EvtType<>(LocationChangeEvt.ANY, "ACCURACY_CHANGED");

    private final Location oldLocation;
    private final Location location;


    // ******************** Constructors **************************************
    public LocationChangeEvt(final EvtType<? extends LocationChangeEvt> evtType, final Location oldLocation, final Location location) {
        super(evtType);
        this.location    = location;
        this.oldLocation = oldLocation;
    }
    public LocationChangeEvt(final Object src, final EvtType<? extends LocationChangeEvt> evtType, final Location oldLocation, final Location location) {
        super(src, evtType);
        this.location    = location;
        this.oldLocation = oldLocation;
    }
    public LocationChangeEvt(final Object src, final EvtType<? extends LocationChangeEvt> evtType, final EvtPriority priority, final Location oldLocation, final Location location) {
        super(src, evtType, priority);
        this.location    = location;
        this.oldLocation = oldLocation;
    }


    // ******************** Methods *******************************************
    public EvtType<? extends LocationChangeEvt> getEvtType() { return (EvtType<? extends LocationChangeEvt>) super.getEvtType(); }

    public Location getOldLocation() { return oldLocation; }

    public Location getLocation() { return location; }
}
