package com.dawson.aaaccount.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.AlertDialogHelper
import com.dawson.aaaccount.util.ErrorCode

class RegistActivity : Activity() {

    private var etPhone: EditText? = null
    private var etPassword: EditText? = null
    private var etVerifyCode: EditText? = null
    private var btnSendVerifyCode: Button? = null
    private var btnRegister: Button? = null
    private val mContext: Context? = null

    //    UserPresenter userPresenter;
    var mProgressDialog: Dialog = ProgressDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        iniComponent()
        //        userPresenter = new UserPresenter(this);

        btnRegister!!.setOnClickListener { _ -> register() }
        btnSendVerifyCode!!.setOnClickListener { _ ->
            val phone = etPhone!!.text.toString()
            if (phone == "") {
                Toast.makeText(this@RegistActivity,
                        R.string.register_null_phone_notice, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //            if (userPresenter != null)
            //                userPresenter.sendRegistVerify(mContext, phone);
        }
        mProgressDialog = ProgressDialog(this)
    }


    private fun iniComponent() {

        etPhone = findViewById(R.id.etPhone) as EditText
        etPassword = findViewById(R.id.etPassword) as EditText

        btnRegister = findViewById(R.id.btnRegister) as Button
        etVerifyCode = findViewById(R.id.etVerifyCode) as EditText
        btnSendVerifyCode = findViewById(R.id.btnSendVerifyCode) as Button
        // 测试初始化数据
        etPhone!!.setText("18628399607")
        etPassword!!.setText("888888")
    }

    private fun register() {

        val phone = etPhone!!.text.toString()
        if (phone == "") {
            Toast.makeText(this@RegistActivity,
                    R.string.register_null_phone_notice, Toast.LENGTH_SHORT).show()
            return
        }
        val password = etPassword!!.text.toString()
        if (password == "") {
            Toast.makeText(this@RegistActivity,
                    R.string.register_null_password_notice, Toast.LENGTH_SHORT)
                    .show()
            return
        }

        val verify = etVerifyCode!!.text.toString()
        if (verify == "") {
            Toast.makeText(this@RegistActivity,
                    R.string.register_null_verify_notice, Toast.LENGTH_SHORT)
                    .show()
            return
        }
        val user = User()
        user.phone = phone
        user.password = password
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                this@RegistActivity, R.string.registering)
        //        userPresenter.register(this.getApplicationContext(), user, verify);
    }


    fun onRegisterResult(result: OperateResult<User>) {
        if (mProgressDialog.isShowing) mProgressDialog.cancel()
        if (result.result == ErrorCode.SUCCESS) {// 注册成功
            AlertDialogHelper.showOKAlertDialog(
                    this@RegistActivity, R.string.register_success, null)// { dialog, which -> finish() }
        } else {
            Common.showErrorInfo(this@RegistActivity, result.errorCode,
                    R.string.register_fail, 0)
        }
    }

}
