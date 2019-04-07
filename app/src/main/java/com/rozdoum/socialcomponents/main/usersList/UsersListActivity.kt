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

package com.rozdoum.socialcomponents.main.usersList

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.UsersAdapter
import com.rozdoum.socialcomponents.adapters.holders.UserViewHolder
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.login.LoginActivity
import com.rozdoum.socialcomponents.main.profile.ProfileActivity
import com.rozdoum.socialcomponents.views.FollowButton

/**
 * Created by Alexey on 03.05.18.
 */

class UsersListActivity : BaseActivity<UsersListView, UsersListPresenter>(), UsersListView {

    private var usersAdapter: UsersAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var userID: String? = null

    private var progressBar: ProgressBar? = null
    private var swipeContainer: SwipeRefreshLayout? = null
    private var userListType: Int = 0
    private var emptyListMessageTextView: TextView? = null

    private var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        userID = intent.getStringExtra(USER_ID_EXTRA_KEY)
        userListType = intent.getIntExtra(USER_LIST_TYPE, -1)

        if (userListType == -1) {
            throw IllegalArgumentException("USER_LIST_TYPE should be defined for " + this.javaClass.simpleName)
        }

        initContentView()

        presenter.chooseActivityTitle(userListType)
        presenter.loadUsersList(userID, userListType, false)
    }

    override fun createPresenter(): UsersListPresenter {
        return if (presenter == null) {
            UsersListPresenter(this)
        } else presenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_FOLLOWING_STATE_REQ && resultCode == UPDATE_FOLLOWING_STATE_RESULT_OK) {
            updateSelectedItem()
        }

        if (requestCode == LoginActivity.LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            presenter.loadUsersList(userID, userListType, true)
        }
    }

    private fun initContentView() {
        if (recyclerView == null) {
            progressBar = findViewById(R.id.progressBar)
            swipeContainer = findViewById(R.id.swipeContainer)
            emptyListMessageTextView = findViewById(R.id.emptyListMessageTextView)

            swipeContainer!!.setOnRefreshListener { presenter.onRefresh(userID, userListType) }

            initProfilesListRecyclerView()
        }
    }

    private fun initProfilesListRecyclerView() {
        recyclerView = findViewById(R.id.usersRecyclerView)
        usersAdapter = UsersAdapter(this)
        usersAdapter!!.setCallback(object : UserViewHolder.Callback {
            override fun onItemClick(position: Int, view: View) {
                selectedItemPosition = position
                val userId = usersAdapter!!.getItemByPosition(position)
                openProfileActivity(userId, view)
            }

            override fun onFollowButtonClick(position: Int, followButton: FollowButton) {
                selectedItemPosition = position
                val userId = usersAdapter!!.getItemByPosition(position)
                presenter.onFollowButtonClick(followButton.state, userId)
            }
        })

        recyclerView!!.addItemDecoration(DividerItemDecoration(recyclerView!!.context,
                (recyclerView!!.layoutManager as LinearLayoutManager).orientation))

        recyclerView!!.adapter = usersAdapter

    }

    @SuppressLint("RestrictedApi")
    private fun openProfileActivity(userId: String, view: View?) {
        val intent = Intent(this@UsersListActivity, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            val imageView = view.findViewById<ImageView>(R.id.photoImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(this@UsersListActivity,
                    android.util.Pair(imageView, getString(R.string.post_author_image_transition_name)))
            startActivityForResult(intent, UPDATE_FOLLOWING_STATE_REQ, options.toBundle())
        } else {
            startActivityForResult(intent, UPDATE_FOLLOWING_STATE_REQ)
        }
    }

    override fun onProfilesIdsListLoaded(list: List<String>) {
        usersAdapter!!.setList(list)
    }

    override fun showLocalProgress() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLocalProgress() {
        progressBar!!.visibility = View.GONE
        swipeContainer!!.isRefreshing = false
    }

    override fun showEmptyListMessage(message: String) {
        emptyListMessageTextView!!.visibility = View.VISIBLE
        emptyListMessageTextView!!.text = message
    }

    override fun hideEmptyListMessage() {
        emptyListMessageTextView!!.visibility = View.GONE
    }

    override fun updateSelectedItem() {
        if (selectedItemPosition != RecyclerView.NO_POSITION) {
            usersAdapter!!.updateItem(selectedItemPosition)
        }
    }

    companion object {
        private val TAG = UsersListActivity::class.java.simpleName

        val USER_ID_EXTRA_KEY = "UsersListActivity.USER_ID_EXTRA_KEY"
        val USER_LIST_TYPE = "UsersListActivity.USER_LIST_TYPE"

        val UPDATE_FOLLOWING_STATE_REQ = 1501
        val UPDATE_FOLLOWING_STATE_RESULT_OK = 1502
    }
}
