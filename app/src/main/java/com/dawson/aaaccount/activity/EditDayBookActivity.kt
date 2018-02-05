package com.dawson.aaaccount.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.activity.BaseSimpleSelectActivity.Companion.SELECT_FAMILY_PAYER
import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.*
import com.dawson.aaaccount.model.leancloud.*
import com.dawson.aaaccount.model.leancloud.DayBookModel
import com.dawson.aaaccount.model.leancloud.FamilyModel
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_day_book.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditDayBookActivity : BaseActivity() {
    private var dayBookModel: IDayBookModel = DayBookModel()
    private var fileMode = FileModel()
    private var familyModel: IFamilyModel = FamilyModel()
    private var categoryModel: ICategoryModel = CategoryModel()
    private var userModel: IUserModel = UserModel()
    // 家庭
    private val families = mutableListOf<Family>()
    private var familyNames = listOf<String>()
    private var selectedFamilyIndex: Int = 0// 0表示个人消费 1表示家庭消费
    //    private var flag: Int = 0// 0表示个人消费 1表示家庭消费
    // 消费类别
    private val categories = mutableListOf<ConsumptionCategory>()
    private var categoryNames = listOf<String>()
    private var selectedCategoryIndex: Int = 0

    private var selectedPayerIndex: Int = 0
    // 消费人员
    private val consumers = mutableListOf<User>()
    private var selectedConsumers: BooleanArray = booleanArrayOf()
    private var consumerStrs = listOf<String>()
    // 日期
    private var selectedDate: Date = Date()
    // 照片
    private var currentIndex = -1

    /**
     * 添加时,缩略图和预览图都是本地图片存放在selectedPic和uploadPic
     * 修改时，缩略图是服务器缩略图和本地图片，存放在selectedPic；预览图是服务器原图和本地图片，分别存放在orgPic和uploadPic
     * 需要上传的图片存都放在uploadPic
     */
    private val selectedPic: Array<String?> = arrayOfNulls(3)
    //服务器原图 用于预览
    private val orgPic: Array<String?> = arrayOfNulls(3)
    private val thumPic: Array<String?> = arrayOfNulls(3)
    private val photoChoose: PhotoChoose = PhotoChoose(this)
    //需要上传的图片
    private var uploadPic: Array<String?> = arrayOfNulls(3)

    private var daybookId: String = ""
    private var is_modify: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_day_book)
        if (intent.getStringExtra("daybook_id") != null)
            daybookId = intent.getStringExtra("daybook_id")
        is_modify = !TextUtils.isEmpty(daybookId)
        initComponent()

        familyModel.getMyFamily().observeOn(AndroidSchedulers.mainThread())
                .doOnNext { initFamily(it) }.observeOn(Schedulers.io())
                .flatMap { categoryModel.get() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { initCategory(it) }
                .observeOn(Schedulers.io())
                .flatMap {
                    if (is_modify)
                        dayBookModel.getById(daybookId).observeOn(AndroidSchedulers.mainThread())
                                .map { initDayBook(it.content!!) }
                    else Observable.just("")
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    cancelDialog()
                    if (!is_modify) et_money.requestFocus()
                    et_remark.clearFocus()
                }, { ex ->
                    cancelDialog()
                    Common.showErrorInfo(this, ErrorCode.FAIL,
                            R.string.operate_fail, 0)
                    DLog.error("edbook_getMyFamily", ex)
                })
    }

    private fun initDayBook(editDayBook: DayBook) {
        et_money.setText(editDayBook.money.toString(), TextView.BufferType.NORMAL)
        if (editDayBook.family == null) {
            tv_family.text = userModel.currentUser!!.name
            tv_consumer.text = userModel.currentUser!!.name
            selectedFamilyIndex = 0
        } else {
            tv_family.text = editDayBook.family?.name
            families.forEachIndexed { index, family ->
                if (family.id == editDayBook.family?.id) {
                    selectedFamilyIndex = index
                    initMemberList(family)
                    return@forEachIndexed
                }
            }
            val stringBuilder = StringBuilder()
            editDayBook.customers?.forEach {
                stringBuilder.append(it.name)
                stringBuilder.append(" ")
                consumers.forEachIndexed { index, user0 ->
                    if (user0.id == it.id) {
                        selectedConsumers[index] = true
                        return@forEachIndexed
                    }
                }
            }
            tv_consumer.text = stringBuilder.toString()
        }
        tv_category.text = editDayBook.category?.name
        categories.forEachIndexed { index, cc ->
            if (editDayBook.category?.id == cc.id) {
                selectedCategoryIndex = index
                return@forEachIndexed
            }
        }
        tv_payer.text = editDayBook.payer?.name
        tv_date.text = editDayBook.date?.format("yyyy年MM月dd日")
        selectedDate = editDayBook.date!!
        et_remark.setText(editDayBook.description, TextView.BufferType.NORMAL)

        if (editDayBook.thumbPictures != null && editDayBook.thumbPictures?.size!! > 0) {
            currentIndex = -1
            editDayBook.thumbPictures?.forEach {
                setPicture(it)
                orgPic[currentIndex] = editDayBook.pictures?.get(currentIndex)
                thumPic[currentIndex] = it
            }
        }
    }

    private fun initFamily(result: OperateResult<List<Family>>) {
        val sf = Family()// 自己作为虚拟家庭
        sf.id = userModel.currentUser!!.id
        sf.name = userModel.currentUser!!.name
        families.add(sf)
        families.addAll(result.content!!)
        familyNames = families.indices.map {
            if (it == 0) "自己"
            else families[it].name!! + if (families[it].isTemp) "(临时)" else ""
        }.toList()
        selectedFamilyIndex = 0
        tv_family.text = familyNames[0]
        initMemberList(families[0])
    }

    private fun initCategory(result: OperateResult<List<ConsumptionCategory>>) {
        if (result.result == ErrorCode.SUCCESS) {
            categories.clear()
            categories.addAll(result.content!!)
            categoryNames = categories.indices.map { categories[it].name }.toList()
            selectedCategoryIndex = 0
            tv_category.text = categoryNames[selectedCategoryIndex]
        }
    }

    /**
     * 初始化家庭成员列表
     *
     * @param family 家庭
     * @param flag   0 表示自己 1表示一个家庭
     */
    private fun initMemberList(family: Family) {
        if (selectedFamilyIndex == 0) { // 消费人员设置为自己
            tv_consumer.text = family.name
            tv_payer.text = family.name
        } else {
            consumers.clear()
            consumers.addAll(family.members!!)
            tv_consumer.text = ""
            selectedConsumers = BooleanArray(consumers.size)
            consumerStrs = consumers.indices.map { consumers[it].name!! }.toList()
            selectedPayerIndex = 0
            tv_payer.text = consumerStrs[0]
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = if (is_modify) "修改账单" else "添加账单"
        nav_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_save) {
                save()
            }
            true
        }
    }

    private fun initComponent() {
        initCommonTitle()

        tv_family.setOnClickListener { _ ->
            val intent = Intent(this, BaseSimpleSelectActivity::class.java)
            intent.putExtra("select_string", familyNames.toTypedArray())
            intent.putExtra("select_index", selectedFamilyIndex)
            intent.putExtra("title", getString(R.string.select_family_title))
            startActivityForResult(intent, BaseSimpleSelectActivity.SELECT_FAMILY)
        }

        tv_category.setOnClickListener { _ ->
            val intent = Intent(this, BaseSimpleSelectActivity::class.java)
            intent.putExtra("select_string", categoryNames.toTypedArray())
            intent.putExtra("select_index", selectedCategoryIndex)
            intent.putExtra("title", getString(R.string.select_type_title))
            startActivityForResult(intent, BaseSimpleSelectActivity.SELECT_CATEGORY)
        }

        tv_payer.setOnClickListener { _ ->
            if (selectedFamilyIndex == 0) return@setOnClickListener
            val intent = Intent(this, BaseSimpleSelectActivity::class.java)
            intent.putExtra("select_string", consumerStrs.toTypedArray())
            intent.putExtra("select_index", selectedPayerIndex)
            intent.putExtra("is_mutil_select", false)
            intent.putExtra("title", getString(R.string.select_payer_title))
            startActivityForResult(intent, SELECT_FAMILY_PAYER)
        }

        tv_consumer.setOnClickListener { _ ->
            if (selectedFamilyIndex == 0)
                return@setOnClickListener
            val intent = Intent(this, BaseSimpleSelectActivity::class.java)
            intent.putExtra("select_string", consumerStrs.toTypedArray())
            intent.putExtra("select_index", selectedConsumers)
            intent.putExtra("is_mutil_select", true)
            intent.putExtra("title", getString(R.string.select_member_title))
            startActivityForResult(intent, BaseSimpleSelectActivity.SELECT_FAMILY_MEMBER)
        }

        tv_date.text = DateFormat.getDateInstance(DateFormat.LONG).format(selectedDate)
        tv_date.setOnClickListener { _ ->
            val c = Calendar.getInstance()
            c.time = selectedDate
            DawsonDatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, arg1, arg2, arg3 ->
                val c1 = Calendar.getInstance()
                c1.set(arg1, arg2, arg3)
                selectedDate = c1.time
                tv_date.text = DateFormat.getDateInstance().format(selectedDate)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                    .get(Calendar.DAY_OF_MONTH)).show()
        }
        ll_pic.getChildAt(3).setOnClickListener { _ -> photoChoose.start() }

        for (i in 0..2) {
            val rl = ll_pic.getChildAt(i) as RelativeLayout
            rl.getChildAt(0).setOnClickListener { previewPic(i) }
            rl.getChildAt(1).setOnClickListener {
                //删除
                selectedPic[i] = null
                (rl.getChildAt(0) as ImageView).setImageBitmap(null)
                rl.visibility = View.GONE
                if (is_modify) {
                    uploadPic[i] = null
                    orgPic[i] = null
                    thumPic[i] = null
                }
                currentIndex--
                if (currentIndex >= 0)
                    (ll_pic.getChildAt(currentIndex) as RelativeLayout).getChildAt(1).visibility = View.VISIBLE
                ll_pic.getChildAt(3).visibility = View.VISIBLE
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BaseSimpleSelectActivity.SELECT_FAMILY) {
            if (resultCode != RESULT_OK) return
            selectedFamilyIndex = data?.getIntExtra("select_index", 0)!!
            tv_family.text = families[selectedFamilyIndex].name
            // 初始化家庭成员
            initMemberList(families[selectedFamilyIndex])
        } else if (requestCode == SELECT_FAMILY_PAYER) run {
            if (resultCode != RESULT_OK) return
            selectedPayerIndex = data?.getIntExtra("select_index", 0)!!
            tv_payer.text = consumerStrs[selectedPayerIndex]
        }
        else if (requestCode == BaseSimpleSelectActivity.SELECT_FAMILY_MEMBER) {
            if (resultCode != RESULT_OK) return
            selectedConsumers = data?.extras?.get("select_index") as BooleanArray
            val temp = StringBuilder()
            for (i in selectedConsumers.indices) {
                if (selectedConsumers[i]) {
                    temp.append(consumerStrs[i])
                    temp.append("  ")
                }
            }
            tv_consumer.text = temp
        } else if (requestCode == BaseSimpleSelectActivity.SELECT_CATEGORY) {
            if (resultCode != RESULT_OK) return
            selectedCategoryIndex = data?.getIntExtra("select_index", 0)!!
            tv_category.text = categories[selectedCategoryIndex]
                    .name
        } else if (requestCode == OperateCode.CAPTURE ||
                requestCode == OperateCode.SELECT_PICTURE) {
            photoChoose.onActivityResult(requestCode, resultCode, data)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ b ->
                        setPicture(b)
                    }, { ex ->
                        Toast.makeText(this@EditDayBookActivity, "操作失败",
                                Toast.LENGTH_SHORT).show()
                        DLog.error("edbook_choose_photo_onActivityResult", ex)
                    })
        }
    }

    private fun previewPic(index: Int) {
        val intent = Intent()
        intent.setClass(this, PreviewPictureActivity::class.java)
        if (is_modify) {
            val temp = mutableListOf<String>()//合并orgPic和uploadPic
            temp.addAll(orgPic.filterNotNull())
            temp.addAll(uploadPic.filterNotNull())
            intent.putExtra("urls", temp.toTypedArray())
        } else
            intent.putExtra("urls", selectedPic.filterNotNull().toTypedArray())
        intent.putExtra("index", index)
        startActivity(intent)
    }

    /**
     * 显示图片
     */
    private fun setPicture(arg0: Uri) {
        currentIndex++
        selectedPic[currentIndex] = FilePathConstants.getRealFilePath(applicationContext, arg0)
        uploadPic[currentIndex] = selectedPic[currentIndex]
        ll_pic.getChildAt(currentIndex).visibility = View.VISIBLE
        if (currentIndex > 0)
            (ll_pic.getChildAt(currentIndex - 1) as RelativeLayout).getChildAt(1).visibility = View.INVISIBLE
        ImageLoadUtil.loadImage(selectedPic[currentIndex], (ll_pic.getChildAt(currentIndex) as RelativeLayout).getChildAt(0) as ImageView)

        if (currentIndex >= 2) {
            ll_pic.getChildAt(3).visibility = View.INVISIBLE
        } else {
            ll_pic.getChildAt(3).visibility = View.VISIBLE
        }
    }

    /**
     * 显示服务器图片 初始化调用
     */
    private fun setPicture(url: String) {
        currentIndex++
        selectedPic[currentIndex] = url
        ll_pic.getChildAt(currentIndex).visibility = View.VISIBLE
        if (currentIndex > 0)
            (ll_pic.getChildAt(currentIndex - 1) as RelativeLayout).getChildAt(1).visibility = View.INVISIBLE
        ImageLoadUtil.loadImage(selectedPic[currentIndex], (ll_pic.getChildAt(currentIndex) as RelativeLayout).getChildAt(0) as ImageView)

        if (currentIndex >= 2) {
            ll_pic.getChildAt(3).visibility = View.INVISIBLE
        } else {
            ll_pic.getChildAt(3).visibility = View.VISIBLE
        }
    }

    /**
     * 保存
     */
    fun save() {
        if (et_money.text.toString() == "") {
            Toast.makeText(this, "消费金额不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        var money: Double

        try {
            money = java.lang.Double.valueOf(et_money.text.toString())!!
            money = Common.convertDouble(money, 2)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "消费金额格式错误", Toast.LENGTH_SHORT).show()
            return
        }

        val daybook = DayBook()
        if (is_modify) daybook.id = daybookId
        daybook.money = money
        daybook.creator = userModel.currentUser
        daybook.category = categories[selectedCategoryIndex]

        if (selectedFamilyIndex > 0)
            daybook.payer = consumers[selectedPayerIndex]
        else
            daybook.payer = userModel.currentUser

        if (selectedFamilyIndex > 0) {
            daybook.family = families[selectedFamilyIndex]
            daybook.customers = ArrayList()
            selectedConsumers.indices
                    .filter { selectedConsumers[it] }
                    .mapTo(daybook.customers!!) { consumers[it] }
        }

        daybook.pictures = mutableListOf()
        daybook.thumbPictures = mutableListOf()
        if (is_modify) {
            daybook.pictures?.addAll(orgPic.filterNotNull())
            daybook.thumbPictures?.addAll(thumPic.filterNotNull())
        }

        daybook.settled = 0
        daybook.date = selectedDate// 消费日期
        daybook.description = et_remark.text.toString()
        mProgressDialog = AlertDialogHelper.showWaitProgressDialog(this,
                R.string.handling)

        val obs: Observable<OperateResult<Any>>
        val upp = uploadPic.filterNotNull()
        obs = if (upp.isNotEmpty()) {
            fileMode.uploadFile(applicationContext, upp.toMutableList(),
                    { _, _ -> })
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { res ->
                        for (i in 0 until upp.size) {
                            val pics = res.content?.get(upp[i])
                            daybook.pictures?.add(pics?.get(0)!!)
                            daybook.thumbPictures?.add(pics?.get(1)!!)
                        }
                        OperateResult<Any>("")
                    }
        } else {
            Observable.just(OperateResult<Any>(""))
        }

        obs.observeOn(Schedulers.io())
                .flatMap { dayBookModel.save(applicationContext, daybook) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ handleSaveResult() },
                        { ex ->
                            cancelDialog()
                            Common.showErrorInfo(this, ErrorCode.FAIL, R.string.operate_fail)
                            DLog.error("edbook_save", ex)
                        })
    }

    private fun handleSaveResult() {
        cancelDialog()
        AlertDialogHelper.showOKAlertDialog(this,
                R.string.operate_success, null)
        setResult(RESULT_OK)
        if (is_modify) {
            finish()
        } else {
            et_money.setText("")
            et_remark.setText("")

            //clear pic
            currentIndex = -1
            for (i in 0..2) {
                selectedPic[i] = null
                ll_pic.getChildAt(i).visibility = View.GONE
            }
            ll_pic.getChildAt(3).visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }
}
