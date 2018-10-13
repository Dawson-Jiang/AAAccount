package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_settle.*
import java.util.*

class SettleActivity : BaseActivity() {
    internal var family: Family? = null
    private var settle: Settle? = null
    private var flag: Int = 0//0 结算  1结算详情

    private val settleModel = BaseModelFactory.factory.createSettleModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settle)
        flag = intent.getIntExtra("flag", 0)
        if (flag == 1) {
            settle = intent.extras.get("settle") as Settle
        }
        initComponent()
        if (flag == 0) {
            layoutDate.visibility = View.GONE
            initFamily()
        } else {
            title = "结算详情"
            showSettle()
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
//        title = if (flag == 0) "结算-${family?.name!!}" else "结算详情"

        if (flag == 0)
            enableOperate("结算") {
                if (settle == null || settle?.settleDetails == null || settle?.settleDetails?.size!! <= 0) {
                    Toast.makeText(this, "没有需要结算数据", Toast.LENGTH_SHORT).show()
                    return@enableOperate
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
            }
    }

    private fun initComponent() {
        initCommonTitle()
        //TODO
//        tv_daybook.setOnClickListener {
//            val intent = Intent(this, SettleListActivity::class.java)
//            intent.putExtra("family", family)
//            startActivity(intent)
//        }
    }

    private fun showSettle() {
        val sdf = "yyyy.MM.dd"
        if (settle?.startDate != null && settle?.endDate != null) {
            tvTimeInterval!!.text = "${settle?.startDate?.format(sdf)} - ${settle?.endDate?.format(sdf)}"
        } else {
            tvTimeInterval!!.text = ""
        }
        if (flag == 1) {
            tvDate.text = sdf.format(settle?.date)
        }
        tvSettleMoney!!.text = settle?.money.toString()

        val detailMapList = ArrayList<Map<String, String>>()
        for (item in settle!!.settleDetails!!) {
            val detailMap = HashMap<String, String>()
            detailMap["consumer"] = item.user!!.name!!
            detailMap["payMoney"] = String.format("支付 %.2f", item.pay)
            detailMap["consumeMoney"] = String.format("消费 %.2f", item.consume)
            detailMap["settleMoney"] = String.format("%.2f", item.settleMoney)
            detailMapList.add(detailMap)
        }

        val sa = SimpleAdapter(this@SettleActivity, detailMapList,
                R.layout.layout_settle_detail_list_item, arrayOf("payMoney", "consumeMoney", "settleMoney", "consumer"),
                intArrayOf(R.id.tvPayMoney, R.id.tvConsumeMoney, R.id.tvSettleMoney, R.id.tvConsumer))
        lvDetail!!.visibility = View.VISIBLE
        tvNoDetail!!.visibility = View.GONE
        lvDetail!!.adapter = sa
    }


    private var selectedFamilyIndex: Int = 0
    private var families: MutableList<Family> = mutableListOf()
    private var familyNames: MutableList<String> = mutableListOf()

    private var familyModel: IFamilyModel = BaseModelFactory.factory.createFamilyModel()

    /**
     * 初始化家庭
     */
    private fun initFamily() {
        families.clear()
        familyNames.clear()
        selectedFamilyIndex = 0
        familyModel.getMyFamily()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { result ->
                    families.clear()
                    if (result.result == ErrorCode.SUCCESS) {
                        if (result.content != null && !result.content?.isEmpty()!!) {
                            families.addAll(result.content!!)
                            familyNames = families.indices.map {
                                families[it].name!!
                            }.toMutableList()
                        } else {
                            //显示无家庭提示
                            Toast.makeText(this, "您还没有家庭，请创建或加入一个家庭！", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        Common.showErrorInfo(this, result.errorCode,
                                R.string.operate_fail, 0)
                    }
                }
                .subscribe({
                    if (families.isEmpty()) {
                        //显示无家庭提示
                        Toast.makeText(this, "您还没有家庭，请创建或加入一个家庭！", Toast.LENGTH_LONG).show()
                        finish()
                    } else if (families.size == 1) {
                        family = families[selectedFamilyIndex]
                        title = "结算-${family?.name}"
                        refresh()
                    } else {
                        gotoSelectFamily();
                    }
                }, {
                    it.printStackTrace()
                    Common.showErrorInfo(this, ErrorCode.FAIL,
                            R.string.operate_fail, 0)
                })
    }

    private fun gotoSelectFamily() {
        if (families.isEmpty()) {
            Toast.makeText(this, "您还没有家庭，请创建或加入一个家庭！", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, BaseSimpleSelectActivity::class.java)
        intent.putExtra("select_string", familyNames.toTypedArray())
        intent.putExtra("select_index", selectedFamilyIndex)
        intent.putExtra("title", getString(R.string.select_family_title))
        startActivityForResult(intent, BaseSimpleSelectActivity.SELECT_FAMILY)
    }

    private fun refresh() {
        tvTimeInterval.text = "-"
        tvSettleMoney.text = "0.00"
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                R.string.handling)
        settleModel.statistic(family!!, null, null, false)
                .subscribe({ result -> onStatistic(result) }, { e ->
                    e.printStackTrace()
                    onStatistic(OperateResult(ErrorCode.FAIL, "操作失败"))
                })
    }

    private fun onStatistic(result: OperateResult<Settle>) {
        cancelDialog()
        if (result.result == ErrorCode.SUCCESS) {
            settle = result.content
            if (settle == null) {//无结算数据
                tvNoDetail.visibility = View.VISIBLE
                lvDetail.visibility = View.GONE
            } else {
                showSettle()
            }
        } else {
            Common.showErrorInfo(this@SettleActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    private fun onSettle(result: OperateResult<Any>) {
        cancelDialog()
        if (result.result == ErrorCode.SUCCESS) {
            refresh()
            Toast.makeText(this@SettleActivity, R.string.operate_success, Toast.LENGTH_SHORT).show()
        } else {
            Common.showErrorInfo(this@SettleActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BaseSimpleSelectActivity.SELECT_FAMILY) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFamilyIndex = data!!.getIntExtra("select_index", 0)
                family = families[selectedFamilyIndex]
                title = "结算-${family?.name}"
                refresh()
            }
        }
    }
}
