package com.rozdoum.socialcomponents

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import java.lang.Class
import java.lang.SafeVarargs

/**
 * Automatically generated from [com.bumptech.glide.annotation.GlideExtension] annotated classes.
 *
 * @see RequestOptions
 */
class GlideOptions : RequestOptions(), Cloneable {

    @CheckResult
    override fun sizeMultiplier(@FloatRange(from = 0.0, to = 1.0) arg0: Float): GlideOptions {
        return super.sizeMultiplier(arg0) as GlideOptions
    }

    @CheckResult
    override fun useUnlimitedSourceGeneratorsPool(flag: Boolean): GlideOptions {
        return super.useUnlimitedSourceGeneratorsPool(flag) as GlideOptions
    }

    @CheckResult
    override fun useAnimationPool(flag: Boolean): GlideOptions {
        return super.useAnimationPool(flag) as GlideOptions
    }

    @CheckResult
    override fun onlyRetrieveFromCache(flag: Boolean): GlideOptions {
        return super.onlyRetrieveFromCache(flag) as GlideOptions
    }

    @CheckResult
    override fun diskCacheStrategy(arg0: DiskCacheStrategy): GlideOptions {
        return super.diskCacheStrategy(arg0) as GlideOptions
    }

    @CheckResult
    override fun priority(arg0: Priority): GlideOptions {
        return super.priority(arg0) as GlideOptions
    }

    @CheckResult
    override fun placeholder(arg0: Drawable?): GlideOptions {
        return super.placeholder(arg0) as GlideOptions
    }

    @CheckResult
    override fun placeholder(@DrawableRes arg0: Int): GlideOptions {
        return super.placeholder(arg0) as GlideOptions
    }

    @CheckResult
    override fun fallback(arg0: Drawable?): GlideOptions {
        return super.fallback(arg0) as GlideOptions
    }

    @CheckResult
    override fun fallback(@DrawableRes arg0: Int): GlideOptions {
        return super.fallback(arg0) as GlideOptions
    }

    @CheckResult
    override fun error(arg0: Drawable?): GlideOptions {
        return super.error(arg0) as GlideOptions
    }

    @CheckResult
    override fun error(@DrawableRes arg0: Int): GlideOptions {
        return super.error(arg0) as GlideOptions
    }

    @CheckResult
    override fun theme(arg0: Resources.Theme?): GlideOptions {
        return super.theme(arg0) as GlideOptions
    }

    @CheckResult
    override fun skipMemoryCache(skip: Boolean): GlideOptions {
        return super.skipMemoryCache(skip) as GlideOptions
    }

    @CheckResult
    override fun override(width: Int, height: Int): GlideOptions {
        return super.override(width, height) as GlideOptions
    }

    @CheckResult
    override fun override(size: Int): GlideOptions {
        return super.override(size) as GlideOptions
    }

    @CheckResult
    override fun signature(arg0: Key): GlideOptions {
        return super.signature(arg0) as GlideOptions
    }

    @CheckResult
    override fun clone(): GlideOptions {
        return super.clone()
    }

    @CheckResult
    override fun <T> set(arg0: Option<T>, arg1: T): GlideOptions {
        return super.set(arg0, arg1) as GlideOptions
    }

    @CheckResult
    override fun decode(arg0: Class<*>): GlideOptions {
        return super.decode(arg0) as GlideOptions
    }

    @CheckResult
    override fun encodeFormat(arg0: Bitmap.CompressFormat): GlideOptions {
        return super.encodeFormat(arg0) as GlideOptions
    }

    @CheckResult
    override fun encodeQuality(@IntRange(from = 0, to = 100) arg0: Int): GlideOptions {
        return super.encodeQuality(arg0) as GlideOptions
    }

    @CheckResult
    override fun frame(@IntRange(from = 0) arg0: Long): GlideOptions {
        return super.frame(arg0) as GlideOptions
    }

    @CheckResult
    override fun format(arg0: DecodeFormat): GlideOptions {
        return super.format(arg0) as GlideOptions
    }

    @CheckResult
    override fun disallowHardwareConfig(): GlideOptions {
        return super.disallowHardwareConfig() as GlideOptions
    }

    @CheckResult
    override fun downsample(arg0: DownsampleStrategy): GlideOptions {
        return super.downsample(arg0) as GlideOptions
    }

    @CheckResult
    override fun timeout(@IntRange(from = 0) arg0: Int): GlideOptions {
        return super.timeout(arg0) as GlideOptions
    }

    @CheckResult
    override fun optionalCenterCrop(): GlideOptions {
        return super.optionalCenterCrop() as GlideOptions
    }

