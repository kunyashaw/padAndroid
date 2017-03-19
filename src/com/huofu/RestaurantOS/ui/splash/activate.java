package com.huofu.RestaurantOS.ui.splash;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.support.titanic.Titanic;
import com.huofu.RestaurantOS.support.titanic.TitanicTextView;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.FIleUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/***
 * 展示激活界面，获取验证码并进行激活
 *
 * @author kunyashaw
 */

public class activate extends BaseActivity {

    public static boolean active = false;

    public static String tag = "activate";
    public static Context ctxt = null;


    public static FtpServer ftpServer = null;
    public static String pathFtpCache = Environment.getExternalStorageDirectory() + File.separator + "huoFu" + File.separator + "FtpCache" + File.separator;


    private RelativeLayout rl_activate = null;
    private RelativeLayout rl_activate_input = null;
    private RelativeLayout rl_activate_qrscan_ready = null;

    public Button buttonActivate = null;
    public Button buttonSwitchQrScan = null;
    public Button buttonSwitchInputCode = null;

    public EditText etActivteCode = null;
    public TextView textviewScanStauts = null;

    public static boolean flagAuthSuccess = false;//二维码授权是否成功，如果成功，则停止扫描二维码扫描状态的定时器
    public boolean flagInQrcodeDialog = false;//是否在二维码显示状态
    public static boolean flagWaitingCmd = false;//是否在扫码后等待用户操作，如果是，这个时候停止刷新二维码

    //public static Handler mUiHandler = null;

    Titanic titanic = null;//new Titanic();
    TitanicTextView ttv = null;//new TitanicTextView(getApplicationContext());
    Dialog authDialog = null;
    PopupWindow dialog_loading = null;
    PopupWindow dialog_show_error = null;
    Dialog authFailedDialog = null;
    PopupWindow pop_loading = null;


    public static ImageView imageViewQrcode = null;

    public static Timer timerQrcode = null;//检测员工二维码登录状态的定时器
    public static TimerTask timerTaskQrcode = null;//检测员工二维码登录状态的定时任务
    public static String tokenQrcode = null;//二维码登录的token
    public static long qrcodeCreateTime = 0;

    public boolean hasFocus = false;

    public static BitmapUtils bitmapUtils;
    public static BitmapDisplayConfig bigPicDisplayConfig;
    public static DefaultBitmapLoadCallBack<ImageView> callback;
    public static String pathAcitvateCache = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "activateCache" + File.separator;

    public static final int ERROR = -1;
    public static final int AUTH_FAILED = 0;
    public static final int AUTH_SUCCESS = 1;
    public static final int GET_QRCODE_SUCCESS = 2;
    public static final int SCAN_QRCODE_SUCCESS = 3;
    public static final int GET_QRCODE = 4;
    public static final int SHOW_LOADING_TEXT = 5;
    public static final int HIDE_LOADING = 6;
    public static final int SHOW_ERROR_MESSAGE = 7;
    public static final int SHOW_LOGIN_OVERDATED = 8;
    public static final int UPDATE_QRCODE_SCAN_STATUS = 9;
    public static final int SHOW_QRCODE_REFRSH_AGAIN = 10;
    public static final int STOP_SCAN_QRCODE_TIMER = 11;


    boolean flagActivate = true;

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        active = true;

        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        active = false;
        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        ViewServer.get(this).addWindow(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activate);

        init();

        widgetConfigure();

