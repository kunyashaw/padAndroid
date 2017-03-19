package com.huofu.RestaurantOS.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.NavigationBarFuncAdapter;
import com.huofu.RestaurantOS.adapter.NavigationBarGridviewAdatper;
import com.huofu.RestaurantOS.support.shapedImage.CircleImageView;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.ui.pannel.Lable.LableActivity;
import com.huofu.RestaurantOS.ui.pannel.clientMenu.ClientMenuSplashActivty;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.ui.pannel.stockPlan.StockPlanActivity;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.ui.pannel.takeUp.TakeUpActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.launchPadUtils;
import com.huofu.RestaurantOS.widget.SimpleSideDrawer;
import com.huofu.RestaurantOS.widget.SystemBarTintManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by tim on 5/8/15.
 */
public abstract class BaseActivity extends FragmentActivity {

    public Context ctxt;
    public static String tag = "BaseActivity";
    public View viewLoadingPop;//“加载中”弹窗showAtLocation时依附的空间
    protected Handler mUiHandler = null;
    public SimpleSideDrawer mslidingMenu = null;
    public SystemBarTintManager tintManager = null;

    public List listFuncMap = null;// 导航栏各功能得集合
    public List listAllFuncMap = null;// 所有功能集合

    Dialog dialog_show_all_func = null;
    public PopupWindow popwindowAllFunc = null;
    public PopupWindow pop_loading = null;
    public PopupWindow popShowErrorOneOption = null;
    public PopupWindow popShowErrorTwoOptions = null;


    public ListView listviewLeftNavigation = null;
    public BitmapUtils bitmapUtils;
    public BitmapDisplayConfig bigPicDisplayConfig;
    public LayoutInflater inflater;
    public View.OnClickListener oclGoToSetting;
    public View.OnClickListener oclGoToSettingAutoTakeup;
    public TextView tvShowLoadignText;
    public static String pathLoginStaffCache = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "ImageCache" + File.separator;
    public static String pathApkDownloadCache = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "lastedVersion.apk";
    int width;
    int height;

    public boolean active = false;

    protected abstract void dealWithmessage(Message msg);

    public BaseActivity() {

        ctxt = MainApplication.getContext();

        mUiHandler = new Handler() {
            public void handleMessage(Message msg) {
                dealWithmessage(msg);
            }
        };

        MainApplication.setHandler(mUiHandler);
        MainApplication.setmNowForegroundClass(this.getClass());
        MainApplication.setmActivity(this);

        bitmapUtils = new BitmapUtils(ctxt, pathLoginStaffCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用,图片太大时容易OOM。
        inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        width = CommonUtils.getScreenWidth(ctxt);
        height = CommonUtils.getScreenHeight(ctxt);
        oclGoToSetting = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finishWithNextActivity(SettingsActivity.class);
                Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(1);
                list.add(1);
                intent.putIntegerArrayListExtra("index", list);
                BaseActivity.this.startActivity(intent);
                popShowErrorTwoOptions.dismiss();
            }
        };

