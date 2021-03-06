package com.huofu.RestaurantOS.fragment.settings.Delivery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListviewSettingDeliveryBuildingAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentDeliveryBuildingsChooseSet extends Fragment{

    Context ctxt;
    Handler handler;
    public FragmentDeliveryBuildingsChooseSet(Context ctxt,Handler handler)
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

                ApisManager.GetStoreDeliverySettingInfo(LocalDataDeal.readFromLocalStoreId(ctxt), new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        FragmentsDeliverySettingManager.showDeliveryChooseSet();
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });


            }
        });


        View grid = inflater.inflate(R.layout.delivery_address_list, null);

        ListView lv = (ListView) grid.findViewById(R.id.listview_delivery_address_list_all);
        ListviewSettingDeliveryBuildingAdapter lsdbAdapter = new ListviewSettingDeliveryBuildingAdapter(FragmentsDeliverySettingManager.listDeliveryBuilding, ctxt);
        lv.setAdapter(lsdbAdapter);

        TextView tvAdd = (TextView) grid.findViewById(R.id.tv_delivery_address_add);
        tvAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showDeliveryBuildingAdd();
                FragmentsDeliverySettingManager.showDeliveryBuildingAdd();
            }
        });

        return grid;
    }
}
