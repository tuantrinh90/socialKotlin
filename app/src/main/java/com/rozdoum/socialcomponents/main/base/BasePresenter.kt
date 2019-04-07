/*
 * Copyright 2018 Rozdoum
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

package com.rozdoum.socialcomponents.main.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.ProfileStatus
import com.rozdoum.socialcomponents.managers.ProfileManager

/**
 * Created by Alexey on 03.05.18.
 */

open class BasePresenter<T : BaseView>(protected var context: Context) : MvpBasePresenter<T>() where T : MvpView {

    protected var TAG = this.javaClass.simpleName
    private val profileManager: ProfileManager

    protected val currentUserId: String?
        get() = FirebaseAuth.getInstance().uid

    init {
        profileManager = ProfileManager.getInstance(context)
    }

    @JvmOverloads
    fun checkInternetConnection(anchorView: View? = null): Boolean {
        val hasInternetConnection = hasInternetConnection()
        if (!hasInternetConnection) {
            ifViewAttached { view ->
                if (anchorView != null) {
                    view.showSnackBar(anchorView, R.string.internet_connection_failed)
                } else {
                    view.showSnackBar(R.string.internet_connection_failed)
                }
            }
        }

        return hasInternetConnection
    }

    fun hasInternetConnection(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun checkAuthorization(): Boolean {
        val profileStatus = profileManager.checkProfile()
        if (profileStatus == ProfileStatus.NOT_AUTHORIZED || profileStatus == ProfileStatus.NO_PROFILE) {
            ifViewAttached { it.startLoginActivity() }
            return false
        } else {
            return true
        }
    }

    fun doAuthorization(status: ProfileStatus) {
        if (status == ProfileStatus.NOT_AUTHORIZED || status == ProfileStatus.NO_PROFILE) {
            ifViewAttached { it.startLoginActivity() }
        }
    }

}
