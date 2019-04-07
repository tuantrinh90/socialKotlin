/*
 *
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
 *
 */

package com.rozdoum.socialcomponents.adapters.holders

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.CommentsAdapter
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListener
import com.rozdoum.socialcomponents.model.Comment
import com.rozdoum.socialcomponents.model.Profile
import com.rozdoum.socialcomponents.utils.FormatterUtil
import com.rozdoum.socialcomponents.utils.GlideApp
import com.rozdoum.socialcomponents.utils.ImageUtil
import com.rozdoum.socialcomponents.views.ExpandableTextView

/**
 * Created by alexey on 10.05.17.
 */

class CommentViewHolder(itemView: View, private val callback: CommentsAdapter.Callback?) : RecyclerView.ViewHolder(itemView) {

    private val avatarImageView: ImageView
    private val commentTextView: ExpandableTextView
    private val dateTextView: TextView
    private val profileManager: ProfileManager
    private val context: Context

    init {
        this.context = itemView.context
        profileManager = ProfileManager.getInstance(itemView.context.applicationContext)

        avatarImageView = itemView.findViewById<View>(R.id.avatarImageView) as ImageView
        commentTextView = itemView.findViewById<View>(R.id.commentText) as ExpandableTextView
        dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView

        if (callback != null) {
            itemView.setOnLongClickListener(View.OnLongClickListener { v ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    callback.onLongItemClick(v, position)
                    return@OnLongClickListener true
                }

                false
            })
        }
    }

    fun bindData(comment: Comment) {
        val authorId = comment.authorId

        if (authorId != null) {
            profileManager.getProfileSingleValue(authorId, createOnProfileChangeListener(commentTextView,
                    avatarImageView, comment, dateTextView))
        } else {
            fillComment("", comment, commentTextView, dateTextView)
        }

        avatarImageView.setOnClickListener { v -> callback!!.onAuthorClick(authorId, v) }
    }

    private fun createOnProfileChangeListener(expandableTextView: ExpandableTextView,
                                              avatarImageView: ImageView,
                                              comment: Comment,
                                              dateTextView: TextView): OnObjectChangedListener<Profile> {
        return object : OnObjectChangedListener<Profile> {
            override fun onObjectChanged(obj: Profile) {
                val userName = obj.username
                fillComment(userName, comment, expandableTextView, dateTextView)

                if (obj.photoUrl != null) {
                    ImageUtil.loadImage(GlideApp.with(context), obj.photoUrl, avatarImageView)
                }
            }

            override fun onError(errorText: String) {
                fillComment("", comment, commentTextView, dateTextView)
            }
        }
    }

    private fun fillComment(userName: String, comment: Comment, commentTextView: ExpandableTextView, dateTextView: TextView) {
        val contentString = SpannableStringBuilder(userName + "   " + comment.text)
        contentString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.highlight_text)),
                0, userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        commentTextView.text = contentString

        val date = FormatterUtil.getRelativeTimeSpanString(context, comment.createdDate)
        dateTextView.text = date
    }
}
