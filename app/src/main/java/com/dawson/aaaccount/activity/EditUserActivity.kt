package com.dawson.aaaccount.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.model.leancloud.FileModel
import com.dawson.aaaccount.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.android.synthetic.main.common_title.*

class EditUserActivity : BaseActivity() {
    private var photoChoose: PhotoChoose = PhotoChoose(this)
    private val userModel: IUserModel =  BaseModelFactory.factory.createUserModel()
    private val user: User = userModel.currentUser!!
    private val fileModel = FileModel()
    private var realPath: String? = ""//添加时选择头像使用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        iniComponent()
        showUser()
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "修改个人信息"
        nav_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_save) {
                save()
            }
            true
        }
    }

    private fun iniComponent() {
        initCommonTitle()

        ivHead.setOnClickListener { photoChoose.start() }
    }

    private fun showUser() {
        ImageLoadUtil.loadCircleImage(user.headUrl, ivHead)
        etName!!.setText(user.name)
    }

    private fun handleUpdateResult(result: OperateResult<User>) {
        cancelDialog()
        if (result.result == ErrorCode.SUCCESS) {
            user.name = result.content?.name
            Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            if (result.errorCode == 202) {
                Toast.makeText(this, "该名称已经被占用", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.operate_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun save() {
        val userTemp = User()
        userTemp.name = etName!!.text.toString()
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                R.string.handling)
        val obs: Observable<OperateResult<User>>
        if (!TextUtils.isEmpty(realPath)) {
            obs = fileModel.uploadFile(applicationContext, realPath!!, { _ -> })
                    .flatMap { res ->
                        userTemp.headUrl = res.content?.get(0)
                        userTemp.headThumbUrl = res.content?.get(1)
                        userModel.update(userTemp)
                    }
        } else {
            obs = userModel.update(userTemp)
        }
        obs.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res -> handleUpdateResult(res) },
                        { ex ->
                            DLog.error("eduser_save", ex)
                            handleUpdateResult(OperateResult(ErrorCode.FAIL, ex.message!!))
                        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OperateCode.CAPTURE ||
                requestCode == OperateCode.SELECT_PICTURE) {
            photoChoose.onActivityResult(requestCode, resultCode, data).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ b ->
                        realPath = FilePathConstants.getRealFilePath(applicationContext, b)!!
                        ImageLoadUtil.loadCircleImage(realPath, ivHead)
                    }, { ex ->
                        cancelDialog()
                        Toast.makeText(this@EditUserActivity, "操作失败",
                                Toast.LENGTH_SHORT).show()
                        DLog.error("eduser_photo_choose", ex)
                    })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return super.onCreateOptionsMenu(menu)
    }
}