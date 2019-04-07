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

package com.rozdoum.socialcomponents.main.post

import android.content.Context
import android.net.Uri
import android.support.annotation.StringRes
import android.text.TextUtils

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.pickImageBase.PickImagePresenter
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnPostCreatedListener
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.utils.ValidationUtil

/**
 * Created by Alexey on 03.05.18.
 */

abstract class BaseCreatePostPresenter<V : BaseCreatePostView>(context: Context) : PickImagePresenter<V>(context), OnPostCreatedListener {

    protected var creatingPost = false
    protected var postManager: PostManager

    @get:StringRes
    protected abstract val saveFailMessage: Int

    protected abstract val isImageRequired: Boolean

    init {
        postManager = PostManager.getInstance(context)
    }

    protected abstract fun savePost(title: String, description: String)

    protected fun attemptCreatePost(imageUri: Uri) {
        // Reset errors.
        ifViewAttached { view ->
            view.setTitleError(null)
            view.setDescriptionError(null)

            val title = view.titleText.trim { it <= ' ' }
            val description = view.descriptionText.trim { it <= ' ' }

            var cancel = false

            if (TextUtils.isEmpty(description)) {
                view.setDescriptionError(context.getString(R.string.warning_empty_description))
                cancel = true
            }

            if (TextUtils.isEmpty(title)) {
                view.setTitleError(context.getString(R.string.warning_empty_title))
                cancel = true
            } else if (!ValidationUtil.isPostTitleValid(title)) {
                view.setTitleError(context.getString(R.string.error_post_title_length))
                cancel = true
            }

            if (isImageRequired && view.imageUri == null) {
                view.showWarningDialog(R.string.warning_empty_image)
                view.requestImageViewFocus()
                cancel = true
            }

            if (!cancel) {
                creatingPost = true
                view.hideKeyboard()
                savePost(title, description)
            }
        }
    }

    fun doSavePost(imageUri: Uri) {
        if (!creatingPost) {
            if (hasInternetConnection()) {
                attemptCreatePost(imageUri)
            } else {
                ifViewAttached { view -> view.showSnackBar(R.string.internet_connection_failed) }
            }
        }
    }

    override fun onPostSaved(success: Boolean) {
        creatingPost = false

        ifViewAttached { view ->
            view.hideProgress()
            if (success) {
                view.onPostSavedSuccess()
                LogUtil.logDebug(TAG, "Post was saved")
            } else {
                view.showSnackBar(saveFailMessage)
                LogUtil.logDebug(TAG, "Failed to save a post")
            }
        }
    }

}
