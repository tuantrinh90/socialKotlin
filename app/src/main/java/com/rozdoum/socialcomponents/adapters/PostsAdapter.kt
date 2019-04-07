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

package com.rozdoum.socialcomponents.adapters

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.holders.LoadViewHolder
import com.rozdoum.socialcomponents.adapters.holders.PostViewHolder
import com.rozdoum.socialcomponents.controllers.LikeController
import com.rozdoum.socialcomponents.enums.ItemType
import com.rozdoum.socialcomponents.main.main.MainActivity
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.managers.listeners.OnPostListChangedListener
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.model.PostListResult
import com.rozdoum.socialcomponents.utils.PreferencesUtil

/**
 * Created by Kristina on 10/31/16.
 */

class PostsAdapter(private val mainActivity: MainActivity, private val swipeContainer: SwipeRefreshLayout?) : BasePostsAdapter(mainActivity) {

    private var callback: Callback? = null
    private var isLoading = false
    private var isMoreDataAvailable = true
    private var lastLoadedItemCreatedDate: Long = 0

    init {
        initRefreshLayout()
        setHasStableIds(true)
    }

    private fun initRefreshLayout() {
        if (swipeContainer != null) {
            this.swipeContainer.setOnRefreshListener { onRefreshAction() }
        }
    }

    private fun onRefreshAction() {
        if (activity.hasInternetConnection()) {
            loadFirstPage()
            cleanSelectedPostInformation()
        } else {
            swipeContainer!!.isRefreshing = false
            mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed)
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ItemType.ITEM.typeCode) {
            PostViewHolder(inflater.inflate(R.layout.post_item_list_view, parent, false),
                    createOnClickListener(), activity)
        } else {
            LoadViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
        }
    }

    private fun createOnClickListener(): PostViewHolder.OnClickListener {
        return object : PostViewHolder.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                if (callback != null) {
                    selectedPostPosition = position
                    callback!!.onItemClick(getItemByPosition(position), view)
                }
            }

            override fun onLikeClick(likeController: LikeController?, position: Int) {
                val post = getItemByPosition(position)
                likeController!!.handleLikeClickAction(activity, post)
            }

            override fun onAuthorClick(position: Int, view: View) {
                if (callback != null) {
                    callback!!.onAuthorClick(getItemByPosition(position).authorId, view)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading) {
            val mHandler = activity.getWindow().decorView.handler
            mHandler.post {
                //change adapter contents
                if (activity.hasInternetConnection()) {
                    isLoading = true
                    postList.add(Post(ItemType.LOAD))
                    notifyItemInserted(postList.size)
                    loadNext(lastLoadedItemCreatedDate - 1)
                } else {
                    mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed)
                }
            }


        }

        if (getItemViewType(position) != ItemType.LOAD.typeCode) {
            (holder as PostViewHolder).bindData(postList[position])
        }
    }

    private fun addList(list: List<Post>) {
        this.postList.addAll(list)
        notifyDataSetChanged()
        isLoading = false
    }

    fun loadFirstPage() {
        loadNext(0)
        PostManager.getInstance(mainActivity.applicationContext).clearNewPostsCounter()
    }

    private fun loadNext(nextItemCreatedDate: Long) {

        if (!PreferencesUtil.isPostWasLoadedAtLeastOnce(mainActivity) && !activity.hasInternetConnection()) {
            mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed)
            hideProgress()
            callback!!.onListLoadingFinished()
            return
        }

        val onPostsDataChangedListener = object : OnPostListChangedListener<Post> {
            override fun onListChanged(result: PostListResult) {
                lastLoadedItemCreatedDate = result.lastItemCreatedDate
                isMoreDataAvailable = result.isMoreDataAvailable
                val list = result.posts

                if (nextItemCreatedDate == 0L) {
                    postList.clear()
                    notifyDataSetChanged()
                    swipeContainer!!.isRefreshing = false
                }

                hideProgress()

                if (!list.isEmpty()) {
                    addList(list)

                    if (!PreferencesUtil.isPostWasLoadedAtLeastOnce(mainActivity)) {
                        PreferencesUtil.setPostWasLoadedAtLeastOnce(mainActivity, true)
                    }
                } else {
                    isLoading = false
                }

                callback!!.onListLoadingFinished()
            }

            override fun onCanceled(message: String) {
                callback!!.onCanceled(message)
            }
        }

        PostManager.getInstance(activity).getPostsList(onPostsDataChangedListener, nextItemCreatedDate)
    }

    private fun hideProgress() {
        if (!postList.isEmpty() && getItemViewType(postList.size - 1) == ItemType.LOAD.typeCode) {
            postList.removeAt(postList.size - 1)
            notifyItemRemoved(postList.size - 1)
        }
    }

    fun removeSelectedPost() {
        postList.removeAt(selectedPostPosition)
        notifyItemRemoved(selectedPostPosition)
    }

    override fun getItemId(position: Int): Long {
        return getItemByPosition(position).id.hashCode().toLong()
    }

    interface Callback {
        fun onItemClick(post: Post, view: View)
        fun onListLoadingFinished()
        fun onAuthorClick(authorId: String, view: View)
        fun onCanceled(message: String)
    }

    companion object {
        val TAG = PostsAdapter::class.java.simpleName
    }
}
