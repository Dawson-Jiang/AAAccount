package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part


/**
 * <p>project: AAAccount </p>
 * <p>des: 基础公用的接口 </p>
 * <p>date: 2018/9/17 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
interface CommonService {
    @POST("common/upload_log")
    fun uploadLog(@Body param: List<Map<String, String>>): Observable<OperateResult<Any>>

    @Multipart
    @POST("/common/file_upload")
    fun fileUpload(@Body file: MultipartBody): Observable<OperateResult<List<String>>>
}