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

import android.text.Spannable
import android.view.View

import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile

/**
 * Created by Alexey on 03.05.18.
 */

interface ProfileView : BaseView {

    fun showUnfollowConfirmation(profile: Profile)

    fun updateFollowButtonState(followState: FollowState)

    fun updateFollowersCount(count: Int)

    fun updateFollowingsCount(count: Int)

    fun setFollowStateChangeResultOk()

    fun openPostDetailsActivity(post: Post, postItemView: View)

    fun startEditProfileActivity()

    fun openCreatePostActivity()

    fun setProfileName(username: String)

    fun setProfilePhoto(photoUrl: String)

    fun setDefaultProfilePhoto()

    fun updateLikesCounter(text: Spannable)

    fun hideLoadingPostsProgress()

    fun showLikeCounter(show: Boolean)

    fun updatePostsCounter(text: Spannable)

    fun showPostCounter(show: Boolean)

    fun onPostRemoved()

    fun onPostUpdated()

}
