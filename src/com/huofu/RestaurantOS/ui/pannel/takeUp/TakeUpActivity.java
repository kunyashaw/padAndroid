package com.huofu.RestaurantOS.ui.pannel.takeUp;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;
import com.huofu.RestaurantOS.widget.SimpleSideDrawer;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/****
 * 取号模块
 */
public class TakeUpActivity extends BaseActivity {

    public static boolean active = false;
    public RelativeLayout rlTakeUpStart = null;
    public RelativeLayout rlTakeUpDial = null;
    public RelativeLayout rlTakeupDialOrChoosePackaged = null;
    public RelativeLayout rlTakeupChildDial = null;

    //public Dialog dialog_choose_packaged= null;
    public PopupWindow dialog_show_error = null;
    public PopupWindow dialog_show_error_with_title = null;
    public PopupWindow dialog_show_print_error = null;
    public Dialog dialog_loading = null;
    public PopupWindow pop_loading = null;

    public Button buttonTakeUpStart = null;
    public TextView tvMerchantNameInTakeUpMain = null;
    public TextView tvMerchantStoreNameInTakeUpMain = null;
    public TextView tvMerchantStoreNameInTakeUpDial = null;

    public SimpleSideDrawer mslidingMenu = null;

    public static Context ctxt = null;

    public boolean hasFocus = false;

    public OrderDetailInfo printFailedOdi = null;

    public HandlerUtils hu = null;
    public static Handler handler = null;
    public static Handler handlerThread = null;
    public static String tag = "TakeUpActivity";
    public String input_code = "";

    public RelativeLayout rlCloseInDialDialog = null;
    public Button buttonCloseInDialDialog = null;
    public Button buttonTakeupMenu = null;
    public static List<MealBucket> list_meal_bucket = null;//营业时间段的数据列表


    public static TimerTask timerTaskCalcChooseWaiting = null;//当用户输入取餐码后弹出让选择打包还是堂食的定时任务，如果超过1分钟，用户没有选择，则自动返回到取餐首页
    public static Timer timerCalcChooseWaiting = null;

    public static TimerTask timerTaskCalcChooseWaitingDial = null;//当用户输入取餐码后弹出让选择打包还是堂食的定时任务，如果超过1分钟，用户没有选择，则自动返回到取餐首页
    public static Timer timerCalcChooseWaitingDial = null;

    public BitmapUtils bitmapUtils;
    public BitmapDisplayConfig bigPicDisplayConfig;
    public DefaultBitmapLoadCallBack<ImageView> callback;// 图片加载的回调函数
    public static String pathLoginStaffCache = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "huofu"
            + File.separator + "ImageCache" + File.separator;

    public static final int PRINT_PACKAGE = 6;
    public static final int PRINT_EATIN = 7;

    public long mLastTime = 0;
    public int count = 0;
    public long timeWaitingChoose = 0;

    public TextView tvInputOrder = null;
    public static int take_mode = -1;

    public View gridChildChoose = null;

    public static ActivityManager mActivityManager = null;

    public static final int SHOW_CHOOSE_PACKAGED_DIALOG = 0;
    public static final int SHOW_ERROR_MESSAGE = 1;
    public static final int PRINT_RECEIPT = 2;//打印取餐条 在一张纸上
    public static final int PERFORM_TAKEUP = 3;
    public static final int CLOSE_DIALOG_CHOOSE_PACKAGED = 4;
    public static final int SHOW_LOADING_TEXT = 5;
    public static final int HIDE_LOADING = 6;
    public static final int SHOW_TAKEUP_MAIN = 7;//显示取餐主界面
    public static final int CLEAR_INPUT_CONTENT = 8;//情况输入内容
    public static final int TEST_PRINTER_AND_SHOW_DIAL = 9;//测试打印机是否连接并进入取餐输入界面
    public static final int SHOW_LOGIN_OVERDATED = 10;//登录过期
    public static final int SHOW_ERROR_MESSAGE_PRINT_AGAIN = 11;//打印失败弹出再次打印窗口
    public static final int SHOW_ERROR_MESSAGE_WITH_TITLE = 12;
    public static final int CLEAN_CLOSE_DIALOG = 13;//清空输入区域并关闭弹窗
    public static final int SHOW_ERROR_MESSAGE_WITH_TWO_OPTIONS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        CommonUtils.LogWuwei(tag, "-----------------------------自助取号开启--------------------");
        ViewServer.get(this).addWindow(this);

