/*
 *  Copyright 2018 Rozdoum
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
package com.rozdoum.socialcomponents.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.FollowState
import com.rozdoum.socialcomponents.utils.LogUtil

class FollowButton : android.support.v7.widget.AppCompatButton {

    var state: Int = 0
        private set


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    fun setState(followState: FollowState) {
        when (followState) {
            FollowState.I_FOLLOW_USER, FollowState.FOLLOW_EACH_OTHER -> state = FOLLOWING_STATE
            FollowState.USER_FOLLOW_ME -> state = FOLLOW_BACK_STATE
            FollowState.NO_ONE_FOLLOW -> state = FOLLOW_STATE
            FollowState.MY_PROFILE -> state = INVISIBLE_STATE
        }

        updateButtonState()
        LogUtil.logDebug(TAG, "new state code: $state")
    }

    private fun init() {
        state = INVISIBLE_STATE
        updateButtonState()
    }

    fun updateButtonState() {
        isClickable = true

        when (state) {
            FOLLOW_STATE -> {
                visibility = View.VISIBLE
                setText(R.string.button_follow_title)
                background = ContextCompat.getDrawable(context, R.drawable.follow_button_dark_bg)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            FOLLOW_BACK_STATE -> {
                visibility = View.VISIBLE
                setText(R.string.button_follow_back_title)
                background = ContextCompat.getDrawable(context, R.drawable.follow_button_dark_bg)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            FOLLOWING_STATE -> {
                visibility = View.VISIBLE
                setText(R.string.button_following)
                background = ContextCompat.getDrawable(context, R.drawable.follow_button_light_bg)
                setTextColor(ContextCompat.getColor(context, R.color.primary_dark_text))
            }

            INVISIBLE_STATE -> {
                visibility = View.INVISIBLE
                isClickable = false
            }
        }
    }

    companion object {
        val TAG = FollowButton::class.java.simpleName

        val FOLLOW_STATE = 1
        val FOLLOW_BACK_STATE = 2
        val FOLLOWING_STATE = 3
        val INVISIBLE_STATE = -1
    }
}
