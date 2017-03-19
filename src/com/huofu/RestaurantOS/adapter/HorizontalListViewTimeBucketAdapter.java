package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;

/**
 * author: Created by zzl on 15/8/19.
 */
public class HorizontalListViewTimeBucketAdapter extends BaseAdapter {

    List<MealBucket> ls;
    Context ctxt;
    Handler handler;

    public HorizontalListViewTimeBucketAdapter(List<MealBucket> ls, Context ctxt, Handler handler) {
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

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView tv = new TextView(ctxt);
        tv.setWidth(250);
        tv.setText("" + ls.get(position).name);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (position < ls.size()) {
                        Message msg = new Message();
                        msg.obj = ls.get(position).time_bucket_id;
                        msg.what = StockSupplyActivity.UPDATE_NOW_CHOOSE_TIMEBUCKET_ID;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    StackTraceElement[] listException = e.getStackTrace();
                    for (int k = 0; k < listException.length; k++) {
                        CommonUtils.LogWuwei(StockSupplyActivity.tag, listException[k].getFileName() + "-方法:"
                                + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
                    }
                }

            }
        });
        tv.setOnTouchListener(null);
        return tv;
    }
}
