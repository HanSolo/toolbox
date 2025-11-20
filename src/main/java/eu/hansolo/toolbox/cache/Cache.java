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

import java.util.List;
import java.util.Map;


public interface Cache<T extends Object, U extends Object> {
    void put(T key, U value);

    U get(T key);

    void remove(T key);
    void remove(final List<T> keysToRemove);

    void putAll(Map<T,U> entries);

    void clear();

    long size();

    boolean isEmpty();
}
