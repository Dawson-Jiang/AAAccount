package com.dawson.aaaccount.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase

/**
 * 数据库Dao
 * Created by Dawson on 2017/6/27.
 */

object GreenDaoUtil {
    private var openHelper: AccountDevOpenHelper? = null
    var daoSession: DaoSession? = null
        private set
    private var db: SQLiteDatabase? = null
    private var daoMaster: DaoMaster? = null

    fun initDaoDB(context: Context) {
        openHelper = AccountDevOpenHelper(context, "aaaccount.db")
        db = openHelper!!.writableDatabase
        daoMaster = DaoMaster(db)
        daoSession = daoMaster!!.newSession()
    }
}
