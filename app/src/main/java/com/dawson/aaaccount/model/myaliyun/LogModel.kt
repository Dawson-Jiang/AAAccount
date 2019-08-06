package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import com.dawson.aaaccount.dao.DBSystemLogDao
import com.dawson.aaaccount.dao.utils.GreenDaoUtil
import com.dawson.aaaccount.dao.bean.DBSystemLog
import com.dawson.aaaccount.model.ILogModel
import com.dawson.aaaccount.net.CommonService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.PhoneHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * 日志相关
 * Created by Dawson on 2017/8/12.
 */
class LogModel : ILogModel {
    private val service = RetrofitHelper.getService(CommonService::class.java)

    override fun uploadLog(context: Context) {
        val dis = Observable.create<List<DBSystemLog>> { em ->
            val dao = GreenDaoUtil.daoSession!!.dbSystemLogDao
            val query = dao.queryBuilder()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            query.where(DBSystemLogDao.Properties.CreateTime.ge(calendar.time))
            em.onNext(query.list())
            em.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .map { logs ->
                    val avLogs = ArrayList<Map<String, String>>()
                    val info = StringBuilder()
                    info.append(com.dawson.aaaccount.BuildConfig.VERSION_CODE)
                    info.append("|")
                    info.append(PhoneHelper.phoneType)
                    for (log in logs) {
                        val avLog = HashMap<String, String>()
                        if (UserInstance.current_user != null)
                            avLog["uid"] = UserInstance.current_user?.id!!
                        avLog["title"] = log.title
                        avLog["content"] = log.cotent
                        avLog["phone"] = info.toString()
                        avLogs.add(avLog)
                    }
                    return@map avLogs
                }
                .flatMap {
                    service.uploadLog(it)
                }
                .subscribe({
                    //清除数据库
                    if (it.result == ErrorCode.SUCCESS) {
                        val dao = GreenDaoUtil.daoSession!!.dbSystemLogDao
                        dao.deleteAll()
                    }
                }, {})
    }
}