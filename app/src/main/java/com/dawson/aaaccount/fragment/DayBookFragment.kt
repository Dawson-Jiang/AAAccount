package com.dawson.aaaccount.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.activity.BaseFragment
import com.dawson.aaaccount.activity.BaseSimpleSelectActivity
import com.dawson.aaaccount.activity.BaseSimpleSelectActivity.Companion.SELECT_FAMILY
import com.dawson.aaaccount.activity.EditDayBookActivity
import com.dawson.aaaccount.adapter.DaybookAdapter
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.model.IDayBookModel
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.model.leancloud.DayBookModel
import com.dawson.aaaccount.model.leancloud.FamilyModel
import com.dawson.aaaccount.model.leancloud.UserModel
import com.dawson.aaaccount.util.AlertDialogHelper
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.OperateCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_daybook.view.*
import java.util.*

class DayBookFragment : BaseFragment() {
    private var selectedFamilyIndex: Int = 0
    private var families: MutableList<Family> = ArrayList()
    private var familyNames: List<String> = listOf()

    private var mDayBooks: MutableList<DayBook> = ArrayList()
    private var currentPage = -1
    private val limit = 10
    private var mDaybookAdapter: DaybookAdapter? = null

    private var familyModel: IFamilyModel = FamilyModel()
    private var dayBookModel: IDayBookModel = DayBookModel()
    private var userModel: IUserModel = UserModel()

    private var tempDayBook: DayBook? = null//正在处理的记录  如编辑 删除等

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDaybookAdapter = DaybookAdapter(activity, mDayBooks)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_daybook, null)
        initComponent()
        rootView?.lvRecord?.adapter = mDaybookAdapter
        rootView?.lvRecord?.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent()
            intent.setClass(activity, EditDayBookActivity::class.java)
            intent.putExtra("daybook_id", mDayBooks[position].id)
            startActivityForResult(intent, OperateCode.MODIFIED)
        }
        registerForContextMenu(rootView?.rootView?.lvRecord!!)
        initFamily()
        // 初始化记录列表
        refreshDayBook()
        return rootView
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.add("删除")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item!!.menuInfo as AdapterView.AdapterContextMenuInfo
        if (item.itemId == 0) {
            //删除
            tempDayBook = mDayBooks[info.position]
            AlertDialogHelper.showOKCancelAlertDialog(activity,
                    R.string.del_notice, { _, _ ->
                mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                        activity, R.string.handling)
                dayBookModel.delete(tempDayBook?.id!!)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .doOnError {
                            cancelDialog()
                            Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show()
                        }
                        .subscribe { res ->
                            cancelDialog()
                            if (res.result == ErrorCode.SUCCESS) {
                                Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                                refreshDayBook()
                            } else {
                                Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show()
                            }
                        }
            }, { _, _ -> })
        }
        return super.onContextItemSelected(item)
    }

    private fun initComponent() {
        rootView?.refRecord?.setColorSchemeResources(R.color.colorPrimary)
        rootView?.refRecord?.setOnRefreshListener { refreshDayBook() }
        rootView?.refRecord?.setLoadMoreListener { loadMoreDayBook() }
    }

    fun gotoSelectFamily() {
        val intent = Intent(activity, BaseSimpleSelectActivity::class.java)
        intent.putExtra("select_string", familyNames.toTypedArray())
        intent.putExtra("select_index", selectedFamilyIndex)
        intent.putExtra("title", getString(R.string.select_family_title))
        startActivityForResult(intent, SELECT_FAMILY)
    }

    fun gotoAdd() {
        startActivityForResult(Intent(activity, EditDayBookActivity::class.java), OperateCode.ADD)
    }

    /**
     * 初始化家庭
     */
    private fun initFamily() {
        families = ArrayList()
        val f = Family()// 自己作为虚拟家庭
        f.id = userModel.currentUser!!.id
        f.name = userModel.currentUser!!.name
        families.add(f)
        selectedFamilyIndex = 0
        familyNames = listOfNotNull(f.name)
        familyModel.getMyFamily()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { _ ->
                    Common.showErrorInfo(activity, ErrorCode.FAIL,
                            R.string.operate_fail, 0)
                }
                .subscribe { result ->
                    if (result.result == ErrorCode.SUCCESS) {
                        families.addAll(result.content!!)
                        familyNames = families.indices.map {
                            if (it == 0) "自己"
                            else
                                families[it].name!! + if (families[it].isTemp) "(临时)" else ""
                        }.toList()
                        activity.title = "账单-自己"
                    } else {
                        Common.showErrorInfo(activity, result.errorCode,
                                R.string.operate_fail, 0)
                    }
                }
    }

    /**
     * 刷新列表
     */
    private fun refreshDayBook() {
        currentPage = -1
        rootView?.refRecord?.isRefreshing = true
        val fid = if (selectedFamilyIndex == 0) "" else families[selectedFamilyIndex].id
        dayBookModel[fid!!, currentPage, limit]
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    handleDayBook(result)
                }, { _ ->
                    handleDayBook(OperateResult(ErrorCode.FAIL, "操作失败"))
                })
    }

    /**
     * 加载更多
     */
    private fun loadMoreDayBook() {
        val fid = if (selectedFamilyIndex == 0) "" else families[selectedFamilyIndex].id
        dayBookModel[fid!!, currentPage + 1, limit]
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    handleDayBook(result)
                }, { _ ->
                    handleDayBook(OperateResult(ErrorCode.FAIL, "操作失败"))
                })
    }

    /**
     * 处理和显示获取的记录
     */
    private fun handleDayBook(result: OperateResult<List<DayBook>>) {
        rootView?.refRecord?.isRefreshing = false
        rootView?.refRecord?.setLoading(false)
        if (result.result == ErrorCode.SUCCESS) {
            currentPage++
            if (currentPage == 0) mDayBooks.clear()
            mDayBooks.addAll(result.content!!)
            rootView?.refRecord?.isNeedLoadMore = (result.content!!.size > limit)
            mDaybookAdapter?.notifyDataSetChanged()
            if (mDayBooks.size <= 0) {
                rootView?.lvRecord?.visibility = View.GONE
                rootView?.tvNoData?.visibility = View.VISIBLE
            } else {
                rootView?.lvRecord?.visibility = View.VISIBLE
                rootView?.tvNoData?.visibility = View.GONE
            }
        } else if (result.errorCode == ErrorCode.TOKEN_OVERDUE) {
            userModel.loginTimeOut(activity)
        } else {
            Common.showErrorInfo(activity,
                    result.errorCode, R.string.operate_fail, 0)
        }
    }

    /**
     * 修改个人信息等基础信息后 刷新
     */
    fun refreshBasicInfo() {
        initFamily()
        refreshDayBook()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OperateCode.MODIFIED || requestCode == OperateCode.ADD) {
            if (resultCode == Activity.RESULT_OK) {
                refreshDayBook()
            }
        } else if (requestCode == SELECT_FAMILY) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFamilyIndex = data!!.getIntExtra("select_index", 0)
                activity.title = "账单-${familyNames[selectedFamilyIndex]}"
                refreshDayBook()
            }
        }
    }
}
