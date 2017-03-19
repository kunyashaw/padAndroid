package com.huofu.RestaurantOS.fragment.settings.Tv;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentTvIpEdit extends Fragment{

    Handler handler;
    Context ctxt;
    String tvIp = "";
    public FragmentTvIpEdit(Handler handler,Context ctxt)
    {
        this.handler = handler;
        this.ctxt = ctxt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);


        final View grid = inflater.inflate(R.layout.setting_tv_edit_info, null);

        EditText etPrinterName = (EditText)grid.findViewById(R.id.et_setting_tv_edit_info_ip);

        etPrinterName.addTextChangedListener(new TextWatcher() {

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
                tvIp = s.toString();
            }
        });


        etPrinterName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub

                if(keyCode == event.KEYCODE_ENTER)
                {
                    button_now_setting_edit_save.performClick();
                    return true;
                }
                else
                {
                    return false;
                }

            }
        });



        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setText("完成");

        button_now_setting_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodUtils.HideKeyboard(button_now_setting_edit_save);
                if(CommonUtils.isIp(tvIp))
                {
                    LocalDataDeal.writeToLocalCallTvIp(tvIp, ctxt);
                    FragmentsTvSettingManager.showfragmentTvChooseSet();
                }
                else
                {
                    CommonUtils.sendMsg("保存失败:电视ip格式不正确!", SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                }
            }
        });

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodUtils.HideKeyboard(button_now_setting_edit_save);
                FragmentsTvSettingManager.showfragmentTvChooseSet();
            }
        });
        return grid;
    }
}
