package com.dawson.aaaccount.model.myaliyun

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.net.FamilyService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.net.UserService
import com.dawson.aaaccount.util.ErrorCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 家庭相关业务
 * Created by dawson on 2017/2/15.
 */
class FamilyModel : IFamilyModel {
    private val service = RetrofitHelper.getService(FamilyService::class.java)

    override fun create(context: Context, family: Family): Observable<OperateResult<Family>> {
        return service.save(family).map {
            if (it.result == ErrorCode.SUCCESS) family.id = it.content!!
            return@map it.cast(family)
        }
    }

    override fun join(family: Family): Observable<OperateResult<Family>> {
        val param = HashMap<String, String>()
        param["fid"] = family.id!!
        param["uid"] = UserInstance.current_user?.id!!

        return service.join(param).map {
            if (it.result == ErrorCode.SUCCESS) family.members?.add(UserInstance.current_user!!)
            return@map it.cast(family)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun disJoin(family: Family): Observable<OperateResult<Any>> {
        val param = HashMap<String, String>()
        param["fid"] = family.id!!
        param["uid"] = UserInstance.current_user?.id!!

        return service.disJoin(param).doOnNext {
            if (it.result == ErrorCode.SUCCESS) {
                family.members?.removeIf { u -> u.id == UserInstance.current_user?.id }
            }
        }
    }

    override fun del(family: Family): Observable<OperateResult<Any>> {
        return service.del(family.id!!)
    }

    override fun getMyFamily(): Observable<OperateResult<List<Family>>> {
        return service.getMyFamily(UserInstance.current_user?.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFamilyById(context: Context, id: String): Observable<OperateResult<Family>> {
        return service.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun modify(context: Context, family: Family): Observable<OperateResult<Family>> {
        return create(context, family)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addMember(family: Family, user: User): Observable<OperateResult<User>> {
        val f = Family()
        f.id = family.id
        f.members = ArrayList()
        f.members?.add(user)
        return service.addMember(f).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun delMemeber(family: Family, user: User): Observable<OperateResult<Any>> {
        val f = Family()
        f.id = family.id
        f.members = ArrayList()
        f.members?.add(user)
        val param = HashMap<String, String>()
        param["fid"] = family.id!!
        param["uid"] = user.id!!
        return service.delMemeber(param).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun modifyMemeber(user: User): Observable<OperateResult<User>> {
        return RetrofitHelper.getService(UserService::class.java).update(user).map {
            it.cast(user)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}