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

package com.rozdoum.socialcomponents.main.post.editPost

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.main.MainActivity
import com.rozdoum.socialcomponents.main.post.BaseCreatePostActivity
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil

class EditPostActivity : BaseCreatePostActivity<EditPostView, EditPostPresenter>(), EditPostView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val post = intent.getSerializableExtra(POST_EXTRA_KEY) as Post
        presenter.setPost(post)
        showProgress()
        fillUIFields(post)
    }

    override fun onStart() {
        super.onStart()
        presenter.addCheckIsPostChangedListener()
    }

    override fun onStop() {
        super.onStop()
        presenter.closeListeners()
    }

    override fun createPresenter(): EditPostPresenter {
        return if (presenter == null) {
            EditPostPresenter(this)
        } else presenter
    }

    override fun openMainActivity() {
        val intent = Intent(this@EditPostActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun fillUIFields(post: Post) {
        titleEditText.setText(post.title)
        descriptionEditText.setText(post.description)
        loadPostDetailsImage(post.imagePath)
        hideProgress()
    }

    private fun loadPostDetailsImage(imagePath: String?) {
        ImageUtil.loadImageCenterCrop(GlideApp.with(this), imagePath!!, imageView, object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.save -> {
                presenter.doSavePost(imageUri)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = EditPostActivity::class.java.simpleName
        val POST_EXTRA_KEY = "EditPostActivity.POST_EXTRA_KEY"
        val EDIT_POST_REQUEST = 33
    }
}
