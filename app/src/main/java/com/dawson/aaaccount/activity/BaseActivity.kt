package com.dawson.aaaccount.activity

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        setSupportActionBar(nav_toolbar)
        nav_toolbar.setNavigationOnClickListener { _ -> finish() }
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        nav_toolbar.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
