package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.fragment.settings.Delivery.FragmentsDeliverySettingManager;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton.OnToggleChanged;

import java.util.Calendar;
import java.util.List;

/****
 *
 * 外送营业时间段列表对应的适配器
 *
 */

public class ListviewSettingDeliveryTimebucketAdapter extends BaseAdapter{

	List<MealBucket> ls = null;
	Context ctxt = null;
	Handler handler;
	long todatTimeStamp = 0;
	
	public ListviewSettingDeliveryTimebucketAdapter(List<MealBucket> ls, Context ctxt,Handler handler)
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
		
		View grid = inflater.inflate(R.layout.setting_delivery_time_bucket, null);
		
		TextView tv = (TextView)grid.findViewById(R.id.textview_setting_delivery_time_bucket_info);
		String name = ls.get(position).name;
		if(todatTimeStamp == 0)
		{
			Time time = new Time();
			time.setToNow();
			Calendar cal = Calendar.getInstance();
			cal.set(time.year, time.month, time.monthDay, 0,0,0);
			todatTimeStamp = (long) (cal.getTimeInMillis()/1000);
		}
		String time = "("+CommonUtils.getStrTime(todatTimeStamp+(ls.get(position).start_time/1000))+"-"+
						CommonUtils.getStrTime(todatTimeStamp+(ls.get(position).end_time)/1000)+")";
		tv.setText(name+" "+time);
		
		final int timeBucketId = ls.get(position).time_bucket_id;

		final ToggleButton sb = (ToggleButton)grid.findViewById(R.id.btn_setting_delivery_time_bucket_toggle);
		
		sb.setOnToggleChanged(new OnToggleChanged() {
			
			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				final boolean flagSwitch = on;
				int flag = -1;
				if(on)
				{
					flag = 1;
					CommonUtils.sendMsg("正在开启营业时间段", SettingsActivity.SHOW_LOADING_TEXT,handler);
				}
				else
				{
					flag = 0;
					CommonUtils.sendMsg("正在关闭营业时间段", SettingsActivity.SHOW_LOADING_TEXT,handler);
				}
				final int flagResult = flag;

				ApisManager.SetStoreDeliveryTimeBucket(ls.get(position).time_bucket_id, flag, FragmentsDeliverySettingManager.store_id, new ApiCallback() {
					@Override
					public void success(Object object) {
						CommonUtils.sendMsg("",SettingsActivity.HIDE_LOADING,handler);
						for(int k=0;k<FragmentsDeliverySettingManager.mealBucketList.size();k++)
						{
							if(timeBucketId == FragmentsDeliverySettingManager.mealBucketList.get(k).time_bucket_id)
							{
								FragmentsDeliverySettingManager.mealBucketList.get(k).delivery_supported = flagResult;
								break;
							}
						}

					}

					@Override
					public void error(BaseApi.ApiResponse response) {
						CommonUtils.sendMsg("",SettingsActivity.HIDE_LOADING,handler);
						CommonUtils.sendMsg(response.error_message,SettingsActivity.SHOW_ERROR_MESSAGE,handler);
						if(flagSwitch)
						{
							sb.toggleOff();
						}
						else
						{
							sb.toggleOn();
						}

					}
				});
			}
		});
		
		if(ls.get(position).delivery_supported == 1)
		{
			sb.setToggleOn();
		}
		else
		{
			sb.setToggleOff();
		}

		
		return grid;
	}
	

}
