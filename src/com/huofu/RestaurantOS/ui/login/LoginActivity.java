package com.huofu.RestaurantOS.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.LoginGridviewAdapter;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.AppInfo;
import com.huofu.RestaurantOS.bean.AppVersionInfo;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.user.LoginStaff;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.service.getTakeupListIntentService;
import com.huofu.RestaurantOS.support.shapedImage.CircleImageView;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.funcSplash.AllFuncSplashActivity;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.AutoPushMealUtils;
import com.huofu.RestaurantOS.utils.AutoInstall;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * *
 * 登录
 */
public class LoginActivity extends BaseActivity {

    public static String tag = "LoginActivity";

    public static Context ctxt = null;

    public int num_qrcode_success = 0;
    public Bitmap bitmapNowQrcode = null;

    public RelativeLayout rlLoginMain = null;
    public RelativeLayout rlLoginChooseStaff = null;
    public RelativeLayout rlLoginAllStaffGridview = null;
    public RelativeLayout rlLoginChooseStaffButton = null;
    public RelativeLayout rlLoginInputPassword = null;
    public RelativeLayout rlKeyboardInput = null;
    public RelativeLayout rlQrcodeScan = null;
    public RelativeLayout rlLoginQrcodeScanReady = null;
    public RelativeLayout rlLoginQrcodeScanWatingUserCmd = null;

    public TextView tvChooseStaffName = null;
    public TextView tvQrScanStaffName = null;
    public TextView tvMerchantName = null;
    public TextView tvStoreName = null;
    public TextView tvQrcodeExpired = null;


    public GridView gridviewStaffList = null;

    public Button buttonSwitchQrcodeLoginWay = null;
    public Button buttonSwitchKeyboardLoginWay = null;
    public Button buttonLoginCancelQrscanLogin = null;

    public ImageView imageViewQrcode = null;//加载二维码的图片
    public CircleImageView imageviewChooseStaff = null;//员工头像
    public CircleImageView imageviewChooseStaffQrHead = null;//扫码后等待确认时的员工头像

    public static boolean flagChooseStaff = false;//是否有员工选中
    public static boolean flagInQrcodeDialog = false;
    public static boolean flagWaitngCmd = false;

    public static long qrcodeCreateTimeInLogin = 0;
    public long mExitTime = 0;

    public static Timer timerQrcode = null;//检测员工二维码登录状态的定时器
    public static TimerTask timerTaskQrcode = null;//检测员工二维码登录状态的定时任务
    public String tokenQrcode = null;//二维码登录的token

    public int positionStaffId = -1;
    public LoginGridviewAdapter gridviewStaffListAdapter = null;
    public String password = "";

    public static BitmapUtils bitmapUtils;
    public static BitmapDisplayConfig bigPicDisplayConfig;
    public HttpUtils httpUtils;
    public DefaultBitmapLoadCallBack<ImageView> callback;//图片加载的回调函数
    public static DefaultBitmapLoadCallBack<ImageView> callbackSetOverLay;//图片加载后设置蒙版

    public static Button button_password_no_1 = null;
    public static Button button_password_no_2 = null;
    public static Button button_password_no_3 = null;
    public static Button button_password_no_4 = null;
    public static Button button_password_no_5 = null;
    public static Button button_password_no_6 = null;

    Dialog dialog_progress = null;
    Dialog dialog_loading = null;
    PopupWindow dialog_show_error = null;


    public static List<LoginStaff> listLoginStaff = new ArrayList<LoginStaff>();
    List<LoginStaff> listLoginStaffSave = new ArrayList<LoginStaff>();

    public boolean hasFoucs = false;
    public final static int GET_LIST_LOGIN_STAFF = 0;
    public final static int REMOVE_GRIDVIEWS = 1;
    public final static int REMOVE_LOGIN_DIALOG = 2;
    public final static int LOGIN_SUCCESS = 3;
    public final static int GET_QRCODE_SUCCESS = 4;//二维码获取成功
    public final static int SCAN_QRCODE_SUCCESS = -2;//用户确认扫码登录结果
    public final static int LOGIN = 6;
    public final static int SCAN_QRCODE_WAITING_CMD = 7;//等待确认
    public static final int SHOW_LOADING_TEXT = 8;
    public static final int HIDE_LOADING = 9;
    public static final int GET_LIST_LOGIN_STAFF_AFTER_CHECKIN = 10;
    public static final int UPDATE_MERCHANT_STORE_NAME = 11;//获取副本信息后更新商户名称和店铺名称
    public static final int SHOW_ERROR_MESSAGE = 12;
    public static final int SHOW_AUTH_ERROR = 13;
    public static final int SWITCH_TO_KEYBOARD = 14;//从二维码扫描登录状态切换到键盘登录界面
    public static final int SHOW_QRCODE_REFRSH_AGAIN = 15;//由于在二维码显示界面，用户5分钟内都没有扫码，那么弹窗提醒重新扫码或者跳转到键盘登录
    public static final int SHOW_TOAST = 16;
    public static final int SHOW_ERROR_MSG_TWO_OPTIONS = 17;
    public static final int SHOW_ERROR_MSG_ONE_OPTION = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ctxt = MainApplication.getContext();

        UmengUpdateAgent.update(this);

        ViewServer.get(this).addWindow(this);

        setContentView(R.layout.login);

        init();

        widgetConfigure();

        checkIn();

        getTakeupListIntentService.flagServiceOn = false;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        ViewServer.get(this).setFocusedWindow(this);

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
        CommonUtils.sendMailToMe(ctxt, 8);
        stopQrScanTimer();
        flagChooseStaff = false;

        listLoginStaff.clear();
        listLoginStaffSave.clear();

