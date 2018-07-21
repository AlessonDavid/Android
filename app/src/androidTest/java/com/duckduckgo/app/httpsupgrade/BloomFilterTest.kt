/*
 * Copyright (c) 2018 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.httpsupgrade

import com.duckduckgo.app.FileUtilities
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class BloomFilterTest {

    private lateinit var bloomData: List<String>
    private lateinit var testData: List<String>
    private lateinit var testee: BloomFilter

    @Before
    fun before() {
        bloomData = FileUtilities.loadLines("binary/bloom_https_data_sample")
        testData = FileUtilities.loadLines("binary/bloom_top_sites_sample")
        testee = BloomFilter(bloomData.size, TARGET_FALSE_POSITIVE_RATE)
        bloomData.forEach { testee.add(it) }
    }

    @Test
    fun whenTopSitesTestedThenFalsePositiveRateIsWithinBounds() {

        var falsePositives = 0
        var truePositives = 0
        var falseNegatives = 0
        var trueNegatives = 0

        for (element in testData) {

            val result = testee.contains(element)
            when {
                result == false && bloomData.contains(element) -> {
                    falseNegatives++
                    fail("True negatives should not occur")
                }
                result == true && bloomData.contains(element).not() -> {
                    falsePositives++
                    Timber.d("Site $element was a false positive. This can happen")
                }
                result == false && bloomData.contains(element).not() -> trueNegatives++
                result == true && bloomData.contains(element) -> truePositives++
            }
        }

        val falsePositiveRate = falsePositives / testData.size
        Timber.d("False positive $falsePositives")
        Timber.d("True positive $truePositives")
        Timber.d("False negative $falseNegatives")
        Timber.d("True negative $trueNegatives")
        Timber.d("False positive rate $falsePositiveRate")
        assertTrue(falsePositiveRate < TARGET_FALSE_POSITIVE_RATE)
    }

    companion object {
        const val TARGET_FALSE_POSITIVE_RATE = 0.001
    }
}