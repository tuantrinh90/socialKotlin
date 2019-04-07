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
import android.os.Environment

import java.io.File
import java.io.IOException

/**
 * Created by Kristina on 7/23/14.
 */
object ImagesDir {

    private val TEMP_IMAGES_PATH = "images/temp"
    private var imagesTempDir: File? = null

    fun getTempImagesDir(context: Context): File? {
        if (imagesTempDir == null) {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                imagesTempDir = context.getExternalFilesDir(TEMP_IMAGES_PATH)
            } else {
                imagesTempDir = context.cacheDir
            }
        }

        if (imagesTempDir != null && !imagesTempDir!!.exists()) {
            imagesTempDir!!.mkdirs()
        }

        return imagesTempDir
    }

    fun isTempImagesDir(path: String, context: Context): Boolean {
        try {
            return path.startsWith(getTempImagesDir(context)!!.canonicalPath)
        } catch (e: IOException) {
            LogUtil.logError("isTempImagesDir", "Failed to get temp images folder", e)
        }

        return false
    }

    fun cleanDirs(file: File) {
        //to end the recursive loop
        if (!file.exists())
            return

        //if directory, go inside and call recursively
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                //call recursively
                cleanDirs(f)
            }
        }
        //call delete to delete files and empty directory
        file.delete()

    }

}
