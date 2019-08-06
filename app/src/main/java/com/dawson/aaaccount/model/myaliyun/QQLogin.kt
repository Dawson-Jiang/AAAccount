package com.dawson.aaaccount.model.myaliyun

import android.app.Activity
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.exception.QQLoginException
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

    fun login(): Observable<OperateResult<Map<String, String>>> {
        return auth()
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter { it.result == ErrorCode.SUCCESS }
                .observeOn(Schedulers.io())
                .map<OperateResult<Map<String, String>>> {
                    val author = HashMap<String, String>()
                    val jsonObject = it.content!!
                    author["openid"] = jsonObject.getString("openid")
                    author["access_token"] = jsonObject.getString("access_token")
//                    author["expires_in"] = jsonObject.getString("expires_in")
                    mTencent?.openId = author["openid"]
                    mTencent?.setAccessToken(author["access_token"], jsonObject.getString("expires_in"))
                    return@map it.cast(author)
                }
    }

    fun getUserInfo(): Observable<OperateResult<Map<String, String>>> {
        return Observable.create<OperateResult<Map<String, String>>> {
            val qqToken = mTencent?.qqToken
            val userInfo = UserInfo(activity.applicationContext, qqToken)
            userInfo.getUserInfo(object : IUiListener {
                override fun onComplete(p0: Any?) {
                    val json = p0 as JSONObject
                    val info = HashMap<String, String>()
                    info["nickname"] = json.getString("nickname")
                    info["figureurl_2"] = json.getString("figureurl_2")
                    //user.headThumbUrl = json.getString("figureurl_1")
                    it.onNext(OperateResult(info))
                    it.onComplete()
                }

                override fun onCancel() {
                    it.onComplete()
                }

                override fun onError(p0: UiError?) {
                    it.onError(QQLoginException(p0!!))
                }
            })
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
                        em.onComplete()
                    }

                    override fun onCancel() {
                        em.onNext(OperateResult())
                        em.onComplete()
                    }

                    override fun onError(p0: UiError?) {
                        em.onError(QQLoginException(p0!!))
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


class QQLoginTest(val activity: Activity) {

    fun login(): Observable<OperateResult<Map<String, String>>> {
        val author = HashMap<String, String>()
        author["openid"] = "68801BC1A9DC8F1B968BE0E9AE21EA5B"
        author["access_token"] = "617661D6211EB9F8A815E23FCBAF95CF"
        return Observable.just(OperateResult(author.toMap()))
    }

    fun getUserInfo(): Observable<OperateResult<Map<String, String>>> {
        val info = HashMap<String, String>()
        info["nickname"] = "东山再起"
        info["figureurl_2"] = "http://qzapp.qlogo.cn/qzapp/1106203415/68801BC1A9DC8F1B968BE0E9AE21EA5B/100"
        return Observable.just(OperateResult(info.toMap()))
    }
}
