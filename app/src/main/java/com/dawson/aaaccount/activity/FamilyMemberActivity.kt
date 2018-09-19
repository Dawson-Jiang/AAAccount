package com.dawson.aaaccount.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_family_member.*
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.layout_member_list_item.view.*

/**
 * 家庭成员
 */
class FamilyMemberActivity : BaseActivity() {

    private var family_index = 0
    private var family: Family? = null//家庭
    private val memberAdapter: MemberAdapter = MemberAdapter()
    private val userModel = BaseModelFactory.factory.createUserModel()
    private val familyModel = BaseModelFactory.factory.createFamilyModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_member)
        family_index = intent.getIntExtra("family_index", -1)
        family = CommonCach.families[family_index]
        initComponent()
        lvMember.adapter = memberAdapter
        memberAdapter.notifyDataSetChanged()
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "家庭成员 - ${family?.name}" + if (family?.isTemp!!) "(临时)" else ""
        enableOperate("添加") {
            editMemeber(null)
        }
    }

    private fun initComponent() {
        initCommonTitle()
        registerForContextMenu(lvMember)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 0, 0, "修改")
        menu?.add(0, 1, 0, "删除")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item!!.menuInfo as AdapterView.AdapterContextMenuInfo
        if (family?.members?.get(info.position) == userModel.currentUser) {
            return super.onContextItemSelected(item)
        }
        if (item.itemId == 0) {
//修改
            val user = family?.members?.get(info.position)!!
            editMemeber(user)
        } else if (item.itemId == 1) {
            AlertDialogHelper.showOKCancelAlertDialog(this@FamilyMemberActivity,
                    R.string.del_notice, { _, _ ->
                familyModel.delMemeber(family!!, family?.members?.get(info.position)!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ _ ->
                            Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                            family?.members?.removeAt(info.position)
                            memberAdapter.notifyDataSetChanged()
                            setResult(RESULT_OK)
                        }, { ex ->
                            Common.showErrorInfo(this, ErrorCode.FAIL, R.string.operate_fail, 0)
                            DLog.error("fm_del", ex)
                        })
            }, { _, _ -> })
        }
        return super.onContextItemSelected(item)
    }

    private fun editMemeber(user: User?) {
        val et_name = EditText(this)
        if (user == null)
            et_name.hint = "成员名称"
        else
            et_name.setText(user.name!!)
        AlertDialog.Builder(this).setTitle(if (user == null) "添加" else "修改")
                .setView(et_name)
                .setPositiveButton(R.string.save) { _, _ ->
                    val obs: io.reactivex.Observable<OperateResult<User>>
                    if (user == null) {
                        val usert = User()
                        usert.name = et_name.text.toString()
                        obs = familyModel.addMember(family!!, usert)
                    } else {
                        user.name = et_name.text.toString()
                        obs = familyModel.modifyMemeber(user)
                    }
                    obs.observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                                if (user == null) family?.members?.add(it.content!!)
                                memberAdapter.notifyDataSetChanged()
                                setResult(RESULT_OK)
                            }, {
                                Common.showErrorInfo(this, ErrorCode.FAIL,
                                        R.string.operate_fail, 0)
                                DLog.error("fm_ed_member", it)
                            })
                }
                .setNegativeButton(R.string.cancel, null).create().show()
    }


    inner class MemberAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return family?.members!!.size
        }

        override fun getItem(position: Int): Any {
            return family?.members!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var cv = convertView
            if (cv == null) {
                cv = layoutInflater.inflate(R.layout.layout_member_list_item, null)
            }
            val user = family?.members!![position]
            cv?.tvName?.text = user.name
            if (family?.isTemp!!) {
                cv?.tvPhone?.visibility = GONE
                cv?.ivHead?.visibility = GONE
            } else {
                cv?.tvPhone?.visibility = VISIBLE
                cv?.ivHead?.visibility = VISIBLE
                cv?.tvPhone?.text = user.phone
                // 异步下载图片
                ImageLoadUtil.loadCircleImage(user.headUrl, cv?.ivHead!!)
            }
            return cv!!
        }
    }
}
