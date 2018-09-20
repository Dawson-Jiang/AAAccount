package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.*
import com.dawson.qrlibrary.CaptureActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_edit_family.*

class EditFamilyActivity : BaseActivity() {
    private var operateFlag = OperateCode.ADD// 操作标记 * 创建  * 修改 * 查看、加入
    private var editFamily: Family? = null

    private var photoChoose: PhotoChoose = PhotoChoose(this)
    private var realPath: String? = ""//添加时选择头像使用

    private val familyModel = BaseModelFactory.factory.createFamilyModel()
    private val fileModel = BaseModelFactory.factory.createFileModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        operateFlag = intent.getIntExtra("operateFlag", OperateCode.ADD)
        setContentView(R.layout.activity_edit_family)
        initComponent()
        if (operateFlag == OperateCode.MODIFIED) {
            val fid = intent.getStringExtra("family_id")
            familyModel.getFamilyById(applicationContext, fid)
                    .subscribe {
                        if (it.result == ErrorCode.SUCCESS) {
                            editFamily = it.content
                            showFamily()
                        }
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OperateCode.SCAN_CODE) {
            if (resultCode == RESULT_OK) {//扫码成功 获取信息
                val id = data?.getStringExtra(CaptureActivity.RET_KEY)!!
                if (TextUtils.isEmpty(id)) {
                    finish()
                    return
                }
                mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                        this, R.string.handling)
                //到服务器获取家庭信息
                familyModel.getFamilyById(applicationContext, id)
                        .subscribe({ result ->
                            operateFlag = OperateCode.JOIN
                            etName.isEnabled = false
                            cancelDialog()
                            editFamily = result.content
                            title = "加入家庭"
                            enableOperate("加入") { joinFamily() }
                            ivHead.setOnClickListener {}
                            showFamily()
                        }, { ex ->
                            Toast.makeText(this, "获取家庭信息失败", Toast.LENGTH_SHORT).show()
                            finish()
                            DLog.error("edfamily_getFamilyById", ex)
                        })
            }
        } else if (requestCode == OperateCode.CAPTURE ||
                requestCode == OperateCode.SELECT_PICTURE) {
            photoChoose.onActivityResult(requestCode, resultCode, data).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ b ->
                        realPath = FilePathConstants.getRealFilePath(applicationContext, b)!!
                        ImageLoadUtil.loadCircleImage(realPath, ivHead)
                    }, { ex ->
                        Toast.makeText(this@EditFamilyActivity, "操作失败",
                                Toast.LENGTH_SHORT).show()
                        DLog.error("edfamily_choose_photo", ex)
                    })
        } else if (requestCode == 34 && resultCode == RESULT_OK) {
            showFamily()
            setResult(Activity.RESULT_OK)
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        when (operateFlag) {
            OperateCode.ADD -> {// 创建家庭
                title = "创建家庭"
            }
            OperateCode.MODIFIED -> {
                title = "修改家庭"
            }
        }
        enableOperate("保存") {
            when (operateFlag) {
                OperateCode.ADD -> saveFamily()
                OperateCode.MODIFIED -> saveFamily()
            }
        }
    }

    private fun initComponent() {
        initCommonTitle()
        tvQRCode.setOnClickListener {
            if (operateFlag == OperateCode.MODIFIED) {
                val intent = Intent(this, FamilyQRCodeActivity::class.java)
                intent.putExtra("family", editFamily)
                startActivity(intent)
            } else {
                val intent2 = Intent(this@EditFamilyActivity,
                        ScanCodeActivity::class.java)
                startActivityForResult(intent2, OperateCode.SCAN_CODE)
            }
        }
        tvMember.setOnClickListener {
            val intent = Intent(this, FamilyMemberActivity::class.java)
            intent.putExtra("family", editFamily)
            startActivityForResult(intent, 34)
        }

        when (operateFlag) {
            OperateCode.ADD -> {// 创建家庭
                tvQRCode.text = "扫码加入 >"
                layoutMember.visibility = View.GONE
                line4.visibility = View.GONE
                ivHead.setOnClickListener { _ -> photoChoose.start() }
            }

            OperateCode.MODIFIED -> {
                ivHead.setOnClickListener { _ -> photoChoose.start() }
            }
        }
    }

    /**
     * 显示编辑的家庭信息
     */
    private fun showFamily() {
        if (editFamily == null)
            return
        ImageLoadUtil.loadCircleImage(editFamily?.headThumbUrl, ivHead)
        etName.setText(editFamily?.name)
        tvMember.text = "${editFamily?.members?.size}人"
    }

    /**
     * 加入家庭
     */
    private fun joinFamily() {
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                this@EditFamilyActivity, R.string.handling)
        familyModel.join(editFamily!!).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    cancelDialog()
                    editFamily?.members?.add(UserModel().currentUser!!)
                    CommonLruCach.families.clear()
                    Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }, { ex ->
                    DLog.error("edfamily_join", ex)
                    cancelDialog()
                    Toast.makeText(this, R.string.operate_fail, Toast.LENGTH_SHORT).show()
                })
    }

    private fun saveFamily() {
        val name = etName.text.toString()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this@EditFamilyActivity, "名称不能为空",
                    Toast.LENGTH_SHORT).show()
            return
        }
        val familyTemp = Family()
        familyTemp.name = name
        if (operateFlag == OperateCode.MODIFIED)
            familyTemp.id = editFamily!!.id
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                this@EditFamilyActivity, R.string.handling)
        val obs: Observable<OperateResult<Family>>
        if (!TextUtils.isEmpty(realPath)) {
            obs = fileModel.uploadFile(applicationContext, realPath!!, { _ -> })
                    .flatMap { res ->
                        familyTemp.headUrl = res.content?.get(0)
                        familyTemp.headThumbUrl = res.content?.get(1)
                        if (operateFlag == OperateCode.ADD)
                            familyModel.create(applicationContext, familyTemp)
                        else familyModel.modify(applicationContext, familyTemp)
                    }
        } else {
            obs = if (operateFlag == OperateCode.ADD)
                familyModel.create(applicationContext, familyTemp)
            else familyModel.modify(applicationContext, familyTemp)
        }
        obs.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    cancelDialog()
                    CommonLruCach.families.clear()
                    Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }, { ex ->
                    cancelDialog()
                    Common.showErrorInfo(this, ErrorCode.FAIL,
                            R.string.operate_fail, 0)
                    DLog.error("edfamily_save", ex)
                })
    }
}