        oclGoToSettingAutoTakeup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finishWithNextActivity(SettingsActivity.class);
                Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(1);
                list.add(0);
                intent.putIntegerArrayListExtra("index", list);
                BaseActivity.this.startActivity(intent);
                popShowErrorTwoOptions.dismiss();
            }
        };

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i(tag, "-----------------------------onCreate--------------------");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        Log.i(tag, "-----------------------------onStart--------------------");
        active = true;
        tintManager = new SystemBarTintManager(BaseActivity.this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));
        tintManager.setStatusBarDarkMode(true, BaseActivity.this);

    }

    @Override
    protected void onDestroy() {
        active = false;
        Log.i(tag, "-----------------------------on end --------------------");
        super.onDestroy();

    }


    /**
     * @param msg
     */
    public void sendUIMessage(Message msg) {
        if (null != mUiHandler) {
            mUiHandler.sendMessage(msg);
        }
    }

    /**
     * @param messageId
     * @param object
     */
    public void sendUIMessage(int messageId, Object object) {
        Message msg = new Message();
        msg.what = messageId;
        msg.obj = object;
        this.sendUIMessage(msg);
    }

    /***
     * 配置左侧列表
     */
    public void launchPadConfigure() {
        tintManager = new SystemBarTintManager(BaseActivity.this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));
        tintManager.setStatusBarDarkMode(true, BaseActivity.this);

        mslidingMenu = new SimpleSideDrawer(this);
        mslidingMenu.setLeftBehindContentView(R.layout.lauchpad_left);//navigation_bar_vertical_layout);
        // mslidingMenu.setRightBehindContentView(R.layout.keyboard_setting);

        listAllFuncMap = new ArrayList<Map>();
        listAllFuncMap = launchPadUtils.getLauchpadFuncMap();

        widgetCofigureNavigationBar(0);
    }

    /**
     * 配置左边导航栏
     * showBg->0 不显示statusBar
     */
    public void widgetCofigureNavigationBar(int showBg) {

        if (showBg == 1) {
            tintManager.setStatusBarDarkMode(false, this);
            tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
        }

        CircleImageView civ = (CircleImageView) findViewById(R.id.circleImageView_navigation_now_login_user_head);
        TextView tv = (TextView) findViewById(R.id.textview_navigation_bar_now_login_user_name);
        Map map = LocalDataDeal.readFromLocalNowLoginUserInfo(getApplication());

        if ((String) map.get("head") == null || ((String) map.get("head")).equals("")) {
            civ.setImageResource(R.drawable.default_head_pic);
        } else {
            bitmapUtils.display(civ, (String) map.get("head"), bigPicDisplayConfig, null);
        }
        tv.setText((String) map.get("name"));

        View.OnClickListener oclExitToLogin = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finishWithNextActivity(LoginActivity.class);

            }
        };

        findViewById(R.id.rl_navigation_bar_exit).setOnClickListener(
                oclExitToLogin);

        listFuncMap = new ArrayList<Map>();
        listFuncMap = launchPadUtils.getNavigationFuncMap();

        NavigationBarFuncAdapter adapter = new NavigationBarFuncAdapter(listFuncMap, MainApplication.getContext());
        listviewLeftNavigation = (ListView) findViewById(R.id.listview_navigation_bar);
        listviewLeftNavigation.setAdapter(adapter);

        listviewLeftNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                final Map map = (Map) listFuncMap.get(position);

                if (map.get("name").equals(getString(R.string.launchPadAllFuncName))) {
                    mslidingMenu.hideAboveView();
                }

                if (map.get("name").equals(getString(R.string.launchPadMealDoneName))) {
                    showLoadingDialog("检测后厨打印机是否连接");
                    final String ipKitchen = (String) LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("ip");
                    if (LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt) == -1) {
                        hideLoadingDialog();
                        showDialogErrorTwoOptions("后厨打印机未设置\n请设置后厨打印机ip", oclGoToSetting, "设置");
                        return;
                    } else {
                        if(!StringUtils.isEmpty(ipKitchen))
                        {
                            new Thread() {
                                public void run() {
                                    if (CommonUtils.getPrinterTestResult(ipKitchen) == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                            }
                                        });
                                        finishWithNextActivity(PushMealActivity.class);
                                    } else {
                                        //HandlerUtils.showToast(ctxt, "后厨打印机无法连接\n请确认打印机已开机，并插入网线");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                                showDialogErrorOneOption("后厨打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n 2、Ip地址设置是否正确（当前:" + ipKitchen + ")");
                                            }
                                        });
                                        return;
                                    }
                                }
                            }.start();
                        }
                        else
                        {
                            finishWithNextActivity(PushMealActivity.class);
                        }
                    }
                } else if (map.get("name").equals(getString(R.string.launchPadSelfTakeUpName))) {
                    showLoadingDialog("检测自助取号打印机是否可用");
                    final String ipTakeup = (String) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");
                    if (ipTakeup.equals("") || ipTakeup == null) {
                        hideLoadingDialog();
                        showDialogErrorTwoOptions("自助取号打印机未设置\n请设置自助取号打印机ip", oclGoToSettingAutoTakeup, "设置");
                        return;
                    } else {
                        new Thread() {
                            public void run() {
                                if (CommonUtils.getPrinterTestResult(ipTakeup) == 0) {
                                    finishWithNextActivity(TakeUpActivity.class);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoadingDialog();
                                        }
                                    });
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoadingDialog();
                                            showDialogErrorOneOption("自助取号打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n2、ip地址设置是否正确" +
                                                    "(当前：" + ipTakeup + ")");
                                        }
                                    });
                                    //HandlerUtils.showToast(ctxt, "自助取号打印机无法连接\n请确认打印机已开机，并插入网线");
                                    return;
                                }
                            }
                        }.start();

                    }
                } else if (map.get("name").equals(getString(R.string.launchPadSettingsName))) {
                    finishWithNextActivity(SettingsActivity.class);
                } else if (map.get("name").equals(getString(R.string.launchPadStockSupplyName))) {
                    finishWithNextActivity(StockSupplyActivity.class);
                }


            }
        });


        View grid = findViewById(R.id.rl_launchpad_left_navigation_gridview);
        GridView gridview = (GridView) findViewById(R.id.gridview_dialog_show_all_func);

        grid.setBackgroundResource(R.drawable.bg_right_new);
        tintManager.setStatusBarTintResource(R.drawable.statusbar_bg_with_divider);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                tintManager.setStatusBarDarkMode(true, BaseActivity.this);
                tintManager.setStatusBarTintDrawable(null);
                tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));


                final Map map = (Map) listAllFuncMap.get(position);

                if (map.get("name").equals(getString(R.string.launchPadMealDoneName))) {
                    showLoadingDialog("检测后厨打印机是否连接");
                    final String ipKitchen = (String) LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("ip");
                    CommonUtils.LogWuwei(tag, "ipKitchen is " + ipKitchen);
                    if (LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt) == -1) {
                        hideLoadingDialog();
                        showDialogErrorTwoOptions("未设置出餐口\n" +
                                "请在设置中选择或者添加出餐口", oclGoToSetting, "设置");
                        return;
                    } else {
                        if (!StringUtils.isEmpty(ipKitchen)) {
                            new Thread() {
                                public void run() {
                                    CommonUtils.LogWuwei(tag, "在线程中，判断" + ipKitchen + "是否可用");
                                    if (CommonUtils.getPrinterTestResult(ipKitchen) == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                            }
                                        });
                                        finishWithNextActivity(PushMealActivity.class);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                                showDialogErrorOneOption("后厨打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n2、Ip地址设置是否正确" +
                                                        "(当前:" + ipKitchen + ")");
                                            }
                                        });
                                        return;
                                    }
                                }
                            }.start();
                        } else {
                            finishWithNextActivity(PushMealActivity.class);
                        }

                    }
                } else if (map.get("name").equals(getString(R.string.launchPadSelfTakeUpName))) {
                    showLoadingDialog("检测自助取号打印机是否连接");
                    final String ipTakeup = (String) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");
                    CommonUtils.LogWuwei(tag, "ipTakeup is " + ipTakeup);
                    if (ipTakeup.equals("") || ipTakeup == null) {
                        hideLoadingDialog();
                        //HandlerUtils.showToast(ctxt, "自助取号打印机未设置\n请设置自助取号打印机ip");
                        showDialogErrorTwoOptions("自助取号打印机未设置\n请设置自助取号打印机ip", oclGoToSettingAutoTakeup, "设置");
                        return;
                    } else {
                        new Thread() {
                            public void run() {
                                CommonUtils.LogWuwei(tag, "在线程中，判断" + ipTakeup + "是否可用");
                                if (CommonUtils.getPrinterTestResult(ipTakeup) == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoadingDialog();
                                        }
                                    });
                                    finishWithNextActivity(TakeUpActivity.class);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoadingDialog();
                                            showDialogErrorOneOption("自助取号打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n2、ip地址设置是否正确" +
                                                    "(当前：" + ipTakeup + ")");
                                        }
                                    });
                                    return;
                                }
                            }
                        }.start();
                    }

                } else if (map.get("name").equals(getString(R.string.launchPadSettingsName))) {
                    finishWithNextActivity(SettingsActivity.class);
                } else if (map.get("name").equals(getString(R.string.launchPadMenuEditName))) {
                    finishWithNextActivity(ClientMenuSplashActivty.class);
                } else if (map.get("name").equals(getString(R.string.launchPadDeliveryName))) {
                    finishWithNextActivity(DeliveryActivity.class);
                } else if (map.get("name").equals(getString(R.string.launchPadStockSupplyName))) {
                    finishWithNextActivity(StockSupplyActivity.class);
                } else if (map.get("name").equals(getString(R.string.launchPadStockPlanName))) {
                    finishWithNextActivity(StockPlanActivity.class);
                } else if (map.get("name").equals(getString(R.string.launchPadLablePrint))) {
                    finishWithNextActivity(LableActivity.class);
                }


            }

        });


        NavigationBarGridviewAdatper gridviewAdapter = new NavigationBarGridviewAdatper(listAllFuncMap, ctxt);
        gridview.setAdapter(gridviewAdapter);

    }


    /***
     * 显示luanchpad
     */
    public void showAllFuncDialog() {
        try {
            dialog_show_all_func = new Dialog(BaseActivity.this, R.style.leftToRight);
            dialog_show_all_func.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View grid = inflater.inflate(R.layout.dialog_show_all_func, null);

            GridView gridview = (GridView) grid.findViewById(R.id.gridview_dialog_show_all_func);

            grid.setBackgroundResource(R.drawable.bg_right_new);
            tintManager.setStatusBarTintResource(R.drawable.statusbar_bg_with_divider);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    tintManager.setStatusBarDarkMode(true, BaseActivity.this);
                    tintManager.setStatusBarTintDrawable(null);
                    tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));

                    final Map map = (Map) listAllFuncMap.get(position);

                    if (map.get("name").equals(getString(R.string.launchPadMealDoneName))) {
                        showLoadingDialog("检测后厨打印机是否连接");
                        final String ipKitchen = (String) LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("ip");
                        CommonUtils.LogWuwei(tag, "ipKitchen is " + ipKitchen);
                        if (LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt) == -1) {
                            hideLoadingDialog();
                            showDialogErrorTwoOptions("未设置出餐口\n请在设置中选择或者添加出餐口", oclGoToSetting, "设置");
                            return;
                        } else {
                            if(!StringUtils.isEmpty(ipKitchen))
                            {
                                new Thread() {
                                    public void run() {
                                        CommonUtils.LogWuwei(tag, "在线程中，判断" + ipKitchen + "是否可用");
                                        if (CommonUtils.getPrinterTestResult(ipKitchen) == 0) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hideLoadingDialog();
                                                }
                                            });
                                            finishWithNextActivity(PushMealActivity.class);
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hideLoadingDialog();
                                                    showDialogErrorOneOption("后厨打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n2、Ip地址设置是否正确" +
                                                            "(当前:" + ipKitchen + ")");
                                                }
                                            });
                                            return;
                                        }
                                    }
                                }.start();
                            }
                            else
                            {
                                finishWithNextActivity(PushMealActivity.class);
                            }
                        }
                    } else if (map.get("name").equals(getString(R.string.launchPadSelfTakeUpName))) {
                        showLoadingDialog("检测自助取号打印机是否连接");
                        final String ipTakeup = (String) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");
                        CommonUtils.LogWuwei(tag, "ipTakeup is " + ipTakeup);
                        if (ipTakeup.equals("") || ipTakeup == null) {
                            hideLoadingDialog();
                            //HandlerUtils.showToast(ctxt, "自助取号打印机未设置\n请设置自助取号打印机ip");
                            showDialogErrorTwoOptions("自助取号打印机未设置\n请设置自助取号打印机ip", oclGoToSettingAutoTakeup, "设置");
                            return;
                        } else {
                            new Thread() {
                                public void run() {
                                    CommonUtils.LogWuwei(tag, "在线程中，判断" + ipTakeup + "是否可用");
                                    if (CommonUtils.getPrinterTestResult(ipTakeup) == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                            }
                                        });
                                        finishWithNextActivity(TakeUpActivity.class);
                                    } else {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideLoadingDialog();
                                                showDialogErrorOneOption("自助取号打印机无法连接,请确认:\n1、打印机已开机，并插入网线\n2、ip地址设置是否正确" +
                                                        "(当前：" + ipTakeup + ")");
                                            }
                                        });
                                        return;
                                    }
                                }
                            }.start();
                        }

                    } else if (map.get("name").equals(getString(R.string.launchPadSettingsName))) {
                        finishWithNextActivity(SettingsActivity.class);
                    } else if (map.get("name").equals(getString(R.string.launchPadMenuEditName))) {
                        finishWithNextActivity(ClientMenuSplashActivty.class);
                    } else if (map.get("name").equals(getString(R.string.launchPadDeliveryName))) {
                        finishWithNextActivity(DeliveryActivity.class);
                    } else if (map.get("name").equals(getString(R.string.launchPadStockSupplyName))) {
                        finishWithNextActivity(StockSupplyActivity.class);
                    } else if (map.get("name").equals(getString(R.string.launchPadStockPlanName))) {
                        finishWithNextActivity(StockPlanActivity.class);
                    }else if(map.get("name").equals(getString(R.string.launchPadLablePrint)))
                    {
                        finishWithNextActivity(LableActivity.class);
                    }


                }

            });


            NavigationBarGridviewAdatper adapter = new NavigationBarGridviewAdatper(listAllFuncMap, ctxt);
            gridview.setAdapter(adapter);


            popwindowAllFunc = new PopupWindow(grid, 1850, 1540, false);
            popwindowAllFunc.setOutsideTouchable(false);
            popwindowAllFunc.setAnimationStyle(R.style.AutoDialogAnimation);
            if (mslidingMenu != null) {
                popwindowAllFunc.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 198, 0);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showAllFuncDialog error: " + e.getMessage());
        }

    }


    /***
     * 显示具有两个按钮的错误窗口
     *
     * @param msg  错误内容
     * @param ocl  另一个按钮的点击回调函数
     * @param text 另一个按钮的显示内容，比如说重试、设置等
     */
    public void showDialogErrorTwoOptions(String msg, View.OnClickListener ocl, String text) {
        try {
            View grid = inflater.inflate(R.layout.dialg_show_error, null);

            TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
            Button btnClose = (Button) grid.findViewById(R.id.btn_dialog_error_close);
            Button btnOther = (Button) grid.findViewById(R.id.btn_dialog_error_sure);

            tvContent.setText(msg);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popShowErrorTwoOptions.dismiss();
                }
            });

            btnOther.setText(text);
            btnOther.setOnClickListener(ocl);


            if (popShowErrorTwoOptions == null) {
                popShowErrorTwoOptions = new PopupWindow(grid, width, height, true);
            }
            popShowErrorTwoOptions.setContentView(grid);

            popShowErrorTwoOptions.setFocusable(true);
            popShowErrorTwoOptions.setOutsideTouchable(true);
            popShowErrorTwoOptions.setAnimationStyle(R.style.AutoDialogAnimation);
            popShowErrorTwoOptions.setBackgroundDrawable(new BitmapDrawable());
            popShowErrorTwoOptions.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showDialogErrorTwoOptions error :" + e.getMessage());
        }

    }

    public void showDialogErrorOneOption(String msg, View.OnClickListener ocl, String text) {
        try {
            View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);

            TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
            Button btnClose = (Button) grid.findViewById(R.id.btn_dialog_error_close);

            tvContent.setText(msg);
            btnClose.setOnClickListener(ocl);
            btnClose.setText(text);

            if (popShowErrorOneOption == null) {
                popShowErrorOneOption = new PopupWindow(grid, width, height, true);
            }
            popShowErrorOneOption.setContentView(grid);

            popShowErrorOneOption.setFocusable(true);
            popShowErrorOneOption.setOutsideTouchable(true);
            popShowErrorOneOption.setAnimationStyle(R.style.AutoDialogAnimation);
            //popShowErrorOneOption.setBackgroundDrawable(new BitmapDrawable());
            if (mslidingMenu != null) {
                popShowErrorOneOption.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showDialogErrorOneOption error " + e.getMessage());
        }

    }

    /***
     * 显示只有一个按钮的错误窗口
     */
    public void showDialogErrorOneOption(String errMsg) {

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popShowErrorOneOption.dismiss();
            }
        };
        showDialogErrorOneOption(errMsg, ocl, "关闭");
    }

    /****
     * 由于显示“加载中”窗口时用到的popupwindow需要依附一个控件才能展现，所以这里初始化一下这个控件
     *
     * @param view
     */
    public void showPopupWindowInit(View view) {
        viewLoadingPop = view;
    }

    /**
     * 显示加载中的窗口
     */
    public void showLoadingDialog(String text) {

        try {
            View grid = inflater.inflate(R.layout.loading_layout, null);

            tvShowLoadignText = (TextView) grid.findViewById(R.id.textview_loading_content);

            tvShowLoadignText.setText(text);

            ImageView iv = (ImageView) grid.findViewById(R.id.imageview_loading_pic);
            iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));

            if (pop_loading == null) {
                pop_loading = new PopupWindow(grid, width, height, true);
            }
            pop_loading.setContentView(grid);

            if (!pop_loading.isShowing()) {
                pop_loading.setFocusable(true);
                pop_loading.setOutsideTouchable(true);
                pop_loading.setAnimationStyle(R.style.AutoDialogAnimation);
                pop_loading.setBackgroundDrawable(new BitmapDrawable());
                try {
                    if (mslidingMenu != null) {
                        pop_loading.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);
                    } else {
                        if (viewLoadingPop != null) {
                            pop_loading.showAtLocation(viewLoadingPop, Gravity.NO_GRAVITY, 0, 0);
                        }
                    }

                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showLoadingDialog error :" + e.getMessage());
        }

    }

    /**
     * 隐藏加载中的窗口
     */
    public void hideLoadingDialog() {
        try {
            if (pop_loading != null) {
                if (pop_loading.isShowing() && pop_loading.isFocusable()) {
                    try {
                        pop_loading.dismiss();
                    } catch (Exception e) {

                    }

                }
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "hideLoadingDialog error:" + e.getMessage());
        }


    }


    /***
     * 跳转到下一个activity并结束本activity
     *
     * @param cls
     */
    public void finishWithNextActivity(Class<?> cls) {

        try {
            String nowClassName = this.getClass().getName();
            String destClassName = cls.getName();
            if (destClassName.contains("LoginActivity") || destClassName.contains("PushMealActivity")) {
                MainApplication.notificationList.clear();
                MainApplication.getmActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleSideDrawer.adapter.notifyDataSetChanged();
                    }
                });

            }
            try {
                if (nowClassName.equals(cls.getName())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mslidingMenu.closeLeftSide();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, cls);
                    startActivity(intent);
                    //  this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    finish();
                }
            } catch (Exception e) {
                CommonUtils.LogWuwei(tag, "finishWithNextActivity error:" + e.getMessage());
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "finishWithNextActivity error:" + e.getMessage());
        }

    }
}
