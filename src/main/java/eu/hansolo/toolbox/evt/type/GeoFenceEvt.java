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
import eu.hansolo.toolbox.geo.GeoFence;
import eu.hansolo.toolbox.geo.GeoLocation;
import eu.hansolo.toolbox.geo.Polygon;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


public class GeoFenceEvt extends ChangeEvt {
    public static final EvtType<GeoFenceEvt> ANY                = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<GeoFenceEvt> NAME_CHANGED       = new EvtType<>(GeoFenceEvt.ANY, "NAME_CHANGED");
    public static final EvtType<GeoFenceEvt> INFO_CHANGED       = new EvtType<>(GeoFenceEvt.ANY, "INFO_CHANGED");
    public static final EvtType<GeoFenceEvt> GROUP_CHANGED      = new EvtType<>(GeoFenceEvt.ANY, "GROUP_CHANGED");
    public static final EvtType<GeoFenceEvt> TIME_BASED_CHANGED = new EvtType<>(GeoFenceEvt.ANY, "TIME_BASED_CHANGED");
    public static final EvtType<GeoFenceEvt> START_TIME_CHANGED = new EvtType<>(GeoFenceEvt.ANY, "START_TIME_CHANGED");
    public static final EvtType<GeoFenceEvt> END_TIME_CHANGED   = new EvtType<>(GeoFenceEvt.ANY, "END_TIME_CHANGED");
    public static final EvtType<GeoFenceEvt> ZONE_ID_CHANGED    = new EvtType<>(GeoFenceEvt.ANY, "ZONE_ID_CHANGED");
    public static final EvtType<GeoFenceEvt> ACTIVE_CHANGED     = new EvtType<>(GeoFenceEvt.ANY, "ACTIVE_CHANGED");
    public static final EvtType<GeoFenceEvt> DAYS_CHANGED       = new EvtType<>(GeoFenceEvt.ANY, "DAYS_CHANGED");
    public static final EvtType<GeoFenceEvt> TAGS_CHANGED       = new EvtType<>(GeoFenceEvt.ANY, "TAGS_CHANGED");
    public static final EvtType<GeoFenceEvt> POLYGON_CHANGED    = new EvtType<>(GeoFenceEvt.ANY, "POLYGON_CHANGED");
    public static final EvtType<GeoFenceEvt> ENTERED_FENCE      = new EvtType<>(GeoFenceEvt.ANY, "ENTERED_FENCE");
    public static final EvtType<GeoFenceEvt> LEFT_FENCE         = new EvtType<>(GeoFenceEvt.ANY, "LEFT_FENCE");
    public static final EvtType<GeoFenceEvt> INSIDE_FENCE       = new EvtType<>(GeoFenceEvt.ANY, "INSIDE_FENCE");
    public static final EvtType<GeoFenceEvt> OUTSIDE_FENCE      = new EvtType<>(GeoFenceEvt.ANY, "OUTSIDE_FENCE");


    private EvtType<? extends GeoFenceEvt> evtType;
    private Object                         oldValue;
    private Object                         newValue;


