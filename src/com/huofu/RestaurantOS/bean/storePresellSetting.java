package com.huofu.RestaurantOS.bean;

import java.util.Map;

/**
 * Created by zzl on 15/8/8.
 */
public class storePresellSetting extends  BaseBean{

    int store_id;
    int merchant_id;
    boolean enabled;//是否启用预售：false=不开启，true=开启
    int pre_mode;//预售开始方式：0=未开启，1=按天开启，2=按周开启
    int pre_days;//提前N天开启，pre_mode=1，生效，提前（n＝x天）可预定
    long pre_week_day;//pre_mode=2，生效，每周几（1，2，3.。7）可预定下周
    long update_time;
    long create_time;

    Map<Integer,Integer> days_map ;

//    @Override
//    public storePresellSetting loadBeanWithMap(Map map) {
//            this.store_id = (Integer) map.get("store_id");
//            this.merchant_id= (Integer)map.get("merchant_id");
//            this.enabled = (Boolean)map.get("enabled");
//            this.pre_mode = (Integer)map.get("pre_mode");
//            this.pre_days = (Integer)map.get("pre_days");
//            this.pre_week_day= (Long)map.get("pre_week_dat");
//            this.update_time= (Long)map.get("update_time");
//            this.create_time = (Long)map.get("create_time2");
//            return this;
//    }
}
