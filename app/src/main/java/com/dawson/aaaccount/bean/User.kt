package com.dawson.aaaccount.bean

open class User : BaseEntity() {
    var number: Int = 0
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var token: String? = null
    var phone: String? = null
    /**
     * 头像url
     */
    var headUrl: String? = null
    /**
     * 头像缩略图url
     */
    var headThumbUrl: String? = null

    override fun equals(other: Any?): Boolean {
        return if (other is User) {
            id == other.id
        } else
            false
    }
}
