package com.dawson.aaaccount

import android.support.multidex.MultiDexApplication
import com.avos.avoscloud.AVOSCloud
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.model.leancloud.LogModel
import com.dawson.aaaccount.util.DLog

//import com.facebook.stetho.Stetho

/**
 * 自定义Application
 * Created by dawson on 2016/4/19.
 */
class AAAccountApplication :  MultiDexApplication() {

    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e -> DLog.error("uncaughtException", e) }

    override fun onCreate() {
        super.onCreate()
        aplication = this
        GreenDaoUtil.initDaoDB(this)
        AVOSCloud.initialize(this, "mcoshkm3fro2kef3j4wtkxyid7k6o7zga85g4wjj0c4fc1tw",
                "fh1oohucqgtrv4572ldsswyn9y7udnuw59el6it3xqxjdlpp")
        AVOSCloud.setDebugLogEnabled(DEBUG)
//        if (DEBUG) Stetho.initializeWithDefaults(this.applicationContext)
        // 开启消息接收服务
        //        Intent intent = new Intent("com.jd.myaaaccount.action.SUBSCRIBE");
        //        startService(intent);

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)

        //上传日志
        if (!DEBUG)
            LogModel().uploadLog(aplication.applicationContext)
    }

    companion object {
        /**
         * 是否为调试模式
         */
        val DEBUG = BuildConfig.DEBUG

        var aplication: AAAccountApplication = AAAccountApplication()
    }
}
