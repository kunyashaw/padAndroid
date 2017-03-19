package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.store;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;

/****
 *
 * 设置中超级管理员管理多家店铺时外送的店铺列表对应的适配器
 *
 */

public class ListviewStoreItemAdapter extends BaseAdapter{

	List<store> ls = null;
	Context ctxt = null;
	long todatTimeStamp = 0;
	
	public ListviewStoreItemAdapter(List<store> ls, Context ctxt)
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View grid = inflater.inflate(R.layout.lsitview_store_item, null);
		
		TextView tvName = (TextView)grid.findViewById(R.id.tv_store_item_name);
		
		tvName.setText(ls.get(position).name);
		
		
		grid.findViewById(R.id.rl_store_item).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommonUtils.sendMsg(""+position, SettingsActivity.CHOOSE_STORE_ID, SettingsActivity.handler);
			}
		});
		
		return grid;
	}
	

}
