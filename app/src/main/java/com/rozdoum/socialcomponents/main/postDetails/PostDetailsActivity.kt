/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rozdoum.socialcomponents.main.postDetails

import android.animation.Animator
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.transition.Transition
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.Constants.Companion.POST_STATUS_EXTRA_KEY
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.CommentsAdapter
import com.rozdoum.socialcomponents.controllers.LikeController
import com.rozdoum.socialcomponents.dialogs.EditCommentDialog
import com.rozdoum.socialcomponents.enums.PostStatus
import com.rozdoum.socialcomponents.listeners.CustomTransitionListener
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.imageDetail.ImageDetailActivity
import com.rozdoum.socialcomponents.main.post.editPost.EditPostActivity
import com.rozdoum.socialcomponents.main.profile.ProfileActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.model.Comment
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.AnimationUtils
import com.rozdoum.socialcomponents.utils.FormatterUtil
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.Utils

class PostDetailsActivity : BaseActivity<PostDetailsView, PostDetailsPresenter>(),
        PostDetailsView, EditCommentDialog.CommentDialogCallback {

    private var commentEditText: EditText? = null
    private var scrollView: ScrollView? = null
    private var likesContainer: ViewGroup? = null
    private var likesImageView: ImageView? = null
    private var commentsLabel: TextView? = null
    private var likeCounterTextView: TextView? = null
    private var commentsCountTextView: TextView? = null
    //private TextView watcherCounterTextView;
    private var authorTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var authorImageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var postImageView: ImageView? = null
    private var titleTextView: TextView? = null
    private var descriptionEditText: TextView? = null
    private var commentsProgressBar: ProgressBar? = null
    private var commentsRecyclerView: RecyclerView? = null
    private var warningCommentsTextView: TextView? = null


    private var complainActionMenuItem: MenuItem? = null
    private var editActionMenuItem: MenuItem? = null
    private var deleteActionMenuItem: MenuItem? = null

    private var postId: String? = null

    private var postManager: PostManager? = null
    private var likeController: LikeController? = null
    private var authorAnimationInProgress = false

    private var isAuthorAnimationRequired: Boolean = false
    private var commentsAdapter: CommentsAdapter? = null
    private var mActionMode: ActionMode? = null
    private var isEnterTransitionFinished = false
    private var sendButton: Button? = null
    private var shareContainer: LinearLayout? = null

    override val commentText: String
        get() = commentEditText!!.text.toString()

    internal var authorAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            authorAnimationInProgress = true
        }

        override fun onAnimationEnd(animation: Animator) {
            authorAnimationInProgress = false
        }

        override fun onAnimationCancel(animation: Animator) {
            authorAnimationInProgress = false
        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        postManager = PostManager.getInstance(this)

        isAuthorAnimationRequired = intent.getBooleanExtra(Constants.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, false)
        postId = intent.getStringExtra(Constants.POST_ID_EXTRA_KEY)

        incrementWatchersCount()

        titleTextView = findViewById(R.id.titleTextView)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        postImageView = findViewById(R.id.postImageView)
        progressBar = findViewById(R.id.progressBar)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        scrollView = findViewById(R.id.scrollView)
        commentsLabel = findViewById(R.id.commentsLabel)
        commentEditText = findViewById(R.id.commentEditText)
        likesContainer = findViewById(R.id.likesContainer)
        likesImageView = findViewById(R.id.likesImageView)
        authorImageView = findViewById(R.id.authorImageView)
        authorTextView = findViewById(R.id.authorTextView)
        likeCounterTextView = findViewById(R.id.likeCounterTextView)
        commentsCountTextView = findViewById(R.id.commentsCountTextView)
        //watcherCounterTextView = findViewById(R.id.watcherCounterTextView);
        dateTextView = findViewById(R.id.dateTextView)
        commentsProgressBar = findViewById(R.id.commentsProgressBar)
        warningCommentsTextView = findViewById(R.id.warningCommentsTextView)
        sendButton = findViewById(R.id.sendButton)
        shareContainer = findViewById(R.id.shareContainer)


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAuthorAnimationRequired) {
            authorImageView!!.scaleX = 0f
            authorImageView!!.scaleY = 0f

            // Add a listener to get noticed when the transition ends to animate the fab button
            window.sharedElementEnterTransition.addListener(object : CustomTransitionListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onTransitionEnd(transition: Transition) {
                    super.onTransitionEnd(transition)
                    //disable execution for exit transition
                    if (!isEnterTransitionFinished) {
                        isEnterTransitionFinished = true
                        AnimationUtils.showViewByScale(authorImageView!!)
                                .setListener(authorAnimatorListener)
                                .start()
                    }
                }
            })
        }

        initRecyclerView()
        initListeners()

        presenter.loadPost(postId)
        supportPostponeEnterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
        postManager!!.closeListeners(this)
    }

    override fun createPresenter(): PostDetailsPresenter {
        return if (presenter == null) {
            PostDetailsPresenter(this)
        } else presenter
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && isAuthorAnimationRequired
                && !authorAnimationInProgress
                && !AnimationUtils.isViewHiddenByScale(authorImageView!!)) {

            val hideAuthorAnimator = AnimationUtils.hideViewByScale(authorImageView!!)
            hideAuthorAnimator.setListener(authorAnimatorListener)
            hideAuthorAnimator.withEndAction { this@PostDetailsActivity.onBackPressed() }
        } else {
            super.onBackPressed()
        }
    }

    private fun initListeners() {
        postImageView!!.setOnClickListener { v -> presenter.onPostImageClick() }

        commentEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                sendButton!!.isEnabled = charSequence.toString().trim { it <= ' ' }.length > 0
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        sendButton!!.setOnClickListener { v -> presenter.onSendButtonClick() }

        commentsCountTextView!!.setOnClickListener { view -> scrollToFirstComment() }

        authorImageView!!.setOnClickListener { v -> presenter.onAuthorClick(v) }
        authorTextView!!.setOnClickListener { v -> presenter.onAuthorClick(v) }

        likesContainer!!.setOnClickListener { v ->
            if (likeController != null && presenter.isPostExist) {
                likeController!!.handleLikeClickAction(this, presenter.post!!)
            }
        }

        //long click for changing animation
        likesContainer!!.setOnLongClickListener {
            likeController!!.changeAnimationType()
            true
        }

        fun initRecyclerView() {
            commentsAdapter = CommentsAdapter()
            commentsAdapter!!.setCallback(object : CommentsAdapter.Callback {
                override fun onLongItemClick(view: View, position: Int) {
                    val selectedComment = commentsAdapter!!.getItemByPosition(position)
                    startActionMode(selectedComment)
                }

                override fun onAuthorClick(authorId: String, view: View) {
                    openProfileActivity(authorId, view)
                }
            })
            commentsRecyclerView!!.adapter = commentsAdapter
            commentsRecyclerView!!.isNestedScrollingEnabled = false
            commentsRecyclerView!!.addItemDecoration(DividerItemDecoration(commentsRecyclerView!!.context,
                    (commentsRecyclerView!!.layoutManager as LinearLayoutManager).orientation))

            presenter.getCommentsList(this, postId)
        }

        private fun startActionMode(selectedComment: Comment) {
            if (mActionMode != null) {
                return
            }

            //check access to modify or remove post
            if (presenter.hasAccessToEditComment(selectedComment.authorId) || presenter.hasAccessToModifyPost()) {
                mActionMode = startSupportActionMode(ActionModeCallback(selectedComment))
            }
        }

        fun incrementWatchersCount() {
            postManager!!.incrementWatchersCount(postId!!)
            val intent = intent
            setResult(Activity.RESULT_OK, intent.putExtra(POST_STATUS_EXTRA_KEY, PostStatus.UPDATED))
        }

        override fun scrollToFirstComment() {
            scrollView!!.smoothScrollTo(0, commentsLabel!!.top)
        }

        override fun clearCommentField() {
            commentEditText!!.setText(null)
            commentEditText!!.clearFocus()
            hideKeyboard()
        }

         fun scheduleStartPostponedTransition(imageView: ImageView) {
            imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    imageView.viewTreeObserver.removeOnPreDrawListener(this)
                    supportStartPostponedEnterTransition()
                    return true
                }
            })
        }

        override fun openImageDetailScreen(imagePath: String) {
            val intent = Intent(this, ImageDetailActivity::class.java)
            intent.putExtra(ImageDetailActivity.IMAGE_URL_EXTRA_KEY, imagePath)
            startActivity(intent)
        }

        override fun openProfileActivity(userId: String, view: View?) {
            val intent = Intent(this@PostDetailsActivity, ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId)

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

                val options = ActivityOptions.makeSceneTransitionAnimation(this@PostDetailsActivity,
                        android.util.Pair(view, getString(R.string.post_author_image_transition_name)))
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        }

        override fun setTitle(title: String) {
            titleTextView!!.text = title
        }

        override fun setDescription(description: String) {
            descriptionEditText!!.text = description
        }

        override fun loadPostDetailImage(imagePath: String) {
            val width = Utils.getDisplayWidth(this)
            val height = resources.getDimension(R.dimen.post_detail_image_height).toInt()

            ImageUtil.loadImageCenterCrop(GlideApp.with(this), imagePath, postImageView!!, width, height, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    scheduleStartPostponedTransition(postImageView!!)
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    scheduleStartPostponedTransition(postImageView!!)
                    progressBar!!.visibility = View.GONE
                    return false
                }
            })
        }

        override fun loadAuthorPhoto(photoUrl: String) {
            ImageUtil.loadImage(GlideApp.with(this@PostDetailsActivity), photoUrl, authorImageView, DiskCacheStrategy.DATA)
        }

        override fun setAuthorName(username: String) {
            authorTextView!!.text = username
        }

        override fun initLikeController(post: Post) {
            likeController = LikeController(this, post, likeCounterTextView!!, likesImageView!!, false)
        }

        override fun updateCounters(post: Post) {
            val commentsCount = post.commentsCount
            commentsCountTextView!!.text = commentsCount.toString()
            commentsLabel!!.text = String.format(getString(R.string.label_comments), commentsCount)
            likeCounterTextView!!.text = post.likesCount.toString()
            likeController!!.isUpdatingLikeCounter = false
            //watcherCounterTextView.setText(String.valueOf(post.getWatchersCount()));
            val date = FormatterUtil.getRelativeTimeSpanStringShort(this, post.createdDate)
            dateTextView!!.text = date
            shareContainer!!.setOnClickListener { v -> Utils.share(applicationContext, post.imagePath!!) }
            presenter.updateCommentsVisibility(commentsCount)
        }

        override fun initLikeButtonState(exist: Boolean) {
            if (likeController != null) {
                likeController!!.initLike(exist)
            }
        }

        override fun showComplainMenuAction(show: Boolean) {
            if (complainActionMenuItem != null) {
                complainActionMenuItem!!.isVisible = show
            }
        }

        override fun showEditMenuAction(show: Boolean) {
            if (editActionMenuItem != null) {
                editActionMenuItem!!.isVisible = show
            }
        }

        override fun showDeleteMenuAction(show: Boolean) {
            if (deleteActionMenuItem != null) {
                deleteActionMenuItem!!.isVisible = show
            }
        }

        override fun openEditPostActivity(post: Post) {
            val intent = Intent(this@PostDetailsActivity, EditPostActivity::class.java)
            intent.putExtra(EditPostActivity.POST_EXTRA_KEY, post)
            startActivityForResult(intent, EditPostActivity.EDIT_POST_REQUEST)
        }

        override fun showCommentProgress(show: Boolean) {
            commentsProgressBar!!.visibility = if (show) View.VISIBLE else View.GONE
        }

        override fun showCommentsWarning(show: Boolean) {
            warningCommentsTextView!!.visibility = if (show) View.VISIBLE else View.GONE
        }

        override fun showCommentsRecyclerView(show: Boolean) {
            commentsRecyclerView!!.visibility = if (show) View.VISIBLE else View.GONE
        }

        override fun onCommentsListChanged(list: List<Comment>) {
            commentsAdapter!!.setList(list)
        }

        override fun showCommentsLabel(show: Boolean) {
            commentsLabel!!.visibility = if (show) View.VISIBLE else View.GONE
        }

        private fun openEditCommentDialog(comment: Comment) {
            val editCommentDialog = EditCommentDialog()
            val args = Bundle()
            args.putString(EditCommentDialog.COMMENT_TEXT_KEY, comment.text)
            args.putString(EditCommentDialog.COMMENT_ID_KEY, comment.id)
            editCommentDialog.arguments = args
            editCommentDialog.show(fragmentManager, EditCommentDialog.TAG)
        }

        override fun onCommentChanged(newText: String, commentId: String?) {
            presenter.updateComment(newText, commentId)
        }

        override fun onPostRemoved() {
            val intent = intent
            setResult(Activity.RESULT_OK, intent.putExtra(POST_STATUS_EXTRA_KEY, PostStatus.REMOVED))
        }

        private inner class ActionModeCallback internal constructor(internal var selectedComment: Comment) : ActionMode.Callback {

            // Called when the action mode is created; startActionMode() was called
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.comment_context_menu, menu)

                menu.findItem(R.id.editMenuItem).isVisible = presenter.hasAccessToEditComment(selectedComment.authorId)

                return true
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // Return false if nothing is done
            }
            // Called when the user selects a contextual menu item

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.editMenuItem -> {
                        openEditCommentDialog(selectedComment)
                        mode.finish() // Action picked, so close the CAB
                        return true
                    }
                    R.id.deleteMenuItem -> {
                        presenter.removeComment(selectedComment.id)
                        mode.finish()
                        return true
                    }
                    else -> return false
                }
            }
            // Called when the user exits the action mode

            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
            }
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.post_details_menu, menu)
            complainActionMenuItem = menu.findItem(R.id.complain_action)
            editActionMenuItem = menu.findItem(R.id.edit_post_action)
            deleteActionMenuItem = menu.findItem(R.id.delete_post_action)
            presenter.updateOptionMenuVisibility()
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (!presenter.isPostExist) {
                return super.onOptionsItemSelected(item)
            }

            // Handle item selection
            when (item.itemId) {
                R.id.complain_action -> {
                    presenter.doComplainAction()
                    return true
                }

                R.id.edit_post_action -> {
                    presenter.editPostAction()
                    return true
                }

                R.id.delete_post_action -> {
                    presenter.attemptToRemovePost()
                    return true
                }
            }

            return super.onOptionsItemSelected(item)
        }
    }
}
