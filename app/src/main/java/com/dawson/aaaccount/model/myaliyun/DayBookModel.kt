package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import android.text.TextUtils
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IDayBookModel
import com.dawson.aaaccount.net.DaybookService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.util.ErrorCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 账单相关业务
 * Created by dawson on 2017/2/12.
 */
class DayBookModel : IDayBookModel {
    private val service = RetrofitHelper.getService(DaybookService::class.java)

    override fun save(context: Context, dayBook: DayBook): Observable<OperateResult<DayBook>> {
        if (dayBook.pictures != null && !dayBook.pictures?.isEmpty()!!) {
            dayBook.pic1 = dayBook.pictures?.get(0)
            if (dayBook.pictures?.size!! > 1) dayBook.pic2 = dayBook.pictures?.get(1)
            if (dayBook.pictures?.size!! > 2) dayBook.pic3 = dayBook.pictures?.get(2)
        }
        return service.save(dayBook).map {
            if (it.result == ErrorCode.SUCCESS) {
                dayBook.id = it.content!!
            }
            return@map it.cast(dayBook)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(familyId: String, page: Int, limit: Int): Observable<OperateResult<List<DayBook>>> {
        val param = HashMap<String, String>()
        param["page"] = page.toString()
        param["limit"] = limit.toString()
        return if (TextUtils.isEmpty(familyId)) {
            param["uid"] = UserInstance.current_user?.id!!
            service.getMyDaybook(param).doOnNext {
                if (it.result == ErrorCode.SUCCESS && it.content != null && !it.content?.isEmpty()!!) {
                    it.content?.forEach { db ->
                        db.pictures = mutableListOf()
                        if (!TextUtils.isEmpty(db.pic1)) db.pictures?.add(db.pic1!!)
                        if (!TextUtils.isEmpty(db.pic2)) db.pictures?.add(db.pic2!!)
                        if (!TextUtils.isEmpty(db.pic3)) db.pictures?.add(db.pic3!!)

                    }
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            param["fid"] = familyId
            service.getFamilyDaybook(param).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun getById(id: String): Observable<OperateResult<DayBook>> {
        return service.get(mutableMapOf(Pair("id", id)))
                .doOnNext {
                    if (it.result == ErrorCode.SUCCESS && it.content != null) {
                        val db = it.content!!
                        db.pictures = mutableListOf()
                        if (!TextUtils.isEmpty(db.pic1)) db.pictures?.add(db.pic1!!)
                        if (!TextUtils.isEmpty(db.pic2)) db.pictures?.add(db.pic2!!)
                        if (!TextUtils.isEmpty(db.pic3)) db.pictures?.add(db.pic3!!)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun delete(id: String): Observable<OperateResult<Any>> {
        return service.del(mutableMapOf(Pair("id", id))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
