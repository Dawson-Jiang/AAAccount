package com.dawson.aaaccount.model.leancloud.bean

import android.text.TextUtils
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.*
import com.dawson.aaaccount.dao.bean.DBDayBook
import com.dawson.aaaccount.dao.bean.DBFamily
import com.dawson.aaaccount.dao.bean.DBUser
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.model.leancloud.DataObjectHelper

/**
 * 数据bean转换相关扩展函数
 * Created by Dawson on 2017/11/15.
 */
fun ConsumptionCategory.withAVObject(avObject: AVObject): ConsumptionCategory {
    id = avObject.objectId
    name = avObject.getString(DataObjectHelper.CONSUME_CATEGORY.NAME)
    return this
}

fun MutableList<ConsumptionCategory>.withAVCategories(avObjects: List<AVObject>): MutableList<ConsumptionCategory> {
    avObjects.forEach { add(ConsumptionCategory("").withAVObject(it)) }
    return this
}

fun DayBook.withAVObject(avObject: AVObject): DayBook {
    createTime = avObject.createdAt
    lastModifiedTime = avObject.updatedAt
    date = avObject.getDate(DataObjectHelper.DAY_BOOK.DATE)
    id = avObject.objectId
//    settled = avObject.getInt(DataObjectHelper.DAY_BOOK.SETTLE)
    money = avObject.getDouble(DataObjectHelper.DAY_BOOK.MONEY)
    val pics = avObject.getString(DataObjectHelper.DAY_BOOK.PICTURES)
    val thum_pics = avObject.getString(DataObjectHelper.DAY_BOOK.THUM_PICTURES)
    if (!TextUtils.isEmpty(pics)) {
        pictures = pics.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
        thumbPictures = thum_pics.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
    }
    val c = avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.CONSUME_CATEGORY)
    category = ConsumptionCategory("").withAVObject(c)
    val r = avObject.getAVUser<AVUser>(DataObjectHelper.DAY_BOOK.RECORDER)
    creator = User().withAVUser(r)
    val f = avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.FAMILY)
    if (f != null) family = Family().withAVObject(f)

    payer = if (family != null && family?.isTemp!!) {
        User().withAVObject(avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.PAYER2))
    } else {
        User().withAVUser(avObject.getAVUser<AVUser>(DataObjectHelper.DAY_BOOK.PAYER))
    }

    description = avObject.getString(DataObjectHelper.DAY_BOOK.DESCRIPTION)
    return this
}

fun Family.withAVObject(avObject: AVObject): Family {
    id = avObject.objectId
    name = avObject.getString(DataObjectHelper.FAMILY.NAME)
    number = avObject.getInt(DataObjectHelper.FAMILY.NUMBER)
    isTemp = avObject.getBoolean(DataObjectHelper.FAMILY.TEMP)
    createTime = avObject.createdAt
    lastModifiedTime = avObject.updatedAt
    val file = avObject.getAVFile<AVFile>(DataObjectHelper.FAMILY.HEAD)
    if (file != null) {
        headUrl = file.url
        headThumbUrl = file.getThumbnailUrl(true, Common.THUMB_SIZE, Common.THUMB_SIZE)
    }
    return this
}

fun User.withAVUser(avUser: AVUser): User {
    phone = avUser.mobilePhoneNumber
    name = avUser.username
    id = avUser.objectId
    createTime = avUser.createdAt
    lastModifiedTime = avUser.updatedAt
    val avFile = avUser.getAVFile<AVFile>(DataObjectHelper.USER.HEAD)
    if (avFile != null) {
        headUrl = avFile.url
        headThumbUrl = avFile.getThumbnailUrl(true, Common.THUMB_SIZE, Common.THUMB_SIZE)
    }
    return this
}

fun User.withAVObject(avObject: AVObject): User {
    name = avObject.getString(DataObjectHelper.MEMBER.NAME)
    id = avObject.objectId
    createTime = avObject.createdAt
    lastModifiedTime = avObject.updatedAt
    return this
}

fun MutableList<User>.withAVUsers(avUsers: List<AVUser>): MutableList<User> {
    avUsers.forEach { add(User().withAVUser(it)) }
    return this
}

