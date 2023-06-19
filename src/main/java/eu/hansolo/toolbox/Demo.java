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

package eu.hansolo.toolbox;

import eu.hansolo.toolbox.Helper.SystemSummary;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;
import eu.hansolo.toolbox.evt.type.GeoLocationChangeEvt;
import eu.hansolo.toolbox.evt.type.ListChangeEvt;
import eu.hansolo.toolbox.evt.type.MapChangeEvt;
import eu.hansolo.toolbox.evt.type.MatrixItemChangeEvt;
import eu.hansolo.toolbox.evt.type.PropertyChangeEvt;
import eu.hansolo.toolbox.evtbus.EvtBus;
import eu.hansolo.toolbox.evtbus.Subscriber;
import eu.hansolo.toolbox.evtbus.Topic;
import eu.hansolo.toolbox.geo.GeoLocation;
import eu.hansolo.toolbox.geo.GeoLocationBuilder;
import eu.hansolo.toolbox.observables.ObservableList;
import eu.hansolo.toolbox.observables.ObservableMap;
import eu.hansolo.toolbox.observables.ObservableMatrix;
import eu.hansolo.toolbox.properties.BooleanProperty;
import eu.hansolo.toolbox.properties.DoubleProperty;
import eu.hansolo.toolbox.properties.IntegerProperty;
import eu.hansolo.toolbox.properties.ObjectProperty;
import eu.hansolo.toolbox.properties.ReadOnlyBooleanProperty;
import eu.hansolo.toolbox.properties.ReadOnlyDoubleProperty;
import eu.hansolo.toolbox.statemachine.State;
import eu.hansolo.toolbox.statemachine.StateChangeException;
import eu.hansolo.toolbox.statemachine.StateMachine;
import eu.hansolo.toolbox.time.DateTimes;
import eu.hansolo.toolbox.time.Dates;
import eu.hansolo.toolbox.time.Times;
import eu.hansolo.toolbox.tuples.Pair;
import eu.hansolo.toolbox.tuples.Quartet;
import eu.hansolo.toolbox.tuples.Triplet;
import eu.hansolo.toolbox.unit.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.toolbox.unit.Category.BLOOD_GLUCOSE;
import static eu.hansolo.toolbox.unit.Category.LENGTH;
import static eu.hansolo.toolbox.unit.Category.TEMPERATURE;
import static eu.hansolo.toolbox.unit.Category.VOLUME;
import static eu.hansolo.toolbox.unit.UnitDefinition.CELSIUS;
import static eu.hansolo.toolbox.unit.UnitDefinition.CENTIMETER;
import static eu.hansolo.toolbox.unit.UnitDefinition.CUBIC_METER;
import static eu.hansolo.toolbox.unit.UnitDefinition.FAHRENHEIT;
import static eu.hansolo.toolbox.unit.UnitDefinition.INCHES;
import static eu.hansolo.toolbox.unit.UnitDefinition.KELVIN;
import static eu.hansolo.toolbox.unit.UnitDefinition.LITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.METER;
import static eu.hansolo.toolbox.unit.UnitDefinition.MILLIGRAM_PER_DECILITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.MILLIMOL_PER_LITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.NANOMETER;


public class Demo {
    private PoJo                   pojo;
    private DoubleProperty         doubleProperty;
    private ObjectProperty<String> objectProperty;
    private IntegerProperty        integerProperty;
    private DoubleProperty         doubleProperty1;
    private ReadOnlyDoubleProperty readOnlyDoubleProperty;


    public Demo() {
        propertiesDemo();

        tuplesDemo();

        converterDemo();

        observableListDemo();

        observableMapDemo();

        observableMatrixDemo();

        datesDemo();

        timesDemo();

        dateTimesDemo();

        stateMachineDemo();

        evtBusDemo();

        helperDemo();

        geoDemo();
    }

