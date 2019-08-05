package com.dawson.aaaccount.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * 扩展方法集合
 * Created by Dawson on 2017/11/28.
 */
fun Date.format(format: String): String {
    val sdf = SimpleDateFormat(format)
    return sdf.format(this)
}

fun Date.format(): String {
    return format("yyyy.MM.dd HH:mm:ss")
}


fun Date.getWeekDay(): String {
    val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    val cal = Calendar.getInstance()
    cal.time = this
    var w = cal.get(Calendar.DAY_OF_WEEK) - 1
    if (w < 0) w = 0
    return weekDays[w]
}