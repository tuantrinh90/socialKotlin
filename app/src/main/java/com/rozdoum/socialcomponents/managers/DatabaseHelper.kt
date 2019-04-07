/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rozdoum.socialcomponents.managers

import android.content.Context
import android.net.Uri

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.HashMap

/**
 * Created by Kristina on 10/28/16.
 */

class DatabaseHelper private constructor(private val context: Context) {
    private var database: FirebaseDatabase? = null
    internal var storage: FirebaseStorage
    private val activeListeners = HashMap<ValueEventListener, DatabaseReference>()

    val storageReference: StorageReference
        get() = storage.getReferenceFromUrl(context.resources.getString(R.string.storage_link))

    val databaseReference: DatabaseReference
        get() = database!!.reference

    fun init() {
        database = FirebaseDatabase.getInstance()
        database!!.setPersistenceEnabled(true)
        storage = FirebaseStorage.getInstance()

        //        Sets the maximum time to retry upload operations if a failure occurs.
        storage.maxUploadRetryTimeMillis = Constants.Database.MAX_UPLOAD_RETRY_MILLIS.toLong()
    }

    fun closeListener(listener: ValueEventListener) {
        if (activeListeners.containsKey(listener)) {
            val reference = activeListeners[listener]
            reference.removeEventListener(listener)
            activeListeners.remove(listener)
            LogUtil.logDebug(TAG, "closeListener(), listener was removed: $listener")
        } else {
            LogUtil.logDebug(TAG, "closeListener(), listener not found :$listener")
        }
    }

    fun closeAllActiveListeners() {
        for (listener in activeListeners.keys) {
            val reference = activeListeners[listener]
            reference.removeEventListener(listener)
        }

        activeListeners.clear()
    }

    fun addActiveListener(listener: ValueEventListener, reference: DatabaseReference) {
        activeListeners[listener] = reference
    }

    fun removeImage(imageTitle: String): Task<Void> {
        val desertRef = storageReference.child("$IMAGES_STORAGE_KEY/$imageTitle")
        return desertRef.delete()
    }

    fun uploadImage(uri: Uri, imageTitle: String): UploadTask {
        val riversRef = storageReference.child("$IMAGES_STORAGE_KEY/$imageTitle")
        // Create file metadata including the content type
        val metadata = StorageMetadata.Builder()
                .setCacheControl("max-age=7776000, Expires=7776000, public, must-revalidate")
                .build()

        return riversRef.putFile(uri, metadata)
    }

    companion object {

        val TAG = DatabaseHelper::class.java.simpleName

        private var instance: DatabaseHelper? = null

        val POSTS_DB_KEY = "posts"
        val PROFILES_DB_KEY = "profiles"
        val POST_COMMENTS_DB_KEY = "post-comments"
        val POST_LIKES_DB_KEY = "post-likes"
        val FOLLOW_DB_KEY = "follow"
        val FOLLOWINGS_DB_KEY = "followings"
        val FOLLOWINGS_POSTS_DB_KEY = "followingPostsIds"
        val FOLLOWERS_DB_KEY = "followers"
        val IMAGES_STORAGE_KEY = "images"

        fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context)
            }

            return instance
        }
    }

}
