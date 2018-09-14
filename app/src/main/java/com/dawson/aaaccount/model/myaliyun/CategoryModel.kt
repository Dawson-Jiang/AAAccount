package com.dawson.aaaccount.model.myaliyun

import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.ICategoryModel
import com.dawson.aaaccount.net.DaybookService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.net.UserService
import io.reactivex.Observable

/**
 * 类别管理
 * Created by dawson on 2017/5/24.
 */
class CategoryModel : ICategoryModel {
    private val service = RetrofitHelper.getService(DaybookService::class.java)

    override fun get(): Observable<OperateResult<List<ConsumptionCategory>>> {
        return service.getCategories()
    }
}
