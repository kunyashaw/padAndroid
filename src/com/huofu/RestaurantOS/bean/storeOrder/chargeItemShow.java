package com.huofu.RestaurantOS.bean.storeOrder;

import java.util.List;

/*****
 *
 * 显示在待出餐列表的收费项目
 * 0->不统计打包(放在一起)  1->统计打包  2->统计堂食 3->不统计堂食(放在一起)
 *
 */

public class chargeItemShow implements Comparable<chargeItemShow>{
	
	public String orderId = "";
	public int take_serinal_num = -1;
	public int type = -1;//0->不统计打包(放在一起)  1->统计打包  2->统计堂食 3->不统计堂食(放在一起)
	public List<ChargItem> listChargeItemEatin = null;
	public List<ChargItem> listChargeItemPackaged = null;
	public ChargItem charge_item = null;
	public boolean packaged = false;
	public boolean flagEnabled = true;//设置cis是否可以被点击
	public String takeOrderTime = "";//取号时间
	
	public Integer getTakeSerinalNum()
	{
		return take_serinal_num;
	}
	
	public void setTakeSerinalNum(int take_serinal_num)
	{
		this.take_serinal_num = take_serinal_num;
	}
	
	
	@Override
	public int compareTo(chargeItemShow another) {
		// TODO Auto-generated method stub
		return this.getTakeSerinalNum().compareTo(another.getTakeSerinalNum());
	}
	
	
	
}
