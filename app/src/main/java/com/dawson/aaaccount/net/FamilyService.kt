package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
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
    fun join(@Body param: Map<String, String>): Observable<OperateResult<Any>>

    @POST("family/dis_join")
    fun disJoin(@Body param: Map<String, String>): Observable<OperateResult<Any>>

    @POST("family/del")
    fun del(@Body id: String): Observable<OperateResult<Any>>

    @POST("family/get_my_family")
    fun getMyFamily(@Body uid: String): Observable<OperateResult<List<Family>>>

    @POST("family/get")
    fun get(@Body id: String): Observable<OperateResult<Family>>

    @POST("family/add_member")
    fun addMember(@Body family: Family): Observable<OperateResult<User>>

    @POST("family/del_member")
    fun delMemeber(@Body param: Map<String, String>): Observable<OperateResult<Any>>
}