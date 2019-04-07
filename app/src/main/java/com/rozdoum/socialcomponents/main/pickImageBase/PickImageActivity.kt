/*
 * Copyright 2017 Rozdoum
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

package com.rozdoum.socialcomponents.main.pickImageBase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.LogUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

abstract class PickImageActivity<V : PickImageView, P : PickImagePresenter<V>> : BaseActivity<V, P>(), PickImageView {

    protected var imageUri: Uri? = null

    protected abstract val progressView: ProgressBar

    protected abstract val imageView: ImageView

    protected abstract fun onImagePikedAction()

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SAVED_STATE_IMAGE_URI, imageUri)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_STATE_IMAGE_URI)) {
                imageUri = savedInstanceState.getParcelable(SAVED_STATE_IMAGE_URI)
                loadImageToImageView(imageUri)
            }
        }

        super.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("NewApi")
    fun onSelectImageClick(view: View) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE)
        } else {
            CropImage.startPickImageActivity(this)
        }
    }

    override fun loadImageToImageView(imageUri: Uri?) {
        if (imageUri == null) {
            return
        }

        this.imageUri = imageUri
        ImageUtil.loadLocalImage(GlideApp.with(this), imageUri, imageView, object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                progressView.visibility = View.GONE
                LogUtil.logDebug(TAG, "Glide Success Loading image from uri : " + imageUri.path!!)
                return false
            }
        })
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)

            if (presenter.isImageFileValid(imageUri)) {
                this.imageUri = imageUri
            }

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
            } else {
                // no permissions required or already grunted
                onImagePikedAction()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.logDebug(TAG, "CAMERA_CAPTURE_PERMISSIONS granted")
                CropImage.startPickImageActivity(this)
            } else {
                showSnackBar(R.string.permissions_not_granted)
                LogUtil.logDebug(TAG, "CAMERA_CAPTURE_PERMISSIONS not granted")
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (imageUri != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.logDebug(TAG, "PICK_IMAGE_PERMISSIONS granted")
                onImagePikedAction()
            } else {
                showSnackBar(R.string.permissions_not_granted)
                LogUtil.logDebug(TAG, "PICK_IMAGE_PERMISSIONS not granted")
            }
        }
    }

    protected fun handleCropImageResult(requestCode: Int, resultCode: Int, data: Intent) {
        presenter.handleCropImageResult(requestCode, resultCode, data)
    }

    protected fun startCropImageActivity() {
        if (imageUri == null) {
            return
        }

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setMinCropResultSize(Constants.Profile.MIN_AVATAR_SIZE, Constants.Profile.MIN_AVATAR_SIZE)
                .setRequestedSize(Constants.Profile.MAX_AVATAR_SIZE, Constants.Profile.MAX_AVATAR_SIZE)
                .start(this)
    }

    override fun hideLocalProgress() {
        progressView.visibility = View.GONE
    }

    companion object {
        private val SAVED_STATE_IMAGE_URI = "RegistrationActivity.SAVED_STATE_IMAGE_URI"
    }
}

