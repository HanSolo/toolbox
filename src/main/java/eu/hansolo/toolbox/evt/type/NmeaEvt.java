/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 Gerrit Grunwald.
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
import eu.hansolo.toolbox.nmea.GGA;
import eu.hansolo.toolbox.nmea.NmeaSentence;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;

import java.util.Objects;


public class NmeaEvt extends ChangeEvt {
    public static final EvtType<NmeaEvt> ANY = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<NmeaEvt> GGA = new EvtType<>(NmeaEvt.ANY, "GGA");
    public static final EvtType<NmeaEvt> GLL = new EvtType<>(NmeaEvt.ANY, "GLL");
    public static final EvtType<NmeaEvt> GSA = new EvtType<>(NmeaEvt.ANY, "GSA");
    public static final EvtType<NmeaEvt> RMC = new EvtType<>(NmeaEvt.ANY, "RMC");
    public static final EvtType<NmeaEvt> VTG = new EvtType<>(NmeaEvt.ANY, "VTG");
    public static final EvtType<NmeaEvt> GSV = new EvtType<>(NmeaEvt.ANY, "GSV");

    private EvtType<? extends NmeaEvt> evtType;


    // ******************** Constructors **************************************
    public NmeaEvt(final EvtType<? extends NmeaEvt> evtType) {
        super(evtType);
    }
    public NmeaEvt(final NmeaSentence src, final EvtType<? extends NmeaEvt> evtType) {
        super(src, evtType);
        this.evtType = evtType;
    }
    public NmeaEvt(final GGA src, final EvtType<? extends NmeaEvt> evtType) {
        super(src, evtType);
        this.evtType = evtType;
    }


    // ******************** Methods *******************************************
    @Override public NmeaSentence getSource() { return (NmeaSentence) super.getSource(); }

    @Override public EvtType<? extends NmeaEvt> getEvtType() { return (EvtType<? extends NmeaEvt>) super.getEvtType(); }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        GeoLocationChangeEvt that = (GeoLocationChangeEvt) o;
        return Objects.equals(this.evtType, that.getEvtType()) && Objects.equals(getSource(), that.getSource());
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), getSource());
    }
}
