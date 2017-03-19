package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * author: Created by zzl on 15/10/26.
 */
public class StoreHeartBeatInfo extends BaseBean{

    public int store_id;
    public int merchant_id;
    public int has_idle_port;//是否有空闲出餐口 0是没有 1是有
    public int today_serial_number;//当日最大取餐流水号
    public int delivery_wait_for_prepare;//等待备餐的外送订单数量
    public int delivery_preparing;//正在备餐的外送订单数量
    public int delivery_prepare_finish;//备餐完成的外送订单数量
    public int delivering;//正在送餐中的外送订单数量
}
