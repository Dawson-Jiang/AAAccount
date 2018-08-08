package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IFamilyModel
import io.reactivex.Observable

/**
 * 家庭相关业务
 * Created by dawson on 2017/2/15.
 */
class FamilyModel : IFamilyModel {
    override fun create(context: Context, family: Family): Observable<OperateResult<Family>> {
        return   Observable.just(OperateResult())

    }

    override fun join(family: Family): Observable<OperateResult<Family>> {
        return   Observable.just(OperateResult())

    }

    override fun disJoin(family: Family): Observable<OperateResult<Any>> {
        return   Observable.just(OperateResult())

    }

    override fun del(family: Family): Observable<OperateResult<Any>> {
        return   Observable.just(OperateResult())

    }

    override fun getMyFamily(): Observable<OperateResult<List<Family>>> {
        return   Observable.just(OperateResult())

    }

    override fun getFamilyById(context: Context, id: String): Observable<OperateResult<Family>> {
        return   Observable.just(OperateResult())

    }

    override fun modify(context: Context, family: Family): Observable<OperateResult<Family>> {
        return   Observable.just(OperateResult())

    }

    override fun addMember(family: Family, user: User): Observable<OperateResult<User>> {
        return   Observable.just(OperateResult())

    }

    override fun delMemeber(family: Family, user: User): Observable<OperateResult<Any>> {
        return   Observable.just(OperateResult())

    }

    override fun modifyMemeber(user: User): Observable<OperateResult<User>> {
        return   Observable.just(OperateResult())

    }
}