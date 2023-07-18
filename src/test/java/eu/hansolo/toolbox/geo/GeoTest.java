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

import eu.hansolo.toolbox.evt.type.GeoLocationChangeEvt;
import org.junit.jupiter.api.Test;

import java.util.Locale;


public class GeoTest {

    @Test
    void testGeo() {
        System.out.println("\n-------------------- geo demo --------------------");
        GeoLocation home = GeoLocationBuilder.create()
                                             .name("Home")
                                             .latitude(51.912781150242054)
                                             .longitude(7.633729751419756)
                                             .altitude(66)
                                             .build();

        GeoLocation azul = GeoLocationBuilder.create()
                                             .name("Azul")
                                             .latitude(37.40668261833162)
                                             .longitude(-122.01573123930172)
                                             .altitude(20)
                                             .build();

        home.addGeoLocationObserver(GeoLocationChangeEvt.ACCURACY_CHANGED, e -> System.out.println("Accuracy changed from " + e.getOldGeoLocation().getAccuracy() + " to " + e.getGeoLocation().getAccuracy()));
        home.addGeoLocationObserver(GeoLocationChangeEvt.NAME_CHANGED, e -> System.out.println("Name changed from: " + e.getOldGeoLocation().getName() + " to " + e.getGeoLocation().getName()));

        System.out.println("Distance from Home to Azul: " + String.format(Locale.US, "%.2f km", (home.getDistanceTo(azul) / 1000)));
        home.setName("Home of Han Solo");
        home.setAccuracy(0.9);
    }
}
