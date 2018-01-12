package com.dawson.aaaccount.util

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker

import com.dawson.aaaccount.R

import java.lang.reflect.Field

/**
 * Created by dawson on 2017/4/28.
 */

class DawsonDatePickerDialog : DatePickerDialog {

    constructor(context: Context, callBack: DatePickerDialog.OnDateSetListener, year: Int, monthOfYear: Int, dayOfMonth: Int) : super(context, callBack, year, monthOfYear, dayOfMonth) {}

    constructor(context: Context, theme: Int, listener: DatePickerDialog.OnDateSetListener, year: Int,
                monthOfYear: Int, dayOfMonth: Int) : super(context, theme, listener, year, monthOfYear, dayOfMonth) {
    }

    override fun show() {
        super.show()
        setDividerColor()
        /*修改按钮颜色这个必须在show或者create方法后面*/
        val commitBtn = getButton(DialogInterface.BUTTON_POSITIVE) //确认按钮
        commitBtn.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    fun setDividerColor() {
        val datePicker = datePicker
        // 获取 mSpinners
        val llFirst = datePicker.getChildAt(0) as LinearLayout

        // 获取 NumberPicker
        val mSpinners = llFirst.getChildAt(0) as LinearLayout
        for (i in 0 until mSpinners.childCount) {
            val view = mSpinners.getChildAt(i) as? NumberPicker ?: continue
            val pickerFields = NumberPicker::class.java.declaredFields
            for (pf in pickerFields) {
                if (pf.name == "mSelectionDivider") {
                    pf.isAccessible = true
                    try {
                        pf.set(view, ColorDrawable(context.resources.getColor(R.color.colorPrimary)))
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                    break
                }
            }
        }

    }
}