fun MutableList<User>.withAVMembers(avObjects: List<AVObject>): MutableList<User> {
    avObjects.forEach { add(User().withAVObject(it)) }
    return this
}

fun Settle.withAVObject(avObject: AVObject): Settle {
    id = avObject.objectId
    money = avObject.getDouble(DataObjectHelper.SETTLE.MONEY)
    endDate = avObject.getDate(DataObjectHelper.SETTLE.END_DATE)
    startDate = avObject.getDate(DataObjectHelper.SETTLE.START_DATE)
    settled = avObject.getInt(DataObjectHelper.SETTLE.SETTLE)
    date = avObject.getDate(DataObjectHelper.SETTLE.DATE)
    val avCreator = avObject.getAVObject(DataObjectHelper.SETTLE.CREATOR, AVUser::class.java)
    creator = User().withAVUser(avCreator)
    return this
}

fun Settle.SettleDetail.withAVObject(avObject: AVObject): Settle.SettleDetail {
    id = avObject.objectId
    pay = avObject.getDouble(DataObjectHelper.SETTLE_DETAIL.PAY)
    consume = avObject.getDouble(DataObjectHelper.SETTLE_DETAIL.CONSUME)
    settleMoney = avObject.getDouble(DataObjectHelper.SETTLE_DETAIL.SETTLE)
    agree = avObject.getInt(DataObjectHelper.SETTLE_DETAIL.AGREE)
    val avUser = avObject.getAVObject(DataObjectHelper.SETTLE_DETAIL.USER, AVUser::class.java)
    user = User().withAVUser(avUser)
    return this
}

fun MutableList<Settle.SettleDetail>.withAVSettleDetails(avObjects: List<AVObject>): MutableList<Settle.SettleDetail> {
    avObjects.forEach { add(Settle.SettleDetail().withAVObject(it)) }
    return this
}


fun DBUser.withAVUser(avUser: AVUser): DBUser {
    name = avUser.username
    id = avUser.objectId
    lastModifiedTime = avUser.updatedAt
    return this
}

fun DBUser.withAVObject(avObject: AVObject): DBUser {
    name = avObject.getString(DataObjectHelper.MEMBER.NAME)
    id = avObject.objectId
    lastModifiedTime = avObject.updatedAt
    return this
}

fun MutableList<DBUser>.withAVUsers2(avUsers: List<AVUser>): MutableList<DBUser> {
    avUsers.forEach { add(DBUser().withAVUser(it)) }
    return this
}

fun MutableList<DBUser>.withAVMembers2(avObjects: List<AVObject>): MutableList<DBUser> {
    avObjects.forEach { add(DBUser().withAVObject(it)) }
    return this
}

fun DBFamily.withAVObject(avObject: AVObject): DBFamily {
    id = avObject.objectId
    name = avObject.getString(DataObjectHelper.FAMILY.NAME)
    number = avObject.getInt(DataObjectHelper.FAMILY.NUMBER)
    isTemp = avObject.getBoolean(DataObjectHelper.FAMILY.TEMP)
    lastModifiedTime = avObject.updatedAt
    return this
}

fun DBDayBook.withAVObject(avObject: AVObject): DBDayBook {
    date = avObject.getDate(DataObjectHelper.DAY_BOOK.DATE)
    id = avObject.objectId
    settled = avObject.getInt(DataObjectHelper.DAY_BOOK.SETTLE)
    money = avObject.getDouble(DataObjectHelper.DAY_BOOK.MONEY)
    lastModifiedTime = avObject.updatedAt

    creator = DBUser().withAVUser(avObject.getAVUser<AVUser>(DataObjectHelper.DAY_BOOK.RECORDER))
    val f = avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.FAMILY)
    if (f != null) family = DBFamily().withAVObject(f)
    payer = if (f != null && f.getBoolean(DataObjectHelper.FAMILY.TEMP)) {
        DBUser().withAVObject(avObject.getAVObject<AVObject>(DataObjectHelper.DAY_BOOK.PAYER2))
    } else {
        DBUser().withAVUser(avObject.getAVUser<AVUser>(DataObjectHelper.DAY_BOOK.PAYER))
    }
    return this
}