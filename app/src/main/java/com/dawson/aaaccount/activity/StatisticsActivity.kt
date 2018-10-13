package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.AlertDialogHelper
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.format
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StatisticsActivity : BaseActivity() {
    private val families: MutableList<Family> = ArrayList()
    private var familyNames: List<String> = listOf()
    private var selectedFamilyIndex: Int = 0

    private var settle: Settle? = null

    private var startTime: Date? = null
    private var endTime: Date? = null
    private var isContainSettle: Boolean = true

    private val familyModel = BaseModelFactory.factory.createFamilyModel()
    private val userModel = BaseModelFactory.factory.createUserModel()
    private val settleModel = BaseModelFactory.factory.createSettleModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_statistics)

        initComponent()
        initFamily()
        settleModel.statisticMine(null, null).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> onStatistic(result) }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "统计(自己)"

        enableIvRight {
            val intent = Intent(this, BaseFilterActivity::class.java)
            intent.putExtra("family_names", familyNames.toTypedArray())
            intent.putExtra("family_select_index", selectedFamilyIndex)
            intent.putExtra("is_ct_settle", isContainSettle)
            intent.putExtra("start", startTime)
            intent.putExtra("end", endTime)
            startActivityForResult(intent, FILTER)
        }
    }

    private fun initComponent() {
        initCommonTitle()
    }

    /**
     * 初始化家庭
     */
    private fun initFamily() {
        val f = Family()// 自己作为虚拟家庭
        f.id = userModel.currentUser!!.id
        f.name = userModel.currentUser!!.name
        families.add(f)
        familyNames = listOfNotNull("自己")
        selectedFamilyIndex = 0
        familyModel.getMyFamily().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> onGetFamily(result) }, { onGetFamily(OperateResult()) })
    }

    private fun showStatistic() {
        val sdf = "yyyy.MM.dd"
        tvTimeInterval.text = "${settle?.startDate?.format(sdf)} - ${settle?.endDate?.format(sdf)}"
        tvSettleMoney.text = settle?.money.toString()

        if (settle != null && settle?.settleDetails != null && settle?.settleDetails?.isNotEmpty()!!) {
            val detailMapList = ArrayList<Map<String, String>>()
            for (item in settle?.settleDetails!!) {
                val detailMap = HashMap<String, String>()
                detailMap["consumer"] = item.user?.name!!
                detailMap["payMoney"] = String.format("支付 %.2f", item.pay)
                detailMap["consumeMoney"] = String.format("消费 %.2f", item.consume)
                detailMap["settleMoney"] = String.format("%.2f", item.settleMoney)
                detailMapList.add(detailMap)
            }

            val sa = SimpleAdapter(this, detailMapList,
                    R.layout.layout_settle_detail_list_item, arrayOf("payMoney", "consumeMoney", "settleMoney", "consumer"),
                    intArrayOf(R.id.tvPayMoney, R.id.tvConsumeMoney, R.id.tvSettleMoney, R.id.tvConsumer))
            lvDetail!!.visibility = View.VISIBLE
            tvNoDetail!!.visibility = View.GONE
            lvDetail!!.adapter = sa
        } else {
            lvDetail!!.visibility = View.GONE
            tvNoDetail!!.visibility = View.VISIBLE
        }
    }

    private fun refresh() {
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                R.string.handling)
        val obs = if (selectedFamilyIndex == 0) settleModel.statisticMine(startTime, endTime)
        else settleModel.statistic(families[selectedFamilyIndex], startTime, endTime, isContainSettle)
        obs.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onStatistic(result)
                }, { e ->
                    e.printStackTrace()
                    onStatistic(OperateResult(ErrorCode.FAIL, ""))
                })
    }

    private fun onStatistic(result: OperateResult<Settle>) {
        cancelDialog()
        if (result.result == ErrorCode.SUCCESS) {
            settle = result.content
            showStatistic()
        } else {
            Common.showErrorInfo(this, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    private fun onGetFamily(result: OperateResult<List<Family>>) {
        if (result.result == ErrorCode.SUCCESS) {
            families.addAll(result.content!!)
            familyNames = families.indices.map {
                if (it == 0) "自己"
                else families[it].name!! + if (families[it].isTemp) "(临时)" else ""
            }.toList()
        } else {
            Common.showErrorInfo(this, result.errorCode,
                    "加载失败", 0)
        }
     }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILTER) {
            if (resultCode != Activity.RESULT_OK) return
            selectedFamilyIndex = data?.getIntExtra("select_index", 0)!!
            title = "统计-${families[selectedFamilyIndex].name}"
            startTime = data.extras.get("start") as Date
            endTime = data.extras.get("end") as Date
            isContainSettle = data.getBooleanExtra("is_ct_settle", true)
             refresh()
        }
    }

    companion object {
        const val FILTER = 4
    }
}
