package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.*
import kotlinx.android.synthetic.main.activity_feedback_list.*
import kotlinx.android.synthetic.main.layout_feedback_list_item.view.*

class FeedbackListActivity : BaseActivity() {

    private val feedBackModel = BaseModelFactory.factory.createFeedBackModel()
    internal val feedbacks = mutableListOf<Feedback>()

    private val feedbackAdapter = object : BaseAdapter() {
        override fun getCount(): Int {
            return feedbacks.size
        }

        override fun getItem(position: Int): Any {
            return feedbacks[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = layoutInflater.inflate(R.layout.layout_feedback_list_item, null)
            }
            val feedback = feedbacks[position]
            view?.tv_title?.text = feedback.title
            view?.tv_time?.text = feedback?.createTime?.format("yyyy.MM.dd")
            view?.tv_reply?.visibility = if (TextUtils.isEmpty(feedback.reply)) View.GONE else View.VISIBLE
            return view!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_list)

        initComponent()
        lv_feedback.adapter = feedbackAdapter
        // 初始化家庭
        initFeedback()
    }


    private fun initComponent() {
        initCommonTitle()
        srefreshRecord.setColorSchemeResources(R.color.colorPrimary)
        srefreshRecord.setOnRefreshListener { initFeedback() }
        lv_feedback.setOnItemClickListener { _, _, arg2, _ ->
            val intent = Intent()
            intent.putExtra("feedback", feedbacks[arg2])
            intent.setClass(this@FeedbackListActivity, FeedBackActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "我的反馈"

        enableOperate("添加") {
            startActivityForResult(Intent(this, FeedBackActivity::class.java), OperateCode.ADD)
        }
    }

    private fun initFeedback() {
        srefreshRecord.isRefreshing = true
        feedBackModel.getMyFeedback()
                .subscribe({ result -> onGetFeedback(result) }, {
                    onGetFeedback(OperateResult(ErrorCode.FAIL, it.message!!))
                    DLog.error("feedback_init", it)
                })
    }


    private fun onGetFeedback(result: OperateResult<List<Feedback>>) {
        srefreshRecord.isRefreshing = false
        feedbacks.clear()
        if (result.result == ErrorCode.SUCCESS) {
            feedbacks.clear()
            feedbacks.addAll(result.content!!)
        } else {
            Common.showErrorInfo(this@FeedbackListActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
        feedbackAdapter.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK)
            if (requestCode == OperateCode.ADD) {
                initFeedback()
            } else feedbackAdapter.notifyDataSetChanged()
        }
    }
}
