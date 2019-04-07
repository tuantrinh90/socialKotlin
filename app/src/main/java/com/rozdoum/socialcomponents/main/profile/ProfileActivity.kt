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

package com.rozdoum.socialcomponents.main.profile

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.Toolbar
import android.text.Spannable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.login.LoginManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.PostsByUserAdapter
import com.rozdoum.socialcomponents.dialogs.UnfollowConfirmationDialog
import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.editProfile.EditProfileActivity
import com.rozdoum.socialcomponents.main.login.LoginActivity
import com.rozdoum.socialcomponents.main.main.MainActivity
import com.rozdoum.socialcomponents.main.post.createPost.CreatePostActivity
import com.rozdoum.socialcomponents.main.postDetails.PostDetailsActivity
import com.rozdoum.socialcomponents.main.usersList.UsersListActivity
import com.rozdoum.socialcomponents.main.usersList.UsersListType
import com.rozdoum.socialcomponents.managers.FollowManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.LogUtil
import com.rozdoum.socialcomponents.utils.LogoutHelper
import com.rozdoum.socialcomponents.views.FollowButton

class ProfileActivity : BaseActivity<ProfileView, ProfilePresenter>(), ProfileView, GoogleApiClient.OnConnectionFailedListener, UnfollowConfirmationDialog.Callback, View.OnClickListener {

    // UI references.
    private var nameEditText: TextView? = null
    private var imageView: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var postsCounterTextView: TextView? = null
    private var postsProgressBar: ProgressBar? = null

    private var mAuth: FirebaseAuth? = null
    private val mGoogleApiClient: GoogleApiClient? = null
    private var currentUserId: String? = null
    private var userID: String? = null

    private var postsAdapter: PostsByUserAdapter? = null
    private var swipeContainer: SwipeRefreshLayout? = null
    private var likesCountersTextView: TextView? = null
    private var followersCounterTextView: TextView? = null
    private var followingsCounterTextView: TextView? = null
    private var txtSignOut: TextView? = null
    //private FollowButton followButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        userID = intent.getStringExtra(USER_ID_EXTRA_KEY)

