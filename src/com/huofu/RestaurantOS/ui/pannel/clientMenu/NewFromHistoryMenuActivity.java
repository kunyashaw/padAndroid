package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;
import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.support.greenDao.MenuTableDao;
import com.huofu.RestaurantOS.support.niftyDialog.Effectstype;
import com.huofu.RestaurantOS.support.niftyDialog.NiftyDialogBuilder;
import com.huofu.RestaurantOS.support.privoinceSelector.CharacterParser;
import com.huofu.RestaurantOS.support.privoinceSelector.ClearEditText;
import com.huofu.RestaurantOS.support.privoinceSelector.PinyinComparator;
import com.huofu.RestaurantOS.support.privoinceSelector.SideBar;
import com.huofu.RestaurantOS.support.privoinceSelector.SideBar.OnTouchingLetterChangedListener;
import com.huofu.RestaurantOS.support.privoinceSelector.SortAdapter;
import com.huofu.RestaurantOS.support.privoinceSelector.SortModel;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.ui.pannel.tvMenu.ShowMenuActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.RateTextCircularProgressBar;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/****
 * 菜单新建
 */

public class NewFromHistoryMenuActivity extends Activity implements OnTouchListener {

    public static boolean active = false;
    public static String tag = "NewFromHistoryMenuActivity";//askUpdateMenu
    public static boolean hasFocus = false;
    public int progress = 0;

    static Context context = null;
    public String ip_address = "";
    public static int choice_last_or_customed = 0;//选择添加菜单为从历史中修改新菜单的方式：1->上周拷贝到本周并修改 2->自定义

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    public static MenuTableDao menuTableDao;
    public static MenuDetailDao menuDetailDao;
    public static Long menu_index;
    public static RelativeLayout rl_menu;

    public PopupWindow pop_loading = null;
    public PopupWindow dialog_show_error = null;

    public AlertDialog dlLoadModule = null;

    public static String pathDataBase = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "data" + File.separator;
    public static String pathFonts = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "fonts" + File.separator;

    public static AlertDialog.Builder builderSetMenuIp;

    public static OnClickListener ocl = null;
    public static OnTouchListener otl = null;
    public static TextWatcher tw = null;
    public static TextView tv_now_choosen_index = null;
    public static TextView tv_preview_font_size = null;
    public static SeekBar seekbar_font_size = null;
    public static TextView tv_detail_font_size = null;
    public static Button button_font_size_smallest = null;
    public static Button button_font_size_small = null;
    public static Button button_font_size_medium = null;
    public static Button button_font_size_big = null;
    public static Button button_font_size_biggest = null;

    public static int font_smallest_size = 15;
    public static int font_small_size = 28;
    public static int font_medium_size = 35;
    public static int font_big_size = 45;
    public static int font_biggest_size = 55;

    public static List<MealBucket> list_meal_bucket = null;// 营业时间段的数据列表

    public static List<ChargItem> list_charge_item = null;// 收费项目列表


    public int width_screen;
    public int height_screen;

    public double x_fraction;
    public double y_fraction;


    private int _xDelta;
    private int _yDelta;

    public static CustomScrollView sv = null;
    private Handler mHandler = new Handler();
    private int offset_x = 0;
    private int offset_y = 0;
    public String detail_item = null;

    public static Long menu_id_now_choose;//example：201412051
    public static int menu_type = 1;
    public static int now_timebucket_id = 0;//当前的营业时间段

    public static Handler handler;
    public static final int UPDATE_MENU_ID = 0;
    public static final int UPDATE_TEXT_CONTENT = 1;
    public static final int UPDATE_NOW_BAR_TIPS = 2;
    public static final int RELOAD_MOBILE_UI = 3;
    public static final int PRIEVIEW = 4;
    public static final int UPDATE_PRICE_LAYOUT = 5;
    public static final int UPDATE_X_Y = 6;
    public static final int UPDATE_PROGRESS_BAR = 7;
    public static final int UPDATE_MENU_MOBILE_DEFAULT = 8;
    public static final int UPDATE_MENU_MOBILE = 9;
    public static final int UPDATE_MATCH_PROGRESS = 10;
    public static final int UPDATE_MATCH_DATABASE = 11;
    public static final int SHOW_HANDLE_BIND_DIALOG = 12;
    public static final int SHOW_AUTO_MATCH_RESULT_DIALOG = 13;
    public static final int SHOW_MENU = 14;
    public static final int SHOW_DIALOG_LOCAL_NO_MENUS = 15;
    public static final int SHOW_BIND_DIALOG_AFTER_GET_MENU = 16;//在获取完当日菜单后，显示关联窗口
    public static final int HIDE_LOADING = 17;// 关闭窗口（加载进度）
    public static final int SHOW_LOADING_TEXT = 18;// 显示窗口（加载进度）
    public static final int SHOW_ERROR_MESSAGE = 19;
    public static final int UPDATE_MENU_DATA_BY_JSON = 20;//解析云端到本地的数据
    public static final int SHOW_TOAST = 21;

    private int pos = 0;
    int lastX = 0;
    int lastY = 0;
    int screenWidth;
    int screenHeight;

    private ActionMode mActionMode = null;
    final static int CONTEXT_MENU_1 = Menu.FIRST;
    final static int CONTEXT_MENU_2 = Menu.FIRST + 1;
    final static int CONTEXT_MENU_3 = Menu.FIRST + 2;
    final static int CONTEXT_MENU_4 = Menu.FIRST + 3;
    final static int CONTEXT_MENU_5 = Menu.FIRST + 4;
    final static int CONTEXT_MENU_6 = Menu.FIRST + 5;
    final static int CONTEXT_MENU_7 = Menu.FIRST + 6;
    final static int CONTEXT_MENU_8 = Menu.FIRST + 7;
    final static int CONTEXT_MENU_9 = Menu.FIRST + 8;
    final static int CONTEXT_MENU_10 = Menu.FIRST + 9;
    final static int CONTEXT_MENU_11 = Menu.FIRST + 10;

    public static Button button_new_choosen = null;

    public static String choosen_color = null;//在选择已经存在的配色方案时选择的颜色
    public static String now_new_content = null;//当前修改的文本
    public static int choose_font_size = 0;//当前修改的字体大小
    public static int modified_distance = 0;//当前修改的距离
    public static int modified_price = 0;//当前修改的价格
    public static int modified_widget_id = 0;//当前修改的控件id

    public static AlertDialog dlg = null;//在选择已经存在的配色方案的对话框
    public static AlertDialog dlg_font_size = null;//在选择已经存在的字体大小的对话框
    public static AlertDialog dlg_modift_content = null;//修改文字内容
    public static boolean flag_clear_tv_screen = true;

    public static int actionBarHeight = 0;
    public static int statusBarHeight = 1;
    public static int scrollBarOffset = 0;

    public static Boolean flag_whether_add = false;//在显示菜单的时候不更新内容
    public static Callback ac = null;

    String added_item_name = null;
    int added_price = 0;
    int added_padding = 0;
    Boolean flag_item_new_kind = false;
    String added_type = null;
    String added_additiontal = null;

    List listResultPrieview = null;
    Dialog dlg_prrgress_bar = null;
    RateTextCircularProgressBar mRateTextCircularProgressBar = null;
    long mExitTime = 0;
    int count = 0;
    long firstClick = 0;// 第一次点击的时间 long型    
    long lastClick = 0;// 最后一次点击的时间  
    boolean flag_move;//记录手指的按下、移动、抬起

    TextView bind_textview_bind_name_tips = null;
    TextView bind_textview_bind_already_name = null;
    String bind_choosen_type = null;//绑定到服务器的是套餐还是单品
    int bind_id = -1;//绑定到服务器的套餐id或者单品id

    private ListView sortListView;
    private SideBar sortSideBar;
    private TextView sortDialogChoosenTips;
    private SortAdapter sortAdapter;
    private ClearEditText sortClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    public static String[][] serverComboNameId = new String[1000][2];
    public static String[][] serverSingleNameId = new String[1000][2];

    private long menuIndexCopyTo = -1;
    private int noOfTimesCalled = 0;
    public Toast mtoast = null;

    public Thread thread_update_instant = null;

    //保存复制的东西
    public String copyText = null;
    public boolean copyNewFlag = false;
    public int copyPrice = -1;
    public int copyDistance = -1;
    public String copyType = null;
    public String copyAdditiontal = null;
    public String copyBindType = null;
    public int copyBindId = -1;

    //保存更新数据库的记录，用于撤销
    public static MenuDetail menuDetailEntityLastRecord = null;


    /**
     * contextMenu的创建
     */
    @Override
    /*menu：需要显示的快捷菜单
	v：V是用户选择的界面元素
	menuInfo：menuInfo是所选择界面元素的额外信息*/
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    //应用运行时，保持屏幕高亮，不锁屏
        button_new_choosen = (Button) v;

        modified_widget_id = button_new_choosen.getId();
        // CommonUtils.LogWuwei(tag, "___________________________可拖拽_____________");
        button_new_choosen.setOnTouchListener(this);


        //添加菜单项
        menu.setHeaderTitle(button_new_choosen.getText().toString());
        menu.add(0, CONTEXT_MENU_1, 1, "1、复制");
        menu.add(0, CONTEXT_MENU_2, 2, "2、粘贴");
        menu.add(0, CONTEXT_MENU_3, 3, "3、删除菜品");
        menu.add(0, CONTEXT_MENU_4, 4, "4、改变字体大小");
        menu.add(0, CONTEXT_MENU_5, 5, "5、改变字体");
        menu.add(0, CONTEXT_MENU_6, 6, "6、改变内容");
        menu.add(0, CONTEXT_MENU_7, 7, "7、改变距离");
        menu.add(0, CONTEXT_MENU_8, 8, "8、改变价格");
        menu.add(0, CONTEXT_MENU_9, 9, "9、绑定菜品");

        menu.add(0, CONTEXT_MENU_10, 10, "10、高亮开关");
        menu.add(0, CONTEXT_MENU_11, 11, "11、附加信息(加辣)开关");
        //menu.add(0, CONTEXT_MENU_11, 11, "11、删除该菜品");

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    /**
     * contextMenu菜单单击响应
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case CONTEXT_MENU_1:
                modify_copy();
                break;
            case CONTEXT_MENU_2:
                modify_paste();
                break;
            case CONTEXT_MENU_3:
                delete_item();
                break;
            case CONTEXT_MENU_4:
                Toast.makeText(this, "改变字体大小", Toast.LENGTH_LONG).show();
                modify_font_size();
                break;
            case CONTEXT_MENU_5:
                Toast.makeText(this, "改变字体", Toast.LENGTH_LONG).show();
                break;
            case CONTEXT_MENU_6:
                modify_content();
                break;
            case CONTEXT_MENU_7:
                modify_distance();
                break;
            case CONTEXT_MENU_8:
                modify_price();
                break;
            case CONTEXT_MENU_9://先获取菜单
                String nextWeek[] = CommonUtils.getNextWeekDate();
                String thisWeek[] = CommonUtils.getThisWeekDate();
                String date[] = new String[7];
                String tips = null;

                if (choice_last_or_customed == 0) {
                    menu_id_now_choose = Long.parseLong(thisWeek[pos] + menu_type);
                    date = thisWeek;
                } else if (choice_last_or_customed == 1) {
                    menu_id_now_choose = Long.parseLong(nextWeek[pos] + menu_type);
                    date = nextWeek;
                }
                //201412051
                String date_target = menu_id_now_choose / 100000 + "-" + menu_id_now_choose % 100000 / 1000 + "-" + menu_id_now_choose % 1000 / 10;
                ApisManager.getTvMenu(now_timebucket_id, date_target, 0, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", HIDE_LOADING, handler);
                        int index = 0;
                        com.alibaba.fastjson.JSONObject objResult = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject oj = objResult.getJSONObject("store_time_bucket_menu_display");
                        if (oj.toString().contains("unsorted_charge_items")) {
                            if (oj.getJSONArray("unsorted_charge_items") != null) {
                                com.alibaba.fastjson.JSONArray chargeItemList = oj.getJSONArray("unsorted_charge_items");
                                for (int k = 0; k < chargeItemList.size(); k++) {
                                    com.alibaba.fastjson.JSONObject objCI = chargeItemList.getJSONObject(k);
                                    ChargItem ci = new ChargItem();
                                    ci.charge_item_id = objCI.getLong("charge_item_id");
                                    ci.charge_item_name = objCI.getString("name");
                                    NewFromHistoryMenuActivity.list_charge_item.add(ci);
                                    NewFromHistoryMenuActivity.serverComboNameId[index][0] = ci.charge_item_name;
                                    NewFromHistoryMenuActivity.serverComboNameId[index][1] = Long.toString(ci.charge_item_id);
                                    index++;
                                }
                            }

                        }

                        if (oj.toString().contains("cats")) {
                            if (oj.getJSONArray("cats") != null) {
                                com.alibaba.fastjson.JSONArray chargeItemTypesList = oj.getJSONArray("cats");
                                for (int p = 0; p < chargeItemTypesList.size(); p++) {
                                    com.alibaba.fastjson.JSONArray chargeItemList = chargeItemTypesList.getJSONObject(p).getJSONArray("charge_items");
                                    for (int k = 0; k < chargeItemList.size(); k++) {
                                        com.alibaba.fastjson.JSONObject objCI = chargeItemList.getJSONObject(k);
                                        ChargItem ci = new ChargItem();
                                        ci.charge_item_id = objCI.getLong("charge_item_id");
                                        ci.charge_item_name = objCI.getString("name");
                                        NewFromHistoryMenuActivity.list_charge_item.add(ci);
                                        NewFromHistoryMenuActivity.serverComboNameId[index][0] = ci.charge_item_name;
                                        NewFromHistoryMenuActivity.serverComboNameId[index][1] = Long.toString(ci.charge_item_id);
                                        index++;
                                    }
                                }

                            }
                        }


                        CommonUtils.sendMsg("", NewFromHistoryMenuActivity.SHOW_BIND_DIALOG_AFTER_GET_MENU, handler);

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", HIDE_LOADING, handler);
                    }
                });
                break;
            case CONTEXT_MENU_10:
                modify_highlight();
                break;
            case CONTEXT_MENU_11:
                modify_addtiontal();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    /**************************************
     * activity基本生命周期
     ***********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.new_from_history_menu);

        init();

        widget_configure();

        getTimeBucketList();

        newMenu();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "onRestart");
        active = true;
        super.onStart();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "onPause");
        MobclickAgent.onPause(this);
        super.onPause();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "onResume");

        MobclickAgent.onResume(this);

        if (button_new_choosen != null) {
            InputMethodUtils.HideKeyboard(button_new_choosen);
        } else {
            CommonUtils.LogWuwei(tag, "the button now choosen is null");
        }
        super.onResume();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "onStop");
        active = false;
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "on destroy");
        daoMaster = null;
        daoSession = null;
        menuTableDao = null;
        menuDetailDao = null;
        super.onDestroy();
    }


    /**
     * 创建和配置tabs
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        context = getApplicationContext();
        getTimeBucketList();

        getMenuInflater().inflate(R.menu.client_new_from_history_action_bar_menu, menu);
        ActionBar actionBar = getActionBar();
        ColorDrawable dr = new ColorDrawable(Color.parseColor("#d54f30"));
        actionBar.setBackgroundDrawable(dr);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("返回");
        actionBar.setDisplayUseLogoEnabled(false);


        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        forceTabs();

        MenuItem statusSpinner = menu.findItem(R.id.menu_status_spinner);
        setupStatusSpinner(statusSpinner);

        MenuItem weekdaySpinner = menu.findItem(R.id.menu_weekday_spinner);
        setupWeekDaySpinner(weekdaySpinner);


        MenuItem tips = menu.findItem(R.id.item_client_new_from_history_show_tips);

        View view = tips.getActionView();
        if (view instanceof TextView) {
            tv_now_choosen_index = (TextView) view;
            tv_now_choosen_index.setTextColor(Color.WHITE);
            //tv_now_choosen_index.setEllipsize(TruncateAt.MARQUEE);
            tv_now_choosen_index.setText("显示标题中...                     ");
            CommonUtils.LogWuwei(tag, "________textview new success!");
        } else {
            CommonUtils.LogWuwei(tag, "_________textview new failed");
        }
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
        if (hasFocus) {
            actionBarHeight = CommonUtils.getActionBarHeigth(this);
            statusBarHeight = CommonUtils.getTitleBarHeight(this);
        }
    }


    /**************************************
     * tab配置
     ***********************************************/

