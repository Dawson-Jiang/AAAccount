package com.dawson.aaaccount.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
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
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.model.IDayBookModel
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.util.AlertDialogHelper
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.OperateCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_daybook.view.*
import java.util.*

class DayBookFragment : BaseFragment() {
    private var selectedFamilyIndex: Int = 0
    private var families: MutableList<Family> = mutableListOf()
    private var familyNames: MutableList<String> = mutableListOf()

    private var mDayBooks: MutableList<DayBook> = ArrayList()
    private var currentPage = -1
    private val limit = 10
    private lateinit var mDaybookAdapter: DaybookAdapter

    private var familyModel: IFamilyModel = BaseModelFactory.factory.createFamilyModel()
    private var dayBookModel: IDayBookModel = BaseModelFactory.factory.createDayBookModel()
    private var userModel: IUserModel = BaseModelFactory.factory.createUserModel()

    private var tempDayBook: DayBook? = null//正在处理的记录  如编辑 删除等

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDaybookAdapter = DaybookAdapter(activity as Activity, mDayBooks)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_daybook, null)
        initComponent()
        rootView?.lvRecord?.adapter = mDaybookAdapter
        rootView?.lvRecord?.layoutManager = LinearLayoutManager(activity)
        rootView?.lvRecord?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL))
        mDaybookAdapter.setClick { position ->
            val intent = Intent()
            intent.setClass(activity, EditDayBookActivity::class.java)
            intent.putExtra("daybook_id", mDayBooks[position].id)
            if (is_family) intent.putExtra("family", families[selectedFamilyIndex])
            startActivityForResult(intent, OperateCode.MODIFIED)
        }
        registerForContextMenu(rootView?.rootView?.lvRecord!!)
//       if(is_family) initFamily()
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
            AlertDialogHelper.showOKCancelAlertDialog(activity as Context,
                    R.string.del_notice, { _, _ ->
                mProgressDialog = AlertDialogHelper.showWaitProgressDialog(
                        activity as Context, R.string.handling)
                dayBookModel.delete(tempDayBook?.id!!)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ res ->
                            cancelDialog()
                            if (res.result == ErrorCode.SUCCESS) {
                                Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                                refreshDayBook()
                            } else {
                                Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show()
                            }
                        }, {
                            cancelDialog()
                            Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show()
                        })
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
        if (!is_family) return
        if (families.isEmpty()) {
            Toast.makeText(activity, "您还没有家庭，请创建或加入一个家庭！", Toast.LENGTH_LONG).show()
            return //只有自己 无需选择
        }

        val intent = Intent(activity, BaseSimpleSelectActivity::class.java)
        intent.putExtra("select_string", familyNames.toTypedArray())
        intent.putExtra("select_index", selectedFamilyIndex)
        intent.putExtra("title", getString(R.string.select_family_title))
        startActivityForResult(intent, SELECT_FAMILY)
    }

    fun gotoAdd() {
        val intent = Intent()
        intent.setClass(activity, EditDayBookActivity::class.java)
        if (is_family) intent.putExtra("family", families[selectedFamilyIndex])
        startActivityForResult(intent, OperateCode.ADD)
    }

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
                    if (result.result == ErrorCode.SUCCESS) {
                        if (result.content != null && !result.content?.isEmpty()!!) {
                            families.addAll(result.content!!)
                            familyNames = families.indices.map {
                                families[it].name!!
                            }.toMutableList()
                            activity?.title = "家庭账单-${familyNames[0]}"
                        } else {
                            //显示无家庭提示
                            Toast.makeText(activity, "您还没有家庭，请创建或加入一个家庭！", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Common.showErrorInfo(activity as Activity, result.errorCode,
                                R.string.operate_fail, 0)
                    }
                }
                .subscribe({
                    if (!families.isEmpty()) refreshDayBook()
                }, {
                    Common.showErrorInfo(activity as Activity, ErrorCode.FAIL,
                            R.string.operate_fail, 0)
                    it.printStackTrace()
                })
    }

    /**
     * 刷新列表
     */
    private fun refreshDayBook() {
        currentPage = -1
        rootView?.refRecord?.isRefreshing = true
        val fid = if (is_family) families[selectedFamilyIndex].id else ""
        dayBookModel[fid!!, currentPage + 1, limit]
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
        val fid = if (is_family) families[selectedFamilyIndex].id else ""
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
            if (result.content != null)
                mDayBooks.addAll(result.content!!)
            rootView?.refRecord?.isNeedLoadMore = (result.content!!.size > limit)
            mDaybookAdapter.is_family = is_family
            mDaybookAdapter.notifyDataSetChanged()
            if (mDayBooks.size <= 0) {
                rootView?.lvRecord?.visibility = View.GONE
                rootView?.tvNoData?.visibility = View.VISIBLE
            } else {
                rootView?.lvRecord?.visibility = View.VISIBLE
                rootView?.tvNoData?.visibility = View.GONE
            }
        } else if (result.errorCode == ErrorCode.TOKEN_OVERDUE) {
            userModel.loginTimeOut(activity as Activity)
        } else {
            Common.showErrorInfo(activity as Activity,
                    result.errorCode, R.string.operate_fail, 0)
        }
    }

    /**
     * 修改个人信息等基础信息后 刷新
     */
    fun refreshBasicInfo() {
        if (is_family) initFamily()
        else refreshDayBook()
    }

    var is_family = false

    fun switchFamily(family: Boolean) {
        if (is_family == family) return
        is_family = family
        if (is_family) activity?.title = "家庭账单"
        else activity?.title = "我的账单"
        refreshBasicInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OperateCode.MODIFIED || requestCode == OperateCode.ADD) {
            if (resultCode == Activity.RESULT_OK) {
                refreshDayBook()
            }
        } else if (requestCode == SELECT_FAMILY) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFamilyIndex = data!!.getIntExtra("select_index", 0)
                activity?.title = "家庭账单-${familyNames[selectedFamilyIndex]}"
                refreshDayBook()
            }
        }
    }
}