        mAuth = FirebaseAuth.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            currentUserId = firebaseUser.uid
        }

        // Set up the login form.
        progressBar = findViewById(R.id.progressBar)
        imageView = findViewById(R.id.imageView)
        nameEditText = findViewById(R.id.nameEditText)
        txtSignOut = findViewById(R.id.txtSignOut)
        postsCounterTextView = findViewById(R.id.postsCounterTextView)
        likesCountersTextView = findViewById(R.id.likesCountersTextView)
        followersCounterTextView = findViewById(R.id.followersCounterTextView)
        followingsCounterTextView = findViewById(R.id.followingsCounterTextView)
        postsProgressBar = findViewById(R.id.postsProgressBar)
        //followButton = findViewById(R.id.followButton);
        swipeContainer = findViewById(R.id.swipeContainer)

        initListeners()

        txtSignOut!!.setOnClickListener(this)
        presenter.checkFollowState(userID)

        loadPostsList()
        supportPostponeEnterTransition()
    }

    public override fun onStart() {
        super.onStart()
        presenter.loadProfile(this, userID)
        presenter.getFollowersCount(userID)
        presenter.getFollowingsCount(userID)

        mGoogleApiClient?.connect()
    }

    public override fun onStop() {
        super.onStop()
        FollowManager.getInstance(this).closeListeners(this)
        ProfileManager.getInstance(this).closeListeners(this)

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected) {
            mGoogleApiClient.stopAutoManage(this)
            mGoogleApiClient.disconnect()
        }
    }

    override fun createPresenter(): ProfilePresenter {
        return if (presenter == null) {
            ProfilePresenter(this)
        } else presenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CreatePostActivity.CREATE_NEW_POST_REQUEST -> {
                    postsAdapter!!.loadPosts()
                    showSnackBar(R.string.message_post_was_created)
                    setResult(Activity.RESULT_OK)
                }

                PostDetailsActivity.UPDATE_POST_REQUEST -> presenter.checkPostChanges(data)

                LoginActivity.LOGIN_REQUEST_CODE -> presenter.checkFollowState(userID)
            }
        }
    }

    private fun initListeners() {
        //        followButton.setOnClickListener(v -> {
        //            presenter.onFollowButtonClick(followButton.getState(), userID);
        //        });
        followingsCounterTextView!!.setOnClickListener { v -> startUsersListActivity(UsersListType.FOLLOWINGS) }
        followersCounterTextView!!.setOnClickListener { v -> startUsersListActivity(UsersListType.FOLLOWERS) }
        swipeContainer!!.setOnRefreshListener(OnRefreshListener { this.onRefreshAction() })
    }

    private fun onRefreshAction() {
        postsAdapter!!.loadPosts()
    }

    private fun startUsersListActivity(usersListType: Int) {
        val intent = Intent(this@ProfileActivity, UsersListActivity::class.java)
        intent.putExtra(UsersListActivity.USER_ID_EXTRA_KEY, userID)
        intent.putExtra(UsersListActivity.USER_LIST_TYPE, usersListType)
        startActivity(intent)
    }

    private fun loadPostsList() {
        if (recyclerView == null) {
            recyclerView = findViewById(R.id.recycler_view)
            postsAdapter = PostsByUserAdapter(this, userID!!)
            postsAdapter!!.setCallBack(object : PostsByUserAdapter.CallBack {
                override fun onItemClick(post: Post, view: View) {
                    presenter.onPostClick(post, view)
                }

                override fun onPostsListChanged(postsCount: Int) {
                    presenter.onPostListChanged(postsCount)
                }

                override fun onPostLoadingCanceled() {
                    hideLoadingPostsProgress()
                }
            })

            recyclerView!!.layoutManager = LinearLayoutManager(this)
            (recyclerView!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            recyclerView!!.adapter = postsAdapter
            postsAdapter!!.loadPosts()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun openPostDetailsActivity(post: Post, v: View) {
        val intent = Intent(this@ProfileActivity, PostDetailsActivity::class.java)
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.id)
        intent.putExtra(PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, true)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val imageView = v.findViewById<View>(R.id.postImageView)

            val options = ActivityOptions.makeSceneTransitionAnimation(this@ProfileActivity,
                    android.util.Pair(imageView, getString(R.string.post_image_transition_name))
            )
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle())
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST)
        }
    }

    private fun scheduleStartPostponedTransition(imageView: ImageView) {
        imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                supportStartPostponedEnterTransition()
                return true
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun startEditProfileActivity() {
        val intent = Intent(this@ProfileActivity, EditProfileActivity<*, *>::class.java)
        startActivity(intent)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        LogUtil.logDebug(TAG, "onConnectionFailed:$connectionResult")
    }

    override fun openCreatePostActivity() {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivityForResult(intent, CreatePostActivity.CREATE_NEW_POST_REQUEST)
    }

    override fun setProfileName(username: String) {
        nameEditText!!.text = username
    }

    override fun setProfilePhoto(photoUrl: String) {
        ImageUtil.loadImage(GlideApp.with(this), photoUrl, imageView, object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                scheduleStartPostponedTransition(imageView!!)
                progressBar!!.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                scheduleStartPostponedTransition(imageView!!)
                progressBar!!.visibility = View.GONE
                return false
            }
        })
    }

    override fun setDefaultProfilePhoto() {
        progressBar!!.visibility = View.GONE
        imageView!!.setImageResource(R.drawable.ic_stub)
    }

    override fun updateLikesCounter(text: Spannable) {
        likesCountersTextView!!.text = text
    }

    override fun hideLoadingPostsProgress() {
        swipeContainer!!.isRefreshing = false
        if (postsProgressBar!!.visibility != View.GONE) {
            postsProgressBar!!.visibility = View.GONE
        }
    }

    override fun showLikeCounter(show: Boolean) {
        likesCountersTextView!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updatePostsCounter(text: Spannable) {
        postsCounterTextView!!.text = text
    }

    override fun showPostCounter(show: Boolean) {
        postsCounterTextView!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onPostRemoved() {
        postsAdapter!!.removeSelectedPost()
    }

    override fun onPostUpdated() {
        postsAdapter!!.updateSelectedPost()
    }

    override fun showUnfollowConfirmation(profile: Profile) {
        val unfollowConfirmationDialog = UnfollowConfirmationDialog()
        val args = Bundle()
        args.putSerializable(UnfollowConfirmationDialog.PROFILE, profile)
        unfollowConfirmationDialog.arguments = args
        unfollowConfirmationDialog.show(fragmentManager, UnfollowConfirmationDialog.TAG)
    }

    override fun updateFollowButtonState(followState: FollowState) {
        //followButton.setState(followState);
    }

    override fun updateFollowersCount(count: Int) {
        //        followersCounterTextView.setVisibility(View.VISIBLE);
        //        String followersLabel = getResources().getQuantityString(R.plurals.followers_counter_format, count, count);
        //        followersCounterTextView.setText(presenter.buildCounterSpannable(followersLabel, count));
    }

    override fun updateFollowingsCount(count: Int) {
        //        followingsCounterTextView.setVisibility(View.VISIBLE);
        //        String followingsLabel = getResources().getQuantityString(R.plurals.followings_counter_format, count, count);
        //        followingsCounterTextView.setText(presenter.buildCounterSpannable(followingsLabel, count));
    }

    override fun setFollowStateChangeResultOk() {
        setResult(UsersListActivity.UPDATE_FOLLOWING_STATE_RESULT_OK)
    }

    override fun onUnfollowButtonClicked() {
        presenter.unfollowUser(userID)
    }

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        if (userID.equals(currentUserId)) {
    //            MenuInflater inflater = getMenuInflater();
    //            inflater.inflate(R.menu.profile_menu, menu);
    //            return true;
    //        }
    //
    //        return super.onCreateOptionsMenu(menu);
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        // Handle item selection
    //        switch (item.getItemId()) {
    //            case R.id.editProfile:
    //                presenter.onEditProfileClick();
    //                return true;
    //            case R.id.signOut:
    //                LogoutHelper.signOut(mGoogleApiClient, this);
    //                if (LoginManager.getInstance() != null) LoginManager.getInstance().logOut();
    //                startMainActivity();
    //                return true;
    //            case R.id.createPost:
    //                presenter.onCreatePostClick();
    //            default:
    //                return super.onOptionsItemSelected(item);
    //        }
    //    }

    override fun onClick(v: View) {
        if (v.id == R.id.txtSignOut) {
            LogoutHelper.signOut(mGoogleApiClient!!, this)
            if (LoginManager.getInstance() != null) LoginManager.getInstance().logOut()
            startMainActivity()
        }
    }

    companion object {
        private val TAG = ProfileActivity::class.java.simpleName
        val CREATE_POST_FROM_PROFILE_REQUEST = 22
        val USER_ID_EXTRA_KEY = "ProfileActivity.USER_ID_EXTRA_KEY"
    }
}
