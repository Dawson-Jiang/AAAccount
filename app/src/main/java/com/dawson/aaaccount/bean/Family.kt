package com.dawson.aaaccount.bean

open class Family : BaseEntity() {
    var number: Int = 0

    var name: String? = null
    /**
     * 家庭成员
     */
    var members: MutableList<User>? = null
    /**
     * 头像url
     */
    var headUrl: String? = null
    /**
     * 头像缩略图url
     */
    var headThumbUrl: String? = null

    /**
     * 是否临时家庭
     */
    var isTemp: Boolean = false
}
