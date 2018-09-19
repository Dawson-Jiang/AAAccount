package com.dawson.aaaccount.activity

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dawson.aaaccount.R
import kotlinx.android.synthetic.main.activity_base_simple_select.*
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.layout_select_list_item.view.*

class BaseSimpleSelectActivity : BaseActivity() {

    private var selectStrings: Array<String>? = null
    private var selectIndexs: BooleanArray? = null
    private var selectIndex: Int = 0
    private var isMutilSelect: Boolean = false//是否多选

    private val adapter = object : BaseAdapter() {
        override fun getCount(): Int {
            return selectStrings!!.size
        }

        override fun getItem(i: Int): Any {
            return selectStrings!![i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var v2: View? = view
            if (v2 == null) {
                v2 = this@BaseSimpleSelectActivity.layoutInflater.inflate(R.layout.layout_select_list_item, null)
            }
            v2?.tv_name?.text = selectStrings!![i]
            if (isMutilSelect)
                v2?.iv_state?.visibility = if (selectIndexs!![i]) View.VISIBLE else View.GONE
            else
                v2?.iv_state?.visibility = if (selectIndex == i) View.VISIBLE else View.GONE
            return v2!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_simple_select)
        isMutilSelect = intent.getBooleanExtra("is_mutil_select", false)
        selectStrings = intent.extras.get("select_string") as Array<String>
        if (isMutilSelect)
            selectIndexs = intent.extras.get("select_index") as BooleanArray
        else
            selectIndex = intent.getIntExtra("select_index", 0)
        initComponent()
    }

    private fun initComponent() {
        initCommonTitle()
        lv_main!!.setOnItemClickListener { _, _, i, _ ->
            if (isMutilSelect)
                selectIndexs?.set(i, !selectIndexs!![i])
            else
                selectIndex = i
            adapter.notifyDataSetChanged()
        }
        lv_main!!.adapter = adapter
        adapter.notifyDataSetChanged()
        enableOperate(R.string.confirm) {
            if (isMutilSelect)
                intent.putExtra("select_index", selectIndexs)
            else
                intent.putExtra("select_index", selectIndex)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = intent.getStringExtra("title")
    }

    companion object {
        val SELECT_FAMILY = 1
        val SELECT_FAMILY_MEMBER = 2
        val SELECT_CATEGORY = 3
        val SELECT_FAMILY_PAYER = 4
    }
}
