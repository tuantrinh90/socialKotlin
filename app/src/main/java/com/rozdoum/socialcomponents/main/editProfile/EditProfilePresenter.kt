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

package com.rozdoum.socialcomponents.main.editProfile

import android.content.Context
import android.net.Uri
import android.text.TextUtils

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.main.pickImageBase.PickImagePresenter
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.ValidationUtil

/**
 * Created by Alexey on 03.05.18.
 */

open class EditProfilePresenter<V : EditProfileView>(context: Context) : PickImagePresenter<V>(context) {

    protected var profile: Profile? = null
    protected var profileManager: ProfileManager

    init {
        profileManager = ProfileManager.getInstance(context.applicationContext)

    }

    fun loadProfile() {
        ifViewAttached { it.showProgress() }
        profileManager.getProfileSingleValue(currentUserId, object : OnObjectChangedListenerSimple<Profile>() {
            override fun onObjectChanged(obj: Profile) {
                profile = obj
                ifViewAttached { view ->
                    if (profile != null) {
                        view.setName(profile!!.username)

                        if (profile!!.photoUrl != null) {
                            view.setProfilePhoto(profile!!.photoUrl)
                        }
                    }

                    view.hideProgress()
                    view.setNameError(null)
                }
            }
        })
    }

    fun attemptCreateProfile(imageUri: Uri) {
        if (checkInternetConnection()) {
            ifViewAttached { view ->
                view.setNameError(null)

                val name = view.nameText.trim { it <= ' ' }
                var cancel = false

                if (TextUtils.isEmpty(name)) {
                    view.setNameError(context.getString(R.string.error_field_required))
                    cancel = true
                } else if (!ValidationUtil.isNameValid(name)) {
                    view.setNameError(context.getString(R.string.error_profile_name_length))
                    cancel = true
                }

                if (!cancel) {
                    view.showProgress()
                    profile!!.username = name
                    createOrUpdateProfile(imageUri)
                }
            }
        }
    }

    private fun createOrUpdateProfile(imageUri: Uri) {
        profileManager.createOrUpdateProfile(profile!!, imageUri, { success ->
            ifViewAttached { view ->
                view.hideProgress()
                if (success) {
                    onProfileUpdatedSuccessfully()
                } else {
                    view.showSnackBar(R.string.error_fail_create_profile)
                }
            }
        })
    }

    protected open fun onProfileUpdatedSuccessfully() {
        ifViewAttached { it.finish() }
    }

}
