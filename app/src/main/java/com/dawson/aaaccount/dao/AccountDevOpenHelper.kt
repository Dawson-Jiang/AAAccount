package com.dawson.aaaccount.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.dawson.aaaccount.dao.DaoMaster.DevOpenHelper

import org.greenrobot.greendao.database.Database

/**
 * 数据库
 * Created by Dawson on 2017/7/7.
 */

class AccountDevOpenHelper : DevOpenHelper {
    constructor(context: Context, name: String) : super(context, name)

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory) : super(context, name, factory) {}

    override fun onCreate(db: Database?) {
        super.onCreate(db)
    }

    override fun onUpgrade(db: Database, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= 1)
            DBSystemLogDao.createTable(db, false)
        if (oldVersion <= 2) {
            db.execSQL("ALTER TABLE  \"DBFAMILY\" ADD COLUMN  \"IS_TEMP\" INTEGER NOT NULL DEFAULT 0;")
        }
    }
}
