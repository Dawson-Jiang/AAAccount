package com.dawson.aaaccount.model.leancloud

import android.content.Context
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.Settle
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.dao.*
import com.dawson.aaaccount.dao.bean.*
import com.dawson.aaaccount.dao.utils.GreenDaoUtil
import com.dawson.aaaccount.model.ISettleModel
import com.dawson.aaaccount.model.leancloud.bean.*
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.format
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * 统计结算
 * Created by dawson on 2017/3/29.
 */
class SettleModel : ISettleModel {
    override fun settle(settle: Settle): Observable<OperateResult<Any>> {
        return Observable.create<ArrayList<AVObject>> {
            //保存详情
            val details = settle.settleDetails!!
            val avDetails = ArrayList<AVObject>()
            for (detail in details) {
                val avDetail = AVObject(DataObjectHelper.SETTLE_DETAIL.CLASS_NAME)
                avDetail.put(DataObjectHelper.SETTLE_DETAIL.AGREE, 1)
                avDetail.put(DataObjectHelper.SETTLE_DETAIL.CONSUME, detail.consume)
                avDetail.put(DataObjectHelper.SETTLE_DETAIL.PAY, detail.pay)
                avDetail.put(DataObjectHelper.SETTLE_DETAIL.SETTLE, detail.settleMoney)
                avDetail.put(DataObjectHelper.SETTLE_DETAIL.USER, AVUser.getCurrentUser())
                avDetails.add(avDetail)
            }
            AVObject.saveAll(avDetails)
            it.onNext(avDetails)
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .map<AVObject> { avDetails ->
                    val avSettle = AVObject(DataObjectHelper.SETTLE.CLASS_NAME)
                    avSettle.put(DataObjectHelper.SETTLE.CREATOR, AVUser.getCurrentUser())
                    avSettle.put(DataObjectHelper.SETTLE.END_DATE, settle.endDate)
                    avSettle.put(DataObjectHelper.SETTLE.START_DATE, settle.startDate)
                    avSettle.put(DataObjectHelper.SETTLE.DATE, settle.date)
                    avSettle.put(DataObjectHelper.SETTLE.SETTLE, 1)
                    val family = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME,
                            settle.family?.id)
                    avSettle.put(DataObjectHelper.SETTLE.FAMILY, family)
                    val relation = avSettle.getRelation<AVObject>(DataObjectHelper.SETTLE.DETAIL)
                    for (avDetail in avDetails) {
                        relation.add(avDetail)
                    }
                    avSettle.put(DataObjectHelper.SETTLE.MONEY, settle.money)
                    avSettle.save()
                    avSettle
                }
                .map<OperateResult<Any>> { avSettle ->
                    //更新本地数据库
                    val avDaybooks = ArrayList<AVObject>()
                    for (dayBook in DAY_BOOKS!!) {
                        dayBook.settled = 1
                        dayBook.settleId = avSettle.objectId
                        val avDaybook = AVObject.createWithoutData(DataObjectHelper.DAY_BOOK.CLASS_NAME,
                                dayBook.id)
                        avDaybook.put(DataObjectHelper.DAY_BOOK.SETTLE, 1)
                        avDaybooks.add(avDaybook)
                    }
                    AVObject.saveAll(avDaybooks)
                    GreenDaoUtil.daoSession?.dbDayBookDao?.updateInTx(DAY_BOOKS)
                    OperateResult(null)
                }
    }

    override fun getByFamilyId(familyId: String): Observable<OperateResult<List<Settle>>> {
        return Observable.create<List<AVObject>> {
            val query = AVQuery<AVObject>(DataObjectHelper.SETTLE.CLASS_NAME)
            query.include(DataObjectHelper.SETTLE.CREATOR)
//            query.include(DataObjectHelper.SETTLE.FAMILY)
            val family = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, familyId)
            query.whereEqualTo(DataObjectHelper.SETTLE.FAMILY, family)
            it.onNext(query.find())
            it.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<List<Settle>>> { list ->
                    val settles = ArrayList<Settle>()
                    for (avObject in list) {
                        val settle = Settle().withAVObject(avObject)
                        val drelation = avObject.getRelation<AVObject>(DataObjectHelper.SETTLE.DETAIL)
                        val dquery = drelation.query
                        dquery.include(DataObjectHelper.SETTLE_DETAIL.USER)
                        try {
                            settle.settleDetails = mutableListOf<Settle.SettleDetail>().withAVSettleDetails(dquery.find()).toMutableList()
                        } catch (ex: AVException) {
                            ex.printStackTrace()
                        }
                        settles.add(settle)
                    }
                    OperateResult(settles)
                }
    }

