package com.huofu.RestaurantOS.ui.pannel.Lable;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.tspl.TscWifiActivity;
import com.huofu.RestaurantOS.widget.Keyboard;

/**
 * author: Created by zzl on 15/12/29.
 */
public class LableActivity extends BaseActivity{

    View.OnClickListener oclKeyboardEnter;
    Keyboard keyboard;
    Handler handlerPushMeal;
    Context ctxt;
    EditText etLableAddress;
    String lableAddress;
    String lableAddressTmp;
    TscWifiActivity TscEthernetDll = new TscWifiActivity();

    public static final int SHOW_LOADING_TEXT = 1;// 显示窗口（加载进度）
    public static final int HIDE_LOADING = 2;// 关闭窗口（加载进度）
    public static final int SHOW_ERROR_MESSAGE = 3;// 显示窗口（错误）
    public static final int SHOW_TOAST = 4;//toast


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        init();
        widgetCofigure();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, (String) msg.obj);
                break;
            case SHOW_ERROR_MESSAGE:
                showDialogErrorOneOption((String) msg.obj);
                break;
        }

        handlerPushMeal = this.mUiHandler;

    }


    public void init()
    {
        ctxt = getApplicationContext();
        launchPadConfigure();


        oclKeyboardEnter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContentUsingRightKeyboard = keyboard.getContent();
                if (StringUtils.isEmpty(inputContentUsingRightKeyboard)) {
                    return;
                }
                TscEthernetDll.writePackagedInfo(inputContentUsingRightKeyboard);
                CommonUtils.sendMsg(inputContentUsingRightKeyboard + "正在叫号", SHOW_TOAST, handlerPushMeal);
                keyboard.clearContent();
            }
        };//初始化
        keyboard = new Keyboard(ctxt, null, mslidingMenu.mRightBehindBase, mslidingMenu, oclKeyboardEnter);

        mslidingMenu.setRightBehindContentView(keyboard);
        keyboard.HideBtnDot();
        etLableAddress = (EditText)findViewById(R.id.editTextLablePrinter);
        lableAddress = LocalDataDeal.readFromLocalLabelPrinterIp(ctxt);
        if(!StringUtils.isEmpty(lableAddress))
        {
            etLableAddress.setHint(lableAddress);
        }
        else
        {
            etLableAddress.setHint("请输入标签打印机IP地址");
        }



    }

    public void widgetCofigure()
    {

        etLableAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btnSaveLableAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lableAddressTmp = etLableAddress.getText().toString();
                if (CommonUtils.isIp(lableAddressTmp)) {
                    lableAddress = lableAddressTmp;
                    LocalDataDeal.writeToLocalLablePrinterIp(lableAddress, ctxt);
                    InputMethodUtils.HideKeyboard(etLableAddress);
                } else {
                    sendUIMessage(SHOW_ERROR_MESSAGE, "标签打印机地址 输入格式不正确");
                }

            }
        });
        findViewById(R.id.buttonShowButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = LocalDataDeal.readFromLocalLabelPrinterIp(ctxt);
                if(StringUtils.isEmpty(ip) && !CommonUtils.isIp(ip))
                {
                    sendUIMessage(SHOW_ERROR_MESSAGE,"请输入IP地址");
                   return;
                }
                mslidingMenu.toggleRightDrawer();

                new Thread()
                {
                    public void run()
                    {
                        String ip = LocalDataDeal.readFromLocalLabelPrinterIp(ctxt);
                        TscEthernetDll.openport(ip, 9100);//打开指定IP和端口号

                        //setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset)
                        //宽度、高度、速度、浓度
                        //sensor为0：sensor_distance 垂直间距距离 sensor_offset垂直间距的偏移
                        //sensor为1：sensor_distance 定义黑标高度和额外送出长度 sensor_offset黑标偏移量
                        TscEthernetDll.setup(40, 30, 10, 10, 0, 5, 0);

                        TscEthernetDll.clearbuffer();

                /*--------------------------标签打印机初始化--------------------------*/
                        TscEthernetDll.sendcommand("SET TEAR ON\n");//是否将撕纸位置移动到撕纸处
                        //TscEthernetDll.sendcommand("SET COUNTER @1 1\n");//设定计时器及增量
                        TscEthernetDll.sendcommand("SHIFT 10\n");
                    }

                }.start();

            }
        });
    }


}
