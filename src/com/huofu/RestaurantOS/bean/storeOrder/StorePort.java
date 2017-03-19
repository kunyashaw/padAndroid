package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * author: Created by zzl on 15/10/28.
 */
public class StorePort extends BaseBean{

    public call_peripheral callPeripheral;
    public printer_peripheral printerPeripheral;
    public int call_type;
    public long call_peripheral_id;
    public int checkout_type;
    public int has_pack;
    public String letter;
    public long merchant_id;
    public String name;
    public long port_id;
    public long printer_peripheral_id;
    public long store_id;
}
