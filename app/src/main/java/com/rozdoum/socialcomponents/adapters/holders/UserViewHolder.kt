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

package com.rozdoum.socialcomponents.adapters.holders

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.managers.FollowManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.views.FollowButton

/**
 * Created by Alexey on 03.05.18.
 */

class UserViewHolder(view: View, callback: Callback?, private val activity: Activity) : RecyclerView.ViewHolder(view) {

    private val context: Context
    private val photoImageView: ImageView
    private val nameTextView: TextView
    private val followButton: FollowButton

    private val profileManager: ProfileManager

    init {
        this.context = view.context
        profileManager = ProfileManager.getInstance(context)

        nameTextView = view.findViewById(R.id.nameTextView)
        photoImageView = view.findViewById(R.id.photoImageView)
        followButton = view.findViewById(R.id.followButton)

        view.setOnClickListener { v ->
            val position = adapterPosition
            if (callback != null && position != RecyclerView.NO_POSITION) {
                callback.onItemClick(adapterPosition, v)
            }
        }

        followButton.setOnClickListener { v ->
            callback?.onFollowButtonClick(adapterPosition, followButton)
        }
    }

    fun bindData(profileId: String) {
        profileManager.getProfileSingleValue(profileId, createProfileChangeListener())
    }

    fun bindData(profile: Profile) {
        fillInProfileFields(profile)
    }

    private fun createProfileChangeListener(): OnObjectChangedListener<Profile> {
        return object : OnObjectChangedListenerSimple<Profile>() {
            override fun onObjectChanged(obj: Profile) {
                fillInProfileFields(obj)
            }
        }
    }

    protected fun fillInProfileFields(profile: Profile) {
        nameTextView.text = profile.username
        val currentUserId = FirebaseAuth.getInstance().uid
        //        if (currentUserId != null) {
        //            if (!currentUserId.equals(profile.getId())) {
        //                FollowManager.getInstance(context).checkFollowState(currentUserId, profile.getId(), followState -> {
        //                    followButton.setVisibility(View.VISIBLE);
        //                    followButton.setState(followState);
        //                });
        //            } else {
        //                followButton.setState(FollowState.MY_PROFILE);
        //            }
        //        } else {
        //            followButton.setState(FollowState.NO_ONE_FOLLOW);
        //        }

        if (profile.photoUrl != null) {
            ImageUtil.loadImage(GlideApp.with(activity), profile.photoUrl, photoImageView)
        }
    }

    interface Callback {
        fun onItemClick(position: Int, view: View)

        fun onFollowButtonClick(position: Int, followButton: FollowButton)
    }

    companion object {
        val TAG = UserViewHolder::class.java.simpleName
    }

}