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


package com.rozdoum.socialcomponents.main.search.posts

import android.content.Context

import com.rozdoum.socialcomponents.main.base.BasePresenter
import com.rozdoum.socialcomponents.managers.PostManager
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.LogUtil

/**
 * Created by Alexey on 08.06.18.
 */
class SearchPostsPresenter(private val context: Context) : BasePresenter<SearchPostsView>(context) {
    private val postManager: PostManager

    init {
        postManager = PostManager.getInstance(context)
    }

    @JvmOverloads
    fun search(searchText: String = "") {
        if (checkInternetConnection()) {
            if (searchText.isEmpty()) {
                filterByLikes()
            } else {
                ifViewAttached { it.showLocalProgress() }
                postManager.searchByTitle(searchText, OnDataChangedListener<Post> { this.handleSearchResult(it) })
            }

        } else {
            ifViewAttached { it.hideLocalProgress() }
        }
    }

    private fun filterByLikes() {
        if (checkInternetConnection()) {
            ifViewAttached { it.showLocalProgress() }
            postManager.filterByLikes(LIMIT_POSTS_FILTERED_BY_LIKES, OnDataChangedListener<Post> { this.handleSearchResult(it) })
        } else {
            ifViewAttached { it.hideLocalProgress() }
        }
    }

    private fun handleSearchResult(list: List<Post>) {
        ifViewAttached { view ->
            view.hideLocalProgress()
            view.onSearchResultsReady(list)

            if (list.isEmpty()) {
                view.showEmptyListLayout()
            }

            LogUtil.logDebug(TAG, "found items count: " + list.size)
        }
    }

    companion object {
        val LIMIT_POSTS_FILTERED_BY_LIKES = 10
    }
}
