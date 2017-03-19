package com.huofu.RestaurantOS.support.greenDao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig menuDetailDaoConfig;
    private final DaoConfig menuTableDaoConfig;

    private final MenuDetailDao menuDetailDao;
    private final MenuTableDao menuTableDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        menuDetailDaoConfig = daoConfigMap.get(MenuDetailDao.class).clone();
        menuDetailDaoConfig.initIdentityScope(type);

        menuTableDaoConfig = daoConfigMap.get(MenuTableDao.class).clone();
        menuTableDaoConfig.initIdentityScope(type);

        menuDetailDao = new MenuDetailDao(menuDetailDaoConfig, this);
        menuTableDao = new MenuTableDao(menuTableDaoConfig, this);

        registerDao(MenuDetail.class, menuDetailDao);
        registerDao(MenuTable.class, menuTableDao);
    }
    
    public void clear() {
        menuDetailDaoConfig.getIdentityScope().clear();
        menuTableDaoConfig.getIdentityScope().clear();
    }

    public MenuDetailDao getMenuDetailDao() {
        return menuDetailDao;
    }

    public MenuTableDao getMenuTableDao() {
        return menuTableDao;
    }

}
