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

package com.rozdoum.socialcomponents.main.editProfile.createProfile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.editProfile.EditProfileActivity

class CreateProfileActivity : EditProfileActivity<CreateProfileView, CreateProfilePresenter>(), CreateProfileView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initContent() {
        val largeAvatarURL = intent.getStringExtra(LARGE_IMAGE_URL_EXTRA_KEY)
        presenter.buildProfile(largeAvatarURL)
    }

    override fun createPresenter(): CreateProfilePresenter {
        return if (presenter == null) {
            CreateProfilePresenter(this)
        } else presenter
    }

    override fun setDefaultProfilePhoto() {
        imageView.setImageResource(R.drawable.ic_stub)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.create_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.continueButton -> {
                presenter.attemptCreateProfile(imageUri)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val LARGE_IMAGE_URL_EXTRA_KEY = "CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY"
    }
}
