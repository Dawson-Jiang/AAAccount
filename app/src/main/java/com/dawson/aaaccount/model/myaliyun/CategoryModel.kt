package com.dawson.aaaccount.model.myaliyun

import android.util.LruCache
import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.ICategoryModel
import com.dawson.aaaccount.net.DaybookService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.util.ErrorCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 类别管理
 * Created by dawson on 2017/5/24.
 */
class CategoryModel : ICategoryModel {
    private val service = RetrofitHelper.getService(DaybookService::class.java)

    object CategoryInstance {
        var categories = mutableListOf<ConsumptionCategory>()
    }

    override fun get(): Observable<OperateResult<List<ConsumptionCategory>>> {
        return if (CategoryInstance.categories.isEmpty())
            service.getCategories().subscribeOn(Schedulers.io())
                    .doOnNext {
                        if (it.result == ErrorCode.SUCCESS) {
                            CategoryInstance.categories.clear()
                            CategoryInstance.categories.addAll(it.content!!)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        else Observable.just(OperateResult(CategoryInstance.categories.toList())) .observeOn(AndroidSchedulers.mainThread())
    }
}