    // ******************** Constructors **************************************
    public GeoFenceEvt(final GeoFence src, final EvtType<? extends GeoFenceEvt> evtType, final Object oldValue, final Object newValue) {
        super(src, evtType);
        this.evtType  = evtType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    public GeoFenceEvt(final GeoFence src, final EvtType<? extends GeoFenceEvt> evtType, final Object oldValue, final Object newValue, final EvtPriority priority) {
        super(src, evtType, priority);
        this.evtType  = evtType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends GeoLocationChangeEvt> getEvtType() { return (EvtType<? extends GeoLocationChangeEvt>) super.getEvtType(); }

    public Optional<String> getOldName() { return NAME_CHANGED.equals(this.evtType) ? Optional.of((String) this.oldValue) : null; }
    public Optional<String> getNewName() { return NAME_CHANGED.equals(this.evtType) ? Optional.of((String) this.newValue) : null; }

    public Optional<String> getOldInfo() { return INFO_CHANGED.equals(this.evtType) ? Optional.of((String) this.oldValue) : null; }
    public Optional<String> getNewInfo() { return INFO_CHANGED.equals(this.evtType) ? Optional.of((String) this.newValue) : null; }

    public Optional<String> getOldGroup() { return GROUP_CHANGED.equals(this.evtType) ? Optional.of((String) this.oldValue) : null; }
    public Optional<String> getNewGroup() { return GROUP_CHANGED.equals(this.evtType) ? Optional.of((String) this.newValue) : null; }

    public Optional<Boolean> getOldTimeBased() { return TIME_BASED_CHANGED.equals(this.evtType) ? Optional.of((Boolean) this.oldValue) : null; }
    public Optional<Boolean> getNewTimeBased() { return TIME_BASED_CHANGED.equals(this.evtType) ? Optional.of((Boolean) this.newValue) : null; }

    public Optional<LocalTime> getOldStartTime() { return START_TIME_CHANGED.equals(this.evtType) ? Optional.of((LocalTime) this.oldValue) : null; }
    public Optional<LocalTime> getNewStartTime() { return START_TIME_CHANGED.equals(this.evtType) ? Optional.of((LocalTime) this.newValue) : null; }

    public Optional<LocalTime> getOldEndTime() { return END_TIME_CHANGED.equals(this.evtType) ? Optional.of((LocalTime) this.oldValue) : null; }
    public Optional<LocalTime> getNewEndTime() { return END_TIME_CHANGED.equals(this.evtType) ? Optional.of((LocalTime) this.newValue) : null; }

    public Optional<ZoneId> getOldZoneId() { return ZONE_ID_CHANGED.equals(this.evtType) ? Optional.of((ZoneId) this.oldValue) : null; }
    public Optional<ZoneId> getNewZoneId() { return ZONE_ID_CHANGED.equals(this.evtType) ? Optional.of((ZoneId) this.newValue) : null; }

    public Optional<Boolean> getWasActive() { return ACTIVE_CHANGED.equals(this.evtType) ? Optional.of((Boolean) this.oldValue) : Optional.empty(); }
    public Optional<Boolean> isActive() { return ACTIVE_CHANGED.equals(this.evtType) ? Optional.of((Boolean) this.newValue) : Optional.empty(); }

    public Optional<Set<DayOfWeek>> getOldDays() { return DAYS_CHANGED.equals(this.evtType) ? Optional.of((Set<DayOfWeek>) this.oldValue) : Optional.empty(); }
    public Optional<Set<DayOfWeek>> getNewDays() { return DAYS_CHANGED.equals(this.evtType) ? Optional.of((Set<DayOfWeek>) this.newValue) : Optional.empty(); }

    public Optional<Set<String>> getOldTags() { return TAGS_CHANGED.equals(this.evtType) ? Optional.of((Set<String>) this.oldValue) : Optional.empty(); }
    public Optional<Set<String>> getNewTags() { return TAGS_CHANGED.equals(this.evtType) ? Optional.of((Set<String>) this.newValue) : Optional.empty(); }

    public Optional<Polygon> getOldPolygon() { return POLYGON_CHANGED.equals(this.evtType) ? Optional.of((Polygon) this.oldValue) : Optional.empty(); }
    public Optional<Polygon> getNewPolygon() { return POLYGON_CHANGED.equals(this.evtType) ? Optional.of((Polygon) this.newValue) : Optional.empty(); }

    public Optional<GeoLocation> getGeoLocation() {
        if (ENTERED_FENCE.equals(this.evtType) ||
            LEFT_FENCE.equals(this.evtType)    ||
            INSIDE_FENCE.equals(this.evtType)  ||
            OUTSIDE_FENCE.equals(this.evtType)) {
            return Optional.of((GeoLocation) this.newValue);
        } else {
            return Optional.empty();
        }
    }


    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        GeoFenceEvt that = (GeoFenceEvt) o;
        return Objects.equals(this.oldValue, that.oldValue) && Objects.equals(this.newValue, that.newValue);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), this.oldValue, this.newValue);
    }
}
