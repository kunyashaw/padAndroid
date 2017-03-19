package com.huofu.RestaurantOS.ui.funcSplash;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ExpandableListviewSettingAdapter;
import com.huofu.RestaurantOS.adapter.ListviewSettingTvCallAdapter;
import com.huofu.RestaurantOS.adapter.ListviewStoreItemAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.SettingTitleComponent;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.bean.storeOrder.store;
import com.huofu.RestaurantOS.fragment.settings.AutoPushMeal.FragmentAutoPushMealManager;
import com.huofu.RestaurantOS.fragment.settings.Delivery.FragmentsDeliverySettingManager;
import com.huofu.RestaurantOS.fragment.settings.MealPort.FragmentMealPortSettingManager;
import com.huofu.RestaurantOS.fragment.settings.Printer.FragmentsPrinterSettingManager;
import com.huofu.RestaurantOS.fragment.settings.Tv.FragmentsTvSettingManager;
import com.huofu.RestaurantOS.support.miBand.bueToothDevice;
import com.huofu.RestaurantOS.support.miBand.listviewMiBandListAdapter;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.widget.Keyboard;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/****
 * 设置的activity
 */
public class SettingsActivity extends BaseActivity  {

    public static boolean active = false;
    public static String tag = "setting";
    public static Context ctxt = null;
    ExpandableListView expLv = null;
    ExpandableListviewSettingAdapter expLvAdapter = null;
    public static Application app = null;
    public static BluetoothAdapter mBluetoothAdapter;
    public boolean mScanning;
    public static final long SCAN_PERIOD = 30000;//扫描时间，30s
    public static List<bueToothDevice> blueToothDeviceList = null;
    public static listviewMiBandListAdapter miBandListAdapter = null;
    public static List<peripheral> listPeripheral = new ArrayList<peripheral>();

    public Button button_setting_menu = null;//菜单按钮
    public Button button_now_setting_content = null;//现在的设置项的内容
    public Button button_now_setting_edit_cancel = null;
    public Button button_now_setting_edit_save = null;
    public Button button_now_setting_back = null;
    public static TextView tvInputOrder = null;

    public RelativeLayout rlSettingDetail = null;

    public static BitmapUtils bitmapUtils;
    public static BitmapDisplayConfig bigPicDisplayConfig;
    public static DefaultBitmapLoadCallBack<ImageView> callback;// 图片加载的回调函数

    PopupWindow dialog_loading = null;
    Dialog dialog_store_list = null;

    public boolean hasFocus = false;
    public static Handler handler = null;

    public static List<List<SettingTitleComponent>> childList = null;//左侧设置项子项
    public static List<String> groupList = null;//左侧设置项父项
    public static List<store> listStore = null;//店铺列表
    public static ListviewSettingTvCallAdapter listviewSettingTvCallWayadapter = null;

    public static ActivityManager mActivityManager = null;
    public static long nowChooseStoreId = 0;
    public static String nowChooseIdStoreName = "";
    public static Keyboard keyboard;
    public static View.OnClickListener oclKeyboardEnter;


    /********************************
     * 设置项id
     **********************************/

    public static final int SETTINGS_LIST_CHILD_PRINTER = 8;

    public static final int SETTINGS_LIST_CHILD_SMART_POS = 9;
    public static final int LANGUAGE_LIST_CHILD_SELECT = 11;

    public static final int SETTINGS_LIST_CHILD_TV = 12;
    public static final int SETTINGS_LIST_CHILD_DELIVERY = 13;
    public static final int SETTINGS_LIST_CHILD_MI_BAND = 14;
    public static final int SETTINGS_LIST_CHILD_AUTO_PUSH = 15;//自动出餐

    public static final int SETTINGS_LIST_CHILD_MANUAL_TAKEUP_PROT = 16;//自助取号台
    public static final int SETTINGS_LIST_CHILD_CASHIER_PORT = 17;//收银台
    public static final int SETTINGS_LIST_CHILD_MEAL_PUSH_PORT = 18;//出餐台

