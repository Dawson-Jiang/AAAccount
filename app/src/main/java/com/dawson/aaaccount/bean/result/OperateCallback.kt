package com.dawson.aaaccount.bean.result

/**
 * Created by Administrator on 2017/5/27.
 */

interface OperateCallback<T> {
    fun callback(result: OperateResult<T>)
}
