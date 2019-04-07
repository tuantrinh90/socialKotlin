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

package com.rozdoum.socialcomponents.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.rozdoum.socialcomponents.GlideRequests
import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.enums.UploadImagePrefix

import java.util.Date


object ImageUtil {

    val TAG = ImageUtil::class.java.simpleName

    fun generateImageTitle(prefix: UploadImagePrefix, parentId: String?): String {
        return if (parentId != null) {
            prefix.toString() + parentId
        } else prefix.toString() + Date().time

    }

    fun loadImage(glideRequests: GlideRequests, url: String, imageView: ImageView, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL) {
        glideRequests.load(url)
                .diskCacheStrategy(diskCacheStrategy)
                .error(R.drawable.ic_stub)
                .into(imageView)
    }

    fun loadImage(glideRequests: GlideRequests, url: String, imageView: ImageView,
                  listener: RequestListener<Drawable>) {
        glideRequests.load(url)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView)
    }

    fun loadImageCenterCrop(glideRequests: GlideRequests, url: String, imageView: ImageView,
                            width: Int, height: Int) {
        glideRequests.load(url)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .into(imageView)
    }

    fun loadImageCenterCrop(glideRequests: GlideRequests, url: String, imageView: ImageView,
                            width: Int, height: Int, listener: RequestListener<Drawable>) {
        glideRequests.load(url)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView)
    }

    fun loadImageCenterCrop(glideRequests: GlideRequests, url: String, imageView: ImageView,
                            listener: RequestListener<Drawable>) {
        glideRequests.load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView)
    }


    fun loadBitmap(glideRequests: GlideRequests, url: String, width: Int, height: Int): Bitmap? {
        try {
            return glideRequests.asBitmap()
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .submit(width, height)
                    .get()
        } catch (e: Exception) {
            LogUtil.logError(TAG, "getBitmapfromUrl", e)
            return null
        }

    }

    fun loadImageWithSimpleTarget(glideRequests: GlideRequests, url: String, simpleTarget: SimpleTarget<Bitmap>) {
        glideRequests.asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(simpleTarget)
    }

    fun loadLocalImage(glideRequests: GlideRequests, uri: Uri, imageView: ImageView,
                       listener: RequestListener<Drawable>) {
        glideRequests.load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .listener(listener)
                .into(imageView)
    }
}
