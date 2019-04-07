/*
 *
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
 *
 */

package com.rozdoum.socialcomponents.main.interactors

import android.content.Context
import android.net.Uri
import android.util.Log

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Query
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.UploadTask
import com.rozdoum.socialcomponents.ApplicationHelper
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.UploadImagePrefix
import com.rozdoum.socialcomponents.managers.DatabaseHelper
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostCreatedListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostListChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnTaskCompleteListener
import com.rozdoum.socialcomponents.model.Like
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.PostListResult
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

/**
 * Created by Alexey on 05.06.18.
 */

class PostInteractor private constructor(private val context: Context) {

    private val databaseHelper: DatabaseHelper?

    init {
        databaseHelper = ApplicationHelper.databaseHelper
    }

    fun generatePostId(): String {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POSTS_DB_KEY)
                .push()
                .key
    }

    fun createOrUpdatePost(post: Post) {
        try {
            val postValues = post.toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates["/" + DatabaseHelper.POSTS_DB_KEY + "/" + post.id] = postValues

            databaseHelper!!.databaseReference.updateChildren(childUpdates)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    fun removePost(post: Post): Task<Void> {
        val postRef = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY).child(post.id!!)
        return postRef.removeValue()
    }

    fun incrementWatchersCount(postId: String) {
        val postRef = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY + "/" + postId + "/watchersCount")
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = 1
                } else {
                    mutableData.value = currentValue + 1
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError, b: Boolean, dataSnapshot: DataSnapshot) {
                LogUtil.logInfo(TAG, "Updating Watchers count transaction is completed.")
            }
        })
    }

    fun getPostList(onDataChangedListener: OnPostListChangedListener<Post>, date: Long) {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY)
        val postsQuery: Query
        if (date == 0L) {
            postsQuery = databaseReference.limitToLast(Constants.Post.POST_AMOUNT_ON_PAGE).orderByChild("createdDate")
        } else {
            postsQuery = databaseReference.limitToLast(Constants.Post.POST_AMOUNT_ON_PAGE).endAt(date.toDouble()).orderByChild("createdDate")
        }

        postsQuery.keepSynced(true)
        postsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val objectMap = dataSnapshot.value as Map<String, Any>?
                val result = parsePostList(objectMap)

                if (result.posts.isEmpty() && result.isMoreDataAvailable) {
                    getPostList(onDataChangedListener, result.lastItemCreatedDate - 1)
                } else {
                    onDataChangedListener.onListChanged(parsePostList(objectMap))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "getPostList(), onCancelled", Exception(databaseError.message))
                onDataChangedListener.onCanceled(context.getString(R.string.permission_denied_error))
            }
        })
    }

    fun getPostListByUser(onDataChangedListener: OnDataChangedListener<Post>, userId: String) {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY)
        val postsQuery: Query
        postsQuery = databaseReference.orderByChild("authorId").equalTo(userId)

        postsQuery.keepSynced(true)
        postsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = parsePostList(dataSnapshot.value as Map<String, Any>?)
                onDataChangedListener.onListChanged(result.posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "getPostListByUser(), onCancelled", Exception(databaseError.message))
            }
        })
    }

    fun getPost(id: String, listener: OnPostChangedListener): ValueEventListener {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY).child(id)
        val valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    if (isPostValid((dataSnapshot.value as Map<String, Any>?)!!)) {
                        val post = dataSnapshot.getValue(Post::class.java)
                        if (post != null) {
                            post.id = id
                        }
                        listener.onObjectChanged(post!!)
                    } else {
                        listener.onError(String.format(context.getString(R.string.error_general_post), id))
                    }
                } else {
                    listener.onObjectChanged(null!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "getPost(), onCancelled", Exception(databaseError.message))
            }
        })

        databaseHelper.addActiveListener(valueEventListener, databaseReference)
        return valueEventListener
    }

    fun getSinglePost(id: String, listener: OnPostChangedListener) {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY).child(id)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null && dataSnapshot.exists()) {
                    if (isPostValid((dataSnapshot.value as Map<String, Any>?)!!)) {
                        val post = dataSnapshot.getValue(Post::class.java)
                        post!!.id = id
                        listener.onObjectChanged(post)
                    } else {
                        listener.onError(String.format(context.getString(R.string.error_general_post), id))
                    }
                } else {
                    listener.onError(context.getString(R.string.message_post_was_removed))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "getSinglePost(), onCancelled", Exception(databaseError.message))
            }
        })
    }

    private fun parsePostList(objectMap: Map<String, Any>?): PostListResult {
        val result = PostListResult()
        val list = ArrayList<Post>()
        var isMoreDataAvailable = true
        var lastItemCreatedDate: Long = 0

        if (objectMap != null) {
            isMoreDataAvailable = Constants.Post.POST_AMOUNT_ON_PAGE == objectMap.size

            for (key in objectMap.keys) {
                val obj = objectMap[key]
                if (obj is Map<*, *>) {
                    val mapObj = obj as Map<String, Any>

                    if (!isPostValid(mapObj)) {
                        LogUtil.logDebug(TAG, "Invalid post, id: $key")
                        continue
                    }

                    val hasComplain = mapObj.containsKey("hasComplain") && mapObj["hasComplain"] as Boolean
                    val createdDate = mapObj["createdDate"] as Long

                    if (lastItemCreatedDate == 0L || lastItemCreatedDate > createdDate) {
                        lastItemCreatedDate = createdDate
                    }

                    if (!hasComplain) {
                        val post = Post()
                        post.id = key
                        post.title = mapObj["title"] as String
                        post.description = mapObj["description"] as String
                        post.imagePath = mapObj["imagePath"] as String
                        post.imageTitle = mapObj["imageTitle"] as String
                        post.authorId = mapObj["authorId"] as String
                        post.createdDate = createdDate
                        if (mapObj.containsKey("commentsCount")) {
                            post.commentsCount = mapObj["commentsCount"] as Long
                        }
                        if (mapObj.containsKey("likesCount")) {
                            post.likesCount = mapObj["likesCount"] as Long
                        }
                        if (mapObj.containsKey("watchersCount")) {
                            post.watchersCount = mapObj["watchersCount"] as Long
                        }
                        list.add(post)
                    }
                }
            }

            Collections.sort(list) { lhs, rhs -> rhs.createdDate.compareTo(lhs.createdDate) }

            result.posts = list
            result.lastItemCreatedDate = lastItemCreatedDate
            result.isMoreDataAvailable = isMoreDataAvailable
        }

        return result
    }

    private fun isPostValid(post: Map<String, Any>): Boolean {
        return (post.containsKey("title")
                && post.containsKey("description")
                && post.containsKey("imagePath")
                && post.containsKey("imageTitle")
                && post.containsKey("authorId")
                && post.containsKey("description"))
    }

    fun addComplainToPost(post: Post) {
        databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY).child(post.id!!).child("hasComplain").setValue(true)
    }

    fun isPostExistSingleValue(postId: String, onObjectExistListener: OnObjectExistListener<Post>) {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY).child(postId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onObjectExistListener.onDataChanged(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "isPostExistSingleValue(), onCancelled", Exception(databaseError.message))
            }
        })
    }

    fun subscribeToNewPosts() {
        FirebaseMessaging.getInstance().subscribeToTopic("postsTopic")
    }

    fun removePost(post: Post, onTaskCompleteListener: OnTaskCompleteListener) {
        val databaseHelper = ApplicationHelper.databaseHelper
        val removeImageTask = databaseHelper!!.removeImage(post.imageTitle!!)

        removeImageTask.addOnSuccessListener { aVoid ->
            removePost(post).addOnCompleteListener { task ->
                onTaskCompleteListener.onTaskComplete(task.isSuccessful)
                ProfileInteractor.getInstance(context).updateProfileLikeCountAfterRemovingPost(post)
                removeObjectsRelatedToPost(post.id)
                LogUtil.logDebug(TAG, "removePost(), is success: " + task.isSuccessful)
            }
            LogUtil.logDebug(TAG, "removeImage(): success")
        }.addOnFailureListener { exception ->
            LogUtil.logError(TAG, "removeImage()", exception)
            onTaskCompleteListener.onTaskComplete(false)
        }
    }

    private fun removeObjectsRelatedToPost(postId: String?) {
        CommentInteractor.getInstance(context).removeCommentsByPost(postId).addOnSuccessListener { aVoid -> LogUtil.logDebug(TAG, "Comments related to post with id: $postId was removed") }.addOnFailureListener { e -> LogUtil.logError(TAG, "Failed to remove comments related to post with id: " + postId!!, e) }

        removeLikesByPost(postId).addOnSuccessListener { aVoid -> LogUtil.logDebug(TAG, "Likes related to post with id: $postId was removed") }.addOnFailureListener { e -> LogUtil.logError(TAG, "Failed to remove likes related to post with id: " + postId!!, e) }
    }

    fun createOrUpdatePostWithImage(imageUri: Uri, onPostCreatedListener: OnPostCreatedListener, post: Post) {
        // Register observers to listen for when the download is done or if it fails
        val databaseHelper = ApplicationHelper.databaseHelper
        if (post.id == null) {
            post.id = generatePostId()
        }

        val imageTitle = ImageUtil.generateImageTitle(UploadImagePrefix.POST, post.id)
        val uploadTask = databaseHelper!!.uploadImage(imageUri, imageTitle)

        uploadTask?.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            onPostCreatedListener.onPostSaved(false)

        }?.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            val downloadUrl = taskSnapshot.downloadUrl
            LogUtil.logDebug(TAG, "successful upload image, image url: " + downloadUrl.toString())

            post.imagePath = downloadUrl.toString()
            post.imageTitle = imageTitle
            createOrUpdatePost(post)

            onPostCreatedListener.onPostSaved(true)
        }
    }

    fun createOrUpdateLike(postId: String, postAuthorId: String) {
        try {
            val authorId = FirebaseAuth.getInstance().currentUser!!.uid
            val mLikesReference = databaseHelper!!
                    .databaseReference
                    .child(DatabaseHelper.POST_LIKES_DB_KEY)
                    .child(postId)
                    .child(authorId)
            mLikesReference.push()
            val id = mLikesReference.push().key
            val like = Like(authorId)
            like.id = id

            mLikesReference.child(id).setValue(like, object : DatabaseReference.CompletionListener {
                override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
                    if (databaseError == null) {
                        val postRef = databaseHelper
                                .databaseReference
                                .child(DatabaseHelper.POSTS_DB_KEY + "/" + postId + "/likesCount")

                        incrementLikesCount(postRef)
                        val profileRef = databaseHelper
                                .databaseReference
                                .child(DatabaseHelper.PROFILES_DB_KEY + "/" + postAuthorId + "/likesCount")

                        incrementLikesCount(profileRef)
                    } else {
                        LogUtil.logError(TAG, databaseError.message, databaseError.toException())
                    }
                }

                private fun incrementLikesCount(postRef: DatabaseReference) {
                    postRef.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val currentValue = mutableData.getValue(Int::class.java)
                            if (currentValue == null) {
                                mutableData.value = 1
                            } else {
                                mutableData.value = currentValue + 1
                            }

                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(databaseError: DatabaseError, b: Boolean, dataSnapshot: DataSnapshot) {
                            LogUtil.logInfo(TAG, "Updating likes count transaction is completed.")
                        }
                    })
                }

            })
        } catch (e: Exception) {
            LogUtil.logError(TAG, "createOrUpdateLike()", e)
        }

    }

    fun removeLike(postId: String, postAuthorId: String) {
        val authorId = FirebaseAuth.getInstance().currentUser!!.uid
        val mLikesReference = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_LIKES_DB_KEY)
                .child(postId)
                .child(authorId)
        mLikesReference.removeValue(object : DatabaseReference.CompletionListener {
            override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
                if (databaseError == null) {
                    val postRef = databaseHelper
                            .databaseReference
                            .child(DatabaseHelper.POSTS_DB_KEY + "/" + postId + "/likesCount")
                    decrementLikesCount(postRef)

                    val profileRef = databaseHelper
                            .databaseReference
                            .child(DatabaseHelper.PROFILES_DB_KEY + "/" + postAuthorId + "/likesCount")
                    decrementLikesCount(profileRef)
                } else {
                    LogUtil.logError(TAG, databaseError.message, databaseError.toException())
                }
            }

            private fun decrementLikesCount(postRef: DatabaseReference) {
                postRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentValue = mutableData.getValue(Long::class.java)
                        if (currentValue == null) {
                            mutableData.value = 0
                        } else {
                            mutableData.value = currentValue - 1
                        }

                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(databaseError: DatabaseError, b: Boolean, dataSnapshot: DataSnapshot) {
                        LogUtil.logInfo(TAG, "Updating likes count transaction is completed.")
                    }
                })
            }
        })
    }

    fun hasCurrentUserLike(postId: String, userId: String, onObjectExistListener: OnObjectExistListener<Like>): ValueEventListener {
        val databaseReference = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_LIKES_DB_KEY)
                .child(postId)
                .child(userId)
        val valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onObjectExistListener.onDataChanged(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "hasCurrentUserLike(), onCancelled", Exception(databaseError.message))
            }
        })

        databaseHelper.addActiveListener(valueEventListener, databaseReference)
        return valueEventListener
    }

    fun hasCurrentUserLikeSingleValue(postId: String, userId: String, onObjectExistListener: OnObjectExistListener<Like>) {
        val databaseReference = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_LIKES_DB_KEY)
                .child(postId)
                .child(userId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onObjectExistListener.onDataChanged(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "hasCurrentUserLikeSingleValue(), onCancelled", Exception(databaseError.message))
            }
        })
    }

    fun removeLikesByPost(postId: String?): Task<Void> {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_LIKES_DB_KEY)
                .child(postId!!)
                .removeValue()
    }

    fun searchPostsByTitle(searchText: String, onDataChangedListener: OnDataChangedListener<Post>): ValueEventListener {
        val reference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY)
        val valueEventListener = getSearchQuery(reference, "title", searchText).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = parsePostList(dataSnapshot.value as Map<String, Any>?)
                onDataChangedListener.onListChanged(result.posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "searchPostsByTitle(), onCancelled", Exception(databaseError.message))
            }
        })

        databaseHelper.addActiveListener(valueEventListener, reference)

        return valueEventListener
    }

    fun filterPostsByLikes(limit: Int, onDataChangedListener: OnDataChangedListener<Post>): ValueEventListener {
        val reference = databaseHelper!!.databaseReference.child(DatabaseHelper.POSTS_DB_KEY)
        val valueEventListener = getFilteredQuery(reference, "likesCount", limit).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = parsePostList(dataSnapshot.value as Map<String, Any>?)
                onDataChangedListener.onListChanged(result.posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "filterPostsByLikes(), onCancelled", Exception(databaseError.message))
            }
        })

        databaseHelper.addActiveListener(valueEventListener, reference)

        return valueEventListener
    }

    private fun getSearchQuery(databaseReference: DatabaseReference, childOrderBy: String, searchText: String): Query {
        return databaseReference
                .orderByChild(childOrderBy)
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
    }

    private fun getFilteredQuery(databaseReference: DatabaseReference, childOrderBy: String, limit: Int): Query {
        return databaseReference
                .orderByChild(childOrderBy)
                .limitToLast(limit)
    }

    companion object {

        private val TAG = PostInteractor::class.java.simpleName
        private var instance: PostInteractor? = null

        fun getInstance(context: Context): PostInteractor {
            if (instance == null) {
                instance = PostInteractor(context)
            }

            return instance
        }
    }
}
