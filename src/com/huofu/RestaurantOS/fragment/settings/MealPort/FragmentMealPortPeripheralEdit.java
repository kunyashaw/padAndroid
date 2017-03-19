package com.huofu.RestaurantOS.fragment.settings.MealPort;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * author: Created by zzl on 15/10/22.
 */
@SuppressLint("ValidFragment")
public class FragmentMealPortPeripheralEdit extends Fragment{

    peripheral pp = new peripheral();
    String name = "";
    Handler handler;

    public FragmentMealPortPeripheralEdit(int index)
    {
        handler = FragmentMealPortSettingManager.handler;
        pp = FragmentMealPortSettingManager.listPeripheral.get(index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View grid = inflater.inflate(R.layout.setting_meal_port_periheral_edit,null);

        TextView tvPeripheralIdNameTips = (TextView)grid.findViewById(R.id.tv_setting_add_meal_port_peripheral_name_id_tips);
        TextView tvPeripheralIdName = (TextView)grid.findViewById(R.id.tv_setting_add_meal_port_peripheral_id);

        TextView tvPeripheralDeviceNameTips = (TextView)grid.findViewById(R.id.tv_setting_add_meal_port_peripheral_device_tips);
        final EditText etDeviceName = (EditText)grid.findViewById(R.id.et_setting_add_meal_port_peripheral_device);

        RelativeLayout rlDelete = (RelativeLayout)grid.findViewById(R.id.rl_setting_add_mp_peripheral_delete);


        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        final Button button_now_setting_back = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_content.setVisibility(View.VISIBLE);
        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);


        button_now_setting_edit_cancel.setText("返回");
        button_now_setting_edit_save.setText("");
        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodUtils.HideKeyboard(etDeviceName);
                int type = -1;
                if(FragmentMealPortSettingManager.peripheralType == 0)
                {
                    type = 1;//打印机
                }
                else if(FragmentMealPortSettingManager.peripheralType == 1)
                {
                    type = 3;//叫号设备
                }

                ApisManager.SaveOrAddPeripheralToClound(name, pp.con_id, pp.peripheral_id,type,0,
                        new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.LogWuwei(SettingsActivity.tag,"");
                        ApisManager.GetPeripheralList(new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                FragmentMealPortSettingManager.listPeripheral = (List<peripheral>)object;
                                FragmentMealPortSettingManager.showFragmentMpPeripheralList(FragmentMealPortSettingManager.peripheralType);
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.LogWuwei(SettingsActivity.tag, "" + response.error_message);
                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        if(response.error_code == 240002)
                        {
                            FragmentMealPortSettingManager.showFragmentMpPeripheralList(FragmentMealPortSettingManager.peripheralType);
                        }
                        CommonUtils.LogWuwei(SettingsActivity.tag,""+response.error_message);
                    }
                });
            }
        });


          rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.sendMsg("正在更新外设列表", SettingsActivity.SHOW_LOADING_TEXT,handler);
                InputMethodUtils.HideKeyboard(etDeviceName);
                ApisManager.DeletePeripheralFromClound(pp.peripheral_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        ApisManager.GetPeripheralList(new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING,handler);
                                FragmentMealPortSettingManager.listPeripheral = (List<peripheral>)object;
                                SettingsActivity.listPeripheral = (List<peripheral>)object;
                                FragmentMealPortSettingManager.showFragmentMpPeripheralList(FragmentMealPortSettingManager.peripheralType);

                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING,handler);
                                CommonUtils.sendMsg(""+response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE,handler);
                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING,handler);
                        CommonUtils.sendMsg(""+response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE,handler);
                    }
                });
            }
        });

        if(FragmentMealPortSettingManager.peripheralType == 0)//打印机外设
        {
            button_now_setting_content.setText("编辑小票打印机");
            tvPeripheralIdNameTips.setText("打印机IP地址");
            tvPeripheralDeviceNameTips.setText("打印机外号");

        }
        else if(FragmentMealPortSettingManager.peripheralType == 1)//电视外设
        {
            button_now_setting_content.setText("编辑叫号设备");
            tvPeripheralIdNameTips.setText("叫号设备IP地址");
            tvPeripheralDeviceNameTips.setText("叫号设备名称");
        }

        name = pp.name;
        tvPeripheralIdName.setText(pp.con_id);

        etDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });

        return grid;
    }
}
