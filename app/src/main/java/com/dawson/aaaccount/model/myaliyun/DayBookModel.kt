package com.dawson.aaaccount.model.myaliyun

import android.content.Context
import android.text.TextUtils
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.ConsumptionCategory
import com.dawson.aaaccount.bean.DayBook
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IDayBookModel
import com.dawson.aaaccount.model.leancloud.bean.withAVMembers
import com.dawson.aaaccount.model.leancloud.bean.withAVObject
import com.dawson.aaaccount.model.leancloud.bean.withAVUsers
import com.dawson.aaaccount.net.DaybookService
import com.dawson.aaaccount.net.RetrofitHelper
import com.dawson.aaaccount.util.ErrorCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * 账单相关业务
 * Created by dawson on 2017/2/12.
 */
class DayBookModel : IDayBookModel {
    private val service = RetrofitHelper.getService(DaybookService::class.java)

    override fun save(context: Context, dayBook: DayBook): Observable<OperateResult<DayBook>> {
        return service.save(dayBook).map {
            if (it.result == ErrorCode.SUCCESS) {
                dayBook.id = it.content!!
            }
            return@map it.cast<DayBook>(dayBook)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(familyId: String, page: Int, limit: Int): Observable<OperateResult<List<DayBook>>> {
        val param = HashMap<String, String>()
        param["page"] = page.toString()
        param["limit"] = limit.toString()
        return if (TextUtils.isEmpty(familyId)) {
            param["uid"] = UserInstance.current_user?.id!!
            service.getMyDaybook(param).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            param["fid"] = familyId
            service.getFamilyDaybook(param).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun getById(id: String): Observable<OperateResult<DayBook>> {
        return service.get(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun delete(id: String): Observable<OperateResult<Any>> {
        return service.del(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
