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

package eu.hansolo.toolbox.nmea;

import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.NmeaEvt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum NmeaManager {
    INSTANCE;

    // Conversion factor knots -> kph
    public static final double KNOT = 1.852f;

    // Parser definitions
    //private static final String  GGA_HEADER  = "\\$GPGGA|\\$GLGGA|\\$GBGGA|\\$GNGGA|\\$GAGGA";
    private static final String  GGA_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(GGA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(GGA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(GGA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(GGA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(GGA.INSTANCE.getName()).toString();
    //private static final String  GLL_HEADER  = "\\$GPGLL|\\$GLGLL|\\$GBGLL|\\$GNGLL|\\$GAGLL";
    private static final String  GLL_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(GLL.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(GLL.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(GLL.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(GLL.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(GLL.INSTANCE.getName()).toString();
    //private static final String  GSA_HEADER  = "\\$GPGSA|\\$GLGSA|\\$GBGSA|\\$GNGSA|\\$GAGSA";
    private static final String  GSA_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(GSA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(GSA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(GSA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(GSA.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(GSA.INSTANCE.getName()).toString();
    //private static final String  VTG_HEADER  = "\\$GPVTG|\\$GLVTG|\\$GBVTG|\\$GNVTG|\\$GAVTG";
    private static final String  VTG_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(VTG.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(VTG.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(VTG.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(VTG.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(VTG.INSTANCE.getName()).toString();
    //private static final String  RMC_HEADER  = "\\$GPRMC|\\$GLRMC|\\$GBRMC|\\$GNRMC|\\$GARMC";
    private static final String  RMC_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(RMC.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(RMC.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(RMC.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(RMC.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(RMC.INSTANCE.getName()).toString();
    //private static final String  GSV_HEADER  = "\\$GPGSV|\\$GLGSV|\\$GBGSV|\\$GNGSV|\\$GAGSV";
    private static final String  GSV_HEADER  = new StringBuilder("\\").append(TalkerId.GPS.prefix).append(GSV.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GLONASS.prefix).append(GSV.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GALILEO.prefix).append(GSV.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.BEIDOU.prefix).append(GSV.INSTANCE.getName()).append("|\\")
                                                                      .append(TalkerId.GNSS.prefix).append(GSV.INSTANCE.getName()).toString();
    private static final String  GGA_GROUP   = String.join("", "(", GGA_HEADER, ")");
    private static final String  GLL_GROUP   = String.join("", "(", GLL_HEADER, ")");
    private static final String  GSA_GROUP   = String.join("", "(", GSA_HEADER, ")");
    private static final String  VTG_GROUP   = String.join("", "(", VTG_HEADER, ")");
    private static final String  RMC_GROUP   = String.join("", "(", RMC_HEADER, ")");
    private static final String  GSV_GROUP   = String.join("", "(", GSV_HEADER, ")");
    private static final String  FLOAT       = "(\\-?[0-9]*.?[0-9]*)";
    private static final String  INT         = "(\\-?[0-9]*)";
    private static final String  STRING      = "([^,]+)";
    private static final String  CHAR        = "([a-zA-Z]{1})";
    private static final Pattern GGA_PATTERN = Pattern.compile(String.join(",", GGA_GROUP, STRING, FLOAT, CHAR, FLOAT, CHAR, INT, INT, FLOAT, FLOAT, CHAR, FLOAT, CHAR));
    private static final Matcher GGA_MATCHER = GGA_PATTERN.matcher("");
    private static final Pattern GLL_PATTERN = Pattern.compile(String.join(",", GLL_GROUP, FLOAT, CHAR, FLOAT, CHAR, STRING, CHAR));
    private static final Matcher GLL_MATCHER = GLL_PATTERN.matcher("");
    private static final Pattern GSA_PATTERN = Pattern.compile(String.join(",", GSA_GROUP, CHAR, INT, INT, INT, INT, INT, INT, INT, INT, INT, INT, INT, INT, INT, FLOAT, FLOAT));
    private static final Matcher GSA_MATCHER = GSA_PATTERN.matcher("");
    private static final Pattern VTG_PATTERN = Pattern.compile(String.join(",", VTG_GROUP, FLOAT, CHAR, FLOAT, CHAR, FLOAT, CHAR, FLOAT, CHAR));
    private static final Matcher VTG_MATCHER = VTG_PATTERN.matcher("");
    private static final Pattern RMC_PATTERN = Pattern.compile(String.join(",", RMC_GROUP, STRING, CHAR, FLOAT, CHAR, FLOAT, CHAR, FLOAT, FLOAT, STRING, FLOAT, CHAR));
    private static final Matcher RMC_MATCHER = RMC_PATTERN.matcher("");
    private static final Pattern GSV_PATTERN = Pattern.compile(String.join(",", GSV_GROUP, INT, INT, INT));
    private static final Matcher GSV_MATCHER = GSV_PATTERN.matcher("");

    private Map<EvtType, List<EvtObserver<NmeaEvt>>> observers = new ConcurrentHashMap<>();


    // ******************** Methods *******************************************
    public final void parse(final String sentence) {
        if (sentence.startsWith("$") && sentence.contains(",")) {
            final String header = sentence.substring(sentence.indexOf(",") - 3, sentence.indexOf(","));
            switch (header) {
                case "GGA" -> parseGGA(sentence);
                case "GLL" -> parseGLL(sentence);
                case "GSA" -> parseGSA(sentence);
                case "VTG" -> parseVTG(sentence);
                case "RMC" -> parseRMC(sentence);
                case "GSV" -> parseGSV(sentence);
            }
        }
    }

    private void parseGGA(final String sentence) {
        GGA_MATCHER.reset(sentence);
        while (GGA_MATCHER.find()) {
            GGA.INSTANCE.time               = GGA_MATCHER.group(2).isEmpty() ? "000000" : GGA_MATCHER.group(2);
            GGA.INSTANCE.hour               = Integer.parseInt(GGA.INSTANCE.time.substring(0, 2));
            GGA.INSTANCE.minute             = Integer.parseInt(GGA.INSTANCE.time.substring(2, 4));
            GGA.INSTANCE.second             = Integer.parseInt(GGA.INSTANCE.time.substring(4, 6));
            GGA.INSTANCE.latitudeIndicator  = GGA_MATCHER.group(4).isEmpty() ? 'N' : GGA_MATCHER.group(4).charAt(0);
            GGA.INSTANCE.longitudeIndicator = GGA_MATCHER.group(6).isEmpty() ? 'E' : GGA_MATCHER.group(6).charAt(0);
            GGA.INSTANCE.latitude           = GGA_MATCHER.group(3).isEmpty() ? 0 : degreesMinToDegrees(GGA_MATCHER.group(3)) * (GGA.INSTANCE.latitudeIndicator == 'S' ? -1 : 1);
            GGA.INSTANCE.longitude          = GGA_MATCHER.group(5).isEmpty() ? 0 : degreesMinToDegrees(GGA_MATCHER.group(5)) * (GGA.INSTANCE.longitudeIndicator == 'W' ? -1 : 1);
            GGA.INSTANCE.quality            = GGA_MATCHER.group(7).isEmpty() ? 0 : Integer.parseInt(GGA_MATCHER.group(7)); // 0 = no fix, 1 = fix 2D/3D GPS, 2 = fix DGPS, 6 = estimated fix
            GGA.INSTANCE.satellitesTracked  = GGA_MATCHER.group(8).isEmpty() ? 0 : Integer.parseInt(GGA_MATCHER.group(8)); // 00 ~ 16
            GGA.INSTANCE.hdop               = GGA_MATCHER.group(9).isEmpty() ? 20 : Double.parseDouble(GGA_MATCHER.group(9));
            GGA.INSTANCE.altitude           = isNumeric(GGA_MATCHER.group(10)) ? Double.parseDouble(GGA_MATCHER.group(10)) : 0;
        }
        fireNmeaEvent(new NmeaEvt(GGA.INSTANCE, NmeaEvt.GGA));
    }
    private void parseGLL(final String sentence) {
        GLL_MATCHER.reset(sentence);
        while (GLL_MATCHER.find()) {
            GLL.INSTANCE.latitudeIndicator  = GLL_MATCHER.group(3).isEmpty() ? 'N' : GLL_MATCHER.group(3).charAt(0);
            GLL.INSTANCE.longitudeIndicator = GLL_MATCHER.group(5).isEmpty() ? 'E' : GLL_MATCHER.group(5).charAt(0);
            GLL.INSTANCE.latitude           = GLL_MATCHER.group(2).isEmpty() ? 0 : degreesMinToDegrees(GLL_MATCHER.group(2)) * (GLL.INSTANCE.latitudeIndicator == 'S' ? -1 : 1);
            GLL.INSTANCE.longitude          = GLL_MATCHER.group(4).isEmpty() ? 0 : degreesMinToDegrees(GLL_MATCHER.group(4)) * (GLL.INSTANCE.longitudeIndicator == 'W' ? -1 : 1);
            GLL.INSTANCE.time               = GLL_MATCHER.group(6).isEmpty() ? "000000" : GLL_MATCHER.group(6);
            GLL.INSTANCE.hour               = Integer.parseInt(GLL.INSTANCE.time.substring(0, 2));
            GLL.INSTANCE.minute             = Integer.parseInt(GLL.INSTANCE.time.substring(2, 4));
            GLL.INSTANCE.second             = Integer.parseInt(GLL.INSTANCE.time.substring(4, 6));
            GLL.INSTANCE.status             = GLL_MATCHER.group(7).isEmpty() ? 'V' : GLL_MATCHER.group(7).charAt(0);
        }
        fireNmeaEvent(new NmeaEvt(GLL.INSTANCE, NmeaEvt.GLL));
    }
    private void parseGSA(final String sentence) {
        GSA_MATCHER.reset(sentence);
        while (GSA_MATCHER.find()) {
            GSA.INSTANCE.fix      = GSA_MATCHER.group(3).isEmpty() ? 1 : Integer.parseInt(GSA_MATCHER.group(3));
            GSA.INSTANCE.prns[0]  = GSA_MATCHER.group(4).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(4));
            GSA.INSTANCE.prns[1]  = GSA_MATCHER.group(5).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(5));
            GSA.INSTANCE.prns[2]  = GSA_MATCHER.group(6).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(6));
            GSA.INSTANCE.prns[3]  = GSA_MATCHER.group(7).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(7));
            GSA.INSTANCE.prns[4]  = GSA_MATCHER.group(8).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(8));
            GSA.INSTANCE.prns[5]  = GSA_MATCHER.group(9).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(9));
            GSA.INSTANCE.prns[6]  = GSA_MATCHER.group(10).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(10));
            GSA.INSTANCE.prns[7]  = GSA_MATCHER.group(11).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(11));
            GSA.INSTANCE.prns[8]  = GSA_MATCHER.group(12).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(12));
            GSA.INSTANCE.prns[9]  = GSA_MATCHER.group(13).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(13));
            GSA.INSTANCE.prns[10] = GSA_MATCHER.group(14).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(14));
            GSA.INSTANCE.prns[11] = GSA_MATCHER.group(15).isEmpty() ? 0 : Integer.parseInt(GSA_MATCHER.group(15));
            GSA.INSTANCE.pdop     = GSA_MATCHER.group(16).isEmpty() ? 0 : Double.parseDouble(GSA_MATCHER.group(16));
            GSA.INSTANCE.hdop     = GSA_MATCHER.group(17).isEmpty() ? 0 : Double.parseDouble(GSA_MATCHER.group(17));
        }
        fireNmeaEvent(new NmeaEvt(GSA.INSTANCE, NmeaEvt.GSA));
    }
    private void parseVTG(final String sentence) {
        VTG_MATCHER.reset(sentence);
        while (VTG_MATCHER.find()) {
            VTG.INSTANCE.trueCourse     = VTG_MATCHER.group(2).isEmpty() ? 0 : Double.parseDouble(VTG_MATCHER.group(2));
            VTG.INSTANCE.magneticCourse = VTG_MATCHER.group(4).isEmpty() ? 0 : Double.parseDouble(VTG_MATCHER.group(4));
            VTG.INSTANCE.speedInKnots   = VTG_MATCHER.group(6).isEmpty() ? 0 : Double.parseDouble(VTG_MATCHER.group(6));
            VTG.INSTANCE.speedInKph     = VTG_MATCHER.group(8).isEmpty() ? 0 : Double.parseDouble(VTG_MATCHER.group(8));
        }
        fireNmeaEvent(new NmeaEvt(VTG.INSTANCE, NmeaEvt.VTG));
    }
    private void parseRMC(final String sentence) {
        RMC_MATCHER.reset(sentence);
        while (RMC_MATCHER.find()) {
            RMC.INSTANCE.time               = RMC_MATCHER.group(2).isEmpty() ? "000000" : RMC_MATCHER.group(2);
            RMC.INSTANCE.hour               = Integer.parseInt(RMC.INSTANCE.time.substring(0, 2));
            RMC.INSTANCE.minute             = Integer.parseInt(RMC.INSTANCE.time.substring(2, 4));
            RMC.INSTANCE.second             = Integer.parseInt(RMC.INSTANCE.time.substring(4, 6));
            RMC.INSTANCE.status             = RMC_MATCHER.group(3).isEmpty() ? 'V' : RMC_MATCHER.group(3).charAt(0);
            RMC.INSTANCE.latitudeIndicator  = RMC_MATCHER.group(5).isEmpty() ? 'N' : RMC_MATCHER.group(5).charAt(0);
            RMC.INSTANCE.longitudeIndicator = RMC_MATCHER.group(7).isEmpty() ? 'E' : RMC_MATCHER.group(5).charAt(0);
            RMC.INSTANCE.latitude           = RMC_MATCHER.group(4).isEmpty() ? 0 : degreesMinToDegrees(RMC_MATCHER.group(4)) * (RMC.INSTANCE.latitudeIndicator == 'S' ? -1 : 1);
            RMC.INSTANCE.longitude          = RMC_MATCHER.group(6).isEmpty() ? 0 : degreesMinToDegrees(RMC_MATCHER.group(6)) * (RMC.INSTANCE.longitudeIndicator == 'W' ? -1 : 1);
            RMC.INSTANCE.speedInKnots       = RMC_MATCHER.group(8).isEmpty() ? 0 : Double.parseDouble(RMC_MATCHER.group(8));
            RMC.INSTANCE.speedInKph         = RMC.INSTANCE.speedInKnots / KNOT;
            RMC.INSTANCE.course             = RMC_MATCHER.group(9).isEmpty() ? 0 : Double.parseDouble(RMC_MATCHER.group(9));
            RMC.INSTANCE.date               = RMC_MATCHER.group(10).isEmpty() ? "010170" : RMC_MATCHER.group(10);
            RMC.INSTANCE.day                = Integer.parseInt(RMC.INSTANCE.date.substring(0, 2));
            RMC.INSTANCE.month              = Integer.parseInt(RMC.INSTANCE.date.substring(2, 4));
            RMC.INSTANCE.year               = Integer.parseInt(RMC.INSTANCE.date.substring(4, 6));
            RMC.INSTANCE.mode               = RMC_MATCHER.group(12).isEmpty() ? "N" : (RMC_MATCHER.group(12).contains("*") ? RMC_MATCHER.group(12)
                                                                                                                                        .substring(0, RMC_MATCHER.group(12)
                                                                                                                                                                 .lastIndexOf(
                                                                                                                                                                 "*")) : RMC_MATCHER.group(
            12)); // N = Data not valid, A = Autonomous mode, D = Differential mode, E = Estimated mode
        }
        fireNmeaEvent(new NmeaEvt(RMC.INSTANCE, NmeaEvt.RMC));
    }
    private void parseGSV(final String sentence) {
        GSV_MATCHER.reset(sentence);
        while (GSV_MATCHER.find()) {
            GSV.INSTANCE.noOfGsvSentences     = GSV_MATCHER.group(2).isEmpty() ? 0 : Integer.parseInt(GSV_MATCHER.group(2));
            GSV.INSTANCE.lastSentence         = GSV.INSTANCE.currentSentence;
            GSV.INSTANCE.currentSentence      = GSV_MATCHER.group(3).isEmpty() ? 0 : Integer.parseInt(GSV_MATCHER.group(3));
            GSV.INSTANCE.noOfSatellitesInView = GSV_MATCHER.group(4).isEmpty() ? 0 : Integer.parseInt(GSV_MATCHER.group(4));
        }
        fireNmeaEvent(new NmeaEvt(GSV.INSTANCE, NmeaEvt.GSV));
    }


    // ******************** Helper Methods ************************************
    public final double degreesMinToDegrees(final String DD_MM) {
        // This methods accept all strings of the format
        // DDDMM.MMMM
        // DDDMM
        // MM.MMMM
        // MM

        // check first character, rest is checked by parseInt/parseFloat
        int len = DD_MM.length();
        if (len <= 0 || DD_MM.charAt(0) == '-') throw new NumberFormatException();

        int dotPosition = DD_MM.indexOf('.');
        if (dotPosition < 0) dotPosition = len;

        int degrees;
        double minutes;
        if (dotPosition > 2) {
            degrees = Integer.parseInt(DD_MM.substring(0, dotPosition - 2));
            if (DD_MM.charAt(dotPosition - 2) == '-') throw new NumberFormatException();
            minutes = Double.parseDouble(DD_MM.substring(dotPosition - 2));
        } else {
            degrees = 0;
            minutes = Double.parseDouble(DD_MM);
        }
        return degrees + minutes * (1.0 / 60.0);
    }

    private boolean isNumeric(final String text) {
        if (null == text || text.isEmpty()) { return false; }
        final int length = text.length();
        for (int x = 0; x < length; x++) {
            final char c = text.charAt(x);
            if (x == 0 && (c == '-')) continue;      // negative
            if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
            if (c == '.') continue;                  // double or double values
            return false;                            // invalid
        }
        return true;                                 // valid
    }


    // ******************** Event Handling ************************************
    public final void addNmeaObserver(final EvtType<? extends Evt> type, final EvtObserver<NmeaEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public final void removeNmeaObserver(final EvtType<? extends Evt> type, final EvtObserver<NmeaEvt> observer) {
        if (observers.containsKey(type) && observers.get(type).contains(observer)) { observers.get(type).remove(observer); }
    }
    public final void removeAllNmeaObservers() { observers.clear(); }

    public final void fireNmeaEvent(final NmeaEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(NmeaEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) { observers.get(type).forEach(observer -> observer.handle(evt)); }
    }
}
