package com.huofu.RestaurantOS.ui.pannel.delivery;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryGetDeliveried;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryGetDeliverying;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryGetDoing;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryGetWaitingDelivery;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryGetWaitingTodo;
import com.huofu.RestaurantOS.adapter.GridviewDeliveryStaffListAdapter;
import com.huofu.RestaurantOS.adapter.GridviewSearchMobileResultAdapter;
import com.huofu.RestaurantOS.adapter.HorizontalListviewItemAdapter;
import com.huofu.RestaurantOS.adapter.ListviewChargeItemAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.Amount;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.user.DeliveryStaff;
import com.huofu.RestaurantOS.bean.user.staff;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.widget.SystemBarTintManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.readystatesoftware.viewbadger.BadgeView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/****
 * 外送模块
 */
public class DeliveryActivity extends BaseActivity {

    public static String tag = "DeliveryActivity";//tintManager
    public static boolean active = false;
    public boolean hasFocus = false;
    public Amount amountLast = new Amount();
    public static ImageView ivLoadMoreTips = null;
    public static TextView tvLoadMoreTips = null;

    public int chooseAllDeliveryNum = 0;//批量配送时选中的个数

    public static BitmapUtils bitmapUtils;
    public static BitmapDisplayConfig bigPicDisplayConfig;
    public static DefaultBitmapLoadCallBack<ImageView> callback;// 图片加载的回调函数
    public static DefaultBitmapLoadCallBack<ImageView> callbackSetOverLay;//图片加载后设置蒙版

    public static Context ctxt = null;
    public static List<OrderDetailInfo> listDeliveryChoose = null;//批量配送时选中的订单列表
    public static OrderDetailInfo odiSingleDelivery = null;//单独配送时的订单
    public static boolean flagSingDelivery = false;//单独配送标志位

    public PopupWindow popwindowAllFunc = null;
    public PopupWindow popwindowShowOrderDetailInfo = null;
    public PopupWindow popwindowShowStaffList = null;
    public PopupWindow popwindowShowRefundPart = null;
    public PopupWindow pop_loading = null;

    public static ActivityManager mActivityManager = null;
    public static String pathLoginStaffCache = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "ImageCache" + File.separator;

    public static Handler handler = null;
    public Handler handlerThread = null;

    public Button button_delivery_action_wating_todo = null;
    public RelativeLayout button_delivery_action_wating_todo_underline = null;
    public BadgeView badget_waiting_todo = null;

    public Button button_delivery_action__doing = null;
    public RelativeLayout button_delivery_action__doing_underline = null;
    public BadgeView badget_doing = null;

    public Button button_delivery_action_waiting_delivery = null;
    public RelativeLayout button_delivery_action_waiting_delivery_underline = null;
    public BadgeView badget_waiting_delivery = null;

    public Button button_delivery_action_deliverying = null;
    public RelativeLayout button_delivery_action_deliverying_underline = null;
    public BadgeView badget_deliverying = null;

    public Button button_delivery_action_delivery_done = null;
    public RelativeLayout button_delivery_action_delivery_done_underline = null;

    public Button button_delivery_search = null;
    public RelativeLayout button_delivery_action_search_underline = null;

    public static GridviewDeliveryGetWaitingTodo adapterWaitingToDo = null;
    public static GridviewDeliveryGetDoing adapterDoing = null;
    public static GridviewDeliveryGetWaitingDelivery adapterWaitingDelivery = null;
    public static GridviewDeliveryGetDeliverying adapterDeliverying = null;
    public static GridviewDeliveryGetDeliveried adapterDeliveried = null;


    PopupWindow dialog_show_error = null;

    RelativeLayout rlDeliveryChildMain = null;
    RelativeLayout rlSendMany = null;

    GridView gridviewDelivery = null;


    LayoutInflater inflater = null;
    HorizontalListviewItemAdapter HLadapter = null;

    Timer timerRefreshData = null;
    TimerTask timerTaskRefreshData = null;
    public static List<DeliveryStaff> listStaff = null;
    public static int positionStaffId = -1;
    GridView gridviewStaffList = null;

    public static int nowInTab = -1;

    public static int nowPage = 1;
    public static int nowTotalPage = 1;


    public static String mobile = "";

    public static final int SHOW_LOADING_TEXT = 0;
    public static final int HIDE_LOADING = 1;
    public static final int SHOW_ERROR_MESSAGE = 2;

    public static final int SHOW_GET_DELIVERY_ORDER_LSIT = 3;
    public static final int SHOW_ORDER_DETAIL_INFO = 8;//显示订单详情窗口
    public static final int UPDATE_HORIZONAL_SCORLLVIEW = 9;//更新横向滑动列表（批量配送时选中的订单）
    public static final int SHOW_SATFF_LIST = 10;//显示职员列表(单选)
    public static final int SHOW_ERROR_MESSAGE_DELIVERY_MARKED = 11;//当标记为送达时的弹窗
    public static final int UPDATE_ACTION_BAR_BADGET = 12;//更新角标上的数字
    public static final int SHOW_ERROR_MESSAGE_CANCEL = 13;//取消并退款的弹窗
    public static final int SHOW_REFUND_PART_WINDOW = 14;//网单退款
    public static final int CLEAN_DELIVERY_DEPATCH_BACKGROUND = 15;//清空批量配送后边的那个背景图
    public static final int SHOW_GET_DELIVERY_ORDER_SEARCH_RESULT = 16;//显示搜索结果
    public static final int SHOW_TOAST = 17;//显示toast通知

    public static final int NOTIFY_DELIVERY_WAITNG_TO_DO = 18;//更新等待备餐界面
    public static final int NOTIFY_DELIVERY_DOING = 19;//更新正在备餐界面
    public static final int NOTIFY_DELIVERY_WAITNG_TO_DELIVERY = 20;//更新等待配送界面
    public static final int NOTIFY_DELIVERYING = 21;//更新正在配送界面
    public static final int NOTIFY_DELIVERIED = 22;//更新已送达界面
    public static final int LOAD_STAFF_LIST = 23;//加载送餐人员列表

