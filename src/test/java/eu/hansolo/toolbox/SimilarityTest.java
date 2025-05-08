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

package eu.hansolo.toolbox;

import org.junit.jupiter.api.Test;


public class SimilarityTest {
    @Test
    void testSimilarity() {
        final String text1 = "GlucoStatusFx-23.0.0.jar";
        final String text2 = "GlucoStatusFx-23.0.1.jar";
        final String text3 = "GlucoStatusFx-jar.23.0.0";

        assert Helper.similarity(text1, text2)  > 0.9;
        assert Helper.similarity(text1, text3) > 0.6;
        assert Helper.similarity(text2, text3) > 0.6;
    }
}
