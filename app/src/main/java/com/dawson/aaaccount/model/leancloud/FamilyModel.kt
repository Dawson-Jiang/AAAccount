package com.dawson.aaaccount.model.leancloud

import android.content.Context
import android.text.TextUtils
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IFamilyModel
import com.dawson.aaaccount.model.leancloud.bean.withAVMembers
import com.dawson.aaaccount.model.leancloud.bean.withAVObject
import com.dawson.aaaccount.model.leancloud.bean.withAVUsers
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 家庭相关业务
 * Created by dawson on 2017/2/15.
 */
class FamilyModel : IFamilyModel {
    override fun create(context: Context, family: Family): Observable<OperateResult<Family>> {
        return Observable.create<OperateResult<Family>> { e ->
            val avFamily = AVObject(DataObjectHelper.FAMILY.CLASS_NAME)
            avFamily.put(DataObjectHelper.FAMILY.CREATOR, AVUser.getCurrentUser())
            avFamily.put(DataObjectHelper.FAMILY.NAME, family.name)
            avFamily.put(DataObjectHelper.FAMILY.TEMP, family.isTemp)

            if (family.isTemp) {//临时家庭  先将自己的副本保存到成员表中 在添加关系
                val avMember = AVObject(DataObjectHelper.MEMBER.CLASS_NAME)
                avMember.put(DataObjectHelper.MEMBER.NAME, AVUser.getCurrentUser().username)
                avMember.save()
                val relation = avFamily.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
                relation.add(avMember)
            } else {
                val relation = avFamily.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
                relation.add(AVUser.getCurrentUser())
            }
            if (!TextUtils.isEmpty(family.headUrl) && FileModel.uploadFiles[family.headUrl] != null)
                avFamily.put(DataObjectHelper.FAMILY.HEAD, FileModel.uploadFiles[family.headUrl])
            avFamily.save()
            family.id = avFamily.objectId
            e.onNext(OperateResult(family))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun join(family: Family): Observable<OperateResult<Family>> {
        val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
        return Observable.create<OperateResult<Family>> { e ->
            val relation = avFamily.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
            relation.add(AVUser.getCurrentUser())
            avFamily.save()
            e.onNext(OperateResult(family))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun disJoin(family: Family): Observable<OperateResult<Any>> {
        val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
        return Observable.create<OperateResult<Any>> { e ->
            if (family.isTemp || family.members?.size!! <= 1)//临时家庭  最后一个成员的家庭直接删除家庭
                avFamily.delete()
            else {
                val relation = avFamily.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
                val orgmem = relation.query.find()
                val cu = AVUser.getCurrentUser()
                orgmem.forEach { if (it.objectId == cu.objectId) relation.remove(it) }
                avFamily.save()
            }
            e.onNext(OperateResult(null))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun del(family: Family): Observable<OperateResult<Any>> {
        val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
        return Observable.create<OperateResult<Any>> { e ->
            avFamily.delete()
            e.onNext(OperateResult(null))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun getMyFamily(): Observable<OperateResult<List<Family>>> {
        return Observable.create<List<AVObject>> { e ->
            val querys = ArrayList<AVQuery<AVObject>>()
            querys.add(AVQuery<AVObject>(DataObjectHelper.FAMILY.CLASS_NAME)
                    .whereEqualTo(DataObjectHelper.FAMILY.MEMBER, AVUser.getCurrentUser()))
            querys.add(AVQuery<AVObject>(DataObjectHelper.FAMILY.CLASS_NAME)
                    .whereEqualTo(DataObjectHelper.FAMILY.CREATOR, AVUser.getCurrentUser()))
            e.onNext(AVQuery.or(querys).find())
            e.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap<OperateResult<List<Family>>> { list ->
                    val families = ArrayList<Family>()
                    for (avObject in list) {
                        val family = Family().withAVObject(avObject)
                        if (family.isTemp) {
                            val mrelation = avObject.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
                            val mquery = mrelation.query
                            family.members = mutableListOf<User>().withAVMembers(mquery.find())
                        } else {
                            val urelation = avObject.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
                            val uquery = urelation.query
                            family.members = mutableListOf<User>().withAVUsers(uquery.find())
                        }

                        families.add(family)
                    }
                    Observable.just(OperateResult(families.toList()))
                }
    }

    override fun getFamilyById(context: Context, id: String): Observable<OperateResult<Family>> {
        return Observable.create<AVObject> { e ->
            val query = AVQuery<AVObject>(DataObjectHelper.FAMILY.CLASS_NAME)
            query.whereEqualTo(AVObject.OBJECT_ID, id)
            e.onNext(query.first)
            e.onComplete()
        }.subscribeOn(Schedulers.io())
                .map<OperateResult<Family>> { avObject ->
                    val family = Family().withAVObject(avObject)
                    if (family.isTemp) {
                        val mrelation = avObject.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
                        val mquery = mrelation.query
                        family.members = mutableListOf<User>().withAVMembers(mquery.find())
                    } else {
                        val urelation = avObject.getRelation<AVUser>(DataObjectHelper.FAMILY.MEMBER)
                        val uquery = urelation.query
                        family.members = mutableListOf<User>().withAVUsers(uquery.find())
                    }
                    OperateResult(family)
                }
    }

    override fun modify(context: Context, family: Family): Observable<OperateResult<Family>> {
        return Observable.create<OperateResult<Family>> { e ->
            val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
            if (!TextUtils.isEmpty(family.name))
                avFamily.put(DataObjectHelper.FAMILY.NAME, family.name)
            if (!TextUtils.isEmpty(family.headUrl) && FileModel.uploadFiles[family.headUrl] != null)
                avFamily.put(DataObjectHelper.FAMILY.HEAD, FileModel.uploadFiles[family.headUrl])
            avFamily.save()
            e.onNext(OperateResult(Family().withAVObject(avFamily)))
            e.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun addMember(family: Family, user: User): Observable<OperateResult<User>> {
        return Observable.create<OperateResult<User>> {
            val avMember = AVObject(DataObjectHelper.MEMBER.CLASS_NAME)
            avMember.put(DataObjectHelper.MEMBER.NAME, user.name)
            avMember.save()
            user.id = avMember.objectId
            val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
            val relation = avFamily.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
            relation.add(avMember)
            avFamily.save()
            it.onNext(OperateResult(user))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun delMemeber(family: Family, user: User): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> {
            val avFamily = AVObject.createWithoutData(DataObjectHelper.FAMILY.CLASS_NAME, family.id)
            val relation = avFamily.getRelation<AVObject>(DataObjectHelper.FAMILY.MEMBER2)
            val orgmem = relation.query.find()
            orgmem.forEach { if (user.id == it.objectId) relation.remove(it) }
            avFamily.save()
            val avMember = AVObject.createWithoutData(DataObjectHelper.MEMBER.CLASS_NAME, user.id)
            avMember.delete()
            it.onNext(OperateResult(""))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun modifyMemeber(user: User): Observable<OperateResult<User>> {
        return Observable.create<OperateResult<User>> {
            val avMember = AVObject.createWithoutData(DataObjectHelper.MEMBER.CLASS_NAME, user.id)
            avMember.put(DataObjectHelper.MEMBER.NAME, user.name)
            avMember.save()
            it.onNext(OperateResult(user))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}