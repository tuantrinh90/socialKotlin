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

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.pickImageBase.PickImageActivity
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil

open class EditProfileActivity<V : EditProfileView, P : EditProfilePresenter<V>> : PickImageActivity<V, P>(), EditProfileView {

    // UI references.
    private var nameEditText: EditText? = null
    public override var imageView: ImageView
        protected set
    public override var progressView: ProgressBar? = null
        private set

    override val nameText: String
        get() = nameEditText!!.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        progressView = findViewById(R.id.avatarProgressBar)
        imageView = findViewById(R.id.imageView)
        nameEditText = findViewById(R.id.nameEditText)

        imageView.setOnClickListener(OnClickListener { this.onSelectImageClick(it) })

        initContent()
    }

    override fun createPresenter(): P {
        return if (presenter == null) {
            EditProfilePresenter(this) as P
        } else presenter
    }

    protected open fun initContent() {
        presenter.loadProfile()
    }

    public override fun onImagePikedAction() {
        startCropImageActivity()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data)
        handleCropImageResult(requestCode, resultCode, data)
    }

    override fun setName(username: String) {
        nameEditText!!.setText(username)
    }

    override fun setProfilePhoto(photoUrl: String) {
        ImageUtil.loadImage(GlideApp.with(this), photoUrl, imageView, object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                progressView!!.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                progressView!!.visibility = View.GONE
                return false
            }
        })
    }

    override fun setNameError(string: String?) {
        nameEditText!!.error = string
        nameEditText!!.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.save -> {
                presenter.attemptCreateProfile(imageUri)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = EditProfileActivity<*, *>::class.java.simpleName
    }
}

