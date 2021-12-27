package eu.hansolo.toolbox.geom;

import eu.hansolo.toolbox.Constants;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.LocationChangeEvt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Location {
    private final String                                             id;
    private       Instant                                            timestamp;
    private       double                                             latitude;
    private       double                                             longitude;
    private       double                                             altitude;
    private       double                                             accuracy;
    private       String                                             name;
    private       String                                             info;
    private       Map<EvtType, List<EvtObserver<LocationChangeEvt>>> observers;


    // ******************** Constructors **************************************
    public Location (final double latitude, final double longitude) {
        this(Instant.now(), latitude, longitude, 0, 1, "", "");
    }
    public Location(final Instant timestamp, final double latitude, final double longitude, final double altitude, final double accuracy, final String name, final String info) {
        this.id        = UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.accuracy  = accuracy;
        this.name      = name;
        this.info      = info;
        this.observers = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public String getId() { return id; }

    public Instant getTimestamp() { return timestamp; }
    public long getTimestampInSeconds() { return timestamp.getEpochSecond(); }
    public void setTimestamp(final Instant timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(final double latitude) {
        final Location oldLocation = getCopy();
        this.latitude        = latitude;
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(final double longitude) {
        final Location oldLocation = getCopy();
        this.longitude = longitude;
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getAltitude() { return altitude; }
    public void setAltitude(final double altitude) {
        final Location oldLocation = getCopy();
        this.altitude = altitude;
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(final double accuracy) { this.accuracy = accuracy; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getInfo() { return info; }
    public void setInfo(final String info) { this.info = info; }

    public LocalDateTime getLocaleDateTime() { return getLocalDateTime(ZoneId.systemDefault()); }
    public LocalDateTime getLocalDateTime(final ZoneId ZONE_ID) { return LocalDateTime.ofInstant(timestamp, ZONE_ID); }

    // longitude -> x and latitude -> y
    public Point getAsPoint() { return new Point(longitude, latitude); }

    public void set(final double latitude, final double longitude) {
        final Location oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        timestamp      = Instant.now();
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp) {
        final Location oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp, final double accuracy, final String info) {
        final Location oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        this.accuracy  = accuracy;
        this.info      = info;
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final Location location) {
        final Location oldLocation = getCopy();
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        altitude  = location.getAltitude();
        timestamp = location.getTimestamp();
        accuracy  = location.getAccuracy();
        name      = location.getName();
        info      = location.getInfo();
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getDistanceTo(final Location location) { return calcDistanceInMeter(this, location); }

    public boolean isWithinRangeOf(final Location location, final double meters) { return getDistanceTo(location) < meters; }

    public static double calcDistanceInMeter(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
    }
    public static double calcDistanceInKilometer(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1, p2) / 1000.0;
    }
    public static double calcDistanceInMeter(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double lat1Radians     = Math.toRadians(lat1);
        final double lat2Radians     = Math.toRadians(lat2);
        final double deltaLatRadians = Math.toRadians(lat2 - lat1);
        final double deltaLonRadians = Math.toRadians(lon2 - lon1);

        final double a = Math.sin(deltaLatRadians * 0.5) * Math.sin(deltaLatRadians * 0.5) + Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.sin(deltaLonRadians * 0.5) * Math.sin(deltaLonRadians * 0.5);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double distance = Constants.EARTH_RADIUS * c;

        return distance;
    }

    public double getAltitudeDifferenceInMeter(final Location location) { return (altitude - location.getAltitude()); }

    public double getBearingTo(final Location location) {
        return calcBearingInDegree(getLatitude(), getLongitude(), location.getLatitude(), location.getLongitude());
    }
    public double getBearingTo(final double latitude, final double longitude) {
        return calcBearingInDegree(getLatitude(), getLongitude(), latitude, longitude);
    }

    public boolean isZero() { return Double.compare(latitude, 0d) == 0 && Double.compare(longitude, 0d) == 0; }

    public double calcBearingInDegree(final double lt1, final double ln1, final double lt2, final double ln2) {
        double lat1     = Math.toRadians(lt1);
        double lon1     = Math.toRadians(ln1);
        double lat2     = Math.toRadians(lt2);
        double lon2     = Math.toRadians(ln2);
        double deltaLon = lon2 - lon1;
        double deltaPhi = Math.log(Math.tan(lat2 * 0.5 + Math.PI * 0.25) / Math.tan(lat1 * 0.5 + Math.PI * 0.25));
        if (Math.abs(deltaLon) > Math.PI) {
            if (deltaLon > 0) {
                deltaLon = -(2.0 * Math.PI - deltaLon);
            } else {
                deltaLon = (2.0 * Math.PI + deltaLon);
            }
        }
        double bearing = (Math.toDegrees(Math.atan2(deltaLon, deltaPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public String getCardinalDirectionFromBearing(final double brng) {
        double bearing = brng % 360.0;
        for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
            if (Double.compare(bearing, cardinalDirection.from) >= 0 && Double.compare(bearing, cardinalDirection.to) < 0) {
                return cardinalDirection.direction;
            }
        }
        return "";
    }

    public Location getCopy() {
        return new Location(Instant.ofEpochSecond(this.timestamp.getEpochSecond()), this.latitude, this.longitude, this.altitude, this.accuracy, this.name, this.info);
    }


    // ******************** Event handling ************************************
    public void addLocationObserver(final EvtType type, final EvtObserver<LocationChangeEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
        System.out.println("observer added for type " + type);
    }
    public void removeLocationObserver(final LocationChangeEvt type, final EvtObserver<LocationChangeEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllObservers() { observers.clear(); }

    public void fireLocationEvent(final LocationChangeEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(LocationChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object other) {
        if (other instanceof Location) {
            final Location location = (Location) other;
            return id.equals(location.getId());
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("id").append(QUOTES).append(COLON).append(id).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("timestamp").append(QUOTES).append(COLON).append(timestamp.getEpochSecond()).append(COMMA)
                                  .append(QUOTES).append("latitude").append(QUOTES).append(COLON).append(latitude).append(COMMA)
                                  .append(QUOTES).append("longitude").append(QUOTES).append(COLON).append(longitude).append(COMMA)
                                  .append(QUOTES).append("altitude").append(QUOTES).append(COLON).append(altitude).append(COMMA)
                                  .append(QUOTES).append("accuracy").append(QUOTES).append(COLON).append(accuracy).append(COMMA)
                                  .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(name).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("info").append(QUOTES).append(COLON).append(QUOTES).append(info).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
