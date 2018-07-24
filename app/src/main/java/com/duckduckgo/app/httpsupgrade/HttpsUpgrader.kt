/*
 * Copyright (c) 2017 DuckDuckGo
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

import android.content.Context
import android.net.Uri
import android.support.annotation.WorkerThread
import com.duckduckgo.app.global.UrlScheme
import com.duckduckgo.app.global.isHttps
import timber.log.Timber
import java.io.File

interface HttpsUpgrader {

    @WorkerThread
    fun shouldUpgrade(uri: Uri) : Boolean

    fun upgrade(uri: Uri): Uri {
        return uri.buildUpon().scheme(UrlScheme.https).build()
    }
}

class HttpsUpgraderImpl(context: Context) :HttpsUpgrader {

    private var httpsBloomFilter: BloomFilter? = null

    init {
        val path = context.getFileStreamPath("HTTPS_BLOOM").path
        if (File(path).exists()) {
            httpsBloomFilter = BloomFilter(path, 2900000) //TODO get data from
        }
    }

    @WorkerThread
    override fun shouldUpgrade(uri: Uri) : Boolean {

        if (uri.isHttps) {
            return false
        }

        httpsBloomFilter?.let {
            val shouldUpgrade = it.contains(uri.host)
            Timber.d("Bloom, should upgrade ${uri.host}: $shouldUpgrade")
            return shouldUpgrade
        }

        return false
    }

}