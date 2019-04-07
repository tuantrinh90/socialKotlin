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

package com.rozdoum.socialcomponents.main.main

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.PostsAdapter
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.post.createPost.CreatePostActivity
import com.rozdoum.socialcomponents.main.postDetails.PostDetailsActivity
import com.rozdoum.socialcomponents.main.profile.ProfileActivity
import com.rozdoum.socialcomponents.main.search.SearchActivity
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.AnimationUtils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : BaseActivity<MainView, MainPresenter>(), MainView {

    private var postsAdapter: PostsAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var floatingActionButton: FloatingActionButton? = null

    private var newPostsCounterTextView: TextView? = null
    private var counterAnimationInProgress = false
    private var progressBar: ProgressBar? = null
    private var swipeContainer: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val info = packageManager.getPackageInfo(
                    "com.rozdoum.socialcomponents",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        initContentView()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateNewPostCounter()
    }

    override fun createPresenter(): MainPresenter {
        return if (presenter == null) {
            MainPresenter(this)
        } else presenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST -> refreshPostList()
                CreatePostActivity.CREATE_NEW_POST_REQUEST -> presenter.onPostCreated()

                PostDetailsActivity.UPDATE_POST_REQUEST -> presenter.onPostUpdated(data)
            }
        }
    }

    override fun onBackPressed() {
        attemptToExitIfRoot(floatingActionButton)
    }

    override fun refreshPostList() {
        postsAdapter!!.loadFirstPage()
        if (postsAdapter!!.itemCount > 0) {
            recyclerView!!.scrollToPosition(0)
        }
    }

    override fun removePost() {
        postsAdapter!!.removeSelectedPost()
    }

    override fun updatePost() {
        postsAdapter!!.updateSelectedPost()
    }

    override fun showCounterView(count: Int) {
        AnimationUtils.showViewByScaleAndVisibility(newPostsCounterTextView!!)
        val counterFormat = resources.getQuantityString(R.plurals.new_posts_counter_format, count, count)
        newPostsCounterTextView!!.text = String.format(counterFormat, count)
    }

    private fun initContentView() {
        if (recyclerView == null) {
            progressBar = findViewById(R.id.progressBar)
            swipeContainer = findViewById(R.id.swipeContainer)

            initFloatingActionButton()
            initPostListRecyclerView()
            initPostCounter()
        }
    }

    private fun initFloatingActionButton() {
        floatingActionButton = findViewById(R.id.addNewPostFab)
        if (floatingActionButton != null) {
            floatingActionButton!!.setOnClickListener { v -> presenter.onCreatePostClickAction(floatingActionButton) }
        }
    }

    private fun initPostListRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        postsAdapter = PostsAdapter(this, swipeContainer)
        postsAdapter!!.setCallback(object : PostsAdapter.Callback {
            override fun onItemClick(post: Post, view: View) {
                presenter.onPostClicked(post, view)
            }

            override fun onListLoadingFinished() {
                progressBar!!.visibility = View.GONE
            }

            override fun onAuthorClick(authorId: String, view: View) {
                openProfileActivity(authorId, view)
            }

            override fun onCanceled(message: String) {
                progressBar!!.visibility = View.GONE
                showToast(message)
            }
        })

        recyclerView!!.layoutManager = LinearLayoutManager(this)
        (recyclerView!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView!!.adapter = postsAdapter
        postsAdapter!!.loadFirstPage()
    }

    private fun initPostCounter() {
        newPostsCounterTextView = findViewById(R.id.newPostsCounterTextView)
        newPostsCounterTextView!!.setOnClickListener { v -> refreshPostList() }

        presenter.initPostCounter()

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                hideCounterView()
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun hideCounterView() {
        if (!counterAnimationInProgress && newPostsCounterTextView!!.visibility == View.VISIBLE) {
            counterAnimationInProgress = true
            val alphaAnimation = AnimationUtils.hideViewByAlpha(newPostsCounterTextView!!)
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    counterAnimationInProgress = false
                    newPostsCounterTextView!!.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })

            alphaAnimation.start()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun openPostDetailsActivity(post: Post, v: View) {
        val intent = Intent(this@MainActivity, PostDetailsActivity::class.java)
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.id)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val imageView = v.findViewById<View>(R.id.postImageView)
            val authorImageView = v.findViewById<View>(R.id.authorImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity,
                    android.util.Pair(imageView, getString(R.string.post_image_transition_name)),
                    android.util.Pair(authorImageView, getString(R.string.post_author_image_transition_name))
            )
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle())
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST)
        }
    }

    override fun showFloatButtonRelatedSnackBar(messageId: Int) {
        showSnackBar(floatingActionButton, messageId)
    }

    override fun openCreatePostActivity() {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivityForResult(intent, CreatePostActivity.CREATE_NEW_POST_REQUEST)
    }

    @SuppressLint("RestrictedApi")
    override fun openProfileActivity(userId: String, view: View?) {
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            val authorImageView = view.findViewById<View>(R.id.authorImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity,
                    android.util.Pair(authorImageView, getString(R.string.post_author_image_transition_name)))
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle())
        } else {
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.profile -> {
                presenter.onProfileMenuActionClicked()
                return true
            }

            R.id.search -> {
                val searchIntent = Intent(this, SearchActivity::class.java)
                startActivity(searchIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
