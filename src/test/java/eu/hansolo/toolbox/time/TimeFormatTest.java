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

package eu.hansolo.toolbox.time;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;


public class TimeFormatTest {

    @Test
    void testTimeFormats() {
        System.out.println("\n-------------------- times test --------------------");
        final LocalTime time = LocalTime.of(12, 03, 00);

        assert "12:03:00.0000".equals(Times.HH_mm_ss_SSSS.format(time));
        assert "12:03".equals(Times.HH_mm.format(time));
        assert "120300".equals(Times.HHmmss.format(time));
        assert "120300.0000".equals(Times.HHmmss_SSSS.format(time));
    }
}
