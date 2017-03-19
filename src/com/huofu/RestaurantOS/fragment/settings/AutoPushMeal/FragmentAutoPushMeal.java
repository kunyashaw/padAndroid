package com.huofu.RestaurantOS.fragment.settings.AutoPushMeal;

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

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.storeOrder.MealPortRelation;
import com.huofu.RestaurantOS.service.getTakeupListIntentService;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentAutoPushMeal extends Fragment{

    Handler handler;
    Context ctxt;
    long appcopy_id;
    List<StoreMealPort> listServer = new ArrayList<StoreMealPort>();

    public FragmentAutoPushMeal()
    {
        this.ctxt = MainApplication.getContext();
    }

    public FragmentAutoPushMeal(Handler handler,Context ctxt)
    {
        this.handler = handler;
        this.ctxt = ctxt;
        this.appcopy_id = LocalDataDeal.readFromLocalAppCopyId(ctxt);

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

        button_now_setting_content.setText("智能出餐设置");
        button_now_setting_content.setOnClickListener(null);
        button_now_setting_content.setCompoundDrawables(null,null,null,null);

        View grid = inflater.inflate(R.layout.setting_layout_meal_done, null);
        ToggleButton sb = (ToggleButton)grid.findViewById(R.id.btn_setting_layout_toggle_auto_push);

        if(LocalDataDeal.readFromPushMealSetting(ctxt))
        {
            CommonUtils.LogWuwei("", "历史设置为：自动出餐");
            sb.setToggleOn();
        }
        else
        {
            CommonUtils.LogWuwei("", "历史设置为：手工出餐");
            sb.setToggleOff();
        }

        //如果是开启自动出餐，同时将心跳后获取空闲出餐口的标志位设置为true
        //如果是关闭出餐，先将心跳后获取出餐口的标志位设置为false，然后解绑和本出餐口绑定的自动出餐口，

        sb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                // TODO Auto-generated method stub
                LocalDataDeal.writeToPushMealSetting(on, ctxt);
                if(on)
                {
                    getTakeupListIntentService.flagGetIdleMealPortsInHeart = true;
                    /*if(MealDoneActivity.list_all_order != null)
                    {
                        MealDoneActivity.list_all_order.clear();
                    }
                    if(MealDoneActivity.list_all_waiting_push != null)
                    {
                        MealDoneActivity.list_all_waiting_push.clear();
                    }
                    MealDoneActivity.sendToServer(RequestResultDeal.GET_MEAL_PRODUCT_LIST, 2, "设置中切换到自动出餐");
                    MealDoneActivity.active = true;*/
                    CommonUtils.LogWuwei("", "切换到自动出餐");
                }
                else
                {
                    getTakeupListIntentService.flagGetIdleMealPortsInHeart = false;
                    CommonUtils.LogWuwei("", "切换到手工出餐");
                    listServer = new ArrayList<StoreMealPort>();
                    //切换到自动的时候，首先解除与本APP绑定的手工出餐口
                    ApisManager.checkAppTaskPorts(appcopy_id, new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            listServer = (List<StoreMealPort>) object;
                            List<MealPortRelation> listUnBind = new ArrayList<MealPortRelation>();
                            for(int k=0;k<listServer.size();k++)
                            {
                                MealPortRelation mprResult = new MealPortRelation();
                                StoreMealPort smp = listServer.get(k);
                                if(smp.checkout_type == 1)
                                {
                                    mprResult.port_id = smp.port_id;
                                    mprResult.task_status = 0;
                                    mprResult.printer_status = 1;
                                    mprResult.checkout_type = smp.checkout_type;
                                    listUnBind.add(mprResult);
                                }
                            }

                            ApisManager.registTaskRelation(appcopy_id, listUnBind, new ApiCallback() {
                                @Override
                                public void success(Object object) {
                                    CommonUtils.LogWuwei(SettingsActivity.tag,"");
                                }

                                @Override
                                public void error(BaseApi.ApiResponse response) {
                                    CommonUtils.LogWuwei(SettingsActivity.tag,"");
                                }
                            });
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {
                            CommonUtils.LogWuwei(SettingsActivity.tag,"");
                        }
                    });
                }


            }
        });
        return grid;
    }
}
