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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum CardinalDirection {
    N("North", "N",348.75, 11.25),
    NNE("North North-East", "NNE", 11.25, 33.75),
    NE("North-East", "NE", 33.75, 56.25),
    ENE("East North-East", "ENE", 56.25, 78.75),
    E("East", "E", 78.75, 101.25),
    ESE("East South-East", "ESE",  101.25, 123.75),
    SE("South-East", "SE", 123.75, 146.25),
    SSE("South South-East", "SSE", 146.25, 168.75),
    S("South", "S", 168.75, 191.25),
    SSW("South South-West", "SSW", 191.25, 213.75),
    SW("South-West", "SW", 213.75, 236.25),
    WSW("West South-West", "WSW", 236.25, 258.75),
    W("West", "W", 258.75, 281.25),
    WNW("West North-West", "WNW", 281.25, 303.75),
    NW("North-West", "NW", 303.75, 326.25),
    NNW("North North-West", "NNW", 326.25, 348.75),
    NOT_FOUND("", "", -1, -1);

    public String direction;
    public String shortForm;
    public double from;
    public double to;


    CardinalDirection(final String direction, final String shortForm, final double from, final double to) {
        this.direction = direction;
        this.shortForm = shortForm;
        this.from      = from;
        this.to        = to;
    }

    public static final List<CardinalDirection> getValues() {
        return Arrays.stream(values()).filter(v -> v != NOT_FOUND).collect(Collectors.toList());
    }

    public static final CardinalDirection fromText(final String text) {
        if (null == text || text.isEmpty()) { return NOT_FOUND; }
        switch(text) {
            case "n", "N", "north", "NORTH", "North"                                      -> { return N; }
            case "nne", "NNE", "north north-east", "NORTH NORTH-EAST", "North North-East" -> { return NNE; }
            case "ne", "NE", "north-east", "NORTH-EAST", "North-East"                     -> { return NE; }
            case "ene", "ENE", "east north-east", "EAST NORTH-EAST", "East North-East"    -> { return ENE; }
            case "e", "E", "east", "EAST", "East"                                         -> { return E; }
            case "ese", "ESE", "east south-east", "EAST SOUTH-EAST", "East South-East"    -> { return ESE; }
            case "se", "SE", "south-east", "SOUTH-EAST", "South-East"                     -> { return SE; }
            case "sse", "SSE", "south south-east", "SOUTH SOUTH-EAST", "South South-East" -> { return SSE; }
            case "s", "S", "south", "SOUTH", "South"                                      -> { return S; }
            case "ssw", "SSW", "south south-west", "SOUTH SOUTH-WEST", "South South-West" -> { return SSW; }
            case "sw", "SW", "south-west", "SOUTH-WEST", "South-West"                     -> { return SW; }
            case "wsw", "WSW", "west south-west", "WEST SOUTH-WEST", "West South-West"    -> { return WSW; }
            case "w", "W", "west", "WEST", "West"                                         -> { return W; }
            case "wnw", "WNW", "west north-west", "WEST NORTH-WEST", "West North-West"    -> { return WNW; }
            case "nw", "NW", "north-west", "NORTH-WEST", "North-West"                     -> { return NW; }
            case "nnw", "NNW", "north north-west", "NORTH NORTH-WEST", "North-North-West" -> { return NNW; }
            default -> { return NOT_FOUND; }
        }
    }
}
