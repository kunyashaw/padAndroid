package com.huofu.RestaurantOS.bean.storeOrder;


import com.huofu.RestaurantOS.bean.AnalyticNumber;

/**
 * 在获取统计信息时，产品信息的类
 * @author wow
 *
 */
public class StoreProduct {
	
	public Integer amount= 0;
	public Integer week_amount= 0;
	public Integer inv_type = 1;
	public String unit= "";
	public Long update_time = 0L;
	public Long product_id = 0L;
	public String name =  "";
	public Long create_time = 0L;
	public Integer store_id =  0;
	public Boolean inv_enabled = true;
	public Integer merchant_id = 0;
	public Integer meal_stat = 0;//0不统计  1统计
	public AnalyticNumber analyticNumber=new AnalyticNumber();//统计产品的个数

}
