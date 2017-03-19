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
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.List;

/**
 * author: Created by zzl on 15/10/22.
 */
@SuppressLint("ValidFragment")
public class FragmentMealPortCheck extends Fragment {

    String mealPortName = "";//出餐台名称
    String mealPortStamp = "";//出餐小票标识
    String printerName = "";//打印机名称和ip
    long printerId = -1;//打印机外设id

    String callTvName = "";//叫号设备的名称和ip
    long callTvId = -1;//叫号外接设备id

    String callRuleName = "";//叫号规则
    int callRule = -1;//1:自动叫号 2:手动叫号 3:尾单叫号

    int whetherPacked = 0;//是否做为打包台 0为堂食 1为打包

    StoreMealPort smp = new StoreMealPort();
    Handler handler;

    public FragmentMealPortCheck(int index, Handler handler, boolean flag) {
        smp = FragmentMealPortSettingManager.listMealPorts.get(index);
        this.handler = handler;
        setValueInfo(flag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /***
     * @param flag
     */
    public void setValueInfo(boolean flag) {
        if (flag) {
            FragmentMealPortSettingManager.port_id = smp.port_id;//正在查看的出餐口id
            FragmentMealPortSettingManager.portName = smp.name;//出餐口名称
            FragmentMealPortSettingManager.letter = smp.letter;//出餐口标记
            FragmentMealPortSettingManager.printer_peripheral_id = smp.printer_peripheral_id;//打印机外设id
            FragmentMealPortSettingManager.call_peripheral_id = smp.call_peripheral_id;//叫号设备外设id
            FragmentMealPortSettingManager.call_type = smp.call_type;//叫号规则 1:自动叫号 2:手动叫号 3:尾单叫号
            FragmentMealPortSettingManager.checkout_type = smp.checkout_type;//出餐模式 1:手动出餐 2:自动出餐
            FragmentMealPortSettingManager.has_pack = smp.has_pack;//0堂食 1打包
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        final Button button_now_setting_back = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_content.setVisibility(View.VISIBLE);
        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("查看出餐台");
        button_now_setting_edit_cancel.setText("返回");
        button_now_setting_edit_save.setText("");

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.sendMsg("正在保存", SettingsActivity.SHOW_LOADING_TEXT, handler);
                InputMethodUtils.HideKeyboard(button_now_setting_edit_save);
                ApisManager.SaveMealPort(
                        FragmentMealPortSettingManager.port_id,
                        FragmentMealPortSettingManager.portName,
                        FragmentMealPortSettingManager.letter,
                        FragmentMealPortSettingManager.printer_peripheral_id,
                        FragmentMealPortSettingManager.call_peripheral_id,
                        FragmentMealPortSettingManager.call_type,
                        FragmentMealPortSettingManager.checkout_type,
                        FragmentMealPortSettingManager.has_pack, new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                ApisManager.getAllMealPortList(new ApiCallback() {
                                    @Override
                                    public void success(Object object) {
                                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                                        FragmentMealPortSettingManager.listMealPorts = (List<StoreMealPort>) object;
                                        FragmentMealPortSettingManager.showFragmentPortList();
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

        View grid = inflater.inflate(R.layout.setting_meal_port_add, null);

        /*****出餐台名称**/
        final EditText etMPName = (EditText) grid.findViewById(R.id.et_setting_add_meal_port_name);
        etMPName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mealPortName = s.toString();
                FragmentMealPortSettingManager.portName = mealPortName;
            }
        });
        if (FragmentMealPortSettingManager.portName != null && !FragmentMealPortSettingManager.portName.equals("")) {
            etMPName.setText(FragmentMealPortSettingManager.portName);
        }


        /*****出餐小票标识名称**/
        RelativeLayout rlMPStamp = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_meal_port_stamp);
        TextView tvMpStamp = (TextView) grid.findViewById(R.id.tv_setting_add_meal_port_stamp);
        View.OnClickListener oclMpMark = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMealPortSettingManager.showFragmentMpStampList();
            }
        };
        rlMPStamp.setOnClickListener(oclMpMark);
        tvMpStamp.setOnClickListener(oclMpMark);
        if (FragmentMealPortSettingManager.letter != null && !FragmentMealPortSettingManager.letter.equals("")) {
            mealPortStamp = FragmentMealPortSettingManager.letter + " ";
            tvMpStamp.setText(mealPortStamp);
        }


        /*****小票打印机名称**/
        RelativeLayout rlMpPrinterName = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_mp_port_printer);
        TextView tvMpPrinterName = (TextView) grid.findViewById(R.id.tv_setting_add_mp_printer_name);
        if (FragmentMealPortSettingManager.printer_peripheral_id != 0) {
            printerId = FragmentMealPortSettingManager.printer_peripheral_id;
            for (int k = 0; k < SettingsActivity.listPeripheral.size(); k++) {
                peripheral p = SettingsActivity.listPeripheral.get(k);
                if (printerId == p.peripheral_id) {
                    printerName = "" + p.name + " (" + p.con_id + ")";
                    tvMpPrinterName.setText(printerName);
                    break;
                }
            }
        }
        else
        {
            tvMpPrinterName.setText("不打印小票");
        }

        View.OnClickListener oclPrinterName = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_now_setting_back.setOnClickListener(null);
                button_now_setting_edit_save.setOnClickListener(null);
                FragmentMealPortSettingManager.showFragmentMpPeripheralList(0);
            }
        };
        rlMpPrinterName.setOnClickListener(oclPrinterName);
        tvMpPrinterName.setOnClickListener(oclPrinterName);

        /*****叫号设备名称**/
        RelativeLayout rlMpCallTvName = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_mp_call_tv);
        TextView tvMpCallTvName = (TextView) grid.findViewById(R.id.tv_setting_add_mp_call_tv_name);
        if (FragmentMealPortSettingManager.call_peripheral_id != 0) {
            callTvId = FragmentMealPortSettingManager.call_peripheral_id;
            for (int k = 0; k < SettingsActivity.listPeripheral.size(); k++) {
                peripheral p = SettingsActivity.listPeripheral.get(k);
                if (callTvId == p.peripheral_id) {
                    callTvName = "" + p.name + " (" + p.con_id + ")";
                    tvMpCallTvName.setText(callTvName);
                    break;
                }
            }
        }
        else
        {
            tvMpCallTvName.setText("不使用叫号设备");
        }
        View.OnClickListener oclTvCallName = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_now_setting_back.setOnClickListener(null);
                button_now_setting_edit_save.setOnClickListener(null);
                FragmentMealPortSettingManager.showFragmentMpPeripheralList(1);
            }
        };
        rlMpCallTvName.setOnClickListener(oclTvCallName);
        tvMpCallTvName.setOnClickListener(oclTvCallName);


        /*****叫号规则名称**/
        RelativeLayout rlMpCallRuleName = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_mp_call_ruls);
        TextView tvMpCallRuleName = (TextView) grid.findViewById(R.id.tv_setting_add_mp_call_rules_name);
        View.OnClickListener oclCallRule = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_now_setting_back.setOnClickListener(null);
                button_now_setting_edit_save.setOnClickListener(null);
                FragmentMealPortSettingManager.showFragmentMPCallRule();
            }
        };
        rlMpCallRuleName.setOnClickListener(oclCallRule);
        tvMpCallRuleName.setOnClickListener(oclCallRule);
        if (FragmentMealPortSettingManager.call_type != -1) {
            callRule = FragmentMealPortSettingManager.call_type;
            switch (FragmentMealPortSettingManager.call_type) {
                case 1:
                    tvMpCallRuleName.setText("自动叫号");
                    break;
                case 2:
                    tvMpCallRuleName.setText("手动叫号");
                    break;
                case 3:
                    tvMpCallRuleName.setText("尾单自动叫号");
                    break;
                default:
                    tvMpCallRuleName.setText("");
                    break;
            }
        }
        if(FragmentMealPortSettingManager.call_peripheral_id == 0)
        {
            rlMpCallRuleName.setVisibility(View.INVISIBLE);
        }
        else
        {
            rlMpCallRuleName.setVisibility(View.VISIBLE);
        }

        /*****打包出餐台**/
        RelativeLayout rlPacked = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_mp_whether_packed);
        ToggleButton tbWhetherPacked = (ToggleButton) grid.findViewById(R.id.tb_setting_add_mp_whether_packed);

        tbWhetherPacked.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    whetherPacked = 1;
                } else {
                    whetherPacked = 0;
                }
            }
        });
        if (FragmentMealPortSettingManager.has_pack != -1) {
            if (FragmentMealPortSettingManager.has_pack == 1) {
                tbWhetherPacked.setToggleOn();
            } else if (FragmentMealPortSettingManager.has_pack == 0) {
                tbWhetherPacked.setToggleOff();
            }
        }

        RelativeLayout rlDelete = (RelativeLayout) grid.findViewById(R.id.rl_setting_add_mp_delete);
        rlDelete.setVisibility(View.VISIBLE);
        rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.sendMsg("正在删除", SettingsActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.deleteMealPort(FragmentMealPortSettingManager.port_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        Integer result = (Integer) object;
                        if (result == 0) {
                            CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                            CommonUtils.sendMsg("店内必须至少有一个出餐口", SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                        } else {
                            ApisManager.getAllMealPortList(new ApiCallback() {
                                @Override
                                public void success(Object object) {
                                    FragmentMealPortSettingManager.listMealPorts = (List<StoreMealPort>) object;
                                    FragmentMealPortSettingManager.showFragmentPortList();

                                }

                                @Override
                                public void error(BaseApi.ApiResponse response) {
                                    CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                                    CommonUtils.sendMsg("" + response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                                }
                            });
                        }

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg("删除出餐台失败:" + response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
            }
        });

        return grid;
    }
}
