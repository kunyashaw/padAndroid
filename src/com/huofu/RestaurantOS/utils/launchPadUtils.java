package com.huofu.RestaurantOS.utils;

import android.content.Context;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class launchPadUtils {
	
	public static List getNavigationFuncMap()
	{
		List listFuncMap = new ArrayList<Map>();
		Context ctxt = MainApplication.getContext();
		Map mapIconName = new HashMap<String, Object>();
		listFuncMap = new ArrayList<Map>();

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_meald_done);
		mapIconName.put("name", ctxt.getString(R.string.launchPadMealDoneName));
		listFuncMap.add(mapIconName);

		/*mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_take_up);
		mapIconName.put("name", ctxt.getString(R.string.launchPadSelfTakeUpName));
		listFuncMap.add(mapIconName);*/

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.stock);
		mapIconName.put("name", ctxt.getString(R.string.launchPadStockSupplyName));
		listFuncMap.add(mapIconName);

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_settings);
		mapIconName.put("name", ctxt.getString(R.string.launchPadSettingsName));
		listFuncMap.add(mapIconName);

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_show_all);
		mapIconName.put("name", ctxt.getString(R.string.launchPadAllFuncName));
		listFuncMap.add(mapIconName);

		return listFuncMap;

	}
	
	
	public static List getLauchpadFuncMap()
	{
		List listAllFuncMap = new ArrayList<Map>();
		
		Map mapIconName = new HashMap<String, Object>();
		listAllFuncMap = new ArrayList<Map>();

		Context ctxt = MainApplication.getContext();

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_meald_done);
		mapIconName.put("name", ctxt.getString(R.string.launchPadMealDoneName));
		listAllFuncMap.add(mapIconName);

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_take_up);
		mapIconName.put("name", ctxt.getString(R.string.launchPadSelfTakeUpName));
		listAllFuncMap.add(mapIconName);

		
		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_delivery);
		mapIconName.put("name", ctxt.getString(R.string.launchPadDeliveryName));
		listAllFuncMap.add(mapIconName);
		
		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_pad_edit_menu);
		mapIconName.put("name", ctxt.getString(R.string.launchPadMenuEditName));
		listAllFuncMap.add(mapIconName);
		
		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_settings);
		mapIconName.put("name", ctxt.getString(R.string.launchPadSettingsName));
		listAllFuncMap.add(mapIconName);

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.icon_stock_plan);
		mapIconName.put("name", MainApplication.getContext().getString(R.string.launchPadStockPlanName));
		listAllFuncMap.add(mapIconName);

		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.stock);
		mapIconName.put("name", MainApplication.getContext().getString(R.string.launchPadStockSupplyName));
		listAllFuncMap.add(mapIconName);


		mapIconName = new HashMap<String, Object>();
		mapIconName.put("icon", R.drawable.default_head_pic);
		mapIconName.put("name",MainApplication.getContext().getString(R.string.launchPadLablePrint));
		listAllFuncMap.add(mapIconName);

		return listAllFuncMap;
	}
	
	
	
	

}
