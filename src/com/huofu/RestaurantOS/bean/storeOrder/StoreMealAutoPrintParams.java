package com.huofu.RestaurantOS.bean.storeOrder;

import com.huofu.RestaurantOS.bean.BaseBean;

/**
 * author: Created by zzl on 15/10/27.
 */
public class StoreMealAutoPrintParams extends BaseBean{

    public String repast_date;
    public int take_serial_number;
    public int take_serial_seq;
    public long port_id;
    public int printer_status;//0未连接 1正常连接 2无法打印
    public int printed;//0未打印 1已打印
}
