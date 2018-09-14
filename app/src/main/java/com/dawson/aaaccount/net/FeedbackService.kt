package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackService {
    @POST("feedback/save")
    fun save(@Body param: Map<String,String>): Observable<OperateResult<String>>
}