    /********************************
     * handler正常msg.what
     **********************************/
    public static final int SHOW_LOADING_TEXT = 100;
    public static final int HIDE_LOADING = 101;
    public static final int SHOW_ERROR_MESSAGE = 102;
    public static final int SHOW_ERROR_MESSAGE_TWO_OPTION = 103;
    public static final int SHOW_LOGIN_OVERDATED = 104;//显示登录过期
    public static final int SHOW_DELETE_PERI_MESSAGE = 105;// 确定要删除外接设备吗？显示窗口（错误）
    public static final int SHOW_DELETE_DELIVERY_BUILDING_MESSAGE = 106;//删除某个楼宇的警告弹窗
    public static final int SHOW_SOTRE_LIST = 107;//超级管理员具有外送设置权限的店铺列表
    public static final int CHOOSE_STORE_ID = 108;//超管从店铺列表选择了一个店铺
    public static final int SHOW_MI_BAND = 109;//小米手环配置
    public static final int SHOW_TOAST = 110;//toast tip
    public static final int MIBAND_LIST_ADAPTER_NOTIFY = 111;
    public static final int SHOW_AUTO_MEAL_DONE = 112;//自动出餐
    public static final int SHOW_KEYBOARD = 114;//打开键盘
    public static final int HIDE_KEYBOARD = 115;//关闭键盘
    public static final int NOTIFY_SETTING_EXPLV_ADPTER = 116;
    public static final int OPEN_DEFAULT_MEAL_PORT = 117;


    /************************************
     * 生命周期
     *************************/
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        active = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        CommonUtils.LogWuwei(tag, "-----------------------------设置开始--------------------");
        super.onCreate(savedInstanceState);

        ViewServer.get(this).addWindow(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.setting_layout);

        init();