    @CheckResult
    override fun centerCrop(): GlideOptions {
        return super.centerCrop() as GlideOptions
    }

    @CheckResult
    override fun optionalFitCenter(): GlideOptions {
        return super.optionalFitCenter() as GlideOptions
    }

    @CheckResult
    override fun fitCenter(): GlideOptions {
        return super.fitCenter() as GlideOptions
    }

    @CheckResult
    override fun optionalCenterInside(): GlideOptions {
        return super.optionalCenterInside() as GlideOptions
    }

    @CheckResult
    override fun centerInside(): GlideOptions {
        return super.centerInside() as GlideOptions
    }

    @CheckResult
    override fun optionalCircleCrop(): GlideOptions {
        return super.optionalCircleCrop() as GlideOptions
    }

    @CheckResult
    override fun circleCrop(): GlideOptions {
        return super.circleCrop() as GlideOptions
    }

    @CheckResult
    override fun transform(arg0: Transformation<Bitmap>): GlideOptions {
        return super.transform(arg0) as GlideOptions
    }

    @SafeVarargs
    @CheckResult
    override fun transforms(vararg arg0: Transformation<Bitmap>): GlideOptions {
        return super.transforms(*arg0) as GlideOptions
    }

    @CheckResult
    override fun optionalTransform(arg0: Transformation<Bitmap>): GlideOptions {
        return super.optionalTransform(arg0) as GlideOptions
    }

    @CheckResult
    override fun <T> optionalTransform(arg0: Class<T>,
                                       arg1: Transformation<T>): GlideOptions {
        return super.optionalTransform(arg0, arg1) as GlideOptions
    }

    @CheckResult
    override fun <T> transform(arg0: Class<T>, arg1: Transformation<T>): GlideOptions {
        return super.transform(arg0, arg1) as GlideOptions
    }

    @CheckResult
    override fun dontTransform(): GlideOptions {
        return super.dontTransform() as GlideOptions
    }

    @CheckResult
    override fun dontAnimate(): GlideOptions {
        return super.dontAnimate() as GlideOptions
    }

    @CheckResult
    override fun apply(arg0: RequestOptions): GlideOptions {
        return super.apply(arg0) as GlideOptions
    }

    override fun lock(): GlideOptions {
        return super.lock() as GlideOptions
    }

    override fun autoClone(): GlideOptions {
        return super.autoClone() as GlideOptions
    }

