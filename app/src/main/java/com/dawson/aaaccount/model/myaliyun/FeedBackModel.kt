package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import android.os.AsyncTask
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.DBSystemLogDao
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.model.IFeedBackModel
import com.dawson.aaaccount.net.FeedbackService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.util.FilePathConstants
import com.dawson.aaaccount.util.PhoneHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * 反馈
 * Created by Dawson on 2017/8/17.
 */

class FeedBackModel : IFeedBackModel {


    private val service = RetrofitHelper.getService(FeedbackService::class.java)

    override fun add(title: String, content: String): Observable<OperateResult<Any>> {
        val param = HashMap<String, String>()
        param["title"] = title
        param["content"] = content
        param["uid"] = UserInstance.current_user?.id!!
        return service.save(param).map { it.cast<Any>(null) }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMyFeedback(): Observable<OperateResult<List<Feedback>>> {
        val param = HashMap<String, String>()
        param["uid"] = UserInstance.current_user?.id!!
        return service.getMyFeedback(param).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
