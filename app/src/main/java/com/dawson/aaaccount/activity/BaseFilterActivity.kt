package com.dawson.aaaccount.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView

import com.dawson.aaaccount.R
import com.dawson.aaaccount.util.DawsonDatePickerDialog

import java.text.DateFormat
import java.util.Calendar
import java.util.Date

import com.dawson.aaaccount.activity.BaseSimpleSelectActivity.Companion
import kotlinx.android.synthetic.main.activity_base_filter.*
import kotlinx.android.synthetic.main.common_title.*

class BaseFilterActivity : BaseActivity() {
    private var startDate: Date
    private var endDate: Date

    private var familyNames: Array<String>? = null
    private var selectedFamilyIndex: Int = 0

    private var isContainSettle: Boolean = true

    init {
        val c = Calendar.getInstance()
        endDate = c.time
        c.add(Calendar.MONTH, -1)
        startDate = c.time
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_filter)
        familyNames = intent.extras.get("family_names") as Array<String>
        selectedFamilyIndex = intent.getIntExtra("family_select_index", 0)
        var td = intent.extras.get("start")
        if (td != null) startDate = td as Date
        td = intent.extras.get("end")
        if (td != null) endDate = td as Date
        isContainSettle = intent.getBooleanExtra("is_ct_settle", true)
        initComponent()
    }

    private fun initComponent() {
        initCommonTitle()

        tvFamily!!.text = familyNames!![selectedFamilyIndex]
        tvFamily!!.setOnClickListener { _ ->
            if (familyNames == null || familyNames!!.size <= 0)
                return@setOnClickListener
            val intent = Intent(this, BaseSimpleSelectActivity::class.java)
            intent.putExtra("select_string", familyNames)
            intent.putExtra("select_index", selectedFamilyIndex)
            intent.putExtra("title", getString(R.string.select_family_title))
            startActivityForResult(intent, Companion.SELECT_FAMILY)
        }

        tvStartDate!!.text = DateFormat.getDateInstance().format(startDate)
        tvStartDate!!.setOnClickListener { _ ->
            val c = Calendar.getInstance()
            c.time = startDate
            DawsonDatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, arg1, arg2, arg3 ->
                val c1 = Calendar.getInstance()
                c1.set(arg1, arg2, arg3)
                startDate = c1.time
                tvStartDate!!.text = DateFormat.getDateInstance().format(startDate)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                    .get(Calendar.DAY_OF_MONTH)).show()
        }

        tvEndDate!!.text = DateFormat.getDateInstance().format(endDate)
        tvEndDate!!.setOnClickListener { _ ->
            val c = Calendar.getInstance()
            c.time = endDate
            DawsonDatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, arg1, arg2, arg3 ->
                val c1 = Calendar.getInstance()
                c1.set(arg1, arg2, arg3)
                endDate = c1.time
                tvEndDate!!.text = DateFormat.getDateInstance().format(endDate)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                    .get(Calendar.DAY_OF_MONTH)).show()
        }

        if (!isContainSettle) rgContainSettle.check(R.id.rbCSNo)
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = "查询"
        nav_toolbar.setOnMenuItemClickListener { e ->
            if (e.itemId == R.id.action_ok) {
                intent.putExtra("select_index", selectedFamilyIndex)
                intent.putExtra("start", startDate)
                intent.putExtra("end", endDate)
                intent.putExtra("is_ct_settle", rgContainSettle.checkedRadioButtonId == R.id.rbCSYes )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ok_sure, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Companion.SELECT_FAMILY) {
            if (resultCode != Activity.RESULT_OK) return
            selectedFamilyIndex = data?.getIntExtra("select_index", 0)!!
            tvFamily!!.text = familyNames!![selectedFamilyIndex]
        }
    }
}
