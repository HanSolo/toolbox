/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

package eu.hansolo.toolbox.evt;


import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public final class EvtType<T extends Evt> {
    public  static final EvtType<Evt>       ROOT = new EvtType<>("EVENT", null);
    private        final EvtType<? super T> superType;
    private        final String             name;


    // ******************** Constructors **************************************
    public EvtType(final EvtType<? super T> superType) {
        this(superType, null);
    }
    public EvtType(final String name) {
        this(ROOT, name);
    }
    public EvtType(final EvtType<? super T> superType, final String name) {
        if (null == superType) { throw new NullPointerException("Event super type must not be null (EvtType.name: " + name + ")"); }
        this.superType = superType;
        this.name      = name;
    }
    EvtType(final String name, final EvtType<? super T> superType) {
        this.superType = superType;
        this.name      = name;
    }


    // ******************** Methods *******************************************
    public EvtType<? super T> getSuperType() { return superType; }

    public String getName() { return name; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        EvtType<?> evtType = (EvtType<?>) o;
        return superType.equals(evtType.superType) && name.equals(evtType.name);
    }

    @Override public int hashCode() {
        return Objects.hash(superType, name);
    }

    @Override public String toString() {
        return (null != name) ? new StringBuilder().append(CURLY_BRACKET_OPEN)
                                                   .append(QUOTES).append("class").append(QUOTES).append(COLON).append(QUOTES).append(getClass().getName()).append(QUOTES).append(COMMA)
                                                   .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(getName()).append(QUOTES).append(COMMA)
                                                   .append(QUOTES).append("supertype").append(QUOTES).append(COLON).append(QUOTES).append(getSuperType().name).append(QUOTES)
                                                   .append(CURLY_BRACKET_CLOSE)
                                                   .toString() :
                                super.toString();
    }
}
