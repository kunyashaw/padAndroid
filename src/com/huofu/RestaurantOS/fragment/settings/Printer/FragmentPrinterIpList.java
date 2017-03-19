package com.huofu.RestaurantOS.fragment.settings.Printer;

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
import com.huofu.RestaurantOS.adapter.ListviewSettingPrinterlistAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/26.
 */
@SuppressLint("ValidFragment")
public class FragmentPrinterIpList extends Fragment {


    Context ctxt;
    List<peripheral> listPeripheralPrinter;
    int printerId;
    View.OnClickListener ocl;
    Handler handler;
    ListviewSettingPrinterlistAdapter list_printer_info_adapter;

    public FragmentPrinterIpList(int index,List<peripheral> list,Handler mUiHandler)
    {
        printerId = index;
        listPeripheralPrinter = list;
        this.handler = mUiHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.listPeripheralPrinter = FragmentsPrinterSettingManager.listPeripheralPrinter;
        this.list_printer_info_adapter = FragmentsPrinterSettingManager.list_printer_info_adapter;
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

        button_now_setting_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        listPeripheralPrinter = (List<peripheral>) object;
                        FragmentsPrinterSettingManager.showfragmentPrinterChooseSet(listPeripheralPrinter);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {

                    }
                });
                //FragmentsPrinterSettingManager.showfragmentPrinterChooseSet(listPeripheralPrinter);
            }
        });


        View grid = inflater.inflate(R.layout.listview_printer_list, null);
        ListView lv = (ListView) grid.findViewById(R.id.listview_printer_list_all);

        List<peripheral> list = new ArrayList<peripheral>();
        for (int k = 0; k < listPeripheralPrinter.size(); k++) {
            boolean flagAdd = true;
            peripheral oldp = listPeripheralPrinter.get(k);
            for (int t = 0; t < k; t++) {
                peripheral newp = listPeripheralPrinter.get(t);
                if (oldp.name.equals(newp.name)) {
                    flagAdd = false;
                }

            }
            if (flagAdd) {
                peripheral p = new peripheral();
                try {
                    p = (peripheral) listPeripheralPrinter.get(k).clone();
                } catch (CloneNotSupportedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                list.add(p);
            }
        }
        listPeripheralPrinter.clear();
        for (int k = 0; k < list.size(); k++) {
            peripheral p = new peripheral();
            try {
                p = (peripheral) list.get(k).clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            listPeripheralPrinter.add(p);
        }
        list_printer_info_adapter = new ListviewSettingPrinterlistAdapter(ctxt, listPeripheralPrinter, handler);
        list_printer_info_adapter.setPrinter(printerId);
        list_printer_info_adapter.setNotifyAdapter(list_printer_info_adapter);
        lv.setAdapter(list_printer_info_adapter);

        TextView tv_printer_add = (TextView) grid.findViewById(R.id.tv_printer_add);
        ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentsPrinterSettingManager.showFragmentPrinterAdd(printerId);
            }
        };
        tv_printer_add.setOnClickListener(ocl);


        return grid;
    }

}

