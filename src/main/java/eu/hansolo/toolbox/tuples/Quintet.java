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


public class Quintet<A,B,C,D,E> implements Tuple {
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;


    // ******************** Constructors **************************************
    public Quintet(final A a, final B b, final C c, final D d, final E e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }


    // ******************** Methods *******************************************
    public A getA() { return a; }
    public void setA(final A a) { this.a = a; }

    public B getB() { return b; }
    public void setB(final B b) { this.b = b; }

    public C getC() { return c; }
    public void setC(final C c) { this.c = c; }

    public D getD() { return d; }
    public void setD(final D d) { this.d = d; }

    public E getE() { return e; }
    public void setE(final E e) { this.e = e; }

    @Override public int size() { return 5; }

    @Override public Object getValueAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a; }
            case 1  -> { return this.b; }
            case 2  -> { return this.c; }
            case 3  -> { return this.d; }
            case 4  -> { return this.e; }
            default -> { return null; }
        }
    }

    @Override public Class getTypeAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a.getClass(); }
            case 1  -> { return this.b.getClass(); }
            case 2  -> { return this.c.getClass(); }
            case 3  -> { return this.d.getClass(); }
            case 4  -> { return this.e.getClass(); }
            default -> { return null; }
        }
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("a").append(QUOTES).append(COLON).append(QUOTES).append(a).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("b").append(QUOTES).append(COLON).append(QUOTES).append(b).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("c").append(QUOTES).append(COLON).append(QUOTES).append(c).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("d").append(QUOTES).append(COLON).append(QUOTES).append(d).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("e").append(QUOTES).append(COLON).append(QUOTES).append(e).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Quintet<?, ?, ?, ?, ?> quintet = (Quintet<?, ?, ?, ?, ?>) o;
        return a.equals(quintet.a) && b.equals(quintet.b) && c.equals(quintet.c) && d.equals(quintet.d) && e.equals(quintet.e);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b, c, d, e);
    }
}
