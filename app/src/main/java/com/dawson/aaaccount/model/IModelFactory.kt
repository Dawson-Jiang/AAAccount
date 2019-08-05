package com.dawson.aaaccount.model

import com.avos.avoscloud.AVFile
import com.dawson.aaaccount.model.myaliyun.AliyunFactory
import java.util.HashMap

/**
 * <p>project: AAAccount </p>
 * <p>des: [功能描述] </p>
 * <p>date: 2018/9/17 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
interface IModelFactory {

    fun createCategoryModel():ICategoryModel
    fun createUserModel():IUserModel
    fun createFileModel():IFileModel
    fun createDayBookModel():IDayBookModel
    fun createFamilyModel():IFamilyModel
    fun createFeedBackModel():IFeedBackModel
    fun createLogModel():ILogModel
    fun createSettleModel():ISettleModel
}

abstract class BaseModelFactory{
    companion object {
        var factory: IModelFactory=AliyunFactory()
    }
}

