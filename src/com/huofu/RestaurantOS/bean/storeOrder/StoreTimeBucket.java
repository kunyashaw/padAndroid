package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * 营业时间段类
 * @author wow
 *
 */
public class StoreTimeBucket extends BaseBean{

	public long start_time = -1;//营业开始时间,从0时0点0分开始
	public long end_time = -1;//营业结束时间（从0时0点0分开始）

	public String name = "";//营业时间段名称
	public int store_id = -1;//店铺id
	public int merchant_id = -1;//商户id

	public long in_biz_time = 0;

	public int time_bucket_id = -1;//营业时间段id
	public int delivery_supported = 0;//支持外送

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public int getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(int merchant_id) {
		this.merchant_id = merchant_id;
	}

	public long getIn_biz_time() {
		return in_biz_time;
	}

	public void setIn_biz_time(long in_biz_time) {
		this.in_biz_time = in_biz_time;
	}

	public int getTime_bucket_id() {
		return time_bucket_id;
	}

	public void setTime_bucket_id(int time_bucket_id) {
		this.time_bucket_id = time_bucket_id;
	}

	public int getDelivery_supported() {
		return delivery_supported;
	}

	public void setDelivery_supported(int delivery_supported) {
		this.delivery_supported = delivery_supported;
	}
}
