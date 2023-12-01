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

import eu.hansolo.toolbox.evt.type.GeoFenceEvt;
import eu.hansolo.toolbox.evt.type.GeoLocationChangeEvt;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;


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
        assert home.getName().equals("Home of Han Solo");
        home.setAccuracy(0.9);
        assert home.getAccuracy() == 0.9;
    }

    @Test
    void testGeoFence() {
        GeoLocation l1 = new GeoLocation(51.911504, 7.632918);
        GeoLocation l2 = new GeoLocation(51.912004, 7.635278);
        GeoLocation l3 = new GeoLocation(51.910024, 7.636084);
        GeoLocation l4 = new GeoLocation(51.910220, 7.636916);
        GeoLocation l5 = new GeoLocation(51.909138, 7.637525);
        GeoLocation l6 = new GeoLocation(51.908817, 7.635997);
        GeoLocation l7 = new GeoLocation(51.908563, 7.636117);
        GeoLocation l8 = new GeoLocation(51.908011, 7.633323);
        GeoLocation l9 = new GeoLocation(51.908620, 7.633076);
        GeoLocation l10 = new GeoLocation(51.909262, 7.632963);
        GeoLocation l11 = new GeoLocation(51.911504, 7.632918);

        Polygon polygon = new Polygon(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11);

        GeoFence geoFence = new GeoFence("Krankenhaus Hiltrup", "GeoFence des Hiltruper Krankenhauses" , "", true, false, LocalTime.now(), LocalTime.now(), ZoneId.systemDefault(), Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), Set.of("Krankenhäuser"), polygon);

        GeoLocation location1 = GeoLocationBuilder.create().name("location1").info("initially outside").latitude(51.911757).longitude(7.633701).build();
        GeoLocation location2 = GeoLocationBuilder.create().name("location2").info("initially inside").latitude(51.910356).longitude(7.634406).build();

        geoFence.addGeoFenceObserver(GeoFenceEvt.ENTERED_FENCE, e -> System.out.println("GeoLocation entered fence " + e.getGeoLocation().get().getName()));
        geoFence.addGeoFenceObserver(GeoFenceEvt.INSIDE_FENCE, e -> System.out.println("GeoLocation is inside fence " + e.getGeoLocation().get().getName()));
        geoFence.addGeoFenceObserver(GeoFenceEvt.LEFT_FENCE, e -> System.out.println("GeoLocation left fence " + e.getGeoLocation().get().getName()));
        geoFence.addGeoFenceObserver(GeoFenceEvt.OUTSIDE_FENCE, e -> System.out.println("GeoLocation is outside fence " + e.getGeoLocation().get().getName()));

        assert !geoFence.isInFence(location1);

        assert geoFence.isInFence(location2);

        // Move location1 inside fence
        location1.set(51.910356, 7.634406);
        assert geoFence.isInFence(location1);


        // Move location2 outside fence
        location2.set(51.911757, 7.633701);
        assert !geoFence.isInFence(location2);

    }
}
