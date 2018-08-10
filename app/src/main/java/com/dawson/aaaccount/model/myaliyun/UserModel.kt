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
import com.dawson.aaaccount.util.PhoneHelper
import com.dawson.aaaccount.util.format
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
        return QQLogin(activity).login()
                .doOnNext {
                    val users = GreenDaoUtil.daoSession?.dbUserDao?.loadAll()
                    if (users != null && !users.isEmpty()) {//已经登录
                        UserInstance.current_user = User().withDBUser(users[0])
                    }
                }.observeOn(Schedulers.io())
                .flatMap {
                    getService().update(UserInstance.current_user!!)
                }.flatMap {
                    updateLoginInfo(1)
                }
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
        }.flatMap {
            if (currentUser != null)
                updateLoginInfo()
            else Observable.just(OperateResult())
        }.subscribeOn(Schedulers.io())
    }

    /**
     * 更新登录信息
     */
    private fun updateLoginInfo(flag: Int = 0): Observable<OperateResult<Any>> {
        val info = HashMap<String, String>()
        if (flag == 1) {
            info["date"] = Date(System.currentTimeMillis()).format("yyyy-MM-dd HH:mm:ss")
            info["version"] = BuildConfig.VERSION_CODE.toString()
            info["phone"] = Build.BRAND
            info["phoneType"] = PhoneHelper.phoneType
            info["flavor"] = BuildConfig.FLAVOR
            info["platform"] = "Android"
            info["operate"] = "Android " + Build.VERSION.RELEASE
        }
        info["userId"] = currentUser?.id!!
        return getService().updateLoginInfo(info)
    }

    override fun update(user: User): Observable<OperateResult<User>> {
        return getService().update(user).map {
            GreenDaoUtil.daoSession?.dbUserDao?.update(DBUser().withUser(user))
            OperateResult(user)
        }
    }

    private var service: UserService? = null
    private fun getService(): UserService {
        if (service == null) service = RetrofitHelper.getService(UserService::class.java)
        return service!!
    }
}