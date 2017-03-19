package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;

import java.util.List;
import java.util.Map;

/****
 *
 * launchPad对应的左侧快捷功能列表对应的适配器
 *
 */

public class NavigationBarFuncAdapter extends BaseAdapter{

	public List<Map> ls;
	public Context ctxt;
	
	public NavigationBarFuncAdapter(List<Map> list, Context context)
	{
		ctxt = context;
		ls = list;
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
		
		if(convertView != null)
		{
			return convertView;
		}
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View grid = inflater.inflate(R.layout.listview_navigation_item, null);
		
		ImageView ib = (ImageView)grid.findViewById(R.id.imageview_navigation_func_item);
		TextView tv = (TextView)grid.findViewById(R.id.textview_navigation_func_item);
		
		final Map map = ls.get(position);
		ib.setBackgroundResource((Integer)map.get("icon"));
		tv.setText((String)map.get("name"));
		
		/*
		grid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HandlerUtils.showToast(ctxt, "clicked");
			}
		});*/
		
		return grid;
	}
	
	
	
}
