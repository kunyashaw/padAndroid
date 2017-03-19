package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.ui.pannel.delivery.ViewHolderDeliveried;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;
/****
 *
 * 外送已经送达列表的适配器
 *
 */
public class GridviewDeliveryGetDeliveried extends BaseAdapter{

	List<OrderDetailInfo> listODI = null;
	Context ctxt;
	Handler handler;
	public GridviewDeliveryGetDeliveried(List<OrderDetailInfo> ls, Context ctxt, Handler handler)
	{
		this.listODI = ls;
		this.ctxt = ctxt;
		this.handler = handler;
	}
	
	
	public void add(List<OrderDetailInfo> ls)
	{
		CommonUtils.LogWuwei(DeliveryActivity.tag, "before 已送达ls.size is "+ls.size());;
		if(ls != null)
		{
			for(int k=0;k<ls.size();k++)
			{
				boolean flagAdd = true;
				for(int t=0;t<listODI.size();t++)
				{
					if(listODI.get(t).order_id.equals(ls.get(k).order_id))
					{
						flagAdd = false;
						break;
					}
				}
				if(flagAdd)
				{
					listODI.add(ls.get(k));
				}
			}
		}
		CommonUtils.LogWuwei(DeliveryActivity.tag, "after 已送达ls.size is "+ls.size());;
	}

	public void clear()
	{
		if(listODI != null)
		{
			listODI.clear();
		}
	}
	
	public boolean whetherDiff(List<OrderDetailInfo> ls)
	{
		if(ls != null )
		{
			if(ls.size() != listODI.size())
			{
				return true;
			}
		}

		for(int k=0;k<listODI.size();k++)
		{
			String order_id = listODI.get(k).order_id;
			
			//假设为false，如果遍历时又相同的，则继续；如果遍历一遍都没有相同的，那么说明ls、listODI是不同的队列，这是否返回true
			boolean flagSame = false;
			
			for(int t=0;t<ls.size();t++)
			{
				String new_order_id = ls.get(t).order_id;
				if(order_id.equals(new_order_id))
				{
					flagSame = true;
					break;
				}
			}
			if(!flagSame)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listODI.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listODI.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub


		ViewHolderDeliveried holder;
		
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.gridview_delivery_done, null);
			
			holder = new ViewHolderDeliveried();
			
			holder.rlHead = (RelativeLayout)convertView.findViewById(R.id.gridview_delivery_done_head);
			
			holder.tvSerial = (TextView)convertView.findViewById(R.id.tv_gridview_delivery_done_serinal);
			holder.tvUserInfo = (TextView)convertView.findViewById(R.id.tv_gridview_delivery_done_item_delivery_send_info);
			
			
			holder.tvBuildingAddress = (TextView)convertView.findViewById(R.id.tv_gridview_delivery_done_item_delivery_building_address);
			holder.tvOrderContent = (TextView)convertView.findViewById(R.id.tv_gridview_delivery_done_item_delivery_order_content);
			
			convertView.setTag(holder);
			
		}
		else
		{
			holder = (ViewHolderDeliveried)convertView.getTag();
		}
		
		
		final OrderDetailInfo odi = listODI.get(position);
		
		holder.rlHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);
				
				ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
					@Override
					public void success(Object object) {
						CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
						OrderDetailInfo odi = new OrderDetailInfo();
						JSONObject obj = (JSONObject) object;
						JSONObject objSO = obj.getJSONObject("store_order");
						StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(CommonUtils.converBooleanToInt(objSO.toString()), StoreOrder.class);
						odi.store_order = so;
						odi.take_serial_number = so.take_serial_number;
						odi.list_charge_items_all = so.order_items;

						Message msg = new Message();
						msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
						msg.obj = odi;
						handler.sendMessage(msg);
					}

					@Override
					public void error(BaseApi.ApiResponse response) {
						CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
						CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
					}
				});
			}
		});
		
		holder.tvSerial.setText(odi.store_order.take_serial_number+"");
		holder.tvUserInfo.setText(odi.store_order.store_order_delivery.delivery_staff.name+"("
						+CommonUtils.getHourMin(odi.store_order.store_order_delivery.delivery_finish_time)+"送达)");
		
		holder.tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name+" "+odi.store_order.store_order_delivery.user_address);
		holder.tvOrderContent.setText(odi.orderContent);
		
		
		return convertView;
	}
	
	

}
