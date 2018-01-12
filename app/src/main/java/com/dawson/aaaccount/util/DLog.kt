package com.dawson.aaaccount.util


import android.util.Log
import com.dawson.aaaccount.AAAccountApplication
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.dao.bean.DBSystemLog
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日志记录和上传
 * Created by Dawson on 2017/7/13.
 */

object DLog {
    var isDebug = AAAccountApplication.DEBUG
    @Synchronized
    fun v(tag: String, msg: String) {
        if (!isDebug) {
            return
        }
        Log.v(tag, msg)
    }

    @Synchronized
    fun e(tag: String, msg: String) {
        if (!isDebug) {
            return
        }
        Log.e(tag, msg)
    }

    /**
     * 记录日志 默认类别log

     * @param tag
     * *
     * @param msg
     */
    @Synchronized
    fun r(tag: String, msg: String) {
        r(CATEGORY_LOG, tag, msg)
    }

    val CATEGORY_LOG = "log"
    val CATEGORY_EXCEPTION = "exception"

    /**
     * 记录日志

     * @param category 类别  每个类别会单独存放文件
     * *
     * @param tag
     * *
     * @param msg
     */
    @Synchronized
    fun r(category: String, tag: String, msg: String?) {
        if (isDebug) {
            if (CATEGORY_EXCEPTION == category)
                Log.e(tag, msg)
            else
                Log.i(tag, msg)
            return
        }
        //记录到文件
//        val date = Date(System.currentTimeMillis())
//        val msgBuilder = StringBuilder()
//        msgBuilder.append(date.format("HH:mm:ss"))
//        msgBuilder.append(" ").append(tag).append(" : ")
//        msgBuilder.append(msg).append("\r\n")
//        val fileName = "${category}_${date.format("yyyyMMdd")}.log"//文件名规则：类别_日期.log
//        FilePathConstants.recordLog(msgBuilder.toString(), fileName)

        //记录到数据库
       val syslog= DBSystemLog()
        syslog.cotent=msg
        syslog.createTime=Date(System.currentTimeMillis())
        syslog.title=tag
        GreenDaoUtil.daoSession?.dbSystemLogDao?.insertInTx(syslog)
    }

    /**
     * 记录日志

     * @param tag
     * *
     * @param ex
     */
    @Synchronized
    fun error(tag: String, ex: Throwable) {
        r(CATEGORY_EXCEPTION, tag, getThrowableMessage(ex))
    }

    @Synchronized private fun getThrowableMessage(ex: Throwable): String {
        val errorSB = StringBuilder()
        errorSB.append(ex.toString()).append("\n")
        val stacks = ex.stackTrace
        if (stacks != null && stacks.isNotEmpty()) {
            for (stack in stacks) {
                errorSB.append(stack.toString()).append("\n")
            }
        }

        val cause = ex.cause
        if (cause != null) {
            errorSB.append("Caused by: ")
            errorSB.append(getThrowableMessage(cause))
        }
        return errorSB.toString()
    }

    @Synchronized
    fun d(tag: String, msg: String) {
        if (!isDebug) {
            return
        }
        Log.d(tag, msg)
    }

    @Synchronized
    fun i(tag: String, msg: String) {
        if (!isDebug) {
            return
        }
        Log.i(tag, msg)
    }
}