        widgetConfigure();


    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        ViewServer.get(this).setFocusedWindow(this);

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        active = false;

        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }

        if (dialog_store_list != null) {
            dialog_store_list.dismiss();
        }

        if (popwindowAllFunc != null) {
            popwindowAllFunc.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        active = false;
        CommonUtils.sendMailToMe(ctxt, 2);
        ViewServer.get(this).removeWindow(this);

        childList.clear();
        groupList.clear();
        expLvAdapter = null;
        expLv.setAdapter(expLvAdapter);
        expLv.setOnChildClickListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        if (item.getItemId() == android.R.id.home) {
            mslidingMenu.toggleLeftDrawer();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
    }

    /************************************
     * 初始化及配置相关
     *************************/
    public void init() {

        MainApplication.setmActivity(this);
        app = getApplication();
        ctxt = getApplication();

        nowChooseStoreId = LocalDataDeal.readFromLocalStoreId(ctxt);
        nowChooseIdStoreName = LocalDataDeal.readFromLocalStoreName(ctxt);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        expLv = (ExpandableListView) findViewById(R.id.expandableListview_setting_navigation);
        childList = new ArrayList<List<SettingTitleComponent>>();
        groupList = new ArrayList<String>();

        listStore = new ArrayList<store>();

        leftSettingNavigation();

        expLvAdapter = new ExpandableListviewSettingAdapter(getFragmentManager(), childList, groupList, ctxt, mUiHandler);
        expLv.setAdapter(expLvAdapter);
        for (int i = 0; i < expLvAdapter.getGroupCount(); i++) {
            for (int k = 0; k < expLvAdapter.getChildrenCount(i); k++) {
                SettingTitleComponent stc = (SettingTitleComponent) expLvAdapter.getChild(i, k);
                CommonUtils.LogWuwei(tag, "child is " + stc.title);
            }
        }


        bitmapUtils = new BitmapUtils(getApplicationContext(), pathLoginStaffCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,

        handler = mUiHandler;

    }


    @Override
    protected void dealWithmessage(Message msg) {
        switch (msg.what) {
            case SHOW_LOADING_TEXT:
                String content = (String) msg.obj;
                showLoadingDialog(content);
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case SHOW_ERROR_MESSAGE:
                showDialogErrorOneOption((String) msg.obj);
                break;
            case SHOW_LOGIN_OVERDATED:
                showDialogErrorOneOption("员工登录过期，\n请重新登录");
                break;
            case SHOW_DELETE_PERI_MESSAGE:
                showDelePrepDialog((Integer) msg.obj);
                break;
            case SHOW_DELETE_DELIVERY_BUILDING_MESSAGE:
                showDeleteDeliveryBuildigDialog((Integer) msg.obj);
                break;
            case SHOW_SOTRE_LIST:
                showStoreList();
                break;
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, (String) msg.obj);
                break;
            case MIBAND_LIST_ADAPTER_NOTIFY:
                SettingsActivity.miBandListAdapter.notifyDataSetChanged();
                break;
            case CHOOSE_STORE_ID:
                int position = Integer.parseInt((String) msg.obj);

                nowChooseIdStoreName = listStore.get(position).name;
                nowChooseStoreId = listStore.get(position).store_id;
                if (dialog_store_list != null) {
                    if (dialog_store_list.isShowing()) {
                        dialog_store_list.cancel();
                    }
                }
                CommonUtils.sendMsg("获取服务器外送信息", SettingsActivity.SHOW_LOADING_TEXT, SettingsActivity.handler);
                ApisManager.GetStoreDeliverySettingInfo(LocalDataDeal.readFromLocalStoreId(ctxt), new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        FragmentsDeliverySettingManager.showDeliveryChooseSet();
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
                break;
            case SHOW_KEYBOARD:
                mslidingMenu.toggleRightDrawer();
                break;
            case HIDE_KEYBOARD:
                keyboard.clearContent();
                keyboard.setPositivOCL(null);
                mslidingMenu.closeRightSide();
                break;
            case NOTIFY_SETTING_EXPLV_ADPTER:
                expLvAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }


    public void widgetConfigure() {
        //rlSettingDetail = (RelativeLayout) findViewById(R.id.rl_setting_layout_detail);

        for (int k = 0; k < groupList.size(); k++) {
            expLv.expandGroup(k);
        }


        tabConfigure();

        launchPadConfigure();

        oclKeyboardEnter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };//初始化
        keyboard = new Keyboard(ctxt, null, mslidingMenu.mRightBehindBase, mslidingMenu, oclKeyboardEnter);
        mslidingMenu.setRightBehindContentView(keyboard);

        expLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                explvPerformClick(groupPosition, childPosition);
                return true;
            }
        });

        Intent intent = getIntent();
        ArrayList<Integer> list = new ArrayList<Integer>();
        list = intent.getIntegerArrayListExtra("index");
        if (list != null && list.size() > 0) {
            explvPerformClick(list.get(0), list.get(1));
        }

    }

    /***
     * 设置页面左侧列表的点击回调函数
     * @param groupPosition
     * @param childPosition
     */
    public void explvPerformClick(int groupPosition, int childPosition) {
        final FragmentManager fm = getFragmentManager();

        final SettingTitleComponent stc = childList.get(groupPosition).get(childPosition);
        expLvAdapter.initFlagSelected();
        stc.flagSelected = true;

        if (FragmentAutoPushMealManager.fcm != null) {
            FragmentAutoPushMealManager.fcm.deleteAllFragments();
        }

        if (FragmentsDeliverySettingManager.fcm != null) {
            FragmentsDeliverySettingManager.fcm.deleteAllFragments();
        }

        if (FragmentsPrinterSettingManager.fcm != null) {
            FragmentsPrinterSettingManager.fcm.deleteAllFragments();
        }

        if (FragmentsTvSettingManager.fcm != null) {
            FragmentsTvSettingManager.fcm.deleteAllFragments();
        }

        if (FragmentMealPortSettingManager.fcm != null) {
            FragmentMealPortSettingManager.fcm.deleteAllFragments();
        }

        switch (stc.set_for_what) {
            case SettingsActivity.SETTINGS_LIST_CHILD_PRINTER://设置打印机

                FragmentsPrinterSettingManager.init(fm, handler);
                CommonUtils.sendMsg("获取服务器外接设备信息", SettingsActivity.SHOW_LOADING_TEXT, SettingsActivity.handler);
                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        FragmentsPrinterSettingManager.listPeripheralPrinter = (List<peripheral>) object;
                        FragmentsPrinterSettingManager.showfragmentPrinterChooseSet(FragmentsPrinterSettingManager.listPeripheralPrinter);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });

                break;
            case SETTINGS_LIST_CHILD_MANUAL_TAKEUP_PROT:
                FragmentsTvSettingManager.init(fm, handler);
                FragmentsTvSettingManager.showfragmentTvChooseSet();
                break;
            case SETTINGS_LIST_CHILD_CASHIER_PORT:
                sendUIMessage(SHOW_TOAST, "收银台开发中");
                break;
            case SETTINGS_LIST_CHILD_MEAL_PUSH_PORT:
                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        listPeripheral = (List<peripheral>) object;
                        ApisManager.getAllMealPortList(new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                FragmentMealPortSettingManager.listMealPorts = (List<StoreMealPort>) object;
                                FragmentMealPortSettingManager.init(fm, listPeripheral, handler);
                                FragmentMealPortSettingManager.showFragmentPortList();
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {

                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
                    }
                });
                break;
            case SettingsActivity.SETTINGS_LIST_CHILD_SMART_POS://设置智能pos
                sendUIMessage(SHOW_TOAST, "pos开发中");
                break;
            case SettingsActivity.LANGUAGE_LIST_CHILD_SELECT://设置语言选择
                sendUIMessage(SHOW_TOAST, "多语言开发中");
                break;
            case SettingsActivity.SETTINGS_LIST_CHILD_TV:
                FragmentsTvSettingManager.init(fm, handler);
                FragmentsTvSettingManager.showfragmentTvChooseSet();
                break;
            case SettingsActivity.SETTINGS_LIST_CHILD_DELIVERY:
                FragmentsDeliverySettingManager.init(fm, handler);
                boolean flag = LocalDataDeal.judgeNowLoginUserPermission(ExpandableListviewSettingAdapter.PERMISSION_DELIVERY_SET, ctxt);
                if (!flag)//如果没有权限
                {
                    CommonUtils.sendMsg("您无权操作", SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
                    CommonUtils.LogWuwei(tag, "您无权操作");
                } else {
                    CommonUtils.sendMsg("获取服务器外送信息", SettingsActivity.SHOW_LOADING_TEXT, SettingsActivity.handler);
                    ApisManager.GetStoreDeliverySettingInfo(LocalDataDeal.readFromLocalStoreId(ctxt), new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                            FragmentsDeliverySettingManager.showDeliveryChooseSet();
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {
                            CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                            CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                        }
                    });
                }
                break;
            case SettingsActivity.SETTINGS_LIST_CHILD_MI_BAND:
                CommonUtils.sendMsg(null, SettingsActivity.SHOW_MI_BAND, SettingsActivity.handler);
                break;
            case SettingsActivity.SETTINGS_LIST_CHILD_AUTO_PUSH:
                FragmentAutoPushMealManager.init(fm, handler);
                FragmentAutoPushMealManager.showfragmentAutoPushMeal();
                break;
            default:
                CommonUtils.sendMsg("未处理", SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
                break;
        }
        CommonUtils.sendMsg("", SettingsActivity.NOTIFY_SETTING_EXPLV_ADPTER, handler);
    }


    /***
     * actionBar配置
     */
    public void tabConfigure() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_setting_custom_layout);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);

        View grid = actionBar.getCustomView();

        button_now_setting_content = (Button) findViewById(R.id.button_action_bar_setting_detail);
        button_now_setting_back = (Button) findViewById(R.id.actionbar_btn_setting_back);
        button_now_setting_edit_cancel = (Button) findViewById(R.id.actionbar_btn_setting_edit_cancel);
        button_now_setting_edit_save = (Button) findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_edit_cancel.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);


        button_setting_menu = (Button) findViewById(R.id.button_action_bar_setting_menu);

        OnClickListener oclToggleLeft = new OnClickListener() {
            @Override
            public void onClick(View v) {
                tintManager.setStatusBarDarkMode(false, SettingsActivity.this);
                tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                mslidingMenu.toggleLeftDrawer();
            }
        };
        button_setting_menu.setOnClickListener(oclToggleLeft);
        findViewById(R.id.rl_action_bar_setting_menu).setOnClickListener(oclToggleLeft);
    }

    /**
     * 设置页面左侧的数据组成
     */
    public void leftSettingNavigation() {

        /********************************设备设置*************************/
        List<SettingTitleComponent> childChildList = new ArrayList<SettingTitleComponent>();
        // groupList.add("设备设置");

        SettingTitleComponent stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_PRINTER;
        stc.icon_id = R.drawable.setting_printer;
        stc.title = "打印机设置";
        stc.title_hint = "";
        //childChildList.add(stc);

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_SMART_POS;
        stc.icon_id = R.drawable.setting_smart_pos;
        stc.title = "智能Pos设置";
        stc.title_hint = "";
        //childChildList.add(stc);

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_TV;
        stc.icon_id = R.drawable.setting_smart_pos;
        stc.title = "电视叫号设置";
        stc.title_hint = "";
        //childChildList.add(stc);


        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_MI_BAND;
        stc.icon_id = R.drawable.mi_band;
        stc.title = "小米手环设置";
        stc.title_hint = "";
        //childChildList.add(stc);

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_AUTO_PUSH;
        stc.icon_id = R.drawable.setting_store_sold_time;
        stc.title = "智能出餐设置";
        stc.title_hint = "";
        childChildList.add(stc);


        childList.add(childChildList);

        /********************************自助取号台、收银台、出餐台设置*************************/
        List<SettingTitleComponent> childChildList1 = new ArrayList<SettingTitleComponent>();
        groupList.add("吧台设置");

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_PRINTER;
        stc.icon_id = R.drawable.self_takeup;
        stc.title = "自助取号";
        stc.title_hint = "";
        childChildList1.add(stc);

        /*stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_CASHIER_PORT;
        stc.icon_id = R.drawable.setting_smart_pos;
        stc.title = "收银台";
        stc.title_hint = "";
        childChildList1.add(stc);*/

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_MEAL_PUSH_PORT;
        stc.icon_id = R.drawable.setting_printer;
        stc.title = "出餐台";
        stc.title_hint = "";
        childChildList1.add(stc);

        childList.add(childChildList1);


        /********************************外送设置*************************/
        List<SettingTitleComponent> childChildList2 = new ArrayList<SettingTitleComponent>();
        groupList.add("外送设置");

        stc = new SettingTitleComponent();
        stc.set_for_what = SETTINGS_LIST_CHILD_DELIVERY;
        stc.icon_id = R.drawable.setting_delivery;
        stc.title = "外送设置";
        stc.title_hint = "";
        childChildList2.add(stc);


        childList.add(childChildList2);

        /********************************语言设置*************************/
        List<SettingTitleComponent> childChildList3 = new ArrayList<SettingTitleComponent>();
        groupList.add("语言设置");

        stc = new SettingTitleComponent();
        stc.set_for_what = LANGUAGE_LIST_CHILD_SELECT;
        stc.icon_id = R.drawable.setting_language;
        stc.title = "语言选择";
        stc.title_hint = "";
        childChildList3.add(stc);

        childList.add(childChildList3);

    }


    /**************************************
     * 打印机设置
     *****************************/
    public void showDelePrepDialog(final int intArg) {
        CommonUtils.LogWuwei(tag, "显示删除窗口");
        if (!active) {
            return;
        }

        int lastPrinterChooseId = 0;
        for (int k = 0; k < FragmentsPrinterSettingManager.listPeripheralPrinter.size(); k++) {
            if (intArg == FragmentsPrinterSettingManager.listPeripheralPrinter.get(k).peripheral_id) {
                lastPrinterChooseId = k;
                break;
            }
        }

        final int pos = lastPrinterChooseId;
        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("删除外接设备", SHOW_LOADING_TEXT, handler);
                popShowErrorTwoOptions.dismiss();

                ApisManager.DeletePeripheralFromClound(intArg, new ApiCallback() {
                    @Override
                    public void success(Object object) {

                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(SHOW_LOADING_TEXT, "刷新列表中...");
                        ApisManager.GetPeripheralList(new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                FragmentsPrinterSettingManager.listPeripheralPrinter = (List<peripheral>) object;
                                FragmentsPrinterSettingManager.showFragmentPrinterIpList(pos);
                                sendUIMessage(HIDE_LOADING, "");
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                sendUIMessage(HIDE_LOADING, "");
                                sendUIMessage(SHOW_ERROR_MESSAGE, "" + response.error_message);
                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(SHOW_ERROR_MESSAGE, "" + response.error_message);
                    }
                });

            }
        };
        showDialogErrorTwoOptions("确定要删除本设置吗？", ocl, "删除");
    }



    /**************************************外送设置*****************************/


    /***
     * 删除某个楼宇地址
     */
    public void showDeleteDeliveryBuildigDialog(final int position) {
        CommonUtils.LogWuwei(tag, "删除楼宇地址");
        if (!active) {
            return;
        }

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("删除楼宇", SHOW_LOADING_TEXT, handler);
                popShowErrorTwoOptions.dismiss();

                ApisManager.DeleteDeliveryBuilding(FragmentsDeliverySettingManager.store_id,
                        FragmentsDeliverySettingManager.listDeliveryBuilding.get(position).building_id, new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                sendUIMessage(HIDE_LOADING, "");
                                FragmentsDeliverySettingManager.listDeliveryBuilding.remove(position);
                                FragmentsDeliverySettingManager.showDeliveryBuildingList();
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                sendUIMessage(HIDE_LOADING, "");
                                sendUIMessage(SHOW_ERROR_MESSAGE, "" + response.error_message);
                            }
                        });

            }

            ;
        };
        showDialogErrorTwoOptions("确定要删除本楼宇吗？", ocl, "删除");

    }

    /***
     * 显示店铺列表
     */
    public void showStoreList() {
        CommonUtils.LogWuwei(tag, "显示店铺列表");
        if (!active) {
            return;
        }

        CommonUtils.LogWuwei("time1", "showStoreList");
        if (dialog_store_list == null) {
            dialog_store_list = new Dialog(SettingsActivity.this, R.style.NoTitleBlackBackgroundDialog);
        }

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.listview_store, null);

        ListView lv = (ListView) grid.findViewById(R.id.listview_store_list);
        ListviewStoreItemAdapter lsAdapter = new ListviewStoreItemAdapter(listStore, ctxt);
        lv.setAdapter(lsAdapter);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = 600;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.y = 75;


        dialog_store_list.setContentView(grid);
        dialog_store_list.setCanceledOnTouchOutside(true);


        if (dialog_store_list != null) {
            dialog_store_list.hide();
            if (active) {
                dialog_store_list.show();
            }
        }

        dialog_store_list.getWindow().setAttributes(lp);


    }


    /************************************      按键处理    *************************/
    /**
     * 接受按键操作并做出响应
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
