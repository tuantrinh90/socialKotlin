/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rozdoum.socialcomponents.utils

import android.util.Log


import java.util.Date
import java.util.HashMap

/**
 * Created by Kristina on 6/7/16.
 */
object LogUtil {
    private val TIMING_ENABLED = true
    private val isDebugEnabled = true
    private val INFO_ENABLED = true
    private val LOG_MAPS_TILE_SEARCH = false

    private val TIMING = "Timing"

    private val timings = HashMap<String, Long>()

    fun logTimeStart(tag: String, operation: String) {
        if (isDebugEnabled && TIMING_ENABLED) {
            timings[tag + operation] = Date().time
            Log.i(TIMING, "$tag: $operation started")
        }
    }

    fun logTimeStop(tag: String, operation: String) {
        if (isDebugEnabled && TIMING_ENABLED) {
            if (timings.containsKey(tag + operation)) {
                Log.i(TIMING, tag + ": " + operation + " finished for "
                        + (Date().time - timings[tag + operation]) / 1000 + "sec")
            }
        }
    }

    fun logDebug(tag: String, message: String) {
        if (isDebugEnabled) {
            Log.d(tag, message)
        }
    }

    fun logInfo(tag: String, message: String) {
        if (INFO_ENABLED) {
            Log.i(tag, message)
        }
    }

    fun logMapTileSearch(tag: String, message: String) {
        if (isDebugEnabled && LOG_MAPS_TILE_SEARCH) {
            Log.d(tag, message)
        }
    }

    fun logError(tag: String, message: String, e: Exception) {
        Log.e(tag, message, e)
    }

    fun logError(tag: String, message: String, e: Error) {
        Log.e(tag, message, e)
    }
}
