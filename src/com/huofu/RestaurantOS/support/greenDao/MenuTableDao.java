package com.huofu.RestaurantOS.support.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MENU_TABLE.
*/
public class MenuTableDao extends AbstractDao<MenuTable, Long> {

    public static final String TABLENAME = "MENU_TABLE";

    /**
     * Properties of entity MenuTable.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property MenuName = new Property(0, String.class, "menuName", false, "MENU_NAME");
        public final static Property MenuType = new Property(1, Integer.class, "menuType", false, "MENU_TYPE");
        public final static Property MenuId = new Property(2, Long.class, "menuId", true, "MENU_ID");
        public final static Property MenuCreateTime = new Property(3, String.class, "menuCreateTime", false, "MENU_CREATE_TIME");
        public final static Property StoreId = new Property(4, Long.class, "storeId", false, "STORE_ID");
    };


    public MenuTableDao(DaoConfig config) {
        super(config);
    }
    
    public MenuTableDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MENU_TABLE' (" + //
                "'MENU_NAME' TEXT," + // 0: menuName
                "'MENU_TYPE' INTEGER," + // 1: menuType
                "'MENU_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 2: menuId
                "'MENU_CREATE_TIME' TEXT," + // 3: menuCreateTime
                "'STORE_ID' INTEGER);"); // 4: storeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MENU_TABLE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MenuTable entity) {
        stmt.clearBindings();
 
        String menuName = entity.getMenuName();
        if (menuName != null) {
            stmt.bindString(1, menuName);
        }
 
        Integer menuType = entity.getMenuType();
        if (menuType != null) {
            stmt.bindLong(2, menuType);
        }
 
        Long menuId = entity.getMenuId();
        if (menuId != null) {
            stmt.bindLong(3, menuId);
        }
 
        String menuCreateTime = entity.getMenuCreateTime();
        if (menuCreateTime != null) {
            stmt.bindString(4, menuCreateTime);
        }
 
        Long storeId = entity.getStoreId();
        if (storeId != null) {
            stmt.bindLong(5, storeId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2);
    }    

    /** @inheritdoc */
    @Override
    public MenuTable readEntity(Cursor cursor, int offset) {
        MenuTable entity = new MenuTable( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // menuName
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // menuType
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // menuId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // menuCreateTime
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // storeId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MenuTable entity, int offset) {
        entity.setMenuName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setMenuType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setMenuId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setMenuCreateTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStoreId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MenuTable entity, long rowId) {
        entity.setMenuId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MenuTable entity) {
        if(entity != null) {
            return entity.getMenuId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
