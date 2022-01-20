/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022 Gerrit Grunwald.
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

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class CountdownLock<T> {
    private       ScheduledExecutorService service;
    private final Duration                 timeout;
    private final AtomicBoolean            running;
    private final Command                  cmd;
    private final T                        param;
    private final AtomicLong               secondsToGo;


    // ******************** Constructors **************************************
    public CountdownLock(final Command cmd, final T param, final Duration timeout) {
        if (null == cmd || null == timeout) { throw new IllegalArgumentException("Parameters cannot be null"); }

        this.service     = Executors.newScheduledThreadPool(1);
        this.cmd         = cmd;
        this.param       = param;
        this.timeout     = timeout;
        this.running     = new AtomicBoolean(false);
        this.secondsToGo = new AtomicLong(timeout.getSeconds());
    }


    // ******************** Methods *******************************************
    public void start() {
        if (isRunning()) { return; }

        Runnable runnable = () -> {
            secondsToGo.decrementAndGet();
            if (secondsToGo.get() < 0) {
                cmd.execute(param);
                stop();
            }
        };
        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        running.set(true);
    }

    public void stop() {
        if (!isRunning()) { return; }

        service.shutdown();
        running.set(false);
        service = Executors.newScheduledThreadPool(1);
        secondsToGo.set(timeout.getSeconds());
    }

    public boolean isRunning() { return running.get(); }

    public long getSecondsToGo() { return secondsToGo.get() + 1; }


    // ******************** Inner Classes *************************************
    @FunctionalInterface
    public interface Command<T> {
        void execute(T value);
    }
}
