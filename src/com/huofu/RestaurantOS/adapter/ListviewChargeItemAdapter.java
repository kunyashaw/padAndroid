package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;

public class ListviewChargeItemAdapter extends BaseAdapter{

	List<ChargItem> ls;
	Context ctxt;
	
	
	public ListviewChargeItemAdapter(List<ChargItem> ls, Context ctxt)
	{
		this.ls = ls;
		this.ctxt = ctxt;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			ChargItem ci = ls.get(position);
			
			TextView tv = new TextView(ctxt);
			tv.setTextColor(Color.parseColor("#565656"));
			tv.setTextSize(18);
			
			tv.setText(ci.charge_item_name+"(￥"+ci.charge_item_price/100.0+")×"+CommonUtils.DoubleDeal(ci.charge_item_amount));
			tv.setGravity(Gravity.LEFT);
			return tv;
		}
		else
		{
			return convertView;	
		}
	}
	
	

}
