package com.dawson.aaaccount.model

import android.content.Context
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * 账单业务
 * Created by dawson on 2017/2/12.
 */

interface IDayBookModel {

    /**
     * 添加和修改
     */
    fun save(context: Context, dayBook: DayBook): Observable<OperateResult<DayBook>>

    /**
     * @param familyId null表示获取个人，其他表示获取指定家庭的记录
     * @param page     查询的页数 0开始
     */
    operator fun get(familyId: String, page: Int, limit: Int): Observable<OperateResult<List<DayBook>>>

    /**
     * 获取记录详细信息
     */
    fun getById(id: String): Observable<OperateResult<DayBook>>

    fun delete(id: String): Observable<OperateResult<Any>>
}
