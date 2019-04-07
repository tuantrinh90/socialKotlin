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

package com.rozdoum.socialcomponents.main.imageDetail

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.views.TouchImageView

class ImageDetailActivity : BaseActivity<ImageDetailView, ImageDetailPresenter>(), ImageDetailView {
    private var viewGroup: ViewGroup? = null
    private var touchImageView: TouchImageView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        touchImageView = findViewById(R.id.touchImageView)
        progressBar = findViewById(R.id.progressBar)
        viewGroup = findViewById(R.id.image_detail_container)

        initActionBar()

        val imageUrl = intent.getStringExtra(IMAGE_URL_EXTRA_KEY)
        loadImage(imageUrl)

        touchImageView!!.setOnClickListener { v ->
            val vis = viewGroup!!.systemUiVisibility
            if (vis and View.SYSTEM_UI_FLAG_LOW_PROFILE != 0) {
                viewGroup!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else {
                viewGroup!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
            }
        }
    }

    override fun createPresenter(): ImageDetailPresenter {
        return if (presenter == null) {
            ImageDetailPresenter(this)
        } else presenter
    }

    private fun initActionBar() {
        actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)

            viewGroup!!.setOnSystemUiVisibilityChangeListener { vis ->
                if (vis and View.SYSTEM_UI_FLAG_LOW_PROFILE != 0) {
                    actionBar.hide()
                } else {
                    actionBar.show()
                }
            }

            // Start low profile mode and hide ActionBar
            viewGroup!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
            actionBar.hide()
        }
    }

    private fun loadImage(imageUrl: String) {
        val maxImageSide = presenter.calcMaxImageSide()

        ImageUtil.loadImageWithSimpleTarget(GlideApp.with(this), imageUrl, object : SimpleTarget<Bitmap>(maxImageSide, maxImageSide) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                progressBar!!.visibility = View.GONE
                touchImageView!!.setImageBitmap(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                progressBar!!.visibility = View.GONE
                touchImageView!!.setImageResource(R.drawable.ic_stub)
            }
        })
    }

    companion object {

        private val TAG = ImageDetailActivity::class.java.simpleName

        val IMAGE_URL_EXTRA_KEY = "ImageDetailActivity.IMAGE_URL_EXTRA_KEY"
    }
}
