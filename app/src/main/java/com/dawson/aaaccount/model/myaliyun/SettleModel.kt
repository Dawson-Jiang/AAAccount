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
import java.util.*
import kotlin.collections.HashMap

/**
 * 统计结算
 * Created by dawson on 2017/3/29.
 */
class SettleModel : ISettleModel {
    private val service = RetrofitHelper.getService(SettleService::class.java)

    override fun settle(settle: Settle): Observable<OperateResult<Any>> {
        return service.settle(settle).map { it.cast<Any>(null) }
    }

    override fun getByFamilyId(familyId: String): Observable<OperateResult<List<Settle>>> {
        return service.getFamilySettle(mutableMapOf(Pair("fid", familyId)))
    }

    override fun statistic(family: Family, start: Date?, end: Date?, containSettle: Boolean): Observable<OperateResult<Settle>> {
        val param = HashMap<String, String>()
        param["fid"] = family.id!!
        param["start"] = start?.format()!!
        param["end"] = end?.format()!!
        param["contain_settle"] = containSettle.toString()

        return service.statistic(param)
    }

    override fun statisticMine(start: Date?, end: Date?): Observable<OperateResult<Settle>> {
        val param = HashMap<String, String>()
        param["uid"] = UserInstance.current_user?.id!!
        param["start"] = start?.format()!!
        param["end"] = end?.format()!!

        return service.statisticMine(param)
    }

    override fun syncData(context: Context, all: Boolean): Observable<OperateResult<Any>> {
        //同步自己的所有账单 无需同步  直接返回成功
        return Observable.just(OperateResult<Any>(""))
    }
}
