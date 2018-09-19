package com.dawson.aaaccount.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_settle.*
import kotlinx.android.synthetic.main.common_title.*
import java.util.*

class SettleActivity : BaseActivity() {
    internal var family: Family? = null
    private val mHandler = Handler()
    private var settle: Settle? = null
    private var flag: Int = 0//0 结算  1结算详情
    private var hasSync = true

    private val settleModel = BaseModelFactory.factory.createSettleModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settle)
//        flag = intent.getIntExtra("flag", 0)
        flag = 1
        when (flag) {
            0 -> family = intent.extras.get("family") as Family
            1 -> settle = intent.extras.get("settle") as Settle
            else -> {
                finish()
                return
            }
        }
        initComponent()
        if (flag == 0) {
            title = family?.name!!
            val icon = resources.getDrawable(R.drawable.ic_swap_vert)
            val tintIcon = DrawableCompat.wrap(icon)
            DrawableCompat.setTintList(tintIcon, resources.getColorStateList(R.color.text_white))
            layoutDate.visibility = View.GONE
            refresh()
        } else {
            title = "结算详情"
            showSettle()
            btn_settle!!.visibility = View.GONE
            layoutSettleList.visibility = View.GONE
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = if (flag == 0) "结算-${family?.name!!}" else "结算详情"

        enableOperate(R.string.confirm) {
            val intent = Intent(this, SettleListActivity::class.java)
            intent.putExtra("family", family)
            startActivity(intent)
        }
    }

    private fun initComponent() {
        initCommonTitle()
        btn_settle.setOnClickListener { v -> onClick(v) }
    }

    private fun showSettle() {
        val sdf = "yyyy.MM.dd"
        if (settle?.startDate != null && settle?.endDate != null) {
            tvTimeInterval!!.text = "${settle?.startDate?.format(sdf)} - ${settle?.endDate?.format(sdf)}"
        } else {
            tvTimeInterval!!.text = ""
        }
        if (flag == 1) {
            (findViewById(R.id.tvDate) as TextView).text = sdf.format(settle?.date)
        }
        tvSettleMoney!!.text = settle?.money.toString()

        if (settle!!.settleDetails != null && settle!!.settleDetails!!.size > 0) {
            val detailMapList = ArrayList<Map<String, String>>()
            for (item in settle!!.settleDetails!!) {
                val detailMap = HashMap<String, String>()
                detailMap.put("consumer", item.user!!.name!!)
                detailMap.put("payMoney", String.format("支付 %.2f", item.pay))
                detailMap.put("consumeMoney", String.format("消费 %.2f", item.consume))
                detailMap.put("settleMoney", String.format("%.2f", item.settle))
                detailMapList.add(detailMap)
            }

            val sa = SimpleAdapter(this@SettleActivity, detailMapList,
                    R.layout.layout_settle_detail_list_item, arrayOf("payMoney", "consumeMoney", "settleMoney", "consumer"),
                    intArrayOf(R.id.tvPayMoney, R.id.tvConsumeMoney, R.id.tvSettleMoney, R.id.tvConsumer))
            lvDetail!!.visibility = View.VISIBLE
            tvNoDetail!!.visibility = View.GONE
            lvDetail!!.adapter = sa
            btn_settle!!.isEnabled = true
        } else {
            lvDetail!!.visibility = View.GONE
            tvNoDetail!!.visibility = View.VISIBLE
            btn_settle!!.isEnabled = false
        }
    }

    private fun refresh() {
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this@SettleActivity,
                R.string.handling)
//        settleModel.statistic(family!!, null, null).observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result -> onStatistic(result) }, { e ->
//                    e.printStackTrace()
//                    onStatistic(OperateResult(ErrorCode.FAIL, "操作失败"))
//                })
    }

    fun onStatistic(result: OperateResult<Settle>) {
        if (mProgressDialog != null && mProgressDialog!!.isShowing)
            mProgressDialog!!.cancel()
        if (result.result == ErrorCode.SUCCESS) {
            settle = result.content
            showSettle()
        } else {
            Common.showErrorInfo(this@SettleActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    private fun onSettle(result: OperateResult<Any>) {
        if (mProgressDialog != null && mProgressDialog!!.isShowing)
            mProgressDialog!!.cancel()
        if (result.result == ErrorCode.SUCCESS) {
            refresh()
            Toast.makeText(this@SettleActivity, R.string.operate_success, Toast.LENGTH_SHORT).show()
        } else {
            Common.showErrorInfo(this@SettleActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    fun onSyncCompleted(result: OperateResult<Any>) {
        mHandler.post {
            if (mProgressDialog != null && mProgressDialog!!.isShowing)
                mProgressDialog!!.cancel()
            if (result.result == ErrorCode.SUCCESS) {
                hasSync = true
                refresh()
                Toast.makeText(this@SettleActivity, R.string.operate_success, Toast.LENGTH_SHORT).show()
            } else {
                Common.showErrorInfo(this@SettleActivity, result.errorCode,
                        R.string.operate_fail, 0)
            }
        }
    }

    fun onClick(v: View) {
        //TODO 修改
//        if (v.id == R.id.btn_settle) {
//            mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
//                    R.string.handling)
//            settleModel.syncData(applicationContext, true).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe { result -> onSyncCompleted(result) }
//        } else

        if (v.id == R.id.btn_settle) {
            if (!hasSync) {
                Toast.makeText(this@SettleActivity, "请先点击右上角同步按钮同步数据在结算", Toast.LENGTH_SHORT).show()
                return
            }
            mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                    R.string.handling)
            settle!!.creator = UserModel().currentUser
            settle!!.date = Date()
            settleModel.settle(settle!!).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result -> onSettle(result) }, {
                        DLog.e("settle", it.message!!)
                        onSettle(OperateResult(ErrorCode.FAIL, it.message!!))
                    })
        } else if (v.id == R.id.layoutSettleList) {
            val intent = Intent(this, SettleListActivity::class.java)
            intent.putExtra("family", family)
            startActivity(intent)
        }
    }
}
