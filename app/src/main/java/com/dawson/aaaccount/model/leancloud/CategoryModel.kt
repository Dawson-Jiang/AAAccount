package com.dawson.aaaccount.model.leancloud

import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.ICategoryModel
import com.dawson.aaaccount.model.leancloud.bean.withAVCategories
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * 类别管理
 * Created by dawson on 2017/5/24.
 */
class CategoryModel : ICategoryModel {
    override fun get(): Observable<OperateResult<List<ConsumptionCategory>>> {
        return Observable.create<List<AVObject>> { e ->
            val query = AVQuery<AVObject>(DataObjectHelper.CONSUME_CATEGORY.CLASS_NAME)
            query.order(DataObjectHelper.CONSUME_CATEGORY.SORT_FLAG)
            e.onNext(query.find())
            e.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<List<ConsumptionCategory>>> { list ->
                    OperateResult(mutableListOf<ConsumptionCategory>().withAVCategories(list))
                }
    }
}
