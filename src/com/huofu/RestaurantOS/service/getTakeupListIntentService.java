package com.huofu.RestaurantOS.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.Notification;
import com.huofu.RestaurantOS.bean.NotificationType;
import com.huofu.RestaurantOS.bean.storeOrder.Amount;
import com.huofu.RestaurantOS.bean.storeOrder.StoreHeartBeatInfo;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.manager.TicketsManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.AutoPushMealUtils;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.SimpleSideDrawer;

/****
 * 后台服务，会检测是否有出餐单
 */

public class getTakeupListIntentService extends IntentService {


    public static long index = 0;
    public static String tag = "getTakeupListIntentService";
    public static boolean flagActivate = false;
    public static boolean flagServiceOn = true;
    public static Context context = null;
    public static final long DEALING_TIME = 120;
    public static boolean flagGetIdleMealPortsInHeart = false;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = MainApplication.getContext();
        CommonUtils.LogWuwei(tag, "on create");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        CommonUtils.LogWuwei(tag, "on destroy");
    }

    public getTakeupListIntentService() {

        super(getTakeupListIntentService.class.getName());
        CommonUtils.LogWuwei(tag, "getTakeupListIntentService construction");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (BaseApi.flagActivateService) {
            CommonUtils.LogWuwei(tag, "fuck yes");
        } else {
            CommonUtils.LogWuwei(tag, "fuck no");
            return;
        }
        if (!flagActivate) {
            CommonUtils.LogWuwei(tag, "service启动了");
            flagActivate = true;



            new Thread() {
                public void run() {
                    while (true) {
                        executeHeatBeat();
                    }
                }
            }.start();
        } else {
            CommonUtils.LogWuwei(tag, "service已经在运行了");
        }

    }


    /**
     * 通知中心
     */
    private void executeServerNotification() {
        ApisManager.getDeliveryOrderAnalysisInfo(new ApiCallback() {
            @Override
            public void success(Object object) {
                NotificationDeal((Amount) object);
            }


            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }

    /**
     * 外送通知处理
     *
     * @param amount
     */
    private void NotificationDeal(Amount amount) {

        try {
            String nowClassName = MainApplication.getmActivity().getLocalClassName();
            if (nowClassName.contains("LoginActivity") || nowClassName.contains("MealDoneActivity") || nowClassName.contains("TakeUpActivity")) {
                MainApplication.notificationList.clear();
                return;
            }

            int numPrepare = amount.wait_for_prepare;
            int numDelivery = amount.wait_for_delivery;
            boolean flagChange = false;
            if (numPrepare > 0) {

                Notification nt = new Notification();
                nt.tips = "有" + numPrepare + "个外送订单急需备餐，请及时处理";
                final String tips = nt.tips;
                nt.type = NotificationType.NOTIFICATION_TYPE_MEAL_PREPARE_TASK;
                nt.ocl = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();

                    }
                };

                boolean flagAdd = true;
                for (int k = 0; k < MainApplication.notificationList.size(); k++) {
                    Notification ntLoop = MainApplication.notificationList.get(k);
                    if (ntLoop.type == NotificationType.NOTIFICATION_TYPE_MEAL_PREPARE_TASK) {
                        ntLoop.tips = nt.tips;
                        flagAdd = false;
                        if (numPrepare != Integer.parseInt(ntLoop.number)) {
                            flagChange = true;
                            ntLoop.number = Integer.toString(numPrepare);
                        }
                        break;
                    }
                }
                if (flagAdd) {
                    flagChange = true;
                    nt.number = Integer.toString(numPrepare);
                    Long LastDealtime = MainApplication.LastDealTimeList[NotificationType.NOTIFICATION_TYPE_MEAL_PREPARE_TASK];
                    Long NowTime = System.currentTimeMillis() / 1000;
                    Long Diff = NowTime - LastDealtime;
                    if (Diff > DEALING_TIME) {
                        MainApplication.notificationList.add(nt);
                    }
                    //MainApplication.notificationList.add(nt);
                }
            } else {
                for (int k = 0; k < MainApplication.notificationList.size(); k++) {
                    Notification nt = MainApplication.notificationList.get(k);
                    if (nt.type == NotificationType.NOTIFICATION_TYPE_MEAL_PREPARE_TASK) {
                        MainApplication.notificationList.remove(k);
                        flagChange = true;
                        break;
                    }

                }
            }

            if (numDelivery > 0) {
                Notification nt = new Notification();
                nt.tips = numDelivery + "个订单备餐完毕，请安排配送";
                final String tips = nt.tips;
                nt.type = NotificationType.NOTIFICATION_TYPE_MEAL_DELIVERY_TASK;
                nt.ocl = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
                    }
                };
                boolean flagAdd = true;
                for (int k = 0; k < MainApplication.notificationList.size(); k++) {
                    Notification ntLoop = MainApplication.notificationList.get(k);
                    if (ntLoop.type == NotificationType.NOTIFICATION_TYPE_MEAL_DELIVERY_TASK) {
                        ntLoop.tips = nt.tips;
                        flagAdd = false;
                        if (numDelivery != Integer.parseInt(ntLoop.number)) {
                            flagChange = true;
                            ntLoop.number = Integer.toString(numDelivery);
                        }
                        break;
                    }
                }
                if (flagAdd) {
                    flagChange = true;
                    nt.number = Integer.toString(numDelivery);
                    Long LastDealtime = MainApplication.LastDealTimeList[NotificationType.NOTIFICATION_TYPE_MEAL_DELIVERY_TASK];
                    Long NowTime = System.currentTimeMillis() / 1000;
                    Long Diff = NowTime - LastDealtime;
                    if (Diff > DEALING_TIME) {
                        MainApplication.notificationList.add(nt);
                    }
                }
            } else {
                for (int k = 0; k < MainApplication.notificationList.size(); k++) {
                    Notification nt = MainApplication.notificationList.get(k);
                    if (nt.type == NotificationType.NOTIFICATION_TYPE_MEAL_DELIVERY_TASK) {
                        MainApplication.notificationList.remove(k);
                        flagChange = true;
                        break;
                    }
                }
            }


            boolean flagNotify = true;
            try {

                if (flagChange) {
                    flagNotify = true;
                } else {
                    flagNotify = false;
                }

                if (MainApplication.getmActivity() != null) {

                    if (nowClassName.contains("LoginActivity") || nowClassName.contains("MealDoneActivity") || nowClassName.contains("TakeUpActivity")) {
                        flagNotify = false;
                    }
                }
            } catch (Exception e) {
                CommonUtils.LogWuwei(tag, "失败：" + e.getMessage());
            }


            if (flagNotify) {
                MainApplication.getmActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleSideDrawer.adapter.notifyDataSetChanged();
                    }
                });
            }


        } catch (Exception e) {

        }

    }


    /**
     * 执行心跳任务
     */
    private void executeHeatBeat() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ApisManager.doHeartBeat(new ApiCallback() {
            @Override
            public void success(Object object) {
                flagGetIdleMealPortsInHeart = LocalDataDeal.readFromPushMealSetting(MainApplication.getContext());
                StoreHeartBeatInfo shbf = (StoreHeartBeatInfo) object;

                AutoPushMealUtils.checkAppTaskPort();

                //有空闲的出餐口 && 设置中设置为允许自动出餐，则会进行出餐口的登记和递归启动
                if (shbf.has_idle_port > 0 && flagGetIdleMealPortsInHeart) {
                    AutoPushMealUtils.heatBeatMainDeal();//得到空闲出餐口,判断是否可用，如果可用则进行登记
                }

                //如果开启了出餐 &&  设置为手工出餐，则会进入递归
                if(!TicketsManager.getInstance().stopCheck && PushMealActivity.isActive && !LocalDataDeal.readPushMealWhetherAuto(context))
                {
                    if(shbf.today_serial_number > TicketsManager.getInstance().latestNumber)
                    {
                        boolean flagIsChecking = TicketsManager.getInstance().isChecking;
                        if(!flagIsChecking)
                        {
                            TicketsManager.getInstance().startCheckNewTickets(LocalDataDeal.readFromLocalMealDoneChooseMealPortId(context));
                        }
                    }
                }
            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }
}
