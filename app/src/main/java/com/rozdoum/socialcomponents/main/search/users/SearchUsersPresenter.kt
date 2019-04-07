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


package com.rozdoum.socialcomponents.main.search.users

import android.app.Activity

import com.google.firebase.auth.FirebaseAuth
import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.managers.FollowManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.views.FollowButton

/**
 * Created by Alexey on 08.06.18.
 */
class SearchUsersPresenter(private val activity: Activity) : BasePresenter<SearchUsersView>(activity) {
    private val followManager: FollowManager
    private val currentUserId: String?
    private val profileManager: ProfileManager

    init {

        followManager = FollowManager.getInstance(context)
        currentUserId = FirebaseAuth.getInstance().uid
        profileManager = ProfileManager.getInstance(context.applicationContext)
    }

    fun onFollowButtonClick(state: Int, targetUserId: String) {
        if (checkInternetConnection() && checkAuthorization()) {
            if (state == FollowButton.FOLLOW_STATE || state == FollowButton.FOLLOW_BACK_STATE) {
                followUser(targetUserId)
            } else if (state == FollowButton.FOLLOWING_STATE) {
                unfollowUser(targetUserId)
            }
        }
    }

    private fun followUser(targetUserId: String) {
        ifViewAttached { it.showProgress() }
        followManager.followUser(activity, currentUserId!!, targetUserId, { success ->
            ifViewAttached { view ->
                view.hideProgress()
                if (success) {
                    view.updateSelectedItem()
                } else {
                    LogUtil.logDebug(TAG, "followUser, success: " + false)
                }
            }
        })
    }

    fun unfollowUser(targetUserId: String) {
        ifViewAttached { it.showProgress() }
        followManager.unfollowUser(activity, currentUserId!!, targetUserId, { success ->
            ifViewAttached { view ->
                view.hideProgress()
                if (success) {
                    view.updateSelectedItem()
                } else {
                    LogUtil.logDebug(TAG, "unfollowUser, success: " + false)
                }
            }
        })
    }

    fun loadUsersWithEmptySearch() {
        search("")
    }

    fun search(searchText: String) {
        if (checkInternetConnection()) {
            ifViewAttached { it.showLocalProgress() }
            profileManager.search(searchText, { list ->
                ifViewAttached { view ->
                    view.hideLocalProgress()
                    view.onSearchResultsReady(list)

                    if (list.isEmpty()) {
                        view.showEmptyListLayout()
                    }
                }

                LogUtil.logDebug(TAG, "search text: $searchText")
                LogUtil.logDebug(TAG, "found items count: " + list.size)
            })
        } else {
            ifViewAttached { it.hideLocalProgress() }
        }
    }

}
