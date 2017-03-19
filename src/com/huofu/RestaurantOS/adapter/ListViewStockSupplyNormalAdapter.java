package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.StoreInventoryDate;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.ViewHolderStockSupplyFixed;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/12.
 */

/***
 *
 * 非周期库存（固定库存）列表对应的适配器
 *
 */
public class ListViewStockSupplyNormalAdapter extends BaseAdapter{
    List<StoreInventoryDate> ls = new ArrayList<StoreInventoryDate>();
    Context ctxt;
    Handler handler;

    public ListViewStockSupplyNormalAdapter(List<StoreInventoryDate> ls,Context ctxt,Handler handler)
    {
        this.ls = ls;
        this.ctxt = ctxt;
        this.handler = handler;
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

        try
        {
            final ViewHolderStockSupplyFixed holder;

            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.stock_supply_normal_list_item, null);
                holder = new ViewHolderStockSupplyFixed();

                holder.tvName = (TextView)convertView.findViewById(R.id.stock_supply_normal_list_item_name);
                holder.tvPreSell = (TextView)convertView.findViewById(R.id.textview_stock_suppply_normal_list_item_presell);

                holder.tvBooked = (TextView)convertView.findViewById(R.id.textview_stock_supply_normal_list_item_booked_this_timebucket);
                holder.tvAlreadyPushed = (TextView)convertView.findViewById(R.id.textview_stock_supply_normal_list_item_already_pushed_meal);
                holder.tvWaitingPush = (TextView)convertView.findViewById(R.id.textview_stock_supply_normal_list_item_waiting_push);
                holder.tvLeft = (TextView)convertView.findViewById(R.id.button_stock_supply_normal_modify_number);
                holder.tvLeftTips = (TextView)convertView.findViewById(R.id.textview_stock_supply_normal_list_item_left_tips);
                holder.rlLeft = (RelativeLayout)convertView.findViewById(R.id.rl_stock_supply_normal_list_item_modify);
                convertView.setTag(holder);

            }
            else
            {
                holder = (ViewHolderStockSupplyFixed)convertView.getTag();
            }
            StoreInventoryDate sid = ls.get(position);

            holder.tvName.setText(sid.store_product.name+"("+sid.store_product.unit+")");
            holder.tvPreSell.setText(CommonUtils.DoubleDeal(sid.amount_order_total));

            holder.tvBooked.setText(CommonUtils.DoubleDeal(sid.amount_order));
            holder.tvAlreadyPushed.setText(CommonUtils.DoubleDeal(sid.amount_checkout));
            holder.tvWaitingPush.setText(CommonUtils.DoubleDeal(sid.amount_takeup));
            holder.tvLeft.setText(CommonUtils.DoubleDeal(sid.amount_remain));
            if(sid.amount_remain < 10)
            {
                holder.rlLeft.setBackgroundResource(R.drawable.btn_red_solid);
                holder.tvLeft.setTextColor(ctxt.getResources().getColor(R.color.Constrast));
                holder.tvLeftTips.setTextColor(ctxt.getResources().getColor(R.color.Constrast));
            }
            else
            {
                holder.rlLeft.setBackgroundResource(R.drawable.stock_supply_normal_btn_selector);
                holder.tvLeft.setTextColor(ctxt.getResources().getColor(R.color.Blue));
                holder.tvLeftTips.setTextColor(ctxt.getResources().getColor(R.color.Pale));
            }

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StockSupplyActivity.nowChoocsePostion = position;
                    CommonUtils.sendMsg("",StockSupplyActivity.SHOW_KEYBOARD, handler);
                    CommonUtils.LogWuwei(StockSupplyActivity.tag,"固定库存--"+holder.tvName.getText().toString()
                            +"被选中，之前库存是："+holder.tvLeft.getText().toString());
                }
            };

            holder.rlLeft.setOnClickListener(ocl);
            holder.tvLeft.setOnClickListener(ocl);
            holder.tvLeftTips.setOnClickListener(ocl);
        }
        catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(StockSupplyActivity.tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }
        return convertView;
    }
}
