package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;
import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.support.greenDao.MenuTableDao;
import com.huofu.RestaurantOS.support.niftyDialog.Effectstype;
import com.huofu.RestaurantOS.support.niftyDialog.NiftyDialogBuilder;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.FIleUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.NetWorkUtils;
import com.huofu.RestaurantOS.utils.daoUtils;
import com.huofu.RestaurantOS.widget.RateTextCircularProgressBar;
import com.huofu.RestaurantOS.widget.SystemBarTintManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/****
 * 菜单编辑的导航界面 可以选择浏览、编辑、电视ip编辑
 */
public class ClientMenuSplashActivty extends BaseActivity {

    public static String tag = "ClientMenuSplashActivty";
    public static Context ctxt = null;
    public static boolean active = false;
    public static List<MealBucket> list_meal_bucket = new ArrayList<MealBucket>();

    public static ImageButton imagebutton_client_menu__index_back;
    public static ImageButton imagebutton_client_menu_index_scan;
    public static ImageButton imagebutton_splash_add;
    public static ImageButton imagebutton_splash_update;
    public static ImageButton imagebutton_splash_set_ip;

    public static Button button_client_menu_index_back;

    public static Button button_client_menu_index_scan;
    public static Button button_client_menu_index_add;
    public static Button button_client_menu_index_modify;

    public static BitmapUtils bitmapUtils;
    public static BitmapDisplayConfig bigPicDisplayConfig;
    public static DefaultBitmapLoadCallBack<ImageView> callback;// 图片加载的回调函数
    public static String pathLoginStaffCache = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "huofu"
            + File.separator + "ImageCache" + File.separator;

    boolean hasFocus = false;

    public static OnClickListener backListner;

    public SystemBarTintManager tintManager = null;
    Handler handler = null;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    public static MenuTableDao menuTableDao;
    public static MenuDetailDao menuDetailDao;

    public static boolean flag_clear_tv_screen = true;
    public String detail_item = null;
    List listResultPrieview = null;
    String[] date = null;
    RateTextCircularProgressBar mRateTextCircularProgressBar = null;
    Dialog dlg_prrgress_bar = null;

    public static String[] fontName = {"pianpina.ttf", "Chalkboard.ttf", "Chalkduster.ttf", "digital-7.ttf"};
    public static String pathDataBase = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "data" + File.separator;
    public static String pathFonts = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "fonts" + File.separator;
    public int index = 0;


    public static final int UPDATE_PROGRESS_BAR = 0;
    public static final int HIDE_LOADING = 1;// 关闭窗口（加载进度）
    public static final int SHOW_LOADING_TEXT = 2;// 显示窗口（加载进度）
    public static final int SHOW_ERROR_MESSAGE = 3;
    public static final int SHOW_TOAST = 4;

    public static int menu_list_int[] = new int[3];
    public static int menu_list_int_local[] = {3, 1, 2};
    public String ip_address = "";

    /*********************************
     * activity生命周期
     **********************************/
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        CommonUtils.LogWuwei(tag, "onStart");
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
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        CommonUtils.LogWuwei(tag, "onCreate");

        setContentView(R.layout.client_menu_splash);

        ViewUtils.inject(this);

        init();

        widgetConfigure();

        tabConfigure();

