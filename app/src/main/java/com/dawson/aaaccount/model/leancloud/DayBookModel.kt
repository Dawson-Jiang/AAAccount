package com.dawson.aaaccount.model.leancloud

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
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 账单相关业务
 * Created by dawson on 2017/2/12.
 */
class DayBookModel : IDayBookModel {
    override fun save(context: Context, dayBook: DayBook): Observable<OperateResult<DayBook>> {
        return Observable.create<AVObject> { e ->
            val avDayBook = AVObject(DataObjectHelper.DAY_BOOK.CLASS_NAME)
            if (!TextUtils.isEmpty(dayBook.id)) avDayBook.objectId = dayBook.id
            avDayBook.put(DataObjectHelper.DAY_BOOK.RECORDER, AVUser.getCurrentUser())
            avDayBook.put(DataObjectHelper.DAY_BOOK.DESCRIPTION, dayBook.description)
            avDayBook.put(DataObjectHelper.DAY_BOOK.DATE, dayBook.date)

            if (dayBook.family != null && dayBook.family!!.isTemp) {
                val avUser = AVObject.createWithoutData(DataObjectHelper.MEMBER.CLASS_NAME, dayBook.payer?.id)
                avDayBook.put(DataObjectHelper.DAY_BOOK.PAYER2, avUser)
            } else {
                val avUser = AVUser()
                avUser.objectId = dayBook.payer?.id
                avDayBook.put(DataObjectHelper.DAY_BOOK.PAYER, avUser)
            }
            val category = AVObject.createWithoutData(DataObjectHelper.CONSUME_CATEGORY.CLASS_NAME, dayBook.category?.id)
            avDayBook.put(DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY, category)
            avDayBook.put(DataObjectHelper.DAY_BOOK.MONEY, dayBook.money)

            var stringBuilder: StringBuilder
            if (dayBook.pictures?.size!! > 0) {
                stringBuilder = StringBuilder(dayBook.pictures!![0])
                dayBook.pictures!!.forEachIndexed { i, s ->
                    if (i > 0) {
                        stringBuilder.append(";")
                        stringBuilder.append(s)
                    }
                }
                avDayBook.put(DataObjectHelper.DAY_BOOK.PICTURES, stringBuilder.toString())
            }
            if (dayBook.thumbPictures?.size!! > 0) {
                stringBuilder = StringBuilder(dayBook.thumbPictures!![0])
                dayBook.thumbPictures!!.forEachIndexed { i, s ->
                    if (i > 0) {
                        stringBuilder.append(";")
                        stringBuilder.append(s)
                    }
                }
                avDayBook.put(DataObjectHelper.DAY_BOOK.THUM_PICTURES, stringBuilder.toString())
            }
            e.onNext(avDayBook)
            e.onComplete()
        }.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext { avDayBook ->
                    if (dayBook.family != null) {
                        val family = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, dayBook.family?.id)
                        avDayBook.put(DataObjectHelper.DAY_BOOK.FAMILY, family)
                        if (dayBook.family?.isTemp!!) {
                            val relation = avDayBook.getRelation<AVObject>(DataObjectHelper.DAY_BOOK.CONSUMER2)
                            if (!TextUtils.isEmpty(dayBook.id)) {
                                val orgcus = relation.query.find()
                                orgcus.forEach { relation.remove(it) }
                            }
                            dayBook.customers?.forEach { host ->
                                val avUser = AVObject.createWithoutData(DataObjectHelper.MEMBER.CLASS_NAME, host.id)
                                relation.add(avUser)
                            }
                        } else {
                            val relation = avDayBook.getRelation<AVUser>(DataObjectHelper.DAY_BOOK.CONSUMER)
                            if (!TextUtils.isEmpty(dayBook.id)) {
                                val orgcus = relation.query.find()
                                orgcus.forEach { relation.remove(it) }
                            }
                            dayBook.customers?.forEach { host ->
                                val avUser2 = AVUser()
                                avUser2.objectId = host.id
                                relation.add(avUser2)
                            }
                        }
                    }
                }
                .map<OperateResult<DayBook>> { avDayBook ->
                    avDayBook.save()
                    dayBook.id = avDayBook.objectId
                    OperateResult(dayBook)
                }
    }

    override fun get(familyId: String, page: Int, limit: Int): Observable<OperateResult<List<DayBook>>> {
        return Observable.create<List<AVObject>> { e ->
            val query = AVQuery<AVObject>(DataObjectHelper.DAY_BOOK.CLASS_NAME)
            if (TextUtils.isEmpty(familyId)) {
                query.whereEqualTo(DataObjectHelper.DAY_BOOK.RECORDER, AVUser.getCurrentUser())
                query.whereDoesNotExist(DataObjectHelper.DAY_BOOK.FAMILY)
            } else {
                val family = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, familyId)
                query.whereEqualTo(DataObjectHelper.DAY_BOOK.FAMILY, family)
            }
            query.limit(limit)// 最多返回 10 条结果
            query.skip(page * limit)// 跳过 20 条结果
            query.include("${DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY}.${DataObjectHelper.CONSUME_CATEGORY.NAME}")
                    .include("${DataObjectHelper.DAY_BOOK.PAYER}.${DataObjectHelper.USER.NAME}")
                    .include("${DataObjectHelper.DAY_BOOK.PAYER2}.${DataObjectHelper.MEMBER.NAME}")
            query.selectKeys(mutableListOf(DataObjectHelper.DAY_BOOK.MONEY,
                    "${DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY}.${DataObjectHelper.CONSUME_CATEGORY.NAME}"
                    , "${DataObjectHelper.DAY_BOOK.PAYER}.${DataObjectHelper.USER.NAME}"
                    , "${DataObjectHelper.DAY_BOOK.PAYER2}.${DataObjectHelper.MEMBER.NAME}"
                    , DataObjectHelper.DAY_BOOK.DATE, DataObjectHelper.DAY_BOOK.THUM_PICTURES))
            query.orderByDescending(DataObjectHelper.DAY_BOOK.DATE)
            e.onNext(query.find())
            e.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<List<DayBook>>> { list ->
                    val dayBooks = ArrayList<DayBook>()
                    for (avObject in list) {
                        val dayBook = DayBook()
                        dayBook.id = avObject.objectId
                        dayBook.category = ConsumptionCategory(avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY).getString(DataObjectHelper.CONSUME_CATEGORY.NAME))
                        dayBook.payer = User()
                        var payer = avObject.getAVUser<AVUser>(DataObjectHelper.DAY_BOOK.PAYER)
                        if (payer != null)
                            dayBook.payer?.name = payer.getString(DataObjectHelper.USER.NAME)
                        else {
                            var payer2 = avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.PAYER2)
                            if (payer2 != null) dayBook.payer?.name = payer2.getString(DataObjectHelper.MEMBER.NAME)
                        }
                        dayBook.date = avObject.getDate(DataObjectHelper.DAY_BOOK.DATE)
                        dayBook.money = avObject.getDouble(DataObjectHelper.DAY_BOOK.MONEY)
                        val thumbPics = avObject.getString(DataObjectHelper.DAY_BOOK.THUM_PICTURES)
                        dayBook.thumbPictures = thumbPics?.split(";")?.toMutableList()
                        dayBooks.add(dayBook)
                    }
                    OperateResult(dayBooks)
                }
    }

    override fun getById(id: String): Observable<OperateResult<DayBook>> {
        return Observable.create<AVObject> { e ->
            val query = AVQuery<AVObject>(DataObjectHelper.DAY_BOOK.CLASS_NAME)
            query.whereEqualTo(AVObject.OBJECT_ID, id)
            query.include(DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY)
                    .include(DataObjectHelper.DAY_BOOK.FAMILY)
                    .include(DataObjectHelper.DAY_BOOK.PAYER)
                    .include(DataObjectHelper.DAY_BOOK.PAYER2)
                    .include(DataObjectHelper.DAY_BOOK.RECORDER)
            e.onNext(query.first)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<DayBook>> { avObject ->
                    val dayBook = DayBook().withAVObject(avObject)
                    if (dayBook.family != null && dayBook.family!!.isTemp) {
                        val urelation = avObject.getRelation<AVObject>(DataObjectHelper.DAY_BOOK.CONSUMER2)
                        val uquery = urelation.query
                        dayBook.customers = mutableListOf<User>().withAVMembers(uquery.find())
                    } else {
                        val urelation = avObject.getRelation<AVUser>(DataObjectHelper.DAY_BOOK.CONSUMER)
                        val uquery = urelation.query
                        dayBook.customers = mutableListOf<User>().withAVUsers(uquery.find())
                    }
                    OperateResult(dayBook)
                }
    }

    override fun delete(id: String): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> { e ->
            val avObject = AVObject.create(DataObjectHelper.DAY_BOOK.CLASS_NAME)
            avObject.objectId = id
            avObject.delete()
            e.onNext(OperateResult(null))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}
