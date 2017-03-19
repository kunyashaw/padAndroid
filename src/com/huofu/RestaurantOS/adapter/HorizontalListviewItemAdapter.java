package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

/***
 *
 * 外送中批量配送的横向列表对应的适配器
 *
 */
public class HorizontalListviewItemAdapter extends BaseAdapter{
	

	List<OrderDetailInfo> ls;
	Context ctxt;
	Handler handler;
	
	public HorizontalListviewItemAdapter(List<OrderDetailInfo> ls, Context ctxt, Handler handler)
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
		
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View grid = inflater.inflate(R.layout.horizontal_scrollview_delivery_send_many_item, null);
		
		RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_horizontal_item_child);
		TextView tvSerinal = (TextView)grid.findViewById(R.id.tv_horizontal_scrollview_take_serinal_num);
		TextView tvAddress = (TextView)grid.findViewById(R.id.tv_horizontal_scrollview_address);
		
		tvSerinal.setText(ls.get(position).take_serial_number+"");
		tvAddress.setText(ls.get(position).store_order.store_order_delivery.delivery_building_address);
		
		BadgeView badge = new BadgeView(ctxt,rl);
		badge.setBackgroundResource(R.drawable.delete);
		badge.show();
		
		final OrderDetailInfo odi = ls.get(position);
		
		OnClickListener ocl = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CommonUtils.LogWuwei("", "DeliveryActivity.listDeliveryChoose.size is "+ DeliveryActivity.listDeliveryChoose.size());
				
				for(int k=0;k< DeliveryActivity.listDeliveryChoose.size();k++)
				{
					OrderDetailInfo odiTmp = DeliveryActivity.listDeliveryChoose.get(k);
					if(odiTmp.order_id.equals(odi.order_id))
					{
						DeliveryActivity.listDeliveryChoose.remove(odi);
					}
				}
				
				if(DeliveryActivity.listDeliveryChoose.size() == 0)
				{
					DeliveryActivity.listDeliveryChoose.clear();
					CommonUtils.sendMsg(null, DeliveryActivity.CLEAN_DELIVERY_DEPATCH_BACKGROUND, handler);
				}
				
				CommonUtils.sendMsg("", DeliveryActivity.UPDATE_HORIZONAL_SCORLLVIEW, handler);
			}
		};
		
		badge.setOnClickListener(ocl);
		/*rl.setOnClickListener(ocl);
		tvSerinal.setOnClickListener(ocl);
		tvAddress.setOnClickListener(ocl);*/
		
		
		return grid;
	}
	
}
