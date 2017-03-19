package com.huofu.RestaurantOS.bean.pushMeal;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.bean.BaseBean;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;

import java.util.ArrayList;

/**
 * Created by tim on 11/11/15.
 */
public class TicketBean extends BaseBean implements Cloneable{

    public Integer anaylysisType;//0 统计堂食 1不统计堂食 2统计打包 3不统计打包
    public Long create_time;

    public JSONArray meal_charges;
    public ArrayList<ChargItem> charge_items;

    public String order_id;
    public Boolean packaged;
    public Integer packaged_seq;

    public Integer port_count;
    public Long port_id;
    public String port_letter;
    public String repast_date;
    public Integer take_mode;
    public Integer take_serial_number;
    public Integer take_serial_seq;
    public Long time_bucket_id;

    public Boolean autoPrint;
    public Boolean only_one;
    public Boolean last_ticket;
    public Boolean isCheckout = false;

    @Override
    public Object clone() throws CloneNotSupportedException {
        TicketBean tb = (TicketBean)super.clone();
        tb.charge_items  = (ArrayList<ChargItem>) tb.charge_items.clone();
        return tb;
    }
}
