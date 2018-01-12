package com.dawson.aaaccount.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.util.ImageLoadUtil
import com.dawson.aaaccount.util.format
import com.dawson.aaaccount.util.getWeekDay
import kotlinx.android.synthetic.main.layout_daybook_list_item.view.*

/**
 * 账单列表适配器
 * Created by dawson on 2017/2/22.
 */
class DaybookAdapter(private val mActivity: Activity, private val mDayBooks: List<DayBook>) : BaseAdapter() {

    override fun getCount(): Int {
        return mDayBooks.size
    }

    override fun getItem(arg0: Int): Any? {
        return null
    }

    override fun getItemId(arg0: Int): Long {
        return arg0.toLong()
    }

    @SuppressLint("NewApi")
    override fun getView(index: Int, view: View?, vg: ViewGroup): View {
        var cv = view
        if (cv == null) {
            cv = mActivity.layoutInflater.inflate(
                    R.layout.layout_daybook_list_item, vg, false)
        }
        val dbook = mDayBooks[index]

        cv?.tvMoney?.text = dbook.money.toString()
        cv?.tvType?.text = dbook.category!!.name
        cv?.tvPayer?.text = dbook.payer!!.name
        cv?.tvDate?.text = "${dbook.date?.getWeekDay()} - ${dbook.date?.format("yyyy.MM.dd")}"
        // 异步下载图片
        val temp = dbook.thumbPictures
        if (temp != null && !temp.isEmpty()) {
            ImageLoadUtil.loadImage(dbook.thumbPictures!![0], cv?.ivPicture!!)
        } else cv?.ivPicture!!.setImageBitmap(null)
        return cv
    }
}