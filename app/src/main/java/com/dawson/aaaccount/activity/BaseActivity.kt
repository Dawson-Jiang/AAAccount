package com.dawson.aaaccount.activity

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.common_title.*

/**
 * 所有Activity的基类 将处理页面标题相关事件
 *
 * @author dawson
 */
abstract class BaseActivity : AppCompatActivity() {

    protected var mProgressDialog: Dialog? = null

    protected fun cancelDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) mProgressDialog!!.cancel()
    }

    /**
     * 初始化设置自定义的标题栏 必须在 setcontentview 方法后面调用
     */
    protected open fun initCommonTitle() {
//        setSupportActionBar(nav_toolbar)
        iv_left.setOnClickListener { _ -> finish() }
    }

    protected open fun enableOperate(text: String, l: (View) -> Unit) {
        tv_operate.visibility = View.VISIBLE
        tv_operate.text = text
        tv_operate.setOnClickListener(l)
    }

    protected open fun enableOperate(text: Int, l: (View) -> Unit) {
        enableOperate(getString(text), l)
    }

    protected open fun enableIvRight(rsid: Int, l: (View) -> Unit) {
        if (rsid > 0)
            iv_right.setImageResource(rsid)
        enableIvRight(l)
    }

    protected open fun enableIvRight(l: (View) -> Unit) {
        iv_right.visibility = View.VISIBLE
        iv_right.setOnClickListener(l)
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        tv_title.text = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
