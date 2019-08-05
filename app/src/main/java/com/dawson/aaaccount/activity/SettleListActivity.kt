package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.util.AlertDialogHelper
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
    private val settleModel =  BaseModelFactory.factory.createSettleModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settle_list)
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
        initFamily()
    }

    private fun initComponent() {
        initCommonTitle()
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
                        title = "结算信息-${family?.name}"
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

    private  fun  refresh(){
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                R.string.handling)
        settleModel.getByFamilyId(family?.id!!).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onGetSettle(it) }, { onGetSettle(OperateResult()) })
    }

    private fun onGetSettle(result: OperateResult<List<Settle>>) {
        cancelDialog()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BaseSimpleSelectActivity.SELECT_FAMILY) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFamilyIndex = data!!.getIntExtra("select_index", 0)
                family = families[selectedFamilyIndex]
                title = "结算信息-${family?.name}"
                refresh()
            }
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
