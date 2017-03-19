package com.huofu.RestaurantOS.fragment.settings.Printer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;

import java.util.List;

/**
 * author: Created by zzl on 15/8/27.
 */
@SuppressLint("ValidFragment")
public class FragmentPrinterIpAdd extends Fragment{

    String printerAddName = "";
    String printerAddIp = "";
    int position = -1;
    List<peripheral> list;

    public FragmentPrinterIpAdd(int index,List<peripheral> ls)
    {
        this.position = index;
        this.list = ls;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View grid = inflater.inflate(R.layout.setting_printer_add_info, null);
        final EditText etIp = (EditText)grid.findViewById(R.id.et_setting_printer_add_info_ip);
        final EditText etName = (EditText)grid.findViewById(R.id.et_setting_printer_add_info_name);
        RelativeLayout rlIp = (RelativeLayout)grid.findViewById(R.id.rl_setting_add_printer_ip);
        RelativeLayout rlName = (RelativeLayout)grid.findViewById(R.id.rl_setting_add_printer_name);

        rlIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etIp.requestFocus();
            }
        });

        rlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.requestFocus();
            }
        });

        etIp.addTextChangedListener(new TextWatcher() {

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
                printerAddIp = s.toString();
            }
        });
        etName.addTextChangedListener(new TextWatcher() {

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
                printerAddName = s.toString();
            }
        });

        etName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub

                if (keyCode == event.KEYCODE_ENTER) {
                    etIp.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });

        etIp.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == event.KEYCODE_ENTER) {
                   saveAddResult(grid);
                    etIp.setOnKeyListener(null);
                    return false;
                } else {
                    return false;
                }

            }
        });

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.INVISIBLE);

        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setText("新建");

        button_now_setting_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            saveAddResult(grid);
            }
        });

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentsPrinterSettingManager.showFragmentPrinterIpList(position);
            }
        });
        return grid;
    }

    public void saveAddResult(View grid)
    {
        InputMethodUtils.HideKeyboard(grid);
        InputMethodUtils.TimerHideKeyboard(grid);

        if(!CommonUtils.isIp(printerAddIp))
        {
            CommonUtils.sendMsg("ip格式输入错误", SettingsActivity.SHOW_ERROR_MESSAGE, FragmentsPrinterSettingManager.handler);
            return;
        }


        CommonUtils.sendMsg("新增中..", SettingsActivity.SHOW_LOADING_TEXT, FragmentsPrinterSettingManager.handler);

        ApisManager.SaveOrAddPeripheralToClound(printerAddName, printerAddIp, 0,1,0, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                CommonUtils.sendMsg("刷新列表中..", SettingsActivity.SHOW_LOADING_TEXT, FragmentsPrinterSettingManager.handler);

                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                        FragmentsPrinterSettingManager.listPeripheralPrinter = (List<peripheral>) object;
                        FragmentsPrinterSettingManager.showFragmentPrinterIpList(position);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, FragmentsPrinterSettingManager.handler);
                    }
                });

            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, FragmentsPrinterSettingManager.handler);
            }
        });
    }
}
