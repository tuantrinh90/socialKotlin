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
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.FragmentActivity

import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.google.firebase.iid.FirebaseInstanceId
import com.rozdoum.socialcomponents.main.interactors.ProfileInteractor

object LogoutHelper {

    private val TAG = LogoutHelper::class.java.simpleName
    private var clearImageCacheAsyncTask: ClearImageCacheAsyncTask? = null

    fun signOut(mGoogleApiClient: GoogleApiClient, fragmentActivity: FragmentActivity) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            ProfileInteractor.getInstance(fragmentActivity.applicationContext)
                    .removeRegistrationToken(FirebaseInstanceId.getInstance().token, user.uid)

            for (profile in user.providerData) {
                val providerId = profile.providerId
                logoutByProvider(providerId, mGoogleApiClient, fragmentActivity)
            }
            logoutFirebase(fragmentActivity.applicationContext)
        }

        if (clearImageCacheAsyncTask == null) {
            clearImageCacheAsyncTask = ClearImageCacheAsyncTask(fragmentActivity.applicationContext)
            clearImageCacheAsyncTask!!.execute()
        }
    }

    private fun logoutByProvider(providerId: String, mGoogleApiClient: GoogleApiClient, fragmentActivity: FragmentActivity) {
        when (providerId) {
            GoogleAuthProvider.PROVIDER_ID -> logoutGoogle(mGoogleApiClient, fragmentActivity)

            FacebookAuthProvider.PROVIDER_ID -> logoutFacebook(fragmentActivity.applicationContext)
        }
    }

    private fun logoutFirebase(context: Context) {
        FirebaseAuth.getInstance().signOut()
        PreferencesUtil.setProfileCreated(context, false)
    }

    private fun logoutFacebook(context: Context) {
        FacebookSdk.sdkInitialize(context)
        LoginManager.getInstance().logOut()
    }

    private fun logoutGoogle(mGoogleApiClient: GoogleApiClient?, fragmentActivity: FragmentActivity) {
        var mGoogleApiClient = mGoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiHelper.createGoogleApiClient(fragmentActivity)
        }

        if (!mGoogleApiClient!!.isConnected) {
            mGoogleApiClient.connect()
        }

        val finalMGoogleApiClient = mGoogleApiClient
        mGoogleApiClient.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                if (finalMGoogleApiClient.isConnected) {
                    Auth.GoogleSignInApi.signOut(finalMGoogleApiClient).setResultCallback { status ->
                        if (status.isSuccess) {
                            LogUtil.logDebug(TAG, "User Logged out from Google")
                        } else {
                            LogUtil.logDebug(TAG, "Error Logged out from Google")
                        }
                    }
                }
            }

            override fun onConnectionSuspended(i: Int) {
                LogUtil.logDebug(TAG, "Google API Client Connection Suspended")
            }
        })
    }

    private class ClearImageCacheAsyncTask(private val context: Context) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            Glide.get(context.applicationContext).clearDiskCache()
            return null
        }

        override fun onPostExecute(o: Void) {
            super.onPostExecute(o)
            clearImageCacheAsyncTask = null
            Glide.get(context.applicationContext).clearMemory()
        }
    }
}
