package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import android.text.TextUtils
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IDayBookModel
import com.dawson.aaaccount.model.leancloud.bean.withAVMembers
import com.dawson.aaaccount.model.leancloud.bean.withAVObject
import com.dawson.aaaccount.model.leancloud.bean.withAVUsers
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 账单相关业务
 * Created by dawson on 2017/2/12.
 */
class DayBookModel : IDayBookModel {
    override fun save(context: Context, dayBook: DayBook): Observable<OperateResult<DayBook>> {
        return   Observable.just(OperateResult())
    }

    override fun get(familyId: String, page: Int, limit: Int): Observable<OperateResult<List<DayBook>>> {
        return   Observable.just(OperateResult())

    }

    override fun getById(id: String): Observable<OperateResult<DayBook>> {
        return   Observable.just(OperateResult())

    }

    override fun delete(id: String): Observable<OperateResult<Any>> {
        return   Observable.just(OperateResult())

    }
}
