/*
 *
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
 *
 */

package com.rozdoum.socialcomponents.managers

import android.app.Activity
import android.content.Context

import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.main.interactors.FollowInteractor
import com.rozdoum.socialcomponents.managers.listeners.OnCountChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnRequestComplete
import com.rozdoum.socialcomponents.utils.LogUtil

class FollowManager private constructor(private val context: Context) : FirebaseListenersManager() {
    private val followInteractor: FollowInteractor

    init {
        followInteractor = FollowInteractor.getInstance(context)
    }

    fun checkFollowState(myId: String, userId: String, checkStateListener: CheckStateListener) {
        doesUserFollowMe(myId, userId, { userFollowMe ->

            doIFollowUser(myId, userId, { iFollowUser ->
                val followState: FollowState

                if (userFollowMe && iFollowUser) {
                    followState = FollowState.FOLLOW_EACH_OTHER
                } else if (userFollowMe) {
                    followState = FollowState.USER_FOLLOW_ME
                } else if (iFollowUser) {
                    followState = FollowState.I_FOLLOW_USER
                } else {
                    followState = FollowState.NO_ONE_FOLLOW
                }

                checkStateListener.onStateReady(followState)

                LogUtil.logDebug(TAG, "checkFollowState, state: $followState")
            })
        })
    }


    fun doesUserFollowMe(myId: String, userId: String, onObjectExistListener: OnObjectExistListener<*>) {
        followInteractor.isFollowingExist(userId, myId, onObjectExistListener)
    }

    fun doIFollowUser(myId: String, userId: String, onObjectExistListener: OnObjectExistListener<*>) {
        followInteractor.isFollowingExist(myId, userId, onObjectExistListener)
    }

    fun followUser(activity: Activity, currentUserId: String, targetUserId: String, onRequestComplete: OnRequestComplete) {
        followInteractor.followUser(activity, currentUserId, targetUserId, onRequestComplete)
    }

    fun unfollowUser(activity: Activity, currentUserId: String, targetUserId: String, onRequestComplete: OnRequestComplete) {
        followInteractor.unfollowUser(activity, currentUserId, targetUserId, onRequestComplete)
    }

    fun getFollowersCount(activityContext: Context, targetUserId: String, onCountChangedListener: OnCountChangedListener<*>) {
        val listener = followInteractor.getFollowersCount(targetUserId, onCountChangedListener)
        addListenerToMap(activityContext, listener)
    }

    fun getFollowingsCount(activityContext: Context, targetUserId: String, onCountChangedListener: OnCountChangedListener<*>) {
        val listener = followInteractor.getFollowingsCount(targetUserId, onCountChangedListener)
        addListenerToMap(activityContext, listener)
    }

    fun getFollowingsIdsList(targetUserId: String,
                             onDataChangedListener: OnDataChangedListener<String>) {
        followInteractor.getFollowingsList(targetUserId, onDataChangedListener)
    }

    fun getFollowersIdsList(targetUserId: String,
                            onDataChangedListener: OnDataChangedListener<String>) {
        followInteractor.getFollowersList(targetUserId, onDataChangedListener)
    }

    interface CheckStateListener {
        fun onStateReady(followState: FollowState)
    }

    companion object {

        private val TAG = FollowManager::class.java.simpleName
        private var instance: FollowManager? = null

        fun getInstance(context: Context): FollowManager {
            if (instance == null) {
                instance = FollowManager(context)
            }

            return instance
        }
    }
}
