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

package eu.hansolo.toolbox.properties;

import eu.hansolo.toolbox.Demo;
import eu.hansolo.toolbox.Demo.PoJo;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.type.PropertyChangeEvt;
import org.junit.jupiter.api.Test;


public class PropertiesTest {

    @Test
    void testProperties() {

        System.out.println("\n-------------------- properties test --------------------");
        PoJo pojo = new PoJo();
        pojo.valueProperty().addObserver(evt -> System.out.println("pojo.valueProperty -> Value changed from " + evt.getOldValue() + " to " + evt.getValue()));
        pojo.valueProperty().addInvalidationObserver(evt -> System.out.println("pojo.valueProperty might have changed"));

        EvtObserver<PropertyChangeEvt<Double>> doubleChangeObserver = e -> System.out.println(e.getOldValue() + " -> " + e.getValue());

        // Setup properties
        DoubleProperty doubleProperty = new DoubleProperty() {
            @Override protected void willChange(final Double oldValue, final Double newValue) {
                System.out.println("DoubleProperty will change from " + oldValue + " to " + newValue);
            }
            @Override protected void didChange(final Double oldValue, final Double newValue) {
                System.out.println("DoubleProperty changed from " + oldValue + " to " + newValue);
            }
        };

        doubleProperty.addOnChange(doubleChangeObserver);

        doubleProperty.removeObserver(doubleChangeObserver);

        doubleProperty.removeAllObservers();


        ObjectProperty<String> objectProperty = new ObjectProperty<>();

        IntegerProperty integerProperty = new IntegerProperty(10) {
            @Override public void set(final int VALUE) { super.set(VALUE); }
            @Override public int get() { return super.get(); }
            @Override protected void didChange(final Integer oldValue, final Integer newValue) { System.out.println("Color changed to: " + newValue); }
            @Override public Object getBean() { return PropertiesTest.this; }
            @Override public String getName() { return "color"; }
        };

        DoubleProperty doubleProperty1 = new DoubleProperty(PropertiesTest.this, "oldValue", 10);

        ReadOnlyDoubleProperty readOnlyDoubleProperty = new ReadOnlyDoubleProperty(5);


        // Register listeners
        pojo.doubleValueProperty().addOnChange(e -> System.out.println("DoubleProperty: " + e.getOldValue() + " -> " + e.getValue()));

        doubleProperty.addObserver(e -> System.out.println("DoubleProperty: " + e.getOldValue() + " -> " + e.getValue()));

        objectProperty.addObserver(e -> System.out.println("ObjectProperty<String>: " + e.getOldValue() + " -> " + e.getValue()));



        // Set values
        pojo.setDoubleValue(7);

        System.out.println("\npojo.setValue(7)");
        pojo.setValue(7);

        System.out.println("\npojo.setValue(7)");
        pojo.setValue(7);

        System.out.println("\npojo.setValue(5)");
        pojo.setValue(5);

        System.out.println("\npojo.valueProperty().set(8)");
        pojo.valueProperty().set(8);

        System.out.println("\ndoubleProperty.set(20)");
        doubleProperty.set(20);

        System.out.println("\nobjectProperty.set(Hallo)");
        objectProperty.set(new String("Hallo"));

        System.out.println("\nobjectProperty.set(Test)");
        objectProperty.set(new String("Test"));

        System.out.println("\nobjectProperty.set(Bla)");
        objectProperty.set("Bla");



        // Bindings unidirectional
        System.out.println("\n\n---------- Unidirectional Binding ------------");
        DoubleProperty propertyA = new DoubleProperty(5);
        DoubleProperty propertyB = new DoubleProperty(10);

        System.out.println("Property A: " + propertyA.get() + " is bound: " + propertyA.isBound());
        System.out.println("Property B: " + propertyB.get() + " is bound: " + propertyB.isBound());
        System.out.println("\npropertyA.bind(propertyB)");
        propertyA.bind(propertyB);
        System.out.println("\nProperty A: " + propertyA.get() + " is bound: " + propertyA.isBound());
        System.out.println("Property B: " + propertyB.get() + " is bound: " + propertyB.isBound());
        System.out.println("\npropertyB.set(5)");
        propertyB.set(5);
        System.out.println("\npropertyB = " + propertyB.get());
        System.out.println("propertyA = " + propertyA.get());
        assert propertyB.get() == 5;
        assert propertyA.get() == 5;

        System.out.println("\npropertyA.set(20)");
        try {
            propertyA.set(20);
        } catch (IllegalArgumentException e) {
            System.out.println("Error, a bound value cannot be set.");
        }
        System.out.println("\npropertyA.unbind()");
        propertyA.unbind();
        System.out.println("\nProperty A: " + propertyA.get() + " is bound: " + propertyA.isBound());
        System.out.println("Property B: " + propertyB.get() + " is bound: " + propertyB.isBound());
        System.out.println("\npropertyB.set(15)");
        propertyB.set(15);
        System.out.println("\nProperty A: " + propertyA.get() + " is bound: " + propertyA.isBound());
        System.out.println("Property B: " + propertyB.get() + " is bound: " + propertyB.isBound());

        assert propertyA.get() == 5;
        assert propertyB.get() == 15;

        System.out.println("\nReadOnlyDoubleProperty: " + readOnlyDoubleProperty.getValue());
        System.out.println("DoubleProperty        : " + doubleProperty.getValue());
        System.out.println("Bind DoubleProperty -> ReadOnlyDoubleProperty");
        doubleProperty.bind(readOnlyDoubleProperty);
        System.out.println("DoubleProperty        : " + doubleProperty.getValue());
        System.out.println("Unbind DoubleProperty -> ReadOnlyDoubleProperty");
        doubleProperty.unbind();
        System.out.println("Set DoubleProperty -> 13");
        doubleProperty.set(13);
        System.out.println("DoubleProperty        : " + doubleProperty.getValue());

        assert doubleProperty.get() == 13;


        // Bindings bidirectional
        System.out.println("\n\n---------- Bidirectional Binding ------------");
        DoubleProperty propertyC = new DoubleProperty(0);
        DoubleProperty propertyD = new DoubleProperty(25);

        System.out.println("Property C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyC.bindBidirectional(propertyD)");
        propertyC.bindBidirectional(propertyD);
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyD.set(5)");
        propertyD.set(5);
        System.out.println("\npropertyC = " + propertyC.get());
        System.out.println("propertyD = " + propertyD.get());
        assert propertyD.get() == 5;
        assert propertyC.get() == 5;
        System.out.println("\npropertyC.set(20)");
        propertyC.set(20);
        System.out.println("\npropertyC = " + propertyC.get());
        System.out.println("propertyD = " + propertyD.get());
        assert propertyC.get() == 20;
        assert propertyD.get() == 20;
        System.out.println("\npropertyD.unbind()");
        propertyD.unbind();
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyD.set(5)");
        propertyD.set(5);
        assert propertyD.get() == 5;
        assert propertyC.get() == 20;
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyC.set(10)");
        propertyC.set(10);
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        assert propertyC.get() == 10;
        assert propertyD.get() == 5;
    }

