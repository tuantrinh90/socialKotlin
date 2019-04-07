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

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Query
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.UploadTask
import com.rozdoum.socialcomponents.ApplicationHelper
import com.rozdoum.socialcomponents.enums.UploadImagePrefix
import com.rozdoum.socialcomponents.managers.DatabaseHelper
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnProfileCreatedListener
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.ArrayList

/**
 * Created by Alexey on 05.06.18.
 */

class ProfileInteractor private constructor(private val context: Context) {

    private val databaseHelper: DatabaseHelper?

    init {
        databaseHelper = ApplicationHelper.databaseHelper
    }

    fun createOrUpdateProfile(profile: Profile, onProfileCreatedListener: OnProfileCreatedListener) {
        val task = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.PROFILES_DB_KEY)
                .child(profile.id!!)
                .setValue(profile)
        task.addOnCompleteListener { task1 ->
            onProfileCreatedListener.onProfileCreated(task1.isSuccessful)
            addRegistrationToken(FirebaseInstanceId.getInstance().token, profile.id)
            LogUtil.logDebug(TAG, "createOrUpdateProfile, success: " + task1.isSuccessful)
        }
    }

    fun createOrUpdateProfileWithImage(profile: Profile, imageUri: Uri, onProfileCreatedListener: OnProfileCreatedListener) {
        val imageTitle = ImageUtil.generateImageTitle(UploadImagePrefix.PROFILE, profile.id)
        val uploadTask = databaseHelper!!.uploadImage(imageUri, imageTitle)

        if (uploadTask != null) {
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result.downloadUrl
                    LogUtil.logDebug(TAG, "successful upload image, image url: " + downloadUrl.toString())

                    profile.photoUrl = downloadUrl!!.toString()
                    createOrUpdateProfile(profile, onProfileCreatedListener)

                } else {
                    onProfileCreatedListener.onProfileCreated(false)
                    LogUtil.logDebug(TAG, "fail to upload image")
                }

            }
        } else {
            onProfileCreatedListener.onProfileCreated(false)
            LogUtil.logDebug(TAG, "fail to upload image")
        }
    }

    fun isProfileExist(id: String, onObjectExistListener: OnObjectExistListener<Profile>) {
        val databaseReference = databaseHelper!!.databaseReference.child("profiles").child(id)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onObjectExistListener.onDataChanged(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun getProfile(id: String, listener: OnObjectChangedListener<Profile>): ValueEventListener {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.PROFILES_DB_KEY).child(id)
        val valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profile = dataSnapshot.getValue(Profile::class.java)
                listener.onObjectChanged(profile)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.onError(databaseError.message)
                LogUtil.logError(TAG, "getProfile(), onCancelled", Exception(databaseError.message))
            }
        })
        databaseHelper.addActiveListener(valueEventListener, databaseReference)
        return valueEventListener
    }

    fun getProfileSingleValue(id: String, listener: OnObjectChangedListener<Profile>) {
        val databaseReference = databaseHelper!!.databaseReference.child(DatabaseHelper.PROFILES_DB_KEY).child(id)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profile = dataSnapshot.getValue(Profile::class.java)
                listener.onObjectChanged(profile)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.onError(databaseError.message)
                LogUtil.logError(TAG, "getProfileSingleValue(), onCancelled", Exception(databaseError.message))
            }
        })
    }

    fun updateProfileLikeCountAfterRemovingPost(post: Post) {
        val profileRef = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.PROFILES_DB_KEY + "/" + post.authorId + "/likesCount")
        val likesByPostCount = post.likesCount

        profileRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue != null && currentValue >= likesByPostCount) {
                    mutableData.value = currentValue - likesByPostCount
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError, b: Boolean, dataSnapshot: DataSnapshot) {
                LogUtil.logInfo(TAG, "Updating likes count transaction is completed.")
            }
        })

    }

    fun addRegistrationToken(token: String?, userId: String?) {
        val task = databaseHelper!!
                .databaseReference
                .child(DatabaseHelper.PROFILES_DB_KEY)
                .child(userId!!).child("notificationTokens")
                .child(token!!).setValue(true)
        task.addOnCompleteListener { task1 -> LogUtil.logDebug(TAG, "addRegistrationToken, success: " + task1.isSuccessful) }
    }

    fun updateRegistrationToken(token: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val currentUserId = firebaseUser.uid

            getProfileSingleValue(currentUserId, object : OnObjectChangedListenerSimple<Profile>() {
                override fun onObjectChanged(obj: Profile?) {
                    if (obj != null) {
                        addRegistrationToken(token, currentUserId)
                    } else {
                        LogUtil.logError(TAG, "updateRegistrationToken",
                                RuntimeException("Profile is not found"))
                    }
                }
            })
        }
    }

    fun removeRegistrationToken(token: String, userId: String) {
        val databaseReference = ApplicationHelper.databaseHelper!!.databaseReference
        val tokenRef = databaseReference.child(DatabaseHelper.PROFILES_DB_KEY).child(userId).child("notificationTokens").child(token)
        val task = tokenRef.removeValue()
        task.addOnCompleteListener { task1 -> LogUtil.logDebug(TAG, "removeRegistrationToken, success: " + task1.isSuccessful) }
    }

    fun searchProfiles(searchText: String, onDataChangedListener: OnDataChangedListener<Profile>): ValueEventListener {
        val reference = databaseHelper!!.databaseReference.child(DatabaseHelper.PROFILES_DB_KEY)
        val valueEventListener = getSearchQuery(reference, "username", searchText).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = ArrayList<Profile>()
                for (snapshot in dataSnapshot.children) {
                    val profile = snapshot.getValue(Profile::class.java)
                    list.add(profile)
                }
                onDataChangedListener.onListChanged(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LogUtil.logError(TAG, "searchProfiles(), onCancelled", Exception(databaseError.message))
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

    companion object {

        private val TAG = ProfileInteractor::class.java.simpleName
        private var instance: ProfileInteractor? = null

        fun getInstance(context: Context): ProfileInteractor {
            if (instance == null) {
                instance = ProfileInteractor(context)
            }

            return instance
        }
    }
}