    override fun statistic(family: Family, start: Date?, end: Date?, containSettle: Boolean): Observable<OperateResult<Settle>> {
        return Observable.create<List<DBDayBook>> {
            val dayBookDao = GreenDaoUtil.daoSession?.dbDayBookDao!!
            val query = dayBookDao.queryBuilder()
            query.where(DBDayBookDao.Properties.FamilyId.eq(family.id))
            query.where(DBDayBookDao.Properties.Date.ge(start), DBDayBookDao.Properties.Date.le(end))
            if (!containSettle) query.where(DBDayBookDao.Properties.Settled.notEq(1))
            query.orderAsc(DBDayBookDao.Properties.Date)
            it.onNext(query.list())
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<Settle>> { daybooks ->
                    val settle = Settle()
                    settle.family = family
                    if (start != null && end != null) {
                        settle.startDate = start
                        settle.endDate = end
                    } else {
                        if (daybooks.isNotEmpty()) {
                            settle.startDate = daybooks[0].date
                            settle.endDate = daybooks[daybooks.size - 1].date
                        }
                    }
                    if (daybooks.isEmpty()) {
                        return@map OperateResult(settle)
                    }
                    val settleDetails = ArrayList<Settle.SettleDetail>()
                    family.members?.forEachIndexed { _, user ->
                        val settleDetail = Settle.SettleDetail()
                        settleDetail.user = user
                        settleDetails.add(settleDetail)
                    }
                    var totalMoney = 0.0
                    daybooks.forEach { dbook ->
                        val m = dbook.money// 消费金额
                        totalMoney += m// 消费总金额
                        val cms = dbook.customers// 消费人员
                        val avm = m / cms.size// 平均消费金额

                        // 付款金额
                        val puser = dbook.payer
                        settleDetails.forEach {
                            if (puser.equalsToUser(it.user)) it.pay += m
                        }
                        // 消费金额
                        cms.forEach { cm ->
                            settleDetails.forEach {
                                if (cm.equalsToUser(it.user)) it.consume += avm
                            }
                        }
                    }
                    settle.money = totalMoney
                    DAY_BOOKS = daybooks
                    // 结算金额
                    settleDetails.forEach {
                        it.settleMoney = it.pay - it.consume
                    }
                    settle.settleDetails = settleDetails
                    OperateResult(settle)
                }
    }

    override fun statisticMine(start: Date?, end: Date?): Observable<OperateResult<Settle>> {
        var tstart = start
        var tend = end
        if (tstart == null || tend == null) {
            val calendar = Calendar.getInstance()
            tend = calendar.time
            calendar.add(Calendar.MONTH, -1)
            tstart = calendar.time
        }
        return Observable.create<List<DBDayBook>> {
            val dayBookDao = GreenDaoUtil.daoSession?.dbDayBookDao!!
            val user = AVUser.getCurrentUser()
            val query = dayBookDao.queryBuilder()
            query.where(DBDayBookDao.Properties.CreatorId.eq(user.objectId))

            query.where(DBDayBookDao.Properties.Date.ge(tstart), DBDayBookDao.Properties.Date.le(tend))
            query.where(DBDayBookDao.Properties.FamilyId.isNull)
            query.orderAsc(DBDayBookDao.Properties.Date)
            it.onNext(query.list())
            it.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map<OperateResult<Settle>> { daybooks ->
                    val settle = Settle()
                    settle.startDate = tstart
                    settle.endDate = tend
                    if (daybooks.isEmpty()) {
                        return@map OperateResult(settle)
                    }

                    val totalMoney = daybooks.indices
                            .map {
                                daybooks[it].money// 消费金额// 消费总金额
                            }
                            .sum()
                    settle.money = totalMoney
                    OperateResult(settle)
                }
    }

    override fun syncData(context: Context, all: Boolean): Observable<OperateResult<Any>> {
        //同步自己的所有账单
        return syncDayBook(null).observeOn(Schedulers.io())
                .flatMap<AVObject> { _ ->
                    val querys = ArrayList<AVQuery<AVObject>>()
                    querys.add(AVQuery<AVObject>(DataObjectHelper.FAMILY.CLASS_NAME)
                            .whereEqualTo(DataObjectHelper.FAMILY.MEMBER, AVUser.getCurrentUser()))
                    querys.add(AVQuery<AVObject>(DataObjectHelper.FAMILY.CLASS_NAME)
                            .whereEqualTo(DataObjectHelper.FAMILY.CREATOR, AVUser.getCurrentUser()))
                    val query = AVQuery.or(querys)

                    query.selectKeys(arrayListOf(DataObjectHelper.FAMILY.NAME, DataObjectHelper.FAMILY.NUMBER, DataObjectHelper.FAMILY.TEMP))
                    Observable.fromIterable(query.find())
                }
                .observeOn(Schedulers.io())
                .flatMap<AVObject> { avObject ->
                    if (all) {
                        val familyDao = GreenDaoUtil.daoSession?.dbFamilyDao!!
                        val family = DBFamily().withAVObject(avObject)
                        familyDao.insertOrReplace(family)//更新家庭信息
                        syncUser(avObject)//更新家庭成员信息
                    } else
                        Observable.just(avObject)
                }
                .flatMap<OperateResult<Any>> { avObject ->
                    syncDayBook(avObject)
                }
    }

    private fun syncUser(family: AVObject): Observable<AVObject> {
        return Observable.create<List<DBUser>> { e ->
            val isTemp = family.getBoolean(DataObjectHelper.FAMILY.TEMP)
            val dbusers =
                    if (isTemp) {
                        val mrelation = family.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
                        val mquery = mrelation.query
                        mutableListOf<DBUser>().withAVMembers2(mquery.find())
                    } else {
                        val urelation = family.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
                        val uquery = urelation.query
                        uquery.selectKeys(Arrays.asList(DataObjectHelper.USER.NAME))
                        mutableListOf<DBUser>().withAVUsers2(uquery.find())
                    }
            e.onNext(dbusers)
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap<AVObject> { users ->
                    val userDao = GreenDaoUtil.daoSession?.dbUserDao!!
                    val joinUserToFamilyDao = GreenDaoUtil.daoSession?.joinUserToFamilyDao!!
                    userDao.insertOrReplaceInTx(users)//更新家庭成员信息
                    //更新关系
                    var joinUserToFamilies: MutableList<JoinUserToFamily> = joinUserToFamilyDao.queryBuilder().where(JoinUserToFamilyDao.Properties
                            .Fid.eq(family.objectId)).list()
                    joinUserToFamilyDao.deleteInTx(joinUserToFamilies)//删除原有成员关系
                    joinUserToFamilies = users.mapTo(ArrayList()) { JoinUserToFamily(null, it.id, family.objectId) }
                    joinUserToFamilyDao.insertInTx(joinUserToFamilies)//添加新成员关系
                    Observable.just(family)
                }
    }

    private fun syncDayBook(family: AVObject?): Observable<OperateResult<Any>> {
        return Observable.create<List<AVObject>> { e ->
            val dbConfigDao = GreenDaoUtil.daoSession?.dbConfigDao!!
            val dbConfig: DBConfig? = dbConfigDao.queryBuilder().where(DBConfigDao.Properties.Key.eq(ISettleModel.Companion.DAYBOOK_LAST_SYNC_TIME)).unique()
            var ddate = Date(0)
            if (dbConfig?.value?.toString() != null)
                ddate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dbConfig.value.toString())
            val query = AVQuery<AVObject>(DataObjectHelper.DAY_BOOK.CLASS_NAME)
            query.include(DataObjectHelper.DAY_BOOK.FAMILY)
            query.include(DataObjectHelper.DAY_BOOK.PAYER)
            query.include(DataObjectHelper.DAY_BOOK.PAYER2)
            query.include(DataObjectHelper.DAY_BOOK.RECORDER)
            if (family == null) {
                query.whereEqualTo(DataObjectHelper.DAY_BOOK.RECORDER, AVUser.getCurrentUser())
                query.whereDoesNotExist(DataObjectHelper.DAY_BOOK.FAMILY)
            } else {
                query.whereEqualTo(DataObjectHelper.DAY_BOOK.FAMILY, family)
            }
            query.whereGreaterThan(AVObject.UPDATED_AT, ddate)
            query.order(AVObject.UPDATED_AT)
            query.selectKeys(arrayListOf(DataObjectHelper.DAY_BOOK.DATE,
                    DataObjectHelper.DAY_BOOK.SETTLE,
                    DataObjectHelper.DAY_BOOK.MONEY,
                    DataObjectHelper.DAY_BOOK.RECORDER,
                    DataObjectHelper.DAY_BOOK.FAMILY,
                    DataObjectHelper.DAY_BOOK.PAYER,
                    DataObjectHelper.DAY_BOOK.PAYER2))
            e.onNext(query.find())
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap<OperateResult<Any>> { list ->
                    val dayBookDao = GreenDaoUtil.daoSession?.dbDayBookDao!!
                    val joinDayBookToUserDao = GreenDaoUtil.daoSession?.joinDayBookToUserDao!!
                    var ddate = Date(0)
                    for (avObject in list) {
                        val dayBook = DBDayBook().withAVObject(avObject)
                        dayBookDao.insertOrReplace(dayBook)//更新账单
                        //更新消费人关系
                        val users = if (dayBook.family != null && dayBook.family!!.isTemp) {
                            val urelation = avObject.getRelation<AVObject>(DataObjectHelper.DAY_BOOK.CONSUMER2)
                            val uquery = urelation.query
                            mutableListOf<User>().withAVMembers(uquery.find())
                        } else {
                            val urelation = avObject.getRelation<AVUser>(DataObjectHelper.DAY_BOOK.CONSUMER)
                            val uquery = urelation.query
                            mutableListOf<User>().withAVUsers(uquery.find())
                        }
                        var joinDayBookToUsers: MutableList<JoinDayBookToUser> = joinDayBookToUserDao.queryBuilder().where(JoinDayBookToUserDao.Properties
                                .Did.eq(dayBook.id)).list()
                        joinDayBookToUserDao.deleteInTx(joinDayBookToUsers)//删除原有关系
                        joinDayBookToUsers = ArrayList()
                        for (user in users) {
                            joinDayBookToUsers.add(JoinDayBookToUser(null, user.id, dayBook.id))
                        }
                        joinDayBookToUserDao.insertInTx(joinDayBookToUsers)//添加新关系
                        ddate = dayBook.lastModifiedTime
                    }
                    val dbConfigDao = GreenDaoUtil.daoSession?.dbConfigDao!!
                    var dbConfig: DBConfig? = dbConfigDao.queryBuilder().where(DBConfigDao.Properties.Key.eq(ISettleModel.Companion.DAYBOOK_LAST_SYNC_TIME)).unique()

                    if (dbConfig == null) {
                        dbConfig = DBConfig()
                        dbConfig.key = ISettleModel.Companion.DAYBOOK_LAST_SYNC_TIME
                    }
                    dbConfig.value = ddate.format()
                    dbConfigDao.insertOrReplace(dbConfig)
                    val res: OperateResult<Any> = OperateResult()
                    res.result = ErrorCode.SUCCESS
                    Observable.just(res)
                }
    }

    companion object {
        private var DAY_BOOKS: List<DBDayBook>? = null
    }
}
