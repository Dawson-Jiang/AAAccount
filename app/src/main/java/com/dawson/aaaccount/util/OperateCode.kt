package com.dawson.aaaccount.util

/**
 * 异步操作码
 *
 * @author JD
 */
object OperateCode {

    /**
     * 注册
     */
    val REGIST = 11
    /**
     * 登录
     */
    val LOGIN = 12

    /**
     * 检测登录
     */
    val CHECK_LOGIN = 13
    /**
     * 添加
     */
    val ADD = 14
    /**
     * 修改
     */
    val MODIFIED = 15
    /**
     * 删除
     */
    val DELETE = 16
    /**
     * 获取 查询
     */
    val GET = 17
    /**
     * 加入家庭
     */
    val JOIN = 18

    /**
     * 获取家庭
     */
    val GET_FAMILY = 19

    /**
     * 获取类别
     */
    val GET_CATEGORY = 20

    /**
     * 退出登录
     */
    val LOGOUT = 21
    /**
     * 统计
     */
    val STATISTIC = 22
    /**
     * 结算
     */
    val SETTLE = 23

    /**
     * 查看详情 浏览
     */
    val SCAN = 24

    /**
     * 扫码
     */
    val SCAN_CODE = 25

    /**
     * 浏览 选择图片
     */
    val SELECT_PICTURE = 26
    /**
     * 浏览  4.4版本以上
     */
//    val SELECT_PICTURE_KK = 27

    /**
     * 拍照
     */
    val CAPTURE = 28

    /**
     * 定位
     */
    val LOCATION = 29
    /**
     * 退出家庭
     */
    val EXIT_FAMILY = 30

    /**
     * 修改个人信息 或者家庭信息
     */
    val MODIFIED_BASIC = 31
}
