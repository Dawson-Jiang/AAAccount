package com.dawson.aaaccount.model.myaliyun

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dawson.aaaccount.BuildConfig
import com.dawson.aaaccount.activity.LoginActivity
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.dao.bean.DBUser
import com.dawson.aaaccount.dao.bean.withDBUser
import com.dawson.aaaccount.dao.bean.withUser
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.net.UserService
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.PhoneHelper
import com.dawson.aaaccount.util.format
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap


object UserInstance {
    var current_user: User? = null
}

/**
 * 用户操作逻辑类
 * Created by dawson on 2017/2/10.
 */

class UserModel : IUserModel {
    override val currentUser: User?
        get() = UserInstance.current_user


    private val service = RetrofitHelper.getService(UserService::class.java)

    override fun checkRegUser(context: Context, phone: String): Observable<OperateResult<Boolean>> {
        return Observable.just(OperateResult())
    }

    override fun sendLoginVerify(context: Context, phone: String): Observable<OperateResult<Any>> {
        return Observable.just(OperateResult())
    }

    override fun loginByPhoneVerify(context: Context, phone: String, verify: String): Observable<OperateResult<User>> {
        return Observable.just(OperateResult())
    }

    override fun loginByQQ(activity: Activity): Observable<OperateResult<Any>> {
        val qqLogin = QQLogin(activity)
        var org_user = User()
        return qqLogin.login().subscribeOn(Schedulers.io())
                .flatMap { service.login(it.content!!) }
                .doOnNext {
                    org_user = it.content!!
                    GreenDaoUtil.daoSession?.dbUserDao?.insert(DBUser().withUser(org_user))
                }
                .flatMap {
                    if (it.content!!.name.isNullOrEmpty()) {//第一次登录用户
                        qqLogin.getUserInfo()
                                .observeOn(Schedulers.io())
                                .flatMap {
                                    if (it.result == ErrorCode.SUCCESS) {
                                        org_user.name = it.content!!["nickname"]
                                        org_user.headUrl = it.content!!["figureurl_2"]
                                         GreenDaoUtil.daoSession?.dbUserDao?.update(DBUser().withUser(org_user))
                                        service.update(org_user)
                                    } else Observable.just(OperateResult<Any>())
                                }
                    } else Observable.just(OperateResult<Any>())
                }.observeOn(Schedulers.io())
                .flatMap { initUser(activity) }
                .flatMap {
                    updateLoginInfo(1)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun logout(activity: Activity): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> { e ->
            val qqLogin = QQLogin(activity)
            qqLogin.logout()
            // 清除本地缓存
            cleanLoginInfo(activity.applicationContext)
            e.onNext(OperateResult(null))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun isLogin(context: Context): Boolean {
        return UserInstance.current_user != null
    }

    /**
     * 登录超期 跳转到登录页面
     */
    override fun loginTimeOut(context: Activity) {
        AlertDialog.Builder(context).setTitle("提示").setMessage("登录过期")
                .setPositiveButton("确定") { _, _ ->
                    logout(context).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { _ ->
                                //跳转到登陆页面
                                val intent = Intent()
                                intent.setClass(context, LoginActivity::class.java)
                                context.startActivity(intent)
                                context.finish()
                            }
                }.show()
    }

    override fun cleanLoginInfo(context: Context) {
        GreenDaoUtil.daoSession?.dbUserDao?.deleteAll()
        UserInstance.current_user = null
    }

    override fun initUser(context: Context): Observable<OperateResult<Any>> {
        if (currentUser != null) {
            return updateLoginInfo()
        }

        return Observable.create<Any> { e ->
            val users = GreenDaoUtil.daoSession?.dbUserDao?.loadAll()
            if (users != null && !users.isEmpty()) {//已经登录
                UserInstance.current_user = User().withDBUser(users[0])
            }
            e.onNext("")
            e.onComplete()
        }.subscribeOn(Schedulers.io()).flatMap {
            if (currentUser != null)
                updateLoginInfo()
            else Observable.just(OperateResult())
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 更新登录信息
     */
    private fun updateLoginInfo(flag: Int = 0): Observable<OperateResult<Any>> {
        val user = JsonObject()
        if (flag == 1) {
            val info = JsonObject()
            info.addProperty("date", Date(System.currentTimeMillis()).format("yyyy-MM-dd HH:mm:ss"))
            info.addProperty("version", BuildConfig.VERSION_CODE.toString())
            info.addProperty("phone", Build.BRAND)
            info.addProperty("phoneType", PhoneHelper.phoneType)
            info.addProperty("flavor", BuildConfig.FLAVOR)
            info.addProperty("platform", "Android")
            info.addProperty("os", "Android " + Build.VERSION.RELEASE)
            user.add("loginInfo", info)
        }
        user.addProperty("id", currentUser?.id!!)
        return service.updateLoginInfo(user).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun update(user: User): Observable<OperateResult<User>> {
        return service.update(user).map {
            GreenDaoUtil.daoSession?.dbUserDao?.update(DBUser().withUser(user))
            OperateResult(user)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}