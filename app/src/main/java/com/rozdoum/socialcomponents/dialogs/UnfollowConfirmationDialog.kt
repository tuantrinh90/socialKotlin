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

package com.rozdoum.socialcomponents.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil

/**
 * Created by Alexey on 11.05.18.
 */

class UnfollowConfirmationDialog : DialogFragment() {

    private var callback: Callback? = null
    private var profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (activity is Callback) {
            callback = activity as Callback
        } else {
            throw RuntimeException(activity.title.toString() + " should implements Callback")
        }

        profile = arguments.get(PROFILE) as Profile

        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_confirmation_unfollow, null)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val confirmationMessageTextView = view.findViewById<TextView>(R.id.confirmationMessageTextView)

        confirmationMessageTextView.text = getString(R.string.unfollow_user_message, profile!!.username)

        ImageUtil.loadImage(GlideApp.with(this), profile!!.photoUrl, imageView)

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.button_title_unfollow) { dialog, which ->
                    callback!!.onUnfollowButtonClicked()
                    dialog.cancel()
                }

        return builder.create()
    }

    interface Callback {
        fun onUnfollowButtonClicked()
    }

    companion object {
        val TAG = UnfollowConfirmationDialog::class.java.simpleName
        val PROFILE = "EditCommentDialog.PROFILE"
    }
}
