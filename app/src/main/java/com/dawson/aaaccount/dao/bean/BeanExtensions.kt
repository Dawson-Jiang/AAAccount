package com.dawson.aaaccount.dao.bean

import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.User
import com.dawson.aaaccount.model.leancloud.DataObjectHelper
import com.dawson.aaaccount.util.Common

fun User.withDBUser(dbUser: DBUser): User {
    name = dbUser.name
    id = dbUser.id
    lastModifiedTime = dbUser.lastModifiedTime
    headUrl = dbUser.headPic
    token = dbUser.token
    return this
}

fun DBUser.withUser(user: User): DBUser {
    name = user.name
    id = user.id
    lastModifiedTime = user.lastModifiedTime
    headPic = user.headUrl
    token = user.token
    return this
}