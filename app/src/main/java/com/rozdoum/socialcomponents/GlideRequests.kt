package com.rozdoum.socialcomponents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.CheckResult
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.manager.Lifecycle
import com.bumptech.glide.manager.RequestManagerTreeNode
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.lang.Class
import java.lang.Deprecated
import java.net.URL

/**
 * Includes all additions from methods in [com.bumptech.glide.annotation.GlideExtension]s
 * annotated with [com.bumptech.glide.annotation.GlideType]
 *
 *
 * Generated code, do not modify
 */
class GlideRequests(glide: Glide, lifecycle: Lifecycle,
                    treeNode: RequestManagerTreeNode, context: Context) : RequestManager(glide, lifecycle, treeNode, context) {

    @CheckResult
    override fun <ResourceType> `as`(resourceClass: Class<ResourceType>): GlideRequest<ResourceType> {
        return GlideRequest(glide, this, resourceClass, context)
    }

    override fun applyDefaultRequestOptions(arg0: RequestOptions): GlideRequests {
        return super.applyDefaultRequestOptions(arg0) as GlideRequests
    }

    override fun setDefaultRequestOptions(arg0: RequestOptions): GlideRequests {
        return super.setDefaultRequestOptions(arg0) as GlideRequests
    }

    @CheckResult
    override fun asBitmap(): GlideRequest<Bitmap> {
        return super.asBitmap() as GlideRequest<Bitmap>
    }

    @CheckResult
    override fun asGif(): GlideRequest<GifDrawable> {
        return super.asGif() as GlideRequest<GifDrawable>
    }

    @CheckResult
    override fun asDrawable(): GlideRequest<Drawable> {
        return super.asDrawable() as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: Bitmap?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: Drawable?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: String?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: Uri?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: File?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: Int?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @Deprecated("")
    @CheckResult
    override fun load(arg0: URL?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: ByteArray?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun load(arg0: Any?): GlideRequest<Drawable> {
        return super.load(arg0) as GlideRequest<Drawable>
    }

    @CheckResult
    override fun downloadOnly(): GlideRequest<File> {
        return super.downloadOnly() as GlideRequest<File>
    }

    @CheckResult
    override fun download(arg0: Any?): GlideRequest<File> {
        return super.download(arg0) as GlideRequest<File>
    }

    @CheckResult
    override fun asFile(): GlideRequest<File> {
        return super.asFile() as GlideRequest<File>
    }

    override fun setRequestOptions(toSet: RequestOptions) {
        if (toSet is com.rozdoum.socialcomponents.utils.GlideOptions) {
            super.setRequestOptions(toSet)
        } else {
            super.setRequestOptions(com.rozdoum.socialcomponents.utils.GlideOptions().apply(toSet))
        }
    }
}
