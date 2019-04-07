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

package com.rozdoum.socialcomponents.main.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.View

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.enums.PostStatus
import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.main.postDetails.PostDetailsActivity
import com.rozdoum.socialcomponents.managers.FollowManager
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.views.FollowButton

/**
 * Created by Alexey on 03.05.18.
 */

internal class ProfilePresenter(private val activity: Activity) : BasePresenter<ProfileView>(activity) {

    private val followManager: FollowManager
    private val profileManager: ProfileManager

    private var profile: Profile? = null

    init {
        profileManager = ProfileManager.getInstance(context)
        followManager = FollowManager.getInstance(context)
    }

    private fun followUser(targetUserId: String) {
        ifViewAttached { it.showProgress() }
        followManager.followUser(activity, currentUserId, targetUserId, { success ->
            ifViewAttached { view ->
                if (success) {
                    view.setFollowStateChangeResultOk()
                    checkFollowState(targetUserId)
                } else {
                    LogUtil.logDebug(TAG, "followUser, success: " + false)
                }
            }
        })
    }

    fun unfollowUser(targetUserId: String) {
        ifViewAttached { it.showProgress() }
        followManager.unfollowUser(activity, currentUserId, targetUserId, { success ->
            ifViewAttached { view ->
                if (success) {
                    view.setFollowStateChangeResultOk()
                    checkFollowState(targetUserId)
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
            } else if (state == FollowButton.FOLLOWING_STATE && profile != null) {
                ifViewAttached { view -> view.showUnfollowConfirmation(profile) }
            }
        }
    }

    fun checkFollowState(targetUserId: String) {
        val currentUserId = currentUserId

        if (currentUserId != null) {
            if (targetUserId != currentUserId) {
                followManager.checkFollowState(currentUserId, targetUserId, { followState ->
                    ifViewAttached { view ->
                        view.hideProgress()
                        view.updateFollowButtonState(followState)
                    }
                })
            } else {
                ifViewAttached { view -> view.updateFollowButtonState(FollowState.MY_PROFILE) }
            }
        } else {
            ifViewAttached { view -> view.updateFollowButtonState(FollowState.NO_ONE_FOLLOW) }
        }
    }

    fun getFollowersCount(targetUserId: String) {
        followManager.getFollowersCount(context, targetUserId, { count -> ifViewAttached { view -> view.updateFollowersCount(count.toInt()) } })
    }

    fun getFollowingsCount(targetUserId: String) {
        followManager.getFollowingsCount(context, targetUserId, { count -> ifViewAttached { view -> view.updateFollowingsCount(count.toInt()) } })
    }

    fun onPostClick(post: Post, postItemView: View) {
        PostManager.getInstance(context).isPostExistSingleValue(post.id!!, { exist ->
            ifViewAttached { view ->
                if (exist) {
                    view.openPostDetailsActivity(post, postItemView)
                } else {
                    view.showSnackBar(R.string.error_post_was_removed)
                }
            }
        })
    }

    fun buildCounterSpannable(label: String, value: Int): Spannable {
        val contentString = SpannableStringBuilder()
        contentString.append(value.toString())
        contentString.append("\n")
        val start = contentString.length
        contentString.append(label)
        contentString.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_Second_Light), start, contentString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return contentString
    }

    fun onEditProfileClick() {
        if (checkInternetConnection()) {
            ifViewAttached { it.startEditProfileActivity() }
        }
    }

    fun onCreatePostClick() {
        if (checkInternetConnection()) {
            ifViewAttached { it.openCreatePostActivity() }
        }
    }

    fun loadProfile(activityContext: Context, userID: String) {
        profileManager.getProfileValue(activityContext, userID, object : OnObjectChangedListenerSimple<Profile>() {
            override fun onObjectChanged(obj: Profile) {
                profile = obj
                ifViewAttached { view ->
                    view.setProfileName(profile!!.username)

                    if (profile!!.photoUrl != null) {
                        view.setProfilePhoto(profile!!.photoUrl)
                    } else {
                        view.setDefaultProfilePhoto()
                    }

                    val likesCount = profile!!.likesCount.toInt()
                    val likesLabel = context.resources.getQuantityString(R.plurals.likes_counter_format, likesCount, likesCount)
                    view.updateLikesCounter(buildCounterSpannable(likesLabel, likesCount))
                }
            }
        })
    }

    fun onPostListChanged(postsCount: Int) {
        ifViewAttached { view ->
            val postsLabel = context.resources.getQuantityString(R.plurals.posts_counter_format, postsCount, postsCount)
            view.updatePostsCounter(buildCounterSpannable(postsLabel, postsCount))
            view.showLikeCounter(true)
            view.showPostCounter(true)
            view.hideLoadingPostsProgress()


        }
    }

    fun checkPostChanges(data: Intent?) {
        ifViewAttached { view ->
            if (data != null) {
                val postStatus = data.getSerializableExtra(PostDetailsActivity.POST_STATUS_EXTRA_KEY) as PostStatus

                if (postStatus == PostStatus.REMOVED) {
                    view.onPostRemoved()
                } else if (postStatus == PostStatus.UPDATED) {
                    view.onPostUpdated()
                }
            }
        }
    }
}
