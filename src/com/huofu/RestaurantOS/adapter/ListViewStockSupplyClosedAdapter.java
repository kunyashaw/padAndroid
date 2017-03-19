package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.StoreInventoryDate;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.ViewHolderStockSupplyClosed;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/12.
 */
public class ListViewStockSupplyClosedAdapter extends BaseAdapter{
    public List<StoreInventoryDate> ls = new ArrayList<StoreInventoryDate>();
    Context ctxt;
    Handler handler;

    public ListViewStockSupplyClosedAdapter(List<StoreInventoryDate> ls,Context ctxt,Handler handler)
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
            final ViewHolderStockSupplyClosed holder;

            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.stock_supply_closed_list_item, null);
                holder = new ViewHolderStockSupplyClosed();

                holder.tvName = (TextView)convertView.findViewById(R.id.stock_supply_closed_list_item_name);
                holder.tvPreSell = (TextView)convertView.findViewById(R.id.textview_stock_suppply_closed_list_item_presell);
                holder.tvAlreadyPushed = (TextView)convertView.findViewById(R.id.textview_stock_supply_closed_list_item_already_pushed_meal);
                holder.tvWaitingPush = (TextView)convertView.findViewById(R.id.textview_stock_supply_closed_list_item_waiting_push);

                holder.tb = (ToggleButton)convertView.findViewById(R.id.toggleButton_stock_supply_closed_list_item_switch);
                holder.tb.setToggleOff();
                convertView.setTag(holder);

            }
            else
            {
                holder = (ViewHolderStockSupplyClosed)convertView.getTag();
            }



            StoreInventoryDate sid = ls.get(position);
            holder.tvName.setText(sid.store_product.name + "(" + sid.store_product.unit + ")");

            holder.tvPreSell.setText(CommonUtils.DoubleDeal(sid.amount_order));
            holder.tvAlreadyPushed.setText(CommonUtils.DoubleDeal(sid.amount_checkout));
            holder.tvWaitingPush.setText(CommonUtils.DoubleDeal(sid.amount_takeup));

            holder.tb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    if (on) {
                        CommonUtils.LogWuwei(StockSupplyActivity.tag,holder.tvName.getText().toString()+"开启库存..");
                        CommonUtils.sendMsg("加载中...",StockSupplyActivity.SHOW_LOADING_TEXT,handler);
                        CommonUtils.sendMsg("", StockSupplyActivity.QUERY_MODIFY_PRODCUT_DATA,handler);

                    }
                }
            });
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
