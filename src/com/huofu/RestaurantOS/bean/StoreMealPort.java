package com.huofu.RestaurantOS.bean;

/**
 * author: Created by zzl on 15/10/17.
 */
public class StoreMealPort extends BaseBean{

    public Integer port_id;//出餐口id
    public Integer merchant_id;//商户id
    public Long store_id;//店铺id
    public String name;//出餐口名称
    public String letter;//字母标识
    public Long printer_peripheral_id;//打印机外接设备id
    public Long call_peripheral_id;//叫号电视外接设备id
    public Integer call_type;//叫号规则 1自动叫号 2手动叫号 3尾单叫号
    public Integer checkout_type;//出餐模式:手工出餐(0) 还是自动出餐(1)
    public Integer has_pack;//是否是打包台
    public Long update_time;//更新时间
    public Long create_time;//创建时间
    public Boolean flagCanUse = false;//该出餐口对应的打印机是否可用
}
