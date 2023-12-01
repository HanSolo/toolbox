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

import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.GeoFenceEvt;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class GeoFence {
    private final String                                       id;
    private       String                                       name;
    private       String                                       info;
    private       String                                       group;
    private       boolean                                      isActive;
    private       boolean                                      isTimeBased;
    private       LocalTime                                    startTime;
    private       LocalTime                                    endTime;
    private       ZoneId                                       zoneId;
    private       Set<DayOfWeek>                               days;
    private       Set<String>                                  tags;
    private       Polygon                                      polygon;
    private       HashMap<String, GeoLocation>                 objectsInFence;
    private       Map<EvtType, List<EvtObserver<GeoFenceEvt>>> observers;


    // ******************** Constructor ***************************************
    public GeoFence() {
        this("", "", "", false, false, LocalTime.now(), LocalTime.now(), ZoneId.systemDefault(), new HashSet<>(), new HashSet<>(), new Polygon());
    }
    public GeoFence(final String name, final String info, final String group, final boolean active, final boolean timeBased, final LocalTime startTime, final LocalTime endTime, final ZoneId zoneId, final Set<DayOfWeek> days, final Set<String> tags, final Polygon polygon) {
        this.id             = UUID.randomUUID().toString();
        this.name           = name;
        this.info           = info;
        this.group     = group;
        this.isActive    = active;
        this.isTimeBased = timeBased;
        this.startTime   = startTime;
        this.endTime        = endTime;
        this.zoneId         = zoneId;
        this.days           = new HashSet<>(days);
        this.tags           = new HashSet<>(tags);
        this.objectsInFence = new HashMap<>();
        this.observers      = new ConcurrentHashMap<>();
        this.polygon        = polygon;
    }


    // ******************** Methods *******************************************
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(final String name) {
        final String oldName = this.name;
        this.name = name;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.NAME_CHANGED, oldName, this.name));
    }

    public String getInfo() { return info; }
    public void setInfo(final String info) {
        final String oldInfo = this.info;
        this.info = info;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.INFO_CHANGED, oldInfo, this.info));
    }

    public String getGroup() { return group; }
    public void setGroup(final String group) {
        final String oldGroup = this.group;
        this.group = group;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.GROUP_CHANGED, oldGroup, this.group));
    }

    public boolean isActive() { return isActive; }
    public void setActive(final boolean active) {
        final boolean wasActive = this.isActive;
        this.isActive = active;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.ACTIVE_CHANGED, wasActive, this.isActive));
    }

    public boolean isTimeBased() { return isTimeBased; }
    public void setTimeBased(final boolean timeBased) {
        final boolean wasTimeBased = this.isTimeBased;
        this.isTimeBased = timeBased;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.TIME_BASED_CHANGED, wasTimeBased, this.isTimeBased));
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(final LocalTime startTime) {
        final LocalTime oldStartTime = this.startTime;
        this.startTime = startTime;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.START_TIME_CHANGED, oldStartTime, this.startTime));
    }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(final LocalTime endTime) {
        final LocalTime oldEndTime = this.endTime;
        this.endTime = endTime;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.END_TIME_CHANGED, oldEndTime, this.endTime));
    }

    public ZoneId getZoneId() { return zoneId; }
    public void setZoneId(final ZoneId zoneId) {
        final ZoneId oldZoneId = this.zoneId;
        this.zoneId = zoneId;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.ZONE_ID_CHANGED, oldZoneId, this.zoneId));
    }

    public Set<DayOfWeek> getDays() { return days; }
    public void setDays(final Set<DayOfWeek> days) {
        final Set<DayOfWeek> oldDays = new HashSet<>(this.days);
        this.days = days;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.DAYS_CHANGED, oldDays, this.days));
    }

    public Set<String> getTags() { return tags; }
    public void setTags(final Set<String> tags) {
        final Set<String> oldTags = new HashSet<>(this.tags);
        this.tags = tags;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.TAGS_CHANGED, oldTags, this.tags));
    }

    public Polygon getPolygon() { return polygon; }
    public void setPolygon(final Polygon polygon) {
        final Polygon oldPolygon = this.polygon;
        this.polygon = polygon;
        fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.POLYGON_CHANGED, oldPolygon, this.polygon));
    }

    public boolean isInFence(final GeoLocation location) {
        final boolean isKnown   = this.objectsInFence.containsKey(location.getId());
        final boolean isInFence = this.polygon.contains(location);
        if (isKnown) {
            this.objectsInFence.get(location.getId()).set(location);
            if (isInFence) {
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.INSIDE_FENCE, null, location));  // inside fence
            } else {
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.OUTSIDE_FENCE, null, location)); // outside fence
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.LEFT_FENCE, null, location));    // left fence
                this.objectsInFence.remove(location.getId());
            }
        } else {
            if (isInFence) {
                this.objectsInFence.put(location.getId(), location);
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.INSIDE_FENCE, null, location));  // inside fence
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.ENTERED_FENCE, null, location)); // entered fence
            } else {
                fireGeoFenceEvent(new GeoFenceEvt(GeoFence.this, GeoFenceEvt.OUTSIDE_FENCE, null, location));  // outside fence
            }
        }
        return isInFence;
    }

    public Set<String> getIdsOfObjectsInFence() { return new HashSet<>(this.objectsInFence.keySet()); }


    // ******************** Event handling ************************************
    public void addGeoFenceObserver(final EvtType<? extends Evt> type, final EvtObserver<GeoFenceEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeGeoFenceObserver(final EvtType<? extends Evt> type, final EvtObserver<GeoFenceEvt> observer) {
        if (observers.containsKey(type) && observers.get(type).contains(observer)) {
            observers.get(type).remove(observer);
        }
    }
    public void removeAllGeoFenceObservers() { observers.clear(); }

    public void fireGeoFenceEvent(final GeoFenceEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(GeoFenceEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }
}
