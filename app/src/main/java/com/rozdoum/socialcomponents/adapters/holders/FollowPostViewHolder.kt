package com.rozdoum.socialcomponents.adapters.holders

import android.view.View

import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.managers.listeners.OnPostChangedListener
import com.rozdoum.socialcomponents.model.FollowingPost
import com.rozdoum.socialcomponents.model.Post
import com.rozdoum.socialcomponents.utils.LogUtil

/**
 * Created by Alexey on 22.05.18.
 */
class FollowPostViewHolder : PostViewHolder {


    constructor(view: View, onClickListener: PostViewHolder.OnClickListener, activity: BaseActivity<*, *>) : super(view, onClickListener, activity) {}

    constructor(view: View, onClickListener: PostViewHolder.OnClickListener, activity: BaseActivity<*, *>, isAuthorNeeded: Boolean) : super(view, onClickListener, activity, isAuthorNeeded) {}

    fun bindData(followingPost: FollowingPost) {
        postManager.getSinglePostValue(followingPost.postId, object : OnPostChangedListener {
            override fun onObjectChanged(obj: Post) {
                bindData(obj)
            }

            override fun onError(errorText: String) {
                LogUtil.logError(PostViewHolder.TAG, "bindData", RuntimeException(errorText))
            }
        })
    }

}
