package com.rozdoum.socialcomponents

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.lang.Class
import java.lang.Deprecated
import java.lang.SafeVarargs
import java.net.URL

/**
 * Contains all public methods from [<], all options from
 * [RequestOptions] and all generated options from
 * [com.bumptech.glide.annotation.GlideOption] in annotated methods in
 * [com.bumptech.glide.annotation.GlideExtension] annotated classes.
 *
 *
 * Generated code, do not modify.
 *
 * @see RequestBuilder<TranscodeType>
 *
 * @see RequestOptions
</TranscodeType> */
class GlideRequest<TranscodeType> : RequestBuilder<TranscodeType>, Cloneable {
    internal constructor(transcodeClass: Class<TranscodeType>, other: RequestBuilder<*>) : super(transcodeClass, other) {}

    internal constructor(glide: Glide, requestManager: RequestManager,
                         transcodeClass: Class<TranscodeType>, context: Context) : super(glide, requestManager, transcodeClass, context) {
    }

    @CheckResult
    override fun getDownloadOnlyRequest(): GlideRequest<File> {
        return GlideRequest(File::class.java, this).apply(RequestBuilder.DOWNLOAD_ONLY_OPTIONS)
    }

    /**
     * @see GlideOptions.sizeMultiplier
     */
    @CheckResult
    fun sizeMultiplier(@FloatRange(from = 0.0, to = 1.0) arg0: Float): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).sizeMultiplier(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).sizeMultiplier(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.useUnlimitedSourceGeneratorsPool
     */
    @CheckResult
    fun useUnlimitedSourceGeneratorsPool(flag: Boolean): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).useUnlimitedSourceGeneratorsPool(flag)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).useUnlimitedSourceGeneratorsPool(flag)
        }
        return this
    }

    /**
     * @see GlideOptions.useAnimationPool
     */
    @CheckResult
    fun useAnimationPool(flag: Boolean): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).useAnimationPool(flag)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).useAnimationPool(flag)
        }
        return this
    }

    /**
     * @see GlideOptions.onlyRetrieveFromCache
     */
    @CheckResult
    fun onlyRetrieveFromCache(flag: Boolean): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).onlyRetrieveFromCache(flag)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).onlyRetrieveFromCache(flag)
        }
        return this
    }

    /**
     * @see GlideOptions.diskCacheStrategy
     */
    @CheckResult
    fun diskCacheStrategy(arg0: DiskCacheStrategy): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).diskCacheStrategy(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).diskCacheStrategy(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.priority
     */
    @CheckResult
    fun priority(arg0: Priority): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).priority(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).priority(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.placeholder
     */
    @CheckResult
    fun placeholder(arg0: Drawable?): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).placeholder(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).placeholder(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.placeholder
     */
    @CheckResult
    fun placeholder(@DrawableRes arg0: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).placeholder(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).placeholder(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.fallback
     */
    @CheckResult
    fun fallback(arg0: Drawable?): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).fallback(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).fallback(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.fallback
     */
    @CheckResult
    fun fallback(@DrawableRes arg0: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).fallback(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).fallback(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.error
     */
    @CheckResult
    fun error(arg0: Drawable?): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).error(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).error(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.error
     */
    @CheckResult
    fun error(@DrawableRes arg0: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).error(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).error(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.theme
     */
    @CheckResult
    fun theme(arg0: Resources.Theme?): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).theme(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).theme(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.skipMemoryCache
     */
    @CheckResult
    fun skipMemoryCache(skip: Boolean): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).skipMemoryCache(skip)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).skipMemoryCache(skip)
        }
        return this
    }

    /**
     * @see GlideOptions.override
     */
    @CheckResult
    fun override(width: Int, height: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).override(width, height)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).override(width, height)
        }
        return this
    }

    /**
     * @see GlideOptions.override
     */
    @CheckResult
    fun override(size: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).override(size)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).override(size)
        }
        return this
    }

    /**
     * @see GlideOptions.signature
     */
    @CheckResult
    fun signature(arg0: Key): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).signature(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).signature(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.set
     */
    @CheckResult
    operator fun <T> set(arg0: Option<T>, arg1: T): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).set(arg0, arg1)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).set(arg0, arg1)
        }
        return this
    }

    /**
     * @see GlideOptions.decode
     */
    @CheckResult
    fun decode(arg0: Class<*>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).decode(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).decode(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.encodeFormat
     */
    @CheckResult
    fun encodeFormat(arg0: Bitmap.CompressFormat): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).encodeFormat(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).encodeFormat(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.encodeQuality
     */
    @CheckResult
    fun encodeQuality(@IntRange(from = 0, to = 100) arg0: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).encodeQuality(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).encodeQuality(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.frame
     */
    @CheckResult
    fun frame(@IntRange(from = 0) arg0: Long): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).frame(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).frame(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.format
     */
    @CheckResult
    fun format(arg0: DecodeFormat): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).format(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).format(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.disallowHardwareConfig
     */
    @CheckResult
    fun disallowHardwareConfig(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).disallowHardwareConfig()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).disallowHardwareConfig()
        }
        return this
    }

    /**
     * @see GlideOptions.downsample
     */
    @CheckResult
    fun downsample(arg0: DownsampleStrategy): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).downsample(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).downsample(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.timeout
     */
    @CheckResult
    fun timeout(@IntRange(from = 0) arg0: Int): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).timeout(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).timeout(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.optionalCenterCrop
     */
    @CheckResult
    fun optionalCenterCrop(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalCenterCrop()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalCenterCrop()
        }
        return this
    }

    /**
     * @see GlideOptions.centerCrop
     */
    @CheckResult
    fun centerCrop(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).centerCrop()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).centerCrop()
        }
        return this
    }

    /**
     * @see GlideOptions.optionalFitCenter
     */
    @CheckResult
    fun optionalFitCenter(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalFitCenter()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalFitCenter()
        }
        return this
    }

    /**
     * @see GlideOptions.fitCenter
     */
    @CheckResult
    fun fitCenter(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).fitCenter()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).fitCenter()
        }
        return this
    }

    /**
     * @see GlideOptions.optionalCenterInside
     */
    @CheckResult
    fun optionalCenterInside(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalCenterInside()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalCenterInside()
        }
        return this
    }

    /**
     * @see GlideOptions.centerInside
     */
    @CheckResult
    fun centerInside(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).centerInside()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).centerInside()
        }
        return this
    }

    /**
     * @see GlideOptions.optionalCircleCrop
     */
    @CheckResult
    fun optionalCircleCrop(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalCircleCrop()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalCircleCrop()
        }
        return this
    }

    /**
     * @see GlideOptions.circleCrop
     */
    @CheckResult
    fun circleCrop(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).circleCrop()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).circleCrop()
        }
        return this
    }

    /**
     * @see GlideOptions.transform
     */
    @CheckResult
    fun transform(arg0: Transformation<Bitmap>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).transform(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).transform(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.transforms
     */
    @CheckResult
    fun transforms(vararg arg0: Transformation<Bitmap>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).transforms(*arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).transforms(*arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.optionalTransform
     */
    @CheckResult
    fun optionalTransform(arg0: Transformation<Bitmap>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalTransform(arg0)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalTransform(arg0)
        }
        return this
    }

    /**
     * @see GlideOptions.optionalTransform
     */
    @CheckResult
    fun <T> optionalTransform(arg0: Class<T>,
                              arg1: Transformation<T>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).optionalTransform(arg0, arg1)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).optionalTransform(arg0, arg1)
        }
        return this
    }

    /**
     * @see GlideOptions.transform
     */
    @CheckResult
    fun <T> transform(arg0: Class<T>,
                      arg1: Transformation<T>): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).transform(arg0, arg1)
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).transform(arg0, arg1)
        }
        return this
    }

    /**
     * @see GlideOptions.dontTransform
     */
    @CheckResult
    fun dontTransform(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).dontTransform()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).dontTransform()
        }
        return this
    }

    /**
     * @see GlideOptions.dontAnimate
     */
    @CheckResult
    fun dontAnimate(): GlideRequest<TranscodeType> {
        if (mutableOptions is GlideOptions) {
            this.requestOptions = (mutableOptions as GlideOptions).dontAnimate()
        } else {
            this.requestOptions = GlideOptions().apply(this.requestOptions).dontAnimate()
        }
        return this
    }

    @CheckResult
    override fun apply(arg0: RequestOptions): GlideRequest<TranscodeType> {
        return super.apply(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun transition(arg0: TransitionOptions<*, in TranscodeType>): GlideRequest<TranscodeType> {
        return super.transition(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun listener(arg0: RequestListener<TranscodeType>?): GlideRequest<TranscodeType> {
        return super.listener(arg0) as GlideRequest<TranscodeType>
    }

    override fun error(arg0: RequestBuilder<TranscodeType>?): GlideRequest<TranscodeType> {
        return super.error(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun thumbnail(arg0: RequestBuilder<TranscodeType>?): GlideRequest<TranscodeType> {
        return super.thumbnail(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    @SafeVarargs
    override fun thumbnail(vararg arg0: RequestBuilder<TranscodeType>): GlideRequest<TranscodeType> {
        return super.thumbnail(*arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun thumbnail(sizeMultiplier: Float): GlideRequest<TranscodeType> {
        return super.thumbnail(sizeMultiplier) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: Any?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: Bitmap?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: Drawable?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: String?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: Uri?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: File?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(@RawRes @DrawableRes arg0: Int?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @Deprecated("")
    @CheckResult
    override fun load(arg0: URL?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun load(arg0: ByteArray?): GlideRequest<TranscodeType> {
        return super.load(arg0) as GlideRequest<TranscodeType>
    }

    @CheckResult
    override fun clone(): GlideRequest<TranscodeType> {
        return super.clone()
    }
}
