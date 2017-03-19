package com.huofu.RestaurantOS.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;


/**
 * author: Created by zzl on 15/8/13.
 */

/****
 * 自定义键盘
 */
public class Keyboard extends LinearLayout {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btn0;
    private Button btnDot;
    private Button btnBack;
    private Button btnNegative;
    private Button btnPositive;
    private TextView tvShow;
    private String inputContent = "";
    private SimpleSideDrawer mslidingMenu;
    private View grid;

    /***
     * @param context
     * @param attrs
     * @param view         这个view是用来将键盘和mslidingMenu关联起来的，是SimpleSideDrawer类中的一个view
     * @param mslidingMenu 通过它来关掉键盘
     * @param oclPositive  输入完成后点击确认后的点击回调，有用户在初始化keyboard时制定
     */
    public Keyboard(Context context, AttributeSet attrs, LinearLayout view, SimpleSideDrawer mslidingMenu, View.OnClickListener oclPositive) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.keyboard_setting, view);
        btnInit(grid);
        btnConfig(oclPositive);
        this.mslidingMenu = mslidingMenu;
    }

    /**
     * 设置确定按钮的回调
     *
     * @param ocl
     */
    public void setPositivOCL(OnClickListener ocl) {
        btnConfig(ocl);
    }

    /**
     * 按键初始化
     *
     * @param grid
     */
    private void btnInit(View grid) {
        btn0 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num0);
        btn1 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num1);
        btn2 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num2);
        btn3 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num3);
        btn4 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num4);
        btn5 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num5);
        btn6 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num6);
        btn7 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num7);
        btn8 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num8);
        btn9 = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_num9);

        btnBack = (Button) grid.findViewById(R.id.button_setting_keyboard_dial_delete);
        tvShow = (TextView) grid.findViewById(R.id.textview_setting_keybpard_dial_content_input);
        btnDot = (Button) grid.findViewById(R.id.button_setting_keyboard_dial_dot);
        btnPositive = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_enter);
        btnNegative = (Button) grid.findViewById(R.id.button_setting_keybpard_dial_cancel);
    }

    /**
     * 获取输入的内容
     *
     * @return
     */
    public String getContent() {
        return inputContent;
    }

    public void showBtnDot()
    {
        if(btnDot != null )
        {
            btnDot.setVisibility(View.VISIBLE);
        }
    }

    public void HideBtnDot()
    {
        if(btnDot != null )
        {
            btnDot.setVisibility(View.INVISIBLE);
        }
    }

    public void getFoucs() {
        grid.requestFocus();
    }


    /**
     * 清空输入的内容
     */
    public void clearContent() {
        inputContent = "";
        tvShow.setText("");
        if(btnDot != null && btnDot.getVisibility() == View.VISIBLE)
        {
            btnDot.setEnabled(true);
        }
        btnPositive.setEnabled(false);//设置为不可用
    }


    /**
     * 按键配置
     *
     * @param oclPositive
     */
    private void btnConfig(View.OnClickListener oclPositive) {

        View.OnClickListener oclCancel = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mslidingMenu.closeRightSide();
                mslidingMenu.flagRightOpen = false;
                inputContent = "";
                tvShow.setText("");
                if(btnDot != null)
                {
                    btnDot.setEnabled(true);
                }


            }
        };
        btnNegative.setOnClickListener(oclCancel);
        btnPositive.setOnClickListener(oclPositive);

        View.OnClickListener oclBack = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputContent.equals("")) {
                    StringBuffer sb = new StringBuffer(inputContent);
                    sb = sb.deleteCharAt(sb.length() - 1);
                    inputContent = sb.toString();
                    btnPositive.setEnabled(true);//设置为可用
                }
                else
                {
                    btnPositive.setEnabled(false);//设置为不可用
                }
                tvShow.setText(inputContent);
                if(!inputContent.contains("."))
                {
                    if(btnDot != null && btnDot.getVisibility() == View.VISIBLE)
                    {
                        btnDot.setEnabled(true);
                    }
                }
            }
        };
        btnBack.setOnClickListener(oclBack);

        View.OnClickListener oclNum = new OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = ((Button) v).getText().toString();

                if (inputContent.length() <= 7) {
                    inputContent += text;
                    tvShow.setText(inputContent);
                    btnPositive.setEnabled(true);//设置可用
                }

            }
        };

        final View.OnClickListener oclDot = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((Button) v).getText().toString();
                if (inputContent.length() <= 7) {
                    inputContent += text;
                    tvShow.setText(inputContent);
                }
                if(btnDot != null && btnDot.getVisibility() == View.VISIBLE)
                {
                    btnDot.setEnabled(false);
                }


            }
        };
        btnDot.setOnClickListener(oclDot);

        btn0.setOnClickListener(oclNum);
        btn1.setOnClickListener(oclNum);
        btn2.setOnClickListener(oclNum);
        btn3.setOnClickListener(oclNum);
        btn4.setOnClickListener(oclNum);
        btn5.setOnClickListener(oclNum);
        btn6.setOnClickListener(oclNum);
        btn7.setOnClickListener(oclNum);
        btn8.setOnClickListener(oclNum);
        btn9.setOnClickListener(oclNum);

    }

}
