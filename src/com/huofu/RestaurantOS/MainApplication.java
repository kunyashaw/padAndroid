package com.huofu.RestaurantOS;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.Notification;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.utils.CrashHandler;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim on 6/8/15.
 */
public class MainApplication extends Application {
    private static Context mContext;
    private static Handler mUiHandler;
    private static Class<?> mNowForegroundClass;
    private static String tag = "MainApplicatonTag";
    private static Activity mActivity = null;
    public static List<Notification> notificationList=new ArrayList<Notification>();//通知列表

    //各个类型的通知上次点击的时间,对应NotificationType
     /* 0;//打印机故障
    1;//备餐任务
    2;//外送任务
    3;//差评提醒
    4;//新的反馈提醒*/
    public static Long[] LastDealTimeList = new Long[5];

    /**
     * 微信通知
     * 1、explv 单击微信通知
     * 2、explv 长按时部分出餐进行微信通知
     * 3、通过键盘输入数字，进行微信通知
     * 4、出餐历史中进行叫号，进行微信通知
     *
     */


    /**
     *电视叫号 （前提：非右滑出餐&&堂食->才会触发叫号）
     * 如果是出餐叫号，直接叫号
     * 如果是尾单叫号，在为尾单时叫号
     * 如果是出餐不叫号，则不叫号
     */


    @Override
    public void onCreate() {
        super.onCreate();
        initInformData();
        mContext = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(mContext);

        ;
        if(!StringUtils.isEmpty((String) LocalDataDeal.readFromLocalWorkKey(MainApplication.getContext()).get("workKey")))
        {
            ApisManager.GetTimeBucketList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    TemplateModulsParse.mealBucketList = (List<MealBucket>) object;
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        }

    }

    public void initInformData()
    {
        LastDealTimeList[0] = 0L;
        LastDealTimeList[1] = 0L;
        LastDealTimeList[2] = 0L;
        LastDealTimeList[3] = 0L;
        LastDealTimeList[4] = 0L;
    }

    public static void setmActivity(Activity activity)
    {
        mActivity = activity;
    }

    public static Activity getmActivity()
    {
        return mActivity;
    }

    public static void setmNowForegroundClass(Class<?> mClass) {
        mNowForegroundClass = mClass;
    }

    public static Class<?> getmNowForegroundClass() {
        return mNowForegroundClass;
    }

    public static void setHandler(Handler handler) {
        mUiHandler = handler;
    }

    public static Handler getHandler() {
        return mUiHandler;
    }

    /**获取Context.
     * @return
     */
    public static Context getContext(){
        return mContext;
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
