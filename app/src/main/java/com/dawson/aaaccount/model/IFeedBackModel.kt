package com.dawson.aaaccount.model

import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * Created by Dawson on 2017/8/17.
 */

interface IFeedBackModel {
    fun add(title: String, content: String ):Observable<OperateResult<Any>>
    fun getMyFeedback():Observable<OperateResult<List<Feedback>>>
}