    // This is where the magic happens!
    public void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                    .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, true);
        } catch (final Exception e) {
            // Handle issues as needed: log, warn user, fallback etc
            // This error is safe to ignore, standard tabs will appear.
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        forceTabs(); // Handle orientation changes.
    }


    /**
     * 选择早中晚餐类型
     *
     * @param item
     */
    private void setupStatusSpinner(MenuItem item) {
        View view = item.getActionView();
        if (view instanceof Spinner) {
            List<String> list = new ArrayList<String>();
            for (MealBucket mb : list_meal_bucket) {
                list.add(mb.name);
            }

            Spinner spinner = (Spinner) view;
            ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // TODO Auto-generated method stub

                    try {

                        menu_type = arg2;
                        now_timebucket_id = list_meal_bucket.get(arg2).time_bucket_id;

                        String nextWeek[] = CommonUtils.getNextWeekDate();
                        String thisWeek[] = CommonUtils.getThisWeekDate();
                        String date[] = new String[7];
                        String tips = null;

                        if (choice_last_or_customed == 0) {
                            menu_id_now_choose = Long.parseLong(thisWeek[pos] + menu_type);
                            date = thisWeek;
                            tips = "本周";
                        } else if (choice_last_or_customed == 1) {
                            menu_id_now_choose = Long.parseLong(nextWeek[pos] + menu_type);
                            date = nextWeek;
                            tips = "下周";
                        }

                        Message msg = new Message();
                        msg.what = UPDATE_MENU_MOBILE;
                        msg.obj = Long.toString(menu_id_now_choose / 10) + menu_type;

                        handler.sendMessage(msg);

                        String text = null;

                        String dayOfWeek = null;

                        switch (pos) {
                            case 0:
                                dayOfWeek = "周一";
                                break;
                            case 1:
                                dayOfWeek = "周二";
                                break;
                            case 2:
                                dayOfWeek = "周三";
                                break;
                            case 3:
                                dayOfWeek = "周四";
                                break;
                            case 4:
                                dayOfWeek = "周五";
                                break;
                            case 5:
                                dayOfWeek = "周六";
                                break;
                            case 6:
                                dayOfWeek = "周日";
                                break;
                            default:
                                dayOfWeek = "不确定";
                                break;
                        }

                        switch (menu_type) {
                            case 0:
                                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")早餐";
                                break;
                            case 1:
                                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")午餐";
                                break;
                            case 2:
                                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")晚餐";
                                break;
                        }
                        tv_now_choosen_index.setText(text + "                          ");
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }
    }


    /**
     * 选择周一、二、三、四、五、六、七
     *
     * @param item
     */
    private void setupWeekDaySpinner(MenuItem item) {
        View view = item.getActionView();
        if (view instanceof Spinner) {
            Spinner spinner = (Spinner) view;
            spinner.setAdapter(ArrayAdapter.createFromResource(this,
                    R.array.menu_weekday,
                    android.R.layout.simple_spinner_dropdown_item));

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    pos = arg2;
                    updateMenuIdNowChoose(arg2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }


    /**
     * 对创建的菜单经常switch的选择
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
		/*case R.id.item_client_new_from_history_set_menu_type:
			break;*/
	/*	case R.id.item_client_new_from_history_recovery:
			recovery();
			break;*/
            case R.id.item_client_new_from_history_add:
                add_menu_item(getApplicationContext());
                return true;
/*		case R.id.item_client_new_from_history_save:
			return true;
*/
            case R.id.item_client_new_from_history_preview:
                preview(menu_id_now_choose);
                //MsgUtils.SendSingleMsg(splash.handlerTools, "预览功能 敬请期待", HandlerUtils.SHOW_NORMAL_TOAST);*/
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_client_new_from_history_set_ip:
                menuSetting();
                break;
            case R.id.item_client_new_from_history_delete:
                deleteMenu(context, menuDetailDao, rl_menu, tv_now_choosen_index.getText().toString());
                break;
            case R.id.item_client_new_from_history_copy:
                copyMenu();
                break;
            case R.id.item_client_new_from_history_auto_match:
                new Thread() {
                    public void run() {
                        try {
                            auto_match(menu_id_now_choose);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.item_get_menu_from_clound:
                askUpdateMenu();
                break;
		/*case R.id.item_client_new_from_history_check_match_result://显示关联结果
			showMatchResultDialog();
			break;*/
        }
        return false;
    }


    /**************************************init and widgetConfigure***********************************************/
    /**
     * 初始化工作
     */
    private void init() {
        CommonUtils.getThisWeekDate();
        CommonUtils.getNextWeekDate();
        MainApplication.setmActivity(this);
        //mtoast = new Toast(getApplicationContext());
        context = getApplicationContext();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_MENU_ID:
                        updateMenuIdNowChoose(pos);
                        break;
                    case UPDATE_TEXT_CONTENT:
                        String widget_id_str = msg.getData().getString("widget_id");//id
                        String content = msg.getData().getString("str");//文本内容

                        int x = msg.getData().getInt("x");//x
                        int y = msg.getData().getInt("y");//y
                        int price = msg.getData().getInt("price");//价格
                        if (menuDetailDao != null) {
                            updateLocalDataBase(widget_id_str, content, x, y, price, -1, "", "", -1, "", -1, "");
                        }

                        break;
                    case UPDATE_NOW_BAR_TIPS:
                        tv_now_choosen_index.setText((String) msg.obj + "                          ");
                        break;
                    case RELOAD_MOBILE_UI:
                        CommonUtils.LogWuwei(tag, "in Handler RELOAD_MOBILE_UI");
                        show_menu_mobile(menu_id_now_choose);
                        break;
                    case PRIEVIEW:
                        preview(menu_id_now_choose);
                        break;
                    case UPDATE_PRICE_LAYOUT:
                        QueryBuilder qb = menuDetailDao.queryBuilder();
                        Long tmp_id = menu_id_now_choose + 20000;

                        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
                        List listResult = qb.list();

                        Button button_modified = (Button) findViewById(modified_widget_id);

                        for (int i = 0; i < listResult.size(); i++) {

                            //	CommonUtils.LogWuwei(tag, "数据库匹配中....");

                            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);

                            if (menu_detail_entity.getWidgetId() == modified_widget_id) {
                                //		CommonUtils.LogWuwei(tag, "数据库中匹配到数据"+"type is "+menu_detail_entity.getType()+" price is "+menu_detail_entity.getPrice());
                                if (menu_detail_entity.getType().equals("text") && menu_detail_entity.getPrice() != 888 && menu_detail_entity.getPrice() != 0) {
                                    Button button = null;
                                    button = (Button) findViewById(modified_widget_id + 1000);

                                    if (button != null) {
                                        double distance = menu_detail_entity.getDistance();
                                        if (distance > 0) {
                                            button.setX((float) (button_modified.getX() + distance * 1.5));
                                            button.setY((float) (button_modified.getY()));
                                        }
                                    }


                                }
                                break;
                            }
                        }
                        break;
                    case UPDATE_X_Y:
                        int x_1 = msg.getData().getInt("x");
                        int y_1 = msg.getData().getInt("y");
                        if (x_1 == 0 && y_1 == 0) {
                            return;
                        }
                        CommonUtils.LogWuwei(tag, "handler msg x is " + x_1 + " y_1 is " + y_1 + " id is " + modified_widget_id);
                        CommonUtils.LogWuwei(tag, " menu_id_now_choose " + menu_id_now_choose);
                        if (Long.toString(menu_id_now_choose).length() == 8) {
                            menu_id_now_choose = menu_id_now_choose * 10 + menu_type;
                        }
                        updateLocalDataBase(Integer.toString(modified_widget_id), "", x_1, y_1, -1, -1, "", "", -1, "", -1, "");
                        show_menu_mobile(menu_id_now_choose);
                        break;
                    case UPDATE_PROGRESS_BAR:
                        String str_progress = (String) msg.obj;
                        int progress = Integer.parseInt(str_progress);
                        mRateTextCircularProgressBar.setProgress(progress * 100 / 16);
                        if (progress == 16) {
                            dlg_prrgress_bar.cancel();
                            //dlLoadModule.cancel();
                        }
                        break;
                    case UPDATE_MENU_MOBILE_DEFAULT:
                        show_menu_mobile(menu_id_now_choose * 10 + 0);
                        CommonUtils.LogWuwei(tag, "显示周一的菜单完毕");
                        break;
                    case UPDATE_MENU_MOBILE:
                        String str = (String) msg.obj;
                        CommonUtils.LogWuwei(tag, "menu_type update str is " + str);
                        if (str != null && !str.equals("") && !str.equals(" ")) {
                            Long date = Long.parseLong((String) msg.obj);
                            CommonUtils.LogWuwei(tag, "menu_type update :date is " + date);
                            show_menu_mobile(date);
                        }
                        break;
                    case UPDATE_MATCH_PROGRESS:
                        Bundle B = msg.getData();
                        int match_progress = B.getInt("progress");
                        int max = B.getInt("max");
                        String title = B.getString("title");
                        Boolean flag_show_progress = B.getBoolean("flag_show_progress");

                        if (match_progress != 0 && flag_show_progress) {
                            title = title + match_progress * 100 / max + "%";
                            CommonUtils.LogWuwei("god", "title is " + title);
                        }
					
				/*if(mtoast!=null)
		          {
					mtoast.setText(title);
		          }
		          else
		          {
		        	  	mtoast=Toast.makeText(getApplicationContext(),title, Toast.LENGTH_SHORT);
		          } 
		          mtoast.show(); //显示toast信息
*/
                        break;

                    case UPDATE_MATCH_DATABASE:
                        //删除同一日期内widget_id相同的行
                        MenuDetail menu_detail_entity = (MenuDetail) msg.obj;
                        QueryBuilder qb_1 = menuDetailDao.queryBuilder();
                        DeleteQuery<MenuDetail> dq =
                                qb_1.where(MenuDetailDao.Properties.MenuDateId.eq(menu_detail_entity.getMenuDateId()),
                                        MenuDetailDao.Properties.WidgetId.eq(Integer.toString(menu_detail_entity.getWidgetId())))
                                        .buildDelete();

                        dq.executeDeleteWithoutDetachingEntities();

                        menuDetailDao.insertOrReplace(menu_detail_entity);
                        break;

                    case SHOW_HANDLE_BIND_DIALOG:
                        dealMatchResult(msg.obj);
                        break;
                    case SHOW_MENU:
                        if (msg.obj != null) {
                            long menu_index = Long.parseLong((String) msg.obj);
                            show_menu_mobile(menu_index);
                        }
                        break;
                    case SHOW_DIALOG_LOCAL_NO_MENUS:
                        showDialogNoRecentMenu(Integer.parseInt((String) msg.obj));
                        break;
                    case SHOW_BIND_DIALOG_AFTER_GET_MENU:
                        added_type = "text";
                        added_item_name = button_new_choosen.getText().toString();
                        showBindInfo(added_item_name, null, button_new_choosen.getId(), false);
                        break;
                    case SHOW_LOADING_TEXT:
                        content = (String) msg.obj;
                        CommonUtils.LogWuwei("time1", content + " received");
                        showLoadingDialog(content);
                        break;
                    case HIDE_LOADING:
                        hideLoadingDialog();
                        break;
                    case SHOW_ERROR_MESSAGE:
                        String errMsg = (String) msg.obj;
                        showDialogError(errMsg, 0);
                        break;
                    case UPDATE_MENU_DATA_BY_JSON:
                        Map map = (Map) msg.obj;
                        String dayMenuDisplayJson = (String) map.get("content");
                        int nowWeekDay = (Integer) map.get("nowWeekDay");
                        updateMenuDataByJson(dayMenuDisplayJson, nowWeekDay);
                        break;
                    case SHOW_TOAST:
                        HandlerUtils.showToast(context, (String) msg.obj);
                        break;
                }
            }
        };
        ip_address = LocalDataDeal.readFromLocalMenuIp(context);
        builderSetMenuIp = new AlertDialog.Builder(this);//新建一个Alertdialog的builder

        daoMaster = CommonUtils.getDaoMaster(getApplicationContext(), daoMaster);
        daoSession = CommonUtils.getDaoSession(getApplicationContext(), daoSession, daoMaster);

        menuTableDao = daoSession.getMenuTableDao();
        menuDetailDao = daoSession.getMenuDetailDao();

        rl_menu = (RelativeLayout) findViewById(R.id.relativeLayout_new_from_history_menu);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        sv = (CustomScrollView) findViewById(R.id.scrollview_new_from_history_menu);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;

        width_screen = CommonUtils.getScreenWidth(context);
        height_screen = CommonUtils.getScreenHeight(context);
		
		/*x_fraction = 1.5*(width_screen/1920);
		y_fraction = 1.533*(height_screen/1104);*/
		
		/*x_fraction = 1.6*(width_screen/1920);
		y_fraction = 2.133*(height_screen/1104);*/


        ShowMenuActivity.width_screen = CommonUtils.getScreenWidth(context);
        ShowMenuActivity.height_screen = CommonUtils.getScreenHeight(context);

        ShowMenuActivity.x_fraction = 1.5 * (ShowMenuActivity.width_screen / 1920);
        ShowMenuActivity.y_fraction = 1.533 * (ShowMenuActivity.height_screen / 1104);


        x_fraction = ShowMenuActivity.x_fraction;
        y_fraction = ShowMenuActivity.y_fraction;


        ac = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //showPopWindow();
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        };


        //翩翩体
        ShowMenuActivity.fontFacePianPian = Typeface.createFromFile(pathFonts + "pianpina.ttf");
        //黑板字
        ShowMenuActivity.fontFaceChalkBoard = Typeface.createFromFile(pathFonts + "Chalkboard.ttf");

        //画竖线用的字体
        ShowMenuActivity.fontFaceVerticalLine = Typeface.createFromFile(pathFonts + "Chalkduster.ttf");

        ShowMenuActivity.logoButton = new Button(this);
        ShowMenuActivity.logoButton.setBackgroundResource(R.drawable.wuwei);

        ShowMenuActivity.font_fraction = 1;// density/1.33;

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();


        for (int i = 0; i < NewFromHistoryMenuActivity.serverComboNameId.length; i++) {
            serverComboNameId[i][0] = null;
            serverComboNameId[i][1] = null;
        }

        for (int i = 0; i < NewFromHistoryMenuActivity.serverSingleNameId.length; i++) {
            serverSingleNameId[i][0] = null;
            serverSingleNameId[i][1] = null;
        }

        getTimeBucketList();
    }


    /**
     * 一些配置
     */
    public void widget_configure() {
        ocl = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.LogWuwei(tag, "______editText is clicked___________");
                button_new_choosen = (Button) v;
                modified_widget_id = button_new_choosen.getId();
                CommonUtils.LogWuwei(tag, "text is " + button_new_choosen.getText().toString());
            }
        };

        otl = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        offset_x = (int) event.getX();
                        offset_y = (int) event.getY();
                        break;
                    default:
                        break;
                }

                return false;
            }
        };

        ShowMenuActivity.handlerShowMenu = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case ShowMenuActivity.REGISTER_FOR_CONTEXT_MENU:
                        View v = (View) msg.obj;
                        registerForContextMenu(v);
                        //CommonUtils.LogWuwei(tag, "可拖拽，可弹出菜单");
                        v.setOnTouchListener(NewFromHistoryMenuActivity.this);
                        break;
                }
            }
        };

        sv.setScrollingEnabled(true);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rl_menu.getLayoutParams();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        statusBarHeight = CommonUtils.getTitleBarHeight(this);
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }


        CommonUtils.LogWuwei(tag, "actionBarHeight is " + actionBarHeight + " statusBarHeight is " + statusBarHeight);

        lp.width = CommonUtils.getScreenWidth(context);
        lp.height = CommonUtils.getScreenHeight(context) - actionBarHeight - statusBarHeight;

    }


    /**
     * 当最近两个月本地都没有存储菜单时，弹窗选择是创建空白菜单还是从模板加载一周的菜单
     */
    public void showDialogNoRecentMenu(final int flag) {

        CommonUtils.LogWuwei(tag, "准备显示对话窗，让用户选择是从模板加载还是创建空白菜单");


        final AlertDialog.Builder builder = new Builder(NewFromHistoryMenuActivity.this);


        builder.setMessage("检测到本地没有存储菜单，请选择:\n" +
                "1、从模板加载\n" +
                "2、创建空白菜单");
        builder.setTitle("提示");

        builder.setPositiveButton("模板加载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CommonUtils.LogWuwei(tag, "准备显示进度窗口");
                dialog_progress_bar_show();
                CommonUtils.LogWuwei(tag, "从模板加载中");
                MenuOperate.loadModule(flag, handler);

            }
        });
        builder.setNegativeButton("空白菜单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MenuOperate.createNullMenu(flag, menuDetailDao);//创建本周的空白菜单
            }
        });

        dlLoadModule = builder.create();
        dlLoadModule.show();
    }


    public void dialog_progress_bar_show() {
        try {
            LayoutInflater factory = LayoutInflater.from(getApplicationContext());

            final View DialogView = factory.inflate(R.layout.circular_progress_bar, null);

            dlg_prrgress_bar = new Dialog(NewFromHistoryMenuActivity.this);
            dlg_prrgress_bar.setContentView(DialogView);
            dlg_prrgress_bar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dlg_prrgress_bar.setCanceledOnTouchOutside(false);
            dlg_prrgress_bar.setTitle("正在从模板加载数据...");
            dlg_prrgress_bar.show();
            dlg_prrgress_bar.setCancelable(false);

            mRateTextCircularProgressBar = (RateTextCircularProgressBar) dlg_prrgress_bar.findViewById(R.id.client_menu_update_progress_bar);
            mRateTextCircularProgressBar.setMax(100);
            mRateTextCircularProgressBar.setTextColor(Color.WHITE);
            mRateTextCircularProgressBar.setProgress(0);
            mRateTextCircularProgressBar.getCircularProgressBar().setCircleWidth(20);
        } catch (Exception e) {

        }

    }


    /**
     * 显示菜单
     * 点击tab时，显示对应菜单，同时在数据库中创造临时数据供修改和保存，如果临时数据已经存在
     * 如果有临时数据，则显示临时数据构成的菜单
     */
    public void show_menu_mobile(Long menu_index) {
        CommonUtils.LogWuwei(tag, "show_menu is called:menu_index is " + menu_index);

        Boolean flag = false;//flag->true,display tmp menu

        //判断一下是否存在临时数据，如果有临时数据的话，只需要显示临时数据就行
        QueryBuilder qb_tmp = menuDetailDao.queryBuilder();
        qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_index + 20000)));
        List listResultTmp = qb_tmp.list();
        if (listResultTmp.size() > 0) {
            flag = true;
        }

        //查看对应的menuTable表是否存在
        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_index)));
        List listResult = qb.list();


        if (listResult.size() == 0 && listResultTmp.size() == 0) {
            rl_menu.removeAllViews();
            return;
        } else {
            rl_menu.removeAllViews();
            List list;
            CommonUtils.LogWuwei(tag, "menu_index+20000 is " + (menu_index + 20000));
            CommonUtils.LogWuwei(tag, "menu_index is " + menu_index);
            if (flag) {
                CommonUtils.LogWuwei(tag, "存在临时数据，会显示临时菜单");
                list = listResultTmp;
            } else {
                CommonUtils.LogWuwei(tag, "不存在临时数据，显示默认菜单");
                list = listResult;
            }
            flag_whether_add = false;
            for (int i = 0; i < list.size(); i++) {
                MenuDetail menu_detail_entity = (MenuDetail) list.get(i);

                String menu_id_str = menu_detail_entity.getMenuDateId();
                String date_id = "";
                String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
                String type = menu_detail_entity.getType();
                String name = menu_detail_entity.getName();
                String x = Double.toString(menu_detail_entity.getX());
                String y = Double.toString(menu_detail_entity.getY());
                String price = Integer.toString(menu_detail_entity.getPrice());
                String font_size = Integer.toString(menu_detail_entity.getFontSize());
                String font_color = menu_detail_entity.getFontColor();
                String background_color = menu_detail_entity.getBackgroundColor();
                double distance = menu_detail_entity.getDistance();
                String additiontal = menu_detail_entity.getAddtiontal();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("date_id", menu_id_str);//0
                bundle.putString("menu_id", date_id);//1
                bundle.putString("widget_id", widget_id);//2
                bundle.putString("type", type);//3
                bundle.putString("name", name);//4
                bundle.putString("x", x);  //5
                bundle.putString("y", y);  //6   
                bundle.putString("price", price);  //7  
                bundle.putString("font_size", font_size);//8
                bundle.putString("font_color", font_color);  //9  
                bundle.putString("background_color", background_color); //10
                bundle.putString("distance", Double.toString(distance));
                bundle.putString("additiontal", additiontal);

                msg.setData(bundle);//msg利用Bundle传递数据
                ShowMenuActivity.paintTv(msg, rl_menu, false, context);//调用showMenu的paintTv接口，显示在手机上，让用户修改

                if (!flag) {
                    createTmpData(menu_detail_entity);//存放临时数据	
                }


            }
            flag_whether_add = true;
        }


    }


    /**
     * 当前在修改的菜单预览
     *
     * @param menu_index
     */
    public void preview(Long menu_index) {

        boolean flag_show_tmp = false;


        //查看对应的menuTable表是否存在
        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_index + 20000)));
        final List listResultTmp = qbTmp.list();


        if (listResultTmp.size() > 0) {
            flag_show_tmp = true;
        }


        //查看对应的menuTable表是否存在
        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_index)));
        final List listResultNormal = qb.list();
        flag_clear_tv_screen = true;

        if (flag_show_tmp) {
            listResultPrieview = listResultTmp;
        } else {
            listResultPrieview = listResultNormal;
        }

        if (listResultPrieview.size() == 0) {
            return;
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            dm = getResources().getDisplayMetrics();
            float density = dm.density;
            new Thread() {
                public void run() {

                    for (int i = 0; i < listResultPrieview.size(); i++) {

                        MenuDetail menu_detail_entity = (MenuDetail) listResultPrieview.get(i);

                        String menu_id_str = menu_detail_entity.getMenuDateId();
                        String date_id = "";
                        String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
                        String type = menu_detail_entity.getType();
                        String name = menu_detail_entity.getName();
                        String x = Double.toString(menu_detail_entity.getX());
                        String y = Double.toString(menu_detail_entity.getY());
                        String price = Integer.toString(menu_detail_entity.getPrice());
                        String font_size = Integer.toString(menu_detail_entity.getFontSize());
                        String font_color = menu_detail_entity.getFontColor();
                        String background_color = menu_detail_entity.getBackgroundColor();
                        double distance = menu_detail_entity.getDistance();
                        String additiontal = menu_detail_entity.getAddtiontal();
                        String bindType = menu_detail_entity.getRedundance1();
                        int bindServerItemId = menu_detail_entity.getBindToItemServerId();

                        //CommonUtils.LogWuwei(tag, "_______________name is " + name + "menu_id_str is " + menu_id_str + "type is " + type + "additiontal is " + additiontal);
	    			    			
	    			    			
	    			    			/*if(name.contains("-") || name.contains("|"))
	    			    			{
	    			    				name  = name;	
	    			    			}
	    			    			else
	    			    			{
	    			    				name = URLEncoder.encode(name);	
	    			    			}*/
                        if (name != null) {
                            CommonUtils.LogWuwei(tag, " before urlEncoder name is " + name);
                            name = URLEncoder.encode(name);
                            CommonUtils.LogWuwei(tag, "after urlEncoder name is " + name);
                        }


                        if (!background_color.equals("0x0000") || !background_color.equals("0x000")) {
                            background_color = "0x0000";
                        }


                        detail_item = "date_id=" + menu_id_str + "&menu_id=" + date_id + "&widget_id=" +
                                widget_id + "&type=" + type + "&name=" + name + "&x=" + x + "&y=" + y + "&price=" + price +
                                "&font_size=" + font_size + "&font_color=" + font_color + "&background_color=" + background_color +
                                "&distance=" + Double.toString(distance) +
                                "&additiontal=" + additiontal +
                                "&redundance1=" + bindType +
                                "&BindToItemServerId=" + Integer.toString(bindServerItemId);


	    			    			

	    			        		/*try
	    			        		{
	    			        			Thread.sleep(10);
	    						}
	    			        		catch (InterruptedException e1) 
	    			        		{
	    							e1.printStackTrace();
	    						}*/

//	    					MsgUtils.SendSingleMsg(splash.handlerTools,splash.ip_address,HandlerUtils.SHOW_NORMAL_TOAST);


                        if (detail_item == null || detail_item.equals("")) {
                            return;
                        }
                        String uriAPI = "http://" + ip_address + ":8081/?" + detail_item; //
                        CommonUtils.LogWuwei(tag, "uriAPI is "+uriAPI);
	    			       
	    					/*建立HTTPost对象*/
                        HttpPost httpRequest = new HttpPost(uriAPI); 
	    			      
	    			        /*
	    			         * NameValuePair实现请求参数的封装
	    			        */
                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        if (i == 0 && flag_clear_tv_screen) {
                            params.add(new BasicNameValuePair("preview", "1"));
                            flag_clear_tv_screen = false;
                            i = -1;


                        } else {
                            params.add(new BasicNameValuePair("preview", "0"));
                        }


                        try { 	  
		    			          /* 添加请求参数到请求对象*/
                            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
		    			          /*发送请求并等待响应*/
                            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); 
		    			          /*若状态码为200 ok*/
                            if (httpResponse.getStatusLine().getStatusCode() == 200) { 
		    			            /*读返回数据*/
                                CommonUtils.LogWuwei(tag, "post success");
                                String strResult = EntityUtils.toString(httpResponse.getEntity());
                                // MsgUtils.SendSingleMsg(splash.handlerTools, "post success", HandlerUtils.SHOW_NORMAL_TOAST);
                            } else {
                                CommonUtils.LogWuwei(tag, "post failure " + httpResponse.getStatusLine().getStatusCode());
                                //HandlerUtils.showToast(context,

                                CommonUtils.sendMsg("Error Response: " + httpResponse.getStatusLine().toString(), SHOW_TOAST, handler);
                            }
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                            CommonUtils.sendMsg("ClientProtocolException" + e.getMessage(), SHOW_TOAST, handler);
                            CommonUtils.LogWuwei(tag, "ClientProtocolException is " + e.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                            CommonUtils.LogWuwei(tag, "IOException is " + e.getMessage());
                            CommonUtils.sendMsg("IOException" + e.getMessage(), SHOW_TOAST, handler);
                            //HandlerUtils.showToast(context,  "IOException : "+e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtils.LogWuwei(tag, "Exception is " + e.getMessage());
                            CommonUtils.sendMsg(e.getMessage(), SHOW_TOAST, handler);
                            //HandlerUtils.showToast(context,  "Exception : "+e.getMessage());
                        }
                        detail_item = "";
                    }
                }
            }.start();

        }
    }


    /**
     * 新建菜单
     */
    public void newMenu() {

        Intent intent = getIntent();
        String str_flag = intent.getStringExtra("this_next_week");
        if (str_flag != null) {
            if (CommonUtils.isNumeric(str_flag)) {
                choice_last_or_customed = Integer.parseInt(str_flag);
            }
        }

        if (choice_last_or_customed == 0)//创建本周菜单
        {
            CommonUtils.LogWuwei(tag, "准备创建本周菜单");
            //检查本周菜单是否存在
            boolean flag_this_week = MenuOperate.checkMenuThisWeek(menuDetailDao, handler, menu_type);

            //对数据进行处理
            MenuOperate.dataDealThisWeek(context, menuDetailDao, handler, flag_this_week);

        } else if (choice_last_or_customed == 1)//创建下周菜单
        {
            CommonUtils.LogWuwei(tag, "创建下周菜单");

            //检查下周菜单是否存在
            boolean flag_next_week = MenuOperate.checkMenuNextWeek(menuDetailDao, handler, menu_type);

            //对数据进行处理
            MenuOperate.dataDealNextWeek(context, menuDetailDao, handler, flag_next_week);

        }
    }


    /**
     * 将从服务器获取到的json数据写入电视本地
     */
    public void updateMenuDataByJson(String dayMenuDisplayJson, int nowWeekDay) {
        JSONArray test_object = null;

        try {

            test_object = new JSONArray(dayMenuDisplayJson); //JSONObject(dayMenuDisplayJson).getJSONArray("daymenudisplays");
            for (int i = 0; i < test_object.length(); i++) {
                JSONObject testItem = (JSONObject) test_object.get(i);

                {
                    JSONObject jsonobj = testItem;// paramArray.getJSONObject(k);
                    String menu_id_str = jsonobj.getString("menu_id_str");
                    String date_id = jsonobj.getString("date_id");//1-->date_id是包含菜单类型的，举例201411210
                    String widget_id = jsonobj.getString("widget_id");//2
                    String type = jsonobj.getString("type");//3

                    String name = jsonobj.getString("name");//4
                    name = name.replace("\\/", "/");
                    //CommonUtils.LogWuwei(tag, "name is "+name);

                    String x = jsonobj.getString("x");//5
                    String y = jsonobj.getString("y");//6
                    String price = jsonobj.getString("price");//7
                    String font_size = jsonobj.getString("font_size");//8
                    String font_color = jsonobj.getString("font_color");//9
                    String background_color = jsonobj.getString("background_color");//10
                    String distance = jsonobj.getString("distance");
                    String additiontal = jsonobj.getString("additiontal");
                    String bindType = jsonobj.getString("bindType");
                    String bindToItemServerId = jsonobj.getString("bindServerItemId");
                    String redundance1 = jsonobj.getString("redundance1");
                    String redundance2 = jsonobj.getString("redundance2");
                    String redundance3 = jsonobj.getString("redundance3");
                    String redundance4 = jsonobj.getString("redundance4");
                    String redundance5 = jsonobj.getString("redundance5");

                    if (i == 0) {

                        Long date = Long.parseLong(menu_id_str);
                        Long dateTmp = Long.parseLong(menu_id_str) + 20000;

                        QueryBuilder qb = menuDetailDao.queryBuilder();
                        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)));
                        List listResult = qb.list();

                        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
                        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)));
                        List listResultTmp = qbTmp.list();


                        DeleteQuery<MenuDetail> dq =
                                qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
                                        MenuDetailDao.Properties.WidgetId.ge(0))
                                        .buildDelete();

                        dq.executeDeleteWithoutDetachingEntities();

                        DeleteQuery<MenuDetail> dqTmp =
                                qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
                                        MenuDetailDao.Properties.WidgetId.ge(0))
                                        .buildDelete();

                        dqTmp.executeDeleteWithoutDetachingEntities();

                    }

                    date_id = "0";

                    if (bindToItemServerId.equals("")) {
                        bindToItemServerId = "0";
                    }

                    double distance_double = 0;
                    if (distance != null && !distance.equals("")) {
                        distance_double = Double.parseDouble(distance);
                    }

                    MenuDetail menu_detail_entity = new MenuDetail(
                            menu_id_str,
                            Long.parseLong(date_id),
                            Integer.parseInt(widget_id),
                            type,
                            name,
                            Double.parseDouble(x),
                            Double.parseDouble(y),
                            Integer.parseInt(price),
                            Integer.parseInt(font_size),
                            font_color,
                            background_color,
                            distance_double,
                            additiontal,
                            Integer.parseInt(bindToItemServerId),
                            redundance1,
                            redundance2,
                            redundance3,
                            redundance4,
                            redundance5
                    );
                    menuDetailDao.insertOrReplace(menu_detail_entity);
                }


                //String testParams = testItem.getString("parameters");
                //CommonUtils.LogWuwei(tag, "\n|\n|\n|testParams is "+testParams);
            }


            try {
                if (nowWeekDay == 6) {
                    CommonUtils.sendMsg(null, HIDE_LOADING, handler);
                }
            } catch (Exception e) {
                HandlerUtils.showToast(context, "同步菜单异常:" + e.getMessage());
            }
        } catch (Exception e) {
            HandlerUtils.showToast(context, "同步菜单异常:" + e.getMessage());
        }
    }


    /**
     * 删除当前菜单
     */
    public void deleteMenu(final Context ctxt, final MenuDetailDao menuDetailDao, final RelativeLayout rl_menu, String txt) {

        CommonUtils.LogWuwei(tag, "删除菜单中。。。。");
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(NewFromHistoryMenuActivity.this);
        Effectstype effect;
        effect = Effectstype.Shake;

        //txt = tv_now_choosen_index.getText().toString(); 
        txt = txt.substring(8, 24);

        dialogBuilder
                .withTitle("删除菜单")
                        // .withTitle(null) no title
                .withTitleColor("#FFFFFF")
                        // def
                .withDividerColor("#11000000")
                        // def
                .withMessageColor("#FFFFFF")
                        // def
                .withMessage(
                        "\n1、删除当前菜单：\n\t\t你将删除\"" + txt + "\"的菜单\n2、更多：\n"
                                + "\t\t可以删除本周菜单或者下周菜单\n")
                .isCancelableOnTouchOutside(false)
                        // def | isCancelable(true)
                .withDuration(300)
                        // def
                .withEffect(effect)
                        // def Effectstype.Slidetop
                .withButton1Text("删除当前菜单").withButton2Text("更多")
                .setButton1Click(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Long date_tmp = NewFromHistoryMenuActivity.menu_id_now_choose;
                        QueryBuilder qb = menuDetailDao.queryBuilder();
                        DeleteQuery<MenuDetail> dq = qb.where(
                                MenuDetailDao.Properties.MenuDateId.eq(Long
                                        .toString(date_tmp)),
                                MenuDetailDao.Properties.WidgetId.ge("0"))
                                .buildDelete();
                        dq.executeDeleteWithoutDetachingEntities();

                        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
                        DeleteQuery<MenuDetail> dqTmp = qbTmp.where(
                                MenuDetailDao.Properties.MenuDateId.eq(Long
                                        .toString(date_tmp + 20000)),
                                MenuDetailDao.Properties.WidgetId.ge("0"))
                                .buildDelete();
                        dqTmp.executeDeleteWithoutDetachingEntities();

                        HandlerUtils.showToast(ctxt, "当前菜单删除成功");
                        rl_menu.removeAllViews();
                        dialogBuilder.dismiss();
                        // dialogBuilder.deleteInstance();
                    }
                }).setButton2Click(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dialogBuilder.dismiss();
                deleteWeekMenu(ctxt, menuDetailDao, rl_menu);
                // dialogBuilder.deleteInstance();
            }
        }).show();

    }

    /**
     * 删除周菜单
     */
    public void deleteWeekMenu(final Context ctxt, final MenuDetailDao menuDetailDao, final RelativeLayout rl_menu) {

        CommonUtils.LogWuwei(tag, "删除菜单中。。。。");
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(NewFromHistoryMenuActivity.this);
        Effectstype effect;
        effect = Effectstype.Shake;

        dialogBuilder.withTitle("删除菜单")
                // .withTitle(null) no title
                .withTitleColor("#FFFFFF")
                        // def
                .withDividerColor("#11000000")
                        // def
                .withMessageColor("#FFFFFF")
                        // def
                .withMessage("\n\n选择删除本周菜单还是下周菜单\n\n")
                .isCancelableOnTouchOutside(false) // def | isCancelable(true)
                .withDuration(300) // def
                .withEffect(effect) // def Effectstype.Slidetop
                .withButton1Text("删除本周") // def gone
                .withButton2Text("删除下周").setButton1Click(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogBuilder.dismiss();
                String thisWeek[] = CommonUtils.getThisWeekDate();
                for (int i = 0; i < 7; i++) {
                    for (int k = 0; k < 3; k++) {
                        Long date_tmp = Long.parseLong(thisWeek[i])
                                * 10 + k;
                        QueryBuilder qb = menuDetailDao.queryBuilder();

                        DeleteQuery<MenuDetail> dq = qb.where(
                                MenuDetailDao.Properties.MenuDateId
                                        .eq(Long.toString(date_tmp)),
                                MenuDetailDao.Properties.WidgetId
                                        .ge("0")).buildDelete();
                        CommonUtils.LogWuwei(tag, "delete tmp date "
                                + (date_tmp));
                        dq.executeDeleteWithoutDetachingEntities();

                        QueryBuilder qb_tmp = menuDetailDao
                                .queryBuilder();
                        DeleteQuery<MenuDetail> dq_tmp = qb_tmp
                                .where(MenuDetailDao.Properties.MenuDateId
                                                .eq(Long.toString(date_tmp + 20000)),
                                        MenuDetailDao.Properties.WidgetId
                                                .ge("0")).buildDelete();
                        CommonUtils.LogWuwei(tag, "delete tmp date "
                                + (date_tmp + 20000));
                        dq_tmp.executeDeleteWithoutDetachingEntities();

                        rl_menu.removeAllViews();
                    }
                }
                HandlerUtils.showToast(ctxt, "本周菜单删除成功");
                dialogBuilder.deleteInstance();
            }
        }).setButton2Click(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogBuilder.dismiss();
                String nextWeek[] = CommonUtils.getNextWeekDate();
                for (int i = 0; i < 7; i++) {
                    for (int k = 0; k < 3; k++) {
                        Long date_tmp = Long.parseLong(nextWeek[i])
                                * 10 + k;// 201412190
                        QueryBuilder qb = menuDetailDao.queryBuilder();
                        DeleteQuery<MenuDetail> dq = qb.where(
                                MenuDetailDao.Properties.MenuDateId
                                        .eq(Long.toString(date_tmp)),
                                MenuDetailDao.Properties.WidgetId
                                        .ge("0")).buildDelete();
                        CommonUtils.LogWuwei(tag, "delete tmp date "
                                + (date_tmp));
                        dq.executeDeleteWithoutDetachingEntities();

                        QueryBuilder qb_tmp = menuDetailDao
                                .queryBuilder();
                        DeleteQuery<MenuDetail> dq_tmp = qb_tmp
                                .where(MenuDetailDao.Properties.MenuDateId
                                                .eq(Long.toString(date_tmp + 20000)),
                                        MenuDetailDao.Properties.WidgetId
                                                .ge("0")).buildDelete();
                        dq_tmp.executeDeleteWithoutDetachingEntities();
                        CommonUtils.LogWuwei(tag, "delete tmp date "
                                + (date_tmp + 20000));

                        rl_menu.removeAllViews();

                        dq = null;
                    }
                }
                HandlerUtils.showToast(ctxt, "下周菜单删除成功");
                dialogBuilder.deleteInstance();
            }
        }).show();
    }


    /**
     * 询问用户是选择同步本周菜单还是下周菜单
     */
    public void askUpdateMenu() {
        final AlertDialog.Builder builder = new Builder(NewFromHistoryMenuActivity.this);
        builder.setMessage("请选择同步从云端同步的，是本周菜单还是下周菜单？");
        builder.setTitle("提示");
        builder.setPositiveButton("本周", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                builder.create().dismiss();
                new Thread() {
                    public void run() {
                        updateWeekMenu(0);
                    }
                }.start();
            }
        });
        builder.setNegativeButton("下周", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                builder.create().dismiss();
                new Thread() {
                    public void run() {
                        updateWeekMenu(1);
                    }
                }.start();
            }
        });
        builder.create().show();
    }


    /******************************************菜单同步到云端******************************************/
    /**
     * 执行同步菜单（从服务器到电视本地）的过程
     *
     * @param flag->0 同步本周菜单 flag->1 同步下周菜单
     */
    public void updateWeekMenu(int flag) {

        //1、获取选择的周一到周五的日期
        String date[] = null;
        if (flag == 0) {
            date = CommonUtils.getThisWeekDate();
        } else if (flag == 1) {
            date = CommonUtils.getNextWeekDate();
        }

        progress = 0;
        for (int i = 0; i < date.length; i++) {
            try {

                Thread.sleep(500);

                //1、得到每天对应的时间戳
                long timeStamp = CommonUtils.getTimeStamp(Long.parseLong(date[i]));

                long dataLong = Long.parseLong(date[i]);

                String data = "";
                if (dataLong % 10000 / 100 < 10) {
                    if (dataLong % 10000 % 100 < 10) {
                        data = dataLong / 10000 + "-0" + dataLong % 10000 / 100 + "-0" + dataLong % 10000 % 100;
                    } else {
                        data = dataLong / 10000 + "-0" + dataLong % 10000 / 100 + "-" + dataLong % 10000 % 100;
                    }
                } else {
                    if (dataLong % 10000 % 100 < 10) {
                        data = dataLong / 10000 + "-" + dataLong % 10000 / 100 + "-0" + dataLong % 10000 % 100;
                    } else {
                        data = dataLong / 10000 + "-" + dataLong % 10000 / 100 + "-" + dataLong % 10000 % 100;
                    }

                }


                if (list_meal_bucket == null) {
                    getTimeBucketList();
                    return;
                }
                for (int k = 0; k < list_meal_bucket.size(); k++) {
                    progress++;
                    Thread.sleep(1000);

                    if ((progress * 100) / 21 <= 100) {
                        CommonUtils.sendMsg("从云端已经同步到本地" + (progress * 100) / 21 + "%", SHOW_TOAST, handler);
                    }


                    //2、得到每天的菜单json数据
                    final int nowWeekDay = i;
                    long id = list_meal_bucket.get(k).time_bucket_id;
                    ApisManager.getTvMenuFromClound(id, data, new ApiCallback() {
                        @Override
                        public void success(Object object) {

                            com.alibaba.fastjson.JSONObject objResult = (com.alibaba.fastjson.JSONObject) object;
                            com.alibaba.fastjson.JSONObject storeTvMenuObj = objResult.getJSONObject("store_tv_menu");
                            String content = storeTvMenuObj.getString("content");
                            int time_bucket_id = storeTvMenuObj.getInteger("time_bucket_id");

                            Map map = new HashMap<String, String>();
                            map.put("content", content);
                            map.put("nowWeekDay", nowWeekDay);
                            Message msg = new Message();
                            msg.what = NewFromHistoryMenuActivity.UPDATE_MENU_DATA_BY_JSON;
                            msg.obj = map;
                            handler.sendMessage(msg);

                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {

                        }
                    });
                }


            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        CommonUtils.sendMsg(null, HIDE_LOADING, handler);

    }

    /**
     * 在选中某天的菜单后，在手机上显示菜单的时候，同时在手机的数据库中将当前菜单拷贝一份，
     * 注意不是全部拷贝，是有规则的，日期发生了变化：201410031-》201430031 是在月份的
     * 第一个字符加上2（目的是为了区分打开activity时拷贝过来的数据，点击保存将tmp数据转正）
     * <p/>
     * 注意：如果已经存在临时数据怎么办？
     *
     * @param tmp
     */
    public void createTmpData(MenuDetail menu_detail_entity) {


        CommonUtils.LogWuwei(tag, "___________________createTmpData__________________________");
        Long long_date_id;
        String date_id = menu_detail_entity.getMenuDateId();
        if (date_id.charAt(4) < '2') {
            long_date_id = Long.parseLong(date_id) + 20000;
        } else {
            long_date_id = Long.parseLong(date_id);
        }


        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(long_date_id)),
                MenuDetailDao.Properties.WidgetId.eq(menu_detail_entity.getWidgetId()));
        List listResult = qb.list();
        if (listResult.size() == 0) {
            CommonUtils.LogWuwei(tag, "插入临时数据中:long_date_id is " + long_date_id);
            menu_detail_entity.setMenuDateId(Long.toString(long_date_id));
            menuDetailDao.insert(menu_detail_entity);
        }

    }


    public void updateMenuTypeNowChoose(int pos) {

    }


    /**
     * 根据用户选择的tab，显示不同日期的菜单供用户修改，注意点：如果修改过，默认显示修改过的菜单
     *
     * @param pos
     */
    public void updateMenuIdNowChoose(int pos) {
        String nextWeek[] = CommonUtils.getNextWeekDate();
        String thisWeek[] = CommonUtils.getThisWeekDate();
        String date[] = new String[7];
        String tips = null;

        if (choice_last_or_customed == 0) {
            menu_id_now_choose = Long.parseLong(thisWeek[pos] + menu_type);
            date = thisWeek;
            tips = "本周";
        } else if (choice_last_or_customed == 1) {
            menu_id_now_choose = Long.parseLong(nextWeek[pos] + menu_type);
            date = nextWeek;
            tips = "下周";
        }


        String text = null;

        String dayOfWeek = null;

        switch (pos) {
            case 0:
                dayOfWeek = "周一";
                break;
            case 1:
                dayOfWeek = "周二";
                break;
            case 2:
                dayOfWeek = "周三";
                break;
            case 3:
                dayOfWeek = "周四";
                break;
            case 4:
                dayOfWeek = "周五";
                break;
            case 5:
                dayOfWeek = "周六";
                break;
            case 6:
                dayOfWeek = "周日";
                break;
            default:
                dayOfWeek = "不确定";
                break;
        }

        switch (menu_type) {
            case 0:

                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")早餐";
                break;
            case 1:
                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")午餐";
                break;
            case 2:
                text = "当前修改菜单为：" + tips + dayOfWeek + "(" + date[pos] + ")晚餐";
                break;
        }

        CommonUtils.sendMsg(text, UPDATE_NOW_BAR_TIPS, handler);

        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose;
        CommonUtils.LogWuwei(tag, "In updateMenuIdNowChoose pos is " + pos);
        show_menu_mobile(tmp_id);

    }


    /**
     * 用户的修改数据存放在数据库中临时行中
     *
     * @param date_id:201432051
     */
    public void updateLocalDataBase(String widget_id_str, String content, int x, int y, int price, int font_size, String font_color, String background_color, int distance, String additiontal, int bindToServerId, String redundance1) {
        CommonUtils.LogWuwei(tag, "======widget_id content x y price:" + widget_id_str + " " + content + " " + x + " " + y + " " + price);
        //查看对应的menuTable表是否存在
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        if (listResult.size() != 0) {
            for (int i = 0; i < listResult.size(); i++) {
                final MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
                if (menu_detail_entity.getWidgetId() == Integer.parseInt(widget_id_str)) {
                    if (menuDetailEntityLastRecord != menu_detail_entity) {
                        menuDetailEntityLastRecord = menu_detail_entity;
                        CommonUtils.LogWuwei(tag, "-\n\n\nmenuDetailEntityLastRecord 赋值一次\n\n\n\n-");
                    }


                    //		CommonUtils.LogWuwei(tag,"before update content is :"+menu_detail_entity.getName());
                    if (content != null && !content.equals("") && !content.equals(" ")) {
                        CommonUtils.LogWuwei(tag, menu_detail_entity.getMenuDateId() + "in update:now change content:" + content);
                        menu_detail_entity.setName(content);
                    }

                    if (x != -1 && y != -1) {
                        CommonUtils.LogWuwei(tag, menu_detail_entity.getMenuDateId() + "in update:now change position: x" + x + " y:" + y);
                        menu_detail_entity.setX((double) x);
                        menu_detail_entity.setY((double) y);
                    }

                    if (price != -1) {
                        CommonUtils.LogWuwei(tag, menu_detail_entity.getMenuDateId() + "in update:now change price:" + price);
                        menu_detail_entity.setPrice(price);
                    }

                    if (font_size != -1) {
                        menu_detail_entity.setFontSize(font_size);
                    }

                    if (distance != -1) {
                        menu_detail_entity.setDistance((double) distance);
                    }

                    if (bindToServerId != -1)//设定绑定到服务器的id号
                    {
                        menu_detail_entity.setBindToItemServerId(bindToServerId);
                    }

                    if (font_color.length() > 0 && !font_color.equals("") && !font_color.equals(" ")) {
                        menu_detail_entity.setFontColor(font_color);
                    }

                    if (background_color.length() > 0 && !background_color.equals("") && !background_color.equals(" ")) {
                        menu_detail_entity.setBackgroundColor(background_color);
                    }

                    if (additiontal.length() > 0 && additiontal != null && !additiontal.equals("")) {
                        menu_detail_entity.setAddtiontal(additiontal);
                    }

                    if (redundance1 != null && !redundance1.equals(""))//设置绑定的是套餐还是单品
                    {
                        menu_detail_entity.setRedundance1(redundance1);
                    }


                    MenuDetail save_menu_detail_entity = menu_detail_entity;

                    //删除同一日期内widget_id相同的行
                    DeleteQuery<MenuDetail> dq =
                            qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)),
                                    MenuDetailDao.Properties.WidgetId.eq(widget_id_str))
                                    .buildDelete();
                    dq.executeDeleteWithoutDetachingEntities();
                    menuDetailDao.insertOrReplace(save_menu_detail_entity);

                    new Thread() {
                        public void run() {
                            updateToClound(menu_detail_entity);
                            updateToTv(menu_detail_entity);
                        }
                    }.start();
						
						/*runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								updateToClound(menu_detail_entity);	
							}
						});*/
						
				/*		CommonUtils.LogWuwei(tag,"after update content is :"+menu_detail_entity.getName()
								+"menu_date_id is "+menu_detail_entity.getMenuDateId());
					*/
                    break;
                }
            }
        }
    }


    public void updateToClound(MenuDetail menu_detail_entity_tmp) {

        long detail_date = 0;
        JSONArray totalMenuDetail = new JSONArray();
        long final_date = menu_id_now_choose / 10;
        long k = menu_type;
        boolean flag_show_tmp = true;
        int widgetId = menu_detail_entity_tmp.getWidgetId();

        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(menu_detail_entity_tmp.getMenuDateId()));
        final List listResultTmp = qbTmp.list();

        for (int i = 0; i < listResultTmp.size(); i++) {
            JSONObject jsobjectTmp = new JSONObject();
            MenuDetail menu_detail_entity = (MenuDetail) listResultTmp.get(i);
            if (menu_detail_entity.getWidgetId() == widgetId) {
                menu_detail_entity = menu_detail_entity_tmp;
            }
            String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
            String menu_id_str = menu_detail_entity.getMenuDateId();
            String date_id = "";
            String type = menu_detail_entity.getType();
            String name = menu_detail_entity.getName();
            String x = Double.toString(menu_detail_entity.getX());
            String y = Double.toString(menu_detail_entity.getY());
            String price = Integer.toString(menu_detail_entity.getPrice());
            String font_size = Integer.toString(menu_detail_entity.getFontSize());
            String font_color = menu_detail_entity.getFontColor();
            String background_color = menu_detail_entity.getBackgroundColor();
            double distance = menu_detail_entity.getDistance();
            String additiontal = menu_detail_entity.getAddtiontal();
            String bindType = menu_detail_entity.getRedundance1();
            int bindServerItemId = menu_detail_entity.getBindToItemServerId() == null ? -1 : menu_detail_entity.getBindToItemServerId();
            String redundance1 = menu_detail_entity.getRedundance1();
            String redundance2 = menu_detail_entity.getRedundance2();
            String redundance3 = menu_detail_entity.getRedundance3();
            String redundance4 = menu_detail_entity.getRedundance4();
            String redundance5 = menu_detail_entity.getRedundance5();

            if (redundance1 == null) {
                redundance1 = "";
            }

            if (redundance2 == null) {
                redundance2 = "";
            }
            if (redundance3 == null) {
                redundance3 = "";
            }

            if (redundance4 == null) {
                redundance4 = "";
            }

            if (redundance5 == null) {
                redundance5 = "";
            }

            if (!background_color.equals("0x0000") || !background_color.equals("0x000") || background_color == null) {
                background_color = "0x0000";
            }
            if (name == null) {
                name = "";
            }


            if (font_color == null) {
                font_color = "";
            }


            if (flag_show_tmp)//如果是临时菜单，把menu_id_str-20000
            {
                long menu_id_long = Long.parseLong(menu_id_str) - 20000;
                menu_id_str = Long.toString(menu_id_long);
                detail_date = menu_id_long;
            } else {
                detail_date = final_date;
            }


            try {
                jsobjectTmp.put("menu_id_str", menu_id_str);
                jsobjectTmp.put("date_id", date_id);
                jsobjectTmp.put("widget_id", widget_id);
                jsobjectTmp.put("type", type);
                jsobjectTmp.put("name", name);
                jsobjectTmp.put("x", x);
                jsobjectTmp.put("y", y);
                jsobjectTmp.put("price", price);
                jsobjectTmp.put("font_size", font_size);
                jsobjectTmp.put("font_color", font_color);
                jsobjectTmp.put("background_color", background_color);
                jsobjectTmp.put("distance", distance);
                jsobjectTmp.put("additiontal", additiontal);
                jsobjectTmp.put("bindType", bindType);
                if (bindServerItemId != -1) {
                    jsobjectTmp.put("bindServerItemId", "");
                } else {
                    jsobjectTmp.put("bindServerItemId", bindServerItemId);
                }
                jsobjectTmp.put("redundance1", redundance1);
                jsobjectTmp.put("redundance2", redundance2);
                jsobjectTmp.put("redundance3", redundance3);
                jsobjectTmp.put("redundance4", redundance4);
                jsobjectTmp.put("redundance5", redundance5);


                if (name != null || !name.equals("")) {
                    name = URLEncoder.encode(name);
                }

                totalMenuDetail.put(jsobjectTmp);
                jsobjectTmp = null;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                CommonUtils.LogWuwei("JSONException", "JSONException error:" + e.getMessage());
                e.printStackTrace();
            }

        }

        long timeStamp = CommonUtils.getTimeStamp(detail_date / 10);

        for (int p = 0; p < ClientMenuSplashActivty.menu_list_int.length; p++) {
            //CommonUtils.LogWuwei(tag, "早、中、晚"+menu_list_int[0]+" "+menu_list_int[1]+" "+menu_list_int[2]+" timeStamp is "+timeStamp+"day is "+detail_date);
            if (ClientMenuSplashActivty.menu_list_int[p] == -1 || ClientMenuSplashActivty.menu_list_int[p] == 0) {
                ClientMenuSplashActivty.menu_list_int = ClientMenuSplashActivty.menu_list_int_local;
                break;
            }
        }
        String tmp = totalMenuDetail.toString();
        CommonUtils.LogWuwei(tag, "length is " + totalMenuDetail.toString().length() + "\n" + "totalMenuDetail.toString() is :\n" + totalMenuDetail.toString());
    }


    /**
     * 将当前修改的菜品同步到电视
     *
     * @param menu_detail_entity_tmp
     */
    public void updateToTv(MenuDetail menu_detail_entity_tmp) {

        CommonUtils.LogWuwei("update", "本地同步菜单中。。。。");

        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(menu_detail_entity_tmp.getMenuDateId()),
                MenuDetailDao.Properties.WidgetId.eq(menu_detail_entity_tmp.getWidgetId()));
        final List listResultTmp = qbTmp.list();
        flag_clear_tv_screen = true;
        int widgetId = menu_detail_entity_tmp.getWidgetId();
		
	/*	for(int i=0;i<listResultTmp.size();i++)
		{	
			MenuDetail menu_detail_entity = (MenuDetail) listResultTmp.get(i);
		
			if(menu_detail_entity.getWidgetId() == widgetId)
			{
				
			}*/
        MenuDetail menu_detail_entity = menu_detail_entity_tmp;
        String menu_id_str = menu_detail_entity.getMenuDateId();
        String date_id = "";
        String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
        String type = menu_detail_entity.getType();
        String name = menu_detail_entity.getName();
        String x = Double.toString(menu_detail_entity.getX());
        String y = Double.toString(menu_detail_entity.getY());
        String price = Integer.toString(menu_detail_entity.getPrice());
        String font_size = Integer.toString(menu_detail_entity.getFontSize());
        String font_color = menu_detail_entity.getFontColor();
        String background_color = menu_detail_entity.getBackgroundColor();
        double distance = menu_detail_entity.getDistance();
        String additiontal = menu_detail_entity.getAddtiontal();
        String bindType = menu_detail_entity.getRedundance1();
        int bindServerItemId = menu_detail_entity.getBindToItemServerId();

        CommonUtils.LogWuwei(tag, "name is " + name + " bindType is " + bindType);

        if (bindType != null && !bindType.equals("")) {
            if (CommonUtils.isNumeric(bindType)) {
                if (Integer.parseInt(bindType) == 1) {
                    CommonUtils.LogWuwei("bindType", name + "被绑定到套餐 id:" + bindServerItemId);
                } else {
                    CommonUtils.LogWuwei("bindType", name + "被绑定到单品 id:" + bindServerItemId);
                }
            }

        }

        CommonUtils.LogWuwei(tag, "___________menu_id_str is " + menu_id_str + "widget_id is" + widget_id + "name is " + name + "type is " + type);
        if (name != null) {
            name = URLEncoder.encode(name);
        }


        if (!background_color.equals("0x0000") || !background_color.equals("0x000")) {
            background_color = "0x0000";
        }

        long menu_id_long = Long.parseLong(menu_id_str) - 20000;
        menu_id_str = Long.toString(menu_id_long);
        detail_item = "date_id=" + menu_id_str + "&menu_id=" + date_id + "&widget_id=" +
                widget_id + "&type=" + type + "&name=" + name + "&x=" + x + "&y=" + y + "&price=" + price +
                "&font_size=" + font_size + "&font_color=" + font_color + "&background_color=" + background_color +
                "&distance=" + Double.toString(distance) +
                "&additiontal=" + additiontal +
                "&redundance1=" + bindType +
                "&BindToItemServerId=" + Integer.toString(bindServerItemId);

        //CommonUtils.LogWuwei(tag, "detail_item is "+detail_item);


        try {
            if (detail_item == null || detail_item.equals("")) {
                return;
            }
            ip_address = LocalDataDeal.readFromLocalMenuIp(context);
            String uriAPI = "http://" + ip_address + ":8081/?" + detail_item;

            //建立HTTPost对象
            HttpPost httpRequest = new HttpPost(uriAPI);
            CommonUtils.LogWuwei("uri", "uri is " + uriAPI);

            // NameValuePair实现请求参数的封装

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("remove_widget", Integer.toString(menu_detail_entity.getWidgetId())));
			
	       /* if(i == 0 && flag_clear_tv_screen)
	        {
	        		params.add(new BasicNameValuePair("preview","1"));
	        		flag_clear_tv_screen = false;
	        }
	        else
	        {
	        		params.add(new BasicNameValuePair("preview","2"));//存储
	        		if(i == listResultTmp.size()-1)
	        		{
	        			CommonUtils.LogWuwei("update", "准备让电视更新界面");
	        			params.add(new BasicNameValuePair("show","3"));//show	
	        		}
	        }
      */

            try {
                //   添加请求参数到请求对象
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                // 发送请求并等待响应
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                //若状态码为200 ok
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    // 读返回数据
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                } else {
                    CommonUtils.LogWuwei(tag, "post failure " + httpResponse.getStatusLine().getStatusCode());
                }
            } catch (ClientProtocolException e) {

                e.printStackTrace();
                CommonUtils.LogWuwei(tag, "ClientProtocolException is " + e.getMessage());
            } catch (IOException e) {
                CommonUtils.LogWuwei(tag, "IOException is " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                CommonUtils.LogWuwei(tag, "Exception is " + e.getMessage());
                e.printStackTrace();
            }
            httpRequest = null;

        } finally {

        }
    		/*catch (InterruptedException e1) 
    		{
			e1.printStackTrace();
		}*/


        detail_item = "";
        menu_detail_entity = null;


    }


    /**
     * 将用户在点击标题栏的添加按钮后，在对话框中输入的数据插入数据库中
     */
    public void insertToDataBase() {
        //主题、提示、标题、有价格菜品、没价格菜品、横线、纵线
		/*
		主题 举例：“五味，放心吃（周一菜单）” 两个属性 必须赋值：type（theme）、name、widget_id
		提示 举例：“微信关注“五味”，没单减1元” 两个属性必须复制：type（tips）、name、widget_id
		
		标题 举例：“面食”、“饭类” 必须赋值的属性：type（text）、name、x、y、widget_id、font_color、font_size、price(888)
		有价格菜品 举例：“西红柿鸡蛋饭 20元” 必须赋值的属性：type（text）、name、x、y、widget_id、font_color、font_size、price（真实价格）、background_color、distance
		无价格菜品 举例： “韭菜鸡蛋饺子 ”    必须赋值的属性：type（text）、name、x、y、widget_id、font_color、font_size、price(0)、background_color
		
		横线  必须赋值的属性 type（line）、y、widget_id、font_size(25)
		纵线  必须赋值的属性 type（vertical_line）、x、y、widget_id、price、font_color（price和font_color想成得到高度）

		logo 必须赋值的属性 type(pic)、x、y、widget_id
		 */
        //0、数据是否有效
        //0.0类型为菜品时，必须同时两个值有效（名称和价格）
        //0.1类型为主题、标题、提示时，必须1个值有效（名字）
        //0.2类型为图片时，可以都没有

        Boolean flag = false;//准备插入的数据是否有效
        String error = "";

        if (added_type == null) {
            flag = false;
            error = "没有选择类型";
        } else if (added_type.equals("text")) {
            if (added_item_name.length() > 0 && added_padding > 0) {
                flag = true;
            } else {
                error = "添加菜品时，菜品名称、或者价格、或者间隙有错误";
            }
        } else if (added_type.equals("theme") || added_type.equals("tips") || added_type.equals("title")) {
            if (added_item_name.length() > 0) {
                flag = true;
                if (added_type.equals("tips") && added_item_name.length() > 25) {
                    flag = false;
                    error = "提示长度不能超过15个字符";
                }
            } else {
                error = "设置主题、或者提示、或者标题没有输入名字";
            }
        } else if (added_type.equals("line") || added_type.equals("pic")) {
            flag = true;
        } else if (added_type.equals("line_vertical")) {
            if (added_price > 0) {
                flag = true;
            } else {
                error = "没有设置纵线的高度";
            }
        }


        if (!flag)//如果不满足条件
        {
            Toast.makeText(getApplicationContext(), "添加失败，失败原因为" + error, Toast.LENGTH_LONG).show();
            return;
        }

        if (bind_choosen_type == null) {
            bind_choosen_type = "";
        }


        //1、准备插入
        //查看对应的menuTable表是否存在
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        //int view_id = listResult.size();
        int view_id = getMiniumId();
        CommonUtils.LogWuwei(tag, "new id is " + getMiniumId());

        //2、插入
        MenuDetail menu_detail_entity = new MenuDetail();

        menu_detail_entity.setMenuDateId(Long.toString(menu_id_now_choose + 20000));
        menu_detail_entity.setMenuId((long) 0);

        menu_detail_entity.setWidgetId(view_id);
        menu_detail_entity.setType(added_type);
        menu_detail_entity.setX((double) 0);
        menu_detail_entity.setY((double) 0);
        menu_detail_entity.setPrice(0);
        menu_detail_entity.setName(added_item_name);

        menu_detail_entity.setFontSize(28);
        menu_detail_entity.setFontColor("#ffffffff");
        menu_detail_entity.setBackgroundColor("0x0000");

        menu_detail_entity.setDistance((double) 230);
        menu_detail_entity.setAddtiontal("");


        menu_detail_entity.setBindToItemServerId(bind_id);
        menu_detail_entity.setRedundance1(bind_choosen_type);
        menu_detail_entity.setRedundance2("");
        menu_detail_entity.setRedundance3("");
        menu_detail_entity.setRedundance4("");
        menu_detail_entity.setRedundance5("");

        if (added_type.equals("text")) {
            menu_detail_entity.setFontSize(28);
            menu_detail_entity.setName(added_item_name);
            menu_detail_entity.setPrice(added_price);
            menu_detail_entity.setDistance((double) added_padding);
            if (flag_item_new_kind) {
                menu_detail_entity.setFontColor("#ff98fb98");
            } else {
                menu_detail_entity.setFontColor("#ffffffff");
            }

            CommonUtils.LogWuwei(tag, "___________snd msg is additiontal is " + added_additiontal);
            if (added_additiontal != null) {
                if (CommonUtils.isNumeric(added_additiontal) && !added_additiontal.equals("")) {
                    if (Integer.parseInt(added_additiontal) == 1) {
                        menu_detail_entity.setBackgroundColor("1");
                        menu_detail_entity.setAddtiontal("1");
                    }
                } else {
                    menu_detail_entity.setAddtiontal("");
                }
            }
        } else if (added_type.equals("theme") || added_type.equals("tips") || added_type.equals("title")) {
            menu_detail_entity.setName(added_item_name);
            if (added_type.equals("title"))//提示"面食" “饭类”
            {
                added_type = "text";
                menu_detail_entity.setType(added_type);
                menu_detail_entity.setPrice(888);
                menu_detail_entity.setFontSize(35);
            } else if (added_type.equals("theme")) {
                menu_detail_entity.setFontSize(45);
            } else {
                menu_detail_entity.setFontSize(35);
            }
        } else if (added_type.equals("line"))//横线
        {
            menu_detail_entity.setName("1");
            menu_detail_entity.setFontSize(25);
            menu_detail_entity.setFontColor("#ffffffff");
            menu_detail_entity.setBackgroundColor("#ff969696");
        } else if (added_type.equals("line_vertical"))//纵线
        {
            menu_detail_entity.setName("2");
            menu_detail_entity.setFontSize(25);
            menu_detail_entity.setFontColor("80");
            menu_detail_entity.setPrice(added_price);
        } else if (added_type.equals("pic")) {
            menu_detail_entity.setX((double) 0);
            menu_detail_entity.setY((double) 0);
        }

        menuDetailDao.insertOrReplace(menu_detail_entity);

        //3、插入成功显示在pad，失败告知用户失败原因
        show_menu_mobile(menu_id_now_choose);
    }


    /**
     * 复制一个
     */
    public void modify_copy() {

        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                added_item_name = button_new_choosen.getText().toString();
                added_type = menu_detail_entity.getType();
                added_additiontal = menu_detail_entity.getAddtiontal();
                added_padding = (int) (menu_detail_entity.getDistance() + 0);
                added_price = menu_detail_entity.getPrice();
                bind_choosen_type = menu_detail_entity.getRedundance1();
                bind_id = menu_detail_entity.getBindToItemServerId();

                if (menu_detail_entity.getFontColor().equals("#ff98fb98")) {
                    flag_item_new_kind = true;
                    copyNewFlag = flag_item_new_kind;
                }
                //insertToDataBase();
                //show_menu_mobile(menu_id_now_choose);

                copyText = added_item_name;
                copyAdditiontal = added_type;
                copyType = added_type;
                copyDistance = added_padding;
                copyPrice = added_price;
                copyBindType = bind_choosen_type;
                copyBindId = bind_id;

                break;
            }
        }
    }


    /**
     * 粘贴
     */
    public void modify_paste() {

        added_item_name = copyText;
        added_type = copyType;
        added_additiontal = copyAdditiontal;
        added_padding = copyDistance;
        added_price = copyPrice;
        bind_choosen_type = copyBindType;
        bind_id = copyBindId;
        flag_item_new_kind = copyNewFlag;

        insertToDataBase();
        show_menu_mobile(menu_id_now_choose);


        added_additiontal = null;
        added_item_name = null;
        added_padding = 0;
        added_price = 0;
        added_type = null;
        flag_item_new_kind = false;
        bind_id = -1;
        bind_choosen_type = null;

        copyText = null;
        copyType = null;
        copyAdditiontal = null;
        copyAdditiontal = null;
        copyPrice = -1;
        copyBindId = -1;
        copyBindType = null;
        copyDistance = -1;
        copyPrice = -1;
        copyNewFlag = false;
    }


    /**
     * 修改文字内容
     */
    public void modify_content() {

        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(NewFromHistoryMenuActivity.this);
        final View DialogView = factory.inflate(R.layout.modify_content, null);

        dlg_modift_content = new AlertDialog.Builder(NewFromHistoryMenuActivity.this)
                .setTitle("修改文字内容")
                .setView(DialogView)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method
                                int id = button_new_choosen.getId();
                                CommonUtils.LogWuwei(tag, "id is " + id);
                                Message msg = new Message();
                                msg.what = UPDATE_TEXT_CONTENT;
                                Bundle bundle = new Bundle();

                                bundle.putInt("x", -1);
                                bundle.putInt("y", -1);
                                bundle.putString("str", now_new_content);
                                if (button_new_choosen.getId() > 1000) {
                                    return;
                                } else {
                                    bundle.putString("widget_id", Integer.toString(button_new_choosen.getId()));//0
                                    bundle.putInt("price", -1);
                                }
                                msg.setData(bundle);//msg利用Bundle传递数据
                                NewFromHistoryMenuActivity.handler.sendMessage(msg);

                                Button btn = dlg_modift_content.getButton(AlertDialog.BUTTON_POSITIVE);
                                InputMethodUtils.HideKeyboard(btn);

                                show_menu_mobile(menu_id_now_choose);
                                Button et = (Button) findViewById(id);
                                CommonUtils.LogWuwei(tag, et == null ? "et is null" : "et is not null and text is " + et.getText().toString());
                                et.setText(now_new_content);
                                updateLocalDataBase(Integer.toString(button_new_choosen.getId()), now_new_content, -1, -1, -1, -1, "", "", -1, "", -1, "");
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method
                                // stub
                                dlg_modift_content.dismiss();
                            }
                        }).create();

        dlg_modift_content.show();

        TextView tv = (TextView) dlg_modift_content.findViewById(R.id.textView_older_content);
        EditText et_new_content = (EditText) dlg_modift_content.findViewById(R.id.editText_new_content);

        if (button_new_choosen != null) {
            if (tv != null) {
                tv.setText(button_new_choosen.getText().toString());
            }
        }


        if (et_new_content != null) {
            et_new_content.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    now_new_content = s.toString();
                }
            });
        }


    }


    /***
     * 修改价格
     */
    public void modify_price() {
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        String name = "";
        String price = "";

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                name = menu_detail_entity.getName();
                if (menu_detail_entity.getPrice() != null && menu_detail_entity.getPrice() != 0) {
                    price = Integer.toString(menu_detail_entity.getPrice());
                    if (Integer.parseInt(price) > 300) {
                        HandlerUtils.showToast(context, "无价格信息");
                        return;
                    }
                    break;
                } else {
                    HandlerUtils.showToast(context, "无价格信息");
                    return;
                }

            }
        }

        //弹出窗口让用户选择价格
        EditText et_price = new EditText(this);
        et_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.toString().length() > 0 && CommonUtils.isNumeric(s.toString())) {
                    modified_price = Integer.parseInt(s.toString());
                }

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入" + name + "价格");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(et_price);
        et_price.setHint(price);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (modified_price != 0) {
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()),
                            "", -1, -1, modified_price, -1, "", "", -1, "", -1, "");
                    show_menu_mobile(menu_id_now_choose);
                    modified_price = 0;
                } else {
                    Toast.makeText(getApplicationContext(), "输如内容无效", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();


    }


    /**
     * 修改字体大小
     */
    public void modify_font_size() {
        LayoutInflater factory = LayoutInflater.from(NewFromHistoryMenuActivity.this);
        final View DialogView = factory.inflate(R.layout.font_size_picker, null);
        //添加一个列表，显示已经有的字体颜色

        dlg_font_size = new AlertDialog.Builder(
                NewFromHistoryMenuActivity.this)
                .setTitle("请选择字体大小")
                .setView(DialogView)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method
                                if (choose_font_size != 0) {
                                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString()
                                            , -1, -1, -1, choose_font_size, "", "", -1, "", -1, "");
                                    button_new_choosen.setTextSize(choose_font_size);
                                    choose_font_size = 0;
                                    show_menu_mobile(menu_id_now_choose);
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method
                                // stub
                                dialog.dismiss();
                            }
                        }).create();

        dlg_font_size.show();

        tv_preview_font_size = (TextView) dlg_font_size.findViewById(R.id.textview_preview_font_size);
        seekbar_font_size = (SeekBar) dlg_font_size.findViewById(R.id.seekBar_font_size);
        seekbar_font_size.setMax(font_biggest_size);
        seekbar_font_size.setVisibility(View.INVISIBLE);

        OnClickListener ocl_font_size = new OnClickListener() {//设置字体大小时，1、seekBar同时滚动 2、tv_detial_font_size同步 

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.button_smallest_button:
                        choose_font_size = font_smallest_size;
                        //seekbar_font_size.setProgress(font_smallest_size);
                        //tv_detail_font_size.setText(Integer.toString(font_smallest_size));
                        tv_preview_font_size.setTextSize(font_smallest_size);
                        //button_new_choosen.setTextSize(font_smallest_size);
                        break;
                    case R.id.button_small_button:
                        choose_font_size = font_small_size;
                        //seekbar_font_size.setProgress(font_small_size);
                        //tv_detail_font_size.setText(Integer.toString(font_small_size));
                        tv_preview_font_size.setTextSize(font_small_size);
                        //button_new_choosen.setTextSize(font_small_size);
                        break;
                    case R.id.button_medium_button:
                        choose_font_size = font_medium_size;
                        //seekbar_font_size.setProgress(font_medium_size);
                        //tv_detail_font_size.setText(Integer.toString(font_medium_size));
                        tv_preview_font_size.setTextSize(font_medium_size);
                        //button_new_choosen.setTextSize(font_medium_size);
                        break;
                    case R.id.button_big_button:
                        choose_font_size = font_big_size;
                        //seekbar_font_size.setProgress(font_big_size);
                        //tv_detail_font_size.setText(Integer.toString(font_big_size));
                        tv_preview_font_size.setTextSize(font_big_size);
                        //button_new_choosen.setTextSize(font_big_size);
                        break;
                    case R.id.button_biggest_button:
                        choose_font_size = font_biggest_size;
                        //seekbar_font_size.setProgress(font_biggest_size);
                        //tv_detail_font_size.setText(Integer.toString(font_biggest_size));
                        tv_preview_font_size.setTextSize(font_biggest_size);
                        //button_new_choosen.setTextSize(font_biggest_size);
                        break;
                }
            }
        };


        tv_detail_font_size = (TextView) dlg_font_size.findViewById(R.id.textview_show_font_detail_size);
        tv_detail_font_size.setVisibility(View.INVISIBLE);

        button_font_size_smallest = (Button) dlg_font_size.findViewById(R.id.button_smallest_button);
        button_font_size_smallest.setOnClickListener(ocl_font_size);

        button_font_size_small = (Button) dlg_font_size.findViewById(R.id.button_small_button);
        button_font_size_small.setOnClickListener(ocl_font_size);

        button_font_size_medium = (Button) dlg_font_size.findViewById(R.id.button_medium_button);
        button_font_size_medium.setOnClickListener(ocl_font_size);

        button_font_size_big = (Button) dlg_font_size.findViewById(R.id.button_big_button);
        button_font_size_big.setOnClickListener(ocl_font_size);

        button_font_size_biggest = (Button) dlg_font_size.findViewById(R.id.button_biggest_button);
        button_font_size_biggest.setOnClickListener(ocl_font_size);


        if (seekbar_font_size == null) {
            return;
        } else {
            seekbar_font_size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    choose_font_size = progress;
                    tv_preview_font_size.setTextSize(progress);
                    tv_detail_font_size.setText(Integer.toString(progress));
                    button_new_choosen.setTextSize(progress);
                }
            });
        }


    }


    /**
     * 设置餐品是否高亮显示
     */
    public void modify_highlight() {
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();


        int count = 0;
        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getFontColor().equals("#ff98fb98")) {
                count++;
            }
        }


        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                if (menu_detail_entity.getFontColor().equals("#ff98fb98")) {
                    menu_detail_entity.setFontColor("#ffffffff");
                    CommonUtils.LogWuwei(tag, "now change fonr_color to " + "#ffffffff");
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString(),
                            -1, -1, -1, -1, "#ffffffff", "", -1, "", -1, "");
                    button_new_choosen.setTextColor(Color.parseColor("#ffffffff"));
                } else {
                    if (count == 4) {
                        Toast.makeText(context, "高亮显示不允许超过4个", Toast.LENGTH_LONG).show();
                        return;
                    }
                    menu_detail_entity.setFontColor("#ff98fb98");
                    CommonUtils.LogWuwei(tag, "now change fonr_color to " + "#ff98fb98");
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString(),
                            -1, -1, -1, -1, "#ff98fb98", "", -1, "", -1, "");
                    button_new_choosen.setTextColor(Color.parseColor("#ff98fb98"));
                }
                show_menu_mobile(menu_id_now_choose);
                break;
            }
        }

    }


    /**
     * 附属信息设置（比如说辣椒、香菜等）
     */
    public void modify_addtiontal() {
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                if (menu_detail_entity.getAddtiontal().equals("1"))//如果本来就是有辣椒，则设置为无辣椒
                {
                    //	menu_detail_entity.setBackgroundColor("0x0000");
                    menu_detail_entity.setAddtiontal("0");
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), "", -1, -1, -1, -1, "", "0x0000", -1, "0", -1, "");
                    show_menu_mobile(menu_id_now_choose);

                } else {
                    //	menu_detail_entity.setBackgroundColor("1");
                    if (menu_detail_entity.getPrice() > 0 && menu_detail_entity.getPrice() < 300) {
                        menu_detail_entity.setAddtiontal("1");
                        updateLocalDataBase(Integer.toString(button_new_choosen.getId()), "", -1, -1, -1, -1, "", "1", -1, "1", -1, "");
                        show_menu_mobile(menu_id_now_choose);
                    } else {
                        HandlerUtils.showToast(context, "不允许加辣");
                    }

                }


                break;
            }
        }
    }


    /**
     * 修改价格和菜品之间的距离
     */
    public void modify_distance() {

        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        String distance = "";

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                if (menu_detail_entity.getDistance() != null && !menu_detail_entity.getDistance().equals("")) {
                    if (menu_detail_entity.getDistance() > (double) 0) {
                        distance = Double.toString(menu_detail_entity.getDistance());
                    } else {
                        HandlerUtils.showToast(context, "无距离信息");
                        return;
                    }
                } else {
                    HandlerUtils.showToast(context, "无距离信息");
                    return;
                }
                break;
            }
        }

        String[] distance_list = new String[]{"距离1(150px)", "距离2(180px)", "距离3(200px)", "距离4(230px)",
                "距离5(260px)", "距离6(290px)", "距离7(320px)", "距离8(350px)"};
        DialogInterface.OnClickListener buttonOnClick = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (which >= 0) {
                    switch (which) {
                        case 0:
                            modified_distance = 150;
                            break;
                        case 1:
                            modified_distance = 180;
                            break;
                        case 2:
                            modified_distance = 200;
                            break;
                        case 3:
                            modified_distance = 230;
                            break;
                        case 4:
                            modified_distance = 260;
                            break;
                        case 5:
                            modified_distance = 290;
                            break;
                        case 6:
                            modified_distance = 320;
                            break;
                        case 7:
                            modified_distance = 350;
                            break;
                    }
                    CommonUtils.LogWuwei(tag, "choose distance is " + modified_distance);
                } else if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (modified_distance > 0) {
                        CommonUtils.LogWuwei(tag, "修改距离中：" + modified_distance);
                        updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString(), -1, -1, -1, -1, "", "", modified_distance, "", -1, "");
                    }

                    modified_distance = 0;
                    show_menu_mobile(menu_id_now_choose);
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    modified_distance = 0;
                    Toast.makeText(getApplicationContext(), "取消选择距离", Toast.LENGTH_LONG).show();
                }
            }
        };

        //弹窗让用户选择距离
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择菜品与价格间隙长度（修改前是" + distance + ")");
        builder.setSingleChoiceItems(distance_list, -1, buttonOnClick);

        builder.setPositiveButton("确定", buttonOnClick);
        builder.setNegativeButton("取消", buttonOnClick);
        builder.show();


    }


    /**
     * 删除选中的菜品
     */
    public void delete_item() {

        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("确认删除" + button_new_choosen.getText().toString() + "吗");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                QueryBuilder qb = menuDetailDao.queryBuilder();
                Long tmp_id = menu_id_now_choose + 20000;

                qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
                List listResult = qb.list();

                for (int i = 0; i < listResult.size(); i++) {
                    MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
                    if (menu_detail_entity.getWidgetId() == button_new_choosen.getId()) {
                        //删除同一日期内widget_id相同的行
                        DeleteQuery<MenuDetail> dq =
                                qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)),
                                        MenuDetailDao.Properties.WidgetId.eq(Integer.toString(button_new_choosen.getId())))
                                        .buildDelete();

                        dq.executeDeleteWithoutDetachingEntities();
                        show_menu_mobile(menu_id_now_choose);
                        break;
                    }
                }
            }

        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }

        });
        builder.show();
    }

    /***
     * 菜单拷贝
     */
    public void copyMenu() {
        final DatePicker dp = new DatePicker(getApplicationContext());

        final AlertDialog dlg = new AlertDialog.Builder(NewFromHistoryMenuActivity.this)
                .setTitle("选择拷贝到的日期：")
                .setView(dp)
                .setPositiveButton("拷贝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (menuIndexCopyTo != -1) {
                            copyDataDeal(menu_id_now_choose, menuIndexCopyTo, menu_type);
                            menuIndexCopyTo = -1;
                        } else {
                            HandlerUtils.showToast(context, "拷贝未选择日期");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .create();
        dlg.show();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dp.setSpinnersShown(true);
        dp.setCalendarViewShown(false);
        dp.init(year, month, day, new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                // TODO Auto-generated method stub

                noOfTimesCalled++;
                if (noOfTimesCalled % 2 != 0) {
					/*CommonUtils.LogWuwei(tag, year+":"+monthOfYear+":"+dayOfMonth);
					
					menuIndexCopyTo = year*10000+(monthOfYear+1)*100+dayOfMonth;
					
					
					int y=year-1;
					int m=monthOfYear;
					int c=20;
					int d=dayOfMonth+12;
					int w=(y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1)%7;

					CommonUtils.LogWuwei(tag, "menuIndexCopyTo is "+menuIndexCopyTo);
				*/

                    monthOfYear = monthOfYear + 1;

                    menuIndexCopyTo = year * 10000 + (monthOfYear) * 100 + dayOfMonth;

                    CommonUtils.LogWuwei(tag, "" + year + ":" + monthOfYear + ":" + dayOfMonth + "menuIndexCopyTo is " + menuIndexCopyTo);

                    int w = Integer.parseInt(CommonUtils.CaculateWeekDay(year, monthOfYear, dayOfMonth));
                    String myWeek = null;
                    switch (w) {
                        case 1:
                            myWeek = "一";
                            break;
                        case 2:
                            myWeek = "二";
                            break;
                        case 3:
                            myWeek = "三";
                            break;
                        case 4:
                            myWeek = "四";
                            break;
                        case 5:
                            myWeek = "五";
                            break;
                        case 6:
                            myWeek = "六";
                            break;
                        case 7:
                            myWeek = "日";
                            break;
                        default:
                            break;
                    }

                    //int myWeek = CommonUtils.CaculateWeekDay(year, monthOfYear+1, dayOfMonth);

                    String title = null;
                    switch (menu_type) {
                        case 0:
                            title = "确定将当前菜单拷贝到:" + year + "年" + (monthOfYear) + "月" + dayOfMonth + "(周" + myWeek + ") 早餐??";
                            break;
                        case 1:
                            title = "确定将当前菜单拷贝到:" + year + "年" + (monthOfYear) + "月" + dayOfMonth + "(周" + myWeek + ") 午餐??";
                            break;
                        case 2:
                            title = "确定将当前菜单拷贝到:" + year + "年" + (monthOfYear) + "月" + dayOfMonth + "(周" + myWeek + ") 晚餐??";
                            break;
                    }

                    dlg.setTitle(title);
                }


            }
        });
    }


    /**
     * 撤销
     */
    public void recovery() {
        //删除记录
        if (menuDetailEntityLastRecord != null) {
            Long date = Long.parseLong(menuDetailEntityLastRecord.getMenuDateId());
            int widgetId = menuDetailEntityLastRecord.getWidgetId();
            String saveName = menuDetailEntityLastRecord.getName();
            CommonUtils.LogWuwei(tag, "date is " + date + "\n widgetId is " + widgetId +
                    "\nmenu_id_now_choose is " + menu_id_now_choose + "name is " + saveName);

            if (date == menu_id_now_choose) {
                CommonUtils.LogWuwei(tag, "InRecovery " + "date == menu_id_now_choose" + "准备查询数据并删除--成功");
                QueryBuilder qb = menuDetailDao.queryBuilder();
                //删除同一日期内widget_id相同的行
                DeleteQuery<MenuDetail> dq =
                        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
                                MenuDetailDao.Properties.WidgetId.eq(widgetId))
                                .buildDelete();
                dq.executeDeleteWithoutDetachingEntities();

                QueryBuilder qbTmp = menuDetailDao.queryBuilder();
                //删除同一日期内widget_id相同的行
                DeleteQuery<MenuDetail> dqTmp =
                        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date + 20000)),
                                MenuDetailDao.Properties.WidgetId.eq(widgetId))
                                .buildDelete();
                dqTmp.executeDeleteWithoutDetachingEntities();

                menuDetailDao.insertOrReplace(menuDetailEntityLastRecord);

                menuDetailEntityLastRecord.setMenuDateId(Long.toString(date + 20000));
                menuDetailDao.insertOrReplace(menuDetailEntityLastRecord);

                menuDetailEntityLastRecord.setMenuDateId(Long.toString(date));

                show_menu_mobile(menu_id_now_choose);
                return;
            } else if (date == (menu_id_now_choose + 20000)) {
                CommonUtils.LogWuwei(tag, "InRecovery 准备查询数据并删除--成功");
                QueryBuilder qb = menuDetailDao.queryBuilder();
                //删除同一日期内widget_id相同的行
                DeleteQuery<MenuDetail> dq =
                        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
                                MenuDetailDao.Properties.WidgetId.eq(widgetId))
                                .buildDelete();
                dq.executeDeleteWithoutDetachingEntities();


                QueryBuilder qbTmp = menuDetailDao.queryBuilder();
                //删除同一日期内widget_id相同的行
                DeleteQuery<MenuDetail> dqTmp =
                        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date - 20000)),
                                MenuDetailDao.Properties.WidgetId.eq(widgetId))
                                .buildDelete();
                dqTmp.executeDeleteWithoutDetachingEntities();


                menuDetailDao.insertOrReplace(menuDetailEntityLastRecord);

                menuDetailEntityLastRecord.setMenuDateId(Long.toString(date - 20000));
                menuDetailDao.insertOrReplace(menuDetailEntityLastRecord);

                menuDetailEntityLastRecord.setMenuDateId(Long.toString(date));

                show_menu_mobile(menu_id_now_choose);
                return;
            } else {
                CommonUtils.LogWuwei(tag, "InRecovery 准备查询数据并删除--失败");
            }
        } else {
            CommonUtils.LogWuwei(tag, "InRecovery menuDetailEntityLastRecord为null");
        }

        //恢复记录
    }


    /**
     * 当用户在actionBar点击添加按钮时，弹出窗口，让用户输入菜品名称、菜品价格、菜品名称与价格的距离
     */
    public void add_menu_item(Context context) {

        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(context);
        final View DialogView = factory.inflate(R.layout.get_item_detailed_info_when_added, null);
        AlertDialog dlg = new AlertDialog.Builder(NewFromHistoryMenuActivity.this)
                .setTitle("请添加菜品信息")
                .setView(DialogView)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method

                                CommonUtils.LogWuwei(tag, "item_name is " + added_item_name + "\n" +
                                        "item_price is " + added_price + "\n" +
                                        "item_type is " + added_type + "\n" +
                                        "item_distance is " + added_padding + "\n" +
                                        "item_additontal is " + added_additiontal + "\n" +
                                        "flag_item_new_kind is" + flag_item_new_kind);

                                insertToDataBase();

                                added_additiontal = null;
                                added_item_name = null;
                                added_padding = 0;
                                added_price = 0;
                                added_type = null;
                                flag_item_new_kind = false;
                                bind_id = -1;
                                bind_choosen_type = null;


                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method
                                // stub
                                dialog.dismiss();
                            }
                        }).create();
        dlg.show();
        dlg.setCanceledOnTouchOutside(false);

        final EditText et_item_name = (EditText) dlg.findViewById(R.id.editText_item_name);
        final EditText et_item_price = (EditText) dlg.findViewById(R.id.editText_item_price);
        final Spinner spinner_distance = (Spinner) dlg.findViewById(R.id.spinner_item_name_price_distance);
        final Spinner spinner_type = (Spinner) dlg.findViewById(R.id.spinner_item_type);
        final Spinner spinner_additiontal = (Spinner) dlg.findViewById(R.id.spinner_item_additiontal);
        final CheckBox checkBox_item_new_kind = (CheckBox) dlg.findViewById(R.id.checkBox_item_new_kind);
        final CheckBox checkBox_item_whether_bind = (CheckBox) dlg.findViewById(R.id.checkBox_item_whether_bind);

        final TextView tv_type = (TextView) dlg.findViewById(R.id.textView_item_type_tips);
        final TextView tv_name = (TextView) dlg.findViewById(R.id.textView_item_name_tips);
        final TextView tv_price = (TextView) dlg.findViewById(R.id.textView_item_price_tips);
        final TextView tv_additiontal = (TextView) dlg.findViewById(R.id.textView_item_additiontal_tips);
        final TextView tv_distance = (TextView) dlg.findViewById(R.id.textView_distance_tips);


        et_item_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                added_item_name = s.toString();
            }
        });

        et_item_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String str_added_price = s.toString();
                if (str_added_price.length() > 0 && CommonUtils.isNumeric(str_added_price)) {
                    added_price = Integer.parseInt(str_added_price);
                }
            }
        });


        //----------------------------------------------1、spinner设置间距-----------------------------------------//
        List<String> list = new ArrayList<String>();
        list.add("间距1:180");
        list.add("间距2:230");
        list.add("间距3:320");
        list.add("间距4:350");
        list.add("选择菜品名称与价格的间隙");

        final SpinnerHintAdapter hintAdapter = new SpinnerHintAdapter(this, android.R.layout.simple_spinner_item, list);
        hintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_distance.setAdapter(hintAdapter);
        spinner_distance.setSelection(hintAdapter.getCount());

        spinner_distance.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, "you choose" + arg2 + " ." + hintAdapter.getItem(arg2));
                switch (arg2) {
                    case 0:
                        added_padding = 180;
                        break;
                    case 1:
                        added_padding = 230;
                        break;
                    case 2:
                        added_padding = 320;
                        break;
                    case 3:
                        added_padding = 350;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                added_padding = 230;
            }

        });


        //----------------------------------------------2、spinner设置类型（主题、标题、提示、菜品）------------------------//
        List<String> list_type = new ArrayList<String>();
        list_type.add("菜单主题");
        list_type.add("菜品类别");
        list_type.add("提示");
        list_type.add("菜品");
        list_type.add("logo");
        list_type.add("横线");
        list_type.add("纵线");
        list_type.add("选择类型");


        final SpinnerHintAdapter arrayAdapter_type = new SpinnerHintAdapter(this, android.R.layout.simple_spinner_item, list_type);

        arrayAdapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_type.setAdapter(arrayAdapter_type);
        spinner_type.setSelection(arrayAdapter_type.getCount());


        spinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                switch (arg2) {
                    case 0:
                        added_type = "theme";

                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.INVISIBLE);
                        tv_price.setVisibility(View.INVISIBLE);

                        et_item_name.setVisibility(View.VISIBLE);
                        tv_name.setVisibility(View.VISIBLE);

                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);

                        spinner_distance.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);

                        et_item_name.setHint("五味,放心吃(午餐：周一)");
                        break;
                    case 1:
                        added_type = "title";

                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.INVISIBLE);
                        tv_price.setVisibility(View.INVISIBLE);

                        et_item_name.setVisibility(View.VISIBLE);
                        tv_name.setVisibility(View.VISIBLE);

                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);

                        spinner_distance.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);

                        et_item_name.setHint("面食");
                        break;
                    case 2:
                        added_type = "tips";

                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.INVISIBLE);
                        tv_price.setVisibility(View.INVISIBLE);

                        et_item_name.setVisibility(View.VISIBLE);
                        tv_name.setVisibility(View.VISIBLE);

                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);

                        spinner_distance.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);

                        et_item_name.setHint("微信关注\"五味\"自助下单每天减一元");
                        break;
                    case 3:
                        added_type = "text";
                        et_item_price.setVisibility(View.VISIBLE);
                        et_item_name.setVisibility(View.VISIBLE);
                        spinner_additiontal.setVisibility(View.VISIBLE);
                        spinner_distance.setVisibility(View.VISIBLE);

                        tv_price.setVisibility(View.VISIBLE);
                        tv_price.setHint("价格");
                        tv_name.setVisibility(View.VISIBLE);
                        tv_additiontal.setVisibility(View.VISIBLE);
                        tv_name.setVisibility(View.VISIBLE);
                        tv_distance.setVisibility(View.VISIBLE);
                        checkBox_item_new_kind.setVisibility(View.VISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.VISIBLE);

                        et_item_price.setHint("30");
                        et_item_name.setHint("西红柿鸡蛋饭");
                        break;
                    case 4:
                        added_type = "pic";
                        et_item_price.setHint("无需设置价格");
                        et_item_name.setHint("无需输入文字内容");

                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.INVISIBLE);
                        et_item_name.setVisibility(View.INVISIBLE);
                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        spinner_distance.setVisibility(View.INVISIBLE);

                        tv_price.setVisibility(View.INVISIBLE);
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);
                        break;
                    case 5://画横线
                        added_type = "line";
                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.INVISIBLE);
                        et_item_name.setVisibility(View.INVISIBLE);
                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        spinner_distance.setVisibility(View.INVISIBLE);

                        tv_price.setVisibility(View.INVISIBLE);
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);
                        break;
                    case 6://画纵线(price代表长度)
                        added_type = "line_vertical";
                        checkBox_item_new_kind.setVisibility(View.INVISIBLE);
                        checkBox_item_whether_bind.setVisibility(View.INVISIBLE);

                        et_item_price.setVisibility(View.VISIBLE);
                        et_item_price.setHint("8");

                        et_item_name.setVisibility(View.INVISIBLE);
                        spinner_additiontal.setVisibility(View.INVISIBLE);
                        spinner_distance.setVisibility(View.INVISIBLE);

                        tv_price.setVisibility(View.VISIBLE);
                        tv_price.setHint("高度");
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_additiontal.setVisibility(View.INVISIBLE);
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_distance.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                added_type = "text";
            }

        });


        //----------------------------------------------3、spinner设置附加信息（辣椒、香菜等）---------------------------------//
        List<String> list_additiontal = new ArrayList<String>();
        list_additiontal.add("辣椒");
        list_additiontal.add("香菜");
        list_additiontal.add("其他");
        list_additiontal.add("无");
        list_additiontal.add("选择菜品附加属性");

        final SpinnerHintAdapter arrayAdapter_additontal = new SpinnerHintAdapter(this, android.R.layout.simple_spinner_item, list_additiontal);

        arrayAdapter_additontal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_additiontal.setAdapter(arrayAdapter_additontal);
        spinner_additiontal.setSelection(arrayAdapter_additontal.getCount());


        spinner_additiontal.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                //MsgUtils.SendSingleMsg(splash.handlerTools, "敬请期待", HandlerUtils.SHOW_NORMAL_TOAST);
                switch (arg2) {
                    case 0:
                        added_additiontal = "1";
                        break;
                    case 1:
                        added_additiontal = null;
                        break;
                    case 2:
                        added_additiontal = null;
                        break;
                    case 3:
                        added_additiontal = null;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        checkBox_item_new_kind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                flag_item_new_kind = isChecked;
            }
        });

        checkBox_item_whether_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked)//选中，弹窗让用户选择绑定的菜品名称和个数
                {
                    showBindInfo(et_item_name.getText().toString(), checkBox_item_whether_bind, getMiniumId(), true);
                } else//未选中，清除菜品名称和个数信息
                {
                    //清除数据
                    checkBox_item_whether_bind.setText("菜品绑定");
                }
            }
        });

    }


    /**
     * 弹出窗口让用户选择绑定的菜品类型和服务器中对应的菜品id
     *
     * @param title     正在添加的菜品名称
     * @param view      选择框view
     * @param widget_id 正在添加的菜品id
     * @param flag      调用来自正在添加菜品（true）还是contextMenu（false）
     */
    public void showBindInfo(String title, CheckBox view, final int widget_id, final boolean flag) {
        if (title == null || title.equals("") || !added_type.equals("text")) {
            HandlerUtils.showToast(context, "请保证:\n1、正在添加的项目的类型是菜品\n2、菜品名称不为空");
            title = "请选择要绑定到的菜品名称和数量";
            if (view != null) {
                view.setChecked(false);
            }
            return;
        }

        final CheckBox checkbox_bind_view = view;
        LayoutInflater factory = LayoutInflater.from(context);
        final View DialogView = factory.inflate(R.layout.menu_item_bind_info, null);

        ListView listview = null;
        EditText et_num = null;

        final String titleFinally = title;

        final AlertDialog dlg = new AlertDialog.Builder(NewFromHistoryMenuActivity.this)
                .setTitle(title)
                .setView(DialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (bind_choosen_type != null && bind_id != -1) {
                            //存储到数据库中
                            if (checkbox_bind_view != null) {
                                checkbox_bind_view.setText("已经绑定到：" + bind_choosen_type + " 服务器对应菜品id为:" + bind_id);
                                dialog.dismiss();
                            }
                            if (!flag)//如果是conttextMenu调用的，则在这里存储变更数据
                            {
                                saveBindResult(bind_choosen_type, bind_id, widget_id);
                            }
                        } else {
                            if (checkbox_bind_view != null) {
                                checkbox_bind_view.setChecked(false);
                            }
                            HandlerUtils.showToast(context, "请确定同时选择了绑定菜品名称和绑定数量");

                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (checkbox_bind_view != null) {
                            checkbox_bind_view.setChecked(false);
                        }


                    }
                })
                .create();

        dlg.show();


        final String[] name = {"点我开始关联"};

        //bind_textview_bind_name_tips = (TextView)dlg.findViewById(R.id.textview_menu_item_bind_info_name_tips);
        bind_textview_bind_already_name = (TextView) dlg.findViewById(R.id.textview_menu_itme_now_binded);

        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_id_now_choose)));
        List listResult = qb.list();

        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResultTmp = qbTmp.list();

        CommonUtils.LogWuwei(tag, "menu_id_now_choose+20000 is " + menu_id_now_choose);
        CommonUtils.LogWuwei(tag, "menu_id_now_choose+20000 is " + (menu_id_now_choose + 20000));

        if (listResultTmp.size() > 0) {
            listResult = listResultTmp;
            CommonUtils.LogWuwei(tag, "临时菜单数据");
        } else {
            CommonUtils.LogWuwei(tag, "没有临时菜单数据");
        }

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);

            CommonUtils.LogWuwei(tag, "_______serverId is " + menu_detail_entity.getBindToItemServerId() + " type " + menu_detail_entity.getRedundance1() + "date is " + menu_detail_entity.getMenuDateId());

            if (menu_detail_entity.getWidgetId() == widget_id) {
                String bindAlreadyType = menu_detail_entity.getRedundance1();
                int bindAlreadyId = menu_detail_entity.getBindToItemServerId() != null ? menu_detail_entity.getBindToItemServerId() : -1;
                CommonUtils.LogWuwei(tag, "widgetId 匹配成功" + "bindAlreadyType " + bindAlreadyType + " bindAlreadyId is " + bindAlreadyId);

                String bindAlreadyName = null;
                String bindAlreadyTypeText = null;

                if (bindAlreadyType != null && !bindAlreadyType.equals("")) {
                    CommonUtils.LogWuwei(tag, "widgetId 匹配成功" + " bindAlreadyType is " + bindAlreadyType);

                    //if(CommonUtils.isNumeric(bindAlreadyType))
                    {
                        if (Integer.parseInt(bindAlreadyType) == 1) {
                            bindAlreadyTypeText = "套餐";
                            CommonUtils.LogWuwei(tag, "绑定类型为套餐，下一步根据id找名字");
                            for (int k = 0; k < serverComboNameId.length; k++) {
                                if (serverComboNameId[k][1] != null) {
                                    if (Integer.parseInt(serverComboNameId[k][1]) == bindAlreadyId) {
                                        bindAlreadyName = serverComboNameId[k][0];
                                        CommonUtils.LogWuwei(tag, "根据id找到名字" + bindAlreadyName);
                                        CommonUtils.LogWuwei(tag, "bindAlreadyName is " + bindAlreadyName + " bindAlreadyTypeText is " + bindAlreadyTypeText);
                                    }
                                }

                            }
                        } else {
                            bindAlreadyTypeText = "单品";
                            CommonUtils.LogWuwei(tag, "绑定类型为单品，下一步根据id找名字");
                            for (int k = 0; k < serverSingleNameId.length; k++) {
                                if (serverSingleNameId[k][1] != null && !serverSingleNameId[k][1].equals("")) {
                                    if (Integer.parseInt(serverSingleNameId[k][1]) == bindAlreadyId) {
                                        bindAlreadyName = serverSingleNameId[k][0];
                                        CommonUtils.LogWuwei(tag, "根据id找名字" + bindAlreadyName);
                                        CommonUtils.LogWuwei(tag, "bindAlreadyName is " + bindAlreadyName + " bindAlreadyTypeText is " + bindAlreadyTypeText);
                                    }
                                }
                            }
                        }
                    }
                }

                if (bindAlreadyName != null && !bindAlreadyName.equals("")) {
                    bind_textview_bind_already_name.setText("修改前：\"" + title + "\"已关联到" + ":\"" + bindAlreadyName + "\"");
                } else {
                    bind_textview_bind_already_name.setText("无关联记录");
                }

                break;
            }
        }

        listview = (ListView) dlg.findViewById(R.id.listview_menu_item_bind_info_name);
        listview.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, name));
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, "你选中了" + name[arg2]);

                //bind_choosen_name = name[arg2];
				/*if(name[arg2].equals("套餐"))
				{
					bind_choosen_type = "1";
				}
				else
				{
					bind_choosen_type = "0";
				}*/

                bind_choosen_type = "1";

                //bind_textview_bind_name_tips.setText("选择要绑定的类型:"+name[arg2]);

                if (arg2 == 0) {
                    showBindListView(dlg, titleFinally, 0);
                } else {
                    showBindListView(dlg, titleFinally, 1);
                }

            }
        });
    }


    public void saveBindResult(String ItemCategory, int server_id, int widget_id) {

        CommonUtils.LogWuwei(tag, "ready to save bind info :widget_id" + widget_id + ":server_id is " + server_id
                + "ItemCategory is " + ItemCategory);

        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_id_now_choose)));
        List listResult = qb.list();

        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResultTmp = qbTmp.list();

        CommonUtils.LogWuwei(tag, "tmp_id is " + tmp_id);

        if (listResultTmp.size() > 0) {
            listResult = listResultTmp;
            CommonUtils.LogWuwei(tag, "有临时数据+listResult.size() is " + listResult.size());
        } else {
            CommonUtils.LogWuwei(tag, "没有临时数据 +listResult.size() is " + listResult.size());
        }

        CommonUtils.LogWuwei(tag, "listResult.size() is" + listResult.size() + "ready to save bind info :widget_id" + widget_id + ":server_id is " + server_id
                + "ItemCategory is " + ItemCategory);

        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);

            if (menu_detail_entity.getWidgetId() == widget_id) {
                //设置与服务器绑定的菜品id
                menu_detail_entity.setBindToItemServerId(server_id);

                //设置与服务器绑定是菜品是套餐还是单品
                if (ItemCategory.equals("1")) {
                    menu_detail_entity.setRedundance1("1");
                    menu_detail_entity.setBindToItemServerId(server_id);
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString(), -1, -1, -1, -1, "", "", -1, "", server_id, "1");
                } else if (ItemCategory.equals("0")) {
                    menu_detail_entity.setRedundance1("0");
                    menu_detail_entity.setBindToItemServerId(server_id);
                    updateLocalDataBase(Integer.toString(button_new_choosen.getId()), button_new_choosen.getText().toString(), -1, -1, -1, -1, "", "", -1, "", server_id, "0");
                }
                CommonUtils.LogWuwei(tag, "data modified success");

                break;
            }
        }

    }


    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
    public boolean onTouch(View v, MotionEvent event) {


        int action = event.getAction();

        Button button = (Button) v;

        switch (action) {
            case MotionEvent.ACTION_DOWN://按下时候

                flag_move = false;

                CommonUtils.LogWuwei("motion", "__________" + button.getText().toString() + "is clicked" + "flag_move is " + flag_move);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                registerForContextMenu(v);
                if (sv != null) {
                    sv.setScrollingEnabled(false);
                }


                if (firstClick != 0 && System.currentTimeMillis() - firstClick > 500) {
                    count = 0;
                }
                count++;
                if (count == 1) {
                    firstClick = System.currentTimeMillis();
                } else if (count == 2) {
                    lastClick = System.currentTimeMillis();
                    // 两次点击小于500ms 也就是连续点击  
                    if (lastClick - firstClick < 500) {
                        CommonUtils.LogWuwei("Double", "Double clicked");
                        openContextMenu(v);
                    }
                    clear();
                }


                break;

            case MotionEvent.ACTION_MOVE://移动时候

                flag_move = true;
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                modified_widget_id = v.getId();
                unregisterForContextMenu(v);
                if (rl_menu == null || sv == null) {
                    return false;
                }
                rl_menu.setBackgroundResource(R.drawable.background_grid_1);
                v.setFocusable(false);

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;					
	    				
	    			/*	if(left < 0){
	    					left = 0;
	    					right = left + v.getWidth();
	    				}					
	    							
	    				if(top < 0){
	    					top = 0;
	    					bottom = top + v.getHeight();
	    				}	*/


                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - v.getHeight();
                }

                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - v.getWidth();
                }

                //移动view
                if (v.getId() < 1000) {
                    v.layout(left, top, right, bottom);
                }


                lastX = (int) event.getRawX();//更新移动后的位置（横坐标）
                lastY = (int) event.getRawY();//更新移动后的位置（纵坐标）

                CommonUtils.LogWuwei(tag, "lastX is " + lastX + " lastY is " + lastY);
	    				
	    				/*CommonUtils.LogWuwei(tag, "screenWIdth is "+screenWidth+"screenHeight"+
	    				screenHeight+"left"+left+"top"+top+"right"+right+"bottom"+bottom);*/
                break;

            case MotionEvent.ACTION_UP://手指抬起
                CommonUtils.LogWuwei("motion", "__________________" + button.getText().toString() + "is action up" + "flag_move is " + flag_move);
                if (sv == null || rl_menu == null) {
                    return false;
                }

                sv.setScrollingEnabled(true);
                rl_menu.setBackgroundResource(R.drawable.blackboard);
                registerForContextMenu(v);
                //v.setFocusable(true);

                QueryBuilder qb = menuDetailDao.queryBuilder();
                Long tmp_id = menu_id_now_choose + 20000;

                qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
                List listResult = qb.list();


                Message msg_refresh_price = new Message();
                msg_refresh_price.what = UPDATE_PRICE_LAYOUT;
                handler.sendMessage(msg_refresh_price);


                int location[] = new int[2];
                v.getLocationOnScreen(location);
                CommonUtils.LogWuwei(tag, "locatuon[0] is " + location[0] + " location[1] is " + location[1]);

                actionBarHeight = 112;//CommonUtils.getActionBarHeigth(this);
                statusBarHeight = CommonUtils.getTitleBarHeight(this);
	    				
	            		/*x_fraction = 1.6*(width_screen/2048);
	            		y_fraction = 2.133*(height_screen/1536);*/
	            		
	    				/*int x=(int) (location[0]/1.6);
	    				int y=(int) ((location[1]-actionBarHeight-statusBarHeight+scrollBarOffset)/2.13333);*/

                int x = (int) (location[0] / 1.5);
                int y = (int) ((location[1] - actionBarHeight - statusBarHeight + scrollBarOffset) / 1.533333);


                if (location[0] < 0 || location[0] > screenWidth) {
                    x = screenWidth / 2;
                }
                if (location[1] < 0 || location[1] > screenHeight) {
                    y = screenHeight / 2;
                }


                CommonUtils.LogWuwei(tag, "location[0] is " + location[0] +
                        "location[1] is " + location[1] + "\n x is " + x + " y is " + y + " actionBar Height is " + actionBarHeight + " statusBar Height is " + statusBarHeight);

                if (flag_move) {
                    CommonUtils.LogWuwei(tag, "flag_move is true");
                    Message msg_x_y = new Message();
                    msg_x_y.what = UPDATE_X_Y;
                    Bundle bundle_x_y = new Bundle();
                    bundle_x_y.putInt("x", x);
                    bundle_x_y.putInt("y", y);
                    msg_x_y.setData(bundle_x_y);
                    handler.sendMessage(msg_x_y);
                }
                flag_move = false;
                break;
        }
        return false;
    }

    // 清空状态  
    void clear() {
        count = 0;
        firstClick = 0;
        lastClick = 0;
    }

    /**
     * 找到当前表中最小可用的id，用于赋值给添加新项目时的id
     * 举例：1，2，3，4，9，10，那么改函数应该返回5
     *
     * @return
     */
    public int getMiniumId() {
        int return_id = 0;
        boolean flag = false;//true--> id中间存在可用数据  false--->id中间不存在可用数据，要在最大数的基础上加1


        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long tmp_id = menu_id_now_choose + 20000;

        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(tmp_id)));
        List listResult = qb.list();


        //1、首先采用选择法排序
        int[] all_id = new int[listResult.size()];

        //1.1初始化
        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
            all_id[i] = menu_detail_entity.getWidgetId();
        }


        //1.2得到一个从小到大的序列
        for (int i = 0; i < listResult.size() - 1; i++) {
            for (int j = i + 1; j < listResult.size(); j++) {
                if (all_id[i] > all_id[j]) {
                    int tmp = all_id[i];
                    all_id[i] = all_id[j];
                    all_id[j] = tmp;
                }
            }
        }

        //2、从有序数组中得到最小可用的数据
        for (int i = 0; i < listResult.size(); i++) {
            if (all_id[i] > i) {
                return_id = i;
                flag = true;//得到最小可用数据
                break;
            }
        }

        if (!flag)//如果没有得到可用最小数据
        {
            if (all_id.length > 0) {
                return_id = all_id[all_id.length - 1] + 1;
            } else {
                return_id = 0;
            }

        }

        return return_id;
    }


    /**
     * 自动关联
     *
     * @param menu_index 日期和餐类型的拼接:201404031
     * @throws InterruptedException
     */
    public void auto_match(long menu_index) throws InterruptedException {

        final String[][] matchFailedLastNameIdList = new String[500][2];//在自动关联之后还是没有关联成果的列表
        int[] idListSuccess_AfterAutoMatch = new int[500];
        int id_success_index = 0;

        String matchFailedNameWidgettIdList[][] = null;//存储自动关联之前未关联的菜品列表
        int no_match_index = 0;


        for (int i = 0; i < idListSuccess_AfterAutoMatch.length; i++) {
            idListSuccess_AfterAutoMatch[i] = -1;
        }
        //1、弹出窗口初始化
        //dialog_progress_bar_init();

        //2、获取到当前正在修改的菜单
        QueryBuilder qb = menuDetailDao.queryBuilder();
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(menu_index)));
        List listResult = qb.list();
        dialog_progress_bar_set(false, 0, 1, "准备读取当前菜单");

        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(menu_index + 20000));
        List listResultTmp = qbTmp.list();
        if (listResultTmp.size() > 0) {
            listResult = listResultTmp;
        }
        dialog_progress_bar_set(false, 1, 1, "解析菜单成功");


        //3、查看当前的关联情况，得到未关联列表
        List<MenuDetail> list_no_match = new ArrayList<MenuDetail>(listResult.size());
        matchFailedNameWidgettIdList = new String[listResult.size()][2];


        boolean flag_all_match = true;
        dialog_progress_bar_set(false, 0, listResult.size(), "准备查看关联情况");


        for (int i = 0; i < listResult.size(); i++) {
            MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);

            String bind_info = menu_detail_entity.getRedundance1();//关联是否为空、套餐（1）、单品（0）
            //int bind_to_server_id = menu_detail_entity.getBindToItemServerId()==null?-1: menu_detail_entity.getBindToItemServerId();//关联的菜品id（在服务器上的id）
            String bind_name = menu_detail_entity.getName();


            dialog_progress_bar_set(true, i, listResult.size(), "正在查看关联情况");
            if (
                    bind_info == null
                            || bind_info.equals("")
                            && bind_name != null
                            && menu_detail_entity.getType().equals("text")) {
                flag_all_match = false;
                list_no_match.add(menu_detail_entity);
                matchFailedNameWidgettIdList[no_match_index][0] = bind_name;
                matchFailedNameWidgettIdList[no_match_index][1] = Integer.toString(menu_detail_entity.getWidgetId());
                no_match_index++;

            }
        }
        String nameAll = "";

        for (int i = 0; i < matchFailedNameWidgettIdList.length; i++) {
            if (matchFailedNameWidgettIdList[i][0] == null || matchFailedNameWidgettIdList[i][0].equals("")) {
                break;
            }
            nameAll += matchFailedNameWidgettIdList[i][0] + "\n";
            CommonUtils.LogWuwei(tag, matchFailedNameWidgettIdList[i][0] + "未关联");
        }


        dialog_progress_bar_set(false, listResult.size(), listResult.size(), "查看本地菜单信息完毕");
        dialog_progress_bar_set(false, listResult.size(), listResult.size(), "共发现" + (no_match_index) + "未关联菜品");
        dialog_progress_bar_set(false, listResult.size(), listResult.size(), "未关联列表\n:" + nameAll);


        //4、将电视菜单的菜品名称和服务器菜单菜品名称一样的进行关联
        if (!flag_all_match) {
            int max = 2 * (no_match_index + 1);
            dialog_progress_bar_set(false, 0, max, "共发现" + (no_match_index + 1) + "个菜品未关联到服务器:");
            if (serverComboNameId != null)//将套餐自动关联
            {
                dialog_progress_bar_set(false, 1, max, "匹配服务器套餐列表中。。。");
                for (int i = 0; i < list_no_match.size(); i++) {
                    MenuDetail menu_detail_entity = list_no_match.get(i);
                    if (menu_detail_entity == null) {
                        break;
                    }

                    boolean flag_combo_match = false;
                    for (int k = 0; k < serverComboNameId.length; k++) {
                        if (serverComboNameId[k][0] == null) {
                            break;
                        }

                        if (menu_detail_entity.getName().equals(serverComboNameId[k][0])) {
                            flag_combo_match = true;
                            idListSuccess_AfterAutoMatch[id_success_index] = menu_detail_entity.getWidgetId();
                            id_success_index++;
                            menu_detail_entity.setRedundance1("1");
                            menu_detail_entity.setBindToItemServerId(Integer.parseInt(serverComboNameId[k][1]));

                            Message msg = new Message();
                            msg.what = UPDATE_MATCH_DATABASE;
                            msg.obj = menu_detail_entity;
                            handler.sendMessage(msg);

                            dialog_progress_bar_set(false, 1 + i, max, "和服务器套餐关联进度:" + menu_detail_entity.getName() + "已经关联到" + (serverComboNameId[k][0]));

                            break;
                        }
                    }
                    if (!flag_combo_match) {
                        dialog_progress_bar_set(false, 1, max, "和服务器套餐关联进度:" + menu_detail_entity.getName() + "关联套餐失败");
                    }

                }

            }

            if (serverSingleNameId != null) {
                dialog_progress_bar_set(false, 1, max, "匹配服务器单品列表中。。。");
                for (int i = 0; i < list_no_match.size(); i++) {
                    MenuDetail menu_detail_entity = list_no_match.get(i);
                    if (menu_detail_entity == null) {
                        break;
                    }

                    boolean flag_single_match = false;
                    for (int k = 0; k < serverSingleNameId.length; k++) {
                        if (serverSingleNameId[k][0] == null) {
                            break;
                        }

                        if (menu_detail_entity.getName().equals(serverSingleNameId[k][0])) {
                            idListSuccess_AfterAutoMatch[id_success_index] = menu_detail_entity.getWidgetId();
                            id_success_index++;
                            flag_single_match = true;
                            menu_detail_entity.setRedundance1("0");
                            menu_detail_entity.setBindToItemServerId(Integer.parseInt(serverSingleNameId[k][1]));

                            Message msg = new Message();
                            msg.what = UPDATE_MATCH_DATABASE;
                            msg.obj = menu_detail_entity;
                            handler.sendMessage(msg);

                            dialog_progress_bar_set(false, 1, max, "和服务器单品关联进度\n:" + menu_detail_entity.getName() + "已经关联到" + (serverSingleNameId[k][0]));
                            break;
                        }
                    }

                    if (!flag_single_match) {
                        dialog_progress_bar_set(false, 1, max, "和服务器套餐关联进度:" + menu_detail_entity.getName() + "关联单品失败");
                    }

                }

            }

            int index = 0;
            for (int i = 0; i < matchFailedNameWidgettIdList.length; i++) {
                if (matchFailedNameWidgettIdList[i][0] == null
                        || matchFailedNameWidgettIdList[i][0].equals("")
                        || matchFailedNameWidgettIdList[i][1].equals("")
                        || matchFailedNameWidgettIdList[i][1] == null) {
                    break;
                }

                boolean flag_add = true;//是否添加到"最终的未关联列表"的标志位

                for (int k = 0; k < idListSuccess_AfterAutoMatch.length; k++) {
                    if (idListSuccess_AfterAutoMatch[k] == -1) {
                        break;
                    }
                    if (idListSuccess_AfterAutoMatch[k] == Integer.parseInt(matchFailedNameWidgettIdList[i][1])) {
                        flag_add = false;
                    }
                }

                if (flag_add) {
                    matchFailedLastNameIdList[index][0] = matchFailedNameWidgettIdList[i][0];
                    matchFailedLastNameIdList[index][1] = matchFailedNameWidgettIdList[i][1];
                    index++;
                }
            }


            //手工进行关联"自动关联后失败的菜品项目"

            Message msg = new Message();
            msg.what = SHOW_HANDLE_BIND_DIALOG;
            msg.obj = matchFailedLastNameIdList;
            handler.sendMessage(msg);


        }


    }


    /**
     * 处理自动关联后的结果（弹窗，让用户进行手工关联）
     *
     * @param obj
     */
    public void dealMatchResult(Object obj) {
        final String matchFailedLastNameIdList[][] = (String[][]) obj;
        int index = 0;
        for (int i = 0; i < matchFailedLastNameIdList.length; i++) {
            if (matchFailedLastNameIdList[i][0] == null
                    || matchFailedLastNameIdList[i][0].equals("")
                    || matchFailedLastNameIdList[i][1] == null
                    || matchFailedLastNameIdList[i][1].equals("")) {
                index = i;
                break;
            }
            CommonUtils.LogWuwei(tag, "matchFailedLastNameIdList[" + i + "][0] is " + matchFailedLastNameIdList[i][0]);
            CommonUtils.LogWuwei(tag, "matchFailedLastNameIdList[" + i + "][1] is " + matchFailedLastNameIdList[i][1]);
        }

        final String[] nameListview = new String[index];
        for (int i = 0; i < matchFailedLastNameIdList.length; i++) {
            if (i == index) {
                break;
            }
            if (matchFailedLastNameIdList[i][0] != null) {
                nameListview[i] = matchFailedLastNameIdList[i][0];
            }
            CommonUtils.LogWuwei(tag, "index is " + index + " nameListview[" + i + "] is " + nameListview[i]);
        }

        CommonUtils.LogWuwei(tag, "---ready----");
        for (int i = 0; i < nameListview.length; i++) {
            CommonUtils.LogWuwei(tag, "-------" + nameListview[i]);
        }
        CommonUtils.LogWuwei(tag, "---done----");

        AlertDialog.Builder ab = new AlertDialog.Builder(NewFromHistoryMenuActivity.this);

        ab.setCancelable(false)
                .setTitle("以下菜品自动关联失败\n(如需手工关联，请单击列表;否则，点击确认完成自动关联)")
                .setMultiChoiceItems(nameListview, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            added_type = "text";
                            added_item_name = matchFailedLastNameIdList[which][0];
                            button_new_choosen = (Button) findViewById(Integer.parseInt(matchFailedLastNameIdList[which][1]));
                            showBindInfo(added_item_name, null, Integer.parseInt(matchFailedLastNameIdList[which][1]), false);
                        } else {
                            QueryBuilder qb = menuDetailDao.queryBuilder();
                            Long id = menu_id_now_choose + 20000;
                            qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id)),
                                    MenuDetailDao.Properties.WidgetId.eq(Integer.parseInt(matchFailedLastNameIdList[which][1])));
                            List listResult = qb.list();
                            if (listResult.size() > 0) {
                                for (int i = 0; i < listResult.size(); i++) {
                                    MenuDetail menu_detail_entity = (MenuDetail) listResult.get(0);
                                    menu_detail_entity.setBindToItemServerId(null);
                                    menu_detail_entity.setRedundance1("");

                                    MenuDetail save_menu_detail_entity = menu_detail_entity;

                                    //删除同一日期内widget_id相同的行
                                    DeleteQuery<MenuDetail> dq =
                                            qb.where(
                                                    MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id)),
                                                    MenuDetailDao.Properties.WidgetId.eq(Integer.parseInt(matchFailedLastNameIdList[which][1])))
                                                    .buildDelete();

                                    dq.executeDeleteWithoutDetachingEntities();

                                    menuDetailDao.insertOrReplace(save_menu_detail_entity);
                                }
                            }
                        }
                    }
                })
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                            }
                        })
                .show();//显示对话框   

    }


    /**
     * 显示现在关联的结果列表
     */
    public void showMatchResultDialog() {
		/*AlertDialog.Builder ab = new AlertDialog.Builder(NewFromHistoryMenuActivity.this);
		
		//List<String> list = new ArrayList<String>();
		
		String[] list = new String[]{};
		
		QueryBuilder qb = menuDetailDao.queryBuilder();
		//删除同一日期内widget_id相同的行
			DeleteQuery<MenuDetail> dq = 
					qb.where(
							MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
							MenuDetailDao.Properties.WidgetId.eq(widgetId)).buildDelete();
  		dq.executeDeleteWithoutDetachingEntities();
		
		
		ab.setCancelable(false)   
		.setTitle("关联结果:")
		.setItems(new String[]{"1"}, null)
		.setPositiveButton("确认",   
		new DialogInterface.OnClickListener(){   
			public void onClick(DialogInterface dialoginterface, int i){   
				dialoginterface.dismiss();   
			}   
		})   
		.show();//显示对话框 
*/
    }


    /**
     * 弹出窗口初始化
     */
    public void dialog_progress_bar_init() {
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());

        final View DialogView = factory.inflate(R.layout.circular_progress_bar, null);

        if (dlg_prrgress_bar == null) {
            dlg_prrgress_bar = new Dialog(NewFromHistoryMenuActivity.this);
        }
        dlg_prrgress_bar.setContentView(DialogView);
        dlg_prrgress_bar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg_prrgress_bar.setCanceledOnTouchOutside(false);

        dlg_prrgress_bar.show();
        dlg_prrgress_bar.setCancelable(false);

        if (mRateTextCircularProgressBar == null) {
            mRateTextCircularProgressBar = (RateTextCircularProgressBar) dlg_prrgress_bar.findViewById(R.id.client_menu_update_progress_bar);
        }

        mRateTextCircularProgressBar.setMax(100);
        mRateTextCircularProgressBar.setTextColor(Color.WHITE);
        mRateTextCircularProgressBar.getCircularProgressBar().setCircleWidth(20);
    }


    /***
     * 更改窗口标题和进度
     *
     * @param progress：当前进度 title:窗口的标题
     * @throws InterruptedException
     */
    public void dialog_progress_bar_set(boolean flag_show_progress, int progress, int total, String title) throws InterruptedException {

        //   if(dlg_prrgress_bar != null && mRateTextCircularProgressBar != null)
        {

            Thread.sleep(10);
            CommonUtils.LogWuwei(tag, "dialog_progress_bar_set:" + "progress is " + progress + "\n\ntitle is " + title);
            //dlg_prrgress_bar.setTitle(title);

            Bundle B = new Bundle();
            B.putInt("progress", progress);
            B.putInt("max", total);
            B.putString("title", title);
            B.putBoolean("flag_show_progress", flag_show_progress);

            Message msg = new Message();
            msg.setData(B);
            msg.what = UPDATE_MATCH_PROGRESS;
            handler.sendMessage(msg);
        }

    }


    /**
     * 将当前的菜单保存。
     * 保存的过程：
     * 1、删除真实数据
     * 2、将临时数据(201432221)直接拷贝到真实数据(201412221)
     * 3、删除临时数据
     */
    public static void save() {
        //步骤1 删真实数据
        CommonUtils.LogWuwei(tag, "步骤1 删真实数据");
        QueryBuilder qb = menuDetailDao.queryBuilder();
        Long id = menu_id_now_choose;
        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id)));
        List listResult = qb.list();
        if (listResult.size() > 0) {
            DeleteQuery<MenuDetail> dq =
                    qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id)))
                            .buildDelete();
            CommonUtils.LogWuwei(tag, "步骤1 删真实数据,准备删除日期为" + id + "的菜品");
            dq.executeDeleteWithoutDetachingEntities();
        } else {
            CommonUtils.LogWuwei(tag, "id is " + menu_id_now_choose + "listResult size is " + listResult.size());
        }

        //步骤2，将临时数据拷贝到真实数据
        CommonUtils.LogWuwei(tag, "步骤2，将临时数据拷贝到真实数据");
        QueryBuilder qb_tmp = menuDetailDao.queryBuilder();
        Long id_tmp = menu_id_now_choose + 20000;

        qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id_tmp)));
        List listResultTmp = qb_tmp.list();


        for (int i = 0; i < listResultTmp.size(); i++) {
            CommonUtils.LogWuwei(tag, "步骤2，将临时数据拷贝到真实数据中，new id is " + id);
            MenuDetail menu_detail_entity_tmp = (MenuDetail) listResultTmp.get(i);

            MenuDetail menu_detail_entity = menu_detail_entity_tmp;
            menu_detail_entity.setMenuDateId(Long.toString(id));

            menuDetailDao.insertOrReplace(menu_detail_entity);
        }

        //步骤3，删除临时数据
        CommonUtils.LogWuwei(tag, "步骤3，删除临时数据");
        if (listResultTmp.size() > 0) {
            DeleteQuery<MenuDetail> dq =
                    qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id_tmp)))
                            .buildDelete();
            CommonUtils.LogWuwei(tag, "步骤3，删除临时数据中");
            dq.executeDeleteWithoutDetachingEntities();

            HandlerUtils.showToast(context, "保存成功");
        }

    }


    public static void saveAs() {

    }


    public static void showMenuInfo() {

    }


    /**
     * 显示加载中的窗口
     */
    public void showLoadingDialog(String text) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.loading_layout, null);

        TextView tv = (TextView) grid.findViewById(R.id.textview_loading_content);
        tv.setText(text);

        ImageView iv = (ImageView) grid.findViewById(R.id.imageview_loading_pic);
        iv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_loading));


        if (pop_loading == null) {
            pop_loading = new PopupWindow(grid, 2048, 1536, true);
        } else {
            pop_loading.setContentView(grid);
        }

        pop_loading.setFocusable(true);
        pop_loading.setOutsideTouchable(true);
        pop_loading.setAnimationStyle(R.style.AutoDialogAnimation);
        pop_loading.setBackgroundDrawable(new BitmapDrawable());
        if (hasFocus) {
            pop_loading.showAtLocation(rl_menu, Gravity.NO_GRAVITY, 0, 0);
        }

    }

    /**
     * 隐藏加载中的窗口
     */
    public void hideLoadingDialog() {

        if (pop_loading != null) {
            if (pop_loading.isShowing()) {
                pop_loading.dismiss();
            }
        }

    }


    /**
     * 显示异常的窗口
     *
     * @param msg:错误内容
     * @param result:-1->退回到登录界面 0-》停留在当前窗口 1-》去设置界面
     */
    public void showDialogError(String msg, final int result) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);

        TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
        tvContent.setText(msg);

        final Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);

        if (result == 1) {
            btn_close.setText("设置");
        }

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (v.getId() == btn_close.getId()) {
                    if (dialog_show_error != null) {
                        if (dialog_show_error.isShowing()) {
                            dialog_show_error.dismiss();
                        }
                    }
                    if (result == -1) {
                        startActivity(new Intent(NewFromHistoryMenuActivity.this, LoginActivity.class));
                        finish();
                    } else if (result == 1) {
                        startActivity(new Intent(NewFromHistoryMenuActivity.this, SettingsActivity.class));
                        finish();
                    }
                }

            }
        };
        btn_close.setOnClickListener(ocl);

        if (dialog_show_error == null) {
            dialog_show_error = new PopupWindow(grid, 2048, 1536, true);
        } else {
            dialog_show_error.setContentView(grid);
        }

        dialog_show_error.setFocusable(true);
        dialog_show_error.setOutsideTouchable(true);
        dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
        dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
        dialog_show_error.showAtLocation(rl_menu, Gravity.NO_GRAVITY, 0, 0);

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //Toast.makeText(this, "再按一次退出app", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("确定离开？", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });

                builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                builder.setTitle("提示窗口");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void showBindListView(final Dialog dlgSetTitle, final String title, final int flag) {
        // 实例化汉字转拼音类
        LayoutInflater factory = LayoutInflater.from(context);
        final View DialogView = factory.inflate(R.layout.activity_listview_sort_main, null);

//	    final AlertDialog dlg = null;
        ListView listview = null;
        EditText et_num = null;

        final AlertDialog dlg = new AlertDialog.Builder(new ContextThemeWrapper(NewFromHistoryMenuActivity.this, android.R.style.Theme_Holo_Light))
                .setView(DialogView)
                .create();
        dlg.show();
        dlg.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dlgSetTitle.setTitle(title);
                    bind_id = -1;
                }
                return false;
            }
        });


        sortSideBar = (SideBar) dlg.findViewById(R.id.sidrbar);
        sortDialogChoosenTips = (TextView) dlg.findViewById(R.id.dialog);
        sortSideBar.setTextView(sortDialogChoosenTips);

        // 设置右侧触摸监听
        sortSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = sortAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) dlg.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) sortAdapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                if (flag == 0) {
                    for (int i = 0; i < serverComboNameId.length; i++) {
                        if (serverComboNameId[i][0].equals(((SortModel) sortAdapter.getItem(position)).getName())) {
                            bind_id = Integer.parseInt(serverComboNameId[i][1]);
                            if (dlgSetTitle != null) {
                                dlgSetTitle.setTitle("确定要将\"" + title + "\"关联到\"" + serverComboNameId[i][0] + "\"吗？");
                            }
                            dlg.dismiss();
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < serverSingleNameId.length; i++) {
                        if (serverSingleNameId[i][0].equals(((SortModel) sortAdapter.getItem(position)).getName())) {
                            bind_id = Integer.parseInt(serverSingleNameId[i][1]);
                            if (dlgSetTitle != null) {
                                dlgSetTitle.setTitle("确定要将\"" + title + "\"关联到\"" + serverSingleNameId[i][0] + "\"吗？");
                            }
                            dlg.dismiss();
                            break;
                        }
                    }
                }


            }
        });

        if (flag == 0) {
            //得到套餐列表的有效长度
            int length = 0;
            for (int i = 0; i < serverComboNameId.length; i++) {
                if (serverComboNameId[i][0] == null) {
                    length = i;
                    break;
                }
            }


            String[] comboName = new String[length];
            for (int i = 0; i < length; i++) {
                if (serverComboNameId[i][0] != null) {
                    comboName[i] = serverComboNameId[i][0];
                    CommonUtils.LogWuwei("copy", "copty t0 one array :" + comboName[i]);
                } else {
                    break;
                }
            }

            SourceDateList = filledData(comboName);//getResources().getStringArray(comboName));//R.array.menu_type));

        } else {
            //得到套餐列表的有效长度
            int length = 0;
            for (int i = 0; i < serverSingleNameId.length; i++) {
                if (serverSingleNameId[i][0] == null) {
                    length = i;
                    CommonUtils.LogWuwei("copySingle", "length is " + length);
                    break;
                }
            }


            String[] singleName = new String[length];
            for (int i = 0; i < length; i++) {
                if (serverSingleNameId[i][0] != null) {
                    singleName[i] = serverSingleNameId[i][0];
                    CommonUtils.LogWuwei("copySingle", "copty t0 one array :" + singleName[i]);
                } else {
                    break;
                }
            }

            SourceDateList = filledData(singleName);//getResources().getStringArray(comboName));//R.array.menu_type));
        }

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        sortAdapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(sortAdapter);

        sortClearEditText = (ClearEditText) dlg.findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        sortClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date) {

        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        sortAdapter.updateListView(filterDateList);
    }


    public void copyDataDeal(long fromDate, long ToDate, int menuType) {
        CommonUtils.LogWuwei(tag, "1fromDate is " + fromDate);
        if (fromDate > 100000000) {
            fromDate = fromDate / 10;
        }
        CommonUtils.LogWuwei(tag, "2fromDate is " + fromDate);

        if (ToDate > 100000000) {
            CommonUtils.LogWuwei(tag, "toDate is " + ToDate);
            ToDate = ToDate / 10;
        }

        if (fromDate == ToDate) {
            HandlerUtils.showToast(context, "不允许拷贝到自己");
            return;
        }

        CommonUtils.LogWuwei(tag, "toDate is " + ToDate);

        if (menuType > -1 && menuType < 3) {
            QueryBuilder qb = menuDetailDao.queryBuilder();
            Long id = fromDate * 10 + menuType;
            qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id)));
            List listResult = qb.list();

            QueryBuilder qb_tmp = menuDetailDao.queryBuilder();
            Long id_tmp = fromDate * 10 + menuType + 20000;
            qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(id_tmp)));
            List listResultTmp = qb_tmp.list();
            if (listResultTmp.size() > 0) {
                CommonUtils.LogWuwei(tag, "会拷贝临时数据");
                listResult = listResultTmp;
            }


            CommonUtils.LogWuwei(tag, "ToDate is " + ToDate + " menuType is " + menuType);
            Long destaintanceDate = ToDate * 10 + menuType + 20000;
            CommonUtils.LogWuwei(tag, "destaintanceDate is " + destaintanceDate);

            QueryBuilder qb_de = menuDetailDao.queryBuilder();
            DeleteQuery<MenuDetail> dq =
                    qb_de.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(ToDate * 10 + menuType)))
                            .buildDelete();
            dq.executeDeleteWithoutDetachingEntities();

            QueryBuilder qb_de_tmp = menuDetailDao.queryBuilder();
            DeleteQuery<MenuDetail> dqTmp =
                    qb_de_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(destaintanceDate)))
                            .buildDelete();
            dqTmp.executeDeleteWithoutDetachingEntities();

            for (int i = 0; i < listResult.size(); i++) {
                MenuDetail menu_detail_entity_tmp = (MenuDetail) listResult.get(i);
                MenuDetail new_menu_detail_entity = menu_detail_entity_tmp;
                new_menu_detail_entity.setMenuDateId(Long.toString(destaintanceDate));
                menuDetailDao.insert(new_menu_detail_entity);
            }

        }
    }


    /***
     * 设置显示菜单用的ip地址
     */
    public void menuSetting() {

        final EditText et = new EditText(context);
        //et.setTextColor(Color.BLACK);
        et.setBackgroundColor(Color.TRANSPARENT);

        et.setInputType(InputType.TYPE_CLASS_NUMBER);

        String digits = "0123456789.";
        et.setKeyListener(DigitsKeyListener.getInstance(digits));


        if (ip_address != "" && CommonUtils.isIp(ip_address)) {
            et.setText(ip_address);
        } else {
            et.setText("192.168.1.1");
        }

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, s.toString());
                if (CommonUtils.isIp(s.toString())) {
                    ip_address = s.toString();
                    LocalDataDeal.writeToLocalMenuIp(ip_address, context);
                }
            }
        });

        builderSetMenuIp.setTitle("填写显示菜单的电视IP地址");
        builderSetMenuIp.setMessage("设置显示菜单的电视的IP地址");
        builderSetMenuIp.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                InputMethodUtils.TimerHideKeyboard(et);
            }
        });
        builderSetMenuIp.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                InputMethodUtils.TimerHideKeyboard(et);
            }
        });

        builderSetMenuIp.setView(et);
        builderSetMenuIp.show();
    }


    /****
     * 获取营业时间段列表
     */
    public void getTimeBucketList() {
        CommonUtils.sendMsg("获取营业时间段...", SHOW_LOADING_TEXT, handler);
        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                list_meal_bucket = (List<MealBucket>) object;
                CommonUtils.sendMsg("", HIDE_LOADING, handler);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", HIDE_LOADING, handler);
                CommonUtils.sendMsg(response.error_message, SHOW_ERROR_MESSAGE, handler);
            }
        });
    }


}