        dialog_loading = null;
        dialog_progress = null;
        dialog_show_error = null;

        ctxt = null;
        mUiHandler = null;
        rlLoginMain = null;
        rlLoginChooseStaff = null;
        rlLoginAllStaffGridview = null;
        rlLoginChooseStaffButton = null;
        rlLoginInputPassword = null;
        rlKeyboardInput = null;
        rlQrcodeScan = null;
        rlLoginQrcodeScanReady = null;
        rlLoginQrcodeScanWatingUserCmd = null;

        tvChooseStaffName = null;
        tvQrScanStaffName = null;
        tvMerchantName = null;
        tvStoreName = null;


        gridviewStaffList = null;

        buttonSwitchQrcodeLoginWay = null;
        buttonSwitchKeyboardLoginWay = null;
        buttonLoginCancelQrscanLogin = null;

        imageViewQrcode = null;//加载二维码的图片
        imageviewChooseStaff = null;//员工头像
        imageviewChooseStaffQrHead = null;//扫码后等待确认时的员工头像

        flagChooseStaff = false;//是否有员工选中
        flagInQrcodeDialog = false;
        flagWaitngCmd = false;

        qrcodeCreateTimeInLogin = 0;
        mExitTime = 0;

        timerQrcode = null;//检测员工二维码登录状态的定时器
        timerTaskQrcode = null;//检测员工二维码登录状态的定时任务
        tokenQrcode = null;//二维码登录的token

        positionStaffId = -1;
        gridviewStaffListAdapter = null;
        password = "";

        bitmapUtils = null;
        bigPicDisplayConfig = null;
        callback = null;//图片加载的回调函数
        callbackSetOverLay = null;//图片加载后设置蒙版

