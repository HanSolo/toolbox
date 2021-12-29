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

import java.util.Collections;
import java.util.List;


public class Statistics {
    public static final double getMean(final List<Double> data) { return data.stream().mapToDouble(v -> v).sum() / data.size(); }

    public static final double getVariance(final List<Double> data) {
        double mean = getMean(data);
        double temp = 0;
        for (double a : data) { temp += ((a - mean) * (a - mean)); }
        return temp / data.size();
    }

    public static final double getStdDev(final List<Double> data) { return Math.sqrt(getVariance(data)); }

    public static final double getMedian(final List<Double> data) {
        int size = data.size();
        Collections.sort(data);
        return size % 2 == 0 ? (data.get((size / 2) - 1) + data.get(size / 2)) / 2.0 : data.get(size / 2);
    }

    public static final double getMin(final List<Double> data) { return data.stream().mapToDouble(v -> v).min().orElse(0); }

    public static final double getMax(final List<Double> data) { return data.stream().mapToDouble(v -> v).max().orElse(0); }

    public static final double getAverage(final List<Double> DATA) {
        return DATA.stream().mapToDouble(data -> data.doubleValue()).average().orElse(-1);
    }

    public static final double percentile(List<Double> entries, double percentile) {
        Collections.sort(entries);
        final int index = (int) Math.ceil(percentile / 100.0 * entries.size());
        return entries.get(index-1);
    }
}
