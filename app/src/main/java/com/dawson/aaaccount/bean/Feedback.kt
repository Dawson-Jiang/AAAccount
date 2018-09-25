package com.dawson.aaaccount.bean

/**
 * <p>project: AAAccount </p>
 * <p>des: [问题反馈] </p>
 * <p>date: 2018/9/25 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
open class Feedback : BaseEntity() {
    var title: String? = null

    var content: String? = null

    var sessionId: String? = null

    var status: Byte? = null

    /**
     * 客服的回复
     */
    var reply: String? = null
}