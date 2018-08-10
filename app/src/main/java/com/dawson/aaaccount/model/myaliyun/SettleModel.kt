package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.*
import com.dawson.aaaccount.dao.bean.*
import com.dawson.aaaccount.model.ISettleModel
import com.dawson.aaaccount.model.leancloud.bean.*
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.format
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * 统计结算
 * Created by dawson on 2017/3/29.
 */
class SettleModel : ISettleModel {
    override fun settle(settle: Settle): Observable<OperateResult<Any>> {
        return Observable.just(OperateResult())
    }

    override fun getByFamilyId(familyId: String): Observable<OperateResult<List<Settle>>> {
        return Observable.just(OperateResult())

    }

    override fun statistic(family: Family, start: Date?, end: Date?, containSettle: Boolean): Observable<OperateResult<Settle>> {
        return Observable.just(OperateResult())

    }

    override fun statisticMine(start: Date?, end: Date?): Observable<OperateResult<Settle>> {
        return Observable.just(OperateResult())

    }

    override fun syncData(context: Context, all: Boolean): Observable<OperateResult<Any>> {
        //同步自己的所有账单
        return Observable.just(OperateResult())

    }

    private fun syncUser(family: AVObject): Observable<AVObject> {
        return Observable.just(AVObject())

    }

    private fun syncDayBook(family: AVObject?): Observable<OperateResult<Any>> {
        return Observable.just(OperateResult())

    }

    companion object {
        private var DAY_BOOKS: List<DBDayBook>? = null
    }
}