        getTimeBucketList();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
    }

    /*********************************
     * 初始化和基本控件配置
     **********************************/
    public void init() {

        MainApplication.setmActivity(this);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor("#f0f0f0"));
        //tintManager.setStatusBarDarkMode(true, this);

        ctxt = getApplicationContext();
        daoMaster = daoUtils.getDaoMaster(ctxt);
        daoSession = daoUtils.getDaoSession(ctxt);
        menuTableDao = daoSession.getMenuTableDao();
        menuDetailDao = daoSession.getMenuDetailDao();

        //	imagebutton_client_menu_index_scan = (ImageButton) findViewById(R.id.imageButton_clientMenuSplash_scan);
        //button_client_menu_index_scan = (Button) findViewById(R.id.button_clientMenuSplash_scan);

        imagebutton_splash_add = (ImageButton) findViewById(R.id.imagebutton_splash_add);
        imagebutton_splash_update = (ImageButton) findViewById(R.id.imagebutton_splash_update);
        imagebutton_splash_set_ip = (ImageButton) findViewById(R.id.imagebutton_splash_set_ip);

        bitmapUtils = new BitmapUtils(getApplicationContext(), pathLoginStaffCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用,图片太大时容易OOM。


    }

    /**
     * 控件配置
     */
    public void widgetConfigure() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏

        imagebutton_splash_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                askThisOrNextWeek();
            }
        });

        imagebutton_splash_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //如果电视的ip不等于路由器的ip地址且处于alive状态
                client_menu_to_tv();
            }
        });

        imagebutton_splash_set_ip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // NetWorkUtils.menuSetting();
                ip_address = LocalDataDeal.readFromLocalMenuIp(ctxt);
                final EditText et = new EditText(ctxt);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);

                String digits = "0123456789.";
                et.setKeyListener(DigitsKeyListener.getInstance(digits));


                et.setBackgroundColor(Color.TRANSPARENT);
                if (ip_address != "" && CommonUtils.isIp(ip_address)) {
                    et.setText(ip_address);
                } else {
                    et.setText("192.168.1.1");
                }

                et.setSelection(et.getText().toString().length());

                et.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        CommonUtils.LogWuwei(tag, s.toString());
                        if (CommonUtils.isIp(s.toString())) {
                            ip_address = s.toString();
                            LocalDataDeal.writeToLocalMenuIp(ip_address, ctxt);
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ClientMenuSplashActivty.this);
                builder.setTitle("填写显示菜单的电视IP地址");
                builder.setMessage("设置显示菜单的电视的IP地址");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                InputMethodUtils.TimerHideKeyboard(et);
                                InputMethodUtils.HideKeyboard(et);
                                dialog.dismiss();

                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                InputMethodUtils.TimerHideKeyboard(et);
                                InputMethodUtils.HideKeyboard(et);
                                dialog.dismiss();

                            }
                        });

                builder.setView(et);

                AlertDialog alert = builder.create();
                CommonUtils.LogWuwei(tag, "before  show");
                alert.show();
                CommonUtils.LogWuwei(tag, "after  show");
                alert.setCanceledOnTouchOutside(false);
            }
        });

        handler = mUiHandler;
        for (int i = 0; i < menu_list_int.length; i++) {
            menu_list_int[i] = -1;
        }
        launchPadConfigure();
    }

    @Override
    protected void dealWithmessage(Message msg) {
        switch (msg.what) {
            case UPDATE_PROGRESS_BAR:
                int progress = Integer.parseInt((String) msg.obj);
                if (mRateTextCircularProgressBar != null) {
                    mRateTextCircularProgressBar.setProgress(progress);
                    if (progress >= 90) {
                        dlg_prrgress_bar.dismiss();
                        HandlerUtils.showToast(ctxt, "菜单同步完毕");
                        progress = 0;
                    }
                }
                break;
            case SHOW_LOADING_TEXT:
                String content = (String) msg.obj;
                CommonUtils.LogWuwei("time1", content + " received");
                showLoadingDialog(content);
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case SHOW_ERROR_MESSAGE:
                String errMsg = (String) msg.obj;
                showDialogErrorOneOption(errMsg);
                break;
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, (String) msg.obj);
                break;
            default:
                break;
        }
    }

    public void tabConfigure() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_client_menu_splash_layout);
        actionBar.setDisplayShowHomeEnabled(false);

        View grid = actionBar.getCustomView();
        RelativeLayout rl = (RelativeLayout) grid.findViewById(R.id.rl_action_bar_client_menu_splash);
        Button btn = (Button) grid.findViewById(R.id.btn_action_bar_client_menu_splash);

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mslidingMenu.toggleLeftDrawer();
                widgetCofigureNavigationBar(1);
            }
        };

        rl.setOnClickListener(ocl);
        btn.setOnClickListener(ocl);

    }

    /**
     * 判断需要的字体是否存在
     *
     * @return
     */
    public boolean judgeFontsExists() {
        FIleUtils fu = new FIleUtils(getApplicationContext());
        boolean flag = true;
        for (int i = 0; i < fontName.length; i++) {
            if (!fu.isExist(pathFonts + fontName[i])) {
                flag = false;
                CommonUtils.LogWuwei(tag, "字体库" + fontName[i] + "不存在");
            }
        }
        if (flag) {
            CommonUtils.LogWuwei(tag, "字体库完整，不需要下载");
        }
        if (!flag) {
            downloadFonts();
        }
        return flag;
    }

    /**
     * 从网络下载需要的字体
     */
    public void downloadFonts() {

        FIleUtils fu = new FIleUtils(getApplicationContext());

        for (int i = 0; i < fontName.length; i++) {
            fu.deleteFile(pathFonts + fontName[i]);
        }
        String pathPrefix = "http://7vzso4.com1.z0.glb.clouddn.com/";

        HttpUtils http = new HttpUtils();

        index = 0;

        for (index = 0; index < fontName.length; index++) {
            String path = pathPrefix + fontName[index];
            String nowDownloadFontName = pathFonts + fontName[index];
            http.download(path, pathFonts + fontName[index], new RequestCallBack<File>() {

                @Override
                public void onStart() {
                    CommonUtils.LogWuwei(tag, "开始下载...");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    CommonUtils.LogWuwei(tag, "下载进度:" + ((current * 100) / total) + "%");
                    String progress = "正在下载必须的字体文件：已下载" + ((current * 100) / total) + "%";
                    HandlerUtils.showToast(ctxt, progress);
                    CommonUtils.LogWuwei(tag, "downloading " + index + "  " + progress);
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    CommonUtils.LogWuwei(tag, "下载成功，文件已经保存至:" + responseInfo.result.getPath());

                    String progress = "下载成功，文件已经保存至:" + responseInfo.result.getPath();

                    if (responseInfo.result.getPath().equals(pathFonts + "pianpina.ttf")) {
                        HandlerUtils.showToast(ctxt, "初始化完毕，谢谢您的耐心等待");
                        CommonUtils.LogWuwei(tag, "初始化完毕，谢谢您的耐心等待");
                    } else {
                        HandlerUtils.showToast(ctxt, progress);
                    }

                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    CommonUtils.LogWuwei(tag, "下载失败:" + msg + error);
                }
            });
        }
    }


    /********************************* 逻辑实现**********************************/
    /**
     * 同步数据到电视端
     */
    public void client_menu_to_tv() {
        CommonUtils.LogWuwei(tag, "askLocalOrClound start");
        final boolean flag = askLocalOrClound();
        CommonUtils.LogWuwei(tag, "askLocalOrClound finish result is " + flag);

    }

    /**
     * @param flag ->true 云端同步 flag->false 本地同步
     */
    public void doUpdate(final boolean flag) {
        Context ctxt = null;
        if (ClientMenuSplashActivty.this != null) {
            ctxt = ClientMenuSplashActivty.this;
        }

        if (ctxt != null
                && NetWorkUtils.isWifiConnected(getApplicationContext())) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                    .getInstance(ClientMenuSplashActivty.this);
            Effectstype effect;
            effect = Effectstype.SlideBottom;
            String msg = null;
            if (!flag) {
                msg = "本周菜单：你将同步\"本周菜单\"到电视\n\n下周菜单：你将同步\"下周菜单\"到电视";
            } else {
                msg = "本周菜单：你将同步\"本周菜单\"到云端\n\n下周菜单：你将同步\"下周菜单\"到云端";
            }

            dialogBuilder.withTitle("选择同步菜单日期")// .withTitle(null) no title
                    .withTitleColor("#FFFFFF") // def
                    .withDividerColor("#11000000") // def
                    .withMessageColor("#FFFFFF") // def
                    .withMessage(msg).isCancelableOnTouchOutside(true) // def |
                    // isCancelable(true)
                    .withDuration(800) // def
                    .withEffect(effect) // def Effectstype.Slidetop
                    .withButton1Text("本周菜单") // def gone
                    .withButton2Text("下周菜单") // def gone
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                            CommonUtils.LogWuwei("update",
                                    "cloundUpdateBoolean is " + flag);
                            updateMenu(true, flag);
                            dialog_progress_bar_show(0, flag);
                            dialogBuilder.deleteInstance();

                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    CommonUtils.LogWuwei("update",
                            "cloundUpdateBoolean is " + flag);
                    updateMenu(false, flag);
                    dialog_progress_bar_show(1, flag);
                    dialogBuilder.deleteInstance();

                }
            }).show();
        } else {
            Toast.makeText(getApplicationContext(), "请检查本机是否连接网络",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当ip地址不对时弹出对话框
     */
    public void client_menu_not_alive_dialog() {
        Context ctxt = null;
        if (ClientMenuSplashActivty.this != null) {
            ctxt = ClientMenuSplashActivty.this;
        }

        if (ctxt != null) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                    .getInstance(ClientMenuSplashActivty.this);
            Effectstype effect;
            effect = Effectstype.Shake;

            dialogBuilder
                    .withTitle("同步失败")
                            // .withTitle(null) no title
                    .withTitleColor("#FFFFFF")
                            // def
                    .withDividerColor("#11000000")
                            // def
                    .withMessageColor("#FFFFFF")
                            // def
                    .withMessage(
                            "可能失败原因：\n"
                                    + "1、电视没有开启或者启动菜单程序：请打开电视并开启程序\n"
                                    + "2、设置电视IP时，IP地址填写不正确:请正确设置ip（ip地址应该为显示菜单的电视IP）\n"
                                    + "3、本机没有连接网络:请打开wifi，并保证本机与电视在连接同一个wifi")
                    .isCancelableOnTouchOutside(true) // def |
                            // isCancelable(true)
                    .withDuration(300) // def
                    .withEffect(effect) // def Effectstype.Slidetop
                    .withButton1Text("确定") // def gone
                    .setButton1Click(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dialogBuilder.dismiss();
                            dialogBuilder.deleteInstance();
                        }
                    }).show();
        }
    }

    /**
     * 弹出窗口用于显示同步的进度
     *
     * @param flag ->0 本周 1->下周 cloundUpdate->true 同步到云端 false->同步到电视
     */
    public void dialog_progress_bar_show(int flag, boolean cloundUpdate) {
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());

        final View DialogView = factory.inflate(R.layout.circular_progress_bar, null);

        dlg_prrgress_bar = new Dialog(ClientMenuSplashActivty.this);
        dlg_prrgress_bar.setContentView(DialogView);
        dlg_prrgress_bar.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg_prrgress_bar.setCanceledOnTouchOutside(false);
        if (flag == 0) {
            if (cloundUpdate) {
                dlg_prrgress_bar.setTitle("正在同步\"本周菜单\"到云端");
            } else {
                dlg_prrgress_bar.setTitle("正在同步\"本周菜单\"到电视");
            }

        } else {
            if (cloundUpdate) {
                dlg_prrgress_bar.setTitle("正在同步\"下周菜单\"到云端");
            } else {
                dlg_prrgress_bar.setTitle("正在同步\"下周菜单\"到电视");
            }

        }
        dlg_prrgress_bar.show();
        dlg_prrgress_bar.setCancelable(false);
        try {
            Process proc = Runtime.getRuntime().exec("ls");
            InputStream inputstream = proc.getInputStream();

            InputStreamReader inputstreamreader = new InputStreamReader(
                    inputstream);

            BufferedReader bufferedreader = new BufferedReader(
                    inputstreamreader);
            String line = "";

            StringBuilder sb = new StringBuilder(line);

            while ((line = bufferedreader.readLine()) != null) {
                sb.append(line);
            }

            CommonUtils.LogWuwei(tag, "execute result is " + sb.toString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mRateTextCircularProgressBar = (RateTextCircularProgressBar) dlg_prrgress_bar
                .findViewById(R.id.client_menu_update_progress_bar);

        mRateTextCircularProgressBar.setMax(100);
        mRateTextCircularProgressBar.setTextColor(Color.WHITE);
        mRateTextCircularProgressBar.getCircularProgressBar()
                .setCircleWidth(20);
    }

    /**
     * 创建菜单
     *
     * @param flag -->0 本周菜单 1-->下周菜单
     */
    public void newWeekMenu(int flag) {

        if (judgeFontsExists()) {
            Intent intent = new Intent(ClientMenuSplashActivty.this, NewFromHistoryMenuActivity.class);
            intent.putExtra("this_next_week", Integer.toString(flag));
            ClientMenuSplashActivty.this.startActivity(intent);
        }


    }

    /********************************* 云端、现场同步菜单**********************************/
    /**
     * flag为true时同步本周菜单，false同步下周菜单
     */
    public void updateMenu(boolean thisWeekUpdate, boolean clounUpdate) {

        if (thisWeekUpdate) {
            CommonUtils.LogWuwei(tag, "同步本周菜单+clounUpdate is " + clounUpdate);
            date = CommonUtils.getThisWeekDate();
        } else {
            CommonUtils.LogWuwei(tag, "同步下周菜单+clounUpdate is " + clounUpdate);
            date = CommonUtils.getNextWeekDate();
        }
        updateMenuByDate(clounUpdate);
    }

    public void updateMenuByDate(final boolean clounUpdate) {

        new Thread() {
            public void run() {
                int progress = 0;
                String[] month = {"早餐","午餐","晚餐"};
                String[] day= {"周一","周二","周三","周四","周五","周六","周日"};
                for (int i = 0; i < 7; i++) {
                    for (int k = 0; k < list_meal_bucket.size(); k++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
                        }

                        progress++;

                        long detail_date = Long.parseLong(date[i]) * 10 + k;
                        boolean flag_show_tmp = false;
                        long final_date = detail_date;
                        String dateStr = date[i];

                        // 查看对应的menuTable表是否存在
                        QueryBuilder qbTmp = menuDetailDao.queryBuilder();
                        qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long
                                .toString(detail_date + 20000)));
                        final List listResultTmp = qbTmp.list();

                        if (listResultTmp.size() > 0) {
                            flag_show_tmp = true;
                            final_date = detail_date + 20000;
                        }

                        // 查看对应的menuTable表是否存在
                        QueryBuilder qb = menuDetailDao.queryBuilder();
                        qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long
                                .toString(detail_date)));
                        final List listResultNormal = qb.list();
                        flag_clear_tv_screen = true;

                        if (flag_show_tmp) {
                            listResultPrieview = listResultTmp;
                        } else {
                            listResultPrieview = listResultNormal;
                        }

                        if (listResultPrieview.size() == 0) {
                            CommonUtils.LogWuwei(tag, "本地" + detail_date + "没菜单，请先创建菜单");
                            CommonUtils.sendMsg(Integer.toString((int) (progress * 100 / (7*list_meal_bucket.size()))), UPDATE_PROGRESS_BAR, handler);
                            //sendUIMessage(SHOW_TOAST,day[i]+month[k]+"暂时没有菜单，你可以选择创建本周菜单");

                        } else if (listResultPrieview.size() > 0) {

                            CommonUtils.sendMsg(Integer.toString((int) (progress * 100 / (7*list_meal_bucket.size()))), UPDATE_PROGRESS_BAR, handler);
                            CommonUtils.LogWuwei("update", "clounUpdate is " + clounUpdate);

                            if (!clounUpdate) {
                                CommonUtils.LogWuwei("update", "同步到局域网电视端。。。");
                                updateMenuToLocalTv(final_date, flag_show_tmp);
                            } else {
                                CommonUtils.LogWuwei("update", "同步到云端。。。");
                                updateMenuToClound(final_date, k, flag_show_tmp);
                            }
                        }
                    }
                }
                CommonUtils.sendMsg("100", UPDATE_PROGRESS_BAR, handler);

            }
        }.start();

    }

    public void updateMenuToLocalTv(long final_date, boolean flag_show_tmp) {

        CommonUtils.LogWuwei("update", "本地同步菜单中。。。。");
        for (int p = 0; p < listResultPrieview.size(); p++) {

            MenuDetail menu_detail_entity = (MenuDetail) listResultPrieview
                    .get(p);

            String menu_id_str = Long.toString(final_date);// menu_detail_entity.getMenuDateId();
            String date_id = "";
            String widget_id = Integer.toString(menu_detail_entity
                    .getWidgetId());
            String type = menu_detail_entity.getType();
            String name = menu_detail_entity.getName();
            String x = Double.toString(menu_detail_entity.getX());
            String y = Double.toString(menu_detail_entity.getY());
            String price = Integer.toString(menu_detail_entity.getPrice());
            String font_size = Integer.toString(menu_detail_entity
                    .getFontSize());
            String font_color = menu_detail_entity.getFontColor();
            String background_color = menu_detail_entity.getBackgroundColor();
            double distance = menu_detail_entity.getDistance();
            String additiontal = menu_detail_entity.getAddtiontal();
            String bindType = menu_detail_entity.getRedundance1();
            int bindServerItemId = menu_detail_entity.getBindToItemServerId() == null ? -1
                    : menu_detail_entity.getBindToItemServerId();

            CommonUtils.LogWuwei(tag, "name is " + name + " bindType is " + bindType);

            if (bindType != null && !bindType.equals("")) {
                if (CommonUtils.isNumeric(bindType)) {
                    if (Integer.parseInt(bindType) == 1) {
                        CommonUtils.LogWuwei("bindType", name + "被绑定到套餐 id:"
                                + bindServerItemId);
                    } else {
                        CommonUtils.LogWuwei("bindType", name + "被绑定到单品 id:"
                                + bindServerItemId);
                    }
                }

            }

            CommonUtils.LogWuwei(tag, "___________menu_id_str is "
                    + menu_id_str + "widget_id is" + widget_id + "name is "
                    + name + "type is " + type);
            if (name != null) {
                name = URLEncoder.encode(name);
            }

            if (!background_color.equals("0x0000")
                    || !background_color.equals("0x000")) {
                background_color = "0x0000";
            }

            if (flag_show_tmp)// 如果是临时菜单，把menu_id_str-20000
            {
                long menu_id_long = Long.parseLong(menu_id_str) - 20000;
                menu_id_str = Long.toString(menu_id_long);
            }

            if (bindServerItemId == -1) {
                detail_item = "date_id=" + menu_id_str + "&menu_id=" + date_id
                        + "&widget_id=" + widget_id + "&type=" + type
                        + "&name=" + name + "&x=" + x + "&y=" + y + "&price="
                        + price + "&font_size=" + font_size + "&font_color="
                        + font_color + "&background_color=" + background_color
                        + "&distance=" + Double.toString(distance)
                        + "&additiontal=" + additiontal + "&redundance1="
                        + bindType + "&BindToItemServerId=" + "";
            } else {
                detail_item = "date_id=" + menu_id_str + "&menu_id=" + date_id
                        + "&widget_id=" + widget_id + "&type=" + type
                        + "&name=" + name + "&x=" + x + "&y=" + y + "&price="
                        + price + "&font_size=" + font_size + "&font_color="
                        + font_color + "&background_color=" + background_color
                        + "&distance=" + Double.toString(distance)
                        + "&additiontal=" + additiontal + "&redundance1="
                        + bindType + "&BindToItemServerId="
                        + Integer.toString(bindServerItemId);
            }

            // CommonUtils.LogWuwei(tag, "detail_item is "+detail_item);

            try {
                if (detail_item == null || detail_item.equals("")) {
                    return;
                }
                ip_address = LocalDataDeal.readFromLocalMenuIp(ctxt);
                String uriAPI = "http://" + ip_address + ":8081/?"
                        + detail_item;

                // 建立HTTPost对象
                HttpPost httpRequest = new HttpPost(uriAPI);
                CommonUtils.LogWuwei("uri", "uri is " + uriAPI);

                // NameValuePair实现请求参数的封装

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                if (p == 0 && flag_clear_tv_screen) {
                    params.add(new BasicNameValuePair("preview", "1"));
                    flag_clear_tv_screen = false;
                    p = -1;
                } else {
                    params.add(new BasicNameValuePair("preview", "2"));// 存储
                }

                try {
                    // 添加请求参数到请求对象
                    httpRequest.setEntity(new UrlEncodedFormEntity(params,
                            HTTP.UTF_8));
                    // 发送请求并等待响应
                    HttpResponse httpResponse = new DefaultHttpClient()
                            .execute(httpRequest);
                    // 若状态码为200 ok
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        // 读返回数据
                        String strResult = EntityUtils.toString(httpResponse
                                .getEntity());
                    } else {
                        CommonUtils.LogWuwei(tag, "post failure "
                                + httpResponse.getStatusLine().getStatusCode());
                    }
                } catch (ClientProtocolException e) {

                    e.printStackTrace();
                    CommonUtils.sendMsg("ClientProtocolException is " + e.getMessage(), SHOW_TOAST, handler);

                } catch (IOException e) {
                    CommonUtils.LogWuwei(tag,
                            "IOException is " + e.getMessage());
                    e.printStackTrace();
                    CommonUtils.sendMsg("IOException is " + e.getMessage(), SHOW_TOAST, handler);
                } catch (Exception e) {
                    CommonUtils.LogWuwei(tag, "Exception is " + e.getMessage());
                    CommonUtils.sendMsg("Exception is " + e.getMessage(), SHOW_TOAST, handler);
                    e.printStackTrace();
                }
                httpRequest = null;

            } finally {

            }
            /*
             * catch (InterruptedException e1) { e1.printStackTrace(); }
			 */

            detail_item = "";
            menu_detail_entity = null;
        }

    }

    /**
     * @param final_date    :同步的日期（如果有临时数据，为临时数据对应的日期）
     * @param k             :对应服务器的菜单（早餐、午餐、晚餐）的序号
     * @param flag_show_tmp ：final_date 是否为临时数据对应的日期
     */
    public void updateMenuToClound(long final_date, int k, boolean flag_show_tmp) {

        // CommonUtils.LogWuwei("update",
        // "云端同步菜单中。。。。\n\nfinal_date is"+final_date);
        long detail_date = 0;
        // JSONObject jsAll = new JSONObject();
        JSONArray totalMenuDetail = new JSONArray();

        for (int p = 0; p < listResultPrieview.size(); p++)// p<5;p++)
        {
            JSONObject jsobjectTmp = new JSONObject();
            MenuDetail menu_detail_entity = (MenuDetail) listResultPrieview
                    .get(p);

            CommonUtils.LogWuwei("zzlwss", "final_date is " + final_date);

            String menu_id_str = Long.toString(final_date);// menu_detail_entity.getMenuDateId();
            String date_id = "";
            String widget_id = Integer.toString(menu_detail_entity
                    .getWidgetId());
            String type = menu_detail_entity.getType();
            String name = menu_detail_entity.getName();
            String x = Double.toString(menu_detail_entity.getX());
            String y = Double.toString(menu_detail_entity.getY());
            String price = Integer.toString(menu_detail_entity.getPrice());
            String font_size = Integer.toString(menu_detail_entity
                    .getFontSize());
            String font_color = menu_detail_entity.getFontColor();
            String background_color = menu_detail_entity.getBackgroundColor();
            double distance = menu_detail_entity.getDistance();
            String additiontal = menu_detail_entity.getAddtiontal();
            String bindType = menu_detail_entity.getRedundance1();
            if (menu_detail_entity.getBindToItemServerId() == null) {
                menu_detail_entity.setBindToItemServerId(0);
            }
            int bindServerItemId = menu_detail_entity.getBindToItemServerId();
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

            if (!background_color.equals("0x0000")
                    || !background_color.equals("0x000")
                    || background_color == null) {
                background_color = "0x0000";
            }
            if (name == null) {
                name = "";
            }

            if (font_color == null) {
                font_color = "";
            }

            if (flag_show_tmp)// 如果是临时菜单，把menu_id_str-20000
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
                jsobjectTmp.put("bindServerItemId", bindServerItemId);
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
                CommonUtils.LogWuwei("JSONException", "JSONException error:"
                        + e.getMessage());
                e.printStackTrace();
            }

        }

        long timeStamp = CommonUtils.getTimeStamp(detail_date / 10);

        //1、得到每天对应的时间戳

        long dataLong = detail_date / 10;

        String data = "";//dataLong/10000+"-"+dataLong%10000/100+"-"+dataLong%10000%100;
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


        for (int p = 0; p < menu_list_int.length; p++) {
            // CommonUtils.LogWuwei(tag,
            // "早、中、晚"+menu_list_int[0]+" "+menu_list_int[1]+" "+menu_list_int[2]+" timeStamp is "+timeStamp+"day is "+detail_date);
            if (menu_list_int[p] == -1 || menu_list_int[p] == 0) {
                menu_list_int = menu_list_int_local;
                break;
            }
        }
        String tmp = totalMenuDetail.toString();
        CommonUtils.LogWuwei(tag, "length is " + totalMenuDetail.toString().length() + "\n" + "totalMenuDetail.toString() is :\n" + totalMenuDetail.toString());

        ApisManager.SaveMenuToClound(list_meal_bucket.get(k).time_bucket_id, data, tmp, new ApiCallback() {
            @Override
            public void success(Object object) {

            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }

    public void getTimeBucketList() {
        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                list_meal_bucket = (List<MealBucket>) object;
                CommonUtils.sendMsg("", HIDE_LOADING, handler);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", HIDE_LOADING, handler);
                CommonUtils.sendMsg(response.error_message + "", SHOW_LOADING_TEXT, handler);
            }
        });
    }

    /********************************* 窗口显示相关**********************************/
    /**
     * 询问同步菜单的方式
     *
     * @return true-->本地局域网同步 false-->云端上传
     */
    public boolean askLocalOrClound() {
        Context ctxt = null;
        final Boolean flag[] = {true, false};
        if (ClientMenuSplashActivty.this != null) {
            ctxt = ClientMenuSplashActivty.this;
        }

        if (ctxt != null
                && NetWorkUtils.isWifiConnected(getApplicationContext())) {
            CommonUtils.LogWuwei(tag, "askLocalOrClounding");
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                    .getInstance(ClientMenuSplashActivty.this);
            Effectstype effect;
            effect = Effectstype.Fall;

            dialogBuilder
                    .withTitle("选择同步方式")
                            // .withTitle(null) no title
                    .withTitleColor("#FFFFFF")
                            // def
                    .withDividerColor("#11000000")
                            // def
                    .withMessageColor("#FFFFFF")
                            // def
                    .withMessage(
                            "现场同步菜单:1、直接同步到电视\n\n"
                                    + "云端同步菜单:1、存储到云端 2、在电视上选择手动更新")
                    .isCancelableOnTouchOutside(true) // def |
                            // isCancelable(true)
                    .withDuration(300) // def
                    .withEffect(effect) // def Effectstype.Slidetop
                    .withButton1Text("现场同步菜单") // def gone
                    .withButton2Text("云端同步菜单") // def gone
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();

                            new Thread() {
                                public void run() {
                                    final int result = CommonUtils.getPrinterTestResult(ip_address);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result == 0) {
                                                flag[0] = true;
                                                doUpdate(false);
                                            } else {
                                                client_menu_not_alive_dialog();
                                                Toast.makeText(getApplicationContext(), "同步失败",
                                                        Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        }
                                    });


                                }
                            }.start();
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    flag[0] = false;
                    doUpdate(true);

                }
            }).show();

            CommonUtils.LogWuwei(tag, "askLocalOrClound show window flag[0] is " + flag[0]);

        } else {
            CommonUtils.LogWuwei(tag,
                    "askLocalOrClound doesn't show dialog window");
        }

        return flag[0];

    }

    /**
     * 弹出对话框让用户选择是创建/编辑本周菜单还是下周菜单
     */
    public void askThisOrNextWeek() {

        Context ctxt = null;
        if (ClientMenuSplashActivty.this != null) {
            ctxt = ClientMenuSplashActivty.this;
        }

        if (ctxt != null) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                    .getInstance(ClientMenuSplashActivty.this);
            Effectstype effect;
            effect = Effectstype.Fall;

            dialogBuilder
                    .withTitle("请选择\"创建/编辑\"菜单日期")
                            // .withTitle(null) no title
                    .withTitleColor("#FFFFFF")
                            // def
                    .withDividerColor("#11000000")
                            // def
                    .withMessageColor("#FFFFFF")
                            // def
                    .withMessage(
                            "本周菜单：如果本周菜单为空，你将创建本周菜单；否则你将编辑本周菜单\n\n下周菜单：如果下周菜单为空，你将创建下周菜单；否则你将编辑下周菜单")
                    .isCancelableOnTouchOutside(true) // def |
                            // isCancelable(true)
                    .withDuration(300) // def
                    .withEffect(effect) // def Effectstype.Slidetop
                    .withButton1Text("本周菜单") // def gone
                    .withButton2Text("下周菜单") // def gone
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                            newWeekMenu(0);
                            dialogBuilder.deleteInstance();
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    newWeekMenu(1);
                    dialogBuilder.deleteInstance();
                }
            }).show();
        }

    }
}
