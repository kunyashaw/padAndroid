package com.huofu.RestaurantOS.fragment.settings.Printer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.huofu.RestaurantOS.MainApplication;
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
 * author: Created by zzl on 15/8/26.
 */
@SuppressLint("ValidFragment")
public class FragmentPrinterIpEdit extends Fragment {

    Handler handler;
    String printerNewIp;
    String printerNewName;
    int positon;
    List<peripheral> listPeripheralPrinter;
    Context ctxt;

    public FragmentPrinterIpEdit(int position,List<peripheral> listPeripheralPrinter)
    {
        this.positon = position;
        this.listPeripheralPrinter = listPeripheralPrinter;
        ctxt = MainApplication.getContext();
        this.handler = FragmentsPrinterSettingManager.handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View grid = inflater.inflate(R.layout.setting_printer_edit_info, null);

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);


        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setText("完成");



        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodUtils.TimerHideKeyboard(grid);
                InputMethodUtils.HideKeyboard(grid);
                FragmentsPrinterSettingManager.showFragmentPrinterIpList(positon);
            }
        });

        button_now_setting_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveEditResult(positon,grid);
            }
        });

        final EditText etPrinterName = (EditText)grid.findViewById(R.id.et_setting_printer_edit_info_name);
        final EditText etPrinterIp = (EditText)grid.findViewById(R.id.et_setting_printer_edit_info_ip);
        Button btnDelete = (Button)grid.findViewById(R.id.btn_setting_printer_edit_delete);

        RelativeLayout rlName = (RelativeLayout)grid.findViewById(R.id.rl_setting_printer_name);
        rlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPrinterName.requestFocus();
            }
        });


        RelativeLayout rlIp = (RelativeLayout)grid.findViewById(R.id.rl_setting_printer_ip);
        rlIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPrinterIp.requestFocus();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = SettingsActivity.SHOW_DELETE_PERI_MESSAGE;
                msg.obj = listPeripheralPrinter.get(positon).peripheral_id;
                handler.sendMessage(msg);
            }
        });

        etPrinterName.setHint(listPeripheralPrinter.get(positon).name);

        etPrinterIp.setHint(listPeripheralPrinter.get(positon).con_id);

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

                printerNewName = s.toString();
            }
        });

        etPrinterIp.addTextChangedListener(new TextWatcher() {

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
                printerNewIp = s.toString();
            }
        });

        etPrinterName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub

                if (keyCode == event.KEYCODE_ENTER) {
                    etPrinterIp.requestFocus();
                    return true;
                } else {
                    return false;
                }


            }
        });

        etPrinterIp.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == event.KEYCODE_ENTER) {
                    saveEditResult(positon,grid);
                    InputMethodUtils.HideKeyboard(etPrinterIp);
                    InputMethodUtils.TimerHideKeyboard(etPrinterIp);
                    return true;
                } else {
                    return false;
                }
            }
        });

        return grid;
    }

    public void saveEditResult(final int positon,View grid)
    {
        InputMethodUtils.HideKeyboard(grid);
        InputMethodUtils.TimerHideKeyboard(grid);

        if( printerNewIp == null || printerNewIp.equals(""))
        {
            printerNewIp = listPeripheralPrinter.get(positon).con_id;
        }

        if(printerNewName == null || printerNewName.equals(""))
        {
            printerNewName = listPeripheralPrinter.get(positon).name;
        }

        if(!CommonUtils.isIp(printerNewIp))
        {
            CommonUtils.sendMsg("ip格式输入错误", SettingsActivity.SHOW_ERROR_MESSAGE,handler);
            return;

        }

        CommonUtils.sendMsg("修改中..", SettingsActivity.SHOW_LOADING_TEXT, FragmentsPrinterSettingManager.handler);

        ApisManager.SaveOrAddPeripheralToClound(printerNewName, printerNewIp, listPeripheralPrinter.get(positon).peripheral_id,
                1,0,
                new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                CommonUtils.sendMsg("刷新列表中..", SettingsActivity.SHOW_LOADING_TEXT, FragmentsPrinterSettingManager.handler);

                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                        FragmentsPrinterSettingManager.listPeripheralPrinter = (List<peripheral>) object;
                        FragmentsPrinterSettingManager.showFragmentPrinterIpList(positon);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_TOAST, FragmentsPrinterSettingManager.handler);
                    }
                });

            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, FragmentsPrinterSettingManager.handler);
                CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_TOAST, FragmentsPrinterSettingManager.handler);
            }
        });
    }
}
