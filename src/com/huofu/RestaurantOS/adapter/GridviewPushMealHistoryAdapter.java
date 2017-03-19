package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.pushMeal.PushHistoryFood;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.ui.pannel.call.ClientSpeak;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.ViewHolderPushHistory;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * 出餐历史
 */
public class GridviewPushMealHistoryAdapter extends BaseAdapter {

    public String tag = "push_history_adapter";
    public Context ctxt = null;
    public List<PushHistoryFood> list = null;
    Handler handler;

    public GridviewPushMealHistoryAdapter(Handler handler, Context context, List<PushHistoryFood> ls) {
        ctxt = context;
        list = ls;
        this.handler = handler;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolderPushHistory holder;
        final int position = pos;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolderPushHistory();

            convertView = inflater.inflate(R.layout.gridview_history_item, null);

            holder.tv_serinal_num = (TextView) convertView.findViewById(R.id.textview_grdiview_history_item_serinal_num);

            holder.tv_whether_packaged = (TextView) convertView.findViewById(R.id.textview_grdiview_history_item_whether_packaged);

            holder.buttonContent = (TextView) convertView.findViewById(R.id.button_gridview_history_item_order_content);
            holder.buttonTime = (TextView) convertView.findViewById(R.id.textview_grdiview_history_item_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderPushHistory) convertView.getTag();
        }

        String serinal_num = "";

        String packageageInfo = "";
        try {
            if (list.get(position).packaged == 1) {
                if (list.get(position).take_mode == 2 || list.get(position).take_mode == 3) {
                    holder.tv_whether_packaged.setText("打包");

                } else if (list.get(position).take_mode == 4) {
                    holder.tv_whether_packaged.setText("外送");
                }
            } else {
                holder.tv_whether_packaged.setText("");
            }

            holder.buttonTime.setText(CommonUtils.getStrTime(list.get(position).create_time));

            if (list.get(pos).take_serial_seq == 0) {
                serinal_num = list.get(pos).take_serial_number + "";
            } else {
                if(list.get(pos).flagOnly)
                {
                    serinal_num = list.get(pos).take_serial_number+"";
                }
                else
                {
                    serinal_num = list.get(pos).take_serial_number + "-" + list.get(pos).take_serial_seq;
                }

            }
            holder.tv_serinal_num.setText(serinal_num);

            String displayContent = list.get(position).orderName;
            int num = CommonUtils.getCharNum(list.get(position).orderName, '\n');
            StringBuffer sb = new StringBuffer(displayContent);
            displayContent = "";
            int count = 0;
            for (int i = 0; i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (ch == '\n') {
                    if (count != num - 1) {
                        ch = '、';
                    }
                    count++;
                }
                displayContent += ch;
            }

            holder.buttonContent.setText(displayContent);

            OnClickListener ocl = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    String content = "";
                    List<ChargItem> listCI = JSON.parseArray(list.get(pos).meal_charges.toJSONString(),ChargItem.class);
                    for(ChargItem ci:listCI)
                    {
                        content+=ci.charge_item_name+"×"+CommonUtils.DoubleDeal(ci.charge_item_amount)+"\n";
                    }
                    Map map = new HashMap();
                    map.put("takeSerialNum", list.get(pos).take_serial_number);
                    map.put("takeSerialSeq", list.get(pos).take_serial_seq);
                    map.put("content", content);
                    map.put("orderId", list.get(pos).order_id);
                    map.put("packaged", list.get(pos).packaged);
                    CommonUtils.sendObjMsg(map, PushMealActivity.SHOW_HISTORY_REPRINT_DIALOG, handler);
                }
            };

            holder.buttonContent.setOnClickListener(ocl);

            final Button call = (Button) convertView.findViewById(R.id.button_gridview_history_item_call);

            call.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CommonUtils.LogWuwei(tag, call.getText() + "is calling");
                    ClientSpeak.ClientSpeak(list.get(position).take_serial_number, MainApplication.getContext());
                }
            });
        } catch (Exception e) {

        }


        return convertView;

    }

}
