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

import android.content.Context
import android.graphics.Rect
import android.net.Uri

import com.rozdoum.socialcomponents.Constants

/**
 * Created by Kristina on 8/8/15.
 */
object ValidationUtil {
    private val IMAGE_TYPE = arrayOf("jpg", "png", "jpeg", "bmp", "jp2", "psd", "tif", "gif")

    fun isEmailValid(email: String): Boolean {
        val stricterFilterString = "[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}"
        return email.matches(stricterFilterString.toRegex())
    }

    fun isOnlyLatinLetters(text: String): Boolean {
        val regular = "[a-zA-Z]+"
        return text.matches(regular.toRegex())
    }

    fun isMonthValid(monthString: String?): Boolean {
        if (monthString != null) {
            if (monthString.length != 2) {
                return false
            }

            val month = Integer.valueOf(monthString)
            if (month >= 1 && month <= 12) {
                return true
            }
        }
        return false
    }

    fun isYearValid(monthString: String?): Boolean {
        if (monthString != null) {
            if (monthString.length == 2) {
                return true
            }
        }
        return false
    }

    fun isPostTitleValid(name: String): Boolean {
        return name.length <= Constants.Post.MAX_POST_TITLE_LENGTH
    }

    fun isNameValid(name: String): Boolean {
        return name.length <= Constants.Profile.MAX_NAME_LENGTH
    }

    fun isImage(uri: Uri, context: Context): Boolean {
        val mimeType = context.contentResolver.getType(uri)

        if (mimeType != null) {
            return mimeType.contains("image")
        } else {
            val filenameArray = uri.path!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val extension = filenameArray[filenameArray.size - 1]

            if (extension != null) {
                for (type in IMAGE_TYPE) {
                    if (type.toLowerCase() == extension.toLowerCase()) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun hasExtension(path: String): Boolean {
        var extension: String? = null
        val filenameArray = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (filenameArray.size > 1) {
            extension = filenameArray[filenameArray.size - 1]
        }

        return if (extension != null) {
            true
        } else false
    }

    fun containsInvalidSymbol(name: String): Boolean {
        return name.contains("@")
    }

    fun checkImageMinSize(rect: Rect): Boolean {
        return rect.height() >= Constants.Profile.MIN_AVATAR_SIZE && rect.width() >= Constants.Profile.MIN_AVATAR_SIZE
    }
}
