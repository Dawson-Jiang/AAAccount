package com.dawson.aaaccount.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.dawson.aaaccount.R
import jp.wasabeef.glide.transformations.BlurTransformation
import java.security.MessageDigest

/**
 * 加载图片
 * Created by Dawson on 2017/7/28.
 */
object ImageLoadUtil {
    fun loadImage(url: String?, view: ImageView) {
        GlideWrapper.with(view).load(url).centerCrop().placeholder(R.drawable.no_image).transition(DrawableTransitionOptions.withCrossFade()).error(R.drawable.no_image).into(view)
    }

    fun loadBlurImage(context: Activity, rsId: Int?, view: ImageView) {
        GlideWrapper.with(view).load(rsId)
//                .centerCrop()
                .placeholder(R.drawable.no_image)
                .transform(BlurTransformation( 5))
                .error(R.drawable.no_image).into(view)
    }

    fun loadCircleImage(url: String?, view: ImageView) {
        GlideWrapper.with(view).load(url).centerCrop().placeholder(R.drawable.no_image).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).error(R.drawable.no_image).into(view)
    }
}

@GlideModule(glideName = "GlideWrapper")
class MyAppGlideModule : AppGlideModule()


//class GlideBlurformation(context: Context, radius: Int) : BlurTransformation(context, radius) {
//    override fun transform(context: Context, resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
//        return transform(resource, outWidth, outHeight)
//    }
//
//    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
//
//    }
//}
