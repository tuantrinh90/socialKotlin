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

package com.rozdoum.socialcomponents.main.main

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.PostStatus
import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.main.postDetails.PostDetailsActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Alexey on 03.05.18.
 */

internal class MainPresenter(context: Context) : BasePresenter<MainView>(context) {

    private val postManager: PostManager

    init {
        postManager = PostManager.getInstance(context)
    }


    fun onCreatePostClickAction(anchorView: View) {
        if (checkInternetConnection(anchorView)) {
            if (checkAuthorization()) {
                ifViewAttached { it.openCreatePostActivity() }
            }
        }
    }

    fun onPostClicked(post: Post, postView: View) {
        postManager.isPostExistSingleValue(post.id!!, { exist ->
            ifViewAttached { view ->
                if (exist) {
                    view.openPostDetailsActivity(post, postView)
                } else {
                    view.showFloatButtonRelatedSnackBar(R.string.error_post_was_removed)
                }
            }
        })
    }

    fun onProfileMenuActionClicked() {
        if (checkAuthorization()) {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            ifViewAttached { view -> view.openProfileActivity(userId, null) }
        }
    }

    fun onPostCreated() {
        ifViewAttached { view ->
            view.refreshPostList()
            view.showFloatButtonRelatedSnackBar(R.string.message_post_was_created)
        }
    }

    fun onPostUpdated(data: Intent?) {
        if (data != null) {
            ifViewAttached { view ->
                val postStatus = data.getSerializableExtra(PostDetailsActivity.POST_STATUS_EXTRA_KEY) as PostStatus
                if (postStatus == PostStatus.REMOVED) {
                    view.removePost()
                    view.showFloatButtonRelatedSnackBar(R.string.message_post_was_removed)
                } else if (postStatus == PostStatus.UPDATED) {
                    view.updatePost()
                }
            }
        }
    }

    fun updateNewPostCounter() {
        val mainHandler = Handler(context.mainLooper)
        mainHandler.post {
            ifViewAttached { view ->
                val newPostsQuantity = postManager.newPostsCounter
                if (newPostsQuantity > 0) {
                    view.showCounterView(newPostsQuantity)
                } else {
                    view.hideCounterView()
                }
            }
        }
    }

    fun initPostCounter() {
        postManager.setPostCounterWatcher({ newValue -> updateNewPostCounter() })
    }
}
