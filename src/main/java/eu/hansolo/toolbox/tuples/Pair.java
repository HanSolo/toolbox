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

package eu.hansolo.toolbox.tuples;

import java.util.Objects;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Pair<A,B> implements Tuple {
    private A a;
    private B b;


    // ******************** Constructors **************************************
    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }


    // ******************** Methods *******************************************
    public A getA() { return a; }
    public void setA(final A a) { this.a = a; }

    public B getB() { return b; }
    public void setB(final B b) { this.b = b; }

    @Override public int size() { return 2; }

    @Override public Object getValueAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a;    }
            case 1  -> { return this.b;    }
            default -> { return null; }
        }
    }

    @Override public Class getTypeAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a.getClass(); }
            case 1  -> { return this.b.getClass(); }
            default -> { return null; }
        }
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("a").append(QUOTES).append(COLON).append(QUOTES).append(a).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("b").append(QUOTES).append(COLON).append(QUOTES).append(b).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return a.equals(pair.a) && b.equals(pair.b);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b);
    }
}
