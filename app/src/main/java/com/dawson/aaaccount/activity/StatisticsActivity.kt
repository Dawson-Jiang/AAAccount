package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.common_title.*
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

    private var hasSync = false

    private val familyModel =  BaseModelFactory.factory.createFamilyModel()
    private val userModel =  BaseModelFactory.factory.createUserModel()
    private val settleModel =  BaseModelFactory.factory.createSettleModel()

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

        nav_toolbar.setOnMenuItemClickListener {
            when {
                it.itemId == R.id.action_filter -> {
                    val intent = Intent(this, BaseFilterActivity::class.java)
                    intent.putExtra("family_names", familyNames.toTypedArray())
                    intent.putExtra("family_select_index", selectedFamilyIndex)
                    intent.putExtra("is_ct_settle", isContainSettle)
                    intent.putExtra("start", startTime)
                    intent.putExtra("end", endTime)
                    startActivityForResult(intent, FILTER)
                }
//                it.itemId == R.id.action_settle -> {
//                    if (selectedFamilyIndex <= 0) return@setOnMenuItemClickListener true
//                    val intent = Intent(this, SettleActivity::class.java)
//                    intent.putExtra("family", families[selectedFamilyIndex])
//                    startActivityForResult(intent, FILTER)
//                }
                it.itemId == R.id.action_sync -> {
                    mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                            R.string.handling)
                    settleModel.syncData(this.applicationContext, false).observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result -> onSyncCompleted(result) }, { onSyncCompleted(OperateResult()) })
                }
            }
            true
        }
    }

    private fun initComponent() {
        initCommonTitle()

        btn_settle.setOnClickListener {
            if (selectedFamilyIndex == 0) return@setOnClickListener
            if (!hasSync) {
                AlertDialogHelper.showOKCancelAlertDialog(this@StatisticsActivity,
                        R.string.settle_notice2, { _, _ -> }, { _, _ -> })
                return@setOnClickListener
            }
            if (isContainSettle) {
                AlertDialogHelper.showOKCancelAlertDialog(this@StatisticsActivity,
                        R.string.settle_notice, { _, _ -> }, { _, _ -> })
                return@setOnClickListener
            }
            if (settle == null || settle?.settleDetails == null || settle?.settleDetails?.size!! <= 0) {
                Toast.makeText(this@StatisticsActivity, "没有需要结算数据", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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

        tvHistorySettle.setOnClickListener {
            if (selectedFamilyIndex == 0) return@setOnClickListener
            val intent = Intent(this, SettleListActivity::class.java)
            intent.putExtra("family", families[selectedFamilyIndex])
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.statistics, menu)
        return true
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
                .subscribe ({ result -> onGetFamily(result) },{ onGetFamily(OperateResult()) })
    }

    private fun showStatistic() {
        val sdf = "yyyy.MM.dd"
        tvTimeInterval.text = "${settle?.startDate?.format(sdf)} - ${settle?.endDate?.format(sdf)}"
        tvSettleMoney.text = settle?.money.toString()

        if (settle != null && settle?.settleDetails != null && settle?.settleDetails?.isNotEmpty()!!) {
            val detailMapList = ArrayList<Map<String, String>>()
            for (item in settle?.settleDetails!!) {
                val detailMap = HashMap<String, String>()
                detailMap.put("consumer", item.user?.name!!)
                detailMap.put("payMoney", String.format("支付 %.2f", item.pay))
                detailMap.put("consumeMoney", String.format("消费 %.2f", item.consume))
                detailMap.put("settleMoney", String.format("%.2f", item.settle))
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

    private fun onSyncCompleted(result: OperateResult<Any>) {
        cancelDialog()
        if (result.result == ErrorCode.SUCCESS) {
            hasSync = true
            refresh()
            Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
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
        if (selectedFamilyIndex <= 0) nav_toolbar.menu.getItem(1).isEnabled = false
    }

    private fun onSettle(result: OperateResult<Any>) {
        if (mProgressDialog != null && mProgressDialog!!.isShowing)
            mProgressDialog!!.cancel()
        if (result.result == ErrorCode.SUCCESS) {
            refresh()
            Toast.makeText(this@StatisticsActivity, R.string.operate_success, Toast.LENGTH_SHORT).show()
        } else {
            Common.showErrorInfo(this@StatisticsActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILTER) {
            if (resultCode != Activity.RESULT_OK) return
            selectedFamilyIndex = data?.getIntExtra("select_index", 0)!!
            title = "统计-${families[selectedFamilyIndex].name}"
            startTime = data.extras.get("start") as Date
            endTime = data.extras.get("end") as Date
            isContainSettle = data?.getBooleanExtra("is_ct_settle", true)!!
            nav_toolbar.menu.getItem(1).isEnabled = selectedFamilyIndex > 0
            refresh()
        }
    }

    companion object {
        val FILTER = 4
    }
}
