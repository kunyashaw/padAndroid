package com.huofu.RestaurantOS.fragment.settings.Tv;

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

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListviewSettingTvCallAdapter;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/28.
 */
@SuppressLint("ValidFragment")
public class FragmentTvCallMethodChoose extends Fragment {

    Context ctxt;
    Handler handler;
    //ListviewSettingTvCallAdapter listviewSettingTvCallWayadapter;
    public FragmentTvCallMethodChoose(Handler handler,Context ctxt)
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

        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);

        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_edit_save.setText("完成");

        final View grid = inflater.inflate(R.layout.listview_tv_call_way, null);

        ListView lv = (ListView)grid.findViewById(R.id.listview_tv_call_way_list);
        final int beforeChoose = LocalDataDeal.readFromAutoCall(ctxt);

        final List<String> ls = new ArrayList<String>();
        ls.add("出餐不叫号");
        ls.add("出餐叫号");
        ls.add("出餐尾单叫号");

        FragmentsTvSettingManager.listviewSettingTvCallWayadapter = new ListviewSettingTvCallAdapter(ctxt, ls);
        lv.setAdapter(FragmentsTvSettingManager.listviewSettingTvCallWayadapter);

        button_now_setting_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.LogWuwei("", "已经选中" + ls.get(LocalDataDeal.readFromAutoCall(ctxt)));
                FragmentsTvSettingManager.showfragmentTvChooseSet();

            }
        });

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                LocalDataDeal.writeToAutoCall(beforeChoose, ctxt);
                FragmentsTvSettingManager.showfragmentTvChooseSet();
            }
        });
        return  grid;
    }
}
