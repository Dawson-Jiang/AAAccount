package com.dawson.aaaccount.model

import android.content.Context
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateCallback
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * 家庭相关逻辑
 * Created by dawson on 2017/2/15.
 */

interface IFamilyModel {

    fun create(context: Context, family: Family): Observable<OperateResult<Family>>

    /**
     * 加入家庭
     *
     */
    fun join(family: Family): Observable<OperateResult<Family>>

    /**
     * 退出家庭
     * @param family
     */
    fun disJoin(family: Family): Observable<OperateResult<Any>>
    /**
     * 删除家庭
     * @param family
     */
    fun del(family: Family): Observable<OperateResult<Any>>

    /**
     * 获取我(当前用户)的家庭
     *
     * @return
     */
    fun getMyFamily(): Observable<OperateResult<List<Family>>>

    /**
     * 获取指定的家庭
     *
     * @return
     */
    fun getFamilyById(context: Context, id: String): Observable<OperateResult<Family>>

    /**
     * 修改家庭信息
     *
     * @param context
     * @param family
     */
    fun modify(context: Context, family: Family): Observable<OperateResult<Family>>


    /**
     * 添加成员 临时家庭有效
     *
     * @param family
     */
    fun addMember(family: Family, user: User): Observable<OperateResult<User>>

    /**
     * 删除成员 临时家庭有效
     *
     * @param family
     */
    fun delMemeber(family: Family, user: User): Observable<OperateResult<Any>>

    /**
     * 修改成员信息 临时家庭有效
     *@param user
     */
    fun modifyMemeber(user: User): Observable<OperateResult<User>>
}
