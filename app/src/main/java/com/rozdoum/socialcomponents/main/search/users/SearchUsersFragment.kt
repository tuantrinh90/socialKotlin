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

package com.rozdoum.socialcomponents.main.search.users

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.SearchUsersAdapter
import com.rozdoum.socialcomponents.adapters.holders.UserViewHolder
import com.rozdoum.socialcomponents.main.base.BaseFragment
import com.rozdoum.socialcomponents.main.login.LoginActivity
import com.rozdoum.socialcomponents.main.profile.ProfileActivity
import com.rozdoum.socialcomponents.main.search.Searchable
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.AnimationUtils
import com.rozdoum.socialcomponents.views.FollowButton

import android.app.Activity.RESULT_OK
import com.rozdoum.socialcomponents.main.usersList.UsersListActivity.Companion.UPDATE_FOLLOWING_STATE_REQ
import com.rozdoum.socialcomponents.main.usersList.UsersListActivity.Companion.UPDATE_FOLLOWING_STATE_RESULT_OK

class SearchUsersFragment : BaseFragment<SearchUsersView, SearchUsersPresenter>(), SearchUsersView, Searchable {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var usersAdapter: SearchUsersAdapter? = null
    private var emptyListMessageTextView: TextView? = null
    private var lastSearchText = ""

    private var searchInProgress = false

    private var selectedItemPosition = RecyclerView.NO_POSITION

    override fun createPresenter(): SearchUsersPresenter {
        return if (presenter == null) {
            SearchUsersPresenter(activity)
        } else presenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_results, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recycler_view)
        emptyListMessageTextView = view.findViewById(R.id.emptyListMessageTextView)
        emptyListMessageTextView!!.text = resources.getString(R.string.empty_user_search_message)

        initRecyclerView()
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Companion.getUPDATE_FOLLOWING_STATE_REQ() && resultCode == Companion.getUPDATE_FOLLOWING_STATE_RESULT_OK()) {
            updateSelectedItem()
        }

        if (requestCode == LoginActivity.LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            presenter.search(lastSearchText)
        }
    }

    override fun updateSelectedItem() {
        if (selectedItemPosition != RecyclerView.NO_POSITION) {
            usersAdapter!!.updateItem(selectedItemPosition)
        }
    }

    private fun initRecyclerView() {
        usersAdapter = SearchUsersAdapter(activity!!)
        usersAdapter!!.setCallback(object : UserViewHolder.Callback {
            override fun onItemClick(position: Int, view: View) {
                if (!searchInProgress) {
                    selectedItemPosition = position
                    val profile = usersAdapter!!.getItemByPosition(position)
                    openProfileActivity(profile.id, view)
                }
            }

            override fun onFollowButtonClick(position: Int, followButton: FollowButton) {
                if (!searchInProgress) {
                    selectedItemPosition = position
                    val profile = usersAdapter!!.getItemByPosition(position)
                    presenter.onFollowButtonClick(followButton.state, profile.id)
                }
            }
        })

        (recyclerView!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView!!.adapter = usersAdapter

        presenter.loadUsersWithEmptySearch()
    }

    @SuppressLint("RestrictedApi")
    private fun openProfileActivity(userId: String?, view: View?) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            val imageView = view.findViewById<ImageView>(R.id.photoImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    android.util.Pair(imageView, getString(R.string.post_author_image_transition_name)))
            startActivityForResult(intent, Companion.getUPDATE_FOLLOWING_STATE_REQ(), options.toBundle())
        } else {
            startActivityForResult(intent, Companion.getUPDATE_FOLLOWING_STATE_REQ())
        }
    }

    override fun search(searchText: String) {
        lastSearchText = searchText
        presenter.search(searchText)
    }

    override fun onSearchResultsReady(profiles: List<Profile>) {
        hideLocalProgress()
        emptyListMessageTextView!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        usersAdapter!!.setList(profiles)
    }

    override fun showLocalProgress() {
        searchInProgress = true
        AnimationUtils.showViewByScaleWithoutDelay(progressBar!!)
    }

    override fun hideLocalProgress() {
        searchInProgress = false
        AnimationUtils.hideViewByScale(progressBar!!)
    }

    override fun showEmptyListLayout() {
        hideLocalProgress()
        recyclerView!!.visibility = View.GONE
        emptyListMessageTextView!!.visibility = View.VISIBLE
    }
}// Required empty public constructor
