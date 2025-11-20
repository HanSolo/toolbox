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

package eu.hansolo.toolbox.cache;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GenCache<T extends Object, U extends Object> implements Cache<T, U> {
    private static final Logger                     LOGGER   = Logger.getLogger(GenCache.class.getName());
    private static final long                       INTERVAL = 5;
    private        final ConcurrentHashMap<T, U>    cache;
    private        final boolean                    withTimeout;
    private        final ConcurrentHashMap<T, Long> accessMap;
    private        final ScheduledExecutorService   executorService;


    // ******************** Constructors **************************************
    public GenCache() {
        this(-1);
    }
    public GenCache(final long timeout) {
        this(timeout, 128);
    }
    public GenCache(final long timeout, final int initialCapacity) {
        cache = new ConcurrentHashMap<>(initialCapacity, 0.9f, 1);
        if  (timeout > 0) {
            final long t = timeout < 1 ? INTERVAL : timeout;
            this.withTimeout     = true;
            this.accessMap       = new ConcurrentHashMap<>(16, 0.9f, 1);
            this.executorService = Executors.newSingleThreadScheduledExecutor();
            this.executorService.scheduleAtFixedRate(new Cleaner<T, U>(t, GenCache.this, this.accessMap), t, t, TimeUnit.SECONDS);
            LOGGER.log(Level.INFO, "Initialized Cache with timeout of " + t + " seconds");
        } else {
            this.withTimeout     = false;
            this.accessMap       = null;
            this.executorService = null;
            LOGGER.log(Level.INFO, "Initialized Cache without timeout");
        }
    }


    // ******************** Methods *******************************************
    @Override public void put(final T key, final U value) {
        if (null == key) { return; }
        if (null == value) {
            LOGGER.log(Level.INFO, "Package cannot be null -> removed key " + key);
            cache.remove(key);
        } else {
            cache.put(key, value);
            if (this.withTimeout) { accessMap.put(key, Instant.now().getEpochSecond()); }
        }
    }
    @Override public void putAll(final Map<T, U> entries) {
        cache.putAll(entries);
        if (this.withTimeout) {
            final Long now = Instant.now().getEpochSecond();
            entries.entrySet().forEach(entry -> accessMap.put(entry.getKey(), now));
        }
    }

    @Override public U get(final T key) {
        if (this.withTimeout) { accessMap.put(key, Instant.now().getEpochSecond()); }
        return cache.get(key);
    }

    @Override public void remove(final T key) {
        cache.remove(key);
        if (this.withTimeout) { accessMap.remove(key); }
    }
    @Override public void remove(final List<T> keysToRemove) {
        if (this.withTimeout) {
            keysToRemove.stream().parallel().forEach(key -> {
                cache.remove(key);
                accessMap.remove(key);
            });
        } else {
            keysToRemove.stream().parallel().forEach(cache::remove);
        }
    }

    @Override public void clear() {
        cache.clear();
        if (this.withTimeout) { accessMap.clear(); }
        LOGGER.log(Level.INFO, "Package cache cleared");
    }

    @Override public synchronized long size() { return cache.size(); }

    @Override public synchronized boolean isEmpty() { return cache.isEmpty(); }

    /**
     * Replaces all entries in the cache with the ones in the given patch
     *
     * @param patch
     */
    public void setAll(final Map<T, U> patch) {
        synchronized (cache) {
            cache.clear();
            cache.putAll(patch);
            final Long now = Instant.now().getEpochSecond();
            if (this.withTimeout) { patch.entrySet().forEach(entry -> accessMap.put(entry.getKey(), now)); }
            LOGGER.info("Package cache cleared and set with new data");
        }
    }

    /**
     *  Updates only existing entries with values from patch and doesn't add new key/value pairs
     * @param patch
     */
    public void updateExisting(final Map<T, U> patch) {
        if (this.withTimeout) {
            cache.entrySet().stream().filter(entry -> patch.containsKey(entry.getKey())).forEach(entry -> {
                entry.setValue(patch.get(entry.getKey()));
                accessMap.put(entry.getKey(), Instant.now().getEpochSecond());
            });
        } else {
            cache.entrySet().stream().filter(entry -> patch.containsKey(entry.getKey())).forEach(entry -> entry.setValue(patch.get(entry.getKey())));
        }
    }

    /**
     * Updates the cache with the values from the given patch map without updating
     * existing entries.
     *
     * @param patch Map that contains existing and new entries
     */
    public void synchronize(final Map<T, U> patch) {
        patch.forEach(cache::putIfAbsent);
        if (this.withTimeout) {
            final Long now = Instant.now().getEpochSecond();
            patch.entrySet().forEach(entry -> accessMap.putIfAbsent(entry.getKey(), now));
        }
    }

    /**
     * Updates the cache with the values from the given patch map including updates
     * of existing entries in cache. In addition entries that are in the cache but not
     * in the patch map can be removed if removeIfNotInPatch flag is true
     *
     * @param patch
     * @param removeIfNotInPatch
     */
    public void update(final Map<T, U> patch, final boolean removeIfNotInPatch) {
        if (this.withTimeout) {
            final Long now = Instant.now().getEpochSecond();
            patch.forEach((key, value) -> {
                cache.merge(key, value, (v1, v2) -> v1.equals(v2) ? v1 : v2);
                accessMap.put(key, now);
            });
            if (removeIfNotInPatch) {
                if (cache.size() > patch.size()) {
                    Map<T, U> toRemoveFromTarget = new HashMap<>();
                    cache.entrySet().stream().filter(entry -> !patch.containsKey(entry.getKey())).forEach(entry -> toRemoveFromTarget.put(entry.getKey(), entry.getValue()));
                    toRemoveFromTarget.keySet().forEach(key -> {
                        cache.remove(key);
                        accessMap.remove(key);
                    });
                }
            }
        } else {
            patch.forEach((key, value) -> cache.merge(key, value, (v1, v2) -> v1.equals(v2) ? v1 : v2));
            if (removeIfNotInPatch) {
                if (cache.size() > patch.size()) {
                    Map<T, U> toRemoveFromTarget = new HashMap<>();
                    cache.entrySet().stream().filter(entry -> !patch.containsKey(entry.getKey())).forEach(entry -> toRemoveFromTarget.put(entry.getKey(), entry.getValue()));
                    toRemoveFromTarget.keySet().forEach(key -> cache.remove(key));
                }
            }
        }
    }

    /**
     * Replaces all entries in the cache with values from the given patch. In addition
     * it removes entries which does not exist in the patch but in the cache.
     *
     * @param patch
     * @param removeIfNotInPatch
     */
    public void replace(final Map<T, U> patch, final boolean removeIfNotInPatch) {
        if (this.withTimeout) {
            final Long now = Instant.now().getEpochSecond();
            patch.forEach((key, value) -> {
                cache.replace(key, value);
                accessMap.replace(key, now);
            });
            if (removeIfNotInPatch) {
                if (cache.size() > patch.size()) {
                    Map<T, U> toRemoveFromTarget = new HashMap<>();
                    cache.entrySet().stream().filter(entry -> !patch.containsKey(entry.getKey())).forEach(entry -> toRemoveFromTarget.put(entry.getKey(), entry.getValue()));
                    toRemoveFromTarget.keySet().forEach(key -> {
                        cache.remove(key);
                        accessMap.remove(key);
                    });
                }
            }
        } else {
            patch.forEach((key, value) -> cache.replace(key, value));
            if (removeIfNotInPatch) {
                if (cache.size() > patch.size()) {
                    Map<T, U> toRemoveFromTarget = new HashMap<>();
                    cache.entrySet().stream().filter(entry -> !patch.containsKey(entry.getKey())).forEach(entry -> toRemoveFromTarget.put(entry.getKey(), entry.getValue()));
                    toRemoveFromTarget.keySet().forEach(key -> cache.remove(key));
                }
            }
        }
    }

    public boolean containsKey(final T key) { return cache.containsKey(key); }

    public Set<Entry<T, U>> getEntrySet() { return cache.entrySet(); }

    public Collection<T> getKeys() { return cache.keySet(); }

    public Collection<U> getValues() { return new ArrayList<>(cache.values()); }

    /**
     * Returns a shallow copy of the cache
     *
     * @return a shallow copy of the cache
     */
    public ConcurrentHashMap<T, U> getCopy() { return new ConcurrentHashMap<>(cache); }

    public boolean isWithTimeout() { return this.withTimeout; }

    public record Cleaner<T, U> (long timeout, GenCache<T, U> cache, Map<T, Long> accessMap) implements Runnable {
        @Override public void run() {
            final long    now      = Instant.now().getEpochSecond();
            final List<T> toRemove = accessMap.entrySet().stream().parallel().filter(entry -> (now - entry.getValue() > timeout)).map(entry -> entry.getKey()).toList();
            cache.remove(toRemove);
        }
    }
}
