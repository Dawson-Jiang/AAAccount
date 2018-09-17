package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface SettleService {
    @POST("settle/settle")
    fun settle(@Body param: Settle): Observable<OperateResult<String>>

    @POST("settle/get_by_family")
    fun getByFamily(@Body fid: String): Observable<OperateResult<List<Settle>>>

    @POST("settle/statistic")
    fun statistic(@Body param: Map<String,String>): Observable<OperateResult<Settle>>

    @POST("settle/statistic_mine")
    fun statisticMine(@Body param: Map<String,String>): Observable<OperateResult<Settle>>
}