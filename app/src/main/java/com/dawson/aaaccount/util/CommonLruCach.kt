package com.dawson.aaaccount.util

import com.dawson.aaaccount.bean.Family
import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.dawson.aaaccount.bean.ConsumptionCategory


/**
 * 内存缓存
 * Created by Dawson on 2017/12/2.
 */
object CommonLruCach {
    val categories = mutableListOf<ConsumptionCategory>()
    val families = mutableListOf<Family>()
}