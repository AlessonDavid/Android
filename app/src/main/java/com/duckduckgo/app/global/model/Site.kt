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

package com.duckduckgo.app.global.model

import android.net.Uri
import com.duckduckgo.app.privacy.model.HttpsStatus
import com.duckduckgo.app.privacy.model.TermsOfService
import com.duckduckgo.app.trackerdetection.model.TrackerNetwork
import com.duckduckgo.app.trackerdetection.model.TrackingEvent

interface Site {

    val url: String
    val uri: Uri?
    var title: String?
    val https: HttpsStatus
    val termsOfService: TermsOfService
    val memberNetwork: TrackerNetwork?
    val trackingEvents: List<TrackingEvent>
    val trackerCount: Int
    val distinctTrackersByNetwork: Map<String, List<TrackingEvent>>
    val networkCount: Int
    val majorNetworkCount: Int
    val hasTrackerFromMajorNetwork: Boolean
    val allTrackersBlocked: Boolean
    val hasObscureTracker: Boolean
    var hasHttpResources: Boolean
    fun trackerDetected(event: TrackingEvent)

}