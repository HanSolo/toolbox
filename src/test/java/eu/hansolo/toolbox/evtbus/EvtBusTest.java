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

package eu.hansolo.toolbox.evtbus;

import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class EvtBusTest {

    @Test
    void testEvtBus() {
        System.out.println("\n-------------------- evtbus test --------------------");

        // Create a topic class
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

        // Create a message class
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

        // Create a topic event class
        class TopicEvt extends ChangeEvt {
            public static final EvtType<TopicEvt> ANY     = new EvtType<>(ChangeEvt.ANY, "ANY");
            public static final EvtType<TopicEvt> NEW_MSG = new EvtType<>(TopicEvt.ANY, "NEW_MSG");
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

        // Create an event bus class
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

        // Create a subscriber class
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

        // Create an instance of the event bus
        TopicEvtBus eventBus = new TopicEvtBus();

        // Create some instances of our topic
        MyTopic topic1 = new MyTopic("Topic 1");
        MyTopic topic2 = new MyTopic("Topic 2");

        // Create subscribers for the different topic events
        TopicSubscriber newMsgSubscriber = new TopicSubscriber("newMsgSubscriber", TopicEvt.NEW_MSG);
        TopicSubscriber updateSubscriber = new TopicSubscriber("updateSubscriber", TopicEvt.UPDATE_MSG);

        // Subscribe to specific events on a topic using our subscribers
        eventBus.subscribe(topic1, newMsgSubscriber);
        eventBus.subscribe(topic2, updateSubscriber);

        // Publish some messages on our topics
        eventBus.publish(topic1, new TopicEvt(eventBus, TopicEvt.NEW_MSG, new Msg("New Msg topic 1")));
        eventBus.publish(topic1, new TopicEvt(eventBus, TopicEvt.UPDATE_MSG, new Msg("Update Msg topic 1")));

        eventBus.publish(topic2, new TopicEvt(eventBus, TopicEvt.UPDATE_MSG, new Msg("Update Msg topic 2")));
    }
}
