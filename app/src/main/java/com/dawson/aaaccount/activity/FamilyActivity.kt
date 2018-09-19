package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_family.*
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.layout_family_list_item.view.*

class FamilyActivity : BaseActivity() {
    private val familyModel =  BaseModelFactory.factory.createFamilyModel()

    private val familyAdapter = object : BaseAdapter() {
        override fun getCount(): Int {
            return CommonCach.families.size
        }

        override fun getItem(position: Int): Any {
            return CommonCach.families[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = layoutInflater.inflate(R.layout.layout_family_list_item, null)
            }
            val family = CommonCach.families[position]
            view?.tvName?.text = family.name + if (family.isTemp) "(临时)" else ""
            view?.tvMeberCount?.text = family.members?.size.toString()
            ImageLoadUtil.loadCircleImage(family.headThumbUrl, view?.ivPhoto!!)
            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family)
        initComponent()
        lvFamily.adapter = familyAdapter
        // 初始化家庭
        initFamily()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK)
            if (requestCode == OperateCode.ADD) {
                initFamily()
            } else familyAdapter.notifyDataSetChanged()
        }
    }

    private fun initComponent() {
        initCommonTitle()
        srefreshRecord.setColorSchemeResources(R.color.colorPrimary)
        srefreshRecord.setOnRefreshListener { initFamily() }
        lvFamily.setOnItemClickListener { _, _, arg2, _ ->
            editFamily(arg2, OperateCode.MODIFIED)
        }

        registerForContextMenu(lvFamily)
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "我的家庭"

        enableOperate("创建"){
            editFamily(0, OperateCode.ADD)
        }


//        nav_toolbar.setOnMenuItemClickListener { e ->
//            if (e.itemId == R.id.action_create) {
//                editFamily(0, OperateCode.ADD)
//            } else if (e.itemId == R.id.action_scan) {
//                editFamily(0, OperateCode.JOIN)
//            }
//            true
//        }
    }

    /**
     * 初始化家庭
     */
    private fun initFamily() {
        srefreshRecord.isRefreshing = true
        familyModel.getMyFamily().observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> onGetFamily(result) }, { DLog.error("family_init", it) })
    }

    private fun editFamily(index: Int, type: Int) {
        val intent = Intent()
        intent.putExtra("operateFlag", type)
        intent.putExtra("family_index", index)
        intent.setClass(this@FamilyActivity, EditFamilyActivity::class.java)
        startActivityForResult(intent, type)
    }

    private fun onGetFamily(result: OperateResult<List<Family>>) {
        srefreshRecord.isRefreshing = false
        CommonCach.families.clear()
        if (result.result == ErrorCode.SUCCESS) {
            CommonCach.families.clear()
            CommonCach.families.addAll(result.content!!)
            familyAdapter.notifyDataSetChanged()
        } else {
            Common.showErrorInfo(this@FamilyActivity, result.errorCode,
                    R.string.operate_fail, 0)
        }
        familyAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.family, menu)
        return true
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.add(0, 0, 0, "退出")
        menu.add(0, 1, 0, "删除")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item!!.menuInfo as AdapterView.AdapterContextMenuInfo
        if (item.itemId == 0) {
            disJoinFamily(info.position)
        } else if (item.itemId == 1) {
            delFamily(info.position)
        }
        return super.onContextItemSelected(item)
    }


    /**
     * 退出家庭
     */
    private fun disJoinFamily(index: Int) {
        AlertDialogHelper.showOKCancelAlertDialog(this, R.string.family_disjoin_notice, { _, _ ->
            mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                    this, R.string.handling)
            familyModel.disJoin(CommonCach.families[index])
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->
                        cancelDialog()
                        CommonCach.families.removeAt(index)
                        familyAdapter.notifyDataSetChanged()
                        Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                    }, {
                        cancelDialog()
                        Toast.makeText(this, R.string.operate_fail, Toast.LENGTH_SHORT).show()
                        DLog.error("family_disjoin", it)
                    })
        }, { _, _ -> })
    }

    /**
     * 删除家庭
     */
    private fun delFamily(index: Int) {
        AlertDialogHelper.showOKCancelAlertDialog(this, R.string.del_notice, { _, _ ->
            mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                    this, R.string.handling)
            familyModel.del(CommonCach.families[index])
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->
                        cancelDialog()
                        CommonCach.families.removeAt(index)
                        familyAdapter.notifyDataSetChanged()
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                    }, {
                        cancelDialog()
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                        DLog.error("family_del", it)
                    })
        }, { _, _ -> })
    }

}
