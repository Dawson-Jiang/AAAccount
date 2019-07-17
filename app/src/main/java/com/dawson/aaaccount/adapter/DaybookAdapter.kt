package com.dawson.aaaccount.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
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
class DaybookAdapter(private val mActivity: Activity, private val mDayBooks: List<DayBook>) : Adapter<DaybookHolder>() {
    lateinit var clickCallback: (position: Int) -> Unit
    fun setClick(callback: (position: Int) -> Unit) {
        clickCallback = callback
    }

    override fun getItemCount(): Int {
        return mDayBooks.size
    }

    override fun onBindViewHolder(holder: DaybookHolder, position: Int) {
        val dbook = mDayBooks[position]
        holder?.view?.tvMoney?.text = dbook.money.toString()
        holder?.view?.tvType?.text = dbook.category!!.name
        holder?.view?.tvPayer?.text = dbook.payer!!.name
        holder?.view?.tvDate?.text = "${dbook.date?.getWeekDay()} - ${dbook.date?.format("yyyy.MM.dd")}"
        holder?.view?.setOnClickListener { if (clickCallback != null) clickCallback(position) }
        // 异步下载图片
        val temp = dbook.thumbPictures
        if (temp != null && !temp.isEmpty()) {
            ImageLoadUtil.loadImage(dbook.thumbPictures!![0], holder?.view?.ivPicture!!)
        } else holder?.view?.ivPicture!!.setImageBitmap(null)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaybookHolder {
        val cv = mActivity.layoutInflater.inflate(
                R.layout.layout_daybook_list_item, parent, false)
        return DaybookHolder(cv)
    }
//
//    @SuppressLint("NewApi")
//    override fun getView(index: Int, view: View?, vg: ViewGroup): View {
//        var cv = view
//        if (cv == null) {
//            cv = mActivity.layoutInflater.inflate(
//                    R.layout.layout_daybook_list_item, vg, false)
//        }
//        val dbook = mDayBooks[index]
//
//        cv?.tvMoney?.text = dbook.money.toString()
//        cv?.tvType?.text = dbook.category!!.name
//        cv?.tvPayer?.text = dbook.payer!!.name
//        cv?.tvDate?.text = "${dbook.date?.getWeekDay()} - ${dbook.date?.format("yyyy.MM.dd")}"
//        // 异步下载图片
//        val temp = dbook.thumbPictures
//        if (temp != null && !temp.isEmpty()) {
//            ImageLoadUtil.loadImage(dbook.thumbPictures!![0], cv?.ivPicture!!)
//        } else cv?.ivPicture!!.setImageBitmap(null)
//        return cv
//    }
}

data class DaybookHolder(val view: View) : RecyclerView.ViewHolder(view) {

}