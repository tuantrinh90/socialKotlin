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

package com.rozdoum.socialcomponents.main.post.createPost

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.post.BaseCreatePostActivity

class CreatePostActivity : BaseCreatePostActivity<CreatePostView, CreatePostPresenter>(), CreatePostView {

    override fun createPresenter(): CreatePostPresenter {
        return if (presenter == null) {
            CreatePostPresenter(this)
        } else presenter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.create_post_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.post -> {
                presenter.doSavePost(imageUri)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val CREATE_NEW_POST_REQUEST = 11
    }
}
