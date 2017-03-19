package com.huofu.RestaurantOS.ui.pannel.pushMeal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.GridviewAnalysisFoodAdapter;
import com.huofu.RestaurantOS.adapter.GridviewLongClickChooseAdapter;
import com.huofu.RestaurantOS.adapter.GridviewSetAnalysisAdapter;
import com.huofu.RestaurantOS.adapter.ListviewPushMealPortListAdapter;
import com.huofu.RestaurantOS.adapter.PushMealFragmentPagerAdapter;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBean;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;
import com.huofu.RestaurantOS.fragment.pushMeal.PushMealFragment;
import com.huofu.RestaurantOS.fragment.pushMeal.pushMealHistoryFragment;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.manager.TicketsManager;
import com.huofu.RestaurantOS.support.PushMealViewPager;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.pannel.call.ClientSpeak;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;
import com.huofu.RestaurantOS.widget.Keyboard;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * author: Created by zzl on 15/11/19.
 */
public class PushMealActivity extends BaseActivity {

    public static String tag = "PushMealActivity";
    public String actionBarBackGroundColor = "#f0f0f0";//"#df6448";
    public Context ctxt;
    public static boolean isActive = false;


    public Button button_meal_done_menu = null;
    public Button button_push_meal = null;// 出餐按钮（显示出餐界面）
    public Button button_push_history = null;// 出餐历史按钮（显示出餐历史界面）
    public Button button_switch_meal_port = null;//切换出餐口按钮
    public Button button_call = null;

    public PopupWindow popwindowShowPrintAnalysis = null;
    public PopupWindow popwindowShowLongChoose = null;
    public PopupWindow popwindowShowPrintAgain = null;
    public PopupWindow dialog_show_print_error = null;

    public TextView tv_show_now_choose_meal_port = null;//显示当前选择的出餐口
    private com.huofu.RestaurantOS.support.PushMealViewPager mPager;
    public AlertDialog dialogShowMealPortList = null;
    public View.OnClickListener oclKeyboardEnter;
    public Keyboard keyboard;

    public PushMealFragmentPagerAdapter pushMealFragmentPagerAdapter;

    public View view_underline_meal = null;
    public View view_underline_histopry = null;

    public Handler handlerPushMeal;
    public View.OnClickListener oclRefresh = null;

    public List<StoreMealPort> listMealPorts = new ArrayList<StoreMealPort>();
    public List<MealBucket> listTimeBucket = new ArrayList<MealBucket>();
    private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
    public PushMealFragment fragmentPushMealMain;
    public pushMealHistoryFragment fragmentPushMealHistory;

    public static final int SHOW_LOADING_TEXT = 1;// 显示窗口（加载进度）
    public static final int HIDE_LOADING = 2;// 关闭窗口（加载进度）
    public static final int SHOW_ERROR_MESSAGE = 3;// 显示窗口（错误）
    public static final int SHOW_TOAST = 4;//toast

    public static final int SHOW_MEAL_PORT_LIST = 5;//显示出餐台列表
    public static final int HIDE_MEAL_PORT_LIST = 6;//隐藏出餐台列表
    public static final int UPDATE_NOW_PUSH_WAY = 7;//更新出餐口的提示


