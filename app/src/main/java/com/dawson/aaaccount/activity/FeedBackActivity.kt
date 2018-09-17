package com.dawson.aaaccount.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.DLog
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.ShareUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_feed_back.*
import kotlinx.android.synthetic.main.common_title.*

class FeedBackActivity : BaseActivity() {
    private val fbModel =  BaseModelFactory.factory.createFeedBackModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        initCompent()
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "问题反馈"

        nav_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_ok) {
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                    Toast.makeText(this@FeedBackActivity, "标题或内容不能为空！", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener true
                }
                fbModel.add(title, content).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onAdd(it)
                        }, {
                            Common.showErrorInfo(this, ErrorCode.FAIL, R.string.operate_fail, 0)
                            DLog.error("feed_add", it)
                        })
            }
            true
        }
    }

    private fun initCompent() {
        initCommonTitle()
        ShareUtil.shareQQContact(this, tv_contact_qq)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ok_sure, menu)
        menu?.getItem(0)?.title = "提交"
        return super.onCreateOptionsMenu(menu)
    }

    fun onAdd(result: OperateResult<Any>) {
        if (result.result == ErrorCode.SUCCESS) {
            Toast.makeText(this@FeedBackActivity, "提交成功！", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Common.showErrorInfo(this, result.errorCode, R.string.operate_fail, 0)
        }
    }
}
