package com.huofu.RestaurantOS.fragment.settings.Tv;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentTvChooseSet extends Fragment {

    Context ctxt;
    public FragmentTvChooseSet()
    {
        ctxt = MainApplication.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View grid = inflater.inflate(R.layout.setting_layout_tv, null);


        View.OnClickListener ocl = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                //popWindowSetPriter.dismiss();
                //showTvIpEdit();
                FragmentsTvSettingManager.showFragmentTvIpEdit();
            }
        };



        RelativeLayout rl_tv = (RelativeLayout)grid.findViewById(R.id.rl_setting_layout_tv);
        rl_tv.setOnClickListener(ocl);
        TextView tv_tv = (TextView)grid.findViewById(R.id.tv_setting_layout_tv_content);
        String nowTvIp = LocalDataDeal.readFromLocalCallTvIp(ctxt);

        RelativeLayout rl_set_call_way = (RelativeLayout)grid.findViewById(R.id.rl_setting_layout_tv_toggle_call);
        final TextView tv = (TextView)grid.findViewById(R.id.tv_setting_layout_tv_call_way);

        switch(LocalDataDeal.readFromAutoCall(ctxt))
        {
            case 0://出餐不叫号
                tv.setText("出餐不叫号");
                break;
            case 1://出餐叫号
                tv.setText("出餐叫号");
                break;
            case 2://尾单叫号
                tv.setText("尾单叫号");
                break;
            case -1:
                tv.setText("未设置");
                break;
        }

        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showChooseCallWay();
                FragmentsTvSettingManager.showFragmentTvCallMethodChoose();
            }
        });

        rl_set_call_way.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //showChooseCallWay();
                FragmentsTvSettingManager.showFragmentTvCallMethodChoose();
            }
        });


        if(nowTvIp == null || nowTvIp.equals(""))
        {
            tv_tv.setText("未设置");
        }
        else
        {
            tv_tv.setText(nowTvIp);
        }

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_edit_cancel.setVisibility(View.GONE);
        button_now_setting_edit_save.setVisibility(View.GONE);
        button_now_setting_back.setVisibility(View.GONE);
        button_now_setting_content.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("电视设置");
        button_now_setting_content.setOnClickListener(null);
        button_now_setting_content.setCompoundDrawables(null, null, null, null);

        return grid;
    }
}
