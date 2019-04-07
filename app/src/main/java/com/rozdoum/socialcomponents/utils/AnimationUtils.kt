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

package com.rozdoum.socialcomponents.utils

import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AlphaAnimation

object AnimationUtils {

    val DEFAULT_DELAY = 0
    val SHORT_DURATION = 200
    val ALPHA_SHORT_DURATION = 400

    /**
     * Reduces the X & Y
     *
     * @param v the view to be scaled
     *
     * @return the ViewPropertyAnimation to manage the animation
     */
    fun hideViewByScale(v: View): ViewPropertyAnimator {

        val propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY.toLong()).setDuration(SHORT_DURATION.toLong())
                .scaleX(0f).scaleY(0f)

        return propertyAnimator
    }

    fun isViewHiddenByScale(v: View): Boolean {
        return v.scaleX == 0f && v.scaleY == 0f
    }

    /**
     * Shows a view by scaling
     *
     * @param v the view to be scaled
     *
     * @return the ViewPropertyAnimation to manage the animation
     */
    fun showViewByScale(v: View): ViewPropertyAnimator {

        val propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY.toLong())
                .scaleX(1f).scaleY(1f)

        return propertyAnimator
    }

    /**
     * Shows a view by scaling
     *
     * @param v the view to be scaled
     *
     * @return the ViewPropertyAnimation to manage the animation
     */
    fun showViewByScaleWithoutDelay(v: View): ViewPropertyAnimator {

        val propertyAnimator = v.animate()
                .scaleX(1f).scaleY(1f)

        return propertyAnimator
    }

    fun hideViewByScaleAndVisibility(v: View): ViewPropertyAnimator {

        val propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY.toLong()).setDuration(SHORT_DURATION.toLong())
                .scaleX(0f).scaleY(0f).withEndAction { v.visibility = View.GONE }

        return propertyAnimator
    }

    fun hideViewByAlpha(v: View): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = ALPHA_SHORT_DURATION.toLong()
        v.animation = alphaAnimation
        return alphaAnimation
    }

    fun showViewByScaleAndVisibility(v: View): ViewPropertyAnimator {
        v.visibility = View.VISIBLE

        val propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY.toLong())
                .scaleX(1f).scaleY(1f)

        return propertyAnimator
    }
}
