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

package com.rozdoum.socialcomponents.main.post.createPost

import android.content.Context

import com.google.firebase.auth.FirebaseAuth
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.post.BaseCreatePostPresenter
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Alexey on 03.05.18.
 */

class CreatePostPresenter(context: Context) : BaseCreatePostPresenter<CreatePostView>(context) {

    protected override val saveFailMessage: Int
        get() = R.string.error_fail_create_post

    protected override val isImageRequired: Boolean
        get() = true

    override fun savePost(title: String, description: String) {
        ifViewAttached { view ->
            view.showProgress(R.string.message_creating_post)
            val post = Post()
            post.title = title
            post.description = description
            post.authorId = FirebaseAuth.getInstance().currentUser!!.uid
            postManager.createOrUpdatePostWithImage(view.imageUri, this, post)
        }
    }
}
