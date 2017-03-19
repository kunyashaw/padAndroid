package com.huofu.RestaurantOS.bean;

/**
 * author: Created by zzl on 15/8/17.
 */
public class ProductWeekItem extends  BaseBean{

    public int week_day;//1＝周一、2＝周二。。。、3、4、5、6、7＝周日
    public long time_bucket_id;
    public double amount;
    public int next_week;//0=本周（默认），1=下周
}
