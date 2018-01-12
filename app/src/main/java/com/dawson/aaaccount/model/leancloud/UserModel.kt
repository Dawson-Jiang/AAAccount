package com.dawson.aaaccount.model.leancloud

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.avos.avoscloud.AVSMS
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.activity.LoginActivity
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.model.leancloud.bean.withAVUser
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.PhoneHelper
import com.dawson.aaaccount.util.format
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import com.avos.avoscloud.AVException
import com.avos.avoscloud.LogInCallback
import com.dawson.aaaccount.BuildConfig


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
        return Observable.create<Boolean> { e ->
            try {
                AVUser.loginByMobilePhoneNumber(phone, "defaultpwd31415926535897932384626")
                e.onNext(true)
            } catch (ex: AVException) {
                when {
                    ex.code == 211 -> e.onNext(false)
                    ex.code == 210 -> e.onNext(true)
                    else -> e.onError(ex)
                }
            }
            e.onComplete()
        }.subscribeOn(Schedulers.io())
                .map<OperateResult<Boolean>> {
                    OperateResult(it)
                }
    }

    override fun sendLoginVerify(context: Context, phone: String): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> { e ->
            AVSMS.requestSMSCode(phone, null)
            e.onNext(OperateResult(null))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun loginByPhoneVerify(context: Context, phone: String, verify: String): Observable<OperateResult<User>> {
        return Observable.create<AVUser> { e ->
            e.onNext(AVUser.signUpOrLoginByMobilePhone(phone, verify))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map<OperateResult<User>> { avuser ->
                    UserInstance.current_user = User().withAVUser(avuser)
                    OperateResult(UserInstance.current_user)
                }
    }

    override fun loginByQQ(activity: Activity): Observable<OperateResult<Any>> {
        return QQLogin(activity).login()
    }

    override fun logout(activity: Activity): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> { e ->
            AVUser.logOut()
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
        UserInstance.current_user = null
    }

    override fun initUser(context: Context): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> { e ->
            if (currentUser != null) {
                updateInfo()
                e.onNext(OperateResult(null))
                e.onComplete()
                return@create
            }
            val avUser = AVUser.getCurrentUser()
            if (avUser == null) {//初始化失败
                e.onNext(OperateResult(ErrorCode.FAIL, ""))
                e.onComplete()
                return@create
            }
            UserInstance.current_user = User().withAVUser(avUser)
            updateInfo()
            e.onNext(OperateResult(UserInstance.current_user))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    private fun updateInfo() {
        //更新登录信息
        val info = StringBuilder()
        info.append(Date(System.currentTimeMillis()).format("yyyy-MM-dd HH:mm:ss"))
        info.append("|")
        info.append(BuildConfig.VERSION_CODE)
        info.append("|")
        info.append(PhoneHelper.phoneType)
        info.append("|")
        info.append(BuildConfig.FLAVOR)
        val avUser = AVUser.getCurrentUser()
        avUser.put(DataObjectHelper.USER.LOGIN_INFO, info)
        avUser.save()
    }

    override fun update(user: User): Observable<OperateResult<User>> {
        return Observable.create<OperateResult<User>> { e ->
            val avUser = AVUser.getCurrentUser()
            if (!TextUtils.isEmpty(user.name)) {
                avUser.put(DataObjectHelper.USER.NAME, user.name)
            }
            if (!TextUtils.isEmpty(user.headUrl) && FileModel.uploadFiles[user.headUrl] != null) {
                avUser.put(DataObjectHelper.USER.HEAD, FileModel.uploadFiles[user.headUrl])
            }
            avUser.save()
            e.onNext(OperateResult(user))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}