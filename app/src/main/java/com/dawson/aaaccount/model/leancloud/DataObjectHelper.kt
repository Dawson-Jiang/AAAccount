package com.dawson.aaaccount.model.leancloud

/**
 * 数据字段定义
 * Created by dawson on 2017/2/15.
 */

object DataObjectHelper {
    object BASE_CLASS {
        val STATE = "state"
    }

    object CONSUME_CATEGORY {
        val CLASS_NAME = "ConsumeCategory"
        val NAME = "name"
        val SORT_FLAG = "sortFlag"
    }

    object FAMILY {
        val CLASS_NAME = "Family"
        val NUMBER = "ID"//自定义id
        val CREATOR = "creator"
        val NAME = "name"
        val MEMBER = "members"
        val HEAD = "headPic"
        val MEMBER2 = "members2"
        val TEMP = "isTemp"
        //public static final String CONSUME_TYPE= "consumeTypes";//后期可以考虑自定义类别
    }

    object MEMBER {
        val CLASS_NAME = "Member"
        val NAME = "memberName"
    }

    object USER {
        val CLASS_NAME = "_User"
        val HEAD = "headPic"
        val NAME = "username"
        val LOGIN_INFO = "loginInfo"
        //        public static final String  CONSUME_TYPE = "consumeTypes";//后期可以考虑自定义类别
    }


    object DAY_BOOK {
        val CLASS_NAME = "DayBook"
        val CONSUME_CATEGORY = "consumeCategory"
        val FAMILY = "family"
        val RECORDER = "recorder"
        val CONSUMER = "consumers"
        val CONSUMER2 = "consumers2"
        val PAYER = "payer"
        val PAYER2 = "payer2"
        val SETTLE = "settled"
        val MONEY = "money"
        val PICTURES = "pictures"
        val THUM_PICTURES = "thumPictures"
        val DESCRIPTION = "description"
        val DATE = "date"
    }

    object SETTLE {
        val CLASS_NAME = "Settle"
        val START_DATE = "startDate"
        val END_DATE = "endDate"
        val CREATOR = "creator"
        val MONEY = "money"
        val FAMILY = "family"
        val SETTLE = "settled"
        val DETAIL = "details"
        val DATE = "date"

    }

    object SETTLE_DETAIL {
        val CLASS_NAME = "SettleDetail"
        val USER = "user"
        val PAY = "pay"
        val CONSUME = "consume"
        val SETTLE = "settle"
        val AGREE = "agree"
    }


    object AV_FILE {
        val FAMILY_HEAD = "family_head_picture"
        val USER_HEAD = "user_head_picture"
        val DAYBOOK_PIC = "daybook_picture"
        val SYSTEM_LOG = "daybook_picture"
    }

    object FEED_BACK {
        val CLASS_NAME = "FeedBack"
        val USER = "creator"
        val TITLE = "title"
        val CONTENT = "content"
        val PHONE = "phone"
        val REPLY = "reply"
    }

    object SYSTEM_LOG {
        val CLASS_NAME = "SystemLog"
        val USER = "creator"
        val TITLE = "title"
        val CONTENT = "content"
        val PHONE = "phone"
    }
}
