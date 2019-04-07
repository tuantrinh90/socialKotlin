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

package com.rozdoum.socialcomponents.adapters.holders

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rozdoum.socialcomponents.Constants
import com.rozdoum.socialcomponents.GlideApp
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.controllers.LikeController
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListener
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.managers.listeners.OnObjectExistListener
import com.rozdoum.socialcomponents.model.Like
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.FormatterUtil
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.utils.Utils

/**
 * Created by alexey on 27.12.16.
 */

open class PostViewHolder constructor(view: View, onClickListener: OnClickListener?,
                                      private val baseActivity: BaseActivity<*, *>,
                                      isAuthorNeeded: Boolean = true) : RecyclerView.ViewHolder(view) {

    protected var context: Context
    private val postImageView: ImageView
    private val titleTextView: TextView
    private val detailsTextView: TextView
    private val likeCounterTextView: TextView
    private val likesImageView: ImageView
    private val commentsCountTextView: TextView
    //private TextView watcherCounterTextView;
    private val dateTextView: TextView
    private val authorImageView: ImageView
    private val likeViewGroup: ViewGroup

    private val profileManager: ProfileManager
    protected var postManager: PostManager

    private var likeController: LikeController? = null
    private val shareContainer: LinearLayout

    init {
        this.context = view.context

        postImageView = view.findViewById(R.id.postImageView)
        likeCounterTextView = view.findViewById(R.id.likeCounterTextView)
        likesImageView = view.findViewById(R.id.likesImageView)
        commentsCountTextView = view.findViewById(R.id.commentsCountTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        titleTextView = view.findViewById(R.id.titleTextView)
        detailsTextView = view.findViewById(R.id.detailsTextView)
        authorImageView = view.findViewById(R.id.authorImageView)
        likeViewGroup = view.findViewById(R.id.likesContainer)
        shareContainer = view.findViewById(R.id.shareContainer)

        authorImageView.visibility = if (isAuthorNeeded) View.VISIBLE else View.GONE

        profileManager = ProfileManager.getInstance(context.applicationContext)
        postManager = PostManager.getInstance(context.applicationContext)

        view.setOnClickListener { v ->
            val position = adapterPosition
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onItemClick(adapterPosition, v)
            }
        }

        likeViewGroup.setOnClickListener { view1 ->
            val position = adapterPosition
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onLikeClick(likeController, position)
            }
        }

        authorImageView.setOnClickListener { v ->
            val position = adapterPosition
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onAuthorClick(adapterPosition, v)
            }
        }
    }

    fun bindData(post: Post) {

        likeController = LikeController(context, post, likeCounterTextView, likesImageView, true)

        val title = removeNewLinesDividers(post.title!!)
        titleTextView.text = title
        val description = removeNewLinesDividers(post.description!!)
        detailsTextView.text = description
        likeCounterTextView.text = post.likesCount.toString()
        commentsCountTextView.text = post.commentsCount.toString()

        val date = FormatterUtil.getRelativeTimeSpanStringShort(context, post.createdDate)
        dateTextView.text = date

        val imageUrl = post.imagePath
        val width = Utils.getDisplayWidth(context)
        val height = context.resources.getDimension(R.dimen.post_detail_image_height).toInt()

        // Displayed and saved to cache image, as needs for post detail.
        ImageUtil.loadImageCenterCrop(GlideApp.with(baseActivity), imageUrl!!, postImageView, width, height)


        if (post.authorId != null) {
            profileManager.getProfileSingleValue(post.authorId!!, createProfileChangeListener(authorImageView))
        }

        shareContainer.setOnClickListener { v -> Utils.share(context, imageUrl) }

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            postManager.hasCurrentUserLikeSingleValue(post.id!!, firebaseUser.uid, createOnLikeObjectExistListener())
        }
    }

    private fun removeNewLinesDividers(text: String): String {
        val decoratedTextLength = if (text.length < Constants.MAX_TEXT_LENGTH_IN_LIST)
            text.length
        else
            Constants.MAX_TEXT_LENGTH_IN_LIST
        return text.substring(0, decoratedTextLength).replace("\n".toRegex(), " ").trim { it <= ' ' }
    }

    private fun createProfileChangeListener(authorImageView: ImageView): OnObjectChangedListener<Profile> {
        return object : OnObjectChangedListenerSimple<Profile>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            override fun onObjectChanged(obj: Profile) {
                if (obj.photoUrl != null) {
                    if (!baseActivity.isFinishing() && !baseActivity.isDestroyed()) {
                        ImageUtil.loadImage(GlideApp.with(baseActivity), obj.photoUrl!!, authorImageView)
                    }
                }
            }
        }
    }

    private fun createOnLikeObjectExistListener(): OnObjectExistListener<Like> {
        return likeController.initLike(true)
    }

    interface OnClickListener {
        fun onItemClick(position: Int, view: View)

        fun onLikeClick(likeController: LikeController?, position: Int)

        fun onAuthorClick(position: Int, view: View)
    }

    companion object {
        val TAG = PostViewHolder::class.java.simpleName
    }
}