        button_password_no_1 = null;
        button_password_no_2 = null;
        button_password_no_3 = null;
        button_password_no_4 = null;
        button_password_no_5 = null;
        button_password_no_6 = null;

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        active = false;

        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }
        if (dialog_progress != null) {
            dialog_progress.dismiss();
        }
        if (dialog_show_error != null) {
            dialog_show_error.dismiss();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        this.hasFoucs = hasFocus;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 检测屏幕的方向：纵向或横向
    }

    /*****************************************  初始化及配置 ********************************************/
    /**
     * 初始化
     */
    public void init() {

        MainApplication.setmActivity(this);
        ctxt = MainApplication.getContext();
        AutoPushMealUtils.flagRun = false;
        getTakeupListIntentService.flagActivate = false;
        rlLoginMain = (RelativeLayout) findViewById(R.id.rlLoginMain);
        rlLoginAllStaffGridview = (RelativeLayout) findViewById(R.id.rl_login_staff_list);
        rlLoginChooseStaff = (RelativeLayout) findViewById(R.id.rl_login_after_choose_staff);
        rlLoginChooseStaffButton = (RelativeLayout) findViewById(R.id.rl_login_all_staff_list_button);
        rlLoginInputPassword = (RelativeLayout) findViewById(R.id.rl_login_input_password);
        rlKeyboardInput = (RelativeLayout) findViewById(R.id.rl_login_keyboard_input);
        rlQrcodeScan = (RelativeLayout) findViewById(R.id.rl_login_qrcode_scan);
        rlLoginQrcodeScanReady = (RelativeLayout) findViewById(R.id.rl_login_qrcode_scan_ready);
        rlLoginQrcodeScanWatingUserCmd = (RelativeLayout) findViewById(R.id.rl_login_qrcode_scan_waiting_user_cmd);

        tvQrScanStaffName = (TextView) findViewById(R.id.textview_login_qrcan_staff_name);
        tvChooseStaffName = (TextView) findViewById(R.id.textview_login_choose_staff_name);

        tvMerchantName = (TextView) findViewById(R.id.textview_login_title_merchant_name);
        tvStoreName = (TextView) findViewById(R.id.textview_login_merchant_store_name);
        tvQrcodeExpired = (TextView) findViewById(R.id.textview_qrcode_expired);

        buttonSwitchQrcodeLoginWay = (Button) findViewById(R.id.button_switch_login_way);
        buttonSwitchKeyboardLoginWay = (Button) findViewById(R.id.button_switch_login_keyboard_way);
        buttonLoginCancelQrscanLogin = (Button) findViewById(R.id.button_login_cancel_qrscan_login);

        gridviewStaffList = (GridView) findViewById(R.id.gridviewStaffList);

        callback = new DefaultBitmapLoadCallBack<ImageView>() {
            public void onLoadCompleted(ImageView container, String uri, android.graphics.Bitmap bitmap, BitmapDisplayConfig config, com.lidroid.xutils.bitmap.callback.BitmapLoadFrom from) {
                try {
                    imageViewQrcode.setVisibility(View.VISIBLE);
                    container.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    bitmapNowQrcode = bitmap;
                    CommonUtils.sendMsg(null, HIDE_LOADING, mUiHandler);

                    timerQrcode = new Timer();
                    timerTaskQrcode = new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            long qrcodeLastTime = System.currentTimeMillis() / 1000-qrcodeCreateTimeInLogin;
                            CommonUtils.LogWuwei(tag,timerQrcode==null?"timerQrcode is null":"timerQrcode is not null"+"  qrcodeLastTime is "+qrcodeLastTime);
                            if(qrcodeLastTime >300)
                            {
                                timerQrcode.cancel();
                                timerTaskQrcode.cancel();
                                sendUIMessage(SHOW_QRCODE_REFRSH_AGAIN, "");
                                return;
                            }

                            if (active && flagInQrcodeDialog && !tokenQrcode.equals("")) {
                                ApisManager.ScanLoginQrcodeStatus(tokenQrcode, new ApiCallback() {
                                    @Override
                                    public void success(Object object) {
                                        JSONObject obj = (JSONObject) object;
                                        dealQrcodeScanResult(obj);
                                    }

                                    @Override
                                    public void error(BaseApi.ApiResponse response) {
                                        sendUIMessage(HIDE_LOADING, "");
                                        sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
                                    }
                                });

                            } else {
                                timerQrcode.cancel();
                                timerTaskQrcode.cancel();
                                timerQrcode = null;
                                timerTaskQrcode = null;
                            }
                        }
                    };

                    timerQrcode.schedule(timerTaskQrcode, 0, 2000);
                } catch (Exception e) {

                }

            }

        };

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
                Drawable d2 = LoginActivity.this.getResources().getDrawable(R.drawable.head_photo_overlay);
                Drawable[] array = new Drawable[]{d1, d2};

                LayerDrawable ld = new LayerDrawable(array);
                container.setBackgroundDrawable(ld);
            }
        };
    }

    @Override
    public void dealWithmessage(Message msg) {
        if (!active) {
            return;
        }

        switch (msg.what) {
            case GET_LIST_LOGIN_STAFF:
                CommonUtils.sendMsg(null, HIDE_LOADING, mUiHandler);
                listLoginStaff = (List<LoginStaff>) msg.obj;
                if (listLoginStaff != null && listLoginStaff.size() > 0) {
                    for (int i = 0; i < listLoginStaff.size(); i++) {
                        LoginStaff ls = listLoginStaff.get(i);
                        listLoginStaffSave.add(ls);
                    }
                    showList();
                }
                break;
            case REMOVE_GRIDVIEWS:
                LoginGridviewAdapter.selectPic = -1;
                listLoginStaff.clear();
                if (gridviewStaffListAdapter != null) {
                    gridviewStaffListAdapter.notifyDataSetInvalidated();
                }
                break;
            case REMOVE_LOGIN_DIALOG:
                CommonUtils.sendMsg(null, HIDE_LOADING, mUiHandler);
                rlLoginInputPassword.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.shake_500));
                break;
            case LOGIN_SUCCESS:
                sendUIMessage(HIDE_LOADING, "");
                MobclickAgent.onEvent(ctxt, "login_code_success");
                CommonUtils.getTemplateList(ctxt, mUiHandler);
                boolean flagAlreadyInit = LocalDataDeal.readFromLocalFlagFinishInit(ctxt);
                if (flagAlreadyInit) {
                    finishWithNextActivity(AllFuncSplashActivity.class);
                } else {
                    finishWithNextActivity(InitPadActivity.class);
                }

                break;
            case LOGIN:
                loginKeyBoardEnterClickListner();
                break;
            case GET_QRCODE_SUCCESS:
                qrcodeCreateTimeInLogin = System.currentTimeMillis() / 1000;
                CommonUtils.sendMsg("创建二维码成功", SHOW_LOADING_TEXT, mUiHandler);

                stopQrScanTimer();

                Bundle bdQrcode = (Bundle) msg.getData();

                tokenQrcode = bdQrcode.getString("token");
                String qrcode_url = bdQrcode.getString("qrcode_url");

                CommonUtils.sendMsg("加载二维码", SHOW_LOADING_TEXT, mUiHandler);
                bitmapUtils.display(imageViewQrcode, qrcode_url, bigPicDisplayConfig, callback);

                break;
            case SCAN_QRCODE_SUCCESS:
                CommonUtils.sendMsg(null, LoginActivity.HIDE_LOADING, mUiHandler);
                num_qrcode_success++;
                MobclickAgent.onEvent(ctxt, "login_qrcode_success");
                stopQrScanTimer();

                CommonUtils.getTemplateList(ctxt, mUiHandler);
                flagAlreadyInit = LocalDataDeal.readFromLocalFlagFinishInit(ctxt);
                if (flagAlreadyInit) {
                    finishWithNextActivity(AllFuncSplashActivity.class);
                } else {
                    finishWithNextActivity(InitPadActivity.class);
                }

                break;
            case SCAN_QRCODE_WAITING_CMD:
                CommonUtils.sendMsg(null, LoginActivity.HIDE_LOADING, mUiHandler);
                flagWaitngCmd = true;
                if (rlLoginQrcodeScanWatingUserCmd.getVisibility() == View.INVISIBLE) {
                    rlLoginQrcodeScanReady.setVisibility(View.INVISIBLE);
                    rlLoginQrcodeScanWatingUserCmd.setVisibility(View.VISIBLE);
                    rlLoginQrcodeScanWatingUserCmd.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

                    Map map = (Map) msg.obj;
                    String uri = (String) map.get("head");
                    if (uri.equals("")) {
                        imageviewChooseStaffQrHead.setBackgroundResource(R.drawable.default_head_pic);
                    } else {
                        bitmapUtils.display(imageviewChooseStaffQrHead, (String) map.get("head"), bigPicDisplayConfig);
                    }

                    tvQrScanStaffName.setText((String) map.get("name"));
                }
                break;
            case SHOW_LOADING_TEXT:
                String content = (String) msg.obj;
                showLoadingDialog(content);
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case GET_LIST_LOGIN_STAFF_AFTER_CHECKIN:
                getStaffList();
                break;
            case UPDATE_MERCHANT_STORE_NAME:
                tvMerchantName.setText(LocalDataDeal.readFromLocalMerchantName(ctxt));
                tvStoreName.setText(LocalDataDeal.readFromLocalStoreName(ctxt));
                hideLoadingDialog();
                break;
            case SHOW_ERROR_MESSAGE:
                showDialogError((String) msg.obj);
                break;
            case SHOW_AUTH_ERROR:
                switchToKeyboardInputLogin();
                showDialogError((String) msg.obj);
                break;
            case SWITCH_TO_KEYBOARD:
                switchToKeyboardInputLogin();
                break;
            case SHOW_QRCODE_REFRSH_AGAIN:
                dealQrcodeExpired();
                break;
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, (String) msg.obj);
                break;
            case SHOW_ERROR_MSG_TWO_OPTIONS:
                Map map = (Map)msg.obj;
                OnClickListener ocl = (OnClickListener)map.get("ocl");
                String message = (String)map.get("msg");
                String text = (String)map.get("text");
                showDialogErrorTwoOptions(message,ocl,text);
                break;
            case SHOW_ERROR_MSG_ONE_OPTION:
                map = (Map)msg.obj;
                ocl = (OnClickListener)map.get("ocl");
                message = (String)map.get("msg");
                text = (String)map.get("text");
                showDialogErrorOneOption(message,ocl,text);
                break;
            default:
                HandlerUtils.showToast(ctxt, "mUiHandler default deal");
                break;
        }

    }

    /**
     * 控件配置
     */
    public void widgetConfigure() {
        launchPadConfigure();
        bitmapUtils = new BitmapUtils(getApplicationContext(), pathLoginStaffCache, 150 * 300 * 300, 150 * 300 * 300);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。

        bigPicDisplayConfig.setLoadFailedDrawable(this.getResources().getDrawable(R.drawable.default_head_pic));
        httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(60000);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏

        imageviewChooseStaff = (CircleImageView) findViewById(R.id.imageview_login_choose_staff);
        imageViewQrcode = (ImageView) findViewById(R.id.imageview_login_qrcode_scan);
        imageviewChooseStaffQrHead = (CircleImageView) findViewById(R.id.imageview_login_qrcode_scan_staff_head_photo);

        button_password_no_1 = (Button) findViewById(R.id.imageview_login_password_no_1);
        button_password_no_2 = (Button) findViewById(R.id.imageview_login_password_no_2);
        button_password_no_3 = (Button) findViewById(R.id.imageview_login_password_no_3);
        button_password_no_4 = (Button) findViewById(R.id.imageview_login_password_no_4);
        button_password_no_5 = (Button) findViewById(R.id.imageview_login_password_no_5);
        button_password_no_6 = (Button) findViewById(R.id.imageview_login_password_no_6);

        gridviewStaffList.setAdapter(gridviewStaffListAdapter);
        gridviewStaffList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridviewStaffList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                positionStaffId = listLoginStaff.get(position).staff_id;

                tvChooseStaffName.setText(listLoginStaff.get(position).name);
                CommonUtils.LogWuwei(tag, "当前选中的用户名称:" + listLoginStaff.get(position).name + " id为:" + listLoginStaff.get(position).staff_id);

                password = "";
                loginKeyBoardDeleteClickListner(null);

                boolean flagSameUser = false;

                for (int i = 0; i < listLoginStaff.size(); i++) {
                    if (listLoginStaff.get(i).flagStaffChoosen) {

                        LoginActivity.bitmapUtils.display(imageviewChooseStaff, listLoginStaff.get(position).head, LoginActivity.bigPicDisplayConfig, null);

                        listLoginStaff.get(i).flagStaffChoosen = false;
                        if (i == position) {
                            flagSameUser = true;
                        }
                    }

                }

                if (!flagSameUser) {
                    listLoginStaff.get(position).flagStaffChoosen = true;

                    LocalDataDeal.writeToLocalLastChooseStaffId(ctxt, listLoginStaff.get(position).staff_id);

                    if (listLoginStaff.get(position).head.equals("") || listLoginStaff.get(position).head == null) {
                        imageviewChooseStaff.setBackgroundResource(R.drawable.default_head_pic);
                    } else {
                        LoginActivity.bitmapUtils.display(imageviewChooseStaff, listLoginStaff.get(position).head, LoginActivity.bigPicDisplayConfig, null);
                    }

                    if (rlLoginAllStaffGridview.getVisibility() == View.VISIBLE) {
                        rlLoginAllStaffGridview.setVisibility(View.INVISIBLE);
                    }

                    if (rlLoginChooseStaff.getVisibility() == View.INVISIBLE) {

                        rlLoginChooseStaff.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.small_2_big));
                        rlLoginChooseStaff.setVisibility(View.VISIBLE);
                    }

                }
                gridviewStaffListAdapter.setNotifyDataChange(position);


            }
        });


        OnClickListener oclShowQrcode = new OnClickListener() {
            @Override
            public void onClick(View v) {
                flagInQrcodeDialog = true;

                imageViewQrcode.setVisibility(View.INVISIBLE);
                sendUIMessage(SHOW_LOADING_TEXT, "创建二维码");

                getLoginQrcode();

                rlKeyboardInput.setVisibility(View.INVISIBLE);
                rlQrcodeScan.setVisibility(View.VISIBLE);
                rlLoginQrcodeScanWatingUserCmd.setVisibility(View.INVISIBLE);
                rlLoginQrcodeScanReady.setVisibility(View.VISIBLE);
                tvQrcodeExpired.setTextColor(Color.parseColor("#898989"));
                tvQrcodeExpired.setText("请使用微信扫描登录");

                Animation animation = new AnimationUtils().loadAnimation(ctxt, R.anim.slide_right_out);
                rlKeyboardInput.startAnimation(animation);
                rlQrcodeScan.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_left_in));

            }
        };

        buttonSwitchQrcodeLoginWay.setOnClickListener(oclShowQrcode);
        findViewById(R.id.rl_switch_login_way).setOnClickListener(oclShowQrcode);

        OnClickListener oclSwitchToKeyboard = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switchToKeyboardInputLogin();
            }
        };

        buttonLoginCancelQrscanLogin.setOnClickListener(oclSwitchToKeyboard);
        buttonSwitchKeyboardLoginWay.setOnClickListener(oclSwitchToKeyboard);
        findViewById(R.id.rl_switch_login_keyboard_way).setOnClickListener(oclSwitchToKeyboard);

        rlLoginChooseStaffButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                btnChooseStaffClickedDeal();
            }
        });

        rlLoginChooseStaff.setVisibility(View.INVISIBLE);
        rlQrcodeScan.setVisibility(View.INVISIBLE);
        rlLoginQrcodeScanWatingUserCmd.setVisibility(View.INVISIBLE);

        ((TextView) findViewById(R.id.tv_login_app_version)).setText(getResources().getString(R.string.app_name) + " V" + LocalDataDeal.readFromLocalVersion(ctxt)
                        + "  " + LocalDataDeal.readFromLocalAppCopyName(ctxt)
        );
        ((TextView) findViewById(R.id.tv_login_app_version)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApi.flagdebugSwitchAPI) {
                    if (BaseApi.API_DOMAIN.equals("http://54.223.143.247:8100/api/cashier/")) {
                        BaseApi.API_DOMAIN = "http://test.api.huofu.com/api/cashier/";
                        sendUIMessage(SHOW_TOAST, "已经却换到test环境");
                    } else if (BaseApi.API_DOMAIN.equals("http://test.api.huofu.com/api/cashier/")) {
                        BaseApi.API_DOMAIN = "http://54.223.143.247:8100/api/cashier/";
                        sendUIMessage(SHOW_TOAST, "已经却换到bus环境");
                    }
                }


            }
        });
        showPopupWindowInit(buttonSwitchQrcodeLoginWay);
    }

    /*****************************************
     * 逻辑处理
     ********************************************/

    /**
     * *
     * <p/>
     * 二维码过期后的处理
     */
    public void dealQrcodeExpired() {

        if (rlLoginQrcodeScanWatingUserCmd.getVisibility() == View.VISIBLE) {
            rlLoginQrcodeScanReady.setVisibility(View.VISIBLE);
            rlLoginQrcodeScanWatingUserCmd.setVisibility(View.INVISIBLE);
            rlLoginQrcodeScanReady.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
        }

        tvQrcodeExpired.setTextColor(getResources().getColor(R.color.Ponceau));
        tvQrcodeExpired.setText("二维码过期，点击刷新按钮刷新二维码");
        stopQrScanTimer();

        Drawable d2 = LoginActivity.this.getResources().getDrawable(R.drawable.refresh);

        imageViewQrcode.setBackgroundDrawable(d2);

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginActivity.qrcodeCreateTimeInLogin = 0;
                CommonUtils.LogWuwei(tag, "\n\n\n\n\n刷新二维码\n\n\n\n");
                CommonUtils.sendMsg("刷新二维码", LoginActivity.SHOW_LOADING_TEXT, mUiHandler);
                sendUIMessage(SHOW_LOADING_TEXT, "刷新二维码");

                getLoginQrcode();

                tvQrcodeExpired.setTextColor(Color.parseColor("#898989"));
                tvQrcodeExpired.setText("请使用微信扫描登录");
            }
        };

        imageViewQrcode.setOnClickListener(ocl);
        tvQrcodeExpired.setOnClickListener(ocl);
    }

    /***
     * 处理二维码扫描结果
     */
    public void dealQrcodeScanResult(JSONObject oj) {
        long nowTime = System.currentTimeMillis() / 1000;
        if (!LoginActivity.flagWaitngCmd)//超过5分钟没有扫码，则弹窗提示，并停止定时器，用户点了“刷新”重新创建二维码并启动定时器扫描二维码状态
        {
            if (nowTime - LoginActivity.qrcodeCreateTimeInLogin > 300) {
                sendUIMessage(SHOW_QRCODE_REFRSH_AGAIN, "");
                return;
            }
        }
        String statusWhetherScan = oj.getString("status");
        if (Integer.parseInt(statusWhetherScan) != 1) {
            CommonUtils.LogWuwei(tag,"未扫码");
            return;
        }

        String status = oj.getString("related_status");
        if (status.equals("1")) {
            Map map = new HashMap<String, String>();
            JSONObject user = oj.getJSONObject("user");
            map.put("head", user.getString("head"));
            map.put("name", user.getString("name"));
            map.put("user_id", user.getString("user_id"));

            LoginActivity.flagWaitngCmd = true;

            sendUIMessage(LoginActivity.SCAN_QRCODE_WAITING_CMD, map);
            map = null;
            CommonUtils.LogWuwei(tag, "----------扫码成功，等待用户在微信确认登录");
        } else if (status.equals("2")) {
            String staffAccToken = oj.getString("staff_access_token");
            LocalDataDeal.writeToLocalStaffAccessToken(staffAccToken, ctxt);
            LoginStaff loginStaff = oj.getObject("staff", LoginStaff.class);
            JSONArray arrayCodes = oj.getJSONArray("codes");
            JSONArray arrayPermission = oj.getJSONArray("permission_ids");

            CommonUtils.LogWuwei(tag, "~~~~~~~~"+loginStaff.name+"通过二维码登录成功(id:"+loginStaff.staff_id+")~~~~~~~");

            List<String> listCodes = new ArrayList<String>();
            List<String> listPermission = new ArrayList<String>();

            for (int k = 0; k < arrayCodes.size(); k++) {
                listCodes.add(arrayCodes.get(k).toString());
            }

            for (int k = 0; k < arrayPermission.size(); k++) {
                listPermission.add(arrayPermission.get(k).toString());
            }
            LocalDataDeal.writeToLocalNowLoginUserInfo(loginStaff.head, loginStaff.name, listPermission, listCodes, MainApplication.getContext());

            Message msg = new Message();
            msg.what = LoginActivity.SCAN_QRCODE_SUCCESS;
            sendUIMessage(msg);
            LocalDataDeal.writeToLocalLastChooseStaffId(MainApplication.getContext(), loginStaff.staff_id);

        } else if (status.equals("0"))//无权登录
        {
            String msg = "无权登录";
            msg = "\n" + CommonUtils.TOP_BORDER + "\n" + msg + "\n" + CommonUtils.BOTTOM_BORDER;
            CommonUtils.LogWuwei(tag, msg);
            LoginActivity.flagWaitngCmd = false;
            stopQrScanTimer();
            sendUIMessage(SHOW_AUTH_ERROR, "无权登录");
        } else if (status.equals("3"))//取消登录或者关闭
        {
            String msg = "微信端取消登录";
            msg = "\n" + CommonUtils.TOP_BORDER + "\n" + msg + "\n" + CommonUtils.BOTTOM_BORDER;
            CommonUtils.LogWuwei(tag, msg);
            LoginActivity.flagWaitngCmd = false;
            sendUIMessage(SHOW_TOAST, "用户在微信端取消登陆！");
            stopQrScanTimer();
            sendUIMessage(SWITCH_TO_KEYBOARD, "");
        }
    }

    /**
     * 获取副本信息
     */
    public void getAppCopyInfo() {
        ApisManager.getAppCopy(new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(LoginActivity.UPDATE_MERCHANT_STORE_NAME, null);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
            }
        });
    }

    /***
     * 获取登录的二维码
     */
    public void getLoginQrcode() {
        ApisManager.GetLoginQrcode(new ApiCallback() {
            @Override
            public void success(Object object) {
                JSONObject oj = (JSONObject) object;

                Message msg = new Message();
                msg.what = LoginActivity.GET_QRCODE_SUCCESS;
                Bundle bd = new Bundle();
                bd.putString("token", oj.getString("token"));
                bd.putString("qrcode_url", oj.getString("qrcode_url"));
                msg.setData(bd);
                mUiHandler.sendMessage(msg);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
            }
        });
    }

    /**
     * 签到
     */
    public void checkIn() {
        sendUIMessage(SHOW_LOADING_TEXT, "签到中..");
        ApisManager.checkin(new ApiCallback() {
            @Override
            public void success(Object object) {

                sendUIMessage(HIDE_LOADING, "");
                String workKey = (String) object;
                LocalDataDeal.writeToLocalWorkKey(workKey, 1, MainApplication.getContext());

                if (active) {
                    sendUIMessage(LoginActivity.SHOW_LOADING_TEXT, "签到成功,获取员工列表...");
                    getStaffList();
                    getAppCopyInfo();
                    CommonUtils.getTemplateList(ctxt, mUiHandler);//获取小票模板

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

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);

            }
        });

        ApisManager.GetAppInfo(2, new ApiCallback() {

            @Override
            public void success(Object object) {
                CommonUtils.LogWuwei(tag, "获取app信息成功");
                AppInfo appInfo = (AppInfo) object;
                int nowVersion = LocalDataDeal.readFromLocalVersionCode(ctxt);
                if (nowVersion < appInfo.lasted_version) {
                    sendUIMessage(SHOW_TOAST, "发现新版本");
                    ApisManager.GetAppVersionInfo(2, appInfo.lasted_version, new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            final AppVersionInfo info = (AppVersionInfo) object;
                            sendUIMessage(SHOW_TOAST, "下载地址为:" + info.download_url);

                            OnClickListener ocl = new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (popShowErrorOneOption != null) {
                                        popShowErrorOneOption.dismiss();
                                    }

                                    httpUtils.download(info.download_url, pathApkDownloadCache,
                                            false, false, new RequestCallBack<File>() {
                                                @Override
                                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                                    sendUIMessage(SHOW_TOAST, "下载成功");
                                                    AutoInstall.setUrl(pathApkDownloadCache);
                                                    AutoInstall.install(LoginActivity.this);
                                                    sendUIMessage(HIDE_LOADING, "");
                                                }

                                                @Override
                                                public void onFailure(HttpException e, String s) {
                                                    if (e.getExceptionCode() == 416) {
                                                        AutoInstall.setUrl(pathApkDownloadCache);
                                                        AutoInstall.install(LoginActivity.this);
                                                    } else {
                                                        sendUIMessage(SHOW_ERROR_MESSAGE, "下载失败" + s);
                                                    }
                                                    sendUIMessage(HIDE_LOADING, "");
                                                }

                                                @Override
                                                public void onLoading(long total, long current, boolean isUploading) {
                                                    super.onLoading(total, current, isUploading);
                                                    sendUIMessage(SHOW_LOADING_TEXT, "正在下载" + (current * 100) / total + "%");
                                                    sendUIMessage(SHOW_TOAST, "正在下载" + (current * 100) / total + "%");
                                                }

                                                @Override
                                                public void onStart() {
                                                    super.onStart();
                                                    sendUIMessage(SHOW_LOADING_TEXT, "下载中");
                                                }


                                            });
                                }
                            };
                            Map map = new HashMap();
                            map.put("ocl", ocl);
                            map.put("msg", "发现新版本:\n" + info.intro);
                            map.put("text", "立刻下载");
                            sendUIMessage(SHOW_ERROR_MSG_ONE_OPTION, map);

                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {

                        }
                    });
                }

            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "获取app信息失败:" + response.error_message);
            }
        });
    }

    /**
     * 获取员工列表信息
     */
    public void getStaffList() {

        CommonUtils.sendMsg("获取员工列表", SHOW_LOADING_TEXT, mUiHandler);

        ApisManager.getStaffList(1, 100, new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(GET_LIST_LOGIN_STAFF, object);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
            }
        });

    }


    /* **************************************  键盘输入处理 *******************************************/
    public void loginKeyBoardNumClickListner(View view) {

        flagChooseStaff = false;
        for (int i = 0; i < listLoginStaff.size(); i++) {
            if (listLoginStaff.get(i).flagStaffChoosen) {
                flagChooseStaff = true;
                break;
            }
        }

        if (flagChooseStaff) {
            String text = ((Button) view).getText().toString();
            if (password.length() <= 5) {
                password += text;
                switch (password.length()) {
                    case 1:
                        button_password_no_1.setBackgroundResource(R.drawable.password_circle_solod);
                        break;
                    case 2:
                        button_password_no_2.setBackgroundResource(R.drawable.password_circle_solod);
                        break;
                    case 3:
                        button_password_no_3.setBackgroundResource(R.drawable.password_circle_solod);
                        break;
                    case 4:
                        button_password_no_4.setBackgroundResource(R.drawable.password_circle_solod);
                        break;
                    case 5:
                        button_password_no_5.setBackgroundResource(R.drawable.password_circle_solod);
                        break;
                    case 6:
                        button_password_no_6.setBackgroundResource(R.drawable.password_circle_solod);
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    Message msg = new Message();
                                    msg.what = LOGIN;
                                    mUiHandler.sendMessage(msg);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                        break;
                    default:
                        break;
                }
            }

        } else {
            //hu.showToast(ctxt,"请首先选择要登录的员工");
            CommonUtils.sendMsg("请点击头像选择要登录的员工", SHOW_ERROR_MESSAGE, mUiHandler);
        }

    }

    public void loginKeyBoardEnterClickListner() {

        if (password.length() == 6) {
            CommonUtils.sendMsg("登录...", SHOW_LOADING_TEXT, mUiHandler);
            String name = "";
            for (int k = 0; k < listLoginStaff.size(); k++) {
                if (listLoginStaff.get(k).flagStaffChoosen) {
                    positionStaffId = listLoginStaff.get(k).staff_id;
                    name = listLoginStaff.get(k).name;
                }
            }
            CommonUtils.LogWuwei(tag, "员工:"+name+"id is " + positionStaffId +"." + password+"登录中...");
            ApisManager.Login(positionStaffId, password, new ApiCallback() {
                @Override
                public void success(Object object) {
                    CommonUtils.LogWuwei(tag, "登录成功");
                    sendUIMessage(LOGIN_SUCCESS, "");
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    sendUIMessage(HIDE_LOADING, "");
                    CommonUtils.LogWuwei(tag, "登录失败:" + response.error_message);
                    if (response.error_code == 130002)// 员工登录失败
                    {
                        sendUIMessage(REMOVE_LOGIN_DIALOG, response.error_message);
                    } else {
                        sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message);
                    }
                }
            });

            MobclickAgent.updateOnlineConfig(getApplicationContext());//发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率。

            password = "";
            button_password_no_1.setBackgroundResource(R.drawable.password_circle_empty);
            button_password_no_2.setBackgroundResource(R.drawable.password_circle_empty);
            button_password_no_3.setBackgroundResource(R.drawable.password_circle_empty);
            button_password_no_4.setBackgroundResource(R.drawable.password_circle_empty);
            button_password_no_5.setBackgroundResource(R.drawable.password_circle_empty);
            button_password_no_6.setBackgroundResource(R.drawable.password_circle_empty);
        }
    }

    public void loginKeyBoardDeleteClickListner(View view) {
        switch (password.length()) {
            case 1:
                button_password_no_1.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            case 2:
                button_password_no_2.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            case 3:
                button_password_no_3.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            case 4:
                button_password_no_4.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            case 5:
                button_password_no_5.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            case 6:
                button_password_no_6.setBackgroundResource(R.drawable.password_circle_empty);
                break;
            default:
                button_password_no_1.setBackgroundResource(R.drawable.password_circle_empty);
                button_password_no_2.setBackgroundResource(R.drawable.password_circle_empty);
                button_password_no_3.setBackgroundResource(R.drawable.password_circle_empty);
                button_password_no_4.setBackgroundResource(R.drawable.password_circle_empty);
                button_password_no_5.setBackgroundResource(R.drawable.password_circle_empty);
                button_password_no_6.setBackgroundResource(R.drawable.password_circle_empty);
                break;
        }

        StringBuffer sb = new StringBuffer(password);
        if (sb.length() >= 1) {
            password = sb.substring(0, sb.length() - 1);
        }
    }


    /*****************************************  显示  ********************************************/

    /**
     * 显示用户列表
     */
    public void showList() {

        if (!active) {
            return;
        }

        showLoadingDialog("加载头像中...");
        gridviewStaffListAdapter = new LoginGridviewAdapter(ctxt, listLoginStaff);
        gridviewStaffList.setAdapter(gridviewStaffListAdapter);
        gridviewStaffListAdapter.notifyDataSetChanged();

        flagChooseStaff = false;


        for (int i = 0; i < listLoginStaff.size(); i++) {
            if (listLoginStaff.get(i).flagStaffChoosen) {
                flagChooseStaff = true;
                if (listLoginStaff.get(i).head.equals("") || listLoginStaff.get(i).head == null) {
                    imageviewChooseStaff.setBackgroundResource(R.drawable.default_head_pic);
                } else {
                    LoginActivity.bitmapUtils.display(imageviewChooseStaff, listLoginStaff.get(i).head, LoginActivity.bigPicDisplayConfig, null);
                }
                tvChooseStaffName.setText(listLoginStaff.get(i).name);
                break;
            }
        }

        if (flagChooseStaff) {
            if (rlLoginAllStaffGridview.getVisibility() == View.VISIBLE) {
                rlLoginAllStaffGridview.setVisibility(View.INVISIBLE);
            }

            if (rlLoginChooseStaff.getVisibility() == View.INVISIBLE) {
                rlLoginChooseStaff.setVisibility(View.VISIBLE);
            }
        }
        hideLoadingDialog();

    }

    /**
     * 关掉扫描二维码的定时器
     */
    public void stopQrScanTimer() {

        if (!active) {
            return;
        }

        if (timerTaskQrcode != null) {
            timerTaskQrcode.cancel();
            timerTaskQrcode = null;
        }
        if (timerQrcode != null) {
            timerQrcode.cancel();
            timerQrcode = null;
        }
    }

    /**
     * 显示异常的窗口
     */
    public void showDialogError(String msg) {
        if (!active) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);

        TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
        final Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);

        tvContent.setText(msg);

        View.OnClickListener ocl = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (v.getId() == btn_close.getId()) {
                    if (dialog_show_error != null) {
                        if (dialog_show_error.isShowing()) {
                            dialog_show_error.dismiss();
                        }
                    }
                }
            }
        };
        btn_close.setOnClickListener(ocl);

        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);


        if (dialog_show_error == null) {
            dialog_show_error = new PopupWindow(grid, width, height, true);
        }

        dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
        dialog_show_error.setContentView(grid);
        dialog_show_error.setFocusable(true);
        dialog_show_error.setOutsideTouchable(true);
        dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
        try {
            if (active) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dialog_show_error.showAtLocation(buttonSwitchQrcodeLoginWay, Gravity.NO_GRAVITY, 0, 0);
                    }
                }, 100);
            }

        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "dialog_show_error.showAtLocation failed:" + e.getMessage());
        }

    }


    /**
     * 从二维码登录状态转换到键盘输入状态
     */
    public void switchToKeyboardInputLogin() {
        if (gridviewStaffListAdapter != null) {
            gridviewStaffListAdapter.notifyDataSetChanged();
        }

        stopQrScanTimer();

        flagWaitngCmd = false;
        flagInQrcodeDialog = false;
        rlQrcodeScan.setVisibility(View.INVISIBLE);
        rlKeyboardInput.setVisibility(View.VISIBLE);

        btnChooseStaffClickedDeal();

        if (listLoginStaff.size() <= 0) {
            getAppCopyInfo();//从二维码界面切换到输入界面，可以刷新列表：获取副本-》签到-》得到员工列表
            getStaffList();
        }
        rlQrcodeScan.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_left_out));
        rlKeyboardInput.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_right_in));

    }

    /**
     * 选择或者取消用户
     */
    public void btnChooseStaffClickedDeal() {
        if (gridviewStaffListAdapter != null && listLoginStaff.size() == 0) {
            password = "";
            gridviewStaffList.setBackgroundDrawable(null);

            for (int i = 0; i < listLoginStaffSave.size(); i++) {
                LoginStaff item = listLoginStaffSave.get(i);
                boolean flag = true;
                for (int k = 0; k < listLoginStaff.size(); k++) {
                    if (item.staff_id == listLoginStaff.get(k).staff_id) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    listLoginStaff.add(item);
                }

            }
            gridviewStaffListAdapter.notifyDataSetChanged();

        } else {
            if (listLoginStaff.size() == 0) {
                password = "";
            }
        }


        if (rlLoginChooseStaff.getVisibility() == View.VISIBLE) {
            rlLoginChooseStaff.setVisibility(View.INVISIBLE);
        }

        if (rlLoginAllStaffGridview.getVisibility() == View.INVISIBLE) {
            rlLoginAllStaffGridview.setVisibility(View.VISIBLE);
            rlLoginAllStaffGridview.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
        }


        password = "";
        flagChooseStaff = false;
        button_password_no_1.setBackgroundResource(R.drawable.password_circle_empty);
        button_password_no_2.setBackgroundResource(R.drawable.password_circle_empty);
        button_password_no_3.setBackgroundResource(R.drawable.password_circle_empty);
        button_password_no_4.setBackgroundResource(R.drawable.password_circle_empty);
        button_password_no_5.setBackgroundResource(R.drawable.password_circle_empty);
        button_password_no_6.setBackgroundResource(R.drawable.password_circle_empty);
    }


    /**
     * **************************************  按键处理  *******************************************
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try
            {
                if (flagInQrcodeDialog)//如果是在二维码等待扫描界面，则按下返回，返回到登录界面
                {
                    flagInQrcodeDialog = false;
                    flagWaitngCmd = false;
                    stopQrScanTimer();

                    rlQrcodeScan.setVisibility(View.INVISIBLE);
                    rlKeyboardInput.setVisibility(View.VISIBLE);
                    rlKeyboardInput.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
                } else {
                    if(popShowErrorOneOption.isShowing())
                    {
                        return true;
                    }

                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        sendUIMessage(SHOW_TOAST,"再按一次退出app");
                        mExitTime = System.currentTimeMillis();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setPositiveButton("确定离开？", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                MobclickAgent.onKillProcess(ctxt);
                                android.os.Process.killProcess(android.os.Process.myPid());
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
                }
            }
            catch(Exception e)
            {
                CommonUtils.LogWuwei(tag,"onKeyDown error:"+e.getMessage());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
