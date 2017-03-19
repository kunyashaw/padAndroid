package com.huofu.RestaurantOS.bean.storeOrder;

/****
 *
 * 一个产品的数据模型
 *
 */

public class product {
	
	public long serinalNum = 0;//流水号
	public String name = "";//菜品的名称
	public double amount = 0;//菜品数量
	public boolean packaged = false;//是否打包
	public String orderId = "";//属于哪个订单
	public long charge_item_id = 0;//属于哪个收费项目

}
