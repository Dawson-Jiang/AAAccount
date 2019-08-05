package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.ISettleModel
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.net.SettleService
import com.dawson.aaaccount.util.format
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * 统计结算
 * Created by dawson on 2017/3/29.
 */
class SettleModel : ISettleModel {
    private val service = RetrofitHelper.getService(SettleService::class.java)

    override fun settle(settle: Settle): Observable<OperateResult<Any>> {
        return service.settle(settle).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map { it.cast<Any>(null) }
    }

    override fun getByFamilyId(familyId: String): Observable<OperateResult<List<Settle>>> {
        return service.getFamilySettle(mutableMapOf(Pair("fid", familyId))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun statistic(family: Family, start: Date?, end: Date?, containSettle: Boolean): Observable<OperateResult<Settle>> {
        val param = HashMap<String, String>()
        param["fid"] = family.id!!
        if (start != null && end != null) {
            param["start"] = start?.format()!!
            param["end"] = end?.format()!!
            return service.statistic(param).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        } else {
            return service.statisticUnSettled(param).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun statisticMine(start: Date?, end: Date?): Observable<OperateResult<Settle>> {
        val param = HashMap<String, String>()
        param["uid"] = UserInstance.current_user?.id!!
        if (start == null || end == null) {
            val calendar = Calendar.getInstance()
            val tend = calendar.time
            calendar.add(Calendar.MONTH, -1)
            val tstart = calendar.time
            param["start"] = tstart?.format()!!
            param["end"] = tend?.format()!!
        } else {
            param["start"] = start.format()
            param["end"] = end.format()
        }

        return service.statisticMine(param).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun syncData(context: Context, all: Boolean): Observable<OperateResult<Any>> {
        //同步自己的所有账单 无需同步  直接返回成功
        return Observable.just(OperateResult<Any>(""))
    }
}
