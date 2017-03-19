package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.fragment.settings.Printer.FragmentsPrinterSettingManager;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.List;

/***
 *
 * 打印机设置时，显示打印机列表对应的适配器
 *
 */

public class ListviewSettingPrinterlistAdapter extends BaseAdapter{

	Context ctxt = null;
	List<peripheral> ls = null;
	String chooseIp = "";
	String tag = "ListviewSettingPrinterlistAdapter";
	int printerId = 0;
	Handler handler;
	ListviewSettingPrinterlistAdapter adapter;

	public ListviewSettingPrinterlistAdapter(Context ctxt, List<peripheral> ls,Handler handler)
	{
		this.ctxt = MainApplication.getContext();
		this.ls = ls;
		this.handler = handler;
	}
	
	public void updateList(List<peripheral> ls)
	{
		this.ls = ls;
	}
	
	public void setPrinter(int choosePrinter)
	{
		this.printerId = choosePrinter;
	}

	public void setNotifyAdapter(ListviewSettingPrinterlistAdapter adapter)
	{
		this.adapter = adapter;
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
		switch (printerId) {
		case 0:
			chooseIp = (String)LocalDataDeal.readFromLocalIpCreateOrderPrinter(ctxt).get("ip");
			chooseName = (String)LocalDataDeal.readFromLocalIpCreateOrderPrinter(ctxt).get("name");
			break;
		case 1:
			chooseIp = (String)LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("ip");
			chooseName = (String)LocalDataDeal.readFromLocalIpKitchPrinter(ctxt).get("name");
			break;
		case 2:
			chooseIp = (String)LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("ip");
			chooseName = (String)LocalDataDeal.readFromLocalIpTakeupPrinter(ctxt).get("name");
			break;
		default:
			break;
		}
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View grid = inflater.inflate(R.layout.setting_printer_list, null);
		
		final ImageView iv = (ImageView)grid.findViewById(R.id.imageview_setting_printer_loading_whether_right);
		
		
		if(chooseIp.equals(ls.get(position).con_id) && chooseName.equals(ls.get(position).name))
		{
			iv.setImageResource(R.drawable.checkbox2);
		}
		else
		{
			iv.setImageResource(android.R.color.transparent);
		}
		
		TextView tv = (TextView)grid.findViewById(R.id.textview_setting_printer_info);
		tv.setText(ls.get(position).name+" :"+ls.get(position).con_id);
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//HandlerUtils.showToast(ctxt, ls.get(position).name+"is clicked");
				iv.setImageResource(R.drawable.loading);
				iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));
				final SettingsActivity set = new SettingsActivity();
				new Thread()
				{
					public void run()
					{
						OrderDetailInfo.time_printer_test_connect_time = 10000;
						if(CommonUtils.getPrinterTestResult(ls.get(position).con_id) == 0)
						{
							set.runOnUiThread(new Runnable() {
								public void run() {
									iv.clearAnimation();
									iv.setImageResource(R.drawable.checkbox2);
									iv.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
									OrderDetailInfo.time_printer_test_connect_time = 1000;

									switch (printerId) {
									case 0:
										LocalDataDeal.writeToLocalIpCreateOrderPrinter(ls.get(position).name,
												ls.get(position).con_id, ctxt);
										break;
									case 1:
										LocalDataDeal.writeToLocalIpKitchPrinter(ls.get(position).name,
												ls.get(position).con_id, ctxt);
										break;
									case 2:
										LocalDataDeal.writeToLocalIpTakeupPrinter(ls.get(position).name,
												ls.get(position).con_id, ctxt);
										break;
									default:
										break;
									}
									adapter.notifyDataSetChanged();

								}
							});

						}
						else
						{
							set.runOnUiThread(new Runnable() {
								public void run() {
									iv.clearAnimation();
									iv.setImageBitmap(null);
									CommonUtils.sendMsg("设置失败,打印机无法连接", SettingsActivity.SHOW_ERROR_MESSAGE, SettingsActivity.handler);
									OrderDetailInfo.time_printer_test_connect_time = 1000;
								}
							});

						}
					}
				}.start();


			}
		});
		
		
		Button btn = (Button)grid.findViewById(R.id.button_setting_printer_edit);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentsPrinterSettingManager.showFragmentPrinterEdit(position);
			}
		});
		
		return grid;
	}
	

}
