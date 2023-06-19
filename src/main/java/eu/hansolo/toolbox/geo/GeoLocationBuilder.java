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

import eu.hansolo.toolbox.properties.DoubleProperty;
import eu.hansolo.toolbox.properties.LongProperty;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;
import eu.hansolo.toolbox.properties.StringProperty;

import java.util.HashMap;


public class GeoLocationBuilder<B extends GeoLocationBuilder<B>> {
    private HashMap<String, ReadOnlyProperty> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected GeoLocationBuilder() {}


    // ******************** Methods *******************************************
    public static final GeoLocationBuilder create() {
        return new GeoLocationBuilder();
    }

    public final B name(final String name) {
        properties.put("name", new StringProperty(name));
        return (B) this;
    }

    public final B timestamp(final long timestamp) {
        properties.put("timestamp", new LongProperty(timestamp));
        return (B) this;
    }

    public final B latitude(final double latitude) {
        properties.put("latitude", new DoubleProperty(latitude));
        return (B) this;
    }

    public final B longitude(final double longitude) {
        properties.put("longitude", new DoubleProperty(longitude));
        return (B) this;
    }

    public final B altitude(final double altitude) {
        properties.put("altitude", new DoubleProperty(altitude));
        return (B) this;
    }

    public final B accuracy(final double accuracy) {
        properties.put("accuracy", new DoubleProperty(accuracy));
        return (B)this;
    }

    public final B info(final String info) {
        properties.put("info", new StringProperty(info));
        return (B) this;
    }

    public final GeoLocation build() {
        GeoLocation location = new GeoLocation();
        properties.forEach((key, property) -> {
            switch(key) {
                case "name"      -> location.setName(((StringProperty) properties.get(key)).get());
                case "timestamp" -> location.setTimestamp(((LongProperty) properties.get(key)).get());
                case "latitude"  -> location.setLatitude(((DoubleProperty) properties.get(key)).get());
                case "longitude" -> location.setLongitude(((DoubleProperty) properties.get(key)).get());
                case "altitude"  -> location.setAltitude(((DoubleProperty) properties.get(key)).get());
                case "accuracy"  -> location.setAccuracy(((DoubleProperty) properties.get(key)).get());
                case "info"      -> location.setInfo(((StringProperty) properties.get(key)).get());
            }
        });
        return location;
    }
}
