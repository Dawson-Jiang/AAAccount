package com.dawson.aaaccount.bean

import java.io.Serializable
import java.util.Date

/**
 * 实体基类
 *
 * @author JD
 */
open class BaseEntity : Serializable {
    var id: String? = null

    var description: String? = null
    /**
     * 创建时间
     */
    var createTime: Date? = null
    /**
     * 最后修改时间
     */
    var lastModifiedTime: Date? = null
}