    /******************************生命周期**********************/

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery_main);

        init();

        widgetConfigure();

        widgetCofigureNavigationBar(0);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        active = true;
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
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
        ViewServer.get(this).removeWindow(this);

        if (adapterDeliveried != null) {
            adapterDeliveried.clear();
        }

        if (adapterDeliverying != null) {
            adapterDeliverying.clear();
        }

        if (adapterDoing != null) {
            adapterDoing.clear();
        }

        if (adapterWaitingDelivery != null) {
            adapterWaitingDelivery.clear();
        }

        if (adapterWaitingToDo != null) {
            adapterWaitingToDo.clear();
        }

        timerTaskRefreshData.cancel();
        timerRefreshData.cancel();

        if (popwindowAllFunc != null && popwindowAllFunc.isShowing()) {
            popwindowAllFunc.dismiss();
        }

        if (pop_loading != null) {
            pop_loading.dismiss();
        }

        if (popwindowShowOrderDetailInfo != null) {
            popwindowShowOrderDetailInfo.dismiss();
        }

        if (popwindowShowStaffList != null) {
            popwindowShowStaffList.dismiss();
        }

        if (popwindowShowRefundPart != null) {
            popwindowShowRefundPart.dismiss();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
    }

    /*******************************初始化*********************/
    /**
     * 变量初始化
     */
    public void init() {
        ctxt = getApplicationContext();
        inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewServer.get(this).addWindow(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bitmapUtils = new BitmapUtils(getApplicationContext(), pathLoginStaffCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用,图片太大时容易OOM。

        callbackSetOverLay = new DefaultBitmapLoadCallBack<ImageView>() {
            public void onLoadCompleted(ImageView container, String uri, android.graphics.Bitmap bitmap, BitmapDisplayConfig config, com.lidroid.xutils.bitmap.callback.BitmapLoadFrom from) {

                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(circleBitmap);
                Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(0xff424242);
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, (bitmap.getWidth() * 0.9f) / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                Drawable d1 = new BitmapDrawable(circleBitmap);
                Drawable d2 = DeliveryActivity.this.getResources().getDrawable(R.drawable.head_photo_overlay);
                Drawable[] array = new Drawable[]{d1, d2};

                LayerDrawable ld = new LayerDrawable(array);
                container.setBackgroundDrawable(ld);

            }

            ;
        };

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        rlDeliveryChildMain = (RelativeLayout) findViewById(R.id.rl_delivery_main_child);//主布局，里边动态添加view去展示不同状态的外送界面

        View grid = inflater.inflate(R.layout.gridview_delivery, null);
    /*	pullToRefreshLayout = ((PullToRefreshLayout) grid.findViewById(R.id.refresh_view));
        pullToRefreshLayout.setOnRefreshListener(new delivery_listener());*/

        //gridviewDelivery这个gridview会动态加载adapter来显示不同状态的外送界面
        gridviewDelivery = (GridView) grid.findViewById(R.id.gridview_delivery_common);
        rlSendMany = (RelativeLayout) grid.findViewById(R.id.rl_gridview_delivery_send_many);

        rlDeliveryChildMain.addView(grid);

        listDeliveryChoose = new ArrayList<OrderDetailInfo>();

        timerRefreshData = new Timer();
        timerTaskRefreshData = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                loadDeliveryAnalysisData();

            }
        };

        listStaff = new ArrayList<DeliveryStaff>();

        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));
        tintManager.setStatusBarDarkMode(true, this);

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
            case SHOW_TOAST:
                Toast.makeText(ctxt, (String) msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case SHOW_ERROR_MESSAGE_DELIVERY_MARKED:
                String orderId = (String) msg.obj;
                showDialogError(orderId, 2);
                break;
            case SHOW_ERROR_MESSAGE_CANCEL:
                Map map = (Map) msg.obj;
                showDialogError((String) map.get("errorMsg"), (OrderDetailInfo) map.get("odi"), (Integer) map.get("refund_status"));
                break;
            case SHOW_GET_DELIVERY_ORDER_LSIT:
                map = (Map) msg.obj;
                int status = (Integer) map.get("status");
                List<OrderDetailInfo> listODI = (List<OrderDetailInfo>) map.get("OrderDetailInfoList");
                boolean flagAuto = (Boolean) map.get("auto");
                if (status != nowInTab) {
                    return;
                }
                if (listODI.size() == 0) {
                    gridviewDelivery.setAdapter(null);
                    findViewById(R.id.empty_delivery_gridview).setVisibility(View.VISIBLE);
                    return;
                }
                sendUIMessage(SHOW_LOADING_TEXT, "加载数据");
                showDeliveryView(listODI, true, status);//1:等待备餐 2:正在备餐 3:等待配送 4:正在配送 5:已送达
                sendUIMessage(HIDE_LOADING, "加载数据");
                break;
            case SHOW_ORDER_DETAIL_INFO:
                showOrderDetailInfo((OrderDetailInfo) msg.obj);
                break;
            case UPDATE_HORIZONAL_SCORLLVIEW:
                showHorizotalScorllView();
                break;
            case SHOW_SATFF_LIST:
                showStaffList(1, (Map) msg.obj);
                break;
            case UPDATE_ACTION_BAR_BADGET:
                try {
                    Amount amountTmp = (Amount) msg.obj;
                    CommonUtils.getRunningAppProcessInfo();
                    if (!amountTmp.whetherDiff(amountLast))//如果和上次的相同，则不用更新角标和gridview了
                    {
                        return;
                    }

                    if (nowInTab == 1 || nowInTab == 2 || nowInTab == 3 || nowInTab == 4 || nowInTab == 5) {
                        loadDeliveryOrderList(nowInTab, 1);
                    }

                    if (amountTmp.wait_for_prepare > 0 && badget_waiting_todo != null) {
                        badget_waiting_todo.setText(amountTmp.wait_for_prepare + "");
                        badget_waiting_todo.show();
                    } else {
                        if (adapterWaitingToDo != null) {
                            adapterWaitingToDo.clear();
                            adapterWaitingToDo.notifyDataSetChanged();
                        }
                        if (badget_waiting_todo != null) {
                            badget_waiting_todo.hide();
                        }
                    }

                    if (amountTmp.preparing > 0 && badget_doing != null) {
                        badget_doing.setText(amountTmp.preparing + "");
                        badget_doing.show();
                    } else {
                        if (adapterDoing != null) {
                            adapterDoing.clear();
                            adapterDoing.notifyDataSetChanged();
                        }
                        if (badget_doing != null) {
                            badget_doing.hide();
                        }
                    }

                    if (amountTmp.wait_for_delivery > 0 && badget_waiting_delivery != null) {
                        badget_waiting_delivery.setText(amountTmp.wait_for_delivery + "");
                        badget_waiting_delivery.show();
                    } else {
                        if (adapterDeliverying != null) {
                            adapterDeliverying.clear();
                            adapterDeliverying.notifyDataSetChanged();
                        }
                        if (badget_waiting_delivery != null) {
                            badget_waiting_delivery.hide();
                        }
                    }

                    if (amountTmp.delivering > 0 && badget_deliverying != null) {
                        badget_deliverying.setText(amountTmp.delivering + "");
                        badget_deliverying.show();
                    } else {
                        if (adapterDeliveried != null) {
                            adapterDeliveried.clear();
                            adapterDeliveried.notifyDataSetChanged();
                        }
                        if (badget_deliverying != null) {
                            badget_deliverying.hide();
                        }
                    }

                    amountLast = amountTmp;
                } catch (Exception e) {
                    CommonUtils.LogWuwei(tag, "handler deal UPDATE_ACTION_BAR_BADGET msg error:" + e.getMessage());
                }

                break;
            case SHOW_REFUND_PART_WINDOW:
                showRefundPartWindow((OrderDetailInfo) msg.obj);
                break;
            case CLEAN_DELIVERY_DEPATCH_BACKGROUND:
                rlSendMany.setBackgroundResource(android.R.color.transparent);
                rlSendMany.removeAllViews();
                showHorizotalScorllView();
                break;
            case SHOW_GET_DELIVERY_ORDER_SEARCH_RESULT:
                updateDeliveryMobileSearchResult((Map) msg.obj);
                break;
            case NOTIFY_DELIVERY_WAITNG_TO_DO:
                if (adapterWaitingToDo != null) {
                    if (adapterWaitingToDo.getCount() == 0) {
                        showDeliveryView(null, false, 0);
                    } else {
                        adapterWaitingToDo.notifyDataSetChanged();
                    }

                }
                break;
            case NOTIFY_DELIVERY_DOING:
                if (adapterDoing != null) {
                    if (adapterDoing.getCount() == 0) {
                        showDeliveryView(null, false, 1);
                    } else {
                        adapterDoing.notifyDataSetChanged();
                    }

                }
                break;
            case NOTIFY_DELIVERY_WAITNG_TO_DELIVERY:
                if (adapterWaitingDelivery != null) {
                    if (adapterWaitingDelivery.getCount() == 0) {
                        showDeliveryView(null, false, 2);
                    } else {
                        adapterWaitingDelivery.notifyDataSetChanged();
                    }

                }
                break;
            case NOTIFY_DELIVERYING:
                if (adapterDeliverying != null) {
                    if (adapterDeliverying.getCount() == 0) {
                        showDeliveryView(null, false, 3);
                    } else {
                        adapterDeliverying.notifyDataSetChanged();
                    }
                }
                //
                break;
            case NOTIFY_DELIVERIED:
                if (adapterDeliveried != null) {
                    if (adapterDeliveried.getCount() == 0) {
                        showDeliveryView(null, false, 3);
                    } else {
                        adapterDeliveried.notifyDataSetChanged();
                    }
                    //adapterDeliveried.notifyDataSetChanged();
                }
                //showDeliveryView(null, false, 4);
                break;
            case LOAD_STAFF_LIST:
                loadStaffsList();
        }
    }

    /***
     * 控件配置
     */
    public void widgetConfigure() {

        launchPadConfigure();

        tabConfigure();

        timerRefreshData.schedule(timerTaskRefreshData, 0, 5000);
    }

    /******************************
     * 配置
     *************************/
    public void tabConfigure() {

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_delivery_custom_layout);
        actionBar.setDisplayShowHomeEnabled(false);

        View grid = actionBar.getCustomView();

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.LogWuwei("click", "clicked");
                CommonUtils.sendMsg("刷新数据中...", SHOW_LOADING_TEXT, handler);
                loadDeliveryAnalysisData();
                nowPage = 1;
                if (nowInTab == 6) {
                    View grid = inflater.inflate(R.layout.gridview_delivery, null);
                    gridviewDelivery = (GridView) grid.findViewById(R.id.gridview_delivery_common);
                    rlSendMany = (RelativeLayout) grid.findViewById(R.id.rl_gridview_delivery_send_many);

                    rlDeliveryChildMain.addView(grid);
                }


                button_delivery_action_wating_todo.setAlpha(0.5f);
                button_delivery_action__doing.setAlpha(0.5f);
                button_delivery_action_waiting_delivery.setAlpha(0.5f);
                button_delivery_action_deliverying.setAlpha(0.5f);
                button_delivery_action_delivery_done.setAlpha(0.5f);
                button_delivery_search.setAlpha(0.5f);

                button_delivery_action_wating_todo_underline.setVisibility(View.INVISIBLE);
                button_delivery_action__doing_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_waiting_delivery_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_deliverying_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_delivery_done_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_search_underline.setVisibility(View.INVISIBLE);

                switch (v.getId()) {
                    case R.id.button_action_bar_delivery_waiting_todo:
                        nowInTab = 1;
                        button_delivery_action_wating_todo.setAlpha(1);
                        button_delivery_action_wating_todo_underline.setVisibility(View.VISIBLE);
                        break;
                    case R.id.button_action_bar_delivery_doing:
                        nowInTab = 2;
                        button_delivery_action__doing.setAlpha(1f);
                        button_delivery_action__doing_underline.setVisibility(View.VISIBLE);
                        break;
                    case R.id.button_action_bar_delivery_waiting_to_delivery:
                        nowInTab = 3;
                        button_delivery_action_waiting_delivery.setAlpha(1f);
                        button_delivery_action_waiting_delivery_underline.setVisibility(View.VISIBLE);
                        break;
                    case R.id.button_action_bar_delivery_deliverying:
                        nowInTab = 4;
                        button_delivery_action_deliverying.setAlpha(1f);
                        button_delivery_action_deliverying_underline.setVisibility(View.VISIBLE);
                        break;
                    case R.id.button_action_bar_delivery_done:
                        nowInTab = 5;
                        button_delivery_action_delivery_done.setAlpha(1f);
                        button_delivery_action_delivery_done_underline.setVisibility(View.VISIBLE);
                        break;
                }

                loadDeliveryOrderList(nowInTab, 1);
                if(listDeliveryChoose != null && listDeliveryChoose.size() != 0 && nowInTab != 3)
                {
                    listDeliveryChoose.clear();
                    showHorizotalScorllView();
                }
            }
        };

        Button button_menu = (Button) findViewById(R.id.button_action_bar_delivery_menu);
        button_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                mslidingMenu.toggleLeftDrawer();

                widgetCofigureNavigationBar(1);

            }
        });
        findViewById(R.id.rl_action_bar_delivery_menu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                tintManager.setStatusBarDarkMode(false, DeliveryActivity.this);
                tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                mslidingMenu.toggleLeftDrawer();
            }
        });

        List<Button> list = new ArrayList<Button>();
        List<BadgeView> listBadget = new ArrayList<BadgeView>();

        button_delivery_action_wating_todo_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_waiting_todo_underline);
        button_delivery_action_wating_todo = (Button) grid.findViewById(R.id.button_action_bar_delivery_waiting_todo);
        badget_waiting_todo = new BadgeView(ctxt, button_delivery_action_wating_todo);
        listBadget.add(badget_waiting_todo);

        button_delivery_action__doing = (Button) grid.findViewById(R.id.button_action_bar_delivery_doing);
        button_delivery_action__doing_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_doing_underline);
        badget_doing = new BadgeView(ctxt, button_delivery_action__doing);
        listBadget.add(badget_doing);

        button_delivery_action_waiting_delivery = (Button) grid.findViewById(R.id.button_action_bar_delivery_waiting_to_delivery);
        button_delivery_action_waiting_delivery_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_waiting_to_delivery_underline);
        badget_waiting_delivery = new BadgeView(ctxt, button_delivery_action_waiting_delivery);
        listBadget.add(badget_waiting_delivery);

        button_delivery_action_deliverying = (Button) grid.findViewById(R.id.button_action_bar_delivery_deliverying);
        button_delivery_action_deliverying_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_deliverying_underline);
        badget_deliverying = new BadgeView(ctxt, button_delivery_action_deliverying);
        listBadget.add(badget_deliverying);

        for (int k = 0; k < listBadget.size(); k++) {
            BadgeView view = listBadget.get(k);
            view.setBadgeMargin(10, 25);
            view.setBackgroundResource(R.drawable.expandablelistview_item_badget_background);
            view.setTextSize(12f);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.parseColor("#ffffff"));
        }
        button_delivery_action_delivery_done = (Button) grid.findViewById(R.id.button_action_bar_delivery_done);
        button_delivery_action_delivery_done_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_done_underline);

        button_delivery_search = (Button) grid.findViewById(R.id.button_action_bar_delivery_search);
        button_delivery_action_search_underline = (RelativeLayout) grid.findViewById(R.id.button_action_bar_delivery_search_underline);
        button_delivery_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                nowInTab = 6;
                button_delivery_action_wating_todo_underline.setVisibility(View.INVISIBLE);
                button_delivery_action__doing_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_waiting_delivery_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_deliverying_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_delivery_done_underline.setVisibility(View.INVISIBLE);
                button_delivery_action_search_underline.setVisibility(View.VISIBLE);

                button_delivery_action_wating_todo.setAlpha(0.5f);
                button_delivery_action__doing.setAlpha(0.5f);
                button_delivery_action_waiting_delivery.setAlpha(0.5f);
                button_delivery_action_deliverying.setAlpha(0.5f);
                button_delivery_action_delivery_done.setAlpha(0.5f);
                button_delivery_search.setAlpha(1f);

                showSearchLayout();


            }
        });


        button_delivery_action_wating_todo.setOnClickListener(ocl);
        button_delivery_action__doing.setOnClickListener(ocl);
        button_delivery_action_waiting_delivery.setOnClickListener(ocl);
        button_delivery_action_deliverying.setOnClickListener(ocl);
        button_delivery_action_delivery_done.setOnClickListener(ocl);


        button_delivery_action_wating_todo_underline.setVisibility(View.INVISIBLE);
        button_delivery_action__doing_underline.setVisibility(View.INVISIBLE);
        button_delivery_action_waiting_delivery_underline.setVisibility(View.INVISIBLE);
        button_delivery_action_deliverying_underline.setVisibility(View.INVISIBLE);
        button_delivery_action_delivery_done_underline.setVisibility(View.INVISIBLE);
        button_delivery_action_search_underline.setVisibility(View.INVISIBLE);

        button_delivery_action_wating_todo.performClick();

		/*RelativeLayout rl = (RelativeLayout)findViewById(R.id.delivery_action_bar_custom);
		rl.setBackgroundDrawable(new ColorDrawable(Color.parseColor(actionBarBackGroundColor)));*/

    }

    /*****************************窗口显示******************************/
    /**
     * 显示异常的窗口
     *
     * @param msg:错误内容
     * @param result:-1->退回到登录界面 0-》停留在当前窗口 1-》去设置界面 2->设置为已经送达
     */
    public void showDialogError(final String msg, final int result) {
		/*if (dialog_show_error == null)
		{
			dialog_show_error = new Dialog(DeliveryActivity.this,R.style.Shake300Dialog);
		}*/

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = null;
        if (result != 2) {
            grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);
            Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);
            if (result == 1) {
                btn_close.setText("设置");
            }

            OnClickListener ocl = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //if (v.getId() == btn_close.getId())
                    {
                        if (dialog_show_error != null) {
                            if (dialog_show_error.isShowing()) {
                                dialog_show_error.dismiss();
                            }
                        }
                        if (result == -1) {
                            startActivity(new Intent(DeliveryActivity.this, LoginActivity.class));
                            finish();
                        } else if (result == 1) {
                            startActivity(new Intent(DeliveryActivity.this, SettingsActivity.class));
                            finish();
                        }
                    }

                }
            };
            btn_close.setOnClickListener(ocl);
            TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
            tvContent.setText(msg);
        } else if (result == 2) {
            grid = inflater.inflate(R.layout.dialg_show_error, null);
            Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_sure);
            btn_close.setText("放弃");
            btn_close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (dialog_show_error != null && active) {
                        if (dialog_show_error.isShowing()) {
                            dialog_show_error.dismiss();
                        }
                    }
                }
            });

            Button btnEnter = (Button) grid.findViewById(R.id.btn_dialog_error_close);
            btnEnter.setText("确定");
            btnEnter.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CommonUtils.sendMsg("正在标记为已送达", SHOW_LOADING_TEXT, handler);
                    if (dialog_show_error != null && active) {
                        if (dialog_show_error.isShowing()) {
                            dialog_show_error.dismiss();
                        }
                    }

                    List<String> list = new ArrayList<String>();
                    list.add(msg);
                    setDeliveryOrderDeliveried(list);

                }
            });

            TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
            tvContent.setText("为避免订单纠纷，请务必确认送餐员已经送到");
        }

        if (dialog_show_error == null) {
            dialog_show_error = new PopupWindow(grid, 2048, 1536, true);
        } else {
            dialog_show_error.setContentView(grid);
        }

        dialog_show_error.setFocusable(true);
        dialog_show_error.setOutsideTouchable(true);
        dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
        dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
        dialog_show_error.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);


    }


    /***
     * 退款弹窗
     *
     * @param msg
     * @param odi
     * @param refund_status
     */
    public void showDialogError(final String msg, final OrderDetailInfo odi, final int refund_status) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid;
        grid = inflater.inflate(R.layout.dialg_show_error, null);
        Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_sure);
        btn_close.setText("放弃退款");
        btn_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dialog_show_error != null && active) {
                    if (dialog_show_error.isShowing()) {
                        dialog_show_error.dismiss();
                    }
                }
            }
        });

        Button btnEnter = (Button) grid.findViewById(R.id.btn_dialog_error_close);
        btnEnter.setText("退款");
        btnEnter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (dialog_show_error != null && active) {
                    if (dialog_show_error.isShowing()) {
                        dialog_show_error.dismiss();
                    }
                }

                JSONArray array = new JSONArray();
                JSONObject objRefund = new JSONObject();
                try {
                    objRefund.put("refund_amount", odi.store_order.actual_price);
                    objRefund.put("refund_status", refund_status);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                array.put(objRefund);

                ApisManager.setOrderRefund(odi.store_order.actual_price, refund_status, odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        if(nowInTab == 1)
                        {
                            CommonUtils.sendMsg("刷新等待备餐列表", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                            CommonUtils.sendMsg(null, DeliveryActivity.NOTIFY_DELIVERY_WAITNG_TO_DO, handler);
                            loadDeliveryOrderList(1,100);
                        }
                        else if(nowInTab == 2)
                        {
                            CommonUtils.sendMsg("刷新正在备餐列表", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                            loadDeliveryOrderList(2,100);
                            CommonUtils.sendMsg(null, DeliveryActivity.NOTIFY_DELIVERY_DOING, handler);
                        }
                        else if(nowInTab == 6)
                        {
                            CommonUtils.sendMsg("刷新搜索列表", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                            loadSearchMobileResult(1,mobile);
                        }

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {

                    }
                });


                if (nowInTab == 6) {
                    loadSearchMobileResult(1,mobile);
                }
            }
        });

        TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
        tvContent.setText("为避免订单纠纷，请务必确认送餐员已经送到");
        if (dialog_show_error == null) {
            dialog_show_error = new PopupWindow(grid, 2048, 1536, true);
        } else {
            dialog_show_error.setContentView(grid);
        }

        dialog_show_error.setFocusable(true);
        dialog_show_error.setOutsideTouchable(true);
        dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
        dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
        dialog_show_error.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);

    }


    /**
     * 显示外送信息
     *
     * @param listODI
     * @param flagAuto
     * @param status
     */
    public void showDeliveryView(List<OrderDetailInfo> listODI, boolean flagAuto, int status) {
        boolean flagUpdate = false;

        if (!flagAuto) {
            flagUpdate = true;
        } else {
            switch (status) {
                case 1:
                    if (adapterWaitingToDo != gridviewDelivery.getAdapter()) {
                        flagUpdate = true;
                    } else {
                        if (adapterWaitingToDo != null && listODI != null) {
                            if (adapterWaitingToDo.whetherDiff(listODI)) {
                                flagUpdate = true;
                            }
                        } else {
                            flagUpdate = true;
                        }
                    }
                    break;
                case 2:
                    if (adapterDoing != gridviewDelivery.getAdapter()) {
                        flagUpdate = true;
                    } else {
                        if (adapterDoing != null && listODI != null) {
                            if (adapterDoing.whetherDiff(listODI)) {
                                flagUpdate = true;
                            }
                        } else {
                            flagUpdate = true;
                        }
                    }

                    break;
                case 3:
                    if (adapterWaitingDelivery != gridviewDelivery.getAdapter()) {
                        flagUpdate = true;
                    } else {
                        if (adapterWaitingDelivery != null && listODI != null) {
                            if (adapterWaitingDelivery.whetherDiff(listODI)) {
                                flagUpdate = true;
                            }
                        } else {
                            flagUpdate = true;
                        }
                    }

                    break;
                case 4:
                    if (adapterDeliverying != gridviewDelivery.getAdapter()) {
                        flagUpdate = true;
                    } else {
                        if (adapterDeliverying != null && listODI != null) {
                            if (adapterDeliverying.whetherDiff(listODI)) {
                                flagUpdate = true;
                            }
                        } else {
                            flagUpdate = true;
                        }
                    }

                    break;
                case 5:
                    if (adapterDeliveried != gridviewDelivery.getAdapter()) {
                        flagUpdate = true;
                    } else {
                        if (adapterDeliveried != null && listODI != null) {
                            if (adapterDeliveried.whetherDiff(listODI)) {
                                flagUpdate = true;
                            }
                        } else {
                            flagUpdate = true;
                        }
                    }

                    break;
            }
        }

        if (flagUpdate) {
            if (listDeliveryChoose != null && listDeliveryChoose.size() != 0 && status != 3) {
                listDeliveryChoose.clear();
                showHorizotalScorllView();
            }

            if (listODI != null && listODI.size() != 0) {
                switch (status) {
                    case 1:
                        if (adapterWaitingToDo == null) {
                            adapterWaitingToDo = new GridviewDeliveryGetWaitingTodo(listODI, ctxt, handler);
                            gridviewDelivery.setAdapter(adapterWaitingToDo);
                        }
                        break;
                    case 2:
                        if (adapterDoing == null) {
                            adapterDoing = new GridviewDeliveryGetDoing(listODI, ctxt, handler);
                            gridviewDelivery.setAdapter(adapterDoing);
                        }
                        break;
                    case 3:
                        if (adapterWaitingDelivery == null) {
                            adapterWaitingDelivery = new GridviewDeliveryGetWaitingDelivery(listODI, ctxt, handler);
                            gridviewDelivery.setAdapter(adapterWaitingDelivery);
                        }
                        break;
                    case 4:
                        if (adapterDeliverying == null) {
                            adapterDeliverying = new GridviewDeliveryGetDeliverying(listODI, ctxt, handler);
                            gridviewDelivery.setAdapter(adapterDeliverying);
                        }
                        break;
                    case 5:
                        if (adapterDeliveried == null) {
                            adapterDeliveried = new GridviewDeliveryGetDeliveried(listODI, ctxt, handler);
                            gridviewDelivery.setAdapter(adapterDeliveried);
                        }
                        break;
                }

                if (status == nowInTab) {
                    int beforeSize = 0;
                    int afterSize = 0;
                    switch (status) {
                        case 1:
                            beforeSize = adapterWaitingToDo.getCount();
                            adapterWaitingToDo.add(listODI);
                            afterSize = adapterWaitingToDo.getCount();

                            adapterWaitingToDo.notifyDataSetChanged();
                            gridviewDelivery.setAdapter(adapterWaitingToDo);
                            if (beforeSize < afterSize) {
                                gridviewDelivery.smoothScrollToPosition(adapterWaitingToDo.getCount());
                            }

                            break;
                        case 2:
                            beforeSize = adapterDoing.getCount();
                            adapterDoing.add(listODI);
                            afterSize = adapterDoing.getCount();

                            adapterDoing.notifyDataSetChanged();
                            gridviewDelivery.setAdapter(adapterDoing);
                            if (beforeSize < afterSize) {
                                gridviewDelivery.smoothScrollToPosition(adapterDoing.getCount());
                            }
                            break;
                        case 3:
                            beforeSize = adapterWaitingDelivery.getCount();
                            adapterWaitingDelivery.add(listODI);
                            afterSize = adapterWaitingDelivery.getCount();

                            adapterWaitingDelivery.notifyDataSetChanged();
                            gridviewDelivery.setAdapter(adapterWaitingDelivery);
                            if (beforeSize < afterSize) {
                                gridviewDelivery.smoothScrollToPosition(adapterWaitingDelivery.getCount());
                            }

                            break;
                        case 4:
                            beforeSize = adapterDeliverying.getCount();
                            adapterDeliverying.add(listODI);
                            afterSize = adapterDeliverying.getCount();

                            adapterDeliverying.notifyDataSetChanged();
                            gridviewDelivery.setAdapter(adapterDeliverying);
                            if (beforeSize < afterSize) {
                                gridviewDelivery.smoothScrollToPosition(adapterDeliverying.getCount());
                            }

                            break;
                        case 5:
                            beforeSize = adapterDeliveried.getCount();
                            adapterDeliveried.add(listODI);
                            afterSize = adapterDeliveried.getCount();

                            adapterDeliveried.notifyDataSetChanged();
                            gridviewDelivery.setAdapter(adapterDeliveried);
                            if (beforeSize < afterSize) {
                                gridviewDelivery.smoothScrollToPosition(adapterDeliveried.getCount());
                            }
                            break;
                    }
                }


                findViewById(R.id.empty_delivery_gridview).setVisibility(View.INVISIBLE);
            } else {
                boolean flagShowEmppty = true;
                switch (status) {
                    case 1:
                        if (adapterWaitingToDo != null) {
                            if (adapterWaitingToDo.getCount() != 0) {
                                flagShowEmppty = false;
                            }
                        }
                        break;
                    case 2:
                        if (adapterDoing != null) {
                            if (adapterDoing.getCount() != 0) {
                                flagShowEmppty = false;
                            }
                        }
                    case 3:
                        if (adapterWaitingDelivery != null) {
                            if (adapterWaitingDelivery.getCount() != 0) {
                                flagShowEmppty = false;
                            }
                        }
                        break;
                    case 4:
                        if (adapterDeliverying != null) {
                            if (adapterDeliverying.getCount() != 0) {
                                flagShowEmppty = false;
                            }
                        }
                        break;
                    case 5:
                        if (adapterDeliveried != null) {
                            if (adapterDeliveried.getCount() != 0) {
                                flagShowEmppty = false;
                            }
                        }
                        break;
                }

                if (flagShowEmppty) {
                    gridviewDelivery.setAdapter(null);
                    findViewById(R.id.empty_delivery_gridview).setVisibility(View.VISIBLE);
                }
            }
        }

    }


    /****
     * 显示订单详情
     *
     * @param odi
     */
    public void showOrderDetailInfo(OrderDetailInfo odi) {

        View grid = inflater.inflate(R.layout.delivery_order_detail_info, null);

        TextView tvSerial = (TextView) grid.findViewById(R.id.tv_delivery_order_info_detail_content_serial);
        TextView tvStatus = (TextView) grid.findViewById(R.id.tv_delivery_order_info_detail_content_status);
        ListView lv = (ListView) grid.findViewById(R.id.listview_delivery_order_detail_info_charge_items);
        ListviewChargeItemAdapter adapter = new ListviewChargeItemAdapter(odi.list_charge_items_all, ctxt);
        lv.setAdapter(adapter);

        TextView tvAddress = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_address);
        TextView tvContactName = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_contact_person);
        TextView tvContacePhone = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_contact_phone);
        TextView tvInvonce = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_invoce);
        TextView tvAssign = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_assign);

        TextView tvOrderPrice = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_pay_order_price);
        TextView tvTerminalCoupon = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_terminal_coupon);
        TextView tvInternetCoupon = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_internet_coupon);
        TextView tvEnterprise = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_enterprise_rebate);
        TextView tvPayablePrice = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_payable_price);

        TextView tvYJpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_yjpay);
        TextView tvWCpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_wechat);
        TextView tvPOSpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_pos);
        TextView tvCOUPONpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_pay_coupon);
        TextView tvCASHNpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_cash);
        TextView tvPCpay = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_pay_prepaid_card_amount);
        TextView tvUA = (TextView) grid.findViewById(R.id.tv_delivery_order_detail_pay_user_account);

        Button btnClose = (Button) grid.findViewById(R.id.button_delivery_order_detial_info_close);

        if (odi.take_serial_number == 0) {
            tvSerial.setText("订单内容");
        } else {
            tvSerial.setText("" + odi.take_serial_number);
        }

        switch (odi.store_order.store_order_delivery.delivery_status) {
            case 1:
                tvStatus.setText("等待备餐");
                break;
            case 2:
                tvStatus.setText("正在备餐");
                break;
            case 3:
                tvStatus.setText("等待派送");
                break;
            case 4:
                tvStatus.setText("正在派送");
                break;
            case 5:
                tvStatus.setText("已经送达");
                break;
            default:
                break;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");

        tvYJpay.setText(df.format(odi.store_order.order_pay_info.yjpay_amount / 100.0) + "元");
        tvWCpay.setText(df.format(odi.store_order.order_pay_info.wechat_amount / 100.0) + "元");
        tvPOSpay.setText(df.format(odi.store_order.order_pay_info.ipos_amount / 100.0) + "元");
        tvCOUPONpay.setText(df.format(odi.store_order.order_pay_info.coupon_amount / 100.0) + "元");
        tvCASHNpay.setText(df.format(odi.store_order.order_pay_info.cash_amount / 100.0) + "元");
        tvPCpay.setText(df.format(odi.store_order.order_pay_info.prepaid_card_amount / 100.0) + "元");
        tvUA.setText(df.format(odi.store_order.order_pay_info.user_account_amount / 100.0) + "元");

        tvAddress.setText(odi.store_order.store_order_delivery.delivery_building_address);
        tvAddress.setSelected(true);

        tvContactName.setText(odi.store_order.store_order_delivery.contact_name);

        tvContacePhone.setText(odi.store_order.store_order_delivery.contact_phone);

        tvInvonce.setText(odi.store_order.invoice_demand);
        tvInvonce.setSelected(true);

        tvAssign.setText(CommonUtils.getStrTime(odi.store_order.store_order_delivery.delivery_assign_time + ""));
        tvAssign.setSelected(true);

        tvOrderPrice.setText(df.format(odi.store_order.order_price / 100.0) + "元");
        tvTerminalCoupon.setText(df.format(odi.store_order.user_client_coupon / 100.0) + "元");
        tvInternetCoupon.setText(df.format(odi.store_order.internet_rebate_price / 100.0) + "元");
        if (odi.store_order.enterprise_rebate != 0 && odi.store_order.enterprise_rebate_price != 0) {
            tvEnterprise.setText(df.format(odi.store_order.enterprise_rebate_price / 100.0) + "(" + odi.store_order.enterprise_rebate / 10 + "折)");
        } else {
            tvEnterprise.setText("0.00元");
        }


        tvPayablePrice.setText(df.format(odi.store_order.actual_price / 100.0) + "元");

        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popwindowShowOrderDetailInfo != null) {
                    popwindowShowOrderDetailInfo.dismiss();
                }
            }
        });


        if (popwindowShowOrderDetailInfo == null) {
            popwindowShowOrderDetailInfo = new PopupWindow(grid, 2048, 1536, true);
        } else {
            popwindowShowOrderDetailInfo.setContentView(grid);
        }

        try {
            if (active) {
                popwindowShowOrderDetailInfo.setFocusable(true);
                popwindowShowOrderDetailInfo.setOutsideTouchable(true);
                popwindowShowOrderDetailInfo.setAnimationStyle(R.style.AutoDialogAnimation);
                popwindowShowOrderDetailInfo.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 0, 0);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "showOrderDetailInfo window error :" + e.getMessage());
        }


    }


    /***
     * 显示批量配送选中的订单列表
     */
    public void showHorizotalScorllView() {
        if (listDeliveryChoose.size() == 0) {
            gridviewDelivery.setY(50);
            ViewGroup.LayoutParams lp = gridviewDelivery.getLayoutParams();
            lp.height = 2000;
            gridviewDelivery.setLayoutParams(lp);

            chooseAllDeliveryNum = 0;

            if (HLadapter != null) {
                HLadapter.notifyDataSetChanged();
            }
            rlSendMany.removeAllViews();
            //rlSendMany.setBackgroundColor(Color.parseColor("#ffffff"));
            rlSendMany.setBackgroundResource(android.R.color.transparent);
            return;
        }

        if (rlSendMany == null) {
            return;
        }

        //rlSendMany.setBackgroundColor(Color.parseColor("#47505a"));

        View grid = inflater.inflate(R.layout.rl_gridview_delivery_sen_many_child, null);
        HorizontalListView hl = (HorizontalListView) grid.findViewById(R.id.horizntalScrollview_choose_many_send_order);
        HLadapter = new HorizontalListviewItemAdapter(listDeliveryChoose, ctxt, handler);
        hl.setAdapter(HLadapter);

        if (chooseAllDeliveryNum < listDeliveryChoose.size()) {
            hl.scrollTo((HLadapter.getCount() - 1) * 300);
        }

        chooseAllDeliveryNum = listDeliveryChoose.size();

        Button btn = (Button) grid.findViewById(R.id.button_gridview_delivery_send_many);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.sendMsg("获取职员列表", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                loadStaffsList();
            }
        });

        //pullToRefreshLayout.setFocusable(false);

        ViewGroup.LayoutParams lp = gridviewDelivery.getLayoutParams();
        if (lp.height >= 2000) {
            gridviewDelivery.setY(310);
            CommonUtils.LogWuwei(tag, "before height is " + gridviewDelivery.getLayoutParams().height);

            lp.height = 1000;
            gridviewDelivery.setLayoutParams(lp);
            CommonUtils.LogWuwei(tag, "after height is " + gridviewDelivery.getLayoutParams().height);

        }

        rlSendMany.removeAllViews();
        rlSendMany.addView(grid);

    }


    /***
     * @param arg:0              多选时弹窗 1：单独配送
     * @param orderId:单独配送时的订单编号
     */
    public void showStaffList(int arg, Map map) {

        if (listStaff == null) {
            listStaff = new ArrayList<DeliveryStaff>();
        } else {
            listStaff.clear();
        }
        listStaff = (List<DeliveryStaff>) map.get("listStaff");

        View grid = inflater.inflate(R.layout.gridview_delivery_staff, null);
        gridviewStaffList = (GridView) grid.findViewById(R.id.gridviewDeliveryStaffList);
        final GridviewDeliveryStaffListAdapter listStaffAdapter = new GridviewDeliveryStaffListAdapter(ctxt, listStaff);
        gridviewStaffList.setAdapter(listStaffAdapter);
        gridviewStaffList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                positionStaffId = (int) listStaff.get(position).staff_id;

                boolean flagSameUser = false;

               /* for (int i = 0; i < listStaff.size(); i++) {
                    if (listStaff.get(i).flagStaffChoosen) {
                        listStaff.get(i).flagStaffChoosen = false;
                        if (i == position) {
                            flagSameUser = true;
                        }
                    }

                }

                if (!flagSameUser) {
                    listStaff.get(position).flagStaffChoosen = true;
                }*/
                listStaffAdapter.notifyDataSetChanged();
                popwindowShowStaffList.dismiss();

                JSONArray array = new JSONArray();
                List<String> list = new ArrayList<String>();
                if (listDeliveryChoose.size() > 0 && !flagSingDelivery) {
                    for (int k = 0; k < listDeliveryChoose.size(); k++) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("orderId", listDeliveryChoose.get(k).order_id);
                            array.put(obj);
                            list.add(listDeliveryChoose.get(k).order_id);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else if (flagSingDelivery) {
                    JSONObject obj = new JSONObject();
                    try {
                        if (odiSingleDelivery == null) {
                            return;
                        }
                        obj.put("orderId", odiSingleDelivery.order_id + "");
                        array.put(obj);
                        list.add(odiSingleDelivery.order_id);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                setDeliveryOrderDeliverying(list, (Integer) listStaff.get(position).staff_id);
                DeliveryActivity.listDeliveryChoose.clear();
                CommonUtils.sendMsg(null, DeliveryActivity.CLEAN_DELIVERY_DEPATCH_BACKGROUND, handler);

            }

        });

        grid.findViewById(R.id.btn_gridview_delivery_staff_dispatch_cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popwindowShowStaffList.dismiss();
            }
        });

        if (popwindowShowStaffList == null) {
            popwindowShowStaffList = new PopupWindow(grid, 2048, 1536, true);
        } else {
            popwindowShowStaffList.setContentView(grid);
        }

        try {
            if (active) {
                popwindowShowStaffList.setFocusable(true);
                popwindowShowStaffList.setOutsideTouchable(true);
                popwindowShowStaffList.setAnimationStyle(R.style.AutoDialogAnimation);
                popwindowShowStaffList.showAtLocation(button_delivery_action_wating_todo, Gravity.NO_GRAVITY, 0, 0);
            }
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "popwindowShowStaffList show error :" + e.getMessage());
        }
    }


    /***
     * 网单退款窗口
     *
     * @param oid
     */
    public void showRefundPartWindow(OrderDetailInfo oid) {
        View grid = new View(ctxt);

        if (popwindowShowRefundPart == null) {
            popwindowShowRefundPart = new PopupWindow(grid, 400 * 2, 200 * 2, true);
        } else {
            popwindowShowRefundPart.setContentView(grid);
        }

        if (active) {
            popwindowShowRefundPart.setFocusable(true);
            popwindowShowRefundPart.setOutsideTouchable(true);
            popwindowShowRefundPart.setAnimationStyle(R.style.AutoDialogAnimation);
            popwindowShowRefundPart.showAtLocation(mslidingMenu, Gravity.NO_GRAVITY, 312 * 2, 184 * 2);
        }

    }


    /***
     * 显示搜索界面
     */
    public void showSearchLayout() {

        View grid = inflater.inflate(R.layout.gridview_delivery_search, null);
        GridView gridview = (GridView) grid.findViewById(R.id.gridview_delivery_search);
        final SearchView sv = (SearchView) grid.findViewById(R.id.sv_gridview_delivery_search);

        sv.setQueryHint("输入手机号开始查询");
        sv.setIconified(true);
        sv.onActionViewCollapsed();
        sv.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                if (CommonUtils.isPhoneNumber(query)) {

                    CommonUtils.sendMsg("正在搜索手机号为" + mobile + "相关的订单", SHOW_LOADING_TEXT, handler);
                    loadSearchMobileResult(1,query);

                } else {

                    showDialogError("请输入正确的手机号", -3);
                }
                sv.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }

        });

        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(Color.parseColor("#363636"));
        textView.setHint("输入手机号开始查询");
        textView.setHintTextColor(Color.parseColor("#b1b1b1"));

        int searchPlateId = sv.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = sv.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.TRANSPARENT);
        }

        rlDeliveryChildMain.removeAllViews();
        rlDeliveryChildMain.addView(grid);

    }


    /****
     * 显示搜索结果
     */
    public void updateDeliveryMobileSearchResult(Map map) {

        List<OrderDetailInfo> ls = (List<OrderDetailInfo>) map.get("listODI");
        final String mobile = (String) map.get("mobile");

        View grid = inflater.inflate(R.layout.gridview_delivery_search, null);
        GridView gridview = (GridView) grid.findViewById(R.id.gridview_delivery_search);
        GridviewSearchMobileResultAdapter adapter = new GridviewSearchMobileResultAdapter(ls, handler, ctxt, mobile);
        gridview.setAdapter(adapter);
        this.mobile = mobile;

        final SearchView sv = (SearchView) grid.findViewById(R.id.sv_gridview_delivery_search);
        sv.setQueryHint("输入手机号开始查询");
        sv.setIconified(true);
        sv.onActionViewCollapsed();
        sv.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                if (CommonUtils.isPhoneNumber(query)) {

                    CommonUtils.sendMsg("正在搜索手机号为" + mobile + "相关的订单", SHOW_LOADING_TEXT, handler);
                    loadSearchMobileResult(1,query);
                } else {
                    showDialogError("请输入正确的手机号", -3);
                }
                sv.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }

        });

        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(Color.parseColor("#363636"));
        textView.setHint("输入手机号开始查询");
        textView.setHintTextColor(Color.parseColor("#b1b1b1"));

        int searchPlateId = sv.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = sv.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (ls.size() != 0) {
            findViewById(R.id.empty_delivery_gridview).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.empty_delivery_gridview).setVisibility(View.VISIBLE);
        }
        rlDeliveryChildMain.removeAllViews();
        rlDeliveryChildMain.addView(grid);

    }


    /**
     * 接受按键操作并做出响应
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pop_loading != null && pop_loading.isShowing()) {
                pop_loading.dismiss();
                return false;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadSearchMobileResult(int page, final String queryKey)
    {
        ApisManager.getDeliveryListInfoByMobile(page, queryKey, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("",HIDE_LOADING,handler);
                CommonUtils.LogWuwei(tag,"object is "+object.toString());
                List<OrderDetailInfo> list  = (List<OrderDetailInfo>)object;
                Message msg = new Message();
                Map map = new HashMap<String, Object>();
                map.put("listODI", list);
                map.put("mobile", queryKey);
                msg.obj = map;
                msg.what = DeliveryActivity.SHOW_GET_DELIVERY_ORDER_SEARCH_RESULT;
                handler.sendMessage(msg);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("",HIDE_LOADING,handler);
                CommonUtils.sendMsg(response.error_message,SHOW_ERROR_MESSAGE,handler);
            }
        });
    }

    /***
     * 获取外送订单统计数据
     */
    public void loadDeliveryAnalysisData() {
        ApisManager.getDeliveryOrderAnalysisInfo(new ApiCallback() {
            @Override
            public void success(Object object) {
                Amount amount = (Amount) object;
                sendUIMessage(UPDATE_ACTION_BAR_BADGET, amount);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
            }
        });
    }

    /***
     * 获取某个
     *
     * @param status
     */
    public void loadDeliveryOrderList(int status, int page) {

        ApisManager.getDeliveryListInfo(status, page, new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, "");

                Map map = new HashMap<String, Object>();
                map.put("status", nowInTab);
                map.put("OrderDetailInfoList", (List<OrderDetailInfo>) object);
                map.put("auto", true);

                sendUIMessage(SHOW_GET_DELIVERY_ORDER_LSIT, map);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
            }
        });
    }

    /***
     * 获取员工列表
     */
    public void loadStaffsList() {
        ApisManager.getDeliveryStaffs(new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, "");

                Map map = new HashMap<String, Object>();
                map.put("listStaff", (List<staff>) object);
                sendUIMessage(SHOW_SATFF_LIST, map);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, "" + response.error_message);
            }
        });
    }


    /**
     * 设置订单正在派送
     *
     * @param list     订单id的list
     * @param staff_id 派送员工的id
     */
    public void setDeliveryOrderDeliverying(List<String> list, int staff_id) {
        sendUIMessage(SHOW_LOADING_TEXT, "正在配送...");
        ApisManager.setDeliveryOrderDeliverying(staff_id, list, new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, "");
                if (nowInTab == 4) {
                    adapterDeliverying.clear();
                }
                loadDeliveryOrderList(nowInTab, 1);
                loadDeliveryAnalysisData();
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message + "");
            }
        });
    }


    /**
     * 设置订单已经送达
     *
     * @param list
     */
    public void setDeliveryOrderDeliveried(List<String> list) {

        sendUIMessage(SHOW_LOADING_TEXT, "正在标记订单为已送达状态...");
        ApisManager.setDeliveryOrderDeliveried(list, new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, "");
                loadDeliveryOrderList(nowInTab, 1);
                loadDeliveryAnalysisData();
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
            }
        });
    }

}
