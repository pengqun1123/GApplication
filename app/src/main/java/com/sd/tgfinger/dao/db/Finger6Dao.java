package com.sd.tgfinger.dao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sd.tgfinger.pojo.Finger6;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FINGER6".
*/
public class Finger6Dao extends AbstractDao<Finger6, Long> {

    public static final String TABLENAME = "FINGER6";

    /**
     * Properties of entity Finger6.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UId = new Property(0, Long.class, "uId", true, "_id");
        public final static Property Finger6Feature = new Property(1, byte[].class, "finger6Feature", false, "feature");
    }


    public Finger6Dao(DaoConfig config) {
        super(config);
    }
    
    public Finger6Dao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FINGER6\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: uId
                "\"feature\" BLOB);"); // 1: finger6Feature
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FINGER6\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Finger6 entity) {
        stmt.clearBindings();
 
        Long uId = entity.getUId();
        if (uId != null) {
            stmt.bindLong(1, uId);
        }
 
        byte[] finger6Feature = entity.getFinger6Feature();
        if (finger6Feature != null) {
            stmt.bindBlob(2, finger6Feature);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Finger6 entity) {
        stmt.clearBindings();
 
        Long uId = entity.getUId();
        if (uId != null) {
            stmt.bindLong(1, uId);
        }
 
        byte[] finger6Feature = entity.getFinger6Feature();
        if (finger6Feature != null) {
            stmt.bindBlob(2, finger6Feature);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Finger6 readEntity(Cursor cursor, int offset) {
        Finger6 entity = new Finger6( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // uId
            cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1) // finger6Feature
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Finger6 entity, int offset) {
        entity.setUId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFinger6Feature(cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Finger6 entity, long rowId) {
        entity.setUId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Finger6 entity) {
        if(entity != null) {
            return entity.getUId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Finger6 entity) {
        return entity.getUId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
