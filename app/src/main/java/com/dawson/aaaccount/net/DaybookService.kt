package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.result.OperateResult
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * <p>project: AAAccount </p>
 * <p>des: 账单接口 </p>
 * <p>date: 2018/9/14 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
interface DaybookService {

    @POST("daybook/get_category")
    fun getCategories(): Observable<OperateResult<List<ConsumptionCategory>>>

    @POST("daybook/save")
    fun save(@Body daybook: DayBook): Observable<OperateResult<String>>

    @POST("daybook/get")
    fun get(@Body param:Map<String,String>): Observable<OperateResult<DayBook>>

    @POST("daybook/get_my_daybook")
    fun getMyDaybook(@Body param:Map<String,String>): Observable<OperateResult<List<DayBook>>>

    @POST("daybook/get_family_daybook")
    fun getFamilyDaybook(@Body param:Map<String,String>): Observable<OperateResult<List<DayBook>>>

    @POST("daybook/del")
    fun del(@Body param:Map<String,String>): Observable<OperateResult<Any>>
}