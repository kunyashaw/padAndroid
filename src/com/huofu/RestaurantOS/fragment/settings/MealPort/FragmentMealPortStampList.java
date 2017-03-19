package com.huofu.RestaurantOS.fragment.settings.MealPort;

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

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListViewPushMealPortStampAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/22.
 */
@SuppressLint("ValidFragment")
public class FragmentMealPortStampList extends Fragment{

    List<String> list = new ArrayList<String>();
    Handler handler;
    Context ctxt;

    public FragmentMealPortStampList(Handler handler)
    {
        ctxt = MainApplication.getContext();
        this.handler = handler;
        for(char item='A';item<='Z';item++)
            list.add(item+"");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        final Button button_now_setting_back = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);
        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("");
        button_now_setting_edit_save.setText("");
        button_now_setting_edit_cancel.setText("返回");

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FragmentMealPortSettingManager.showFramgemtMPAdd();
                if(FragmentMealPortSettingManager.editType == 0)
                {
                    FragmentMealPortSettingManager.showFramgemtMPAdd();
                }
                else
                {
                    FragmentMealPortSettingManager.showFragmentMpMealPortCheck(FragmentMealPortSettingManager.nowChooseMealPortPositon,false);
                }
            }
        });



        View grid = inflater.inflate(R.layout.setting_meal_port_stamp_list,null);
        ListView lv = (ListView)grid.findViewById(R.id.listview_setting_mp_stamp_list);
        ListViewPushMealPortStampAdapter adapter = new ListViewPushMealPortStampAdapter(ctxt,handler,list);
        lv.setAdapter(adapter);
        adapter.updateAdapter(adapter);
        return  grid;
    }



}
