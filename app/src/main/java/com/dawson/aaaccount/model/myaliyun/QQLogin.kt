package com.dawson.aaaccount.model.myaliyun

import android.app.Activity
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.GreenDaoUtil
import com.dawson.aaaccount.dao.bean.DBUser
import com.dawson.aaaccount.dao.bean.withUser
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.net.UserService
import com.dawson.aaaccount.util.ErrorCode
import com.tencent.connect.UserInfo
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


/**
 * QQ登陆
 * Created by Dawson on 2017/8/12.
 */
class QQLogin(val activity: Activity) {

    companion object {
        var mTencent: Tencent? = null
    }

    init {
        mTencent = Tencent.createInstance("1106203415", activity.applicationContext)
    }

    fun login(): Observable<OperateResult<Any>> {
        return auth()
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter { it.result == ErrorCode.SUCCESS }
                .observeOn(Schedulers.io())
                .flatMap<OperateResult<User>> {
                    val author = HashMap<String, String>()
                    val jsonObject = it.content!!
                    author["openid"] = jsonObject.getString("openid")
                    author["access_token"] = jsonObject.getString("access_token")
//                    author["expires_in"] = jsonObject.getString("expires_in")
                    mTencent?.openId = author["openid"]
                    mTencent?.setAccessToken(author["access_token"], jsonObject.getString("expires_in"))
                    RetrofitHelper.getService(UserService::class.java).login(author)
                }.filter {
                    it.result == ErrorCode.SUCCESS
                }
                .doOnNext {
                    val user = it.content!!
                    if (!user.name.isNullOrEmpty()) {
                        GreenDaoUtil.daoSession?.dbUserDao?.insert(DBUser().withUser(user))
                    }
                }
                .flatMap<OperateResult<Any>> { oruser ->
                    if (oruser.content!!.name.isNullOrEmpty()) {
                        Observable.create<OperateResult<Any>> {
                            val qqToken = mTencent?.qqToken
                            val userInfo = UserInfo(activity.applicationContext, qqToken)
                            userInfo.getUserInfo(object : IUiListener {
                                override fun onComplete(p0: Any?) {
                                    val json = p0 as JSONObject
                                    val user = oruser.content!!
                                    user.name = json.getString("nickname")
                                    user.headUrl = json.getString("figureurl_2")
//                                user.headThumbUrl = json.getString("figureurl_1")
                                    GreenDaoUtil.daoSession?.dbUserDao?.insert(DBUser().withUser(user))
                                    it.onNext(OperateResult(""))
                                }

                                override fun onCancel() {
                                    it.onNext(OperateResult())
                                }

                                override fun onError(p0: UiError?) {
                                    it.onNext(OperateResult(ecode = p0?.errorCode!!, mes = p0.errorMessage))
                                }
                            })
                        }
                    } else Observable.just(OperateResult<Any>(""))
                }
    }

    /**
     * 第三方授权
     */
    private fun auth(): Observable<OperateResult<JSONObject>> {
        return Observable.create<OperateResult<JSONObject>> { em ->
            if (!mTencent?.isSessionValid!!) {
                mTencent?.login(activity, "get_simple_userinfo", object : IUiListener {
                    override fun onComplete(p0: Any?) {
                        em.onNext(OperateResult(p0 as JSONObject))
                    }

                    override fun onCancel() {
                        em.onNext(OperateResult())
                    }

                    override fun onError(p0: UiError?) {
                        em.onNext(OperateResult(ecode = p0?.errorCode!!, mes = p0.errorMessage))
                    }
                })
            }

//            SNS.setupPlatform(activity.applicationContext, SNSType.AVOSCloudSNSQQ, "1106203415",
//                    "22f23c91bb5e4af78eef7943638e3e29", "https://leancloud.cn/1.1/sns/callback/v0ieo7rkabppf3ac")
//            SNS.loginWithCallback(activity, SNSType.AVOSCloudSNSQQ,
//                    object : SNSCallback() {   // 第三方授权callback
//                        override fun done(res: SNSBase, ex: SNSException?) {
//                            if (ex == null) {
//                                if (res.authorizedData() == null) {//忽略这次回调
//                                    DLog.i("authorizedData", "authorizedData is null")
//                                } else {
//                                    DLog.i("authorizedData", res.authorizedData().toString())
//                                    em.onNext(res)
//                                    em.onComplete()
//                                }
//                            } else {
//                                em.onError(ex)
//                            }
//                        }
//                    }
//            )
        }
    }


    fun logout() {
        mTencent?.logout(activity.applicationContext)
//        SNS.setupPlatform(activity.applicationContext, SNSType.AVOSCloudSNSQQ, "1106203415",
//                "22f23c91bb5e4af78eef7943638e3e29", "https://leancloud.cn/1.1/sns/callback/v0ieo7rkabppf3ac")
//        SNS.logout(activity, SNSType.AVOSCloudSNSQQ)
    }

}
