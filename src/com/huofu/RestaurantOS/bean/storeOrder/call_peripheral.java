package com.huofu.RestaurantOS.bean.storeOrder;

/**
 * author: Created by zzl on 15/10/27.
 */
public class call_peripheral {

    public String con_id = "";//连接标识
    public int merchant_id = 0;//商户id
    public String name = "";//名称
    public int peripheral_id = 0;//设备id
    public int status = 0;//1、正常 2、暂停使用 3、停止使用，不可恢复
    public int store_id = 0;//店铺id
    public int type = 0;//1、打印机 2、pos


    public int atype = 0;
    public boolean printer_can_connect = false;//打印机是否可以连接

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        call_peripheral callPeripheral = (call_peripheral)super.clone();
        return callPeripheral;
    }

}
