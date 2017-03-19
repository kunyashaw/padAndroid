package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.fragment.settings.Tv.FragmentsTvSettingManager;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.List;

/****
 *
 * 设置电视叫号设置的几种叫号方式对应的适配器
 *
 */

public class ListviewSettingTvCallAdapter extends BaseAdapter{

	Context ctxt = null;
	List<String> ls = null;
	String chooseIp = "";
	String tag = "listviewSettingPrinterListAdapter";
	
	public ListviewSettingTvCallAdapter(Context ctxt, List<String> ls)
	{
		this.ctxt = ctxt;
		this.ls = ls;
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
		chooseIp = "";
		
		String chooseName = "";

		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View grid = inflater.inflate(R.layout.setting_tv_call, null);
		
		final ImageView iv = (ImageView)grid.findViewById(R.id.imageview_setting_tv_call_whether_right);
		
		TextView tv = (TextView)grid.findViewById(R.id.textview_setting_tv_call_info);
		tv.setText(ls.get(position));
		
		if(LocalDataDeal.readFromAutoCall(ctxt) == position)
		{
			iv.setImageResource(R.drawable.checkbox2);	
		}
		else
		{
			//iv.setImageResource(android.R.color.transparent);
			iv.setImageResource(R.drawable.checkbox);
		}
		
		grid.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv.setImageResource(R.drawable.checkbox2);
				LocalDataDeal.writeToAutoCall(position, ctxt);
				FragmentsTvSettingManager.listviewSettingTvCallWayadapter.notifyDataSetChanged();
			}
		});
		
		
		
		return grid;
	}
	

}