        setContentView(R.layout.take_up_main);

        init();

        widgetConfigure();

        showTakeUpMainWindow();

        threadHandlerInit();

        getTimeBucketList();//获取营业时间段

        launchPadConfigure();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        active = false;

        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }

        if (dialog_show_error != null) {
            dialog_show_error.dismiss();
        }

        if (dialog_show_print_error != null) {
            dialog_show_print_error.dismiss();
        }

        if (dialog_show_error_with_title != null) {
            dialog_show_error_with_title.dismiss();
        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
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

    protected void onDestroy() {
        super.onDestroy();
        CommonUtils.LogWuwei(tag, "-----------------------------自助取号关闭--------------------");

        CommonUtils.sendMailToMe(ctxt, 1);


        active = false;
        ViewServer.get(this).removeWindow(this);

        handler.removeMessages(SHOW_CHOOSE_PACKAGED_DIALOG);
        handler.removeMessages(SHOW_ERROR_MESSAGE);
        handler.removeMessages(PRINT_RECEIPT);
        handler.removeMessages(SHOW_ERROR_MESSAGE_WITH_TITLE);
        handler.removeMessages(PERFORM_TAKEUP);
        handler.removeMessages(CLOSE_DIALOG_CHOOSE_PACKAGED);
        handler.removeMessages(SHOW_LOADING_TEXT);
        handler.removeMessages(HIDE_LOADING);
        handler.removeMessages(SHOW_TAKEUP_MAIN);
        handler.removeMessages(CLEAR_INPUT_CONTENT);
        handler.removeMessages(TEST_PRINTER_AND_SHOW_DIAL);
        handler.removeMessages(SHOW_LOGIN_OVERDATED);
        handler.removeMessages(SHOW_ERROR_MESSAGE_PRINT_AGAIN);

        stopCalcWaitingDialTimer();
        stopCalcWaitingTimer();

        ctxt = null;
        handler = null;
        handlerThread = null;
        System.gc();
    }

    public void init() {

        MainApplication.setmActivity(this);
        ctxt = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        buttonTakeUpStart = (Button) findViewById(R.id.button_takeUp_start);
        buttonCloseInDialDialog = (Button) findViewById(R.id.button_takeup_dial_close);
        rlCloseInDialDialog = (RelativeLayout) findViewById(R.id.rl_takeup_dial_close);
        buttonTakeupMenu = (Button) findViewById(R.id.button_action_bar_takeup_menu);

        tvInputOrder = (TextView) findViewById(R.id.textview_dialog_takeup_dial_content_input);
        SpannableString tvInputOrderHint = new SpannableString("请输入取餐码");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(25, true);
        tvInputOrderHint.setSpan(ass, 0, tvInputOrderHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        (tvInputOrder).setHint(new SpannableString(tvInputOrderHint));

        tvMerchantStoreNameInTakeUpDial = (TextView) findViewById(R.id.tv_takeup_dial_merchant_store_name);

        tvMerchantNameInTakeUpMain = (TextView) findViewById(R.id.tv_takeup_main_merchant_name);
        tvMerchantStoreNameInTakeUpMain = (TextView) findViewById(R.id.tv_takeup_main_merchant_store_name);


        rlTakeUpStart = (RelativeLayout) findViewById(R.id.rl_takeup_main);
        rlTakeUpDial = (RelativeLayout) findViewById(R.id.rl_takeup_dial);
        rlTakeupDialOrChoosePackaged = (RelativeLayout) findViewById(R.id.rl_takeup_dial_or_choose_packaged);
        rlTakeupChildDial = (RelativeLayout) findViewById(R.id.rl_takeup_child_dial);

        list_meal_bucket = new ArrayList<MealBucket>();
        mslidingMenu = new SimpleSideDrawer(TakeUpActivity.this);
        gridChildChoose = ((LayoutInflater) ctxt.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_choose_take_mode, null);
        handler = mUiHandler;
    }

    public void widgetConfigure() {
        mslidingMenu.setLeftBehindContentView(R.layout.navigation_bar_vertical_layout);
        buttonTakeUpStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!CommonUtils.isITimeBucketList(TakeUpActivity.list_meal_bucket, ctxt, handler)) {
                    CommonUtils.sendMsg("当前时间不在营业时间段", SHOW_ERROR_MESSAGE, handler);
                    return;
                }

                CommonUtils.sendMsg(null, TEST_PRINTER_AND_SHOW_DIAL, handler);
            }
        });

        String textDate = CommonUtils.getDateWeekDSay(0);
        String merchantName = LocalDataDeal.readFromLocalMerchantName(ctxt);
        String storeName = LocalDataDeal.readFromLocalStoreName(ctxt);
        tvMerchantNameInTakeUpMain.setText(merchantName);
        tvMerchantStoreNameInTakeUpMain.setText(storeName);
        tvMerchantStoreNameInTakeUpDial.setText(storeName);

        View.OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                input_code = "";

                stopCalcWaitingTimer();
                stopCalcWaitingDialTimer();

                tvInputOrder.setText(input_code);
                showTakeUpMainWindow();

            }
        };
        buttonCloseInDialDialog.setOnClickListener(ocl);
        rlCloseInDialDialog.setOnClickListener(ocl);

        buttonTakeupMenu.setAlpha(0.0f);
        buttonTakeupMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Long nowTime = System.currentTimeMillis() / 1000;
                CommonUtils.LogWuwei(tag, "clicked and count is " + count + " mLastTime is " + mLastTime + " nowTime is " + nowTime +
                        "\nmLastTime - nowTime  is " + (nowTime - mLastTime));
                count++;
                if ((nowTime - mLastTime) < 5) {
                    if (count == 2) {
                        count = 0;
                        //mslidingMenu.toggleLeftDrawer();
                        //widgetCofigureNavigationBar();

                        Intent i = new Intent(TakeUpActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    count = 0;
                }
                mLastTime = nowTime;


            }
        });

        bitmapUtils = new BitmapUtils(getApplicationContext(),
                pathLoginStaffCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,

    }

    public void threadHandlerInit() {
        new Thread() {
            public void run() {
                Looper.prepare();
                handlerThread = new Handler() {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case PRINT_RECEIPT:
                                OrderDetailInfo odi = (OrderDetailInfo) msg.obj;
                                byte[] printerInfo = new byte[0];
                                int result = 0;
                                sendUIMessage(SHOW_LOADING_TEXT, "开始打印小票");
                                try {
                                    printerInfo = TemplateModulsParse.parseTemplateModuleManualCheckout(odi, 1);
                                    String ip = (String) ((Map) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt)).get("ip");
                                    result= odi.writeToPrinterInfo(ctxt, printerInfo, ip);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                sendUIMessage(HIDE_LOADING, "");
                                if (result == -1) {
                                    sendUIMessage(SHOW_ERROR_MESSAGE, "打印失败，请联系店内工作人员，谢谢");
                                    printFailedOdi = odi;
                                }
                                sendUIMessage(CLOSE_DIALOG_CHOOSE_PACKAGED, "");
                                break;
                        }
                    }

                    ;
                };
                Looper.loop();
            }
        }.start();

    }

    @Override
    protected void dealWithmessage(Message msg) {
        switch (msg.what) {
            case SHOW_CHOOSE_PACKAGED_DIALOG:
                OrderDetailInfo odi = (OrderDetailInfo) msg.obj;
                String order_id = odi.order_id;
                showChoosePackageWindow(odi, order_id);
                CommonUtils.LogWuwei(tag, CommonUtils.getTimeStampNow() + "查询订单信息完毕");
                CommonUtils.LogWuwei(tag, "msg received  SHOW_CHOOSE_PACKAGED_DIALOG");
                break;
            case SHOW_ERROR_MESSAGE:
                showDialogErrorOneOption((String)msg.obj);
                break;
            case SHOW_ERROR_MESSAGE_WITH_TITLE:
                Map map = (Map) msg.obj;
                showDialogErrorWithTitle((String) map.get("title"), (String) map.get("errMsg"));
                break;
            case PERFORM_TAKEUP:
                takeUpKeyBoardEnterClickListner(null);
                break;
            case CLOSE_DIALOG_CHOOSE_PACKAGED:
                //dialog_choose_packaged.hide();
                showTakeUpMainWindow();
                break;
            case SHOW_LOADING_TEXT:
                String content = (String) msg.obj;
                showLoadingDialog(content);
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case SHOW_TAKEUP_MAIN:
                showTakeUpMainWindow();
                break;
            case CLEAR_INPUT_CONTENT:
                if (input_code != null && input_code.length() > 1) {
                    input_code = "";
                    tvInputOrder.setText(input_code);
                }
                break;
            case TEST_PRINTER_AND_SHOW_DIAL:
                sendUIMessage(HIDE_LOADING,"");
                showTakeUpDialWindow();
                break;
            case SHOW_LOGIN_OVERDATED:
                showDialogErrorOneOption((String) msg.obj);
                break;
            case SHOW_ERROR_MESSAGE_PRINT_AGAIN:
                String errMsg = (String) msg.obj;
                showDialogPrintFailedError(errMsg);
                break;
            case CLEAN_CLOSE_DIALOG:
                cleanAndCloseDialog();
                break;
        }
    }

    /**
     * 获取营业时间段
     */
    public void getTimeBucketList() {
        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                list_meal_bucket = (List<MealBucket>) object;
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(SHOW_ERROR_MESSAGE, response.error_code);
            }

        });
    }


    /**
     * 显示取号的选号界面
     */
    public void showTakeUpDialWindow() {
        rlTakeUpStart.setVisibility(View.INVISIBLE);
        rlTakeupChildDial.setVisibility(View.VISIBLE);
        rlTakeUpDial.setVisibility(View.VISIBLE);
        rlTakeUpDial.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_horizontal_in));

        timeWaitingChoose = System.currentTimeMillis() / 1000;
        startCalcWaitingDialTimer();
    }

    /**
     * 显示主窗口
     */
    public void showTakeUpMainWindow() {

        rlTakeUpDial.setVisibility(View.INVISIBLE);

        if (gridChildChoose.VISIBLE == View.VISIBLE) {
            rlTakeupDialOrChoosePackaged.removeView(gridChildChoose);
        }

        rlTakeUpStart.setVisibility(View.VISIBLE);

        rlTakeUpStart.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

    }

    /****
     * 显示带有标题的异常窗口
     *
     * @param title
     */
    public void showDialogErrorWithTitle(String title, String msg) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_show_error_with_title_one_option, null);


        TextView tvTitle = (TextView) grid.findViewById(R.id.dialog_show_error_with_title_one_option_title);
        TextView tvContent = (TextView) grid.findViewById(R.id.dialog_show_error_with_title_one_option_conetent);
        final Button btn_close = (Button) grid.findViewById(R.id.dialog_show_error_with_title_one_option_close);

        tvTitle.setText(title);
        tvContent.setText(msg);

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                {
                    if (dialog_show_error_with_title != null) {
                        if (dialog_show_error_with_title.isShowing()) {
                            dialog_show_error_with_title.dismiss();
                        }
                    }
                }

            }
        };
        btn_close.setOnClickListener(ocl);

        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);

        if (dialog_show_error_with_title == null) {
            dialog_show_error_with_title = new PopupWindow(grid, width, height, true);
        }

        dialog_show_error_with_title.setBackgroundDrawable(new BitmapDrawable());
        dialog_show_error_with_title.setContentView(grid);
        dialog_show_error_with_title.setFocusable(true);
        dialog_show_error_with_title.setOutsideTouchable(true);
        dialog_show_error_with_title.setAnimationStyle(R.style.AutoDialogAnimation);
        dialog_show_error_with_title.showAtLocation(buttonTakeUpStart, Gravity.NO_GRAVITY, 0, 0);

    }


    /***
     * 打印失败的弹窗
     *
     * @param errMsg
     */
    public void showDialogPrintFailedError(String msg) {
        timeWaitingChoose = System.currentTimeMillis() / 1000;

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialg_show_error, null);


        TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
        tvContent.setText(msg);

        final Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);
        final Button btn_print_again = (Button) grid.findViewById(R.id.btn_dialog_error_sure);

        OnClickListener ocl = new OnClickListener() {

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
        btn_print_again.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.sendMsg("正在打印", SHOW_LOADING_TEXT, handler);

                if (dialog_show_print_error != null) {
                    if (dialog_show_print_error.isShowing()) {
                        dialog_show_print_error.dismiss();
                    }
                }

                Message msg = new Message();
                msg.what = TakeUpActivity.PRINT_RECEIPT;
                msg.obj = printFailedOdi;
                handlerThread.sendMessage(msg);

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
        dialog_show_print_error.showAtLocation(buttonTakeUpStart, Gravity.NO_GRAVITY, 0, 0);


    }


    /**
     * 选择打包还是堂食
     *
     * @param odi
     */
    public void showChoosePackageWindow(final OrderDetailInfo odi, final String order_id) {

        input_code = "";
        tvInputOrder.setText(input_code);
        timeWaitingChoose = System.currentTimeMillis() / 1000;
        stopCalcWaitingDialTimer();
        startCalcWaitingTimer();

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridChildChoose = inflater.inflate(R.layout.dialog_choose_take_mode, null);

        TextView tv_content = (TextView) gridChildChoose.findViewById(R.id.tv_dialog_choose_take_mode_content);
        Button btn_eatin = (Button) gridChildChoose.findViewById(R.id.button_dialog_choose_take_mode_eatin);
        Button btn_packaged = (Button) gridChildChoose.findViewById(R.id.button_dialog_choose_take_mode_packaged);
        Button btn_close = (Button) gridChildChoose.findViewById(R.id.button_takeup_choose_close);

        CommonUtils.LogWuwei(tag, CommonUtils.getTimeStampNow() + "流水号:" + odi.take_serial_number + " 选择打包还是堂食");
        String content = "";
        if (odi != null) {
            if (odi.list_charge_items_all != null) {
                for (int i = 0; i < odi.list_charge_items_all.size(); i++) {
                    ChargItem ci = odi.list_charge_items_all.get(i);
                    if (i < odi.list_charge_items_all.size() - 1) {
                        content += (ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.charge_item_amount) + "， ");
                    } else {
                        content += (ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.charge_item_amount));
                    }

                }
            }
        }

        //tv_content.setText(odi.orderContent);

        tv_content.setText(content);

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {

                stopCalcWaitingTimer();

                sendUIMessage(SHOW_LOADING_TEXT,"测试打印机是否连接");

                // TODO Auto-generated method stub
                if (v.getId() == R.id.button_dialog_choose_take_mode_eatin)//堂食
                {
                    CommonUtils.LogWuwei(tag, CommonUtils.getTimeStampNow() + "流水号:" + odi.take_serial_number + "堂食");
                    take_mode = 1;
                } else if (v.getId() == R.id.button_dialog_choose_take_mode_packaged) {
                    take_mode = 2;
                }

                sendUIMessage(SHOW_LOADING_TEXT, "获取订单详情");
                ApisManager.OrderTakeCode(take_mode, odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {

                    }
                });

            }
        };
        btn_eatin.setOnClickListener(ocl);
        btn_packaged.setOnClickListener(ocl);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = 874 * 2;
        lp.height = 595 * 2;
        lp.y = 75;
        gridChildChoose.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));


        rlTakeupChildDial.setVisibility(View.INVISIBLE);
        rlTakeupDialOrChoosePackaged.addView(gridChildChoose);

        //rlTakeUpDial.setCanceledOnTouchOutside(false);
        //getWindow().setAttributes(lp);

        gridChildChoose.findViewById(R.id.rl_takeup_choose_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stopCalcWaitingTimer();
                rlTakeupDialOrChoosePackaged.removeView(gridChildChoose);
                rlTakeupChildDial.setVisibility(View.VISIBLE);
                showTakeUpDialWindow();
            }
        });
        btn_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stopCalcWaitingTimer();
                rlTakeupDialOrChoosePackaged.removeView(gridChildChoose);
                rlTakeupChildDial.setVisibility(View.VISIBLE);
                showTakeUpDialWindow();
            }
        });

    }

    /**
     * 开启定时器
     */
    public void startCalcWaitingTimer() {
        timerCalcChooseWaiting = new Timer();
        timerTaskCalcChooseWaiting = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                long nowTime = System.currentTimeMillis() / 1000;
                CommonUtils.LogWuwei(tag, "定时任务中，当前等待时间为:" + (nowTime - timeWaitingChoose));
                if ((nowTime - timeWaitingChoose) == 30) {
                    if (rlTakeUpStart.getVisibility() == View.INVISIBLE) {
                        CommonUtils.sendMsg(null, SHOW_TAKEUP_MAIN, handler);
                        CommonUtils.sendMsg(null, CLEAN_CLOSE_DIALOG, handler);
                    }

                } else if ((nowTime - timeWaitingChoose) > 30) {
                    stopCalcWaitingTimer();
                }

            }
        };

        timerCalcChooseWaiting.schedule(timerTaskCalcChooseWaiting, 0, 1000);
    }


    /**
     * 输入开启定时器
     */
    public void startCalcWaitingDialTimer() {
        timerCalcChooseWaitingDial = new Timer();
        timerTaskCalcChooseWaitingDial = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                long nowTime = System.currentTimeMillis() / 1000;
                CommonUtils.LogWuwei(tag, "定时任务中，当前等待时间为:" + (nowTime - timeWaitingChoose));
                if ((nowTime - timeWaitingChoose) == 60) {
                    if (rlTakeUpStart.getVisibility() == View.INVISIBLE) {
                        CommonUtils.sendMsg(null, SHOW_TAKEUP_MAIN, handler);
                        CommonUtils.sendMsg(null, CLEAN_CLOSE_DIALOG, handler);
                    }
                } else if ((nowTime - timeWaitingChoose) > 60) {
                    stopCalcWaitingDialTimer();
                }

            }
        };

        timerCalcChooseWaitingDial.schedule(timerTaskCalcChooseWaitingDial, 0, 1000);
    }


    /**
     * 关闭输入计时器
     */
    public void stopCalcWaitingDialTimer() {

        if (timerCalcChooseWaitingDial != null) {
            timerCalcChooseWaitingDial.cancel();
        }

        if (timerTaskCalcChooseWaitingDial != null) {
            timerTaskCalcChooseWaitingDial.cancel();
        }
    }


    /**
     * 关闭计时器
     */
    public void stopCalcWaitingTimer() {

        if (timerCalcChooseWaiting != null) {
            timerCalcChooseWaiting.cancel();
        }

        if (timerTaskCalcChooseWaiting != null) {
            timerTaskCalcChooseWaiting.cancel();
        }
    }


    /***
     * 情况输入区域并关闭弹窗
     */
    public void cleanAndCloseDialog() {
        input_code = "";
        tvInputOrder.setText("");
        if (dialog_show_error != null && dialog_show_error.isShowing()) {
            dialog_show_error.dismiss();
        }
        if (dialog_show_print_error != null && dialog_show_error.isShowing()) {
            dialog_show_print_error.dismiss();
        }
        if (dialog_show_error_with_title != null && dialog_show_error_with_title.isShowing()) {
            dialog_show_error_with_title.dismiss();
        }
    }


    public void takeUpKeyBoardNumClickListner(View view) {

        timeWaitingChoose = System.currentTimeMillis() / 1000;
        String text = ((Button) view).getText().toString();

        if (input_code.length() <= 7) {
            input_code += text;
            tvInputOrder.setText(input_code);
        }
    }

    public void takeUpKeyBoardEnterClickListner(View view) {
        timeWaitingChoose = System.currentTimeMillis() / 1000;
        //CommonUtils.sendMsg("判断打印机是否连接", SHOW_LOADING_TEXT, handler);
        if (input_code != null) {
            if (input_code.length() < 6) {
                if (input_code.equals("")) {
                    CommonUtils.sendMsg("请输入取餐码", SHOW_ERROR_MESSAGE, handler);
                } else {
                    CommonUtils.sendMsg("取餐码位数不够，无法取餐", SHOW_ERROR_MESSAGE, handler);
                }
                return;
            } else {
                CommonUtils.sendMsg("正在取号...", SHOW_LOADING_TEXT, handler);
            }
        }

        final String ipTakeup = (String) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");
        CommonUtils.LogWuwei(tag, "ipTakeup is " + ipTakeup);

        sendUIMessage(SHOW_LOADING_TEXT, "判断订单是否有效");
        ApisManager.QueryOrderTakeput(input_code, new ApiCallback() {
            @Override
            public void success(Object object) {

            }
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING, "");
                sendUIMessage(SHOW_ERROR_MESSAGE,""+response.error_message);
            }
        });
    }

    public void takeUpKeyBoardClearClickListner(View view) {

        timeWaitingChoose = System.currentTimeMillis() / 1000;
        input_code = "";
        tvInputOrder.setText(input_code);
        CommonUtils.LogWuwei(tag, "clear ");
    }

    public void takeUpKeyBoardBackClickListner(View view) {

        timeWaitingChoose = System.currentTimeMillis() / 1000;
        if (!input_code.equals("")) {
            StringBuffer sb = new StringBuffer(input_code);
            sb = sb.deleteCharAt(sb.length() - 1);
            input_code = sb.toString();
        }

        tvInputOrder.setText(input_code);
        CommonUtils.LogWuwei(tag, "clear ");
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
