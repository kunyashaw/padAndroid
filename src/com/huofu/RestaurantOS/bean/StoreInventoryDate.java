package com.huofu.RestaurantOS.bean;

/**
 * author: Created by zzl on 15/8/11.
 */
public class StoreInventoryDate extends  BaseBean{

    public long inv_date_id;//指定日期库存ID	long	√
    public int merchant_id;//商户ID	int	√
    public long store_id;//店铺ID	long	√
    public long time_bucket_id;//营业时段ID	long	√

    public String select_date;//指定日期	long	√	指定日期开始时间
    public long inv_week_id;//周期库存ID	long	√
    public int modified;//指定日期库存联动	int	√0=未知，1=周期联动，2=手动库存

    public long product_id	;//产品ID	long	√
    public double amount;//周期库存量	double	√
    public long update_time;//更新时间	long	√
    public long create_time;//创建时间	long	√

    public double in_sell = 0;//
    public double amount_remain = 0;
    public double amount_take = 0;//待出餐
    public double amount_order = 0;//本时段预订
    public double amount_checkout = 0;//已出餐
    public double amount_order_total = 0;//总预订
    public StoreProduct store_product = new StoreProduct();
    public double amount_plan = 0;//
    public double amount_takeup = 0;//待出餐个数个数

}
