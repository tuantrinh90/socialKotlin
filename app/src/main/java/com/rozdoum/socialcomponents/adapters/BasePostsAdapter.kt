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

import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.LogUtil

import java.util.LinkedList

abstract class BasePostsAdapter(protected var activity: BaseActivity<*, *>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var postList: MutableList<Post> = LinkedList()
    protected var selectedPostPosition = RecyclerView.NO_POSITION

    protected fun cleanSelectedPostInformation() {
        selectedPostPosition = -1
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun getItemViewType(position: Int): Int {
        return postList[position].itemType.typeCode
    }

    protected fun getItemByPosition(position: Int): Post {
        return postList[position]
    }

    private fun createOnPostChangeListener(postPosition: Int): OnPostChangedListener {
        return object : OnPostChangedListener {
            override fun onObjectChanged(obj: Post) {
                postList[postPosition] = obj
                notifyItemChanged(postPosition)
            }

            override fun onError(errorText: String) {
                LogUtil.logDebug(TAG, errorText)
            }
        }
    }

    fun updateSelectedPost() {
        if (selectedPostPosition != RecyclerView.NO_POSITION) {
            val selectedPost = getItemByPosition(selectedPostPosition)
            PostManager.getInstance(activity).getSinglePostValue(selectedPost.id, createOnPostChangeListener(selectedPostPosition))
        }
    }

    companion object {
        val TAG = BasePostsAdapter::class.java.simpleName
    }
}
