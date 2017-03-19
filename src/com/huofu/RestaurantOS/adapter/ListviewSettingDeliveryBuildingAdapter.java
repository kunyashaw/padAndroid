package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.DeliveryBuilding;
import com.huofu.RestaurantOS.fragment.settings.Delivery.FragmentsDeliverySettingManager;

import java.util.List;

/****
 *
 * 外送楼宇列表对应的适配器
 *
 */

public class ListviewSettingDeliveryBuildingAdapter extends BaseAdapter{

	List<DeliveryBuilding> ls = null;
	Context ctxt = null;
	long todatTimeStamp = 0;
	
	public ListviewSettingDeliveryBuildingAdapter(List<DeliveryBuilding> ls, Context ctxt)
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
		
		View grid = inflater.inflate(R.layout.delivery_address_list_item, null);
		
		TextView tvName = (TextView)grid.findViewById(R.id.tv_setting_delivery_name);
		TextView tvAddress = (TextView)grid.findViewById(R.id.tv_setting_delivery_adress);
		RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_setting_delivery_building);
		
		rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FragmentsDeliverySettingManager.position = position;
				FragmentsDeliverySettingManager.showDeliveryBuildingEdit();
				
			}
		});
		
		tvName.setText(ls.get(position).name);
		tvAddress.setText(ls.get(position).address);
		
		return grid;
	}
	

}
