package com.dawson.aaaccount.net

import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    /**
     * 更新登录信息 同时验证登录是否失效
     */
    @POST("user/updateLoginInfo")
    fun updateLoginInfo(@Body loginInfo: JsonObject): Observable<OperateResult<Any>>

    /**
     * 登录
     */
    @POST("user/login")
    fun login(@Body author: Map<String,String>): Observable<OperateResult<User>>

    /**
     * 更新用户信息
     */
    @POST("user/update")
    fun update(@Body author: User): Observable<OperateResult<Any>>
}