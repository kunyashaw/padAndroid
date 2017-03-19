package com.huofu.RestaurantOS.bean.pushMeal;

import com.alibaba.fastjson.JSONArray;

/****
 *
 * 出餐历史
 *
 */

public class PushHistoryFood implements Comparable<PushHistoryFood> {

	public Integer packaged ;  //是否打包
	public String order_id="";
	public long create_time = 0;//出餐时间
	public JSONArray meal_charges;
	public int take_serial_seq;
	public int take_serial_number;
	public int take_mode;

	public String orderName = "";     //订单内容
	public boolean flagOnly = false;//在出餐历史中是否需要显示流水号序号s

	@Override
	public int compareTo(PushHistoryFood another) {
		// TODO Auto-generated method stub
		
		PushHistoryFood phfAnother = another;
		long updateTimeAnother = phfAnother.create_time;
		long updateTime = this.create_time;
		
		if(updateTime > updateTimeAnother)
		{
			return -1;
		}
		else if(updateTime < updateTimeAnother)
		{
			return 1;
		}
		else
		{
			return 0;
		}
		
	}
	
	
	
	
}
