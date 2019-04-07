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

package com.rozdoum.socialcomponents.main.search.posts

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.SearchPostsAdapter
import com.rozdoum.socialcomponents.enums.PostStatus
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.base.BaseFragment
import com.rozdoum.socialcomponents.main.postDetails.PostDetailsActivity
import com.rozdoum.socialcomponents.main.profile.ProfileActivity
import com.rozdoum.socialcomponents.main.search.Searchable
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.AnimationUtils

import android.app.Activity.RESULT_OK

class SearchPostsFragment : BaseFragment<SearchPostsView, SearchPostsPresenter>(), SearchPostsView, Searchable {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var postsAdapter: SearchPostsAdapter? = null
    private var emptyListMessageTextView: TextView? = null

    private var searchInProgress = false

    override fun createPresenter(): SearchPostsPresenter {
        return if (presenter == null) {
            SearchPostsPresenter(context)
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
        emptyListMessageTextView!!.text = resources.getString(R.string.empty_posts_search_message)

        initRecyclerView()
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PostDetailsActivity.UPDATE_POST_REQUEST -> if (data != null) {
                    val postStatus = data.getSerializableExtra(PostDetailsActivity.POST_STATUS_EXTRA_KEY) as PostStatus
                    if (postStatus == PostStatus.REMOVED) {
                        postsAdapter!!.removeSelectedPost()

                    } else if (postStatus == PostStatus.UPDATED) {
                        postsAdapter!!.updateSelectedPost()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        postsAdapter = SearchPostsAdapter((activity as BaseActivity<*, *>?)!!)
        postsAdapter!!.setCallBack(object : SearchPostsAdapter.CallBack {
            override fun onItemClick(post: Post, view: View) {
                PostManager.getInstance(activity!!.applicationContext).isPostExistSingleValue(post.id!!, object : OnObjectExistListener<Post> {
                    override fun onDataChanged(exist: Boolean) {
                        if (exist) {
                            openPostDetailsActivity(post, view)
                        } else {
                            showSnackBar(R.string.error_post_was_removed)
                        }
                    }
                })
            }

            override fun onAuthorClick(authorId: String, view: View) {
                openProfileActivity(authorId, view)
            }

            override fun enableClick(): Boolean {
                return !searchInProgress
            }
        })

        (recyclerView!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView!!.adapter = postsAdapter

        presenter.search()
    }


    @SuppressLint("RestrictedApi")
    fun openProfileActivity(userId: String, view: View?) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            val authorImageView = view.findViewById<View>(R.id.authorImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    android.util.Pair(authorImageView, getString(R.string.post_author_image_transition_name)))
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle())
        } else {
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST)
        }
    }


    @SuppressLint("RestrictedApi")
    private fun openPostDetailsActivity(post: Post, v: View) {
        val intent = Intent(activity, PostDetailsActivity::class.java)
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.id)
        intent.putExtra(PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val imageView = v.findViewById<View>(R.id.postImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair(imageView, getString(R.string.post_image_transition_name))
            )
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle())
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST)
        }
    }

    override fun search(searchText: String) {
        presenter.search(searchText)
    }

    override fun onSearchResultsReady(posts: List<Post>) {
        hideLocalProgress()
        emptyListMessageTextView!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        postsAdapter!!.setList(posts)
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
