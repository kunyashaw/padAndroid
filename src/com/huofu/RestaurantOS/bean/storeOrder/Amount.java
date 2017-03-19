package com.huofu.RestaurantOS.bean.storeOrder;


/****
 * 
 * @author kunyashaw
 * {
    "delivering": 0,
    "wait_for_prepare": 5,
    "wait_for_delivery": 0,
    "preparing": 0
	}
 *
 */

/****
 *
 * 外送时各种外送状态对应的数量
 *
 */

public class Amount {
	
	public int wait_for_prepare = 0;//等待备餐
	public int preparing = 0;//正在备餐
	public int wait_for_delivery = 0;//等待配送
	public int delivering = 0;//正在配送
	
	public boolean whetherDiff(Amount amountLast)
	{
		boolean flag = false;
		
		if(amountLast.wait_for_prepare != wait_for_prepare)
		{
			return true;
		}
		
		if(amountLast.preparing != preparing)
		{
			return true;
		}
		
		if(amountLast.wait_for_delivery != wait_for_delivery)
		{
			return true;
		}
		
		if(amountLast.delivering != delivering)
		{
			return true;
		}
		
		return flag;
	}

}
