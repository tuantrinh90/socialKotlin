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

package com.rozdoum.socialcomponents.main.editProfile.createProfile

import android.content.Context

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.rozdoum.socialcomponents.main.editProfile.EditProfilePresenter
import com.rozdoum.socialcomponents.utils.PreferencesUtil

/**
 * Created by Alexey on 03.05.18.
 */

internal class CreateProfilePresenter(context: Context) : EditProfilePresenter<CreateProfileView>(context) {

    fun buildProfile(largeAvatarURL: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        profile = profileManager.buildProfile(firebaseUser!!, largeAvatarURL)

        ifViewAttached { view ->
            view.setName(profile!!.username)

            if (profile!!.photoUrl != null) {
                view.setProfilePhoto(profile!!.photoUrl)
            } else {
                view.hideLocalProgress()
                view.setDefaultProfilePhoto()
            }
        }
    }

    override fun onProfileUpdatedSuccessfully() {
        super.onProfileUpdatedSuccessfully()
        PreferencesUtil.setProfileCreated(context, true)
        profileManager.addRegistrationToken(FirebaseInstanceId.getInstance().token!!, profile!!.id!!)
    }
}
