package com.dawson.aaaccount.model

import android.content.Context
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable
import java.util.*

/**
 * 统计结算
 * Created by dawson on 2017/3/29.
 */

interface ISettleModel {

    /**
     * 结算 修改计算标志，并保存结算信息
     *
     */
    fun settle(settle: Settle): Observable<OperateResult<Any>>

    /**
     * 获取一个家庭的结算
     *
     */
    fun getByFamilyId(familyId: String): Observable<OperateResult<List<Settle>>>

    /**
     * 统计
     *
     */
    fun statistic(family: Family, start: Date?, end: Date?, containSettle: Boolean): Observable<OperateResult<Settle>>

    /**
     * 统计自己的账单
     *
     */
    fun statisticMine(start: Date?, end: Date?): Observable<OperateResult<Settle>>

    /**
     * 同步数据 家庭 人员 账单
     *
     * @param context
     * @return
     */
    fun syncData(context: Context, all: Boolean): Observable<OperateResult<Any>>

    companion object {
        val DAYBOOK_LAST_SYNC_TIME = "DAYBOOK_LAST_SYNC_TIME"
    }
}
