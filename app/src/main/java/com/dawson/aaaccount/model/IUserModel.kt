package com.dawson.aaaccount.model

import android.app.Activity
import android.content.Context
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * 用户操作
 * Created by 097K7X on 2017/2/10.
 */

interface IUserModel {

    /**
     * 获取当前登陆用户
     *
     * @return
     */
    val currentUser: User?

    /**
     * 发送登录验证码
     * @param context
     * @param phone
     */
    fun sendLoginVerify(context: Context, phone: String): Observable<OperateResult<Any>>

    /**
     * 检测是否是已注册用户
     * @param context
     * @param phone
     */
    fun checkRegUser(context: Context, phone: String): Observable<OperateResult<Boolean>>

    /**
     * 手机验证码登录
     * @param context
     * @param phone
     * @param verify
     */
    fun loginByPhoneVerify(context: Context, phone: String, verify: String): Observable<OperateResult<User>>

    /**
     * QQ登录
     * @param activity
     */
    fun loginByQQ(activity: Activity): Observable<OperateResult<Any>>

    /**
     * 退出登陆
     *
     * @param activity
     */
    fun logout(activity: Activity): Observable<OperateResult<Any>>

    /**
     * 登录过期
     *
     * @param context
     */
    fun loginTimeOut(context: Activity)

    fun isLogin(context: Context): Boolean

    fun cleanLoginInfo(context: Context)

    fun initUser(context: Context): Observable<OperateResult<Any>>

    /**
     * 修改用户信息
     *
     * @param user
     */
    fun update(user: User): Observable<OperateResult<User>>
}
