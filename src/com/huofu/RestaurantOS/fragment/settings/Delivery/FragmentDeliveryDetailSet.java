package com.huofu.RestaurantOS.fragment.settings.Delivery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentDeliveryDetailSet extends Fragment{

    Context ctxt;
    Handler handler;
    String inputContentUsingRightKeyboard = "";

    public FragmentDeliveryDetailSet(Context ctxt,Handler handler)
    {
        this.ctxt = ctxt;
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_edit_cancel.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.VISIBLE);

        button_now_setting_back.setText("订单外送设置");
        button_now_setting_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showTakeout();
                CommonUtils.sendMsg("获取服务器外送信息", SettingsActivity.SHOW_LOADING_TEXT, SettingsActivity.handler);
                ApisManager.GetStoreDeliverySettingInfo(FragmentsDeliverySettingManager.store_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, SettingsActivity.handler);
                        FragmentsDeliverySettingManager.showDeliveryChooseSet();
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, SettingsActivity.handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
                    }
                });
            }
        });

        View grid = inflater.inflate(R.layout.delivery_set_detail_info, null);

        ToggleButton sb = (ToggleButton)grid.findViewById(R.id.btn_setting_layout_takeout_delivery_assign_time_supported);

        TextView tvMinOrderDeliveryAmount = (TextView)grid.findViewById(R.id.tv_setting_take_out_min_order_delivery_amount);
        TextView tvDeliveryFee = (TextView)grid.findViewById(R.id.tv_setting_take_out_delivery_fee);
        TextView tvMinOrderFreeDeliveryAmount = (TextView)grid.findViewById(R.id.tv_setting_take_out_min_order_free_delivery_amount);
        TextView tvAheadTime = (TextView)grid.findViewById(R.id.tv_setting_take_out_ahead_time);

        final RelativeLayout rlMinOrderDeliveryAmount = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_min_order_delivery_amount);
        final RelativeLayout rlDeliveryFee = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_delivery_fee);
        final RelativeLayout rlMinOrderFreeDeliveryAmount = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_min_order_free_delivery_amount);
        final RelativeLayout rlAheadTime = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_ahead_time);


        OnClickListener ocl = new OnClickListener() {

            @Override
            public void onClick(final View nowEditView) {
                // TODO Auto-generated method stub

                OnClickListener oclEnter = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputContentUsingRightKeyboard = SettingsActivity.keyboard.getContent();

                        if(inputContentUsingRightKeyboard.contains("."))
                        {

                            String beforeStr = "";
                            String afterStr = "";

                            double d = Double.parseDouble(inputContentUsingRightKeyboard)*100;
                            String dStr = Double.toString(d);
                            StringBuffer sb = new StringBuffer(dStr);

                            for(int i=0;i<dStr.length();i++)
                            {
                                if(sb.charAt(i) == '.')
                                {
                                    break;
                                }
                                beforeStr+=sb.charAt(i);
                            }

                            inputContentUsingRightKeyboard = beforeStr;


                        }
                        else
                        {
                            inputContentUsingRightKeyboard+="00";
                        }

                        if(inputContentUsingRightKeyboard != null && !inputContentUsingRightKeyboard.equals(""))
                        {
                            if(nowEditView.getId() == rlMinOrderDeliveryAmount.getId())
                            {
                                LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "min_order_delivery_amount", Integer.parseInt(inputContentUsingRightKeyboard));

                            }
                            else if(nowEditView.getId() == rlMinOrderFreeDeliveryAmount.getId())
                            {
                                LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "min_order_free_delivery_amount", Integer.parseInt(inputContentUsingRightKeyboard));
                            }
                            else if (nowEditView.getId() == rlDeliveryFee.getId())
                            {
                                LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "delivery_fee", Integer.parseInt(inputContentUsingRightKeyboard));
                            }
                            else if (nowEditView.getId() == rlAheadTime.getId())
                            {
                                LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "ahead_time", Integer.parseInt(inputContentUsingRightKeyboard)*10*60);
                            }
                            saveStoreDeliverySetInfo();
                        }
                        CommonUtils.sendMsg("",SettingsActivity.HIDE_KEYBOARD,handler);
                    }
                };
                SettingsActivity.keyboard.setPositivOCL(oclEnter);
                CommonUtils.sendMsg("",SettingsActivity.SHOW_KEYBOARD,handler);
                }
            };


        rlMinOrderDeliveryAmount.setOnClickListener(ocl);
        rlMinOrderFreeDeliveryAmount.setOnClickListener(ocl);
        rlDeliveryFee.setOnClickListener(ocl);
        rlAheadTime.setOnClickListener(ocl);



        int delivery_assign_time_supported = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "delivery_assign_time_supported"));
        int min_order_delivery_amount = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "min_order_delivery_amount"));
        int delivery_fee = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "delivery_fee"));
        int min_order_free_delivery_amount = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "min_order_free_delivery_amount"));
        int ahead_time = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "ahead_time"));

        if(delivery_assign_time_supported == 1)
        {
            sb.toggleOn();
        }
        else
        {
            sb.toggleOff();
        }


        sb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                // TODO Auto-generated method stub
                if(on)
                {
                    LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "delivery_assign_time_supported", 1);
                }
                else
                {
                    LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "delivery_assign_time_supported", 0);
                }
                saveStoreDeliverySetInfo();
            }
        });


        tvMinOrderDeliveryAmount.setText(""+min_order_delivery_amount/100.0);
        tvDeliveryFee.setText(""+delivery_fee/100.0);
        tvMinOrderFreeDeliveryAmount.setText(""+min_order_free_delivery_amount/100.0);
        tvAheadTime.setText(""+ahead_time/(1000*60)+"分钟");
        return grid;
    }

    public void saveStoreDeliverySetInfo()
    {
        CommonUtils.sendMsg("保存服务器外送信息", SettingsActivity.SHOW_LOADING_TEXT, handler);
        int min_order_delivery_amount = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "min_order_delivery_amount"));
        int min_order_free_delivery_amount = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "min_order_free_delivery_amount"));
        int delivery_fee = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "delivery_fee"));
        int ahead_time = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "ahead_time"));
        int delivery_assign_time_supported = Integer.parseInt(LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt, "delivery_assign_time_supported"));
        ApisManager.SaveStoreDeliverySettingInfo(
                min_order_delivery_amount,
                min_order_free_delivery_amount,
                delivery_fee,
                ahead_time,
                delivery_assign_time_supported,
                FragmentsDeliverySettingManager.store_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING,handler);
                        FragmentsDeliverySettingManager.showDeliveyDetialSet();
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING,handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE,handler);
                    }
                }
        );
    }
}
