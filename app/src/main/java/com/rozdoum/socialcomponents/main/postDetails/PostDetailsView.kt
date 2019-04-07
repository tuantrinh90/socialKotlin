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

package com.rozdoum.socialcomponents.main.postDetails

import android.view.View

import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.model.Comment
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Alexey on 03.05.18.
 */

interface PostDetailsView : BaseView {

    val commentText: String

    fun onPostRemoved()

    fun openImageDetailScreen(imagePath: String)

    fun openProfileActivity(authorId: String, authorView: View)

    fun setTitle(title: String)

    fun setDescription(description: String)

    fun loadPostDetailImage(imagePath: String)

    fun loadAuthorPhoto(photoUrl: String)

    fun setAuthorName(username: String)

    fun initLikeController(post: Post)

    fun updateCounters(post: Post)

    fun initLikeButtonState(exist: Boolean)

    fun showComplainMenuAction(show: Boolean)

    fun showEditMenuAction(show: Boolean)

    fun showDeleteMenuAction(show: Boolean)

    fun clearCommentField()

    fun scrollToFirstComment()

    fun openEditPostActivity(post: Post)

    fun showCommentProgress(show: Boolean)

    fun showCommentsWarning(show: Boolean)

    fun showCommentsRecyclerView(show: Boolean)

    fun onCommentsListChanged(list: List<Comment>)

    fun showCommentsLabel(show: Boolean)
}
