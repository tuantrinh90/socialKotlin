package com.rozdoum.socialcomponents

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.FragmentActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import java.io.File
import java.lang.Deprecated

/**
 * The entry point for interacting with Glide for Applications
 *
 *
 * Includes all generated APIs from all
 * [com.bumptech.glide.annotation.GlideExtension]s in source and dependent libraries.
 *
 *
 * This class is generated and should not be modified
 * @see Glide
 */
object GlideApp {

    /**
     * @see Glide.getPhotoCacheDir
     */
    fun getPhotoCacheDir(arg0: Context): File? {
        return Glide.getPhotoCacheDir(arg0)
    }

    /**
     * @see Glide.getPhotoCacheDir
     */
    fun getPhotoCacheDir(arg0: Context, arg1: String): File? {
        return Glide.getPhotoCacheDir(arg0, arg1)
    }

    /**
     * @see Glide.get
     */
    operator fun get(arg0: Context): Glide {
        return Glide.get(arg0)
    }

    /**
     * @see Glide.init
     */
    @Deprecated("")
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun init(glide: Glide) {
        Glide.init(glide)
    }

    /**
     * @see Glide.init
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun init(arg0: Context, arg1: GlideBuilder) {
        Glide.init(arg0, arg1)
    }

    /**
     * @see Glide.tearDown
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun tearDown() {
        Glide.tearDown()
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: Context): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: Activity): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: FragmentActivity): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: Fragment): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: android.support.v4.app.Fragment): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }

    /**
     * @see Glide.with
     */
    fun with(arg0: View): GlideRequests {
        return Glide.with(arg0) as GlideRequests
    }
}
