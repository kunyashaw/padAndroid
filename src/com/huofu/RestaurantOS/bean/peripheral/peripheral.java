package com.huofu.RestaurantOS.bean.peripheral;

/****
 *
 * 设置时打印机外设
 *
 */
public class peripheral implements Cloneable{
	
	public int peripheral_id = 0;//设备id
	public String name = "";//名称
	public int store_id = 0;//店铺id
	public int merchant_id = 0;//商户id
	public int type = 0;//1、打印机 2、pos 3、叫号设备
	public String con_id = "";//连接标识
	public int status = 0;//1、正常 2、暂停使用 3、停止使用，不可恢复
	public int atype = 0;
	public peripheralIpos Ipos= null;//ipos结构
	public boolean printer_can_connect = false;//打印机是否可以连接

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		peripheral newPeripheral = (peripheral)super.clone();
		return newPeripheral;
	}
	
	
	
}
