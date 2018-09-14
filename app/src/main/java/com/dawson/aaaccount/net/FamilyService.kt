package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * <p>project: AAAccount </p>
 * <p>des: [功能描述] </p>
 * <p>date: 2018/9/14 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
interface FamilyService {

    @POST("family/save")
    fun save(@Body family: Family): Observable<OperateResult<String>>

    @POST("family/join")
    fun join(@Body param:Map<String,String>): Observable<OperateResult<Any>>

    @POST("family/dis_join")
    fun disJoin(@Body param:Map<String,String>): Observable<OperateResult<Any>>
}