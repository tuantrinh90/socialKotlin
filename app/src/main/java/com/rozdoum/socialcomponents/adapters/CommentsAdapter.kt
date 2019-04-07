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

package com.rozdoum.socialcomponents.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.holders.CommentViewHolder
import com.rozdoum.socialcomponents.model.Comment

import java.util.ArrayList

/**
 * Created by alexey on 10.05.17.
 */

class CommentsAdapter : RecyclerView.Adapter<CommentViewHolder>() {
    private var list: List<Comment> = ArrayList()
    private var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.itemView.isLongClickable = true
        holder.bindData(getItemByPosition(position))
    }

    fun getItemByPosition(position: Int): Comment {
        return list[position]
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setList(list: List<Comment>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface Callback {
        fun onLongItemClick(view: View, position: Int)

        fun onAuthorClick(authorId: String, view: View)
    }
}
