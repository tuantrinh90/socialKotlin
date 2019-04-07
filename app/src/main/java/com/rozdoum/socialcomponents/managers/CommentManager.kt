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

import android.content.Context

import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.main.interactors.CommentInteractor
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnTaskCompleteListener
import com.rozdoum.socialcomponents.model.Comment

class CommentManager private constructor(private val context: Context) : FirebaseListenersManager() {
    internal var commentInteractor: CommentInteractor

    init {
        commentInteractor = CommentInteractor.getInstance(context)
    }

    fun createOrUpdateComment(commentText: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener) {
        commentInteractor.createComment(commentText, postId, onTaskCompleteListener)
    }

    fun decrementCommentsCount(postId: String, onTaskCompleteListener: OnTaskCompleteListener) {
        commentInteractor.decrementCommentsCount(postId, onTaskCompleteListener)
    }

    fun getCommentsList(activityContext: Context, postId: String, onDataChangedListener: OnDataChangedListener<Comment>) {
        val valueEventListener = commentInteractor.getCommentsList(postId, onDataChangedListener)
        addListenerToMap(activityContext, valueEventListener)
    }

    fun removeComment(commentId: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener) {
        commentInteractor.removeComment(commentId, postId, onTaskCompleteListener)
    }

    fun updateComment(commentId: String, commentText: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener) {
        commentInteractor.updateComment(commentId, commentText, postId, onTaskCompleteListener)
    }

    companion object {

        private val TAG = CommentManager::class.java.simpleName
        private var instance: CommentManager? = null

        fun getInstance(context: Context): CommentManager {
            if (instance == null) {
                instance = CommentManager(context)
            }

            return instance
        }
    }
}
