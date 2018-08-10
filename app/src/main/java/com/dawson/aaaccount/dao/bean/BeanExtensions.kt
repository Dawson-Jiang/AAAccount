package com.dawson.aaaccount.dao.bean

import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.model.leancloud.DataObjectHelper
import com.dawson.aaaccount.util.Common

fun User.withDBUser(dbUser: DBUser): User {
//    phone = dbUser.
    name = dbUser.name
    id = dbUser.id
//    createTime = dbUser.createdAt
    lastModifiedTime = dbUser.lastModifiedTime
//        headUrl = dbUser.
    return this
}

fun DBUser.withUser(user: User): DBUser {
//    phone = dbUser.
    name = user.name
    id = user.id
//    createTime = dbUser.createdAt
    lastModifiedTime = user.lastModifiedTime
    headPic = user.headUrl
    return this
}