        String clientIString = LocalDataDeal.readFromLocalClientId(getApplicationContext());
        if (clientIString == null || clientIString.equals("")) {
            buttonSwitchQrScan.performClick();

            UmengUpdateAgent.update(this);
        } else if (clientIString != null && !clientIString.equals("")) {
            flagActivate = false;
            stopQrScanTimer();
            finishWithNextActivity(LoginActivity.class);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        MobclickAgent.onResume(this);

        ViewServer.get(this).setFocusedWindow(this);

        if (authFailedDialog != null) {
            if (authFailedDialog.isShowing()) {
                authFailedDialog.hide();
            }
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        active = false;

        ViewServer.get(this).removeWindow(this);

        stopQrScanTimer();

        mUiHandler.removeMessages(SHOW_LOADING_TEXT);
        mUiHandler.removeMessages(HIDE_LOADING);
        mUiHandler.removeMessages(AUTH_FAILED);
        mUiHandler.removeMessages(AUTH_SUCCESS);
        mUiHandler.removeMessages(ERROR);
        mUiHandler.removeMessages(GET_QRCODE);
        mUiHandler.removeMessages(GET_QRCODE_SUCCESS);
        mUiHandler.removeMessages(SCAN_QRCODE_SUCCESS);

    }

    /*****************************************  初始化及配置 ********************************************/
    /**
     * 控件的初始化
     */
    public void init() {

        MainApplication.setmActivity(this);
        rl_activate = (RelativeLayout) findViewById(R.id.rl_activate);
        rl_activate_input = (RelativeLayout) findViewById(R.id.rl_activate_code_input);
        rl_activate_qrscan_ready = (RelativeLayout) findViewById(R.id.rl_activate_qrcode_scan_ready);


        ctxt = getApplicationContext();

        buttonActivate = (Button) findViewById(R.id.buttonActivate);
        buttonSwitchInputCode = (Button) findViewById(R.id.button_switch_acitvate_keyboard_way);
        buttonSwitchQrScan = (Button) findViewById(R.id.button_activate_switch_to_qrscan);

        textviewScanStauts = (TextView) findViewById(R.id.textview_activate_status);

        etActivteCode = (EditText) findViewById(R.id.editTextActivateCode);

        titanic = new Titanic();
        ttv = new TitanicTextView(getApplicationContext());

        bitmapUtils = new BitmapUtils(getApplicationContext(), pathAcitvateCache, 150 * 1024 * 1024, 150 * 1024 * 1024);
        bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
        callback = new DefaultBitmapLoadCallBack<ImageView>() {
            public void onLoadCompleted(ImageView container, String uri, android.graphics.Bitmap bitmap, BitmapDisplayConfig config, com.lidroid.xutils.bitmap.callback.BitmapLoadFrom from) {

                CommonUtils.sendMsg("", HIDE_LOADING, mUiHandler);

                imageViewQrcode.setVisibility(View.VISIBLE);
                imageViewQrcode.setBackgroundDrawable(new BitmapDrawable(bitmap));
                stopQrScanTimer();


                timerQrcode = new Timer();
                timerTaskQrcode = new TimerTask() {

                    @Override
                    public void run() {

                        if (!flagActivate) {
                            timerTaskQrcode.cancel();
                            timerQrcode.cancel();
                            return;
                        }

                        long qrcodeLastTime = System.currentTimeMillis() / 1000-qrcodeCreateTime;
                        CommonUtils.LogWuwei(tag,"qrcodeLastTime is "+qrcodeLastTime);
                        if(qrcodeLastTime >300)
                        {
                            timerTaskQrcode.cancel();
                            timerQrcode.cancel();
                            sendUIMessage(SHOW_QRCODE_REFRSH_AGAIN,"");
                            return;
                        }

                        // TODO Auto-generated method stub
                        if (active) {
                            ApisManager.ScanAuthQrcode(tokenQrcode, new ApiCallback() {
                                @Override
                                public void success(Object object) {
                                    sendUIMessage(HIDE_LOADING,"");
                                    JSONObject oj = (JSONObject) object;

                                    if (Integer.parseInt(oj.getString("status")) != 1) {
                                        return;
                                    }
                                    String status = oj.getString("related_status");


                                    if (status.equals("5") || status.equals("3") && activate.active)//用户确认授权
                                    {
                                        if (activate.flagAuthSuccess) {
                                            return;
                                        }

                                        activate.flagAuthSuccess = true;

                                        if (activate.active) {
                                            CommonUtils.sendMsg(null, activate.STOP_SCAN_QRCODE_TIMER, mUiHandler);
                                            String master_key = oj.getString("master_key");
                                            LocalDataDeal.writeToLocalMasterKey(master_key, ctxt);

                                            String client_id = oj.getString("client_id");
                                            LocalDataDeal.writeToLocalClientId(client_id, ctxt);
                                            CommonUtils.sendMsg("", SCAN_QRCODE_SUCCESS, mUiHandler);
                                        }

                                    } else if (status.equals("1")) {
                                        CommonUtils.sendMsg("请在微信端完成绑定", UPDATE_QRCODE_SCAN_STATUS, mUiHandler);
                                    } else if (status.equals("2")) {
                                        CommonUtils.sendMsg("正在微信端创建商户", UPDATE_QRCODE_SCAN_STATUS, mUiHandler);
                                    } else if (status.equals("4")) {
                                        activate.flagWaitingCmd = true;
                                        CommonUtils.sendMsg("请在微信端选择店铺", UPDATE_QRCODE_SCAN_STATUS, mUiHandler);
                                    } else if (status.equals("6"))//收银台数量达到上限，无法再次授权
                                    {
                                        activate.flagWaitingCmd = false;
                                        activate.stopQrScanTimer();
                                        CommonUtils.sendMsg("收银台数量超过限制", AUTH_FAILED, mUiHandler);
                                    } else if (status.equals("7")) {
                                        activate.flagWaitingCmd = false;
                                        activate.stopQrScanTimer();
                                        CommonUtils.sendMsg("创建店铺数量超过限制", AUTH_FAILED, mUiHandler);
                                    } else if (status.equals("0")) {
                                        activate.flagWaitingCmd = false;
                                        activate.stopQrScanTimer();
                                        CommonUtils.sendMsg("无权开启收银台", AUTH_FAILED, mUiHandler);
                                    }
                                }

                                @Override
                                public void error(BaseApi.ApiResponse response) {
                                    sendUIMessage(HIDE_LOADING,"");
                                }
                            });
                        }

                    }
                };
                timerQrcode.schedule(timerTaskQrcode, 0, 1000);
            }
        };
        imageViewQrcode = (ImageView) findViewById(R.id.imageview_activate_qrcode_scan);

        PackageManager manager;
        PackageInfo info = null;
        manager = this.getPackageManager();
        String versionName = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            versionName = info.versionName;
            String version = CommonUtils.getSringAfterChar(versionName, 'V');
            LocalDataDeal.writeToLocalVersionName(ctxt, version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控件的配置
     */
    public void widgetConfigure() {

        buttonActivate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (authDialog == null) {
                    authDialog = new Dialog(activate.this, R.style.PauseDialog);
                }
                if (ttv.getParent() != null) {
                    ttv = new TitanicTextView(ctxt);
                }


                String key = etActivteCode.getText().toString();
                if (key.length() >= 16) {
                    CommonUtils.LogWuwei(tag, "key is " + key);
                    CommonUtils.sendMsg("正在授权", activate.SHOW_LOADING_TEXT, mUiHandler);
                    activate(ctxt, key);
                } else {
                    CommonUtils.sendMsg("授权码位数不够", activate.SHOW_ERROR_MESSAGE, mUiHandler);
                }


            }
        });

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // TODO Auto-generated method stub
                for (int i = start; i < end; i++) {
                    //在这里判断输入的只能是英文字母和符号'-'
                    if (!Character.isLetter(source.charAt(i))
                            && source.charAt(i) != '-') {
                        return "";
                    }
                }
                return null;
            }
        };

        InputFilter filterLength = new InputFilter.LengthFilter(19);

        etActivteCode.setCursorVisible(false);
        etActivteCode.setFilters(new InputFilter[]{filter, filterLength});
        etActivteCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
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

                etActivteCode.setCursorVisible(true);
                etActivteCode.removeTextChangedListener(this);// 解除文字改变事件
                etActivteCode.setText(s.toString().toUpperCase());// 转换
                etActivteCode.setSelection(s.toString().length());// 重新设置光标位置
                etActivteCode.addTextChangedListener(this);// 重新绑
                String licensePlateNumber = etActivteCode.getText().toString().trim();

                if (s.length() == 19) {
                    InputMethodUtils.HideKeyboard(etActivteCode);
                }

            }
        });
        etActivteCode.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    CommonUtils.LogWuwei(tag, "delete");
                    etActivteCode.setCursorVisible(false);
                }
                return false;
            }
        });

        rl_activate_qrscan_ready.setVisibility(View.INVISIBLE);


        OnClickListener oclGetQrCode = new OnClickListener() {
            @Override
            public void onClick(View v) {
                flagInQrcodeDialog = true;
                CommonUtils.sendMsg("获取二维码", SHOW_LOADING_TEXT, mUiHandler);

                if (LocalDataDeal.readFromLocalPublicKey(ctxt) == null || LocalDataDeal.readFromLocalPublicKey(ctxt).equals("")) {
                    ApisManager.GetPublicKey(new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            sendUIMessage(HIDE_LOADING,"");
                            try {
                                org.json.JSONObject obj = new org.json.JSONObject((String) object);
                                String public_key = obj.getString("public_key");
                                LocalDataDeal.writeToLocalPublicKey(public_key, ctxt);//key是经过base64处理的字符串
                            } catch (Exception e) {
                            }
                            CommonUtils.sendMsg("", GET_QRCODE, mUiHandler);
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {
                            sendUIMessage(HIDE_LOADING,"");
                            sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
                        }
                    });
                } else {
                    CommonUtils.sendMsg("", GET_QRCODE, mUiHandler);
                }


                rl_activate_input.setVisibility(View.INVISIBLE);
                rl_activate_qrscan_ready.setVisibility(View.VISIBLE);
                imageViewQrcode.setVisibility(View.INVISIBLE);
                rl_activate_qrscan_ready.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

                textviewScanStauts.setTextColor(Color.parseColor("#898989"));
                textviewScanStauts.setText(R.string.activateByQrCodeTips);

                rl_activate_qrscan_ready.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_left_in));
                rl_activate_input.startAnimation(new AnimationUtils().loadAnimation(ctxt,R.anim.slide_right_out));
            }
        };

        buttonSwitchQrScan.setOnClickListener(oclGetQrCode);
        findViewById(R.id.rl_switch_activate_qrcode_way).setOnClickListener(oclGetQrCode);

        buttonSwitchInputCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                flagInQrcodeDialog = false;
                flagWaitingCmd = false;
                rl_activate_qrscan_ready.setVisibility(View.INVISIBLE);
                rl_activate_input.setVisibility(View.VISIBLE);
                rl_activate_input.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

                stopQrScanTimer();

                rl_activate_input.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_right_in));
                rl_activate_qrscan_ready.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.slide_left_out));

            }
        });

        findViewById(R.id.rl_switch_activate_keyboard_way).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                flagInQrcodeDialog = false;
                flagWaitingCmd = false;
                rl_activate_qrscan_ready.setVisibility(View.INVISIBLE);
                rl_activate_input.setVisibility(View.VISIBLE);
                rl_activate_input.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

                stopQrScanTimer();
            }
        });

        PackageManager manager;
        PackageInfo info = null;
        manager = this.getPackageManager();
        String versionName = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.tv_app_version)).setText(getResources().getString(R.string.app_name) + " V"+versionName);

        LocalDataDeal.writeToLocalVersion(versionName, ctxt);
        LocalDataDeal.writeToLocalVersionCode(info.versionCode,ctxt);

        ftpServiceStart();
    }

    @Override
    protected void dealWithmessage(Message msg) {
        switch (msg.what) {
            case ERROR:
                Bundle bd = (Bundle) msg.getData();
                CommonUtils.sendMsg(null, HIDE_LOADING, mUiHandler);
                showDialogError(bd.getString("reason"), 0);
                break;
            case AUTH_FAILED:
                stopQrScanTimer();
                buttonSwitchInputCode.performClick();
                CommonUtils.sendMsg(null, HIDE_LOADING, mUiHandler);
                showDialogError((String) msg.obj, 0);
                break;
            case AUTH_SUCCESS:
                CommonUtils.sendMsg(getString(R.string.readyToJumpToLogin), activate.SHOW_LOADING_TEXT, mUiHandler);
                mUiHandler.removeMessages(AUTH_SUCCESS);

                finishWithNextActivity(LoginActivity.class);
                break;
            case GET_QRCODE_SUCCESS://二维码获取成功
                qrcodeCreateTime = System.currentTimeMillis() / 1000;

                Bundle bdQrcode = (Bundle) msg.getData();

                tokenQrcode = bdQrcode.getString("token");
                String qrcode_url = bdQrcode.getString("qrcode_url");
                imageViewQrcode.setVisibility(View.VISIBLE);
                bitmapUtils.display(imageViewQrcode, qrcode_url, bigPicDisplayConfig, callback);
                break;
            case SCAN_QRCODE_SUCCESS://二维码授权成功
                flagAuthSuccess = true;
                //stopQrScanTimer();
                CommonUtils.sendMsg(null, AUTH_SUCCESS, mUiHandler);
                break;
            case GET_QRCODE://从服务器获取二维码
                CommonUtils.sendMsg(getString(R.string.loadingQrcode), SHOW_LOADING_TEXT, mUiHandler);
                ApisManager.GetAuthQrcode(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        sendUIMessage(HIDE_LOADING,"");
                        try {
                            JSONObject obj = (JSONObject) object;
                            String token = (String) obj.get("token");
                            String qrcode_url = (String) obj.get("qrcode_url");

                            Message msg = new Message();
                            msg.what = activate.GET_QRCODE_SUCCESS;
                            Bundle bd = new Bundle();
                            bd.putString("token", token);
                            bd.putString("qrcode_url", qrcode_url);
                            msg.setData(bd);
                            mUiHandler.sendMessage(msg);
                        } catch (Exception e) {
                        }

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        sendUIMessage(HIDE_LOADING,"");
                        sendUIMessage(SHOW_ERROR_MESSAGE, response.error_message + "");
                        CommonUtils.LogWuwei(tag, "GetAuthQrcode failed:" + response.error_message);
                    }
                });

                break;
            case SHOW_LOADING_TEXT:
                String content = (String) msg.obj;
                showLoadingDialog(content);
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case SHOW_ERROR_MESSAGE:
                String errMsg = (String) msg.obj;
                showDialogError(errMsg, 0);
                break;
            case UPDATE_QRCODE_SCAN_STATUS:
                textviewScanStauts.setText((String) msg.obj);
                break;
            case SHOW_QRCODE_REFRSH_AGAIN:
                dealQrcodeExpired();
                break;
            case STOP_SCAN_QRCODE_TIMER:
                stopQrScanTimer();
                break;

        }
    }

    /***
     * 激活
     *
     * @param ctxt
     * @param activateSerialString
     */
    public void activate(final Context ctxt, final String activateSerialString) {

        String publicKey = LocalDataDeal.readFromLocalPublicKey(ctxt);
        if (publicKey == null || publicKey.equals("")) {
            ApisManager.GetPublicKey(new ApiCallback() {
                @Override
                public void success(Object object) {
                    sendUIMessage(HIDE_LOADING,"");
                    activatByCode(activateSerialString);
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    sendUIMessage(HIDE_LOADING,"");
                    sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
                }
            });
        } else {
            activatByCode(activateSerialString);
        }

    }

    public void activatByCode(String activateSerialString) {
        ApisManager.AppAcitvate(activateSerialString, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("", HIDE_LOADING, mUiHandler);
                finishWithNextActivity(LoginActivity.class);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(HIDE_LOADING,"");
                sendUIMessage(SHOW_ERROR_MESSAGE,response.error_message);
            }
        });
    }


    /**
     * 显示加载中的窗口
     */
    public void showLoadingDialog(String text) {

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.loading_layout, null);

        TextView tv = (TextView) grid.findViewById(R.id.textview_loading_content);
        tv.setText(text);

        ImageView iv = (ImageView) grid.findViewById(R.id.imageview_loading_pic);
        iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));

        int width = CommonUtils.getScreenWidth(ctxt);
        int height = CommonUtils.getScreenHeight(ctxt);


        if (dialog_loading == null) {
            dialog_loading = new PopupWindow(grid, width, height, true);
        } else {
            dialog_loading.setContentView(grid);
        }

        dialog_loading.setFocusable(true);
        dialog_loading.setOutsideTouchable(true);
        dialog_loading.setAnimationStyle(R.style.AutoDialogAnimation);
        dialog_loading.setBackgroundDrawable(new BitmapDrawable());
        if (hasFocus) {
            dialog_loading.showAtLocation(rl_activate, Gravity.NO_GRAVITY, 0, 0);
        }

    }


    /**
     * 隐藏加载中的窗口
     */
    public void hideLoadingDialog() {

        if (dialog_loading != null) {
            if (dialog_loading.isShowing()) {
                dialog_loading.dismiss();
            }
        }

    }

    /****
     * 二维码过期后的处理
     */
    public void dealQrcodeExpired() {
        textviewScanStauts.setTextColor(getResources().getColor(R.color.Ponceau));
        textviewScanStauts.setText("二维码过期，点击刷新按钮刷新二维码");
        stopQrScanTimer();

        //Drawable d1 = new BitmapDrawable(bitmapNowQrcode);
        Drawable d2 = activate.this.getResources().getDrawable(R.drawable.refresh);
        //Drawable[] array = new Drawable[]{d1,d2};
        Drawable[] array = new Drawable[]{d2};

        //LayerDrawable ld = new LayerDrawable(array);
        imageViewQrcode.setBackgroundDrawable(d2);

        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                activate.qrcodeCreateTime = 0;
                CommonUtils.LogWuwei(tag, "\n\n\n\n\n刷新二维码\n\n\n\n");
                CommonUtils.sendMsg("", GET_QRCODE, mUiHandler);

                textviewScanStauts.setTextColor(Color.parseColor("#898989"));
                textviewScanStauts.setText("请使用微信扫码获取授权");
            }
        };

        imageViewQrcode.setOnClickListener(ocl);
        textviewScanStauts.setOnClickListener(ocl);
    }


    /**
     * 关掉扫描二维码的定时器
     */
    public static void stopQrScanTimer() {
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
     *
     * @param msg:错误内容
     * @param result:-1->退回到登录界面 0-》停留在当前窗口
     */
    public void showDialogError(String msg, final int result) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);

        TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
        final Button btn_close = (Button) grid.findViewById(R.id.btn_dialog_error_close);

        tvContent.setText(msg);

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
                        finishWithNextActivity(LoginActivity.class);
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
            dialog_show_error.showAtLocation(rl_activate, Gravity.NO_GRAVITY, 0, 0);
        } catch (Exception e) {

        }

    }


    public void finishWithNextActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        startActivity(intent);
        finish();
    }

    /**
     * 启动ftp服务
     */
    public static void ftpServiceStart() {
        try {
            FtpServerFactory serverFactory = new FtpServerFactory();

            ListenerFactory factory = new ListenerFactory();

            factory.setPort(12345);        // set the port of the listener

            FIleUtils.makeFolders(pathFtpCache);
            File file = new File(pathFtpCache);
            if (!file.isDirectory()) {
                file.mkdir();
            }

            String str = "" +
                    "ftpserver.user.admin.username=admin\n" +
                    "ftpserver.user.admin.userpassword=bff4d7685c1475b68c016c478a728b6e\n" +
                    "ftpserver.user.admin.homedirectory=/mnt/sdcard\n" +
                    "ftpserver.user.admin.enableflag=true\n" +
                    "ftpserver.user.admin.writepermission=true\n" +
                    "ftpserver.user.admin.maxloginnumber=250\n" +
                    "ftpserver.user.admin.maxloginperip=250\n" +
                    "ftpserver.user.admin.idletime=300\n" +
                    "ftpserver.user.admin.uploadrate=10000\n" +
                    "ftpserver.user.admin.downloadrate=10000\n";
                    /*"ftpserver.user.anonymous.userpassword=\n"+
					"ftpserver.user.anonymous.homedirectory=/mnt/sdcard\n"+  
					"ftpserver.user.anonymous.enableflag=true\n"+
					"ftpserver.user.anonymous.writepermission=false\n"+  
					"ftpserver.user.anonymous.maxloginnumber=20\n"+
					"ftpserver.user.anonymous.maxloginperip=2\n"+
					"ftpserver.user.anonymous.idletime=300\n"+
					"ftpserver.user.anonymous.uploadrate=4800\n"+   
					"ftpserver.user.anonymous.downloadrate=4800\n";*/

            FIleUtils.makeDirs(pathFtpCache);
            File files = new File(pathFtpCache + "ftpserver.properties");
            FIleUtils.writeFile(pathFtpCache + "ftpserver.properties", str);


            PropertiesUserManagerFactory usermanagerfactory = new PropertiesUserManagerFactory();
            usermanagerfactory.setFile(files);
            serverFactory.setUserManager(usermanagerfactory.createUserManager());


            serverFactory.addListener("default", factory.createListener());        // replace the default listener
            if (ftpServer != null) {
                ftpServer.stop();
            }

            FtpServer server = serverFactory.createServer();
            ftpServer = server;
            // start the server
            try {
                CommonUtils.LogWuwei(tag, "ready to start the ftp server");
                ftpServer.start();
            } catch (FtpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (Exception e) {
            HandlerUtils.showToast(activate.ctxt, "ftp启动失败");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (flagInQrcodeDialog)//如果是在二维码等待扫描界面，则按下返回，返回到输入授权码界面
            {
                stopQrScanTimer();
                flagInQrcodeDialog = false;
                flagWaitingCmd = false;
                rl_activate_qrscan_ready.setVisibility(View.INVISIBLE);
                rl_activate_input.setVisibility(View.VISIBLE);
                rl_activate_input.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


}