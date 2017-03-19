package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * 营业时间段类
 * @author wow
 *
 */
public class MealBucket extends BaseBean{

	public long start_time = -1;//营业开始时间,从0时0点0分开始
	public long end_time = -1;//营业结束时间（从0时0点0分开始）

	public String name = "";//营业时间段名称
	public int store_id = -1;//店铺id
	public int merchant_id = -1;//商户id

	public long in_biz_time = 0;

	public int time_bucket_id = -1;//营业时间段id
	public int delivery_supported = 0;//支持外送
}
