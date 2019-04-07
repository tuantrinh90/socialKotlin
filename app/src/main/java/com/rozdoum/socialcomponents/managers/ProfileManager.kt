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

package com.rozdoum.socialcomponents.managers

import android.content.Context
import android.net.Uri

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ValueEventListener
import com.rozdoum.socialcomponents.enums.ProfileStatus
import com.rozdoum.socialcomponents.main.interactors.ProfileInteractor
import com.rozdoum.socialcomponents.managers.listeners.OnDataChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnProfileCreatedListener
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.PreferencesUtil

/**
 * Created by Kristina on 10/28/16.
 */

class ProfileManager private constructor(private val context: Context) : FirebaseListenersManager() {
    private val profileInteractor: ProfileInteractor

    init {
        profileInteractor = ProfileInteractor.getInstance(context)
    }

    fun buildProfile(firebaseUser: FirebaseUser, largeAvatarURL: String?): Profile {
        val profile = Profile(firebaseUser.uid)
        profile.email = firebaseUser.email
        profile.username = firebaseUser.displayName
        profile.photoUrl = largeAvatarURL ?: firebaseUser.photoUrl!!.toString()
        return profile
    }

    fun isProfileExist(id: String, onObjectExistListener: OnObjectExistListener<Profile>) {
        profileInteractor.isProfileExist(id, onObjectExistListener)
    }

    fun createOrUpdateProfile(profile: Profile, onProfileCreatedListener: OnProfileCreatedListener) {
        createOrUpdateProfile(profile, null, onProfileCreatedListener)
    }

    fun createOrUpdateProfile(profile: Profile, imageUri: Uri?, onProfileCreatedListener: OnProfileCreatedListener) {
        if (imageUri == null) {
            profileInteractor.createOrUpdateProfile(profile, onProfileCreatedListener)
        } else {
            profileInteractor.createOrUpdateProfileWithImage(profile, imageUri, onProfileCreatedListener)
        }
    }

    fun getProfileValue(activityContext: Context, id: String, listener: OnObjectChangedListener<Profile>) {
        val valueEventListener = profileInteractor.getProfile(id, listener)
        addListenerToMap(activityContext, valueEventListener)
    }

    fun getProfileSingleValue(id: String, listener: OnObjectChangedListener<Profile>) {
        profileInteractor.getProfileSingleValue(id, listener)
    }

    fun checkProfile(): ProfileStatus {
        val user = FirebaseAuth.getInstance().currentUser

        return if (user == null) {
            ProfileStatus.NOT_AUTHORIZED
        } else if (!PreferencesUtil.isProfileCreated(context)) {
            ProfileStatus.NO_PROFILE
        } else {
            ProfileStatus.PROFILE_CREATED
        }
    }

    fun search(searchText: String, onDataChangedListener: OnDataChangedListener<Profile>) {
        closeListeners(context)
        val valueEventListener = profileInteractor.searchProfiles(searchText, onDataChangedListener)
        addListenerToMap(context, valueEventListener)
    }

    fun addRegistrationToken(token: String, userId: String) {
        profileInteractor.addRegistrationToken(token, userId)
    }

    companion object {

        private val TAG = ProfileManager::class.java.simpleName
        private var instance: ProfileManager? = null


        fun getInstance(context: Context): ProfileManager {
            if (instance == null) {
                instance = ProfileManager(context)
            }

            return instance
        }
    }
}
