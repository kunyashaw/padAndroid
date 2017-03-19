package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.user.DeliveryStaff;
/****
 *
 * 订单外送信息
 *
 */
public class storeOrderDelivery {

	
	/****
	 * 
	 * 			"delivery_finish_time": 0,
                "user_address": "复星国际中心",
                "delivery_building_id": 7,
                "contact_phone": "13899049309",
                "store_id": 64,
                "order_id": "7524573043900030064003000",
                "delivery_building_name": "你妹",
                "delivery_status": 1,
                "delivery_start_time": 0,
                "delivery_building_address": "就斤斤计较",
                "delivery_assign_time": 1432218033896,
                "contact_name": "vv",
                "merchant_id": 26
	 * 
	 * 
	 * 
	 */
	
	public int merchant_id = -1;
	public int delivery_building_id = -1;
	public int delivery_status = -1;
	
	public String order_id = "";
	public String contact_name = "";
	public String contact_phone = "";
	public String delivery_building_name = "";
	public String delivery_building_address = "";
	public String user_address = "";
	
	public long delivery_finish_time = -1;//送餐到达时间
	public long delivery_start_time = -1;//开始送餐时间
	public long delivery_assign_time = -1;//预约送达时间
	public long store_id = -1;
	
	public DeliveryStaff delivery_staff ;//派送员
	
	
}
