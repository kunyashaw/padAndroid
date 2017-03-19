package com.huofu.RestaurantOS.ui.pannel.stockSupply;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.HorizontalListViewTimeBucketAdapter;
import com.huofu.RestaurantOS.adapter.ListViewStockSupplyClosedAdapter;
import com.huofu.RestaurantOS.adapter.ListViewStockSupplyClosedModifyAdapter;
import com.huofu.RestaurantOS.adapter.ListViewStockSupplyNormalAdapter;
import com.huofu.RestaurantOS.adapter.ListViewStockSupplyPeriodicAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.ProductWeekItem;
import com.huofu.RestaurantOS.bean.StoreInventoryDate;
import com.huofu.RestaurantOS.bean.StoreProduct;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.pannel.delivery.HorizontalListView;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.widget.Keyboard;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/******
 * 库存盘点
 */
public class StockSupplyActivity extends BaseActivity {

    public static String tag = "StockSupplyActivity";

    public static int nowChoocsePostion = 0;
    public static boolean flagActivte = false;
    public Button btn_action_bar_stock_fixed = null;
    public Button btn_action_bar_stock_normal = null;
    public Button btn_action_bar_stock_close = null;

    public RelativeLayout rl_action_bar_stock_fixed_underline = null;
    public RelativeLayout rl_action_bar_stock_normal_underline = null;
    public RelativeLayout rl_action_bar_stock_close_underline = null;
    public RelativeLayout rl_dialog = null;
    public FrameLayout fl_dialog = null;//framelayout
    public RelativeLayout rl_main = null;
    public RelativeLayout rl_action_bar = null;

    public HorizontalListViewTimeBucketAdapter horizontalListViewAdapter = null;
    public HorizontalListView horizontalListview = null;
    public List<ListViewStockSupplyClosedModifyAdapter> adapterList = null;//存储的是
    public LayoutInflater inflater = null;

    public ListView listview = null;
    public TextView tvTips = null;
    public ListViewStockSupplyPeriodicAdapter periodAdapter = null;
    public ListViewStockSupplyNormalAdapter normalAdapter = null;
    public ListViewStockSupplyClosedAdapter closedAdapter = null;
    public List<StoreInventoryDate> listPeriod = new ArrayList<StoreInventoryDate>();
    public List<StoreInventoryDate> listNormal = new ArrayList<StoreInventoryDate>();
    public List<StoreInventoryDate> listClosed = new ArrayList<StoreInventoryDate>();
    public List<MealBucket> listMealBucket = new ArrayList<MealBucket>();

    public Keyboard keyboard;
    public View.OnClickListener oclKeyboardEnter = null;
    public int nowInTab = 1;//1 周期库存，2 非周期库存，0 未开启库存
    public long timeBucketId = 0;
    public int dayStep = 0;
    public long todayDate = 0;

    public Button btn_action_bar_refresh = null;
    public Button btn_action_bar_menu = null;
    public Button btn_aciton_bar_date = null;
    //public TextView tvCalendarViewTips = null;

    public AlertDialog dialogShowCalendarView = null;

    public static final int SHOW_LOADING_TEXT = 1;
    public static final int HIDE_LOADING = 2;
    public static final int SHOW_TOAST = 3;
    public static final int NOTIFY_ACITON_BAR_DATE = 4;
    public static final int NOTIFY_PERIODIC_ADAPTER = 5;
    public static final int NOTIFY_NORMAL_ADAPTER = 6;
    public static final int NOTIFY_CLOSED_ADAPTER = 7;
    public static final int SHOW_KEYBOARD = 8;
    public static final int QUERY_MODIFY_PRODCUT_DATA = 9;
    public static final int SHOW_START_SELL_SET_WINDOW = 10;
    public static final int UPDATE_KEYBOARD_ENTER_OCL = 11;
    public static final int SLIDING_DRAWDER_TOGGLE_ON = 12;
    public static final int UPDATE_NOW_CHOOSE_TIMEBUCKET_ID = 13;
    public static final int UPDATE_KEYBOARD_ENTER_OCL_TAB = 14;
    public static final int NOTIFY_HORIZONTAL_LISTVIEW_ADAPTER = 15;
    public static final int SHOW_ERROR_MSG = 16;

    /*****************************activty生命周期****************/

