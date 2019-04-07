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

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.ApplicationHelper
import com.rozdoum.socialcomponents.managers.DatabaseHelper
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnTaskCompleteListener
import com.rozdoum.socialcomponents.model.Comment
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.ArrayList
import java.util.Collections

/**
 * Created by Alexey on 05.06.18.
 */

class CommentInteractor private constructor(private val context: Context) {

    private val databaseHelper: DatabaseHelper?

    init {
        databaseHelper = ApplicationHelper.databaseHelper
    }

    fun createComment(commentText: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener?) {
        try {
            val authorId = FirebaseAuth.getInstance().currentUser!!.uid
            val mCommentsReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POST_COMMENTS_DB_KEY + "/" + postId)
            val commentId = mCommentsReference.push().key
            val comment = Comment(commentText)
            comment.id = commentId
            comment.authorId = authorId

            mCommentsReference.child(commentId).setValue(comment, object : DatabaseReference.CompletionListener {
                override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
                    if (databaseError == null) {
                        incrementCommentsCount(postId)
                    } else {
                        LogUtil.logError(TAG, databaseError.message, databaseError.toException())
                    }
                }

                private fun incrementCommentsCount(postId: String) {
                    val postRef = databaseHelper.databaseReference.child(DatabaseHelper.POSTS_DB_KEY + "/" + postId + "/commentsCount")
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
                            LogUtil.logInfo(TAG, "Updating comments count transaction is completed.")
                            onTaskCompleteListener?.onTaskComplete(true)
                        }
                    })
                }
            })
        } catch (e: Exception) {
            LogUtil.logError(TAG, "createComment()", e)
        }

    }

    fun updateComment(commentId: String, commentText: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener?) {
        val mCommentReference = databaseHelper!!.databaseReference.child(DatabaseHelper.POST_COMMENTS_DB_KEY).child(postId).child(commentId).child("text")
        mCommentReference.setValue(commentText).addOnSuccessListener { aVoid ->
            onTaskCompleteListener?.onTaskComplete(true)
        }.addOnFailureListener { e ->
            onTaskCompleteListener?.onTaskComplete(false)
            LogUtil.logError(TAG, "updateComment", e)
        }
    }

    fun decrementCommentsCount(postId: String, onTaskCompleteListener: OnTaskCompleteListener?) {
        val postRef = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POSTS_DB_KEY + "/" + postId + "/commentsCount")
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue != null && currentValue >= 1) {
                    mutableData.value = currentValue - 1
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError, b: Boolean, dataSnapshot: DataSnapshot) {
                LogUtil.logInfo(TAG, "Updating comments count transaction is completed.")
                onTaskCompleteListener?.onTaskComplete(true)
            }
        })
    }

    fun removeComment(commentId: String, postId: String, onTaskCompleteListener: OnTaskCompleteListener) {
        val reference = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_COMMENTS_DB_KEY)
                .child(postId)
                .child(commentId)
        reference.removeValue().addOnSuccessListener { aVoid -> decrementCommentsCount(postId, onTaskCompleteListener) }.addOnFailureListener { e ->
            onTaskCompleteListener.onTaskComplete(false)
            LogUtil.logError(TAG, "removeComment()", e)
        }
    }

    fun getCommentsList(postId: String, onDataChangedListener: OnDataChangedListener<Comment>): ValueEventListener {
        val databaseReference = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_COMMENTS_DB_KEY)
                .child(postId)
        val valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = ArrayList<Comment>()
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(Comment::class.java)
                    list.add(comment)
                }

                Collections.sort(list) { lhs, rhs -> rhs.createdDate.compareTo(lhs.createdDate) }

                onDataChangedListener.onListChanged(list)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "getCommentsList(), onCancelled", Exception(databaseError.message))
            }
        })

        databaseHelper.addActiveListener(valueEventListener, databaseReference)
        return valueEventListener
    }

    fun removeCommentsByPost(postId: String): Task<Void> {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.POST_COMMENTS_DB_KEY)
                .child(postId)
                .removeValue()
    }

    companion object {

        private val TAG = CommentInteractor::class.java.simpleName
        private var instance: CommentInteractor? = null

        fun getInstance(context: Context): CommentInteractor {
            if (instance == null) {
                instance = CommentInteractor(context)
            }

            return instance
        }
    }

}
