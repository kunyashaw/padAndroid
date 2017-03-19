package com.huofu.RestaurantOS.fragment.settings.Printer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.List;

/**
 * author: Created by zzl on 15/8/25.
 */
@SuppressLint("ValidFragment")
public class FragmentPrinterChooseSet extends Fragment{

    Context ctxt;
    List<peripheral> listPeripheralPrinter;
    View.OnClickListener ocl;

    public FragmentPrinterChooseSet(List<peripheral> list)
    {
        this.listPeripheralPrinter = list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctxt = MainApplication.getContext();
        ocl = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //popWindowSetPriter.dismiss();
                switch(v.getId())
                {
                    /*case R.id.rl_setting_layout_printer_create_order:
                        FragmentsPrinterSettingManager.showFragmentPrinterIpList(0);
                        break;
                    case R.id.rl_setting_layout_printer_kitechen:
                        FragmentsPrinterSettingManager.showFragmentPrinterIpList(1);
                        break;*/
                    case R.id.rl_setting_layout_printer_self_takeup:
                        FragmentsPrinterSettingManager.showFragmentPrinterIpList(2);
                        break;
                }

            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        Button button_now_setting_back = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button)getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_edit_cancel.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);
        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("打印机设置");
        button_now_setting_content.setOnClickListener(null);
        button_now_setting_content.setCompoundDrawables(null,null,null,null);

        View grid = inflater.inflate(R.layout.setting_layout_printer, null);

      /*  RelativeLayout rl_kitchen = (RelativeLayout) grid.findViewById(R.id.rl_setting_layout_printer_kitechen);
        rl_kitchen.setOnClickListener(ocl);
        TextView tv_kitchen = (TextView) grid.findViewById(R.id.tv_setting_layout_printer_kitchen_content);

        RelativeLayout rl_create_order = (RelativeLayout) grid.findViewById(R.id.rl_setting_layout_printer_create_order);
        rl_create_order.setOnClickListener(ocl);
        TextView tv_create_order = (TextView) grid.findViewById(R.id.tv_setting_layout_printer_create_order_content);
*/
        RelativeLayout rl_takeup = (RelativeLayout) grid.findViewById(R.id.rl_setting_layout_printer_self_takeup);
        rl_takeup.setOnClickListener(ocl);
        TextView tv_takeup = (TextView) grid.findViewById(R.id.tv_setting_layout_printer_self_takeup_content);

        String now_local_kitchen_ip = (String) LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("ip");
        String now_create_order_ip = (String) LocalDataDeal.readFromLocalIpCreateOrderPrinter(ctxt).get("ip");
        String now_takeup_ip = (String) LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");

       /* if (now_local_kitchen_ip == null || now_local_kitchen_ip.equals("")) {
            tv_kitchen.setText("未设置");
        } else {
            String printerName = "";
            if (listPeripheralPrinter != null) {
                for (int i = 0; i < listPeripheralPrinter.size(); i++) {
                    if (listPeripheralPrinter.get(i).con_id.equals(now_local_kitchen_ip)) {
                        printerName = listPeripheralPrinter.get(i).name;
                    }
                }
            }

            //tv_kitchen.setText(printerName+"("+now_local_kitchen_ip+")");
            if (printerName.equals("")) {
                tv_kitchen.setText("未设置");
            } else {
                tv_kitchen.setText(printerName + "(" + now_local_kitchen_ip + ")");
            }
        }

        if (now_create_order_ip == null || now_create_order_ip.equals("")) {
            tv_create_order.setText("未设置");
        } else {
            String printerName = "";
            if (listPeripheralPrinter != null) {
                for (int i = 0; i < listPeripheralPrinter.size(); i++) {
                    if (listPeripheralPrinter.get(i).con_id.equals(now_create_order_ip)) {
                        printerName = listPeripheralPrinter.get(i).name;
                    }
                }
            }
            if (printerName.equals("")) {
                tv_create_order.setText("未设置");
            } else {
                tv_create_order.setText(printerName + "(" + now_create_order_ip + ")");
            }
            //tv_create_order.setText(printerName+"("+now_create_order_ip+")");
        }*/

        if (now_takeup_ip == null || now_takeup_ip.equals("")) {
            tv_takeup.setText("未设置");
        } else {
            String printerName = "";
            if (listPeripheralPrinter != null) {
                for (int i = 0; i < listPeripheralPrinter.size(); i++) {
                    if (listPeripheralPrinter.get(i).con_id.equals(now_takeup_ip)) {
                        printerName = listPeripheralPrinter.get(i).name;
                    }
                }
            }
            if (printerName.equals("")) {
                tv_takeup.setText("未设置");
            } else {
                tv_takeup.setText(printerName + "(" + now_takeup_ip + ")");
            }
        }

        return grid;
    }



}
