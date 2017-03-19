package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.StoreProduct;
import com.huofu.RestaurantOS.ui.pannel.stockPlan.StockPlanActivity;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Created by zzl on 15/8/13.
 */

/****
 *
 * 未开启库存列表中的一个item在弹窗里，展示营业时间段和周几的列表对应的适配器
 *
 */
public class ListViewStockSupplyClosedModifyAdapter extends BaseAdapter{
    public List<StoreProduct> ls = new ArrayList<StoreProduct>();
    Context ctxt;
    Handler handler;
    int list_order;

    public ListViewStockSupplyClosedModifyAdapter(List<StoreProduct> ls,Context ctxt,Handler handler,int k)
    {
        this.ls = ls;
        this.ctxt = ctxt;
        this.handler = handler;
        list_order = k;
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
        LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.listview_stock_supply_closed_modify_popupwindow_item,null);
        final Button btnAmount = (Button)grid.findViewById(R.id.btn_listview_stock_supply_closed_modify_popupwidnow_item_amount);
        final TextView tvWeekDay = (TextView)grid.findViewById(R.id.textview_listview_stock_supply_closed_modify_popupwidnow_item_name);

        final StoreProduct sp = ls.get(position);
        String week = "星期";
        switch(sp.week_day)
        {
            case 1:
                week+="一";
                break;
            case 2:
                week+="二";
                break;
            case 3:
                week+="三";
                break;
            case 4:
                week+="四";
                break;
            case 5:
                week+="五";
                break;
            case 6:
                week+="六";
                break;
            case 7:
                week+="日";
                break;
        }

        if(sp.thisWeek)
        {
            tvWeekDay.setText(week);
        }
        else
        {
            tvWeekDay.setText(week+"(下周)");
        }

        btnAmount.setText(CommonUtils.DoubleDeal(sp.amount));

        btnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message msg = new Message();
                Map map = new HashMap<String, Object>();
                map.put("list_order", list_order);
                map.put("ls", ls);
                map.put("position", position);
                msg.obj = map;

                if (StockPlanActivity.flagActivte) {
                    msg.what = StockPlanActivity.UPDATE_KEYBOARD_ENTER_OCL;
                    handler.sendMessage(msg);
                    CommonUtils.sendMsg("1", StockPlanActivity.SLIDING_DRAWDER_TOGGLE_ON, handler);
                    CommonUtils.LogWuwei(StockPlanActivity.tag,"正在修改库存日期为："+
                            tvWeekDay.getText().toString()+sp.store_time_bucket.name+" 当前库存为:"+btnAmount.getText().toString());
                }

                if (StockSupplyActivity.flagActivte) {
                    msg.what = StockSupplyActivity.UPDATE_KEYBOARD_ENTER_OCL;
                    handler.sendMessage(msg);
                    CommonUtils.sendMsg("1", StockSupplyActivity.SLIDING_DRAWDER_TOGGLE_ON, handler);
                    CommonUtils.LogWuwei(StockSupplyActivity.tag, "正在修改库存日期为：" + tvWeekDay.getText().toString()+" 当前库存为:"+btnAmount.getText().toString());;
                }

            }
        });


        return grid;
    }
}
