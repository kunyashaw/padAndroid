package com.huofu.RestaurantOS.bean;

import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;

/**
 * Created by wow on 15/8/8.
 */
public class StoreProduct extends  BaseBean{

    public int product_id;
    public int merchant_id;	//商户id
    public int store_id;//店铺id
    public String name;//名称
    public String unit;//单位
    public int inv_enabled	;//	0:不开启库存 1:开启库存
    public int inv_type	;//库存类型 1:计划库存 2:固定库存
    public double week_amount	;//产品默认周期库存
    public int meal_stat;//0:不需要统计 1:需要统计
    public double amount;//固定库存剩余
    public long create_time	;//创建时间
    public long update_time	;//最后更新时间
    public String[] remarks	;//产品备注数组,数组内容是string

    public MealBucket store_time_bucket;
    public long inv_week_id;//周期库存ID
    public long time_bucket_id;
    public int week_day;//周一、周二。。。。
    public long end_time;//生效结束时间
    public long begin_time;//生效开始时间

    public boolean thisWeek;//true 为本周 false为下周

}
