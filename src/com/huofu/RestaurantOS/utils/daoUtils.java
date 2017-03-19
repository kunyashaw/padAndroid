package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.os.Environment;

import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster.OpenHelper;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;

import java.io.File;

public class daoUtils {

	
	public static String pathDataBase = Environment.getExternalStorageDirectory()+File.separator+"huofu"+ File.separator+"data"+ File.separator; 
	public static String pathFonts =  Environment.getExternalStorageDirectory()+File.separator+"huofi"+ File.separator+"fonts"+ File.separator;

	/** 
     * 取得DaoMaster 
     *  
     * @param context 
     * @return 
     */  
     public  static DaoMaster getDaoMaster(Context context) 
     {  
        OpenHelper helper = new DaoMaster.DevOpenHelper(context,pathDataBase+"menu_5wei_db", null);  
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());  
        return daoMaster;  
     }  
	      
	    
    /** 
     * 取得DaoSession 
     *  
     * @param context 
     * @return 
     */  
     public  static DaoSession getDaoSession(Context context) 
     {  
        DaoSession daoSession = getDaoMaster(context).newSession();  
        return daoSession;  
     }  
	
}
