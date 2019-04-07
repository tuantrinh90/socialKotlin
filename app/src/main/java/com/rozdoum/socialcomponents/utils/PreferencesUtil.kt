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

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtil {

    private val TAG = PreferencesUtil::class.java.simpleName

    private val SHARED_PREFERENCES_NAME = "com.rozdoum.socialcomponents"
    private val PREF_PARAM_IS_PROFILE_CREATED = "isProfileCreated"
    private val PREF_PARAM_IS_POSTS_WAS_LOADED_AT_LEAST_ONCE = "isPostsWasLoadedAtLeastOnce"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun isProfileCreated(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(PREF_PARAM_IS_PROFILE_CREATED, false)
    }

    fun isPostWasLoadedAtLeastOnce(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(PREF_PARAM_IS_POSTS_WAS_LOADED_AT_LEAST_ONCE, false)
    }

    fun setProfileCreated(context: Context, isProfileCreated: Boolean?) {
        getSharedPreferences(context).edit().putBoolean(PREF_PARAM_IS_PROFILE_CREATED, isProfileCreated!!).commit()
    }

    fun setPostWasLoadedAtLeastOnce(context: Context, isPostWasLoadedAtLeastOnce: Boolean?) {
        getSharedPreferences(context).edit().putBoolean(PREF_PARAM_IS_POSTS_WAS_LOADED_AT_LEAST_ONCE, isPostWasLoadedAtLeastOnce!!).commit()
    }

    fun clearPreferences(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
