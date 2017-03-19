package com.huofu.RestaurantOS.fragment.settings.Delivery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.DeliveryBuilding;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;

import java.util.List;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentDeliveryBuildingEdit extends Fragment{

    Context ctxt;
    Handler handler;
    int positon=0;
    String buildingEditName;
    String buildingEditAddress;

    public FragmentDeliveryBuildingEdit(Context ctxt,Handler handler,int pos)
    {
        this.ctxt = ctxt;
        this.handler = handler;
        this.positon = pos;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setText("完成");

        final View grid = inflater.inflate(R.layout.setting_printer_edit_info, null);

        EditText etPrinterName = (EditText)grid.findViewById(R.id.et_setting_printer_edit_info_name);
        final EditText etPrinterIp = (EditText)grid.findViewById(R.id.et_setting_printer_edit_info_ip);
        Button btnDelete = (Button)grid.findViewById(R.id.btn_setting_printer_edit_delete);
        btnDelete.setText("删除楼宇信息");
        TextView tvName = (TextView)grid.findViewById(R.id.tv_setting_printer_edit_info_name_tips);
        TextView tvIp = (TextView)grid.findViewById(R.id.tv_setting_printer_edit_info_ip_tips);
        tvName.setText("名称");
        tvIp.setText("地址");

        etPrinterName.setHint(FragmentsDeliverySettingManager.listDeliveryBuilding.get(positon).name);

        etPrinterIp.setHint(FragmentsDeliverySettingManager.listDeliveryBuilding.get(positon).address);

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

                buildingEditName = s.toString();
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
                buildingEditAddress = s.toString();
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
                    button_now_setting_edit_save.performClick();
                    return true;
                } else {
                    return false;
                }
            }
        });
        etPrinterIp.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = SettingsActivity.SHOW_DELETE_DELIVERY_BUILDING_MESSAGE;
                msg.obj = positon;
                handler.sendMessage(msg);
                //CommonUtils.sendMsg("确要删除该外接设备吗？", SHOW_DELETE_PERI_MESSAGE, handler);
            }
        });

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodUtils.TimerHideKeyboard(grid);
                InputMethodUtils.HideKeyboard(grid);
                FragmentsDeliverySettingManager.showDeliveryBuildingList();
            }
        });

        button_now_setting_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("保存数据到服务器", SettingsActivity.SHOW_LOADING_TEXT, handler);

                if(buildingEditAddress.equals("") || buildingEditAddress == null)
                {
                    buildingEditAddress = FragmentsDeliverySettingManager.listDeliveryBuilding.get(positon).address;
                }

                if(buildingEditName.equals("") || buildingEditName == null)
                {
                    buildingEditName = FragmentsDeliverySettingManager.listDeliveryBuilding.get(positon).name;
                }


                ApisManager.SaveOrAddDeliveryBuilidng(FragmentsDeliverySettingManager.listDeliveryBuilding.get(positon).building_id,
                        buildingEditName, buildingEditAddress, FragmentsDeliverySettingManager.store_id, new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);

                                ApisManager.GetStoreDeliveryBuildingsList(FragmentsDeliverySettingManager.store_id, new ApiCallback() {
                                    @Override
                                    public void success(Object object) {
                                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                                        FragmentsDeliverySettingManager.listDeliveryBuilding = (List<DeliveryBuilding>) object;
                                        FragmentsDeliverySettingManager.showDeliveryBuildingList();
                                    }

                                    @Override
                                    public void error(BaseApi.ApiResponse response) {
                                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                                    }
                                });
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                                CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                            }
                        });
            }
        });
        return grid;
    }
}
