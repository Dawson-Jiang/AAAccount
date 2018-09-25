package com.dawson.aaaccount.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_feed_back.* 

class FeedBackActivity : BaseActivity() {
    private val fbModel = BaseModelFactory.factory.createFeedBackModel()

    private var feedback: Feedback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        if (intent.extras != null) feedback = intent.extras["feedback"] as? Feedback
        initCompent()
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = if (feedback == null) "问题反馈" else "我的反馈"

        if (feedback == null)
            enableOperate("提交") {
                add()
            }
    }

    private fun initCompent() {
        initCommonTitle()
//        ShareUtil.shareQQContact(this, tv_contact_qq)
        if (feedback != null) {
            etTitle.setText(feedback?.title, TextView.BufferType.NORMAL)
            etTitle.isEnabled = false
            etTitle.clearFocus()
            etContent.setText(feedback?.content, TextView.BufferType.NORMAL)
            etContent.isEnabled = false
            etContent.clearFocus()
            sv_reply.visibility = View.VISIBLE
            tv_time.text = "反馈时间:" + feedback?.createTime?.format("yyyy.MM.dd")
            if (!TextUtils.isEmpty(feedback?.reply)) {
                tv_reply_t.text = "客户回复: " + feedback?.lastModifiedTime?.format("yyyy.MM.dd")
                tv_reply.text = feedback?.reply
            } else {
                tv_reply_t.text = "客户回复: 未回复"
                tv_reply.text = ""
            }
        }else  sv_reply.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ok_sure, menu)
        menu?.getItem(0)?.title = "提交"
        return super.onCreateOptionsMenu(menu)
    }

    fun add() {
        val title = etTitle.text.toString()
        val content = etContent.text.toString()
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            Toast.makeText(this@FeedBackActivity, "标题或内容不能为空！", Toast.LENGTH_SHORT).show()
            return
        }
        fbModel.add(title, content).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onAdd(it)
                }, {
                    Common.showErrorInfo(this, ErrorCode.FAIL, R.string.operate_fail, 0)
                    DLog.error("feed_add", it)
                })
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
