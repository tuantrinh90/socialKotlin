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
import com.rozdoum.socialcomponents.adapters.holders.FollowPostViewHolder
import com.rozdoum.socialcomponents.adapters.holders.PostViewHolder
import com.rozdoum.socialcomponents.controllers.LikeController
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.model.FollowingPost

import java.util.ArrayList


class FollowPostsAdapter(private val activity: BaseActivity<*, *>) : RecyclerView.Adapter<FollowPostViewHolder>() {

    private val itemsList = ArrayList<FollowingPost>()

    private var callBack: CallBack? = null

    private var selectedPostPosition = RecyclerView.NO_POSITION

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item_list_view, parent, false)
        return FollowPostViewHolder(view, createOnClickListener(), activity, true)
    }

    override fun onBindViewHolder(holder: FollowPostViewHolder, position: Int) {
        holder.bindData(itemsList[position])
    }

    fun setList(list: List<FollowingPost>) {
        itemsList.clear()
        itemsList.addAll(list)
        notifyDataSetChanged()
    }

    private fun createOnClickListener(): PostViewHolder.OnClickListener {
        return object : PostViewHolder.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                if (callBack != null) {
                    callBack!!.onItemClick(getItemByPosition(position), view)
                    selectedPostPosition = position
                }
            }

            override fun onLikeClick(likeController: LikeController?, position: Int) {
                val followingPost = getItemByPosition(position)
                likeController!!.handleLikeClickAction(activity, followingPost.postId)
            }

            override fun onAuthorClick(position: Int, view: View) {
                if (callBack != null) {
                    callBack!!.onAuthorClick(position, view)
                }
            }
        }
    }

    fun getItemByPosition(position: Int): FollowingPost {
        return itemsList[position]
    }

    fun updateSelectedItem() {
        if (selectedPostPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPostPosition)
        }
    }

    interface CallBack {
        fun onItemClick(followingPost: FollowingPost, view: View)

        fun onAuthorClick(position: Int, view: View)
    }

    companion object {
        val TAG = FollowPostsAdapter::class.java.simpleName
    }
}