    private void propertiesDemo() {
        System.out.println("\n-------------------- properties demo --------------------");
        pojo = new PoJo();
        pojo.valueProperty().addObserver(evt -> System.out.println("Value changed from " + evt.getOldValue() + " to " + evt.getValue()));

        EvtObserver<PropertyChangeEvt<Double>> doubleChangeObserver = e -> System.out.println(e.getOldValue() + " -> " + e.getValue());

        // Setup properties
        doubleProperty = new DoubleProperty() {
            @Override protected void willChange(final Double oldValue, final Double newValue) {
                System.out.println("\nDoubleProperty will change from " + oldValue + " to " + newValue + "\n");
            }
            @Override protected void didChange(final Double oldValue, final Double newValue) {
                System.out.println("\nDoubleProperty changed from " + oldValue + " to " + newValue + "\n");
            }
        };

        doubleProperty.addOnChange(doubleChangeObserver);

        doubleProperty.removeObserver(doubleChangeObserver);

        doubleProperty.removeAllObservers();


        objectProperty = new ObjectProperty<>();

        integerProperty = new IntegerProperty(10) {
            @Override public void set(final int VALUE) { super.set(VALUE); }
            @Override public int get() { return super.get(); }
            @Override protected void didChange(final Integer oldValue, final Integer newValue) { System.out.println("Color changed to: " + newValue); }
            @Override public Object getBean() { return Demo.this; }
            @Override public String getName() { return "color"; }
        };

        doubleProperty1 = new DoubleProperty(Demo.this, "oldValue", 10);

        readOnlyDoubleProperty = new ReadOnlyDoubleProperty(5);


        // Register listeners
        pojo.doubleValueProperty().addOnChange(e -> System.out.println("DoubleProperty: " + e.getOldValue() + " -> " + e.getValue()));

        doubleProperty.addObserver(e -> System.out.println("DoubleProperty: " + e.getOldValue() + " -> " + e.getValue()));

        objectProperty.addObserver(e -> System.out.println("ObjectProperty<String>: " + e.getOldValue() + " -> " + e.getValue()));



        // Set values
        pojo.setDoubleValue(7);

        pojo.setValue(5);

        pojo.valueProperty().set(8);

        doubleProperty.set(20);

        objectProperty.set(new String("Hallo"));

        objectProperty.set(new String("Test"));

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
        System.out.println("\npropertyC.set(20)");
        propertyC.set(20);
        System.out.println("\npropertyC = " + propertyC.get());
        System.out.println("propertyD = " + propertyD.get());
        System.out.println("\npropertyD.unbind()");
        propertyD.unbind();
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyD.set(5)");
        propertyD.set(5);
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
        System.out.println("\npropertyC.set(10)");
        propertyC.set(10);
        System.out.println("\nProperty C: " + propertyC.get() + " is bound bidirectional: " + propertyC.isBoundBidirectional());
        System.out.println("Property D: " + propertyD.get() + " is bound bidirectional: " + propertyD.isBoundBidirectional());
    }

    private void tuplesDemo() {
        System.out.println("\n-------------------- tuples demo --------------------");
        Pair<Double, Integer> pair = new Pair(5.0, 3);
        Triplet<Double, Integer, Long> triplet = new Triplet(5.0, 3, 500);
        Quartet<Double, Integer, String, Long> quartet = new Quartet(1.0, 5, "Test", 1000);
        System.out.println("Quartet size      : " + quartet.size());
        System.out.println("Quartet value at 2: " + quartet.getValueAt(2));
        System.out.println("Quartet type at 2 : " + quartet.getTypeAt(2));
    }

