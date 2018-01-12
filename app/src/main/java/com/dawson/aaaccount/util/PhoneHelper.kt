package com.dawson.aaaccount.util

import android.os.Build

/**
 * 手机基本信息
 * Created by Dawson on 2017/8/18.
 */

object PhoneHelper {
    /**
     * 获取机型
     *
     * @return
     */
    val phoneType: String
        get() {
            val type = Build.BRAND + "-" + Build.MODEL + "-" + Build.PRODUCT
            DLog.i("phone_type", type)
            return type
        }
}
