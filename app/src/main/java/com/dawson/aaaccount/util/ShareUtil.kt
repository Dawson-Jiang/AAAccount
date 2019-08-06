package com.dawson.aaaccount.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast

import com.dawson.aaaccount.BuildConfig
import com.dawson.aaaccount.R
import com.dawson.aaaccount.activity.MainActivity
import kotlinx.android.synthetic.main.activity_about.*


/**
 * 专用于分享的工具类
 *
 *
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-20  11:56
 */

object ShareUtil {
    fun shareImage(context: Context, uri: Uri, title: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/jpeg"
        context.startActivity(Intent.createChooser(shareIntent, title))
    }


    fun shareApp(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, "AA账单")
        intent.putExtra(Intent.EXTRA_SUBJECT, "欢迎下载使用AA账单")
        //        intent.putExtra(Intent.EXTRA_TEXT, "个人和多人（团队、合伙人或合租人等）记账");
        val f = "欢迎下载使用AA账单"
        if (BuildConfig.FLAVOR == "baidu") {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        } else if (BuildConfig.FLAVOR == "xiaomi") {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        } else if (BuildConfig.FLAVOR == "tencent") {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        } else if (BuildConfig.FLAVOR == "huawei") {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        } else if (BuildConfig.FLAVOR == "zhushou360") {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, f + " http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "分享到"))
        //http://f1.market.xiaomi.com/download/AppStore/080c545916d2df42aef8bd1f655ebde2cf943c492/com.dawson.aaaccount.apk
    }


    /**
     * 发起QQ临时聊天
     */
    fun shareQQContact(activity: Activity, textView: TextView) {
        textView.text = Html.fromHtml(activity.getString(R.string.str_about_contact))
        textView.setOnClickListener { _ ->
            try {
                val url = "mqqwpa://im/chat?chat_type=wpa&uin=3230443237"//uin是发送过去的qq号码
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "未检测到QQ，请安装QQ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
