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

package com.rozdoum.socialcomponents.managers

import android.content.Context

import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.ApplicationHelper

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by alexey on 19.12.16.
 */

open class FirebaseListenersManager {
    internal var activeListeners: MutableMap<Context, List<ValueEventListener>> = HashMap()

    internal fun addListenerToMap(context: Context, valueEventListener: ValueEventListener) {
        if (activeListeners.containsKey(context)) {
            activeListeners[context].add(valueEventListener)
        } else {
            val valueEventListeners = ArrayList<ValueEventListener>()
            valueEventListeners.add(valueEventListener)
            activeListeners[context] = valueEventListeners
        }
    }

    fun closeListeners(context: Context) {
        val databaseHelper = ApplicationHelper.databaseHelper
        if (activeListeners.containsKey(context)) {
            for (listener in activeListeners[context]) {
                databaseHelper!!.closeListener(listener)
            }
            activeListeners.remove(context)
        }
    }
}