    private void converterDemo() {
        System.out.println("\n-------------------- converter demo --------------------");
        Converter temperatureConverter = new Converter(TEMPERATURE, CELSIUS); // Type Temperature with BaseUnit Celsius
        double    celsius              = 32.0;
        double    fahrenheit           = temperatureConverter.convert(celsius, FAHRENHEIT);
        double    kelvin               = temperatureConverter.convert(celsius, KELVIN);
        System.out.println(celsius + Constants.DEGREE + "C   =>   " + fahrenheit + Constants.DEGREE + "F    =>   " + kelvin + Constants.DEGREE + "K");

        /*
        Converter cssConverter = new Converter(CSS_UNITS, PX);
        double    point = 12;
        double    pixel = cssConverter.convert(point, PX);
        System.out.println(point + "pt  =>  " + pixel + "px");
        */

        Converter lengthConverter = new Converter(LENGTH, METER); // Type Length with BaseUnit Meter
        double    meter           = 1.0;
        double    inches          = lengthConverter.convert(meter, INCHES);
        double    nanometer       = lengthConverter.convert(inches, NANOMETER);
        System.out.println(meter + " " + lengthConverter.getUnitShort() + "   =>   " + inches + " in   =>   " + nanometer + " nm");


        Converter volumeConverter = new Converter(VOLUME, CUBIC_METER);
        double    cubicMeter      = 3;
        double liters = volumeConverter.convert(cubicMeter, LITER);
        System.out.println(cubicMeter + " cubic meter -> " + liters + " liter");

        Converter literConverter = new Converter(VOLUME, LITER);
        double liter = 3000;
        double cubicMeters = literConverter.convert(liter, CUBIC_METER);
        System.out.println(liter + " liter -> " + cubicMeters + " cubic meter");

        Converter glucoseConverter = new Converter(BLOOD_GLUCOSE, MILLIMOL_PER_LITER);
        double millimolPerLiter = 6.0;
        double milligramPerDeciliter = glucoseConverter.convert(millimolPerLiter, MILLIGRAM_PER_DECILITER);
        System.out.println(millimolPerLiter + "mmol/l -> " + milligramPerDeciliter + "mg/dl");

        Converter mgdlConverter = new Converter(BLOOD_GLUCOSE, MILLIGRAM_PER_DECILITER);
        double mgdl = 108.108108;
        double mmoll = mgdlConverter.convert(mgdl, MILLIMOL_PER_LITER);
        System.out.println(mgdl + " mg/dl -> " + mmoll + " mmol/l");

        // Convert meter to centimeter
        System.out.println(lengthConverter.convertToString(meter, CENTIMETER));

        // Shorten long numbers
        System.out.println(Converter.format(1_500_000, 1));

        System.out.println(Converter.format(1_000_000, 0));
    }

    private void observableListDemo() {
        System.out.println("\n-------------------- observable list demo --------------------");
        ObservableList<String> observableList = new ObservableList<>();
        observableList.addListChangeObserver(ListChangeEvt.ANY, e -> {
            EvtType<? extends ListChangeEvt<String>> type = e.getEvtType();
            if (ListChangeEvt.CHANGED.equals(type)) {
                System.out.println("List changed");
            } else if (ListChangeEvt.ADDED.equals(type)) {
                e.getAddedElements().forEach(item -> System.out.println("Added: " + item));
            } else if (ListChangeEvt.REMOVED.equals(type)) {
                e.getRemovedElements().forEach(item -> System.out.println("Removed: " + item));
            }
        });
        System.out.println("---------- adding ----------");
        observableList.add("Gerrit");
        observableList.add("Sandra");
        observableList.add("Lilli");
        observableList.add("Anton");
        observableList.add("Neo");
        System.out.println("---------- remove 1 ----------");
        observableList.remove("Neo");
        System.out.println("---------- add list of 3 ----------");
        observableList.addAll(List.of("Test", "Test2", "Test3"));
        System.out.println("---------- remove 1 ----------");
        observableList.remove("Test2");
        System.out.println("---------- add 1 ----------");
        observableList.add(2, "Neo");
        System.out.println("---------- print all ----------");
        observableList.forEach(item -> System.out.println(item));
        System.out.println("---------- retain all (Gerrit, Sandra, Lilli, Anton, Neo) ----------");
        List<String> keep = List.of("Gerrit", "Sandra", "Lilli", "Anton", "Neo");
        observableList.retainAll(keep);
        System.out.println("---------- clear ----------");
        observableList.clear();
    }

    private void observableMapDemo() {
        System.out.println("\n-------------------- observable map demo --------------------");
        ObservableMap<String, Integer> observableMap = new ObservableMap<>();
        observableMap.addMapChangeObserver(MapChangeEvt.ANY, e -> {
            EvtType<? extends MapChangeEvt<String, Integer>> type = e.getEvtType();
            if (MapChangeEvt.MODIFIED.equals(type)) {
                e.getModifiedEntries().forEach(entry -> System.out.println("Modified: " + entry.getKey() + " -> " + entry.getValue()));
            } else if (MapChangeEvt.ADDED.equals(type)) {
                e.getAddedEntries().forEach(entry -> System.out.println("Added   : " + entry.getKey() + " -> " + entry.getValue()));
            } else if (MapChangeEvt.REMOVED.equals(type)) {
                e.getRemovedEntries().forEach(entry -> System.out.println("Removed : " + entry.getKey() + " -> " + entry.getValue()));
            }
        });
        // Add single entries
        observableMap.put("Gerrit", 52);
        observableMap.put("Sandra", 50);
        observableMap.put("Lilli", 18);
        observableMap.put("Anton", 13);
        observableMap.put("Neo", 3);
        System.out.println("---------- remove 1 ----------");
        observableMap.remove("Neo");
        System.out.println("---------- add map of 3 ----------");
        observableMap.putAll(Map.of("Test", 1, "Test2", 2, "Test3", 3));
        System.out.println("---------- remove 1 ----------");
        observableMap.remove("Test2");
        System.out.println("---------- add 1 ----------");
        observableMap.put("Neo", 3);
        System.out.println("---------- print all ----------");
        observableMap.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
        System.out.println("---------- clear ----------");
        observableMap.clear();
    }

