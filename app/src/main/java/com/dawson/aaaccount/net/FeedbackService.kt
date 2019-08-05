package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackService {
    @POST("feedback/add")
    fun save(@Body param: Map<String,String>): Observable<OperateResult<String>>

    @POST("feedback/get_my_feedback")
    fun getMyFeedback(@Body param: Map<String,String>): Observable<OperateResult<List<Feedback>>>
}