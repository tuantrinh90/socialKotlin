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

package com.rozdoum.socialcomponents.main.postDetails

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.main.base.BaseView
import com.rozdoum.socialcomponents.managers.CommentManager
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile

/**
 * Created by Alexey on 03.05.18.
 */

class PostDetailsPresenter(activity: Activity) : BasePresenter<PostDetailsView>(activity) {

    private val postManager: PostManager
    private val profileManager: ProfileManager
    private val commentManager: CommentManager
    var post: Post? = null
        private set
    var isPostExist: Boolean = false
        private set
    private var postRemovingProcess = false

    private var attemptToLoadComments = false

    init {

        postManager = PostManager.getInstance(context.applicationContext)
        profileManager = ProfileManager.getInstance(context.applicationContext)
        commentManager = CommentManager.getInstance(context.applicationContext)
    }

    fun loadPost(postId: String) {
        postManager.getPost(context, postId, object : OnPostChangedListener {
            override fun onObjectChanged(obj: Post) {
                ifViewAttached { view ->
                    if (obj != null) {
                        post = obj
                        isPostExist = true
                        view.initLikeController(post)
                        fillInUI(post!!)
                        view.updateCounters(post)
                        initLikeButtonState()
                        updateOptionMenuVisibility()
                    } else if (!postRemovingProcess) {
                        isPostExist = false
                        view.onPostRemoved()
                        view.showNotCancelableWarningDialog(context.getString(R.string.error_post_was_removed))
                    }
                }
            }

            override fun onError(errorText: String) {
                ifViewAttached { view -> view.showNotCancelableWarningDialog(errorText) }
            }
        })
    }

    private fun initLikeButtonState() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && post != null) {
            postManager.hasCurrentUserLike(context, post!!.id!!, firebaseUser.uid, { exist -> ifViewAttached { view -> view.initLikeButtonState(exist) } })
        }
    }

    private fun fillInUI(post: Post) {
        ifViewAttached { view ->
            view.setTitle(post.title)
            view.setDescription(post.description)
            view.loadPostDetailImage(post.imagePath)

            loadAuthorProfile()
        }
    }

    private fun loadAuthorProfile() {
        if (post != null && post!!.authorId != null) {
            profileManager.getProfileSingleValue(post!!.authorId!!, object : OnObjectChangedListenerSimple<Profile>() {
                override fun onObjectChanged(profile: Profile) {
                    ifViewAttached { view ->
                        if (profile.photoUrl != null) {
                            view.loadAuthorPhoto(profile.photoUrl)
                        }

                        view.setAuthorName(profile.username)
                    }
                }
            })
        }
    }

    fun onAuthorClick(authorView: View) {
        if (post != null) {
            ifViewAttached { view -> view.openProfileActivity(post!!.authorId, authorView) }
        }
    }


    fun onSendButtonClick() {
        if (checkInternetConnection() && checkAuthorization()) {
            sendComment()
        }
    }

    fun hasAccessToModifyPost(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null && post != null && post!!.authorId == currentUser.uid
    }

    fun hasAccessToEditComment(commentAuthorId: String): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null && commentAuthorId == currentUser.uid
    }

    fun updateComment(newText: String, commentId: String) {
        ifViewAttached { it.showProgress() }
        if (post != null) {
            commentManager.updateComment(commentId, newText, post!!.id!!, { success ->
                ifViewAttached { view ->
                    view.hideProgress()
                    view.showSnackBar(R.string.message_comment_was_edited)
                }
            })
        }
    }

    private fun openComplainDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_complain)
                .setMessage(R.string.complain_text)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.add_complain) { dialogInterface, i -> addComplain() }

        builder.create().show()
    }

    private fun addComplain() {
        postManager.addComplain(post!!)
        ifViewAttached { view ->
            view.showComplainMenuAction(false)
            view.showSnackBar(R.string.complain_sent)
        }
    }

    fun doComplainAction() {
        if (checkAuthorization()) {
            openComplainDialog()
        }
    }

    fun attemptToRemovePost() {
        if (hasAccessToModifyPost() && checkInternetConnection()) {
            if (!postRemovingProcess) {
                openConfirmDeletingDialog()
            }
        }
    }

    private fun openConfirmDeletingDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.confirm_deletion_post)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.button_ok) { dialogInterface, i -> removePost() }

        builder.create().show()
    }

    private fun removePost() {
        postRemovingProcess = true
        ifViewAttached { view -> view.showProgress(R.string.removing) }
        postManager.removePost(post!!, { success ->
            ifViewAttached { view ->
                if (success) {
                    view.onPostRemoved()
                    view.finish()
                } else {
                    postRemovingProcess = false
                    view.showSnackBar(R.string.error_fail_remove_post)
                }

                view.hideProgress()
            }
        })


    }

    private fun sendComment() {
        if (post == null) {
            return
        }

        ifViewAttached { view ->
            val commentText = view.commentText

            if (commentText.length > 0 && isPostExist) {
                createOrUpdateComment(commentText)
                view.clearCommentField()
            }
        }
    }

    private fun createOrUpdateComment(commentText: String) {
        commentManager.createOrUpdateComment(commentText, post!!.id!!, { success ->
            ifViewAttached { view ->
                if (success) {
                    if (post != null && post!!.commentsCount > 0) {
                        view.scrollToFirstComment()
                    }
                }
            }
        })
    }

    fun removeComment(commentId: String) {
        ifViewAttached { it.showProgress() }
        commentManager.removeComment(commentId, post!!.id!!, { success ->
            ifViewAttached { view ->
                view.hideProgress()
                view.showSnackBar(R.string.message_comment_was_removed)
            }
        })
    }

    fun editPostAction() {
        if (hasAccessToModifyPost() && checkInternetConnection()) {
            ifViewAttached { view -> view.openEditPostActivity(post) }
        }
    }

    fun updateOptionMenuVisibility() {
        ifViewAttached { view ->
            if (post != null) {
                view.showEditMenuAction(hasAccessToModifyPost())
                view.showDeleteMenuAction(hasAccessToModifyPost())
                view.showComplainMenuAction(!post!!.isHasComplain)
            }
        }
    }

    fun onPostImageClick() {
        ifViewAttached { view ->
            if (post != null && post!!.imagePath != null) {
                view.openImageDetailScreen(post!!.imagePath)
            }
        }
    }

    fun getCommentsList(activityContext: Context, postId: String) {
        attemptToLoadComments = true
        runHidingCommentProgressByTimeOut()

        commentManager.getCommentsList(activityContext, postId, { list ->
            attemptToLoadComments = false
            ifViewAttached { view ->
                view.onCommentsListChanged(list)
                view.showCommentProgress(false)
                view.showCommentsRecyclerView(true)
                view.showCommentsWarning(false)
            }
        })
    }

    private fun runHidingCommentProgressByTimeOut() {
        val handler = Handler()
        handler.postDelayed({
            if (attemptToLoadComments) {
                ifViewAttached { view ->
                    view.showCommentProgress(false)
                    view.showCommentsWarning(true)
                }
            }
        }, TIME_OUT_LOADING_COMMENTS.toLong())
    }

    fun updateCommentsVisibility(commentsCount: Long) {
        ifViewAttached { view ->
            if (commentsCount == 0L) {
                view.showCommentsLabel(false)
                view.showCommentProgress(false)
            } else {
                view.showCommentsLabel(true)
            }
        }
    }

    companion object {
        private val TIME_OUT_LOADING_COMMENTS = 30000
    }
}
