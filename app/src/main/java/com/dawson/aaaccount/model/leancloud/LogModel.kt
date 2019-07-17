package com.dawson.aaaccount.model.leancloud

import android.content.Context
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.dao.DBSystemLogDao
import com.dawson.aaaccount.dao.utils.GreenDaoUtil
import com.dawson.aaaccount.dao.bean.DBSystemLog
import com.dawson.aaaccount.model.ILogModel
import com.dawson.aaaccount.util.PhoneHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 日志相关
 * Created by Dawson on 2017/8/12.
 */
class LogModel : ILogModel {
    override fun uploadLog(context: Context) {
        Observable.create<List<DBSystemLog>> { em ->
            val dao = GreenDaoUtil.daoSession!!.dbSystemLogDao
            val query = dao.queryBuilder()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            query.where(DBSystemLogDao.Properties.CreateTime.ge(calendar.time))
            em.onNext(query.list())
            em.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .doOnNext { logs ->
                    val avLogs = ArrayList<AVObject>()
                    val info = StringBuilder()
                    info.append(com.dawson.aaaccount.BuildConfig.VERSION_CODE)
                    info.append("|")
                    info.append(PhoneHelper.phoneType)
                    for (log in logs) {
                        val avLog = AVObject(DataObjectHelper.SYSTEM_LOG.CLASS_NAME)
                        if (UserInstance.current_user != null)
                            avLog.put(DataObjectHelper.SYSTEM_LOG.USER, AVUser.getCurrentUser())
                        avLog.put(DataObjectHelper.SYSTEM_LOG.TITLE, log.title)
                        avLog.put(DataObjectHelper.SYSTEM_LOG.CONTENT, log.cotent)
                        avLog.put(DataObjectHelper.SYSTEM_LOG.PHONE, info)
                        avLogs.add(avLog)
                    }
                    AVObject.saveAll(avLogs)

                    //清除数据库
                    val dao = GreenDaoUtil.daoSession!!.dbSystemLogDao
                    dao.deleteAll()
                }.subscribe({}, {})
    }
}