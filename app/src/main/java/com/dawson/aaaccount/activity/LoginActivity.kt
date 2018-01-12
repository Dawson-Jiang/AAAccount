package com.dawson.aaaccount.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import com.avos.sns.SNS
import com.avos.sns.SNSException
import com.avos.sns.SNSType
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.AlertDialogHelper
import com.dawson.aaaccount.util.DLog
import com.dawson.aaaccount.util.ErrorCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity() {
    private var mProgressDialog: Dialog? = null
    private val userModel: IUserModel = UserModel()
    //    private int loginMethod = 2;//1 验证码登陆 2 QQ登录

    private fun cancelDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) mProgressDialog!!.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)

        iv_phone.setOnClickListener {
            ll_phone_login.visibility = View.VISIBLE
            ll_qq_login.visibility = View.GONE
        }

        btn_login.setOnClickListener { loginByPhone() }
        btn_send_vercode.setOnClickListener { sendCode() }
        iv_qq.setOnClickListener { loginByQQ() }
        iv_qq_2.setOnClickListener { loginByQQ() }
    }

    /**
     * 发送验证码
     */
    private fun sendCode() {
        val phone = et_phone.text.toString().trim { it <= ' ' }
        if (phone == "") {
            Toast.makeText(this@LoginActivity, R.string.login_null_phone_notice,
                    Toast.LENGTH_SHORT).show()
            return
        }
        btn_send_vercode.isEnabled = false
        userModel.checkRegUser(this@LoginActivity.applicationContext, phone)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { result ->
                    if (result.result == ErrorCode.SUCCESS && result.content!!) return@filter true
                    else {
                        Toast.makeText(this@LoginActivity, "未注册用户请使用QQ登录", Toast.LENGTH_SHORT).show()
                        return@filter false
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap<OperateResult<Any>> {
                    userModel.sendLoginVerify(this@LoginActivity.applicationContext, phone)
//                    Observable.just(OperateResult())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { ex ->
                    ex.printStackTrace()
                    btn_send_vercode.isEnabled = true
                    Toast.makeText(this@LoginActivity, "验证码发送失败", Toast.LENGTH_SHORT).show()
                }
                .subscribe { result ->
                    if (result.result == ErrorCode.SUCCESS) {
                        btn_send_vercode.isEnabled = false
                        btn_send_vercode.text = "发送成功"
                    } else {
                        btn_send_vercode.isEnabled = true
                        Toast.makeText(this@LoginActivity, "验证码发送失败", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun loginByPhone() {
        val phone = et_phone.text.toString()
        val verifyCode = et_verify_code.text.toString()

        if (phone == "") {
            Toast.makeText(this@LoginActivity, R.string.login_null_phone_notice,
                    Toast.LENGTH_SHORT).show()
            return
        } else et_phone_wrapper.isErrorEnabled = false
        if (verifyCode == "") {
            Toast.makeText(this@LoginActivity, R.string.login_null_verify_notice,
                    Toast.LENGTH_SHORT).show()
            return
        }
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                this@LoginActivity, R.string.handling)

        userModel.loginByPhoneVerify(applicationContext, phone, verifyCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    cancelDialog()
                    userModel.initUser(this@LoginActivity.applicationContext).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError { it.printStackTrace() }
                            .subscribe { _ ->
                                val intent = Intent()
                                intent.setClass(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                }, { e ->
                    cancelDialog()
                    Common.showErrorInfo(this@LoginActivity, ErrorCode.FAIL, e.message)
                })
    }

    private fun loginByQQ() {
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                this@LoginActivity, R.string.handling)
        userModel.loginByQQ(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    cancelDialog()
                    userModel.initUser(this@LoginActivity.applicationContext)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { _ ->
                                val intent = Intent()
                                intent.setClass(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                }, { e ->
                    e.printStackTrace()
                    cancelDialog()
                    val snse = e as? SNSException
                    if (snse == null || snse.code != SNSException.USER_CANCEL)
                        Common.showErrorInfo(this@LoginActivity, ErrorCode.FAIL, e.message)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSQQ)
    }
}