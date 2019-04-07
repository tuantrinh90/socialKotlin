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

package com.rozdoum.socialcomponents.main.post.editPost

import android.content.Context

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.post.BaseCreatePostPresenter
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Alexey on 03.05.18.
 */

internal class EditPostPresenter(context: Context) : BaseCreatePostPresenter<EditPostView>(context) {

    private var post: Post? = null

    protected override val saveFailMessage: Int
        get() = R.string.error_fail_update_post

    protected override val isImageRequired: Boolean
        get() = false

    fun setPost(post: Post) {
        this.post = post
    }

    private fun updatePostIfChanged(updatedPost: Post) {
        if (post!!.likesCount != updatedPost.likesCount) {
            post!!.likesCount = updatedPost.likesCount
        }

        if (post!!.commentsCount != updatedPost.commentsCount) {
            post!!.commentsCount = updatedPost.commentsCount
        }

        if (post!!.watchersCount != updatedPost.watchersCount) {
            post!!.watchersCount = updatedPost.watchersCount
        }

        if (post!!.isHasComplain != updatedPost.isHasComplain) {
            post!!.isHasComplain = updatedPost.isHasComplain
        }
    }

    override fun savePost(title: String, description: String) {
        ifViewAttached { view ->
            view.showProgress(R.string.message_saving)

            post!!.title = title
            post!!.description = description

            if (view.imageUri != null) {
                postManager.createOrUpdatePostWithImage(view.imageUri, this, post!!)
            } else {
                postManager.createOrUpdatePost(post!!)
                onPostSaved(true)
            }
        }
    }

    fun addCheckIsPostChangedListener() {
        PostManager.getInstance(context.applicationContext).getPost(context, post!!.id!!, object : OnPostChangedListener {
            override fun onObjectChanged(obj: Post) {
                if (obj == null) {
                    ifViewAttached { view ->
                        view.showWarningDialog(R.string.error_post_was_removed) { dialog, which ->
                            view.openMainActivity()
                            view.finish()
                        }
                    }
                } else {
                    updatePostIfChanged(obj)
                }
            }

            override fun onError(errorText: String) {
                ifViewAttached { view ->
                    view.showWarningDialog(errorText) { dialog, which ->
                        view.openMainActivity()
                        view.finish()
                    }
                }
            }
        })
    }

    fun closeListeners() {
        postManager.closeListeners(context)
    }
}
