package com.dawson.aaaccount.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.leancloud.SettleModel
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.format
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_settle_list.*
import kotlinx.android.synthetic.main.layout_settle_list_item.view.*

class SettleListActivity : BaseActivity() {
    private val settles: MutableList<Settle> = mutableListOf()
    private val settleAdapter: SettleAdapter = SettleAdapter()
    internal var family: Family? = null
    private val settleModel = SettleModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settle_list)
        family = intent.getSerializableExtra("family") as Family
        initComponent()
        lvSettle.adapter = settleAdapter
        lvSettle.setOnItemClickListener { _, _, arg2, _ ->
            val settle = settles[arg2]
            val intent = Intent()
            intent.putExtra("flag", 1)
            intent.putExtra("settle", settle)
            intent.setClass(this@SettleListActivity, SettleActivity::class.java)
            startActivity(intent)
        }
        settleModel.getByFamilyId(family?.id!!).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onGetSettle(it) }, { onGetSettle(OperateResult()) })
    }

    private fun initComponent() {
        initCommonTitle()
        title = "结算信息"
    }

    private fun onGetSettle(result: OperateResult<List<Settle>>) {
        if (result.result == ErrorCode.SUCCESS) {
            settles.clear()
            settles.addAll(result.content!!)
            settleAdapter.notifyDataSetChanged()
            if (settles.size <= 0) {
                lvSettle!!.visibility = View.GONE
                tvNoData!!.visibility = View.VISIBLE
            } else {
                lvSettle!!.visibility = View.VISIBLE
                tvNoData!!.visibility = View.GONE
            }
        } else {
            Common.showErrorInfo(this@SettleListActivity, result.errorCode, R.string.operate_fail, 0)
        }
    }

    inner class SettleAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return settles.size
        }

        override fun getItem(position: Int): Any {
            return settles[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, cv: View?, parent: ViewGroup): View {
            var convertView = cv
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.layout_settle_list_item, null)
            }
            val settle = settles[position]
            val sdf = "yyyy.MM.dd"
            if (settle.startDate != null && settle.endDate != null) {
                convertView?.tvDateInterval!!.text = "${settle.startDate?.format(sdf)} - ${settle.endDate?.format(sdf)}"
            } else {
                convertView?.tvDateInterval!!.text = ""
            }
            convertView.tvMoney!!.text = String.format("%.2f", settle.money)
            return convertView!!
        }
    }
}
