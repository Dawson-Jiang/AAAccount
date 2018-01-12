package com.dawson.aaaccount.model.leancloud

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.LogInCallback
import com.avos.sns.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestFutureTarget
import com.bumptech.glide.request.target.Target
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.util.DLog
import com.dawson.aaaccount.util.GlideWrapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * QQ登陆
 * Created by Dawson on 2017/8/12.
 */
class QQLogin(val activity: Activity) {

    fun login(): Observable<OperateResult<Any>> {
        return auth()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap<User> {
                    loginWithAuth(it)
                }.observeOn(Schedulers.io())
                .flatMap<OperateResult<Any>> {
                    updateInfo(it)
                }
    }

    /**
     * 第三方授权
     */
    private fun auth(): Observable<SNSBase> {
        return Observable.create<SNSBase> { em ->
            SNS.setupPlatform(activity.applicationContext, SNSType.AVOSCloudSNSQQ, "101412765",
                    "f83ceb68b5166e63673b0b08f09e1c0f", "https://leancloud.cn/1.1/sns/callback/v0ieo7rkabppf3ac")
            SNS.loginWithCallback(activity, SNSType.AVOSCloudSNSQQ,
                    object : SNSCallback() {   // 第三方授权callback
                        override fun done(res: SNSBase, ex: SNSException?) {
                            if (ex == null) {
                                if (res.authorizedData() == null) {//忽略这次回调
                                    DLog.i("authorizedData", "authorizedData is null")
                                } else {
                                    DLog.i("authorizedData", res.authorizedData().toString())
                                    em.onNext(res)
                                    em.onComplete()
                                }
                            } else {
                                em.onError(ex)
                            }
                        }
                    }
            )
        }
    }

    /**
     * 第三方授权信息登录
     */
    private fun loginWithAuth(snsRes: SNSBase): Observable<User> {
        return Observable.create<User> { em ->
            SNS.loginWithAuthData(snsRes.userInfo(),
                    object : LogInCallback<AVUser>() { // 第三方注册登录callback
                        override fun done(avUser: AVUser, ex: AVException?) {
                            if (ex == null) {
                                val userTemp = User()
                                userTemp.id = avUser.objectId
                                if (System.currentTimeMillis() - avUser.createdAt.time < 60 * 1000) {   //新用户更新昵称和头像
                                    userTemp.name = snsRes.authorizedData().getString("nickname")
                                    userTemp.headUrl = snsRes.authorizedData().getString("figureurl_qq_2")
                                } else userTemp.headUrl = "has"
                                em.onNext(userTemp)
                                em.onComplete()
                            } else {
                                em.onError(ex)
                            }
                        }
                    })
        }
    }


    /**
     * 新用户 更新信息 包括下载 上传头像
     */
    private fun updateInfo(user: User): Observable<OperateResult<Any>> {
//        if (System.currentTimeMillis() - user.createTime?.time!! < 60 * 1000) {   //新用户更新昵称和头像
//         }
        return if (user.headUrl != null && user.headUrl?.equals("has")!!) {
            Observable.just(OperateResult<Any>(null))
        } else {//新用户同步头像和昵称
            //下载QQ头像
            val target = Glide.with(activity.applicationContext).downloadOnly().load(user.headUrl).submit()
            //上传头像
            FileModel().uploadFile(activity.applicationContext, target.get().absolutePath, { })
                    .observeOn(Schedulers.io()).flatMap<OperateResult<User>> { res ->
                user.headUrl = res.content!![0]
                UserModel().update(user)//更新信息
            }.flatMap<OperateResult<Any>> { Observable.just(OperateResult<Any>(null)) }
        }
    }

    fun logout() {
        SNS.setupPlatform(activity.applicationContext, SNSType.AVOSCloudSNSQQ, "101412765",
                "f83ceb68b5166e63673b0b08f09e1c0f", "https://leancloud.cn/1.1/sns/callback/v0ieo7rkabppf3ac")
        SNS.logout(activity, SNSType.AVOSCloudSNSQQ)
    }
}
