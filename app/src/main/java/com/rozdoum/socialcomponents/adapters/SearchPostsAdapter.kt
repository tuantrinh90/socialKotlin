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

package com.rozdoum.socialcomponents.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.holders.PostViewHolder
import com.rozdoum.socialcomponents.controllers.LikeController
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.model.Post


class SearchPostsAdapter(activity: BaseActivity<*, *>) : BasePostsAdapter(activity) {

    private var callBack: CallBack? = null

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item_list_view, parent, false)

        return PostViewHolder(view, createOnClickListener(), activity, true)
    }

    private fun createOnClickListener(): PostViewHolder.OnClickListener {
        return object : PostViewHolder.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                if (callBack != null && callBack!!.enableClick()) {
                    selectedPostPosition = position
                    callBack!!.onItemClick(getItemByPosition(position), view)
                }
            }

            override fun onLikeClick(likeController: LikeController?, position: Int) {
                if (callBack != null && callBack!!.enableClick()) {
                    val post = getItemByPosition(position)
                    likeController!!.handleLikeClickAction(activity, post)
                }
            }

            override fun onAuthorClick(position: Int, view: View) {
                if (callBack != null && callBack!!.enableClick()) {
                    callBack!!.onAuthorClick(getItemByPosition(position).authorId, view)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostViewHolder).bindData(postList[position])
    }

    fun setList(list: List<Post>) {
        cleanSelectedPostInformation()
        postList.clear()
        postList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeSelectedPost() {
        if (selectedPostPosition != RecyclerView.NO_POSITION) {
            postList.removeAt(selectedPostPosition)
            notifyItemRemoved(selectedPostPosition)
        }
    }

    interface CallBack {
        fun onItemClick(post: Post, view: View)
        fun onAuthorClick(authorId: String, view: View)
        fun enableClick(): Boolean
    }

    companion object {
        val TAG = SearchPostsAdapter::class.java.simpleName
    }
}
