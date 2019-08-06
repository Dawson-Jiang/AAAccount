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

    var is_family = true

    override fun onBindViewHolder(holder: DaybookHolder, position: Int) {
        val dbook = mDayBooks[position]
        holder.view.tvMoney.text = dbook.money.toString()
        holder.view.tvType?.text = dbook.category!!.name

        holder.view.tvPayer?.text = if (is_family) dbook.payer!!.name else ""
        holder.view.tvDate?.text = "${dbook.date?.getWeekDay()} - ${dbook.date?.format("yyyy.MM.dd")}"
        holder.view.setOnClickListener { clickCallback(position) }
        holder.view.tv_settled.visibility = if (dbook.settle != null) View.VISIBLE else View.GONE
        // 异步下载图片
        val temp = dbook.thumbPictures
        if (temp != null && !temp.isEmpty()) {
            ImageLoadUtil.loadImage(dbook.thumbPictures!![0], holder.view.ivPicture!!)
        } else holder.view.ivPicture!!.setImageBitmap(null)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaybookHolder {
        val cv = mActivity.layoutInflater.inflate(
                R.layout.layout_daybook_list_item, parent, false)
        return DaybookHolder(cv)
    }
}

data class DaybookHolder(val view: View) : RecyclerView.ViewHolder(view) {

}