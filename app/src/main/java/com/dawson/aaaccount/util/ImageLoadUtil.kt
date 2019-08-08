package com.dawson.aaaccount.util

import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.dawson.aaaccount.R

/**
 * 加载图片
 * Created by Dawson on 2017/7/28.
 */
object ImageLoadUtil {
    fun loadImage(url: String?, view: ImageView) {
        GlideWrapper.with(view).load(url).centerCrop().placeholder(R.drawable.no_image).transition(DrawableTransitionOptions.withCrossFade()).error(R.drawable.no_image).into(view)
    }

    fun loadCircleImage(url: String?, view: ImageView) {
        GlideWrapper.with(view).load(url).centerCrop().placeholder(R.drawable.no_image).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).error(R.drawable.no_image).into(view)
    }
}

@GlideModule(glideName = "GlideWrapper")
class MyAppGlideModule : AppGlideModule()
