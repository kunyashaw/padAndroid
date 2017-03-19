package com.huofu.RestaurantOS.bean.pushMeal;

import com.huofu.RestaurantOS.bean.BaseBean;

import java.util.ArrayList;

/**
 * Created by tim on 11/11/15.
 */
public class TicketBeanGroup extends BaseBean implements Cloneable{

    public String order_id;
    public Integer take_serial_number;
    public Integer packaged;
    public int take_mode;
    public ArrayList<TicketBean> tickets;
    public boolean flagEnabled = true;


    @Override
    public Object clone() throws CloneNotSupportedException {
        TicketBeanGroup tbg = (TicketBeanGroup)super.clone();
        tbg.tickets = (ArrayList<TicketBean>) tickets.clone();
        return tbg;
    }
}