    public static final int UPDATE_EXPLV = 8;//更新左侧待出餐列表
    public static final int UPDATE_ANALYSIS_GRIDVIEW = 9;//更新右侧统计列表
    public static final int SHOW_ANALYSIS_SET_DIALOG = 10;//设置统计产品时的弹窗
    public static final int HIDE_ANALYSIS_SET_DIALOG = 11;//隐藏统计产品时的弹窗
    public static final int SHOW_CHOOSE_MP_DIALOG = 12;//显示『多选出餐』的窗口
    public static final int HIDE_CHOOSE_MP_DIALOG = 13;//隐藏『多选出餐』的窗口
    public static final int SHOW_HISTORY_REPRINT_DIALOG = 14;//显示『出餐历史的补打』的窗口
    public static final int HIDE_HISTORY_REPRINT_DIALOG = 15;//隐藏『出餐历史的补打』的窗口
    public static final int SHOW_ERROR_MESSAGE_PRINT_AGAIN = 16;//显示『打印错误，重新打印』
    public static final int HIDE_ERROR_MESSAGE_PRINT_AGAIN = 17;//关闭『打印错误，重新打印』


    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        ViewServer.get(this).addWindow(this);
        setContentView(R.layout.mealdone);
        init();
        widgetConfigure();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    /****************************************** 初始化********************************************/
    public void init() {
        launchPadConfigure();
        ctxt = getApplicationContext();
        mPager = (PushMealViewPager) findViewById(R.id.vPager);

        oclKeyboardEnter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.LogWuwei(tag, "settingKeyBoardEnterClickListner");
                String inputContentUsingRightKeyboard = keyboard.getContent();
                if (StringUtils.isEmpty(inputContentUsingRightKeyboard)) {
                    return;
                }
                ClientSpeak.ClientSpeak(Integer.parseInt(inputContentUsingRightKeyboard), ctxt);
                CommonUtils.sendMsg(inputContentUsingRightKeyboard + "正在叫号", SHOW_TOAST, handlerPushMeal);
                keyboard.clearContent();
            }
        };//初始化
        keyboard = new Keyboard(ctxt, null, mslidingMenu.mRightBehindBase, mslidingMenu, oclKeyboardEnter);
        mslidingMenu.setRightBehindContentView(keyboard);
        keyboard.HideBtnDot();

        ClientSpeak.create_thread(getApplicationContext());
        handlerPushMeal = this.mUiHandler;
    }

    public void widgetConfigure() {

        fragmentPushMealMain = new PushMealFragment(ctxt, handlerPushMeal);
        fragmentPushMealHistory = new pushMealHistoryFragment(ctxt, handlerPushMeal);
        fragmentsList.add(fragmentPushMealMain);
        fragmentsList.add(fragmentPushMealHistory);

        pushMealFragmentPagerAdapter = new PushMealFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList);
        mPager.setAdapter(pushMealFragmentPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setPagingEnabled(false);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //CommonUtils.LogWuwei(tag, "------i is " + i + " v is " + v + " i1 is " + i1 + "------");
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    view_underline_meal.setVisibility(View.VISIBLE);
                    view_underline_histopry.setVisibility(View.INVISIBLE);
                } else {
                    view_underline_meal.setVisibility(View.INVISIBLE);
                    view_underline_histopry.setVisibility(View.VISIBLE);
                    fragmentPushMealHistory.updateGridview();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabConfigure();

        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                listTimeBucket = (List<MealBucket>) object;
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING,"");
                sendUIMessage(SHOW_ERROR_MESSAGE,"获取营业时间段结束"+response.error_message);
            }
        });
    }

    /****************************************** tabs配置********************************************/

    /**
     * 工具栏的按钮配置
     */
    public void tabConfigure() {

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(actionBarBackGroundColor)));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_meal_done_custon_layout);
        actionBar.setDisplayShowHomeEnabled(false);

        View grid = actionBar.getCustomView();

        tv_show_now_choose_meal_port = (TextView) findViewById(R.id.tv_action_bar_meal_done_meal_port_name);

        if (LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt) != -1
                && !StringUtils.isEmpty(LocalDataDeal.readFromLocalMealDoneChooseMealPortName(ctxt))) {
            final long lastChooseId = LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt);
            ApisManager.getAllMealPortList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<StoreMealPort> listSMP = (List<StoreMealPort>) object;
                    for (final StoreMealPort smp : listSMP) {
                        if (lastChooseId == smp.port_id) {
                            //出餐模式:手工出餐(0) 还是自动出餐(1)
                            if (smp.checkout_type == 1) {
                                LocalDataDeal.writePushMealWhetherAuto(true,ctxt);
                                sendUIMessage(SHOW_TOAST, "上次选中的出餐口，当前处于自动出餐状态");
                                sendUIMessage(UPDATE_NOW_PUSH_WAY, "自动出餐");
                            } else {
                                LocalDataDeal.writePushMealWhetherAuto(false,ctxt);
                                sendUIMessage(UPDATE_NOW_PUSH_WAY, LocalDataDeal.readFromLocalMealDoneChooseMealPortName(ctxt));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragmentPushMealMain.updatePushWayTips(smp.checkout_type);
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        }


        button_switch_meal_port = (Button) findViewById(R.id.btn_action_bar_meal_done_switch_meal_port);
        button_switch_meal_port.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUIMessage(SHOW_LOADING_TEXT, "加载出餐口列表");
                ApisManager.getAllMealPortList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        listMealPorts = (List<StoreMealPort>) object;
                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(SHOW_MEAL_PORT_LIST, "");
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(SHOW_ERROR_MESSAGE, "" + response.error_message);
                    }
                });
            }
        });

        View.OnClickListener ocl = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Message msg = new Message();
                    switch (v.getId()) {
                        case R.id.button_action_bar_meal_done_checkout:
                            CommonUtils.LogWuwei(tag, "切换tab到出餐");
                            pushMealFragmentPagerAdapter.updateNowChoosePosition(0);
                            mPager.setCurrentItem(0);
                            view_underline_meal.setVisibility(View.VISIBLE);
                            view_underline_histopry.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.button_action_bar_meal_done_checkout_history:
                            CommonUtils.LogWuwei(tag, "切换tab到出餐历史");
                            pushMealFragmentPagerAdapter.updateNowChoosePosition(1);
                            fragmentPushMealHistory.updateGridview();
                            mPager.setCurrentItem(1);
                            pushMealFragmentPagerAdapter.notifyDataSetChanged();
                            view_underline_meal.setVisibility(View.INVISIBLE);
                            view_underline_histopry.setVisibility(View.VISIBLE);
                            break;
                    }
                } catch (Exception e) {

                }

            }
        };

        View.OnClickListener oclCall = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tintManager != null) {
                    tintManager.setStatusBarDarkMode(false, PushMealActivity.this);
                    tintManager.setStatusBarTintResource(R.drawable.statusbar_bg_with_divider);
                }

                if (mslidingMenu != null) {
                    mslidingMenu.toggleRightDrawer();
                }
            }
        };

        button_call = (Button) findViewById(R.id.button_action_bar_meal_done_call);
        button_call.setOnClickListener(oclCall);
        findViewById(R.id.rl_action_bar_meal_done_call).setOnClickListener(oclCall);

        oclRefresh = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.getTemplateList(ctxt, mUiHandler);//获取小票模板
                taskRefresh();
            }
        };

        findViewById(R.id.button_action_bar_meal_done_refresh).setOnClickListener(oclRefresh);

        findViewById(R.id.rl_action_bar_meal_done_refresh).setOnClickListener(oclRefresh);


        button_meal_done_menu = (Button) findViewById(R.id.button_action_bar_meal_done_menu);

        button_meal_done_menu.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         // TODO Auto-generated method stub
                                                         tintManager.setStatusBarDarkMode(false, PushMealActivity.this);
                                                         tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                                                         mslidingMenu.toggleLeftDrawer();

                                                     }
                                                 }

        );

        findViewById(R.id.rl_action_bar_meal_done_menu).
                setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           // TODO Auto-generated method stub
                                           tintManager.setStatusBarDarkMode(false, PushMealActivity.this);
                                           tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                                           mslidingMenu.toggleLeftDrawer();
                                       }
                                   }

                );

        button_push_meal = (Button) grid.findViewById(R.id.button_action_bar_meal_done_checkout);
        button_push_history = (Button) grid.findViewById(R.id.button_action_bar_meal_done_checkout_history);
        button_push_meal.setOnClickListener(ocl);
        button_push_history.setOnClickListener(ocl);

        view_underline_histopry = (View) grid.findViewById(R.id.view_action_bar_below_push_history);
        view_underline_meal = (View) grid.findViewById(R.id.view_action_bar_below_push);

        view_underline_meal.setVisibility(View.VISIBLE);
        view_underline_histopry.setVisibility(View.INVISIBLE);
    }


    /****************************************** 消息处理********************************************/
    /**
     * 主线程的消息处理
     * @param msg
     */
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
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, (String) msg.obj);
                break;
            case SHOW_ERROR_MESSAGE:
                showDialogErrorOneOption((String) msg.obj);
                break;
            case SHOW_MEAL_PORT_LIST:
                showDialogMealPortList();
                break;
            case HIDE_MEAL_PORT_LIST:
                hideDialogMealPortList();
                break;
            case UPDATE_NOW_PUSH_WAY:
                tv_show_now_choose_meal_port.setText((String) msg.obj);
                break;
            case UPDATE_EXPLV:
                Map map = (Map) msg.obj;
                int flag = (Integer) map.get("flag");
                Long productId = 0L;
                String name = "";
                boolean flagScrollToTop = false;
                if (map.containsKey("productId")) {
                    productId = (Long) map.get("productId");
                }
                if (map.containsKey("name")) {
                    name = (String) map.get("name");
                }
                if(map.containsKey("scrollToTop"))
                {
                    flagScrollToTop = (Boolean)map.get("scrollToTop");
                }
                fragmentPushMealMain.updateWaitingPushExplv(flag, productId, name,flagScrollToTop);
                break;
            case UPDATE_ANALYSIS_GRIDVIEW:
                fragmentPushMealMain.updateAnalysisGridview();
                break;
            case SHOW_ANALYSIS_SET_DIALOG:
                showAnalysisSetDialog();
                break;
            case HIDE_ANALYSIS_SET_DIALOG:
                if (popwindowShowPrintAnalysis != null) {
                    popwindowShowPrintAnalysis.dismiss();
                }
                if (fragmentPushMealMain != null) {
                    fragmentPushMealMain.refreshAnalysisFoods();
                }
                break;
            case SHOW_CHOOSE_MP_DIALOG:
                showLongClickChooseDialog((Map) msg.obj);
                break;
            case HIDE_CHOOSE_MP_DIALOG:
                if (popwindowShowLongChoose != null) {
                    popwindowShowLongChoose.dismiss();
                }
                break;
            case SHOW_HISTORY_REPRINT_DIALOG:
                showHistoryReprintDialog((Map) msg.obj);
                break;
            case HIDE_HISTORY_REPRINT_DIALOG:
                if (popwindowShowPrintAgain != null) {
                    popwindowShowPrintAgain.dismiss();
                }
                break;
            case SHOW_ERROR_MESSAGE_PRINT_AGAIN:
                Map mapError = (HashMap) msg.obj;
                OrderDetailInfo odi = (OrderDetailInfo)mapError.get("Odi");
                int purpose = (Integer)mapError.get("purpose");
                showDialogPrintFailedError(odi,purpose);
                break;
            case HIDE_ERROR_MESSAGE_PRINT_AGAIN:
                if(dialog_show_print_error != null)
                {
                    dialog_show_print_error.dismiss();
                }
                break;
        }
    }


    /****************************************** 刷新任务 ********************************************/
    /**
     * 点击刷新后的处理
     */
    public void taskRefresh() {
        sendUIMessage(SHOW_LOADING_TEXT, "刷新中...");
        if(mPager.getCurrentItem() == 0)
        {
            TicketsManager.getInstance().cleanLatestNumber();
            TicketsManager.getInstance().cleanTicketBeanGroups();
            fragmentPushMealMain.dataInit();
        }
        else
        {
            fragmentPushMealHistory.updateGridview();
        }
    }


    /****************************************** 显示、关闭选择出餐口列表 ********************************************/
    /**
     * 显示出餐口列表
     */
    public void showDialogMealPortList() {
        View grid = inflater.inflate(R.layout.setting_meal_port_stamp_list, null);
        ListView lv = (ListView) grid.findViewById(R.id.listview_setting_mp_stamp_list);
        ListviewPushMealPortListAdapter adapter = new ListviewPushMealPortListAdapter(ctxt, handlerPushMeal, listMealPorts);
        lv.setAdapter(adapter);
        adapter.updateAdapter(adapter);

        dialogShowMealPortList = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT).create();
        dialogShowMealPortList.setView(grid);
        dialogShowMealPortList.show();
    }

    /**
     * 隐藏出餐口列表
     */
    public void hideDialogMealPortList() {
        CommonUtils.LogWuwei(tag, "刷新中...");
        taskRefresh();
        dialogShowMealPortList.hide();
        fragmentPushMealMain.updatePushWayTips(0);
    }


    /****************************************** 显示、关闭设置统计产品列表 ********************************************/
    /**
     * 显示设置统计产品的窗口
     */
    public void showAnalysisSetDialog() {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_analysis_set, null);

        final List<StoreProduct> list = new ArrayList<StoreProduct>();
        list.addAll(fragmentPushMealMain.getAnalysisStoreProduct());
        final List<StoreProduct> listSave = new ArrayList<StoreProduct>();

        GridView gridviewAnalysisChooseSet = (GridView) grid.findViewById(R.id.gridview_dialog_analysis_set);
        final GridviewSetAnalysisAdapter adapter = new GridviewSetAnalysisAdapter(ctxt,
                list, this.getResources().getDisplayMetrics().density);
        gridviewAnalysisChooseSet.setAdapter(adapter);

        Button button_cancel = (Button) grid.findViewById(R.id.button_dialog_analysis_set_cancel);
        final Button button_close = (Button) grid.findViewById(R.id.button_dialog_analysis_set_close);
        final Button button_sure = (Button) grid.findViewById(R.id.button_dialog_analysis_set_sure);

        final SearchView sv_analysis_set = (SearchView) grid
                .findViewById(R.id.sv_dialog_analysic_set);

        int id = sv_analysis_set.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);// textview
        TextView textView = (TextView) sv_analysis_set.findViewById(id);
        textView.setTextColor(Color.parseColor("#363636"));
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setHintTextColor(Color.parseColor("#b1b1b1"));

        int resId = getResources().getIdentifier("search_button", "id",
                "android");// icon
        ImageView searchIcon = (ImageView) sv_analysis_set.findViewById(resId);
        RelativeLayout rl = (RelativeLayout) grid
                .findViewById(R.id.rl_dialog_analysis_set_sv_parent);
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) searchIcon
                .getLayoutParams();
        lp.gravity = Gravity.CENTER;

        int linlayId = getResources().getIdentifier("android:id/search_plate",
                null, null);// underline_blue_plate
        ViewGroup v = (ViewGroup) sv_analysis_set.findViewById(linlayId);
        v.setBackgroundColor(Color.TRANSPARENT);

        sv_analysis_set.setQueryHint("名称");
        sv_analysis_set.setIconified(true);
        sv_analysis_set.onActionViewCollapsed();
        sv_analysis_set.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                for (int i = 0; i < listSave.size(); i++) {
                    if (listSave.get(i).name.contains(query) || query.contains(listSave.get(i).name)) {
                        boolean flagNew = true;
                        for (int k = 0; k < list.size(); k++) {
                            if (list.get(k).name.equals(query)) {
                                flagNew = false;
                            }
                        }
                        if (flagNew) {
                            list.add(listSave.get(i));
                        }

                    }
                }
                if (list.size() < 1) {
                    HandlerUtils.showToast(ctxt, "没有搜索菜品名称为:" + query + "的订单");
                } else {
                    InputMethodUtils.HideKeyboard(sv_analysis_set);
                    adapter.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                if (newText.equals("")) {
                    list.clear();

					/*
                     * for(int i=0;i<listSave.size();i++) {
					 * list.add(listSave.get(i)); }
					 * adapter.notifyDataSetChanged();
					 * InputMethodUtils.KeyBoard(sv_analysis_set, "close");
					 */
                }
                return false;
            }
        });

        sv_analysis_set.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    listSave.add(list.get(i));
                }
                list.clear();
            }
        });

        sv_analysis_set.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                // TODO Auto-generated method stub
                InputMethodUtils.KeyBoard(sv_analysis_set, "close");

                for (int i = 0; i < listSave.size(); i++) {
                    list.add(listSave.get(i));
                }

                adapter.notifyDataSetChanged();

                listSave.clear();
                return false;
            }
        });

        rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int resId = getResources().getIdentifier("search_button", "id",
                        "android");
                ImageView searchIcon = (ImageView) sv_analysis_set
                        .findViewById(resId);
                searchIcon.performClick();
            }
        });

        View.OnClickListener ocl_negative = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialog_analysis_set.cancel();
                InputMethodUtils.TimerHideKeyboard((View) button_close);
                popwindowShowPrintAnalysis.dismiss();
            }
        };

        View.OnClickListener ocl_positive = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendUIMessage(SHOW_LOADING_TEXT, "设置中...");
                List<com.huofu.RestaurantOS.bean.storeOrder.StoreProduct> list = adapter.getAnalysisListAfterSet();
                JSONArray array = new JSONArray();
                for (StoreProduct sp : list) {
                    JSONObject obj = new JSONObject();
                    obj.put("product_id", sp.product_id);
                    obj.put("meal_stat", sp.meal_stat);
                    array.add(obj);
                }
                ApisManager.setMealProductList(array, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(HIDE_ANALYSIS_SET_DIALOG, "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentPushMealMain.dataInit();
                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(HIDE_LOADING, "");
                        sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
                        CommonUtils.LogWuwei(tag, "设置统计产品" + response.error_message);
                    }
                });
            }
        };


        button_cancel.setOnClickListener(ocl_negative);
        button_close.setOnClickListener(ocl_negative);
        button_sure.setOnClickListener(ocl_positive);

        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);
        if (popwindowShowPrintAnalysis == null) {
            popwindowShowPrintAnalysis = new PopupWindow(grid, width, height, true);
        } else {
            popwindowShowPrintAnalysis.setContentView(grid);
        }

        popwindowShowPrintAnalysis.setFocusable(true);
        popwindowShowPrintAnalysis.setOutsideTouchable(true);
        popwindowShowPrintAnalysis.setAnimationStyle(R.style.AutoDialogAnimation);
        popwindowShowPrintAnalysis.setBackgroundDrawable(new BitmapDrawable());
        popwindowShowPrintAnalysis.showAtLocation(findViewById(R.id.button_action_bar_meal_done_checkout), Gravity.NO_GRAVITY, 1114, 602);
    }


    /****************************************** 显示、关闭长按多选出餐********************************************/
    /**
     * 显示长按时多选的窗口
     */
    public void showLongClickChooseDialog(Map map) {
        if (listTimeBucket.size() == 0) {
            ApisManager.GetTimeBucketList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    listTimeBucket = (List<MealBucket>) object;
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    sendUIMessage(HIDE_LOADING,"");
                    sendUIMessage(SHOW_ERROR_MESSAGE,"获取营业时间段结束"+response.error_message);
                }
            });
        }
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_long_click_takeup, null);

        TextView tv = (TextView) grid.findViewById(R.id.tv_dialog_long_click_takeup_title);// 标题
        Button btnClose = (Button) grid.findViewById(R.id.button_dialog_long_click_takeup_close);// 关闭按钮
        GridView gridview = (GridView) grid.findViewById(R.id.gridview_dialog_long_click_takeup);// 收费项目列表
        final Button btnPush = (Button) grid.findViewById(R.id.button_dialog_long_click_takeup_push);// 出餐
        final Button btnPushAll = (Button) grid.findViewById(R.id.button_dialog_long_click_takeu_push_all);// 出餐并叫号

        final String orderId = (String) map.get("orderId");
        final int packaged = (Integer) map.get("packaged");
        final int takeSerialNum = (Integer) map.get("takeSerialNum");
        final int groupPosition = (Integer) map.get("groupPosition");
        if (packaged == 1) {
            tv.setText(takeSerialNum + "\t-\t打包部分");
        } else {
            tv.setText(takeSerialNum + "\t-\t堂食部分");
        }

        List<ChargItem> listCI = new ArrayList<ChargItem>();
        List<TicketBeanGroup> listTBG = TicketsManager.getInstance().getTicketBeanGroups();
        for (TicketBeanGroup tbg : listTBG) {
            if (tbg.order_id.equals(orderId) && tbg.packaged == packaged) {
                for (TicketBean tb : tbg.tickets) {
                    for (ChargItem ci : tb.charge_items) {
                        for (int k = 0; k < ci.charge_item_amount; k++) {
                            ChargItem ciTmp = new ChargItem();
                            try {
                                ciTmp = (ChargItem) ci.clone();
                                ciTmp.charge_item_amount = 1;
                                listCI.add(ciTmp);
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
        final GridviewLongClickChooseAdapter adapter = new GridviewLongClickChooseAdapter(ctxt,
                listCI, this.getResources().getDisplayMetrics().density);
        gridview.setAdapter(adapter);


        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popwindowShowLongChoose.dismiss();
            }
        });

        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<ChargItem> listCIResult = adapter.getChargeItemList();
                if(listCIResult.size() == 0)
                {
                    sendUIMessage(SHOW_ERROR_MESSAGE,"请选中要出餐的收费项目");
                    return;
                }
                org.json.JSONArray array = new org.json.JSONArray();
                String name = "";
                for (ChargItem ci : listCIResult) {
                    org.json.JSONObject obj = new org.json.JSONObject();
                    try {
                        obj.put("charge_item_id", ci.charge_item_id);
                        obj.put("amount", ci.charge_item_amount);
                        obj.put("orderId", ci.orderId);
                        obj.put("packaged", ci.packaged);
                        array.put(obj);
                        name += ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.charge_item_amount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (packaged == 0) {
                    sendUIMessage(SHOW_LOADING_TEXT, "堂食部分，部分出餐中");
                } else {
                    sendUIMessage(SHOW_LOADING_TEXT, "打包部分，部分出餐中");
                }
                ApisManager.mealCheckout(name,handlerPushMeal, orderId, CommonUtils.getFormatDate(0),
                        CommonUtils.getTimeBuckerId(listTimeBucket), packaged, array, 0,
                        LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt), new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                sendUIMessage(HIDE_LOADING, "");
                                TicketsManager.getInstance().removeTicket(listCIResult, orderId, packaged);
                                Map map = new HashMap<String, Integer>();
                                if (groupPosition == 0) {
                                    map.put("flag", 0);
                                } else if (groupPosition == 1) {
                                    map.put("flag", 1);
                                    map.put("productId", GridviewAnalysisFoodAdapter.lastChooseProductId);
                                    map.put("name", GridviewAnalysisFoodAdapter.lastName);
                                }
                                sendUIMessage(UPDATE_EXPLV, map);
                                sendUIMessage(HIDE_CHOOSE_MP_DIALOG, "");
                                TicketsManager.getInstance().updateAnalysisNumber();
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handlerPushMeal);
                                CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handlerPushMeal);
                                CommonUtils.LogWuwei(tag,"长按部分出餐"+response.error_message);
                            }
                        });
            }
        });


        btnPushAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TicketBeanGroup> listTBG = TicketsManager.getInstance().getTicketBeanGroups();
                org.json.JSONArray array = new org.json.JSONArray();
                String name = "";
                final List<TicketBean> listTB = new ArrayList<TicketBean>();

                for (TicketBeanGroup tbg : listTBG) {
                    if (tbg.order_id.equals(orderId) && tbg.packaged == packaged) {
                        for (TicketBean tb : tbg.tickets) {
                            listTB.add(tb);
                            for (ChargItem ci : tb.charge_items) {
                                org.json.JSONObject obj = new org.json.JSONObject();
                                try {
                                    obj.put("charge_item_id", ci.charge_item_id);
                                    obj.put("amount", ci.charge_item_amount);
                                    obj.put("orderId", ci.orderId);
                                    obj.put("packaged", ci.packaged);
                                    array.put(obj);
                                    name += ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.charge_item_amount);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                if (packaged == 0) {
                    sendUIMessage(SHOW_LOADING_TEXT, "堂食部分，全部出餐中");
                } else {
                    sendUIMessage(SHOW_LOADING_TEXT, "打包部分，全部出餐中");
                }
                ApisManager.mealCheckout(name, handlerPushMeal,orderId, CommonUtils.getFormatDate(0),
                        CommonUtils.getTimeBuckerId(listTimeBucket), packaged, array, 0,
                        LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt), new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                sendUIMessage(HIDE_LOADING, "");
                                sendUIMessage(SHOW_TOAST, "pushMeal success");
                                TicketsManager.getInstance().removeTicket(listTB);

                                Map map = new HashMap<String, Integer>();
                                if (groupPosition == 0) {
                                    map.put("flag", 0);
                                } else if (groupPosition == 1) {
                                    map.put("flag", 1);
                                    map.put("productId", GridviewAnalysisFoodAdapter.lastChooseProductId);
                                    map.put("name", GridviewAnalysisFoodAdapter.lastName);
                                }
                                sendUIMessage(UPDATE_EXPLV, map);
                                sendUIMessage(HIDE_CHOOSE_MP_DIALOG, "");
                                TicketsManager.getInstance().updateAnalysisNumber();
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handlerPushMeal);
                                CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handlerPushMeal);
                                CommonUtils.LogWuwei(tag,"长按全部出餐"+response.error_message);
                            }
                        });
            }
        });

        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);

        if (popwindowShowLongChoose == null) {
            popwindowShowLongChoose = new PopupWindow(grid, width, height, true);
        } else {
            popwindowShowLongChoose.setContentView(grid);
        }

        popwindowShowLongChoose.setFocusable(true);
        popwindowShowLongChoose.setOutsideTouchable(true);
        popwindowShowLongChoose.setAnimationStyle(R.style.AutoDialogAnimation);
        popwindowShowLongChoose.setBackgroundDrawable(new BitmapDrawable());
        popwindowShowLongChoose.showAtLocation(findViewById(R.id.button_action_bar_meal_done_checkout), Gravity.NO_GRAVITY, 0, 0);
    }

    /****************************************** 在出餐历史中显示、关闭补打********************************************/
    /**
     * 在出餐历史中，选中item的弹窗，可以进行清单打印和再次打印该item
     */
    public void showHistoryReprintDialog(Map map) {

        final int takeSerialNum = (Integer) map.get("takeSerialNum");
        final int takeSerialSeq = (Integer) map.get("takeSerialSeq");
        final String content = (String) map.get("content");
        final String orderId = (String) map.get("orderId");
        final int packaged = (Integer) map.get("packaged");

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_show_history_choosen, null);

        TextView tv_serinal_num = (TextView) grid.findViewById(R.id.textview_dialog_show_history_serinal);
        TextView tv_content = (TextView) grid.findViewById(R.id.textview_dialog_show_history_content);
        RelativeLayout rl_close = (RelativeLayout) grid.findViewById(R.id.rl_dialog_show_history_choosen_close);
        Button btn_close = (Button) grid.findViewById(R.id.button_dialog_show_history_choosen_close);
        Button btn_print_list = (Button) grid.findViewById(R.id.button_dialog_show_choose_close);
        Button btn_reprint = (Button) grid.findViewById(R.id.button_dialog_long_choose_push);
        Button btn_reprint_only = (Button) grid.findViewById(R.id.button_dialog_long_choose_push_only);

        if (takeSerialSeq == 0) {
            tv_serinal_num.setText(takeSerialNum);
        } else {
            tv_serinal_num.setText(takeSerialNum + "-" + takeSerialSeq);
        }


        tv_content.setText(content);
        btn_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popwindowShowPrintAgain.dismiss();
            }
        });
        rl_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popwindowShowPrintAgain.dismiss();
            }
        });

        if (packaged == 0) {
            btn_print_list.setVisibility(View.INVISIBLE);
            btn_reprint.setVisibility(View.INVISIBLE);

            btn_reprint_only.setVisibility(View.VISIBLE);
        }
        btn_print_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUIMessage(HIDE_HISTORY_REPRINT_DIALOG,"");
                ApisManager.getOrderDetailInfoByOrderId(orderId, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        OrderDetailInfo odi = new OrderDetailInfo();
                        JSONObject objStoreOrder = (JSONObject) object;
                        String str = CommonUtils.converBooleanToInt(objStoreOrder.getJSONObject("store_order").toJSONString());
                        odi.store_order = JSON.parseObject(str, StoreOrder.class);
                        odi.take_serial_number = takeSerialNum;
                        odi.take_serial_seq = takeSerialSeq;
                        odi.flagPrintHistory = true;
                        odi.flagPrintList = true;

                        odi.list_charge_items_all = new ArrayList<ChargItem>();
                        List<ChargItem> listCI = new ArrayList<ChargItem>();
                        for(ChargItem ci:odi.store_order.order_items)
                        {
                            if(ci.packed_amount > 0)
                            {
                                listCI.add(ci);
                            }
                        }
                        odi.list_charge_items_all.addAll(listCI);
                        odi.order_id = odi.store_order.order_id;
                        odi.take_mode = odi.store_order.take_mode;
                        odi.port_letter = odi.store_order.store_meal_checkout.get(0).port_letter;
                        odi.timeBucketName = odi.store_order.store_time_bucket.name;
                        odi.packaged = packaged;

                        for (ChargItem ci : odi.store_order.order_items) {
                            if(odi.take_mode == 3)
                            {
                                if(ci.packed_amount > 0)
                                {
                                    odi.orderContentList += ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.packed_amount) + "\n";
                                }
                            }
                            else
                            {
                                odi.orderContentList += ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.packed_amount) + "\n";
                            }

                        }

                        try {
                            int result = 0;
                            if(odi.take_mode==4 )
                            {
                                result = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                        TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 4),
                                        (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip")) ;
                            }
                            else if(odi.take_mode == 2 || odi.take_mode == 3)
                            {
                                result = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                        TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 3),
                                        (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip"));
                            }

                            if(result == 0)
                            {
                                sendUIMessage(SHOW_TOAST,"补打成功");
                            }
                            else
                            {
                                sendUIMessage(SHOW_TOAST,"补打失败");
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            sendUIMessage(SHOW_TOAST, "补打失败");
                        }
                    }


                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
                        sendUIMessage(SHOW_TOAST,"补打失败");
                        CommonUtils.LogWuwei(tag, "出餐历史中补打清单失败" + response.error_message);
                    }
                });

            }
        });

        View.OnClickListener oclReprint = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUIMessage(HIDE_HISTORY_REPRINT_DIALOG,"");
                ApisManager.getOrderDetailInfoByOrderId(orderId, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        OrderDetailInfo odi = new OrderDetailInfo();
                        JSONObject objStoreOrder = (JSONObject) object;
                        String str = CommonUtils.converBooleanToInt(objStoreOrder.getJSONObject("store_order").toJSONString());
                        odi.store_order = JSON.parseObject(str, StoreOrder.class);
                        odi.orderContent = content;
                        odi.take_serial_number = takeSerialNum;
                        odi.store_order.take_serial_number = takeSerialNum;
                        odi.store_order.take_serial_seq = takeSerialSeq;
                        odi.take_serial_seq = takeSerialSeq;
                        odi.flagPrintHistory = true;
                        odi.order_id = odi.store_order.order_id;
                        odi.take_mode = odi.store_order.take_mode;
                        odi.port_letter = odi.store_order.store_meal_checkout.get(0).port_letter;
                        odi.timeBucketName = odi.store_order.store_time_bucket.name;
                        odi.packaged = packaged;

                        try {
                            int result = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                    TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 2),
                                    (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip")
                            );

                            if(result == 0)
                            {
                                sendUIMessage(SHOW_TOAST,"补打成功");
                            }
                            else
                            {
                                sendUIMessage(SHOW_TOAST,"补打失败");
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            sendUIMessage(SHOW_TOAST, "补打失败");
                        }
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
                        sendUIMessage(SHOW_TOAST,"补打失败");
                        CommonUtils.LogWuwei(tag, "出餐历史中补打失败" + response.error_message);
                    }
                });
            }
        };

        btn_reprint.setOnClickListener(oclReprint);
        btn_reprint_only.setOnClickListener(oclReprint);


        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);
        if (popwindowShowPrintAgain == null) {
            popwindowShowPrintAgain = new PopupWindow(grid, width, height, true);
        } else {
            popwindowShowPrintAgain.setContentView(grid);
        }
        popwindowShowPrintAgain.setFocusable(true);
        popwindowShowPrintAgain.setOutsideTouchable(true);
        popwindowShowPrintAgain.setAnimationStyle(R.style.AutoDialogAnimation);
        popwindowShowPrintAgain.setBackgroundDrawable(new BitmapDrawable());
        popwindowShowPrintAgain.showAtLocation(findViewById(R.id.button_action_bar_meal_done_checkout), Gravity.NO_GRAVITY, 1114, 602);

    }

    /***
     * 打印失败的弹窗
     */
    public void showDialogPrintFailedError(final OrderDetailInfo odi, final int purpose)
    {
        try {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View grid = inflater.inflate(R.layout.dialg_show_error, null);

            TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
            tvContent.setText("打印失败，请重新打印");

            final Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);
            final Button btn_print_again = (Button) grid.findViewById(R.id.btn_dialog_error_sure);

            View.OnClickListener ocl = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (v.getId() == btn_close.getId()) {
                        if (dialog_show_print_error != null) {
                            if (dialog_show_print_error.isShowing()) {
                                dialog_show_print_error.dismiss();
                            }
                        }
                    }
                }
            };
            btn_close.setOnClickListener(ocl);

            btn_print_again.setText("重新打印");
            btn_print_again.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    sendUIMessage(SHOW_LOADING_TEXT,"正在打印");

                    if (dialog_show_print_error != null) {
                        if (dialog_show_print_error.isShowing()) {
                            dialog_show_print_error.dismiss();
                        }
                    }

                    new Thread()
                    {
                        public void run()
                        {
                            int printResult = 0;
                            try {
                                printResult = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                        TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, purpose),
                                        (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                printResult = -1;
                            }

                            sendUIMessage(HIDE_LOADING,"");
                            if(printResult == -1)
                            {
                                Map map = new HashMap();
                                map.put("Odi",odi);
                                map.put("purpose",purpose);
                                CommonUtils.sendObjMsg(map, SHOW_ERROR_MESSAGE_PRINT_AGAIN, handlerPushMeal);
                            }
                            else
                            {
                                sendUIMessage(HIDE_ERROR_MESSAGE_PRINT_AGAIN,"");
                            }
                        }
                    }.start();

                }
            });


            int width = CommonUtils.getScreenWidth(ctxt);
            int height = CommonUtils.getScreenHeight(ctxt);

            if (dialog_show_print_error == null) {
                dialog_show_print_error = new PopupWindow(grid, width, height, true);
            }

            dialog_show_print_error.setBackgroundDrawable(new BitmapDrawable());
            dialog_show_print_error.setContentView(grid);
            dialog_show_print_error.setFocusable(true);
            dialog_show_print_error.setOutsideTouchable(true);
            dialog_show_print_error.setAnimationStyle(R.style.AutoDialogAnimation);
            dialog_show_print_error.showAtLocation(findViewById(R.id.button_action_bar_meal_done_checkout), Gravity.NO_GRAVITY, 0, 0);
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showDialogPrintFailedError error: " + e.getMessage());
        }


    }
}
