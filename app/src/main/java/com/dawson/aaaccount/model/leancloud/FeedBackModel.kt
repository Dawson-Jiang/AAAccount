package com.dawson.aaaccount.model.leancloud

import android.content.Context
import android.os.AsyncTask
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.DBSystemLogDao
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.model.IFeedBackModel
import com.dawson.aaaccount.util.FilePathConstants
import com.dawson.aaaccount.util.PhoneHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 反馈
 * Created by Dawson on 2017/8/17.
 */

class FeedBackModel : IFeedBackModel {
    override fun add(title: String, content: String): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> {
            val avFeedback = AVObject(DataObjectHelper.FEED_BACK.CLASS_NAME)
            avFeedback.put(DataObjectHelper.FEED_BACK.USER, AVUser.getCurrentUser())
            avFeedback.put(DataObjectHelper.FEED_BACK.TITLE, title)
            avFeedback.put(DataObjectHelper.FEED_BACK.CONTENT, content)
            avFeedback.put(DataObjectHelper.FEED_BACK.PHONE, PhoneHelper.phoneType)
            avFeedback.save()
            it.onNext(OperateResult(""))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}
