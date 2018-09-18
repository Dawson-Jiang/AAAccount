package com.dawson.aaaccount.bean

import com.google.gson.annotations.SerializedName
import java.util.Date

open class DayBook : BaseEntity() {
    /**
     * 消费金额
     */
    var money: Double = 0.toDouble()

    /**
     * 消费类别
     */
    var category: ConsumptionCategory? = null

    /**
     * 创建人
     */
    @SerializedName("recorder")
    var creator: User? =null

    /**
     * 所属家庭 null表示属于creator
     */
    var family: Family? = null

    /**
     * 付款人
     */
    var payer: User? = null

    /**
     * 消费人员
     */
    var customers: MutableList<User>? = null

    /**
     * 消费日期
     */
    var date: Date? = null

    /**
     * 图片url
     */
    var pictures: MutableList<String>? = null
    /**
     * 缩略图片url
     */
    var thumbPictures: MutableList<String>? = null

    /**
     * 是否结算 0未结算 1已经结算
     */
    var settled: Int = 0
}
