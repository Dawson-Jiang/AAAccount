package com.dawson.aaaccount.activity
import android.os.Bundle
import com.dawson.aaaccount.BuildConfig
import com.dawson.aaaccount.R
import com.dawson.aaaccount.util.ShareUtil
import kotlinx.android.synthetic.main.activity_about.*

/**
 * 关于界面 介绍应用的功能，意义和价值等
 *
 * @author dawson
 */
class AboutActivity : BaseActivity() {
    private val version: String
        get() {
            return BuildConfig.VERSION_NAME
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initCommonTitle()
        title = "关于AA账单"
        tv_version.text = version

        ShareUtil.shareQQContact(this, tv_contact_qq)
    }
}
