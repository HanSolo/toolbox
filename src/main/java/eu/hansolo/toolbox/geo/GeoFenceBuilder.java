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

import eu.hansolo.toolbox.properties.BooleanProperty;
import eu.hansolo.toolbox.properties.ObjectProperty;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;
import eu.hansolo.toolbox.properties.StringProperty;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class GeoFenceBuilder <B extends GeoFenceBuilder<B>> {
    private HashMap<String, ReadOnlyProperty> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected GeoFenceBuilder() {}


    // ******************** Methods *******************************************
    public static final GeoFenceBuilder create() {
        return new GeoFenceBuilder();
    }

    public final B name(final String name) {
        properties.put("name", new StringProperty(name));
        return (B) this;
    }

    public final B info(final String info) {
        properties.put("info", new StringProperty(info));
        return (B) this;
    }

    public final B group(final String group) {
        properties.put("group", new StringProperty(group));
        return (B) this;
    }

    public final B isActive(final boolean isActive) {
        properties.put("isActive", new BooleanProperty(isActive));
        return (B) this;
    }

    public final B isTimeBased(final boolean isTimeBased) {
        properties.put("isTimeBased", new BooleanProperty(isTimeBased));
        return (B) this;
    }

    public final B startTime(final LocalTime startTime) {
        properties.put("startTime", new ObjectProperty<>(startTime));
        return (B) this;
    }

    public final B endTime(final LocalTime endTime) {
        properties.put("endTime", new ObjectProperty<>(endTime));
        return (B) this;
    }

    public final B zoneId(final ZoneId zoneId) {
        properties.put("zoneId", new ObjectProperty<>(zoneId));
        return (B) this;
    }

    public final B days(final DayOfWeek... days) {
        properties.put("days", new ObjectProperty<Set<DayOfWeek>>(new HashSet<>(Arrays.asList(days))));
        return (B) this;
    }
    public final B days(final Set<DayOfWeek> days) {
        properties.put("days", new ObjectProperty<Set<DayOfWeek>>(new HashSet<>(days)));
        return (B) this;
    }

    public final B tags(final String... tags) {
        properties.put("tags", new ObjectProperty<Set<String>>(new HashSet<>(Arrays.asList(tags))));
        return (B) this;
    }
    public final B tags(final Set<String> tags) {
        properties.put("tags", new ObjectProperty<Set<String>>(new HashSet<>(tags)));
        return (B) this;
    }

    public final B polygon(final Polygon polygon) {
        properties.put("polygon", new ObjectProperty<>(polygon));
        return (B) this;
    }

    public final GeoFence build() {
        GeoFence geoFence = new GeoFence();
        properties.forEach((key, property) -> {
            switch(key) {
                case "name"        -> geoFence.setName(((StringProperty) properties.get(key)).get());
                case "info"        -> geoFence.setInfo(((StringProperty) properties.get(key)).get());
                case "group"       -> geoFence.setGroup(((StringProperty) properties.get(key)).get());
                case "isActive"    -> geoFence.setActive(((BooleanProperty) properties.get(key)).get());
                case "isTimeBased" -> geoFence.setTimeBased(((BooleanProperty) properties.get(key)).get());
                case "startTime"   -> geoFence.setStartTime(((ObjectProperty<LocalTime>) properties.get(key)).get());
                case "endTime"     -> geoFence.setEndTime(((ObjectProperty<LocalTime>) properties.get(key)).get());
                case "zoneId"      -> geoFence.setZoneId(((ObjectProperty<ZoneId>) properties.get(key)).get());
                case "days"        -> geoFence.setDays(((ObjectProperty<Set<DayOfWeek>>) properties.get(key)).get());
                case "tags"        -> geoFence.setTags(((ObjectProperty<Set<String>>) properties.get(key)).get());
                case "polygon"     -> geoFence.setPolygon(((ObjectProperty<Polygon>) properties.get(key)).get());
            }
        });
        return geoFence;
    }
}