    /****
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_stock_supply);

        init();

        widgetConfigure();

        loadTimeBucketData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        flagActivte = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flagActivte = false;
        CommonUtils.sendMailToMe(MainApplication.getContext(), 6);
    }

    /*****************************初始化和基本配置（包括handler）****************/
    /*****
     * 初始化
     */
    public void init() {
        listview = (ListView) findViewById(R.id.listview_stock_supply_main);
        LayoutInflater inflater = getLayoutInflater();//(LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup foot = (ViewGroup) inflater.inflate(R.layout.listview_stock_supply_foot, listview, false);
        listview.addFooterView(foot, null, false);
        tvTips = (TextView) foot.findViewById(R.id.textview_stock_supply_main_tips);
        rl_dialog = (RelativeLayout) findViewById(R.id.rl_frameLayout_stock_supply_dialog);
        fl_dialog = (FrameLayout) findViewById(R.id.frameLayout_stock_supply_dialog);
        rl_main = (RelativeLayout) findViewById(R.id.rl_layout_stock_supply_main);
        adapterList = new ArrayList<ListViewStockSupplyClosedModifyAdapter>();
        inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*****
     * 控件配置
     */
    public void widgetConfigure() {
        tabConfigure();
        launchPadConfigure();
        periodAdapter = new ListViewStockSupplyPeriodicAdapter(listPeriod, ctxt, mUiHandler);
        normalAdapter = new ListViewStockSupplyNormalAdapter(listNormal, ctxt, mUiHandler);
        closedAdapter = new ListViewStockSupplyClosedAdapter(listClosed, ctxt, mUiHandler);

        sendUIMessage(UPDATE_KEYBOARD_ENTER_OCL_TAB, mUiHandler);
        keyboard = new Keyboard(ctxt, null, mslidingMenu.mRightBehindBase, mslidingMenu, oclKeyboardEnter);
        mslidingMenu.setRightBehindContentView(keyboard);

    }

    /****
     * tab配置
     */
    public void tabConfigure() {

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_stock_supply);
        actionBar.setDisplayShowHomeEnabled(false);


        View grid = actionBar.getCustomView();
        rl_action_bar = (RelativeLayout) findViewById(R.id.rl_aciton_bar_stock_supply);
        btn_action_bar_refresh = (Button) findViewById(R.id.button_action_bar_stock_supply_refresh);
        btn_action_bar_menu = (Button) findViewById(R.id.button_action_bar_stock_supply_menu);
        btn_aciton_bar_date = (Button) findViewById(R.id.button_action_bar_stock_supply_date);
        btn_action_bar_stock_fixed = (Button) findViewById(R.id.button_action_bar_stock_supply_fixed);
        btn_action_bar_stock_normal = (Button) findViewById(R.id.button_action_bar_stock_supply_normal);
        btn_action_bar_stock_close = (Button) findViewById(R.id.button_action_bar_stock_supply_closed);

        rl_action_bar_stock_fixed_underline = (RelativeLayout) findViewById(R.id.button_action_bar_stock_supply_fixed_underline);
        rl_action_bar_stock_normal_underline = (RelativeLayout) findViewById(R.id.button_action_bar_stock_supply_normal_underline);
        rl_action_bar_stock_close_underline = (RelativeLayout) findViewById(R.id.button_action_bar_stock_supply_closed_underline);

        rl_action_bar_stock_fixed_underline.setVisibility(View.INVISIBLE);
        rl_action_bar_stock_normal_underline.setVisibility(View.INVISIBLE);
        rl_action_bar_stock_close_underline.setVisibility(View.INVISIBLE);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog("加载中...");
                sendUIMessage(UPDATE_KEYBOARD_ENTER_OCL_TAB, mUiHandler);
                switch (v.getId()) {
                    case R.id.button_action_bar_stock_supply_fixed:
                        CommonUtils.LogWuwei(tag, "tab 周期库存被点击了");
                        nowInTab = 1;
                        rl_action_bar_stock_fixed_underline.setVisibility(View.VISIBLE);
                        rl_action_bar_stock_normal_underline.setVisibility(View.INVISIBLE);
                        rl_action_bar_stock_close_underline.setVisibility(View.INVISIBLE);
                        tvTips.setText(getResources().getString(R.string.stockSupplyPriodicTips));
                        break;
                    case R.id.button_action_bar_stock_supply_normal:
                        CommonUtils.LogWuwei(tag, "tab 固定库存被点击了");
                        nowInTab = 2;
                        rl_action_bar_stock_fixed_underline.setVisibility(View.INVISIBLE);
                        rl_action_bar_stock_normal_underline.setVisibility(View.VISIBLE);
                        rl_action_bar_stock_close_underline.setVisibility(View.INVISIBLE);
                        tvTips.setText(getResources().getString(R.string.stockSupplyNormalTips));
                        break;
                    case R.id.button_action_bar_stock_supply_closed:
                        CommonUtils.LogWuwei(tag, "tab 未开启库存被点击了");
                        nowInTab = 0;
                        rl_action_bar_stock_fixed_underline.setVisibility(View.INVISIBLE);
                        rl_action_bar_stock_normal_underline.setVisibility(View.INVISIBLE);
                        rl_action_bar_stock_close_underline.setVisibility(View.VISIBLE);
                        tvTips.setText(getResources().getString(R.string.stockSupplyClosedTips));
                        break;
                }
                nowChoocsePostion = 0;
                loadInventoryData(dayStep, timeBucketId, nowInTab);
            }
        };


        btn_action_bar_stock_fixed.setOnClickListener(ocl);
        btn_action_bar_stock_normal.setOnClickListener(ocl);
        btn_action_bar_stock_close.setOnClickListener(ocl);

        btn_action_bar_stock_fixed.performClick();


        View.OnClickListener oclRefresh = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUIMessage(SHOW_LOADING_TEXT, "刷新中...");
                loadInventoryData(dayStep, timeBucketId, nowInTab);
            }
        };
        btn_action_bar_refresh.setOnClickListener(oclRefresh);
        findViewById(R.id.rl_action_bar_stock_supply_refresh).setOnClickListener(oclRefresh);

        View.OnClickListener oclMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tintManager.setStatusBarDarkMode(false, StockSupplyActivity.this);
                tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                mslidingMenu.toggleLeftDrawer();
            }
        };
        btn_action_bar_menu.setOnClickListener(oclMenu);
        findViewById(R.id.rl_action_bar_stock_supply_menu).setOnClickListener(oclMenu);

        btn_aciton_bar_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                int dayStepBackup = dayStep;
                View grid = inflater.inflate(R.layout.rl_calendarview_timebucketlist, null);
              //  tvCalendarViewTips = (TextView)grid.findViewById(R.id.tvCalendarViewTips);
                CalendarView cv = (CalendarView) grid.findViewById(R.id.cv);
                cv.setShowWeekNumber(false);
                cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        Time time = new Time();
                        time.setToNow();
                        String beginTime = time.year + "-" + time.month + "-" + time.monthDay;
                        String endTime = year + "-" + month + "-" + dayOfMonth;
                        dayStep = CommonUtils.getDiffDayCount(beginTime, endTime);
                        ApisManager.GetTimeBucketList(new ApiCallback() {
                            @Override
                            public void success(Object object) {

                                List<MealBucket> list = (List<MealBucket>) object;
                                listMealBucket = list;
                                sendUIMessage(NOTIFY_ACITON_BAR_DATE, CommonUtils.getStockSupplyDateName(0, "", list));
                                sendUIMessage(NOTIFY_HORIZONTAL_LISTVIEW_ADAPTER, "");
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                sendUIMessage(HIDE_LOADING, "");
                                sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                                CommonUtils.LogWuwei(tag, "GetTimeBucketList失败:" + response.error_message);
                            }
                        });

                    }
                });

                if (horizontalListview == null) {
                    horizontalListview = (HorizontalListView) grid.findViewById(R.id.horizntalScrollview_timebucket_list);
                }

                sendUIMessage(NOTIFY_HORIZONTAL_LISTVIEW_ADAPTER, "");

                if (dialogShowCalendarView == null) {
                    AlertDialog.Builder bulder = new AlertDialog.Builder(StockSupplyActivity.this).setView(grid);
                    dialogShowCalendarView = bulder.create();
                }

                dialogShowCalendarView.show();
                dialogShowCalendarView.setCanceledOnTouchOutside(false);
            }
        });

    }

    /***********************
     * 消息处理
     ************/
    @Override
    protected void dealWithmessage(Message msg) {
        try {
            switch (msg.what) {
                case SHOW_LOADING_TEXT:
                    showLoadingDialog((String) msg.obj);
                    break;
                case HIDE_LOADING:
                    hideLoadingDialog();
                    break;
                case SHOW_TOAST:
                    CommonUtils.LogWuwei(tag, "toast 内容为：" + msg.obj);
                    HandlerUtils.showToast(ctxt, (String) msg.obj);
                    break;
                case SHOW_ERROR_MSG:
                    CommonUtils.LogWuwei(tag, "错误信息为：" + msg.obj);
                    showDialogErrorOneOption((String) msg.obj);
                    break;
                case NOTIFY_ACITON_BAR_DATE:
                    btn_aciton_bar_date.setText((String) (msg.obj));
                    break;
                case NOTIFY_PERIODIC_ADAPTER:
                    notifyListView(listPeriod);
                    break;
                case NOTIFY_NORMAL_ADAPTER:
                    notifyListView(listNormal);
                    break;
                case NOTIFY_CLOSED_ADAPTER:
                    notifyListView(listClosed);
                    break;
                case SHOW_KEYBOARD:
                    mslidingMenu.toggleRightDrawer();
                    break;
                case QUERY_MODIFY_PRODCUT_DATA:
                    loadQueryResultData();
                    break;
                case SHOW_START_SELL_SET_WINDOW:
                    showModfiyInventoryWindow(msg.obj);
                    break;
                case UPDATE_KEYBOARD_ENTER_OCL:
                    Map map = (Map) msg.obj;
                    final int k = (Integer) map.get("list_order");
                    final int position = (Integer) map.get("position");
                    final List<StoreProduct> ls = (List) map.get("ls");
                    oclKeyboardEnter = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ls.get(position).amount = Double.parseDouble(keyboard.getContent());
                            adapterList.get(k).notifyDataSetChanged();
                            keyboard.clearContent();
                            mslidingMenu.closeRightSide();
                            mslidingMenu.flagRightOpen = false;
                            modifyPeriodicInventoryData();
                        }
                    };
                    keyboard.setPositivOCL(oclKeyboardEnter);
                    break;
                case SLIDING_DRAWDER_TOGGLE_ON:
                    int direction = Integer.parseInt((String) msg.obj);
                    if (direction == 0) {
                        mslidingMenu.toggleLeftDrawer();
                    } else {
                        mslidingMenu.toggleRightDrawer();
                    }
                    break;
                case UPDATE_NOW_CHOOSE_TIMEBUCKET_ID:
                    timeBucketId = (Integer) msg.obj;
                    dialogShowCalendarView.cancel();
                    loadInventoryData(dayStep, timeBucketId, nowInTab);
                    String typeName = "";
                    for (int t = 0; t < listMealBucket.size(); t++) {
                        if (timeBucketId == listMealBucket.get(t).time_bucket_id) {
                            typeName = listMealBucket.get(t).name;
                            break;
                        }
                    }
                    sendUIMessage(NOTIFY_ACITON_BAR_DATE, CommonUtils.getStockSupplyDateName(dayStep, typeName, listMealBucket));
                    break;
                case UPDATE_KEYBOARD_ENTER_OCL_TAB:
                    oclKeyboardEnter = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            showLoadingDialog("修改中...");
                            StoreInventoryDate sid = new StoreInventoryDate();
                            switch (nowInTab)//1 周期库存，2 非周期库存，0 未开启库存
                            {
                                case 1:
                                    if (nowChoocsePostion < listPeriod.size()) {
                                        sid = listPeriod.get(nowChoocsePostion);
                                        CommonUtils.LogWuwei(tag, "周期-准备修改为:" + Double.parseDouble(keyboard.getContent()));
                                        ApisManager.UpdateProduceFixedInventory(
                                                sid.product_id, timeBucketId,
                                                Double.parseDouble(keyboard.getContent()), dayStep,
                                                new ApiCallback() {
                                                    @Override
                                                    public void success(Object object) {
                                                        CommonUtils.LogWuwei(tag, "修改成功");
                                                        loadInventoryData(dayStep, timeBucketId, nowInTab);
                                                    }

                                                    @Override
                                                    public void error(BaseApi.ApiResponse response) {
                                                        CommonUtils.LogWuwei(tag, "修改失败:" + response.error_message);
                                                        sendUIMessage(HIDE_LOADING, "");
                                                        sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                                                    }
                                                });
                                    }

                                    break;
                                case 2:
                                    if (nowChoocsePostion < listNormal.size()) {
                                        CommonUtils.LogWuwei(tag, "非周期-准备修改为:" + Double.parseDouble(keyboard.getContent()));
                                        sid = listNormal.get(nowChoocsePostion);
                                        ApisManager.UpdateProduceFixedInventory(
                                                sid.product_id, timeBucketId,
                                                Double.parseDouble(keyboard.getContent()), dayStep,
                                                new ApiCallback() {
                                                    @Override
                                                    public void success(Object object) {
                                                        CommonUtils.LogWuwei(tag, "修改成功");
                                                        loadInventoryData(dayStep, timeBucketId, nowInTab);
                                                    }

                                                    @Override
                                                    public void error(BaseApi.ApiResponse response) {
                                                        CommonUtils.LogWuwei(tag, "修改失败:" + response.error_message);
                                                        sendUIMessage(HIDE_LOADING, "");
                                                        sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                                                    }
                                                });
                                    }
                                    break;
                                case 0:
                                    if (nowChoocsePostion < listClosed.size()) {
                                        sid = listClosed.get(nowChoocsePostion);
                                    }
                                    break;
                            }

                            keyboard.clearContent();
                            mslidingMenu.closeRightSide();
                            mslidingMenu.flagRightOpen = false;
                        }
                    };
                    keyboard.setPositivOCL(oclKeyboardEnter);
                    break;
                case NOTIFY_HORIZONTAL_LISTVIEW_ADAPTER:
                    if (horizontalListViewAdapter != null) {
                        horizontalListViewAdapter.notifyDataSetChanged();
                    } else {
                        horizontalListViewAdapter = new HorizontalListViewTimeBucketAdapter(listMealBucket, ctxt, mUiHandler);
                        horizontalListview.setAdapter(horizontalListViewAdapter);
                        horizontalListview.setDividerWidth(30);
                    }

                    break;
            }
        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }
    }

    /***
     * 更新列表并滚到上次的位置
     *
     * @param list
     */
    private void notifyListView(List<StoreInventoryDate> list) {
        try {
            int index = listview.getFirstVisiblePosition();
            View v = listview.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - listview.getPaddingTop());

            if (list.size() > 0) {
                if (listview.getVisibility() == View.INVISIBLE) {
                    listview.setVisibility(View.VISIBLE);
                }
                switch (nowInTab) {
                    case 0:
                        closedAdapter = new ListViewStockSupplyClosedAdapter(list, ctxt, mUiHandler);
                        listview.setAdapter(closedAdapter);
                        break;
                    case 1:
                        periodAdapter = new ListViewStockSupplyPeriodicAdapter(list, ctxt, mUiHandler);
                        listview.setAdapter(periodAdapter);
                        break;
                    case 2:
                        normalAdapter = new ListViewStockSupplyNormalAdapter(list, ctxt, mUiHandler);
                        listview.setAdapter(normalAdapter);
                        break;
                }
                findViewById(R.id.stock_supply_empty_listview).setVisibility(View.INVISIBLE);
            } else {
                listview.setVisibility(View.INVISIBLE);
                findViewById(R.id.stock_supply_empty_listview).setVisibility(View.VISIBLE);
            }
            if (index < list.size()) {
                listview.setSelectionFromTop(index, top);
            }

        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }

    }


    /***
     * 获取营业时间段，并加载对应的周期库存列表
     */
    public void loadTimeBucketData() {
        try {
            ApisManager.GetTimeBucketList(new ApiCallback() {
                @Override
                public void success(Object object) {

                    List<MealBucket> list = (List<MealBucket>) object;
                    listMealBucket = list;
                    CommonUtils.LogWuwei(tag, "查询到的营业时间段列表为：" + CommonUtils.logInfoFormatTojson(JSON.toJSONString(list).toString()));
                    sendUIMessage(NOTIFY_ACITON_BAR_DATE, CommonUtils.getStockSupplyDateName(0, "", list));
                    if (list != null) {
                        timeBucketId = CommonUtils.getTimeBuckerId(list);
                        CommonUtils.LogWuwei(tag, "当前营业时间段为：" + timeBucketId);
                    }
                    loadInventoryData(dayStep, timeBucketId, nowInTab);
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    CommonUtils.LogWuwei(tag, "GetTimeBucketList失败:" + response.error_message);
                    sendUIMessage(HIDE_LOADING, "");
                    sendUIMessage(SHOW_ERROR_MSG, response.error_message);

                }
            });
        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(StockSupplyActivity.tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }

    }

    /****
     * 从服务器获取周期库存或者非周期库存或者为开启库存
     *
     * @param step
     * @param timeBucketId
     * @param invType      inv_type  未开启 0, 计划库存 1, 固定库存 2
     */
    public synchronized void loadInventoryData(int step, long timeBucketId, final int invType) {
        try {
            ApisManager.queryInventory(new ApiCallback() {
                @Override
                public synchronized void success(final Object object) {
                    try {

                        //CommonUtils.LogWuwei(tag,"查询到的列表结果："+JSON.toJSONString(list).toString());
                        sendUIMessage(HIDE_LOADING, null);
                        List<StoreInventoryDate> list = (List<StoreInventoryDate>) object;
                        switch (invType) {
                            case 0:
                                listClosed = new ArrayList<StoreInventoryDate>();
                                listClosed.addAll(list);
                                sendUIMessage(NOTIFY_CLOSED_ADAPTER, null);
                                break;
                            case 1:
                                listPeriod = new ArrayList<StoreInventoryDate>();
                                listPeriod.addAll(list);
                                sendUIMessage(NOTIFY_PERIODIC_ADAPTER, null);
                                break;
                            case 2:
                                listNormal = new ArrayList<StoreInventoryDate>();
                                listNormal.addAll(list);
                                sendUIMessage(NOTIFY_NORMAL_ADAPTER, null);
                                break;
                        }
                        ;

                    } catch (Exception e) {
                        e.printStackTrace();

                        StackTraceElement[] listException = e.getStackTrace();
                        for (int k = 0; k < listException.length; k++) {
                            CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                                    + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
                        }
                    }

                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    CommonUtils.LogWuwei(tag,"queryInventory失败"+response.error_message);
                    sendUIMessage(HIDE_LOADING, null);
                    sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                }
            }, this.timeBucketId, invType, dayStep);

        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }

    }

    /****
     * 查询结果
     */
    public void loadQueryResultData() {

        try {
            List<StoreInventoryDate> list = new ArrayList<StoreInventoryDate>();
            switch (nowInTab) {
                case 0:
                    list.addAll(listClosed);
                    break;
                case 1:
                    list.addAll(listPeriod);
                    break;
                case 2:
                    list.addAll(listNormal);
                    break;
            }
            ApisManager.QueryInventoryByProduct(list.get(nowChoocsePostion).product_id, new ApiCallback() {
                @Override
                public void success(Object object) {
                    CommonUtils.LogWuwei(tag, "");
                    sendUIMessage(SHOW_START_SELL_SET_WINDOW, object);
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    CommonUtils.LogWuwei(tag,"QueryInventoryByProduct"+response.error_message);
                    sendUIMessage(HIDE_LOADING, null);
                    sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                }
            });
        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }

    }


    /****
     * @param amount
     * @param inv_enabled 是否开启库存
     * @param inv_type    0=未开启，1=周期库存，2=固定库存
     */
    public void loadUpdateInventoryData(double amount, boolean inv_enabled, final int inv_type) {

        try {
            //if(nowChoocsePostion < listClosed.size())
            {
                List<StoreInventoryDate> list = new ArrayList<StoreInventoryDate>();
                switch (nowInTab) {
                    case 0:
                        list.addAll(listClosed);
                        break;
                    case 1:
                        list.addAll(listPeriod);
                        break;
                    case 2:
                        list.addAll(listNormal);
                        break;
                }
                StoreInventoryDate sid = list.get(nowChoocsePostion);
                CommonUtils.LogWuwei(tag, "库存更改：数量--" + amount + "名称--" + sid.store_product.name + "是否开启--" + inv_enabled + "类型（0未开启 1周期 2固定）--" + inv_type);
                ApisManager.UpdateInventory(sid.product_id, inv_enabled, inv_type, amount, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        sendUIMessage(HIDE_LOADING, null);
                        CommonUtils.LogWuwei(tag, "loadUpdateInventoryData success");
                        if (nowInTab != 0) {
                            loadInventoryData(dayStep, timeBucketId, nowInTab);
                        }
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.LogWuwei(tag, "loadUpdateInventoryData error");
                        sendUIMessage(HIDE_LOADING, null);
                        sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                    }
                });

            }

        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }
    }


    /****
     * 修改周期性库存
     */
    public void modifyPeriodicInventoryData() {
        try {
            //if (nowChoocsePostion< listClosed.size())//修改周期库存
            {
                List<StoreInventoryDate> listData = new ArrayList<StoreInventoryDate>();
                switch (nowInTab) {
                    case 0:
                        listData.addAll(listClosed);
                        break;
                    case 1:
                        listData.addAll(listPeriod);
                        break;
                    case 2:
                        listData.addAll(listNormal);
                        break;
                }
                loadUpdateInventoryData(0, true, 1);
                long product_id = listData.get(nowChoocsePostion).product_id;
                List<ProductWeekItem> list = new ArrayList<ProductWeekItem>();
                //adapterList = new ArrayList<ListViewStockSupplyClosedModifyAdapter>();
                for (int k = 0; k < adapterList.size(); k++) {
                    ListViewStockSupplyClosedModifyAdapter adapter = adapterList.get(k);
                    for (int t = 0; t < adapter.getCount(); t++) {
                        StoreProduct sp = adapter.ls.get(t);
                        ProductWeekItem pwi = new ProductWeekItem();
                        pwi.week_day = sp.week_day;
                        pwi.time_bucket_id = sp.time_bucket_id;
                        pwi.amount = sp.amount;
                        if (sp.thisWeek) {
                            pwi.next_week = 0;
                        } else {
                            pwi.next_week = 1;
                        }

                        list.add(pwi);
                    }
                }
                if (list != null && list.size() > 0) {
                    CommonUtils.LogWuwei(tag, "修改周期库存：" + CommonUtils.logInfoFormatTojson(JSON.toJSONString(list).toString()));
                    ApisManager.UpdateWeekProductInventory(product_id, list, new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            sendUIMessage(HIDE_LOADING, "");
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {
                            CommonUtils.LogWuwei(tag,"UpdateWeekProductInventory失败"+response.error_message);
                            sendUIMessage(HIDE_LOADING, "");
                            sendUIMessage(SHOW_ERROR_MSG, response.error_message);
                        }
                    });
                }
            }
        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }
    }


    /****
     * 显示修改库存时的弹窗
     */
    public void showModfiyInventoryWindow(Object object) {
        try {

            adapterList.clear();
            hideLoadingDialog();
            List<List<StoreProduct>> listAllSP = (List<List<StoreProduct>>) object;
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View grid = inflater.inflate(R.layout.layout_modify_stock_supply_closed, null);

            final ToggleButton tbInventory = (ToggleButton) grid.findViewById(R.id.toggleButton_stock_supply_open_inventory);
            final ToggleButton tbPeriodicInventory = (ToggleButton) grid.findViewById(R.id.toggleButton_stock_supply_open_periodic_inventory);
            final TextView tvTips = (TextView) grid.findViewById(R.id.textview_modify_stock_supply_closed_tips);
            final RelativeLayout rlPeriodInventory = (RelativeLayout) grid.findViewById(R.id.rl_stock_supply_open_periodec_inventory);
            Button btnClose = (Button) grid.findViewById(R.id.button_stock_supplu_modify_closed);
            final LinearLayout llStockSupplyMain = (LinearLayout) grid.findViewById(R.id.ll_stock_supply_open_periodec_list_main);
            TextView tvProductNameNowEdit = (TextView) grid.findViewById(R.id.textview_stock_supply_modify_close_now_product);
            final RelativeLayout rlNormalInventory = (RelativeLayout) grid.findViewById(R.id.rl_layout_modify_stock_supply_closed_choose_normal_inventory);
            final Button btnModifiedFixedAmount = (Button) grid.findViewById(R.id.btn_choose_fixed_amount_layout_stock_supply_closed);


            tvProductNameNowEdit.setText(listClosed.get(nowChoocsePostion).store_product.name);
            rlNormalInventory.setVisibility(View.INVISIBLE);


            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loadInventoryData(dayStep, timeBucketId, nowInTab);

                    rl_dialog.removeAllViews();
                    fl_dialog.setVisibility(View.INVISIBLE);
                    getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
                    rl_action_bar.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    rl_action_bar.setAlpha(1f);
                }
            });

            btnModifiedFixedAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    oclKeyboardEnter = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnModifiedFixedAmount.setText(keyboard.getContent());
                            keyboard.clearContent();
                            mslidingMenu.closeRightSide();
                            mslidingMenu.flagRightOpen = false;
                            loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                        }
                    };
                    keyboard.setPositivOCL(oclKeyboardEnter);
                    mslidingMenu.toggleRightDrawer();

                    keyboard.getFoucs();
                }
            });

            tbInventory.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    if (on) {
                        CommonUtils.LogWuwei(tag, "开启库存");
                        tvTips.setVisibility(View.VISIBLE);
                        rlPeriodInventory.setVisibility(View.VISIBLE);
                        if (tbPeriodicInventory.isToggleOn()) {
                            llStockSupplyMain.setVisibility(View.VISIBLE);
                            rlNormalInventory.setVisibility(View.INVISIBLE);
                            modifyPeriodicInventoryData();

                        } else {
                            llStockSupplyMain.setVisibility(View.INVISIBLE);
                            rlNormalInventory.setVisibility(View.VISIBLE);
                            loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                        }


                    } else {
                        CommonUtils.LogWuwei(tag, "关闭库存");
                        tvTips.setVisibility(View.INVISIBLE);
                        rlPeriodInventory.setVisibility(View.INVISIBLE);
                        llStockSupplyMain.setVisibility(View.INVISIBLE);
                        rlNormalInventory.setVisibility(View.INVISIBLE);
                        loadUpdateInventoryData(0, false, 2);
                    }
                }
            });

            tbPeriodicInventory.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    if (on) {
                        CommonUtils.LogWuwei(tag, "开启周期库存");
                        tvTips.setText(getString(R.string.stockSupplyOpenPeriodicTips));
                        llStockSupplyMain.setVisibility(View.VISIBLE);
                        rlNormalInventory.setVisibility(View.INVISIBLE);
                        modifyPeriodicInventoryData();
                    } else {
                        CommonUtils.LogWuwei(tag, "关闭周期库存");
                        tvTips.setText(getString(R.string.stockSupplyOpenNormalTips));
                        llStockSupplyMain.setVisibility(View.INVISIBLE);
                        rlNormalInventory.setVisibility(View.VISIBLE);
                        loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                    }
                }
            });


            tbPeriodicInventory.toggleOn();
            tbInventory.toggleOn();

            Time time = new Time();
            time.setToNow();
            Calendar cal = Calendar.getInstance();
            cal.set(time.year, time.month, time.monthDay, 0, 0, 0);
            long todatTimeStamp = (long) (cal.getTimeInMillis() / 1000);

            for (int k = 0; k < listAllSP.size(); k++) {
                final ListViewStockSupplyClosedModifyAdapter adapter = new ListViewStockSupplyClosedModifyAdapter
                        (listAllSP.get(k), ctxt, mUiHandler, k);

                adapterList.add(adapter);

                ListView lv = new ListView(ctxt);
                lv.setAdapter(adapter);

                View gridTitle = inflater.inflate(R.layout.listview_title_stock_supply_closed_modify, null);
                ;
                Button btn = (Button) gridTitle.findViewById(R.id.btn_listview_title_stock_supply_closed_modify);
                TextView tv = (TextView) gridTitle.findViewById(R.id.textview_listview_title_stock_supply_closed_modify);

                String timeStr = "(" +
                        CommonUtils.getStrTime(todatTimeStamp + (listAllSP.get(k).get(0).store_time_bucket.start_time / 1000)) + "-" +
                        CommonUtils.getStrTime(todatTimeStamp + (listAllSP.get(k).get(0).store_time_bucket.end_time) / 1000) + ")";
                tv.setText(listAllSP.get(k).get(0).store_time_bucket.name + " " + timeStr);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.LogWuwei(tag, "正在统一修改库存...");
                        oclKeyboardEnter = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                double amount = Double.parseDouble(keyboard.getContent());
                                for (int k = 0; k < adapter.getCount(); k++) {
                                    adapter.ls.get(k).amount = amount;
                                }
                                CommonUtils.LogWuwei(tag, "统一修改库存为" + keyboard.getContent());
                                adapter.notifyDataSetChanged();
                                keyboard.clearContent();
                                mslidingMenu.closeRightSide();
                                mslidingMenu.flagRightOpen = false;
                                modifyPeriodicInventoryData();
                            }
                        };
                        keyboard.setPositivOCL(oclKeyboardEnter);
                        mslidingMenu.toggleRightDrawer();
                    }
                });

                int height = 120 * listAllSP.get(k).size();
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.MATCH_PARENT, height);
                View view = new View(ctxt);
                view.setMinimumHeight(80);
                view.setBackgroundColor(getResources().getColor(R.color.BackgroudColor));
                llStockSupplyMain.addView(view);
                llStockSupplyMain.addView(gridTitle);
                llStockSupplyMain.addView(lv, lp);
            }
            View view = new View(ctxt);
            view.setMinimumHeight(80);
            llStockSupplyMain.addView(view);

            ScrollView sv = (ScrollView) grid.findViewById(R.id.scrollview_modify_stock_supply_closed);
            sv.fullScroll(ScrollView.FOCUS_UP);


            int width = CommonUtils.getScreenWidth(ctxt);
            int height = CommonUtils.getScreenHeight(ctxt);

            rl_dialog.addView(grid);
            fl_dialog.setVisibility(View.VISIBLE);
            rl_action_bar.setBackgroundColor(Color.parseColor("#000000"));
            rl_action_bar.setAlpha(0.11f);
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
        } catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(StockSupplyActivity.tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
