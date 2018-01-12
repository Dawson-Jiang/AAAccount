package com.dawson.aaaccount.bean.result

import android.os.Message

import com.dawson.aaaccount.util.ErrorCode

/**
 * 所有网络接收结果基类
 *
 * @author JD
 */
class OperateResult<T> {

    /**
     * 服务端返回的结果 [com.dawson.aaaccount.util.ErrorCode] SUCCESS表示成功 FAIL表示失败
     * 如果失败查看 [.errorCode] 获得详细错误码和 [.errorMsg] 获取错误消息
     */
    /**
     * 获取操作结果
     *
     * @return 如果失败调用 [.getErrorCode] 获得详细错误码和 [.errorMsg] 获取错误消息
     */
    var result: Int = 0
    /**
     * 返回的错误码 值包含在 [com.dawson.aaaccount.util.ErrorCode]
     */
    var errorCode: Int = 0
    /**
     * 错误消息
     */
    var errorMsg: String? = null
    /**
     * 附件信息
     */
    var addInfo: String? = null

    /**
     * 获取实际的操作结果内容
     *
     * @return 如果{@link #result} 等于
     * [com.dawson.aaaccount.util.ErrorCode.SUCCESS] 返回真实的结果信息
     * 否则返回null
     */
    var content: T? = null

    constructor(con: T?) {
        result = ErrorCode.SUCCESS
        content = con
    }

    constructor(ecode: Int, mes: String) {
        errorCode = ecode
        errorMsg = mes
    }

    constructor()
}
