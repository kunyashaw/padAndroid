package com.huofu.RestaurantOS.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.bucketInstance;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster.OpenHelper;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;
import com.huofu.RestaurantOS.utils.mail.sendMail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static String tag = "commonUtils";
    public static String pathDataBase = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "data" + File.separator;
    public static boolean debug = true;

    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    public static final String TOP_BORDER = "╔═══════════════════════════════════════════";
    public static final String BOTTOM_BORDER = "╚═══════════════════════════════════════════";
    private static final String MIDDLE_BORDER = "╟────────────────────────────────────────────────────────────────────────────────────────";

    public static void LogWuwei(String tag, String msg) {
        Log.d(tag, msg);
        if(msg.contains("{") && msg.contains(":"))
        {
            msg = "\n"+CommonUtils.TOP_BORDER+"\n"+msg+"\n"+CommonUtils.BOTTOM_BORDER;
        }
        LogUtil.getInstance().getlogger(tag).debug(msg);
        LogUtil.getInstance().getlogger(LogUtil.LOG_ALL).debug(msg);
    }


    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static String logInfoFormatTojson(String json) {
        String msg = "";
        if (TextUtils.isEmpty(json)) {
            return "";
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                msg = jsonObject.toString(4);
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                msg = jsonArray.toString(4);
            }
        } catch (JSONException e) {
            return  "";
        }
        return  msg;
    }

    public static String getStockSupplyDateName(int step, String typeName, List<MealBucket> list_meal_bucket) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日");
        String str = sf.format(cal.getTime()) + " " + getWeekDay(step);
        System.out.print(str);
        if (typeName.equals("") || typeName == null) {
            if (list_meal_bucket != null) {
                long nowTime = System.currentTimeMillis() / 1000;

                Time time = new Time();
                time.setToNow();
                Calendar calDate = Calendar.getInstance();
                calDate.set(time.year, time.month, time.monthDay, 0, 0, 0);
                long todatTimeStamp = calDate.getTimeInMillis() / 1000;


                int timeBucketId = getTimeBuckerId(list_meal_bucket);
                for (int i = 0; i < list_meal_bucket.size(); i++) {
                    if (timeBucketId == list_meal_bucket.get(i).time_bucket_id) {
                        typeName = " " + list_meal_bucket.get(i).name;
                    }
                }

            }

        }

        return str + typeName;
    }


    public static String converBooleanToInt(String source)
    {
        if(!source.contains("true") && !source.contains("false"))
        {
            return source;
        }

        String result = "";
        if(source.contains("true"))
        {
            result = source.replaceAll("true","1");
        }

        if(source.contains("false"))
        {
            if(StringUtils.isEmpty(result))
            {
                result = source.replaceAll("false","0");
            }
            else
            {
                result = result.replaceAll("false","0");
            }

        }
        return  result;



    }

    /**
     * 通过handler向what发送text消息
     *
     * @param text
     * @param what
     * @param handler
     */
    public static void sendMsg(String text, int what, Handler handler) {
        try {
            Message msg = new Message();
            msg.what = what;
            if (text != null) {
                msg.obj = text;
            }
            if (handler != null) {
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "sendMsg failed:" + e.getMessage());
        }
    }

    /**  * 通过handler向what发送text消息
     *
     * @param text
     * @param what
     * @param handler
     */
    public static void sendObjMsg(Object obj, int what, Handler handler) {
        try {
            Message msg = new Message();
            msg.what = what;
            msg.obj = obj;
            if (handler != null) {
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "sendMsg failed:" + e.getMessage());
        }
    }


    /***
     * 获取打印机连接测试结果
     *
     * @param ctxt
     * @param from 0->MealDoneActivity 1>TakeUpActivity
     * @return 0为成功  -1为失败
     */
    public static int getPrinterTestResult(String ip) {

        int testResult = -1;
        if (CommonUtils.executeCammand(ip)) {
            testResult = 0;
        } else {
            testResult = -1;
        }
        return testResult;
    }


    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否是平板
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void showToast(Context ctxt, String text) {
        Toast.makeText(ctxt, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 获取此时对应的菜单类型
     *
     * @return
     */
    public static int getMenuType() {
        int menu_type = -1;
        Date date = new Date();
        int hour = date.getHours();
        if (hour >= 0 && hour <= 10)// 显示早餐
        {
            menu_type = 0;
        } else if (hour >= 11 && hour < 15)// 显示午餐
        {
            menu_type = 1;
        } else if (hour >= 15 && hour <= 23)// 显示晚餐
        {
            menu_type = 2;
        }
        return menu_type;

    }

    /***
     * 判断一个字符串是否为IP地址
     */
    public static boolean isIp(String ip) {// 判断是否是一个IP

        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }

    }

    /**
     * 检查IP地址是否处于alive状态
     *
     * @param ip
     * @return
     */
    public static boolean executeCammand(String ip) {

        if(BaseApi.CONNECT_FAILED)
        {
            return true;
        }
        CommonUtils.LogWuwei(tag, " \n1、executeCammand ip is " + ip);
        Runtime runtime = Runtime.getRuntime();
        String s = "";
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 3 " + ip);
            BufferedReader in = new BufferedReader(new InputStreamReader(mIpAddrProcess.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
            int mExitValue = mIpAddrProcess.waitFor();
            CommonUtils.LogWuwei(tag, " \n2、mExitValue " + mExitValue + " result is \n" + s);
            if (mExitValue == 0) {
                return true;
            } else {

                InetAddress addr = InetAddress.getByName(ip);
                CommonUtils.LogWuwei(tag, "\n3、ping failed ,now try function:isReachable");
                if (addr.isReachable(1000)) {
                    CommonUtils.LogWuwei(tag, "\n4、function:isReachable called result is true");
                    return true;
                } else {
                    CommonUtils.LogWuwei(tag, "\n4、isReachable failed now try to socket");
                    InetSocketAddress address = new InetSocketAddress(ip, 9100);
                    Socket socket = new Socket();
                    socket.connect(address, 1000);
                    socket.setSoTimeout(1000);
                    OutputStream out = socket.getOutputStream();

                    CommonUtils.LogWuwei(tag, "\n5、socket test success");

                    return true;
                }

            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
            CommonUtils.LogWuwei(tag, " Exception:" + ignore);

        }
        return BaseApi.CONNECT_FAILED;
    }

    /**
     * 将时间戳转换为"2015-03-09 09:15:08"这样的格式
     *
     * @param time
     * @return
     */
    public static String timeConvert(long cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        // 例如：cc_time=1291778220
        long lcc_time = 0;
        if (Long.toString(cc_time).length() > 10) {
            lcc_time = Long.valueOf(cc_time) / 1000;
        } else {
            lcc_time = Long.valueOf(cc_time);
        }

        String re_StrTime = sdf.format(new Date(lcc_time * 1000));
        return re_StrTime;
    }


    /***
     * 讲一个时间戳转为:7:30这样的格式
     *
     * @param cc_time
     * @return
     */
    public static String getStrTime(long cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        // 例如：cc_time=1291778220
        long lcc_time = 0;
        if (Long.toString(cc_time).length() > 10) {
            lcc_time = Long.valueOf(cc_time) / 1000;
        } else {
            lcc_time = Long.valueOf(cc_time);
        }

        String re_StrTime = sdf.format(new Date(lcc_time * 1000));

        int hour = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int min = Integer.parseInt((String) re_StrTime.subSequence(14, 16));
        String minute = "";
        if (min < 10) {
            minute = "0" + min;
        } else {
            minute = "" + min;
        }

        return hour + ":" + minute;
    }

    public static boolean timeWhetherEqual(long timeA,long timeB)
    {
        long timeANew = 0;
        if (Long.toString(timeA).length() > 10) {
            timeANew = Long.valueOf(timeA) / 1000;
        } else {
            timeANew = Long.valueOf(timeA);
        }

        long timeBNew = 0;
        if (Long.toString(timeA).length() > 10) {
            timeBNew = Long.valueOf(timeA) / 1000;
        } else {
            timeBNew = Long.valueOf(timeA);
        }

        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy年mm月dd日");

        Date dA = new Date(Long.valueOf(timeANew)* 1000L);
        Date dB = new Date(Long.valueOf(timeBNew)* 1000L);

        return sdf.format(dA).equals(sdf.format(dB));

    }

    /***
     * 将一个时间戳转为10月29日11：04这样的格式
     *
     * @param cc_time
     * @return
     */
    public static String getStrDateTime(long cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        // 例如：cc_time=1291778220
        long lcc_time = 0;
        if (Long.toString(cc_time).length() > 10) {
            lcc_time = Long.valueOf(cc_time) / 1000;
        } else {
            lcc_time = Long.valueOf(cc_time);
        }

        String re_StrTime = sdf.format(new Date(lcc_time * 1000));

        int month = Integer.parseInt((String) re_StrTime.subSequence(5, 7));
        int day = Integer.parseInt((String) re_StrTime.subSequence(8, 10));
        int hour = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int min = Integer.parseInt((String) re_StrTime.subSequence(14, 16));

        String minute = "";
        if (min < 10) {
            minute = "0" + min;
        } else {
            minute = "" + min;
        }

        return month+"月"+day+"日"+hour + ":" + minute;
    }


    /***
     * 将一个时间戳转为10月29日这样的格式
     *
     * @param cc_time
     * @return
     */
    public static String getStrDate(long cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        // 例如：cc_time=1291778220
        long lcc_time = 0;
        if (Long.toString(cc_time).length() > 10) {
            lcc_time = Long.valueOf(cc_time) / 1000;
        } else {
            lcc_time = Long.valueOf(cc_time);
        }

        String re_StrTime = sdf.format(new Date(lcc_time * 1000));

        int month = Integer.parseInt((String) re_StrTime.subSequence(5, 7));
        int day = Integer.parseInt((String) re_StrTime.subSequence(8, 10));

        return month+"月"+day+"日";
    }

    /***
     * 获取时间差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getTimeDiffToNow(long startTime, long endTime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

        long nowTime = endTime;
        String strNowTime = sdf.format(new Date(nowTime * 1000L));
        int hourNow = Integer.parseInt((String) strNowTime.subSequence(11, 13));
        int minNow = Integer.parseInt((String) strNowTime.subSequence(14, 16));
        int secNow = Integer.parseInt((String) strNowTime.subSequence(17, 19));

        String strStartTime = sdf.format(new Date(startTime * 1000L));
        int hourStart = Integer.parseInt((String) strStartTime.subSequence(11, 13));
        int minStart = Integer.parseInt((String) strStartTime.subSequence(14, 16));
        int secStart = Integer.parseInt((String) strStartTime.subSequence(17, 19));

        String result = "共等待:";

        int nowTotalSec = hourNow * 60 * 60 + minNow * 60 + secNow;
        int startTotalSec = hourStart * 60 * 60 + minStart * 60 + secStart;
        int secDiff = nowTotalSec - startTotalSec;

        if (secDiff > 0) {
            int hour = secDiff / (60 * 60);
            int min = (secDiff % (60 * 60)) / 60;
            int sec = secDiff % 60;

            if (hour != 0 && hour < 24) {
                result += hour + "小时";
            }
            if (min != 0) {
                result += min + "分钟";
            }
            if (sec != 0) {
                result += sec + "秒";
            }
        } else {
            result = "";
        }
        CommonUtils.LogWuwei(tag,result+"(strStartTime is " + strStartTime+"endTime is " + strNowTime);
        return result;
    }


    /**
     * 将时间戳转为字符串 将时间戳转换为"2015-03-09 09:15:08"这样的格式
     *
     * @param time
     * @return
     */
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time) / 1000;
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));

        int year = Integer.parseInt((String) re_StrTime.subSequence(0, 4));
        int month = Integer.parseInt((String) re_StrTime.subSequence(5, 7));
        int day = Integer.parseInt((String) re_StrTime.subSequence(8, 10));
        int hour = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int min = Integer.parseInt((String) re_StrTime.subSequence(14, 16));
        int sec = Integer.parseInt((String) re_StrTime.subSequence(17, 19));
        String monthStr = "";
        String dayStr = "";
        String hourStr = "";
        String minStr = "";
        String secStr = "";

        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = Integer.toString(month);
        }

        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = Integer.toString(day);
        }

        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = Integer.toString(hour);
        }

        if (min < 10) {
            minStr = "0" + min;
        } else {
            minStr = Integer.toString(min);
        }

        if (sec < 10) {
            secStr = "0" + sec;
        } else {
            secStr = Integer.toString(sec);
        }

        re_StrTime = year + "-" + monthStr + "-" + dayStr + " " + hourStr
                + ":" + minStr + ":" + secStr;

        return re_StrTime;
    }

    /**
     * 获取yyyy-mm-dd格式的日期
     *
     * @param step
     * @return
     */
    public static String getFormatDate(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sf.format(cal.getTime());
        return str;
    }

    /**
     * 函数功能是根据当前的日期，当step为正数时，算出加上step后的日期；step为负数时，算出减去step后的日期
     *
     * @param step ：要加或者减的天数
     * @return ：在当前日期的基础上加上或者减去step后的日期
     */
    public static String getDate(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

        String str = sf.format(cal.getTime());

        System.out.print(str);
        return str;
    }

    /**
     * 根据距离今天的日期差，得到是第几周周几：举例说今天是本年的第三周周四，那么getWeek(1)得到便是"第三周>周五"
     *
     * @param step
     * @return
     */
    public static String getWeek(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);

        int week = cal.get(cal.WEEK_OF_YEAR);
        int day = cal.get(cal.DAY_OF_WEEK);

        if (day == 1) {
            week = week - 1;
        }
        day = day - 1;

        String[] day_of_week = {"日", "一", "二", "三", "四", "五", "六", "日"};

        if (week == 0) {
            return "周" + day_of_week[day];
        }

        String str = "第" + week + "周" + ">周" + day_of_week[day];
        CommonUtils.LogWuwei(tag, "getWeek week is " + str + "day is " + day);
        return str;
    }

    /**
     * 根据距离今天的日期差，得到是周几：举例说今天是周二，那么getWeekDay(-1)得到的便是“周一”
     *
     * @param step
     * @return
     */
    public static String getWeekDay(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);

        int week = cal.get(cal.WEEK_OF_YEAR);
        int day = cal.get(cal.DAY_OF_WEEK);

        if (day == 1) {
            week = week - 1;
        }
        day = day - 1;

        String[] day_of_week = {"日", "一", "二", "三", "四", "五", "六", "日"};

        return "星期" + day_of_week[day];

    }


    /**
     * 获取距离今天step的具体信息（3月2日 星期二）
     *
     * @param step
     * @return
     */
    public static String getDateWeekDSay(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, step);

        int month = cal.get(cal.MONTH) + 1;
        int month_day = cal.get(cal.DAY_OF_MONTH);

        int week = cal.get(cal.WEEK_OF_YEAR);
        int day = cal.get(cal.DAY_OF_WEEK);

        if (day == 1) {
            week = week - 1;
        }
        day = day - 1;

        String[] day_of_week = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

        return month + "月" + month_day + "日" + "  " + day_of_week[day];
    }


    /**
     * 显示时间：4：21PM
     *
     * @return
     */
    public static String getTimeTakeup() {
        Time t = new Time();
        t.setToNow();
        int hour = t.hour;
        int minute = t.minute;
        String AMOrPM = "AM";
        String timeDisplay = "";
        if (hour > 12) {
            hour = hour - 12;
            AMOrPM = "PM";
        }
        if (minute < 10) {
            timeDisplay = hour + ":0" + minute + " " + AMOrPM;
        } else {
            timeDisplay = hour + ":" + minute + " " + AMOrPM;
        }

        return timeDisplay;
    }


    /**
     * 获取程序所占内存（log日志查看）
     *
     * @param mActivityManager
     */
    public static void getRunningAppProcessInfo() {
        ActivityManager mActivityManager = (ActivityManager) MainApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);


        if (mActivityManager != null) {
            //获得系统里正在运行的所有进程
            List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();

            for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
                // 进程ID号
                int pid = runningAppProcessInfo.pid;
                // 用户ID
                int uid = runningAppProcessInfo.uid;
                // 进程名
                String processName = runningAppProcessInfo.processName;
                // 占用的内存
                int[] pids = new int[]{pid};
                Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
                int memorySize = memoryInfo[0].dalvikPrivateDirty;
                if (processName.contains("com.huofu")) {
                    CommonUtils.LogWuwei(tag, "1、程序消耗内存memorySize=" + memorySize / 1024.0 + "M");
                    break;
                }

            }
        } else {
            //CommonUtils.LogWuwei(tag, "mActivityManager is null");
        }

    }


    /**
     * 获取程序所占内存（log日志查看）
     *
     * @param mActivityManager
     */
    public static void getRunningAppProcessInfo(ActivityManager mActivityManager, String what) {
        //ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (mActivityManager != null) {
            //获得系统里正在运行的所有进程
            List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();

            for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
                // 进程ID号
                int pid = runningAppProcessInfo.pid;
                // 用户ID
                int uid = runningAppProcessInfo.uid;
                // 进程名
                String processName = runningAppProcessInfo.processName;
                // 占用的内存
                int[] pids = new int[]{pid};
                Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
                int memorySize = memoryInfo[0].dalvikPrivateDirty;

                if (processName.equals("com.huofu.RestaurantOS.beta") || processName.equals("com.huofu.RestaurantOS")) {
                    //CommonUtils.LogWuwei(tag,"processName="+processName+",pid="+pid+"\nuid="+uid+"\nmemorySize="+memorySize/1024.0+"M");
                    CommonUtils.LogWuwei(tag, what + "  程序消耗内存memorySize=" + memorySize / 1024.0 + "M");
                    break;
                }

            }
        } else {
            //CommonUtils.LogWuwei(tag, "mActivityManager is null");
        }

    }


    /**
     * 取得在最小值和最大值之间的任意一个随机数(如果min和max相同，则默认产生1-100之间)
     *
     * @param max
     * @param min
     * @return
     */
    public static int getRandom(int min, int max) {
        if (max == min) {
            min = 0;
            max = 100;
        }
        Random random = new Random();

        return random.nextInt(max) % (max - min + 1) + min;
    }


    /**
     * 获取16位的随机aesKey
     *
     * @return
     */
    public static String getRandomAESKey() {
        return CommonUtils.getRandomAESKey(16);
    }

    public static String getRandomAESKey(int length) {
        String key = "";
        for (int i = 0; i < length; i++) {
            key += getRandom(0, 9);
        }

        return key;
    }


    /**
     * 字符串数组排序
     *
     * @param ss
     * @return
     */
    public static StringSort[] sortName(String[] ss) {
        StringSort mySs[] = new StringSort[ss.length];//创建自定义排序的数组
        for (int i = 0; i < ss.length; i++) {
            mySs[i] = new StringSort(ss[i]);
        }
        Arrays.sort(mySs);//排序
        for (int i = 0; i < mySs.length; i++) {
            System.out.println(mySs[i].s);
        }
        return mySs;
    }


    /**
     * 图片倒影效果
     *
     * @param image
     * @return
     */
    public static Bitmap getRefelection(Bitmap image) {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;

        // Get your bitmap from drawable folder
        Bitmap originalImage = image;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

		  /*Create a Bitmap with the flip matix applied to it.
		   We only want the bottom half of the image*/

        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);

        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);

        //Draw the reflection Image
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x99ffffff, 0x00ffffff, TileMode.CLAMP);

        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);

        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        // Draw a rectangle using the paint with our linear gradient

        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        if (originalImage != null && originalImage.isRecycled()) {
            originalImage.recycle();
            originalImage = null;
        }
        if (reflectionImage != null && reflectionImage.isRecycled()) {
            reflectionImage.recycle();
            reflectionImage = null;
        }
        return bitmapWithReflection;
    }


    public static int getTimeBuckerId(List<MealBucket> list_meal_bucket) {
        if(list_meal_bucket.size() == 0)
        {
            return  0;
        }
        long nowTime = System.currentTimeMillis() / 1000;
        Time time = new Time();
        time.setToNow();
        Calendar cal = Calendar.getInstance();
        cal.set(time.year, time.month, time.monthDay, 0, 0, 0);
        long todatTimeStamp = cal.getTimeInMillis() / 1000;


        for (int i = 0; i < list_meal_bucket.size(); i++) {
            MealBucket mb = list_meal_bucket.get(i);

            long startTime = mb.start_time / 1000 + todatTimeStamp;
            long endTime = mb.end_time / 1000 + todatTimeStamp;


            if (nowTime >= startTime && nowTime <= endTime) {
                CommonUtils.LogWuwei(tag, "id " + mb.time_bucket_id+"名称是："+mb.name);
                return mb.time_bucket_id;
            }
        }

        if (list_meal_bucket.size() == 1)//如果只有一个营业时间段
        {
            return list_meal_bucket.get(0).time_bucket_id;
        } else {
            if (nowTime < (list_meal_bucket.get(0).start_time / 1000 + todatTimeStamp))//如果小于最早的营业时间段开始的时间，那么则返回第一个营业时间段
            {
                return list_meal_bucket.get(0).time_bucket_id;
            }
            //如果当前营业时间大于最后一个营业时间段结束的时间，则返回最后一个营业时间段
            else if (nowTime > (list_meal_bucket.get(list_meal_bucket.size() - 1).end_time / 1000 + todatTimeStamp)) {
                return list_meal_bucket.get(list_meal_bucket.size() - 1).time_bucket_id;
            } else//判断距离哪个营业时间段的起始时间或者结束时间最近
            {
                List<bucketInstance> list = new ArrayList<bucketInstance>();
                for (int k = 0; k < list_meal_bucket.size(); k++) {
                    MealBucket mb = list_meal_bucket.get(k);
                    long startTime = mb.start_time / 1000 + todatTimeStamp;
                    long endTime = mb.end_time / 1000 + todatTimeStamp;

                    bucketInstance bi = new bucketInstance();
                    bi.setTimeBucketId(mb.time_bucket_id);
                    bi.setTimeDiff(Math.abs(startTime - nowTime));
                    list.add(bi);

                    bi = new bucketInstance();
                    bi.setTimeBucketId(mb.time_bucket_id);
                    bi.setTimeDiff(Math.abs(endTime - nowTime));
                    list.add(bi);
                }

                int index = 0;
                long minTimeDiff = list.get(0).timeDiff;
                for (int k = 0; k < list.size(); k++) {
                    if (list.get(k).timeDiff != 0) {
                        if (minTimeDiff >= list.get(k).timeDiff) {
                            minTimeDiff = list.get(k).timeDiff;
                            index = k;
                        }
                    }

                }

                return list.get(index).timeBucketId;
            }
        }
    }

    /**
     * 获取两个日期之间的间隔天数
     * 字符串格式为：yyyy-mm-dd
     *
     * @return
     */
    public static int getDiffDayCount(String beginTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        double day = 0;
        try {
            long begin = df.parse(beginTime).getTime();
            long end = df.parse(endTime).getTime();
            day = (end - begin) / (1000 * 3600 * 24);
        } catch (Exception e) {

        }
        return Integer.parseInt(DoubleDeal(day));

    }

    /**
     * 当前时间是否在营业时间段
     *
     * @return
     */
    public static boolean isITimeBucketList(List<MealBucket> list_meal_bucket, Context ctxt, Handler handler) {

        if (list_meal_bucket == null || list_meal_bucket.size() == 0) {
            LogWuwei(tag, "isITimeBucketList---list_meal_bucket  is null ");
            return false;
        }

        long nowTime = System.currentTimeMillis() / 1000;

        Time time = new Time();
        time.setToNow();
        Calendar cal = Calendar.getInstance();
        cal.set(time.year, time.month, time.monthDay, 0, 0, 0);
        long todatTimeStamp = cal.getTimeInMillis() / 1000;

        boolean flagInSoldTime = false;//是否在营业时间段内
        for (int i = 0; i < list_meal_bucket.size(); i++) {
            long start1Time = list_meal_bucket.get(i).start_time / 1000 + todatTimeStamp;
            long end1Time = list_meal_bucket.get(i).end_time / 1000 + todatTimeStamp;
            if (nowTime >= start1Time && nowTime <= end1Time) {
                flagInSoldTime = true;
                break;
            }
        }

        return flagInSoldTime;
    }


    /**
     * 获取字符串中某个字符之前的字符串，举个例子，传入113-1，‘-‘，那么返回的就是113
     *
     * @param sourceStr
     * @param ch
     * @return
     */
    public static String getSringBeforeChar(String sourceStr, char ch) {
        StringBuffer sb = new StringBuffer(sourceStr);
        StringBuffer newSb = new StringBuffer();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ch) {
                break;
            }
            newSb.append(sb.charAt(i));
        }

        return newSb.toString();

    }


    public static String getSringAfterChar(String sourceStr, char ch) {
        StringBuffer sb = new StringBuffer(sourceStr);
        StringBuffer newSb = new StringBuffer();
        boolean flagStart = false;
        int k=0;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ch) {
                k = i;
                break;
            }
        }
        if(k==0)
        {
            return sourceStr;
        }
        for( int t=k+1;t<sb.length();t++)
        {
            if(t<sb.length())
            {
                newSb.append(sb.charAt(t));
            }

        }
        return newSb.toString();

    }

    /**
     * 计算sourceStr中ch的个数，比如“肉夹馍*1\n鸡丝凉面*1\n鸡腿饭*3\n”统计“*”的个数
     *
     * @param sourceStr
     * @param sch
     * @return
     */
    public static int getCharNum(String sourceStr, char ch) {
        StringBuffer sb = new StringBuffer(sourceStr);
        int num = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ch) {
                num++;
            }
        }

        return num;
    }

    /**
     * 获取当前年月日和时间
     *
     * @return
     */
    public static String getTimeStampNow() {
        long time = System.currentTimeMillis();
        return getStrTime(Long.toString(time));
    }


    /**
     * 获取application的宽度和高度
     *
     * @param activity
     * @return
     */
    public static int[] getApplicationWidthAndHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:" + outRect.top + " ; left: " + outRect.left);
        int info[] = new int[2];
        info[0] = outRect.width();
        info[1] = outRect.height();
        return info;
    }

    /***
     * 获取view可绘制区域的宽度和高度
     *
     * @param activity
     * @return
     */
    public static int[] getViewMapWidthAndHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        int info[] = new int[2];
        info[0] = outRect.width();
        info[1] = outRect.height();
        return info;
    }


    /**
     * 获取actionBar的高度
     *
     * @param activity
     * @return
     */
    public static int getActionBarHeigth(Activity activity) {
        int applicaitonInfo[] = getApplicationWidthAndHeight(activity);
        int viewMapInfo[] = getViewMapWidthAndHeight(activity);
        return applicaitonInfo[1] - viewMapInfo[1];
    }


    /***
     * 获取title的高度
     *
     * @param activity
     * @return
     */
    public static int getTitleBarHeight(Activity activity) {
        int screenInfo[] = getScreenWidthAndHeight(activity);
        int applicationInfo[] = getApplicationWidthAndHeight(activity);
        return screenInfo[1] - applicationInfo[1];
    }


    /***
     * 获取整个屏幕的宽度和高度
     *
     * @param activity
     * @return
     */
    public static int[] getScreenWidthAndHeight(Activity activity) {
        Display disp = activity.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
        int info[] = new int[2];
        info[0] = outP.x;
        info[1] = outP.y;
        return info;
    }


    /***
     * 判断一个时间戳是否属于今天
     */
    public static boolean whetherTheTimestapToday(long timeStamp) {
        long time = timeStamp;
        if (Long.toString(time).length() > 10) {
            time = timeStamp / 1000;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String re_StrTime = sdf.format(new Date(time * 1000L));


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        String str = sf.format(cal.getTime());

        return re_StrTime.equals(str);

    }


    /***
     * 计算距离现在还有多少分钟
     *
     * @param timeStamp
     * @return
     */
    public static String getMinutesLeft(long timeStamp) {

        Time time = new Time();
        time.setToNow();
        String beginTime = time.year + "-" + (time.month+1) + "-" + time.monthDay;

        Date dateDest = new Date(timeStamp);
        Calendar calendarDest = Calendar.getInstance();
        calendarDest.setTime(dateDest);
        String endTime = calendarDest.get(Calendar.YEAR)+ "-" + (calendarDest.get(Calendar.MONTH)+1) + "-" + calendarDest.get(Calendar.DATE);

        long diffInDays = getQuot(beginTime,endTime);
        //int diffInDays = CommonUtils.getDiffDayCount(beginTime, endTime);
        Log.e(tag,"----beginTime is "+beginTime+" endTime is "+endTime+" diffInDays is "+diffInDays);
        //int diffInDays = (int)((System.currentTimeMillis())-timeStamp)/(1000*60*60*24);

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String re_StrTime = sdf.format(new Date(timeStamp));

        int hourDest = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int minDest = Integer.parseInt((String) re_StrTime.subSequence(14, 16));


        String result = "";
        int minsDiff = (hourDest - hour) * 60 + (minDest - min);

        if(diffInDays ==0)//今天的订单
        {
            if (minsDiff >= 0) {
                if (minsDiff > 60) {
                    if (minsDiff % 60 == 0) {
                        result = "还剩" + (hourDest - hour) + "小时";
                    } else {
                        result = "还剩" + minsDiff / 60 + "小时" + minsDiff % 60 + "分";
                    }

                } else {
                    result = "还剩" + minsDiff + "分钟";
                }
            } else if (minsDiff < 0) {
                minsDiff = minsDiff * (-1);
                if (minsDiff > 60) {
                    if (minsDiff % 60 == 0) {
                        result = "已超时" + minsDiff / 60 + "小时";
                    } else {
                        result = "已超时" + minsDiff / 60 + "小时" + minsDiff % 60 + "分";
                    }

                } else {
                    result = "已超时" + minsDiff + "分";
                }
            }
        }
        else if(diffInDays > 0)
        {
            if(hourDest-hour > 0)
            {
                result = "还剩" + diffInDays+"天"+(hourDest - hour) + "小时";
            }
            else if(hourDest == hour)
            {
                result = "还剩" + diffInDays+"天";
            }
            else
            {
                diffInDays--;
                int hourDiff = hourDest-hour+24;
                if(diffInDays == 0)
                {
                    result = "还剩" + hourDiff+"小时";
                }
                else {
                    result = "还剩" + diffInDays+"天"+hourDiff+"小时";;
                }
            }

        }
        else if(diffInDays < 0) {
            diffInDays = diffInDays*(-1);
            result = "已超时" + diffInDays+"天";
        }
        return result;
    }

    /***
     * 根据时间戳获取到5：30格式的时间
     *
     * @param timeStamo
     * @return
     */
    public static String getHourMin(long timeStamp) {
        if (Long.toString(timeStamp).length() > 10) {
            timeStamp = timeStamp / 1000;
        }
        String re_StrTime = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        re_StrTime = sdf.format(new Date(timeStamp * 1000L));

        int hour = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int min = Integer.parseInt((String) re_StrTime.subSequence(14, 16));

        return hour + ":" + min;
    }


    /**
     * 将当前的时间戳和参数对比，算出分钟的时间差
     *
     * @param timeStamp
     * @return
     */
    public static int getMinDif(long timeStamp) {
        if (Long.toString(timeStamp).length() > 10) {
            timeStamp = timeStamp / 1000;
        }

        String re_StrTime = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        re_StrTime = sdf.format(new Date(timeStamp * 1000L));

        int hour = Integer.parseInt((String) re_StrTime.subSequence(11, 13));
        int min = Integer.parseInt((String) re_StrTime.subSequence(14, 16));

        Time t = new Time();
        t.setToNow();
        return (t.hour - hour) * 60 + (t.minute - min);
    }


    /**
     * 判断是否为手机号
     *
     * @param inputText
     * @return
     */
    public static boolean isPhoneNumber(String inputText) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }


    /***
     * 修改图片的点击区域
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     * @return
     */
    public static void viewIncreasedClickedArea(final View view, final int top, final int bottom, final int left, final int right) {
        final View parent = (View) view.getParent();

        parent.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                final Rect r = new Rect();
                view.getHitRect(r);
                r.left += left;
                r.right += right;
                r.top += top;
                r.bottom += bottom;

                parent.setTouchDelegate(new TouchDelegate(r, view));
            }
        });
    }


    /****
     * double value = 100.34567;
     * double ret = round(value,4,BigDecimal.ROUND_HALF_UP)
     * ret为100.3457
     *
     * @param value        double数据
     * @param scale        精度位数（保留的小数位数）
     * @param roundingMode 精度取值方式
     * @return 精度计算后的数据
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }


    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static int getIntWeekOfDate() {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        dateFm.format(date);

        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        //   CommonUtils.LogWuwei(tag,"今天是 "+weekDays[w]);
        return weekDays[w];
    }


    /**
     * 根据今天是周几获取上周周一到周日的日期
     */
    public static String[] getLastWeekDate() {
        int today = CommonUtils.getIntWeekOfDate();//获取今天周几
        String baseDate = "";//初始化上周一的日期
        String date[] = {null, null, null, null, null, null, null};
        int index = 0;
        switch (today)////得到上周一的日期
        {
            case 1:
                baseDate = CommonUtils.getDate(-7);
                index = -7;
                break;
            case 2:
                baseDate = CommonUtils.getDate(-8);
                index = -8;
                break;
            case 3:
                baseDate = CommonUtils.getDate(-9);
                index = -9;
                break;
            case 4:
                baseDate = CommonUtils.getDate(-10);
                index = -10;
                break;
            case 5:
                baseDate = CommonUtils.getDate(-11);
                index = -11;
                break;
            case 6:
                baseDate = CommonUtils.getDate(-12);
                index = -12;
                break;
            case 7:
                baseDate = CommonUtils.getDate(-13);
                index = -13;
                break;
        }

        for (int i = 0; i < 7; i++) {
            date[i] = CommonUtils.getDate(index + i);
            int k = i + 1;
            CommonUtils.LogWuwei(tag, "上周" + k + " is " + date[i]);
        }
        return date;
    }

    /**
     * 根据本周周一到周日的日期
     */
    public static String[] getThisWeekDate() {
        int today = CommonUtils.getIntWeekOfDate();//获取今天周几
        String date[] = {null, null, null, null, null, null, null};
        int index = 0;
        switch (today)////得到上周一的日期
        {
            case 1:
                index = 0;
                break;
            case 2:
                index = -1;
                break;
            case 3:
                index = -2;
                break;
            case 4:
                index = -3;
                break;
            case 5:
                index = -4;
                break;
            case 6:
                index = -5;
                break;
            case 7:
                index = -6;
                break;
        }

        for (int i = 0; i < 7; i++) {
            date[i] = CommonUtils.getDate(index + i);
            int k = i + 1;
            CommonUtils.LogWuwei(tag, "本周" + k + " is " + date[i]);
        }
        return date;
    }


    /**
     * 获取下周周一到周日的日期
     */
    public static String[] getNextWeekDate() {
        int today = CommonUtils.getIntWeekOfDate();//获取今天周几
        String date[] = {null, null, null, null, null, null, null};
        int index = 0;
        switch (today)////得到下周的日期
        {
            case 1:
                index = 7;
                break;
            case 2:
                index = 6;
                break;
            case 3:
                index = 5;
                break;
            case 4:
                index = 4;
                break;
            case 5:
                index = 3;
                break;
            case 6:
                index = 2;
                break;
            case 7:
                index = 1;
                break;
        }

        for (int i = 0; i < 7; i++) {
            date[i] = CommonUtils.getDate(index + i);
            int k = i + 1;
            CommonUtils.LogWuwei(tag, "下周" + k + " is " + date[i]);
        }
        return date;
    }

    /***
     * 根据日期获取时间戳
     *
     * @param normalDate 20141228
     * @return 1419696000
     */
    public static long getTimeStamp(Long normalDate) {
        Long detail_date_tmp = normalDate;
        String date_str = Long.toString(detail_date_tmp);
        String year = date_str.substring(0, 4);
        String month = date_str.substring(4, 6);
        String day = date_str.substring(6, 8);
        //CommonUtils.LogWuwei(tag,"detail_date_tmp is "+detail_date_tmp+" "+ year+":"+month+":"+day);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        Calendar cal = Calendar.getInstance();
        String parseStr = year + "-" + month + "-" + day;

        try {
            date = df.parse(parseStr);
            cal = Calendar.getInstance();
            cal.setTime(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long timeStamp = cal.getTimeInMillis() / 1000;

        CommonUtils.LogWuwei(tag, "timeStamp is " + timeStamp);

        return timeStamp;
    }


    public static int getScreenWidth(Context context) {
        if (context != null) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        } else {
            CommonUtils.LogWuwei(tag, "getScreenWidth arg null");
            return -1;
        }

    }

    public static int getScreenHeight(Context context) {

        if (context != null) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
        } else {
            CommonUtils.LogWuwei(tag, "getScreenWidth arg null");
            return -1;
        }

    }

    /**
     * 根据年月日，获取是周几
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String CaculateWeekDay(int y, int m, int d) {

        String strDate = y + "-" + m + "-" + d;// 定义日期字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        Date date = null;
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        CommonUtils.LogWuwei(tag, strDate + "周" + dayForWeek);
        return Integer.toString(dayForWeek);

    }


    /**
     * 获取今日的日期，举例：20141110，20140110
     *
     * @return,日期的字符串
     */
    public static String getNowDateString() {
        Date date = new Date();
        //年份
        int year = date.getYear() + 1900;
        //月份
        int month = date.getMonth() + 1;
        //日期
        int day = date.getDate();

        String date_string;
        if (month < 10) {
            if (day < 10) {
                date_string = Integer.toString(year) + "0" + Integer.toString(month) + "0" + Integer.toString(day);
            } else {
                date_string = Integer.toString(year) + "0" + Integer.toString(month) + Integer.toString(day);
            }

        } else {
            if (day < 10) {
                date_string = Integer.toString(year) + Integer.toString(month) + "0" + Integer.toString(day);
            } else {
                date_string = Integer.toString(year) + Integer.toString(month) + Integer.toString(day);
            }

        }
        return date_string;
    }


    /***
     * 获取到本周一的日期
     *
     * @return
     */
    public static long getThisMonday() {
        int today = getIntWeekOfDate();//获取今天是周几
        long monday = (long) 0;
        int step = 0;
        switch (today) {
            case 1:
                step = 0;
                break;
            case 2:
                step = -1;
                break;
            case 3:
                step = -2;
                break;
            case 4:
                step = -3;
                break;
            case 5:
                step = -4;
                break;
            case 6:
                step = -5;
                break;
            case 7:
                step = -6;
                break;
        }
        monday = Long.parseLong(getNowDateString()) + step;
        return monday;
    }

    /**
     * 获取到上周的周一是几号
     *
     * @return
     */
    public static long getLastMonday() {
        int today = getIntWeekOfDate();//获取今天是周几
        long monday = (long) 0;
        int step = 0;
        switch (today) {
            case 1:
                step = 0;
                break;
            case 2:
                step = -1;
                break;
            case 3:
                step = -2;
                break;
            case 4:
                step = -3;
                break;
            case 5:
                step = -4;
                break;
            case 6:
                step = -5;
                break;
            case 7:
                step = -6;
                break;
        }
        monday = Long.parseLong(getNowDateString()) - 7 + step;
        return monday;
    }

    /**
     * 获取到下周的周一是几号
     *
     * @return
     */
    public static long getNextMonday() {
        int today = getIntWeekOfDate();//获取今天是周几
        long monday = (long) 0;
        int step = 0;
        switch (today) {
            case 1:
                step = 0;
                break;
            case 2:
                step = -1;
                break;
            case 3:
                step = -2;
                break;
            case 4:
                step = -3;
                break;
            case 5:
                step = -4;
                break;
            case 6:
                step = -5;
                break;
            case 7:
                step = -6;
                break;
        }
        monday = Long.parseLong(getNowDateString()) + 7 + step;
        return monday;
    }

    /***
     * 从今天起往前寻找8周内每个周一的日期
     */
    public static long[] checkRecentEfficientDataFromToday() {
        long modayData[] = new long[8];
        int today = getIntWeekOfDate();//获取今天是周几
        int step = 0;
        switch (today) {
            case 1:
                step = 0;
                break;
            case 2:
                step = -1;
                break;
            case 3:
                step = -2;
                break;
            case 4:
                step = -3;
                break;
            case 5:
                step = -4;
                break;
            case 6:
                step = -5;
                break;
            case 7:
                step = -6;
                break;
        }

        for (int i = 0; i < 8; i++) {
            modayData[i] = Long.parseLong(getDate(step - 7 * i));
            CommonUtils.LogWuwei(tag, "monday date is " + modayData[i]);
        }
        return modayData;
    }


    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate() {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        dateFm.format(date);

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        CommonUtils.LogWuwei(tag, "今天是 " + weekDays[w]);
        return weekDays[w];
    }


    /**
     * 将”20141224” 解析成“2014年12月24日”
     *
     * @param date
     * @return
     */
    public static String parseDate(Long date) {
        int year = (int) (date / 10000);
        int month = (int) (date % 10000) / 100;
        int day = (int) (date % 10000) % 100;
        return year + "年" + month + "月" + day + "日";

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * fontScale + 0.5f);
    }


    /***
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context, DaoMaster daoMaster) {
        if (daoMaster == null) {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context, pathDataBase + "menu_5wei_db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context, DaoSession daoSession, DaoMaster daoMaster) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context, daoMaster);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }


    public static int getHexColor(int[] color) {
        return android.graphics.Color.rgb(color[0], color[1], color[2]);
    }


    public static String getModelInfo() {
        Build bd = new Build();
        String model = bd.MODEL;
        return model;
    }


    /***
     * @param ctxt
     * @param k    0 出餐
     *             1 取号
     *             2 设置
     *             3 登录
     *             4 网络
     *             5 所有日志
     */
    public static void sendMailToMe(Context ctxt, int k) {
        sendMail.sendEmail(ctxt, k);
    }

    public static void sendMailToMe(Context ctxt, String msg) {
        sendMail.sendEmail(ctxt, msg);
    }


    public static String DoubleDeal(double num) {
        String numStr = Double.toString(num);
        for (int k = 0; k < numStr.length(); k++) {
            if (numStr.charAt(k) == '.') {
                if (numStr.charAt(k + 1) == '0') {
                    return Integer.toString((int) num);
                }
            }
        }
        return Double.toString(num);
    }


    /***
     * 请求小票模板信息
     *
     * @param ctxt
     * @param handler
     */
    public static void getTemplateList(Context ctxt, Handler handler) {

        ApisManager.GetReceiptTemplateList(new ApiCallback() {
            @Override
            public void success(Object object) {

            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }

    public static String getDate(){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date dd = new Date();
        return ft.format(dd);
    }

    public static long getQuot(String beginTime, String endTime){
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateBegin = ft.parse( beginTime );
            Date dateEnd = ft.parse( endTime );
            quot = dateEnd.getTime() - dateBegin.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

}
