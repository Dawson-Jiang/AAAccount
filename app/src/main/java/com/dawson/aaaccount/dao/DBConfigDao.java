package com.dawson.aaaccount.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.dawson.aaaccount.dao.bean.DBConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DBCONFIG".
*/
public class DBConfigDao extends AbstractDao<DBConfig, Long> {

    public static final String TABLENAME = "DBCONFIG";

    /**
     * Properties of entity DBConfig.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Key = new Property(1, String.class, "key", false, "KEY");
        public final static Property Value = new Property(2, String.class, "value", false, "VALUE");
    }


    public DBConfigDao(DaoConfig config) {
        super(config);
    }
    
    public DBConfigDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DBCONFIG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"KEY\" TEXT," + // 1: key
                "\"VALUE\" TEXT);"); // 2: value
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DBCONFIG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DBConfig entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(2, key);
        }
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(3, value);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DBConfig entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(2, key);
        }
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(3, value);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DBConfig readEntity(Cursor cursor, int offset) {
        DBConfig entity = new DBConfig( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // key
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // value
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DBConfig entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKey(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setValue(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DBConfig entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DBConfig entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DBConfig entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