    public class PoJo {
        private double          _value;
        private DoubleProperty  value;
        private DoubleProperty  doubleValue;
        private BooleanProperty booleanValue;


        // ******************** Constructors **************************************
        public PoJo() {
            _value       = 0;
            doubleValue  = new DoubleProperty(3);
            booleanValue = new BooleanProperty(true);
        }


        // ******************** Methods *******************************************
        public double getValue() { return null == value ? _value : value.get(); }
        public void setValue(final double value) {
            if (null == this.value) {
                _value = value;
            } else {
                this.value.set(value);
            }
        }
        public DoubleProperty valueProperty() {
            if (null == value) {
                value = new DoubleProperty(_value) {
                    @Override protected void willChange(final Double oldValue, final Double newValue) {
                        System.out.println("Value will change from " + oldValue + " to " + newValue);
                    }
                    @Override protected void didChange(final Double oldValue, final Double newValue) {
                        System.out.println("Value changed from " + oldValue + " to " + newValue);
                    }
                };
            }
            return value;
        }

        public double getDoubleValue() { return doubleValue.get(); }
        public void setDoubleValue(final double value) { doubleValue.set(value); }
        public DoubleProperty doubleValueProperty() { return doubleValue; }

        public boolean isBooleanValue() { return booleanValue.get(); }
        public ReadOnlyBooleanProperty booleanValueProperty() {
            return booleanValue;
        }
    }
}
