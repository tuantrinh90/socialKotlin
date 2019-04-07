/*
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
 */

package com.rozdoum.socialcomponents.managers

import android.content.Context
import android.net.Uri
import android.util.Log

import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.main.interactors.FollowInteractor
import com.rozdoum.socialcomponents.main.interactors.PostInteractor
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostCreatedListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostListChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnTaskCompleteListener
import com.rozdoum.socialcomponents.model.FollowingPost
import com.rozdoum.socialcomponents.model.Like
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Kristina on 10/28/16.
 */

class PostManager private constructor(private val context: Context) : FirebaseListenersManager() {
    var newPostsCounter = 0
        private set
    private var postCounterWatcher: PostCounterWatcher? = null
    private val postInteractor: PostInteractor

    init {
        postInteractor = PostInteractor.getInstance(context)
    }

    fun createOrUpdatePost(post: Post) {
        try {
            postInteractor.createOrUpdatePost(post)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    fun getPostsList(onDataChangedListener: OnPostListChangedListener<Post>, date: Long) {
        postInteractor.getPostList(onDataChangedListener, date)
    }

    fun getPostsListByUser(onDataChangedListener: OnDataChangedListener<Post>, userId: String) {
        postInteractor.getPostListByUser(onDataChangedListener, userId)
    }

    fun getPost(context: Context, postId: String, onPostChangedListener: OnPostChangedListener) {
        val valueEventListener = postInteractor.getPost(postId, onPostChangedListener)
        addListenerToMap(context, valueEventListener)
    }

    fun getSinglePostValue(postId: String, onPostChangedListener: OnPostChangedListener) {
        postInteractor.getSinglePost(postId, onPostChangedListener)
    }

    fun createOrUpdatePostWithImage(imageUri: Uri, onPostCreatedListener: OnPostCreatedListener, post: Post) {
        postInteractor.createOrUpdatePostWithImage(imageUri, onPostCreatedListener, post)
    }

    fun removePost(post: Post, onTaskCompleteListener: OnTaskCompleteListener) {
        postInteractor.removePost(post, onTaskCompleteListener)
    }

    fun addComplain(post: Post) {
        postInteractor.addComplainToPost(post)
    }

    fun hasCurrentUserLike(activityContext: Context, postId: String, userId: String, onObjectExistListener: OnObjectExistListener<Like>) {
        val valueEventListener = postInteractor.hasCurrentUserLike(postId, userId, onObjectExistListener)
        addListenerToMap(activityContext, valueEventListener)
    }

    fun hasCurrentUserLikeSingleValue(postId: String, userId: String, onObjectExistListener: OnObjectExistListener<Like>) {
        postInteractor.hasCurrentUserLikeSingleValue(postId, userId, onObjectExistListener)
    }

    fun isPostExistSingleValue(postId: String, onObjectExistListener: OnObjectExistListener<Post>) {
        postInteractor.isPostExistSingleValue(postId, onObjectExistListener)
    }

    fun incrementWatchersCount(postId: String) {
        postInteractor.incrementWatchersCount(postId)
    }

    fun incrementNewPostsCounter() {
        newPostsCounter++
        notifyPostCounterWatcher()
    }

    fun clearNewPostsCounter() {
        newPostsCounter = 0
        notifyPostCounterWatcher()
    }

    fun setPostCounterWatcher(postCounterWatcher: PostCounterWatcher) {
        this.postCounterWatcher = postCounterWatcher
    }

    private fun notifyPostCounterWatcher() {
        if (postCounterWatcher != null) {
            postCounterWatcher!!.onPostCounterChanged(newPostsCounter)
        }
    }

    fun getFollowingPosts(userId: String, listener: OnDataChangedListener<FollowingPost>) {
        FollowInteractor.getInstance(context).getFollowingPosts(userId, listener)
    }

    fun searchByTitle(searchText: String, onDataChangedListener: OnDataChangedListener<Post>) {
        closeListeners(context)
        val valueEventListener = postInteractor.searchPostsByTitle(searchText, onDataChangedListener)
        addListenerToMap(context, valueEventListener)
    }

    fun filterByLikes(limit: Int, onDataChangedListener: OnDataChangedListener<Post>) {
        closeListeners(context)
        val valueEventListener = postInteractor.filterPostsByLikes(limit, onDataChangedListener)
        addListenerToMap(context, valueEventListener)
    }

    interface PostCounterWatcher {
        fun onPostCounterChanged(newValue: Int)
    }

    companion object {

        private val TAG = PostManager::class.java.simpleName
        private var instance: PostManager? = null

        fun getInstance(context: Context): PostManager {
            if (instance == null) {
                instance = PostManager(context)
            }

            return instance as PostManager
        }
    }
}
