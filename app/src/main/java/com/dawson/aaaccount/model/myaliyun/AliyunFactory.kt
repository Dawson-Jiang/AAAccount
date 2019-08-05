package com.dawson.aaaccount.model.myaliyun

import com.dawson.aaaccount.model.*

/**
 * <p>project: AAAccount </p>
 * <p>des: [功能描述] </p>
 * <p>date: 2018/9/17 </p>
 * <p>云计算1310</p>
 *
 * @author <a href="mail to: kjxjx@163.com" rel="nofollow">Dawson</a>
 * @version v1.0
 */
class AliyunFactory : IModelFactory {
    override fun createCategoryModel() = CategoryModel()
    override fun createUserModel() = UserModel()
    override fun createFileModel() = FileModel()
    override fun createDayBookModel() = DayBookModel()
    override fun createFamilyModel() = FamilyModel()
    override fun createFeedBackModel() = FeedBackModel()
    override fun createLogModel() = LogModel()
    override fun createSettleModel() = SettleModel()
}