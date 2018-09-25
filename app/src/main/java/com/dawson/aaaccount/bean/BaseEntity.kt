package com.dawson.aaaccount.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

/**
 * 实体基类
 *
 * @author JD
 */
open class BaseEntity : Serializable {
    var id: String? = null

    @SerializedName("des")
    var description: String? = null
    /**
     * 创建时间
     */
    var createTime: Date? = null
    /**
     * 最后修改时间
     */
    @SerializedName("updateTime")
    var lastModifiedTime: Date? = null
}
