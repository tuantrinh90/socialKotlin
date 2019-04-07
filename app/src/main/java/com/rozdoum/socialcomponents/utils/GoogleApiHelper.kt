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

import android.support.v4.app.FragmentActivity

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.rozdoum.socialcomponents.R

object GoogleApiHelper {

    fun createGoogleApiClient(fragmentActivity: FragmentActivity): GoogleApiClient {
        val failedListener: GoogleApiClient.OnConnectionFailedListener

        if (fragmentActivity is GoogleApiClient.OnConnectionFailedListener) {
            failedListener = fragmentActivity
        } else {
            throw IllegalArgumentException(fragmentActivity.javaClass.simpleName + " should implement OnConnectionFailedListener")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragmentActivity.resources.getString(R.string.google_web_client_id))
                .requestEmail()
                .build()

        return GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, failedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }
}
