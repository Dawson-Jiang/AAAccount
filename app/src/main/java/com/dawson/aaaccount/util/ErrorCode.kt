package com.dawson.aaaccount.util

/**
 * 系统操作错误码
 * @author JD
 */
object ErrorCode {
    /**
     * 操作成功
     */
    val SUCCESS = 1
    val FAIL = 0

    /**
     * token 过期
     */
    val TOKEN_OVERDUE = 2

    /**
     * 邮箱已存在
     */
    val EMAIL_EXIST = 11

    /**
     * 邮箱或密码错误
     */
    val EMAIL_OR_PWD_WRONG = 12

    /**
     * 家庭不存在或者密码错误
     */
    val FAMILY_OR_PWD_WRONG = 13

    /**
     * 已经是家庭成员 重复加入
     */
    val FAMILY_MEMBER_EXIST = 14
    /**
     * 文件上传失败
     */
    val FILE_UPLOAD_FAILED = 145

    /**
     * 未登录
     */
    val NOT_LOGIN = 201
    /**
     * 网络超时 断网
     */
    val NET_TIMEOUT = 202

    /**
     * 系统错误
     */
    val SYS_ERROR = 0xFF
}