    private void observableMatrixDemo() {
        System.out.println("\n-------------------- observable matrix demo --------------------");
        final Random rnd  = new Random();
        final int    cols = 3;
        final int    rows = 2;
        ObservableMatrix<Integer> integerMatrix = new ObservableMatrix<>(Integer.class, cols, rows);
        integerMatrix.addMatrixItemChangeObserver(MatrixItemChangeEvt.ANY, e -> {
            EvtType<? extends MatrixItemChangeEvt<Integer>> type = e.getEvtType();
            if (MatrixItemChangeEvt.ITEM_ADDED.equals(type)) {
                System.out.println("Item added  : " + e.getItem() + " at " + e.getX() + ", " + e.getY());
            } else if (MatrixItemChangeEvt.ITEM_REMOVED.equals(type)) {
                System.out.println("Item removed: " + e.getOldItem() + " at " + e.getX() + ", " + e.getY());
            } else if (MatrixItemChangeEvt.ITEM_CHANGED.equals(type)) {
                System.out.println("Item changed: " + e.getItem() + " at " + e.getX() + ", " + e.getY());
            }
        });

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Integer value = rnd.nextInt(10);
                integerMatrix.setItemAt(x, y, value);
            }
        }
        integerMatrix.removeItemAt(0, 0);
        integerMatrix.setItemAt(2, 0, 5);
    }

    private void datesDemo() {
        System.out.println("\n-------------------- dates demo --------------------");
        LocalDate localDate = LocalDate.of(2022, 12, 03);
        System.out.println(Dates.dd_MM_yyyy.format(localDate));
        System.out.println(Dates.dd_MMMM_yyyy.format(localDate));
        System.out.println(Dates.yyyy_w.format(localDate));
        System.out.println(Dates.yyyy_w_e.format(localDate));
        System.out.println(Dates.yyyywe.format(localDate));
    }

    private void timesDemo() {
        System.out.println("\n-------------------- times demo --------------------");
        LocalTime localTime = LocalTime.now();
        System.out.println(Times.HH_mm_ss_SSSS.format(localTime));
        System.out.println(Times.HH_mm.format(localTime));
        System.out.println(Times.HHmmss.format(localTime));
        System.out.println(Times.HHmmss_SSSS.format(localTime));
    }

    private void dateTimesDemo() {
        System.out.println("\n-------------------- date times demo --------------------");
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println(DateTimes.toEpoch(zonedDateTime));
        System.out.println(DateTimes.dd_MM_yyyy_HH_mm_ss_SSSS.format(zonedDateTime));
    }

    private void stateMachineDemo() {
        System.out.println("\n-------------------- state machine demo --------------------");
        enum MyState implements State {
            // Available states
            IDLE("IDLE"),
            BUSY("BUSY"),
            ERROR("ERROR"),
            FINISHED("FINISHED");


            // Definition of state transitions
            static {
                IDLE.canTransitionTo(IDLE, BUSY, ERROR);
                BUSY.canTransitionTo(IDLE, BUSY, ERROR);
                ERROR.canTransitionTo(IDLE, ERROR);
            }

            private final String name;
            private       Set    transitions;


            // ******************** Constructor ***************************************
            MyState(String name) {
                this.name = name;
            }


            // ******************** Private Methods ***********************************
            private void canTransitionTo(final MyState... transitions) { this.transitions = EnumSet.copyOf(Arrays.asList(transitions)); }


            // ******************** Public Methods ************************************
            @Override public Set<State> getTransitions() { return this.transitions; }

            @Override public boolean canChangeTo(final State state) { return this.transitions.contains(state); }

            @Override public String getName() { return this.name; }
        }
        StateMachine<MyState> stateMachine = new StateMachine<>() {
            private ObjectProperty<MyState> state = new ObjectProperty<>(MyState.IDLE);


            // ******************** Public Methods ****************************
            @Override public State getState() { return this.state.get(); }

            @Override public void setState(final MyState state) throws StateChangeException {
                if (this.state.get().canChangeTo(state)) {
                    this.state.set(state);
                } else {
                    throw new StateChangeException("Not allowed to change from " + this.state.get().getName() + " to " + state.getName());
                }
            }
            @Override public ObjectProperty<MyState> stateProperty() { return state; }
        };

        stateMachine.stateProperty().addObserver(e -> System.out.println("State changed from " + e.getOldValue().getName() + " to " + e.getValue().getName()));
        try {
            stateMachine.setState(MyState.BUSY);
            stateMachine.setState(MyState.IDLE);
            stateMachine.setState(MyState.ERROR);
            stateMachine.setState(MyState.BUSY);
        } catch (StateChangeException e) {
            System.out.println(e.getMessage() + " -> StateMachine still in state: " + stateMachine.getState().getName());
        }
    }

    private void evtBusDemo() {
        System.out.println("\n-------------------- evtbus demo --------------------");
        class MyTopic implements Topic {
            private final String id;
            private final String name;


            // ******************** Constructors **********************************
            public MyTopic(final String name) {
                this.id   = UUID.randomUUID().toString();
                this.name = name;
            }


            // ******************** Methods ***************************************
            @Override public String getId() { return id; }

            @Override public String getName() { return name; }

            @Override public boolean equals(final Object o) {
                if (this == o) { return true; }
                if (o == null || getClass() != o.getClass()) { return false; }
                MyTopic myTopic = (MyTopic) o;
                return id.equals(myTopic.id);
            }

            @Override public int hashCode() { return Objects.hash(id); }
        }

        class Msg {
            private final String id;
            private final String txt;


            // ******************** Constructors **********************************
            public Msg(final String txt) {
                this.id  = UUID.randomUUID().toString();
                this.txt = txt;
            }


            // ******************** Methods ***************************************
            public String getId() { return id; }

            public String getTxt() { return txt; }

            @Override public String toString() { return txt; }

            @Override public boolean equals(final Object o) {
                if (this == o) { return true; }
                if (o == null || getClass() != o.getClass()) { return false; }
                Msg msg = (Msg) o;
                return id.equals(msg.id);
            }

            @Override public int hashCode() { return Objects.hash(id); }
        }

        class TopicEvt extends ChangeEvt {
            public static final EvtType<TopicEvt> ANY        = new EvtType<>(ChangeEvt.ANY, "ANY");
            public static final EvtType<TopicEvt> NEW_MSG    = new EvtType<>(TopicEvt.ANY, "NEW_MSG");
            public static final EvtType<TopicEvt> UPDATE_MSG = new EvtType<>(TopicEvt.ANY, "UPDATE_MSG");

            private final Msg msg;


            // ******************** Constructors **************************************
            public TopicEvt(final Object src, final EvtType<TopicEvt> evtType, final Msg msg) {
                super(src, evtType);
                this.msg = msg;
            }



            // ******************** Methods *******************************************
            public Msg getMsg() { return msg; }

            @Override public EvtType<? extends TopicEvt> getEvtType() { return (EvtType<? extends TopicEvt>) super.getEvtType(); }

            @Override public boolean equals(final Object o) {
                if (this == o) { return true; }
                if (o == null || getClass() != o.getClass()) { return false; }
                if (!super.equals(o)) { return false; }
                TopicEvt that = (TopicEvt) o;
                return Objects.equals(that.getMsg(), this.getMsg());
            }

            @Override public int hashCode() {
                return Objects.hash(super.hashCode(), msg);
            }
        }

        class TopicEvtBus implements EvtBus {
            private final Map<Topic, Map<EvtType, List<Subscriber>>> topicSubscribers = new ConcurrentHashMap<>();


            // ******************** Methods *******************************************
            @Override public <T extends Evt> void publish(final Topic topic, final T evt) {
                final EvtType type = evt.getEvtType();
                if (topicSubscribers.containsKey(topic)) {
                    Map<EvtType, List<Subscriber>> subscribers = topicSubscribers.get(topic);
                    subscribers.entrySet()
                               .stream()
                               .filter(entry -> entry.getKey().equals(TopicEvt.ANY))
                               .forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
                    if (subscribers.containsKey(type) && !type.equals(TopicEvt.ANY)) {
                        subscribers.get(type).forEach(subscriber -> subscriber.handle(evt));
                    }
                }
            }

            @Override public void subscribe(final Topic topic, final Subscriber subscriber) {
                final EvtType evtType = subscriber.getEvtType();
                if (!topicSubscribers.containsKey(topic))                           { topicSubscribers.put(topic, new ConcurrentHashMap<>()); }
                if (!topicSubscribers.get(topic).containsKey(evtType))              { topicSubscribers.get(topic).put(evtType, new CopyOnWriteArrayList<>()); }
                if (!topicSubscribers.get(topic).get(evtType).contains(subscriber)) { topicSubscribers.get(topic).get(evtType).add(subscriber); }
            }

            @Override public void unsubscribe(final Topic topic, final Subscriber subscriber) {
                final EvtType evtType = subscriber.getEvtType();
                if (topicSubscribers.containsKey(topic)) {
                    if (topicSubscribers.get(topic).containsKey(evtType)) {
                        if (topicSubscribers.get(topic).get(evtType).contains(subscriber)) {
                            // Remove subscriber
                            topicSubscribers.get(topic).get(evtType).remove(subscriber);

                            // Remove evtType from map if it has no subscribers
                            if (topicSubscribers.get(topic).get(evtType).isEmpty()) { topicSubscribers.get(topic).remove(evtType); }

                            // Remove topic from map if it has no subscribers
                            if (topicSubscribers.get(topic).isEmpty()) { topicSubscribers.remove(topic); }
                        }
                    }
                }
            }
        }

        class TopicSubscriber implements Subscriber {
            private final String  name;
            private final EvtType evtType;


            // ******************** Constructors **************************************
            public TopicSubscriber(final String name, final EvtType evtType) {
                this.name    = name;
                this.evtType = evtType;
            }


            // ******************** Methods *******************************************
            public String getName() { return name; }

            @Override public EvtType<Evt> getEvtType() { return evtType; }

            @Override public void handle(final Evt evt) {
                TopicEvt topicEvt = (TopicEvt) evt;
                System.out.println(name + ": " + " -> " + topicEvt.getMsg().getTxt());
            }
        }

        // Test
        TopicEvtBus eventBus = new TopicEvtBus();
        MyTopic topic1 = new MyTopic("Topic 1");
        MyTopic topic2 = new MyTopic("Topic 2");

        TopicSubscriber newMsgSubscriber = new TopicSubscriber("newMsgSubscriber", TopicEvt.NEW_MSG);
        TopicSubscriber updateSubscriber = new TopicSubscriber("updateSubscriber", TopicEvt.UPDATE_MSG);

        eventBus.subscribe(topic1, newMsgSubscriber);
        eventBus.subscribe(topic2, updateSubscriber);

        eventBus.publish(topic1, new TopicEvt(eventBus, TopicEvt.NEW_MSG, new Msg("New Msg topic 1")));
        eventBus.publish(topic1, new TopicEvt(eventBus, TopicEvt.UPDATE_MSG, new Msg("Update Msg topic 1")));
        eventBus.publish(topic2, new TopicEvt(eventBus, TopicEvt.UPDATE_MSG, new Msg("Update Msg topic 2")));
    }

    private void helperDemo() {
        System.out.println("\n-------------------- helper demo --------------------");
        SystemSummary systemSummary = Helper.getSystemSummary();
        System.out.println(systemSummary.toBeautifiedString());
    }

    private void geoDemo() {
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



        home.addGeoLocationObserver(GeoLocationChangeEvt.NAME_CHANGED, e -> System.out.println("Name changed from: " + e.getOldGeoLocation().getName() + " to " + e.getGeoLocation().getName()));

        System.out.println("Distance from Home to Azul: " + String.format(Locale.US, "%.2f km", (home.getDistanceTo(azul) / 1000)));
        home.setName("Home of Han Solo");
    }

    public static void main(String[] args) {
        new Demo();
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
                        System.out.println("\nValue will change from " + oldValue + " to " + newValue + "\n");
                    }
                    @Override protected void didChange(final Double oldValue, final Double newValue) {
                        System.out.println("\nValue changed from " + oldValue + " to " + newValue + "\n");
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
