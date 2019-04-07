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

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.pickImageBase.PickImageActivity
import com.rozdoum.socialcomponents.managers.PostManager

/**
 * Created by Alexey on 03.05.18.
 */
abstract class BaseCreatePostActivity<V : BaseCreatePostView, P : BaseCreatePostPresenter<V>> : PickImageActivity<V, P>(), BaseCreatePostView {

    protected var imageView: ImageView
    protected var progressBar: ProgressBar
    protected var titleEditText: EditText
    protected var descriptionEditText: EditText

    override val titleText: String
        get() = titleEditText.text.toString()

    override val descriptionText: String
        get() = descriptionEditText.text.toString()

    override val imageUri: Uri
        get() = imageUri

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_create_post_activity)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        progressBar = findViewById(R.id.progressBar)

        imageView = findViewById(R.id.imageView)

        imageView.setOnClickListener { v -> onSelectImageClick(v) }

        titleEditText.setOnTouchListener { v, event ->
            if (titleEditText.hasFocus() && titleEditText.error != null) {
                titleEditText.error = null
                return@titleEditText.setOnTouchListener true
            }

            false
        }
    }

    override fun getProgressView(): ProgressBar {
        return progressBar
    }

    override fun getImageView(): ImageView {
        return imageView
    }

    override fun onImagePikedAction() {
        loadImageToImageView(imageUri)
    }

    override fun setDescriptionError(error: String) {
        descriptionEditText.error = error
        descriptionEditText.requestFocus()
    }

    override fun setTitleError(error: String) {
        titleEditText.error = error
        titleEditText.requestFocus()
    }

    override fun requestImageViewFocus() {
        imageView.requestFocus()
    }

    override fun onPostSavedSuccess() {
        setResult(Activity.RESULT_OK)
        this.finish()
    }
}