    companion object {
        private var fitCenterTransform0: GlideOptions? = null

        private var centerInsideTransform1: GlideOptions? = null

        private var centerCropTransform2: GlideOptions? = null

        private var circleCropTransform3: GlideOptions? = null

        private var noTransformation4: GlideOptions? = null

        private var noAnimation5: GlideOptions? = null

        /**
         * @see RequestOptions.sizeMultiplierOf
         */
        @CheckResult
        override fun sizeMultiplierOf(@FloatRange(from = 0.0, to = 1.0) arg0: Float): GlideOptions {
            return GlideOptions().sizeMultiplier(arg0)
        }

        /**
         * @see RequestOptions.diskCacheStrategyOf
         */
        @CheckResult
        override fun diskCacheStrategyOf(arg0: DiskCacheStrategy): GlideOptions {
            return GlideOptions().diskCacheStrategy(arg0)
        }

        /**
         * @see RequestOptions.priorityOf
         */
        @CheckResult
        override fun priorityOf(arg0: Priority): GlideOptions {
            return GlideOptions().priority(arg0)
        }

        /**
         * @see RequestOptions.placeholderOf
         */
        @CheckResult
        override fun placeholderOf(arg0: Drawable?): GlideOptions {
            return GlideOptions().placeholder(arg0)
        }

        /**
         * @see RequestOptions.placeholderOf
         */
        @CheckResult
        override fun placeholderOf(@DrawableRes arg0: Int): GlideOptions {
            return GlideOptions().placeholder(arg0)
        }

        /**
         * @see RequestOptions.errorOf
         */
        @CheckResult
        override fun errorOf(arg0: Drawable?): GlideOptions {
            return GlideOptions().error(arg0)
        }

        /**
         * @see RequestOptions.errorOf
         */
        @CheckResult
        override fun errorOf(@DrawableRes arg0: Int): GlideOptions {
            return GlideOptions().error(arg0)
        }

        /**
         * @see RequestOptions.skipMemoryCacheOf
         */
        @CheckResult
        override fun skipMemoryCacheOf(skipMemoryCache: Boolean): GlideOptions {
            return GlideOptions().skipMemoryCache(skipMemoryCache)
        }

        /**
         * @see RequestOptions.overrideOf
         */
        @CheckResult
        override fun overrideOf(@IntRange(from = 0) arg0: Int,
                                @IntRange(from = 0) arg1: Int): GlideOptions {
            return GlideOptions().override(arg0, arg1)
        }

        /**
         * @see RequestOptions.overrideOf
         */
        @CheckResult
        override fun overrideOf(@IntRange(from = 0) arg0: Int): GlideOptions {
            return GlideOptions().override(arg0)
        }

        /**
         * @see RequestOptions.signatureOf
         */
        @CheckResult
        override fun signatureOf(arg0: Key): GlideOptions {
            return GlideOptions().signature(arg0)
        }

        /**
         * @see RequestOptions.fitCenterTransform
         */
        @CheckResult
        override fun fitCenterTransform(): GlideOptions {
            if (GlideOptions.fitCenterTransform0 == null) {
                GlideOptions.fitCenterTransform0 = GlideOptions().fitCenter().autoClone()
            }
            return GlideOptions.fitCenterTransform0
        }

        /**
         * @see RequestOptions.centerInsideTransform
         */
        @CheckResult
        override fun centerInsideTransform(): GlideOptions {
            if (GlideOptions.centerInsideTransform1 == null) {
                GlideOptions.centerInsideTransform1 = GlideOptions().centerInside().autoClone()
            }
            return GlideOptions.centerInsideTransform1
        }

        /**
         * @see RequestOptions.centerCropTransform
         */
        @CheckResult
        override fun centerCropTransform(): GlideOptions {
            if (GlideOptions.centerCropTransform2 == null) {
                GlideOptions.centerCropTransform2 = GlideOptions().centerCrop().autoClone()
            }
            return GlideOptions.centerCropTransform2
        }

        /**
         * @see RequestOptions.circleCropTransform
         */
        @CheckResult
        override fun circleCropTransform(): GlideOptions {
            if (GlideOptions.circleCropTransform3 == null) {
                GlideOptions.circleCropTransform3 = GlideOptions().circleCrop().autoClone()
            }
            return GlideOptions.circleCropTransform3
        }

        /**
         * @see RequestOptions.bitmapTransform
         */
        @CheckResult
        override fun bitmapTransform(arg0: Transformation<Bitmap>): GlideOptions {
            return GlideOptions().transform(arg0)
        }

        /**
         * @see RequestOptions.noTransformation
         */
        @CheckResult
        override fun noTransformation(): GlideOptions {
            if (GlideOptions.noTransformation4 == null) {
                GlideOptions.noTransformation4 = GlideOptions().dontTransform().autoClone()
            }
            return GlideOptions.noTransformation4
        }

        /**
         * @see RequestOptions.option
         */
        @CheckResult
        override fun <T> option(arg0: Option<T>, arg1: T): GlideOptions {
            return GlideOptions().set(arg0, arg1)
        }

        /**
         * @see RequestOptions.decodeTypeOf
         */
        @CheckResult
        override fun decodeTypeOf(arg0: Class<*>): GlideOptions {
            return GlideOptions().decode(arg0)
        }

        /**
         * @see RequestOptions.formatOf
         */
        @CheckResult
        override fun formatOf(arg0: DecodeFormat): GlideOptions {
            return GlideOptions().format(arg0)
        }

        /**
         * @see RequestOptions.frameOf
         */
        @CheckResult
        override fun frameOf(@IntRange(from = 0) arg0: Long): GlideOptions {
            return GlideOptions().frame(arg0)
        }

        /**
         * @see RequestOptions.downsampleOf
         */
        @CheckResult
        override fun downsampleOf(arg0: DownsampleStrategy): GlideOptions {
            return GlideOptions().downsample(arg0)
        }

        /**
         * @see RequestOptions.timeoutOf
         */
        @CheckResult
        override fun timeoutOf(@IntRange(from = 0) arg0: Int): GlideOptions {
            return GlideOptions().timeout(arg0)
        }

        /**
         * @see RequestOptions.encodeQualityOf
         */
        @CheckResult
        override fun encodeQualityOf(@IntRange(from = 0, to = 100) arg0: Int): GlideOptions {
            return GlideOptions().encodeQuality(arg0)
        }

        /**
         * @see RequestOptions.encodeFormatOf
         */
        @CheckResult
        override fun encodeFormatOf(arg0: Bitmap.CompressFormat): GlideOptions {
            return GlideOptions().encodeFormat(arg0)
        }

        /**
         * @see RequestOptions.noAnimation
         */
        @CheckResult
        override fun noAnimation(): GlideOptions {
            if (GlideOptions.noAnimation5 == null) {
                GlideOptions.noAnimation5 = GlideOptions().dontAnimate().autoClone()
            }
            return GlideOptions.noAnimation5
        }
    }
}
