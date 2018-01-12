package com.dawson.aaaccount.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.dawson.aaaccount.bean.result.OperateResult

import java.net.Proxy

object JsonHelper {
    /**
     * 将json格式转换成指定OperateResult对象
     *
     * @param json
     * @return
     */
    fun <T> getOperateResult(json: String): OperateResult<T>? {
        return JSON.parseObject(json, object : TypeReference<OperateResult<T>>() {

        })
    }

    fun getString(obj: Any): String {
        return JSON.toJSONString(obj)
    }
}
