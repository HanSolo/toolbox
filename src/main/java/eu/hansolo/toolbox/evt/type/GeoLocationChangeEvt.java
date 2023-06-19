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

package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.geo.GeoLocation;

import java.util.Objects;


public class GeoLocationChangeEvt extends ChangeEvt {
    public static final EvtType<GeoLocationChangeEvt> ANY                = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<GeoLocationChangeEvt> TIMESTAMP_CHANGED  = new EvtType<>(GeoLocationChangeEvt.ANY, "TIMESTAMP_CHANGED");
    public static final EvtType<GeoLocationChangeEvt> NAME_CHANGED       = new EvtType<>(GeoLocationChangeEvt.ANY, "NAME_CHANGED");
    public static final EvtType<GeoLocationChangeEvt> INFO_CHANGED       = new EvtType<>(GeoLocationChangeEvt.ANY, "INFO_CHANGED");
    public static final EvtType<GeoLocationChangeEvt> LOCATION_CHANGED   = new EvtType<>(GeoLocationChangeEvt.ANY, "LOCATION_CHANGED");
    public static final EvtType<GeoLocationChangeEvt> ALTITUDE_CHANGED   = new EvtType<>(GeoLocationChangeEvt.ANY, "ALTITUDE_CHANGED");
    public static final EvtType<GeoLocationChangeEvt> ACCURACY_CHANGED   = new EvtType<>(GeoLocationChangeEvt.ANY, "ACCURACY_CHANGED");

    private final GeoLocation oldGeoLocation;
    private final GeoLocation geoLocation;


    // ******************** Constructors **************************************
    public GeoLocationChangeEvt(final EvtType<? extends GeoLocationChangeEvt> evtType, final GeoLocation oldGeoLocation, final GeoLocation geoLocation) {
        super(evtType);
        this.geoLocation    = geoLocation;
        this.oldGeoLocation = oldGeoLocation;
    }
    public GeoLocationChangeEvt(final Object src, final EvtType<? extends GeoLocationChangeEvt> evtType, final GeoLocation oldGeoLocation, final GeoLocation geoLocation) {
        super(src, evtType);
        this.geoLocation    = geoLocation;
        this.oldGeoLocation = oldGeoLocation;
    }
    public GeoLocationChangeEvt(final Object src, final EvtType<? extends GeoLocationChangeEvt> evtType, final EvtPriority priority, final GeoLocation oldGeoLocation, final GeoLocation geoLocation) {
        super(src, evtType, priority);
        this.geoLocation    = geoLocation;
        this.oldGeoLocation = oldGeoLocation;
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends GeoLocationChangeEvt> getEvtType() { return (EvtType<? extends GeoLocationChangeEvt>) super.getEvtType(); }

    public GeoLocation getOldGeoLocation() { return oldGeoLocation; }

    public GeoLocation getGeoLocation() { return geoLocation; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        GeoLocationChangeEvt that = (GeoLocationChangeEvt) o;
        return Objects.equals(oldGeoLocation, that.oldGeoLocation) && Objects.equals(geoLocation, that.geoLocation);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), oldGeoLocation, geoLocation);
    }
}
