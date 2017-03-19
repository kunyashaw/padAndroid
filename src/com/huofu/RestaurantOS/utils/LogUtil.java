package com.huofu.RestaurantOS.utils;

import android.os.Environment;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by tim on 10/8/15.
 */
public class LogUtil {
    private static LogUtil ourInstance = new LogUtil();

    public static LogUtil getInstance() {
        return ourInstance;
    }
    public static String  LOG_ALL = "TAG_LOG_ALL";

    private HashMap<String,Logger> logMap;

    private LogUtil() {
        this.logMap = new HashMap<String,Logger>();
    }

    public String getLogFileName(String tag){
        return this._getLogFilePath(tag)+ tag+".log";
    }

    public Logger getlogger(String tag){
        Logger target = logMap.get(tag);
        if(target ==null){
            target= this.loadLogger(tag);
            this.logMap.put(tag,target);
        }
        return target;
    }

    private String _getLogFilePath(String tag){
        return Environment.getExternalStorageDirectory() + File.separator + "huoFu" + File.separator + "alllog" + File.separator+  tag + File.separator;
    }



    private Logger loadLogger(String tag){
        Logger logger = Logger.getLogger(tag);
        try {
            DailyRollingFileAppender fileAppender= new DailyRollingFileAppender(new PatternLayout("%d %p [%c] - %m%n"), this.getLogFileName(tag),".yyyy-MM-dd"); //  path +  CommonUtils.getDate(0) + "(" + CommonUtils.getWeekDay(0) + ").log");
            fileAppender.setName("DailyRollingFileAppender");
            logger.addAppender(fileAppender);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return logger;
    }




}
