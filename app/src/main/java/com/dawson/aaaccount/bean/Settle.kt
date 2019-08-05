package com.dawson.aaaccount.bean

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 统计结算
 *
 * @author dawson
 */
open class Settle : BaseEntity() {
    var startDate: Date? = null
    var endDate: Date? = null
    /**
     * 结算操作用户
     */
    var creator: User? = null

    /**
     * 结算日期
     */
    var date: Date? = null

    /**
     * 总消费金额
     */
    var money: Double = 0.toDouble()
    /**
     * 所属家庭
     */
    var family: Family? = null
    @SerializedName("details")
    var settleDetails: MutableList<SettleDetail>? = null
    /**
     * 结算状态 0未结算 1已经结算 2结算中
     */
    var settled: Int = 0


    /**
     * 结算详情
     *
     * @author JD
     */
    open class SettleDetail : BaseEntity() {
        var user: User? = null
        /**
         * 付款金额
         */
        var pay: Double = 0.toDouble()
        /**
         * 消费金额
         */
        var consume: Double = 0.toDouble()
        /**
         * 结算金额 正数表示应收金额 负数表示应付金额
         */
         var settleMoney: Double = 0.toDouble()
        /**
         * 是否同意 0不同意 1同意 2等待同意
         */
        var agree: Int = 0

        var settle:Settle?=null
    }
}
