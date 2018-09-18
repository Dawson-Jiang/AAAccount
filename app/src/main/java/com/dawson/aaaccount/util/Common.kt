package com.dawson.aaaccount.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v4.content.PermissionChecker
import android.widget.Toast

import com.dawson.aaaccount.R

import java.math.BigDecimal

import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils

object Common {

    /**
     * 缩略图尺寸
     */
    val THUMB_SIZE = 160

    /**
     * 显示错误消息
     *
     * @param msgId 默认显示的信息
     * @param flag  0 toast方式显示 1 alert方式显示
     */
    fun showErrorInfo(context: Activity, errorCode: Int,
                      msgId: Int, flag: Int) {
        showErrorInfo(context, errorCode, context.resources.getString(msgId), flag)
    }

    /**
     * 显示错误消息 toast方式显示
     *
     * @param msgId 默认显示的信息
     */
    fun showErrorInfo(context: Activity, errorCode: Int,
                      msgId: Int) {
        showErrorInfo(context, errorCode, context.resources.getString(msgId), 0)
    }

    /**
     * 显示错误消息 toast方式显示
     *
     * @param msg 默认显示的信息
     */
    fun showErrorInfo(context: Activity, errorCode: Int,
                      msg: String?) {
        showErrorInfo(context, errorCode, msg, 0)
    }

    /**
     * 显示错误消息
     *
     * @param msg 默认显示的信息
     * @param flag  0 toast方式显示 1 alert方式显示
     */
    fun showErrorInfo(context: Activity, errorCode: Int,
                      msg: String?, flag: Int) {
        val errorMsg: String
        val errorMsgId = R.string.operate_fail
        when (errorCode) {
            ErrorCode.NET_TIMEOUT -> errorMsg = "网络异常"
            ErrorCode.EMAIL_OR_PWD_WRONG -> errorMsg = "用户名或密码错误"
            ErrorCode.EMAIL_EXIST -> errorMsg = "邮箱已存在"
            ErrorCode.TOKEN_OVERDUE -> errorMsg = "登录过期"
            ErrorCode.FAMILY_OR_PWD_WRONG -> errorMsg = "家庭不存在或者密码错误"
            ErrorCode.FAMILY_MEMBER_EXIST -> errorMsg = "重复加入"
            ErrorCode.TOKEN_OVERDUE -> return
            ErrorCode.SYS_ERROR -> errorMsg = "系统错误"
            ErrorCode.FAIL -> errorMsg = if (TextUtils.isEmpty(msg)) "系统错误" else msg!!
            else -> errorMsg = if (TextUtils.isEmpty(msg)) "系统错误$errorCode"
            else ("$msg:$errorCode")
        }

        if (flag == 0)
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        else if (flag == 1) {
            AlertDialogHelper.showOKAlertDialog(context, errorMsgId)
        }
    }

    /**
     * 将double 保留指定小数位
     *
     * @param value 原数据
     * @param len   小数位数
     * @return
     */
    fun convertDouble(value: Double, len: Int): Double {
        val bd = BigDecimal(value)
        return bd.setScale(len, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        if (SDK_INT < 23) return true
        return permissions
                .map { PermissionChecker.checkSelfPermission(context, it) }
                .none { PermissionChecker.PERMISSION_GRANTED != it }
    }

    fun startAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }
}
