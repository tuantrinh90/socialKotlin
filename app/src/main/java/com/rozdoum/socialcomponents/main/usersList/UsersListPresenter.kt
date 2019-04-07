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

package com.rozdoum.socialcomponents.main.usersList

import android.app.Activity
import android.content.Context

import com.google.firebase.auth.FirebaseAuth
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.managers.FollowManager
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.views.FollowButton

/**
 * Created by Alexey on 03.05.18.
 */

internal class UsersListPresenter(private val activity: Activity) : BasePresenter<UsersListView>(activity) {

    private val followManager: FollowManager
    private val currentUserId: String?

    init {

        followManager = FollowManager.getInstance(context)
        currentUserId = FirebaseAuth.getInstance().uid
    }

    fun loadFollowings(userID: String, isRefreshing: Boolean) {
        if (checkInternetConnection()) {
            if (!isRefreshing) {
                ifViewAttached { it.showLocalProgress() }
            }

            FollowManager.getInstance(context).getFollowingsIdsList(userID, { list ->
                ifViewAttached { view ->
                    view.hideLocalProgress()
                    view.onProfilesIdsListLoaded(list)
                    if (list.size > 0) {
                        view.hideEmptyListMessage()
                    } else {
                        val message = context.getString(R.string.message_empty_list, context.getString(R.string.title_followings))
                        view.showEmptyListMessage(message)
                    }
                }
            })
        }
    }

    fun loadFollowers(userID: String, isRefreshing: Boolean) {
        if (checkInternetConnection()) {
            if (!isRefreshing) {
                ifViewAttached { it.showLocalProgress() }
            }

            FollowManager.getInstance(context).getFollowersIdsList(userID, { list ->
                ifViewAttached { view ->
                    view.hideLocalProgress()
                    view.onProfilesIdsListLoaded(list)

                    if (list.size > 0) {
                        view.hideEmptyListMessage()
                    } else {
                        val message = context.getString(R.string.message_empty_list, context.getString(R.string.title_followers))
                        view.showEmptyListMessage(message)
                    }

                }
            })
        }
    }

    fun onRefresh(userId: String, userListType: Int) {
        loadUsersList(userId, userListType, true)
    }

    fun loadUsersList(userId: String, userListType: Int, isRefreshing: Boolean) {
        if (userListType == UsersListType.FOLLOWERS) {
            loadFollowers(userId, isRefreshing)
        } else if (userListType == UsersListType.FOLLOWINGS) {
            loadFollowings(userId, false)
        }
    }

    fun chooseActivityTitle(userListType: Int) {
        ifViewAttached { view ->
            if (userListType == UsersListType.FOLLOWERS) {
                view.setTitle(R.string.title_followers)
            } else if (userListType == UsersListType.FOLLOWINGS) {
                view.setTitle(R.string.title_followings)
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

    fun onFollowButtonClick(state: Int, targetUserId: String) {
        if (checkInternetConnection() && checkAuthorization()) {
            if (state == FollowButton.FOLLOW_STATE || state == FollowButton.FOLLOW_BACK_STATE) {
                followUser(targetUserId)
            } else if (state == FollowButton.FOLLOWING_STATE) {
                unfollowUser(targetUserId)
            }
        }
    }
}
