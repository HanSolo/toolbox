/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.toolbox.geo;

import eu.hansolo.toolbox.Helper;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.GeoLocationChangeEvt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;
import static eu.hansolo.toolbox.Constants.QUOTES_COLON;


public class GeoLocation {
    private String                                                id;
    private long                                                  timestamp;
    private String                                                name;
    private String                                                info;
    private double                                                latitude;
    private double                                                longitude;
    private double                                                altitude;
    private double                                                accuracy;
    private Map<EvtType, List<EvtObserver<GeoLocationChangeEvt>>> observers;


    // ******************** Constructors **************************************
    public GeoLocation() {
        this(Instant.now().getEpochSecond(), "", "", 0.0, 0.0, 0.0, 1.0);
    }
    public GeoLocation(final double latitude, final double longitude) {
        this(Instant.now().getEpochSecond(), "", "", latitude, longitude, 0.0, 1.0);
    }
    public GeoLocation(final long timestamp, final String name, final String info, final double latitude, final double longitude, final double altitude, final double accuracy) {
        this.id        = UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.name      = name;
        this.info      = info;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.accuracy  = accuracy;
        this.observers = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public String getId() { return id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(final long timestamp) {
        final GeoLocation oldLocation = getCopy();
        this.timestamp = timestamp;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.TIMESTAMP_CHANGED, oldLocation, GeoLocation.this));
    }

    public String getName() { return name; }
    public void setName(final String name) {
        final GeoLocation oldLocation = getCopy();
        this.name = name;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.NAME_CHANGED, oldLocation, GeoLocation.this));
    }

    public String getInfo() { return info; }
    public void setInfo(final String info) {
        final GeoLocation oldLocation = getCopy();
        this.info = info;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.INFO_CHANGED, oldLocation, GeoLocation.this));
    }

    public double getLatitude() { return latitude; }
    public void setLatitude(final double latitude) {
        final GeoLocation oldLocation = getCopy();
        this.latitude = latitude;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(final double longitude) {
        final GeoLocation oldLocation = getCopy();
        this.longitude = longitude;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }

    public double getAltitude() { return altitude; }
    public void setAltitude(final double altitude) {
        final GeoLocation oldLocation = getCopy();
        this.altitude = altitude;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.ALTITUDE_CHANGED, oldLocation, GeoLocation.this));
    }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(final double accuracy) {
        final GeoLocation oldLocation = getCopy();
        this.accuracy = accuracy;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.ACCURACY_CHANGED, oldLocation, GeoLocation.this));
    }


    public void set(final double latitude, final double longitude) {
        final GeoLocation oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        this.timestamp = Instant.now().getEpochSecond();
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final long timestamp) {
        final GeoLocation oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final long timestamp, final double accuracy, final String info) {
        final GeoLocation oldLocation = getCopy();
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        this.accuracy  = accuracy;
        this.info      = info;
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }
    public void set(final GeoLocation location) {
        final GeoLocation oldLocation = getCopy();
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        altitude  = location.getAltitude();
        timestamp = location.getTimestamp();
        accuracy  = location.getAccuracy();
        name      = location.getName();
        info      = location.getInfo();
        fireGeoLocationEvent(new GeoLocationChangeEvt(GeoLocation.this, GeoLocationChangeEvt.LOCATION_CHANGED, oldLocation, GeoLocation.this));
    }


    public double getDistanceTo(final GeoLocation location) { return Helper.calcDistanceInMeter(GeoLocation.this, location); }

    public double getAltitudeDistanceTo(final GeoLocation location) { return this.altitude - location.getAltitude(); }

    public double getBearingTo(final GeoLocation location) { return Helper.calcBearingInDegree(GeoLocation.this, location); }

    public CardinalDirection getCardinalDirectionTo(final GeoLocation location) {
        return Helper.getCardinalDirectionFromBearing(Helper.calcBearingInDegree(GeoLocation.this, location));
    }

    public boolean isWithinRangeOf(final GeoLocation location, final double meters) { return getDistanceTo(location) < meters; }

    public GeoLocation getCopy() { return new GeoLocation(this.timestamp, this.name, this.info, this.latitude, this.longitude, this.altitude, this.accuracy); }

    public void dispose() { removeAllGeoLocationObservers(); }


    // ******************** Event handling ************************************
    public void addGeoLocationObserver(final EvtType<? extends Evt> type, final EvtObserver<GeoLocationChangeEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeGeoLocationObserver(final EvtType<? extends Evt> type, final EvtObserver<GeoLocationChangeEvt> observer) {
        if (observers.containsKey(type) && observers.get(type).contains(observer)) {
            observers.get(type).remove(observer);
        }
    }
    public void removeAllGeoLocationObservers() { observers.clear(); }

    public void fireGeoLocationEvent(final GeoLocationChangeEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(GeoLocationChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object other) {
        if (other instanceof GeoLocation) {
            final GeoLocation location = (GeoLocation) other;
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
                                  .append(QUOTES).append("id").append(QUOTES_COLON).append(QUOTES).append(id).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("timestamp").append(QUOTES_COLON).append(timestamp).append(COMMA)
                                  .append(QUOTES).append("name").append(QUOTES_COLON).append(QUOTES).append(name).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("latitude").append(QUOTES_COLON).append(latitude).append(COMMA)
                                  .append(QUOTES).append("longitude").append(QUOTES_COLON).append(longitude).append(COMMA)
                                  .append(QUOTES).append("altitude").append(QUOTES_COLON).append(altitude).append(COMMA)
                                  .append(QUOTES).append("accuracy").append(QUOTES_COLON).append(accuracy).append(COMMA)
                                  .append(QUOTES).append("info").append(QUOTES_COLON).append(QUOTES).append(info).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
