package com.dawson.aaaccount.bean

/**
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
open class Feedback :BaseEntity(){
    var title: String? = null

    var content: String? = null

    var sessionId: String? = null

    var status: Byte? = null

    /**
     * 客服的回复
     */
    var reply: String? = null
}