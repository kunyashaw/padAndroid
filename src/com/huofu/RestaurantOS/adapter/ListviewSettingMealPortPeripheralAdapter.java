package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.fragment.settings.MealPort.FragmentMealPortSettingManager;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.List;

/***
 * 打印机设置时，显示打印机列表对应的适配器
 */

public class ListviewSettingMealPortPeripheralAdapter extends BaseAdapter {

    Context ctxt = null;
    List<peripheral> ls = null;
    String chooseIp = "";
    String tag = SettingsActivity.tag;
    long peripheralId = 0;
    Handler handler;
    ListviewSettingMealPortPeripheralAdapter adapter;

    public ListviewSettingMealPortPeripheralAdapter(
            Context ctxt,
            List<peripheral> ls,
            Handler handler,
            long peripheral_Id
            ) {
        this.ctxt = MainApplication.getContext();
        this.ls = ls;
        this.handler = handler;
        this.peripheralId = peripheral_Id;
    }

    public void updateList(List<peripheral> ls) {
        this.ls = ls;
    }


    public void setNotifyAdapter(ListviewSettingMealPortPeripheralAdapter adapter) {
        this.adapter = adapter;
    }

    public long getNowChoose() {
        return peripheralId;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View grid = inflater.inflate(R.layout.setting_mp_printer_list, null);

        final ImageView iv = (ImageView) grid.findViewById(R.id.imageview_setting_mp_printer_loading_whether_right);
        final TextView tvConnectMark = (TextView) grid.findViewById(R.id.tv_setting_mp_printer_cant_connect);

        if (peripheralId == ls.get(position).peripheral_id) {
            iv.setImageResource(R.drawable.checkbox2);
        } else {
            iv.setImageResource(android.R.color.transparent);
        }

       /* if(FragmentMealPortSettingManager.peripheralType == 0)
        {
            if(ls.get(position).con_id.equals(LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)))
            {
                iv.setImageResource(R.drawable.checkbox2);
                FragmentMealPortSettingManager.printer_peripheral_id= peripheralId;
            }
        }
        else if(FragmentMealPortSettingManager.peripheralType == 1)
        {
            if(ls.get(position).con_id.equals(LocalDataDeal.readFromLocalCallTvIp(ctxt)))
            {
                iv.setImageResource(R.drawable.checkbox2);
                FragmentMealPortSettingManager.call_peripheral_id= peripheralId;
            }

        }*/

        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ls.get(position).type == 3) {
                    LocalDataDeal.writeToLocalCallTvIp(ls.get(position).con_id, ctxt);
                    iv.setImageResource(R.drawable.checkbox2);
                    peripheralId = ls.get(position).peripheral_id;
                    adapter.notifyDataSetChanged();
                    FragmentMealPortSettingManager.call_peripheral_id = peripheralId;
                    return;
                }
                iv.setImageResource(R.drawable.loading);
                iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));
                final SettingsActivity set = new SettingsActivity();
                new Thread() {
                    public void run() {
                        OrderDetailInfo.time_printer_test_connect_time = 10000;

                        if (CommonUtils.getPrinterTestResult(ls.get(position).con_id) == 0) {
                            set.runOnUiThread(new Runnable() {
                                public void run() {
                                    iv.clearAnimation();
                                    iv.setImageResource(R.drawable.checkbox2);
                                    iv.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
                                    OrderDetailInfo.time_printer_test_connect_time = 1000;
                                    peripheralId = ls.get(position).peripheral_id;
                                    FragmentMealPortSettingManager.printer_peripheral_id= peripheralId;
                                    adapter.notifyDataSetChanged();
                                    if (ls.get(position).type == 1) {
                                        LocalDataDeal.writeToLocalIpKitchPrinter(ls.get(position).name,
                                                ls.get(position).con_id, ctxt);
                                    }
                                }
                            });

                        } else {
                            set.runOnUiThread(new Runnable() {
                                public void run() {
                                    iv.clearAnimation();
                                    iv.setImageBitmap(null);
                                    iv.setImageResource(android.R.color.transparent);
                                    CommonUtils.sendMsg("设置失败,该设备无法连接", SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
                                    OrderDetailInfo.time_printer_test_connect_time = 1000;
                                }
                            });

                        }
                    }
                }.start();
            }
        };

        TextView tv = (TextView) grid.findViewById(R.id.textview_setting_mp_printer_info);
        tv.setOnClickListener(ocl);
        tv.setText(ls.get(position).name + " :" + ls.get(position).con_id);
        if (!ls.get(position).printer_can_connect) {
            tvConnectMark.setOnClickListener(ocl);
            tvConnectMark.setVisibility(View.VISIBLE);
            SpannableString s = new SpannableString("(无法连接 重试)");
            s.setSpan(new ForegroundColorSpan(ctxt.getResources().getColor(R.color.Blue)), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvConnectMark.setText(s);
        }


        Button btn = (Button) grid.findViewById(R.id.button_setting_mp_printer_edit);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //FragmentsPrinterSettingManager.showFragmentPrinterEdit(position);
                FragmentMealPortSettingManager.showFragmentMealPortPeripheralEdit(position);
            }
        });

        return grid;
    }


}
