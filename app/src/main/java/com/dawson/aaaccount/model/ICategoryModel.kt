package com.dawson.aaaccount.model

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * 类别管理
 * Created by dawson on 2017/2/15.
 */
interface ICategoryModel {
    fun get(): Observable<OperateResult<List<ConsumptionCategory>>>
}
