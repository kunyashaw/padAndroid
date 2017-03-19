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
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListviewSettingMealPortPeripheralAdapter;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 这个fragment用来显示打印机列表或者电视设备列表
 * type 0->打印机 1->电视设备
 * author: Created by zzl on 15/10/21.
 */
@SuppressLint("ValidFragment")
public class FragmentMealPortPeripheralList extends Fragment{

    Handler handler;
    List<peripheral> listPeripheral=new ArrayList<peripheral>();
    ListviewSettingMealPortPeripheralAdapter adapter;
    int type;//0是打印机外设  1是电视外设

    public FragmentMealPortPeripheralList(Handler handler, List<peripheral> listPeripheral, int type)
    {
        this.handler = handler;
        this.listPeripheral = listPeripheral;
        this.type = type;
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
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.INVISIBLE);

        button_now_setting_back.setVisibility(View.VISIBLE);
        button_now_setting_back.setText("返回");
        button_now_setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(type == 0)
                {
                    FragmentMealPortSettingManager.printer_peripheral_id = adapter.getNowChoose();
                }
                else if(type == 1)
                {
                    if(adapter.getNowChoose() != 0)
                    {
                        FragmentMealPortSettingManager.call_peripheral_id = adapter.getNowChoose();
                    }
                }

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


        View grid = inflater.inflate(R.layout.setting_mp_listview_perpheral_list, null);
        ListView lv = (ListView) grid.findViewById(R.id.listview_setting_mp_peripheral);
        TextView tvNo = (TextView)grid.findViewById(R.id.tv_setting_mp_peripheral_no);


        List<peripheral> listTv = new ArrayList<peripheral>();
        List<peripheral> listPrinter = new ArrayList<peripheral>();

        if(listPeripheral.size() > 0)
        {
            if(type == 0)//打印机
            {
                for(int k=0;k<listPeripheral.size();k++)
                {
                    if(listPeripheral.get(k).type == 1)
                    {
                        listPrinter.add(listPeripheral.get(k));
                    }
                }
                adapter = new ListviewSettingMealPortPeripheralAdapter(
                        MainApplication.getContext(),
                        listPrinter,
                        handler,
                        FragmentMealPortSettingManager.printer_peripheral_id
                        );

            }
            else if(type == 1)//电视外设
            {
                for(int k=0;k<listPeripheral.size();k++)
                {
                    if(listPeripheral.get(k).type == 3) {
                        listTv.add(listPeripheral.get(k));
                    }
                }
                adapter = new ListviewSettingMealPortPeripheralAdapter(
                        MainApplication.getContext(),
                        listTv,
                        handler,
                        FragmentMealPortSettingManager.call_peripheral_id
                );
            }
        }


        if(adapter == null)
        {
            adapter = new ListviewSettingMealPortPeripheralAdapter(MainApplication.getContext(),
                    new ArrayList<peripheral>(),handler,0);
        }
        adapter.setNotifyAdapter(adapter);
        lv.setAdapter(adapter);

        TextView tv_choose_tips = (TextView)grid.findViewById(R.id.tv_setting_mp_peripheral_choose_tips);
        if(listPeripheral.size() > 0)
        {
            if(type == 0)
            {
                tv_choose_tips.setText("请选择打印机");
            }
            else
            {
                tv_choose_tips.setText("请选择叫号电视");
            }
            tv_choose_tips.setVisibility(View.VISIBLE);
        }

        TextView tv_printer_add = (TextView) grid.findViewById(R.id.tv_setting_mp_peripheral_add);
        if(type == 0)
        {
            tv_printer_add.setText("添加打印机");
            tvNo.setText("不打印小票");
        }
        else if(type == 1)
        {
            tv_printer_add.setText("添加电视设备");
            tvNo.setText("不使用叫号设备");
        }

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type == 0)
                {
                    FragmentMealPortSettingManager.printer_peripheral_id = 0;
                }
                else if(type == 1)
                {
                    FragmentMealPortSettingManager.call_peripheral_id = 0;
                }

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

        tv_printer_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainApplication.getContext(), "add test", Toast.LENGTH_SHORT).show();
                FragmentMealPortSettingManager.showFragmentMealPortPeripheralAdd();

            }
        });
        return grid;
    }
}
