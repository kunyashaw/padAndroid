package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * author: Created by zzl on 15/10/23.
 */
public class MealPortRelation extends BaseBean{

    public long port_id;//出餐口id
    public int task_status;//任务关系状态：0=解除任务关系，1=建立任务关系
    public int printer_status;//打印机连接状态：0=未连接，1=正常连接，2=无法打
    public int checkout_type;//出餐方式：0=手动，1=自动

}
