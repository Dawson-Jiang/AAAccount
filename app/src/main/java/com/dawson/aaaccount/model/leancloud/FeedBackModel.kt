package com.dawson.aaaccount.model.leancloud

import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.dawson.aaaccount.bean.Feedback
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.model.IFeedBackModel
import com.dawson.aaaccount.util.PhoneHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 反馈
 * Created by Dawson on 2017/8/17.
 */

class FeedBackModel : IFeedBackModel {
    override fun add(title: String, content: String): Observable<OperateResult<Any>> {
        return Observable.create<OperateResult<Any>> {
            val avFeedback = AVObject(DataObjectHelper.FEED_BACK.CLASS_NAME)
            avFeedback.put(DataObjectHelper.FEED_BACK.USER, AVUser.getCurrentUser())
            avFeedback.put(DataObjectHelper.FEED_BACK.TITLE, title)
            avFeedback.put(DataObjectHelper.FEED_BACK.CONTENT, content)
            avFeedback.put(DataObjectHelper.FEED_BACK.PHONE, PhoneHelper.phoneType)
            avFeedback.save()
            it.onNext(OperateResult(""))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun getMyFeedback(): Observable<OperateResult<List<Feedback>>> {
        return Observable.create<List<AVObject>> { e ->
            val query = AVQuery<AVObject>(DataObjectHelper.FEED_BACK.CLASS_NAME)
            query.whereEqualTo(DataObjectHelper.FEED_BACK.USER, AVUser.getCurrentUser())

            query.find()
            e.onNext(query.find())
            e.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { list ->
                    val feeds = list.map {
                        val feed = Feedback()
                        feed.reply = it.getString(DataObjectHelper.FEED_BACK.REPLY)
                        feed.content = it.getString(DataObjectHelper.FEED_BACK.CONTENT)
                        feed.title = it.getString(DataObjectHelper.FEED_BACK.TITLE)
                        feed.createTime = it.createdAt
                        feed.lastModifiedTime = it.updatedAt
                        feed
                    }
                    OperateResult(feeds)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }
}
