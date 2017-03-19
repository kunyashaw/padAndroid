package com.huofu.RestaurantOS.fragment.settings.Delivery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.store;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentDeliveryChooseSet extends Fragment {

    Handler handler;
    Context ctxt;
    public FragmentDeliveryChooseSet(Context ctxt,Handler handler)
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
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_edit_cancel.setVisibility(View.GONE);
        button_now_setting_edit_save.setVisibility(View.GONE);
        button_now_setting_back.setVisibility(View.GONE);
        button_now_setting_content.setVisibility(View.VISIBLE);
        button_now_setting_content.setText("外送设置(" + FragmentsDeliverySettingManager.stroe_name + ")");

        Drawable drawable = ctxt.getResources().getDrawable(R.drawable.arrow_down);
        drawable.setBounds(0, 0, 24,24);//设置drawable对象的宽度和高度
        button_now_setting_content.setCompoundDrawables(null,null,drawable, null);
        button_now_setting_content.setCompoundDrawablePadding(40);
        button_now_setting_content.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(LocalDataDeal.judgeNowLoginUserWhetherSuperUser(ctxt))
                {
                    //获取具有店铺外送权限的店铺列表
                    ApisManager.GetOwenedStoreList(150001, new ApiCallback() {
                        @Override
                        public void success(Object object) {

                            if(SettingsActivity.listStore != null)
                            {
                                SettingsActivity.listStore.clear();
                            }
                            else
                            {
                                SettingsActivity.listStore = new ArrayList<store>();
                            }

                            JSONArray array  = ((com.alibaba.fastjson.JSONObject)object).getJSONArray("stores");
                            for(int k=0;k<array.size();k++)
                            {
                                com.alibaba.fastjson.JSONObject obj = array.getJSONObject(k);
                                store mStore = new store();
                                mStore.name = obj.getString("name");
                                mStore.store_id = obj.getInteger("store_id");

                                if(SettingsActivity.listStore != null)
                                {
                                    SettingsActivity.listStore.add(mStore);
                                }
                            }

                            CommonUtils.sendMsg("", SettingsActivity.SHOW_SOTRE_LIST, handler);
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {

                        }
                    });
                }
                else
                {
                    CommonUtils.sendMsg("您无权操作", SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
                }

            }
        });

        View grid = inflater.inflate(R.layout.setting_take_out, null);

        String str = LocalDataDeal.readFromLocalStoreDeliverySettingInfo(ctxt,"");
        int delivery_fee = 0;//外送费
        int min_order_delivery_amount = 0;//起送金额
        int delivery_supported = 0;

        try {
            JSONObject obj = new JSONObject(str);
            JSONObject oj =  obj.getJSONObject("store_delivery_setting");
            min_order_delivery_amount = oj.getInt("min_order_delivery_amount");
            delivery_supported = oj.getInt("delivery_supported");
            delivery_fee = oj.getInt("delivery_fee");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //1、外送范围
        final RelativeLayout rlScope = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_scope);
        TextView tv_scope = (TextView)grid.findViewById(R.id.tv_setting_take_out_scope_hint);
        tv_scope.setText(FragmentsDeliverySettingManager.listDeliveryBuilding.size()+"个");
        rlScope.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showDeliveryScope();
                FragmentsDeliverySettingManager.showDeliveryBuildingList();
            }
        });


        //2、支持的营业时间段
        final RelativeLayout rlTimeBucket = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_time_bucket);
        TextView tv_time_bucket = (TextView)grid.findViewById(R.id.tv_setting_take_out_time_bucket_hint);
        int numDeliverySupported = 0;
        for(int i=0;i<FragmentsDeliverySettingManager.mealBucketList.size();i++)
        {
            MealBucket mb = FragmentsDeliverySettingManager.mealBucketList.get(i);
            if(mb.delivery_supported == 1)
            {
                numDeliverySupported++;
            }
        }
        tv_time_bucket.setText(numDeliverySupported+"个");
        rlTimeBucket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentsDeliverySettingManager.showDeliveryTimeBucketList();
            }
        });



        //3、外送设置信息
        final RelativeLayout rlSetInfo = (RelativeLayout)grid.findViewById(R.id.rl_setting_take_out_set_info);
        TextView tv_set_info = (TextView)grid.findViewById(R.id.tv_setting_take_out_set_info_hint);
        tv_set_info.setText("起送金额:"+min_order_delivery_amount/100.0+"元"+";外送费:"+delivery_fee/100.0+"元");
        rlSetInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showDeliverySetInfo()
                FragmentsDeliverySettingManager.showDeliveyDetialSet();
            }
        });


        final ToggleButton sb = (ToggleButton)grid.findViewById(R.id.btn_setting_layout_takeout);


        sb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                // TODO Auto-generated method stub

                final boolean flagSwitch = on;
                int flag = 0;
                if(on)
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;
                }
                final int flagResult = flag;

                CommonUtils.sendMsg("保存结果中...",SettingsActivity.SHOW_LOADING_TEXT,handler);

                ApisManager.SetStoreDeliverySupported(flag, FragmentsDeliverySettingManager.store_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("",SettingsActivity.HIDE_LOADING,handler);
                        LocalDataDeal.setLocalStoreDeliverySettingInfo(ctxt, "delivery_supported", flagResult);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(flagResult == 1)
                                {
                                    rlScope.setVisibility(View.VISIBLE);
                                    rlSetInfo.setVisibility(View.VISIBLE);
                                    rlTimeBucket.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    rlScope.setVisibility(View.INVISIBLE);
                                    rlSetInfo.setVisibility(View.INVISIBLE);
                                    rlTimeBucket.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg(response.error_message,SettingsActivity.HIDE_LOADING,handler);
                        CommonUtils.sendMsg(response.error_message,SettingsActivity.SHOW_ERROR_MESSAGE,handler);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(flagResult == 1)
                                {
                                    sb.toggleOff();
                                }
                                else
                                {
                                    sb.toggleOn();
                                }
                            }
                        });

                    }
                });
            }
        });


        if(delivery_supported == 1)
        {
            //sb.setSwitchState(true);
            sb.setToggleOn();
            rlScope.setVisibility(View.VISIBLE);
            rlSetInfo.setVisibility(View.VISIBLE);
            rlTimeBucket.setVisibility(View.VISIBLE);
        }
        else
        {
            //sb.setSwitchState(false);
            sb.setToggleOff();
            rlScope.setVisibility(View.INVISIBLE);
            rlSetInfo.setVisibility(View.INVISIBLE);
            rlTimeBucket.setVisibility(View.INVISIBLE);
        }

        return grid;
    }
}
