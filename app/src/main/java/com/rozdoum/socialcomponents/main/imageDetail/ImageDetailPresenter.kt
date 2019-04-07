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

package com.rozdoum.socialcomponents.main.imageDetail

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

import com.rozdoum.socialcomponents.main.base.BasePresenter

/**
 * Created by Alexey on 03.05.18.
 */

internal class ImageDetailPresenter(context: Context) : BasePresenter<ImageDetailView>(context) {

    fun calcMaxImageSide(): Int {
        val displaymetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displaymetrics)

        val width = displaymetrics.widthPixels
        val height = displaymetrics.heightPixels

        return if (width > height) width else height
    }
}
