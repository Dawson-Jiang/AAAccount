package com.dawson.aaaccount.bean

import com.google.gson.annotations.SerializedName

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
    @SerializedName("headPic")
    var headUrl: String? = null
    /**
     * 头像缩略图url
     */
    var headThumbUrl: String? = null

    /**
     * 是否是家庭成员
     */
    var isMember:Boolean =false

    override fun equals(other: Any?): Boolean {
        return if (other is User) {
            id == other.id
        } else
            false
    }
}
