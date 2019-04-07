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

import android.app.Activity
import android.content.Context

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.ApplicationHelper
import com.rozdoum.socialcomponents.managers.DatabaseHelper
import com.rozdoum.socialcomponents.managers.FirebaseListenersManager
import com.rozdoum.socialcomponents.managers.listeners.OnCountChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnRequestComplete
import com.rozdoum.socialcomponents.model.Follower
import com.rozdoum.socialcomponents.model.Following
import com.rozdoum.socialcomponents.model.FollowingPost
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.ArrayList
import java.util.Collections

/**
 * Created by Alexey on 05.06.18.
 */

class FollowInteractor private constructor(private val context: Context) {

    private val databaseHelper: DatabaseHelper?

    init {
        databaseHelper = ApplicationHelper.databaseHelper
    }

    fun getFollowingPosts(userId: String, listener: OnDataChangedListener<FollowingPost>) {
        databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOWINGS_POSTS_DB_KEY)
                .child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = ArrayList<FollowingPost>()
                        for (snapshot in dataSnapshot.children) {
                            val followingPost = snapshot.getValue(FollowingPost::class.java)
                            list.add(followingPost)
                        }

                        Collections.reverse(list)

                        listener.onListChanged(list)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        LogUtil.logDebug(TAG, "getFollowingPosts, onCancelled")
                    }
                })
    }

    private fun getFollowersRef(userId: String): DatabaseReference {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(userId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
    }

    private fun getFollowingsRef(userId: String): DatabaseReference {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(userId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
    }

    fun getFollowersList(targetUserId: String, onDataChangedListener: OnDataChangedListener<String>) {
        getFollowersRef(targetUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = ArrayList<String>()
                for (snapshot in dataSnapshot.children) {
                    val follower = snapshot.getValue(Follower::class.java)

                    if (follower != null) {
                        val profileId = follower.profileId
                        list.add(profileId)
                    }

                }
                onDataChangedListener.onListChanged(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logDebug(TAG, "getFollowersList, onCancelled")
            }
        })
    }

    fun getFollowingsList(targetUserId: String, onDataChangedListener: OnDataChangedListener<String>) {
        getFollowingsRef(targetUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = ArrayList<String>()
                for (snapshot in dataSnapshot.children) {
                    val following = snapshot.getValue(Following::class.java)

                    if (following != null) {
                        val profileId = following.profileId
                        list.add(profileId)
                    }

                }
                onDataChangedListener.onListChanged(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logDebug(TAG, "getFollowingsList, onCancelled")
            }
        })
    }

    fun getFollowingsCount(targetUserId: String, onCountChangedListener: OnCountChangedListener<*>): ValueEventListener {
        return getFollowingsRef(targetUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onCountChangedListener.onCountChanged(dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logDebug(TAG, "getFollowingsCount, onCancelled")
            }
        })
    }

    fun getFollowersCount(targetUserId: String, onCountChangedListener: OnCountChangedListener<*>): ValueEventListener {
        return getFollowersRef(targetUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onCountChangedListener.onCountChanged(dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logDebug(TAG, "getFollowersCount, onCancelled")
            }
        })
    }

    fun isFollowingExist(followerUserId: String, followingUserId: String, onObjectExistListener: OnObjectExistListener<*>) {
        val followingRef = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId)

        val followerRef = databaseHelper
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId)


        followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    followerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            onObjectExistListener.onDataChanged(dataSnapshot.exists())
                            LogUtil.logDebug(TAG, "isFollowerExist for" + followingUserId + ", success: " + dataSnapshot.exists())
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                } else {
                    onObjectExistListener.onDataChanged(false)
                    LogUtil.logDebug(TAG, "isFollowingExist for" + followerUserId + ", success: " + dataSnapshot.exists())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logDebug(TAG, "isFollowingExist, onCancelled")
            }
        })
    }

    private fun addFollowing(followerUserId: String, followingUserId: String): Task<Void> {
        val following = Following(followingUserId)
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId)
                .setValue(following)
    }

    private fun addFollower(followerUserId: String, followingUserId: String): Task<Void> {
        val follower = Follower(followerUserId)
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId)
                .setValue(follower)
    }

    private fun removeFollowing(followerUserId: String, followingUserId: String): Task<Void> {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followerUserId)
                .child(DatabaseHelper.FOLLOWINGS_DB_KEY)
                .child(followingUserId)
                .removeValue()
    }

    private fun removeFollower(followerUserId: String, followingUserId: String): Task<Void> {
        return databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.FOLLOW_DB_KEY)
                .child(followingUserId)
                .child(DatabaseHelper.FOLLOWERS_DB_KEY)
                .child(followerUserId).removeValue()
    }

    fun unfollowUser(activity: Activity, followerUserId: String, followingUserId: String, onRequestComplete: OnRequestComplete) {
        removeFollowing(followerUserId, followingUserId)
                .continueWithTask { task -> removeFollower(followerUserId, followingUserId) }
                .addOnCompleteListener(activity) { task ->
                    onRequestComplete.onComplete(task.isSuccessful)
                    LogUtil.logDebug(TAG, "unfollowUser " + followingUserId + ", success: " + task.isSuccessful)
                }

    }

    fun followUser(activity: Activity, followerUserId: String, followingUserId: String, onRequestComplete: OnRequestComplete) {
        addFollowing(followerUserId, followingUserId)
                .continueWithTask { task -> addFollower(followerUserId, followingUserId) }
                .addOnCompleteListener(activity) { task ->
                    onRequestComplete.onComplete(task.isSuccessful)
                    LogUtil.logDebug(TAG, "followUser " + followingUserId + ", success: " + task.isSuccessful)
                }
    }

    companion object {

        private val TAG = FollowInteractor::class.java.simpleName
        private var instance: FollowInteractor? = null

        fun getInstance(context: Context): FollowInteractor {
            if (instance == null) {
                instance = FollowInteractor(context)
            }

            return instance
        }
    }
}
