package com.huofu.RestaurantOS.fragment.settings.MealPort;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListviewPushMealPortListAdapter;
import com.huofu.RestaurantOS.bean.StoreMealPort;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/17.
 */
@SuppressLint("ValidFragment")
public class FragmentMealPortList extends Fragment{

    public static List<StoreMealPort> listMealPorts = new ArrayList<StoreMealPort>();
    Handler handler ;

    public FragmentMealPortList(List<StoreMealPort> list,Handler handler)
    {
        this.listMealPorts = list;
        this.handler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_edit_cancel.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.VISIBLE);
        button_now_setting_content.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("出餐台设置");
        button_now_setting_edit_save.setText("添加");

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMealPortSettingManager.cleanAddedInfo();
                FragmentMealPortSettingManager.showFramgemtMPAdd();
            }
        };
        button_now_setting_edit_save.setOnClickListener(ocl);

        View grid = inflater.inflate(R.layout.port_list_standard_layout,null);

        RelativeLayout rlAdd = (RelativeLayout)grid.findViewById(R.id.port_list_standart_add);
        Button btnAdd = (Button)grid.findViewById(R.id.btn_port_list_add);
        rlAdd.setOnClickListener(ocl);
        btnAdd.setOnClickListener(ocl);


        ListView lv = (ListView)grid.findViewById(R.id.listview_standard_ports_list);
        ListviewPushMealPortListAdapter adapter = new ListviewPushMealPortListAdapter(
                MainApplication.getContext(),handler,listMealPorts);
        lv.setAdapter(adapter);
        adapter.updateAdapter(adapter);

        Button btnBackground = (Button)grid.findViewById(R.id.setting_empty_meal_ports);
        RelativeLayout rlAddBackground = (RelativeLayout)grid.findViewById(R.id.port_list_standart_add);

        if(listMealPorts.size() == 0)
        {
            btnBackground.setVisibility(View.VISIBLE);
            rlAddBackground.setVisibility(View.VISIBLE);
        }
        return  grid;
    }
}
