/*
 * Copyright 2017 Rozdoum
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

package com.rozdoum.socialcomponents.controllers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.design.widget.Snackbar
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.interactors.PostInteractor
import com.rozdoum.socialcomponents.main.main.MainActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.model.Post

/**
 * Created by Kristina on 12/30/16.
 */

class LikeController(private val context: Context, post: Post, private val likeCounterTextView: TextView,
                     private val likesImageView: ImageView, isListView: Boolean) {
    private val postId: String
    private val postAuthorId: String

    var likeAnimationType = LikeController.AnimationType.BOUNCE_ANIM

    private val isListView = false

    var isLiked = false
    var isUpdatingLikeCounter = true

    enum class AnimationType {
        COLOR_ANIM, BOUNCE_ANIM
    }

    init {
        this.postId = post.id
        this.postAuthorId = post.authorId
        this.isListView = isListView
    }

    fun likeClickAction(prevValue: Long) {
        if (!isUpdatingLikeCounter) {
            startAnimateLikeButton(likeAnimationType)

            if (!isLiked) {
                addLike(prevValue)
            } else {
                removeLike(prevValue)
            }
        }
    }

    fun likeClickActionLocal(post: Post) {
        isUpdatingLikeCounter = false
        likeClickAction(post.likesCount)
        updateLocalPostLikeCounter(post)
    }

    private fun addLike(prevValue: Long) {
        isUpdatingLikeCounter = true
        isLiked = true
        likeCounterTextView.text = (prevValue + 1).toString()
        PostInteractor.getInstance(context).createOrUpdateLike(postId, postAuthorId)
    }

    private fun removeLike(prevValue: Long) {
        isUpdatingLikeCounter = true
        isLiked = false
        likeCounterTextView.text = (prevValue - 1).toString()
        PostInteractor.getInstance(context).removeLike(postId, postAuthorId)
    }

    private fun startAnimateLikeButton(animationType: AnimationType) {
        when (animationType) {
            LikeController.AnimationType.BOUNCE_ANIM -> bounceAnimateImageView()
            LikeController.AnimationType.COLOR_ANIM -> colorAnimateImageView()
        }
    }

    private fun bounceAnimateImageView() {
        val animatorSet = AnimatorSet()

        val bounceAnimX = ObjectAnimator.ofFloat(likesImageView, "scaleX", 0.2f, 1f)
        bounceAnimX.duration = ANIMATION_DURATION.toLong()
        bounceAnimX.interpolator = BounceInterpolator()

        val bounceAnimY = ObjectAnimator.ofFloat(likesImageView, "scaleY", 0.2f, 1f)
        bounceAnimY.duration = ANIMATION_DURATION.toLong()
        bounceAnimY.interpolator = BounceInterpolator()
        bounceAnimY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                likesImageView.setImageResource(if (!isLiked)
                    R.drawable.ic_like_active
                else
                    R.drawable.ic_like)
            }
        })

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {}
        })

        animatorSet.play(bounceAnimX).with(bounceAnimY)
        animatorSet.start()
    }

    private fun colorAnimateImageView() {
        val activatedColor = context.resources.getColor(R.color.like_icon_activated)

        val colorAnim = if (!isLiked)
            ObjectAnimator.ofFloat(0f, 1f)
        else
            ObjectAnimator.ofFloat(1f, 0f)
        colorAnim.duration = ANIMATION_DURATION.toLong()
        colorAnim.addUpdateListener { animation ->
            val mul = animation.animatedValue as Float
            val alpha = adjustAlpha(activatedColor, mul)
            likesImageView.setColorFilter(alpha, PorterDuff.Mode.SRC_ATOP)
            if (mul.toDouble() == 0.0) {
                likesImageView.colorFilter = null
            }
        }

        colorAnim.start()
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun initLike(isLiked: Boolean) {
        likesImageView.setImageResource(if (isLiked) R.drawable.ic_like_active else R.drawable.ic_like)
        this.isLiked = isLiked
    }

    private fun updateLocalPostLikeCounter(post: Post) {
        if (isLiked) {
            post.likesCount = post.likesCount + 1
        } else {
            post.likesCount = post.likesCount - 1
        }
    }

    fun handleLikeClickAction(baseActivity: BaseActivity<*, *>, post: Post) {
        PostManager.getInstance(baseActivity.getApplicationContext()).isPostExistSingleValue(post.id) { exist ->
            if (exist) {
                if (baseActivity.hasInternetConnection()) {
                    doHandleLikeClickAction(baseActivity, post)
                } else {
                    showWarningMessage(baseActivity, R.string.internet_connection_failed)
                }
            } else {
                showWarningMessage(baseActivity, R.string.message_post_was_removed)
            }
        }
    }

    fun handleLikeClickAction(baseActivity: BaseActivity<*, *>, postId: String) {
        PostManager.getInstance(baseActivity.getApplicationContext()).getSinglePostValue(postId, object : OnPostChangedListener {
            override fun onObjectChanged(post: Post) {
                if (baseActivity.hasInternetConnection()) {
                    doHandleLikeClickAction(baseActivity, post)
                } else {
                    showWarningMessage(baseActivity, R.string.internet_connection_failed)
                }
            }

            override fun onError(errorText: String) {
                baseActivity.showSnackBar(errorText)
            }
        })
    }

    private fun showWarningMessage(baseActivity: BaseActivity<*, *>, messageId: Int) {
        if (baseActivity is MainActivity) {
            baseActivity.showFloatButtonRelatedSnackBar(messageId)
        } else {
            baseActivity.showSnackBar(messageId)
        }
    }

    private fun doHandleLikeClickAction(baseActivity: BaseActivity<*, *>, post: Post) {
        if (baseActivity.checkAuthorization()) {
            if (isListView) {
                likeClickActionLocal(post)
            } else {
                likeClickAction(post.likesCount)
            }
        }
    }

    fun changeAnimationType() {
        if (likeAnimationType == LikeController.AnimationType.BOUNCE_ANIM) {
            likeAnimationType = LikeController.AnimationType.COLOR_ANIM
        } else {
            likeAnimationType = LikeController.AnimationType.BOUNCE_ANIM
        }

        val snackbar = Snackbar
                .make(likesImageView, "Animation was changed", Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    companion object {

        private val ANIMATION_DURATION = 300
    }
}
