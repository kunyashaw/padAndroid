package com.huofu.RestaurantOS.bean.storeOrder;


import com.huofu.RestaurantOS.bean.user.User;
import com.huofu.RestaurantOS.bean.user.staff;

import java.util.List;

/**
 * 订单信息（订单编号、下单时间、取餐时间、应收金额、实收金额、优惠金额、下单类型、流水号）
 *
 * @author wow
 */

public class StoreOrder {

    public String order_id = "";//订单id
    public int merchant_id = 0;//商户id
    public long store_id = 0;//店铺id
    public long staff_id = 0;//	服务员工ID（用户下单时，staff_id＝0）
    public long user_id;//bigint	下单用户（员工下单时，user_id＝0）
    public long time_bucket_id;//bigint	营业时间ID

    // 1、收银台 CASHIER(1)  2、微信 WECHAT(2) 3、电视 TV(3), 4、电脑网页 PCWEB(4), 5、手机网页 MOBILEWEB(5), 6、app APP(6);
    public int client_type;//tinyint	下单终端类型（需要定义客户端类型枚举类)


    public long enterprise_id;//bigint	协议企业ID
    public int enterprise_rebate;//tinyint	协议企业折扣
    public int enterprise_rebate_price;//int	协议企业折扣，订单中多少钱可以按协议企业打折，按订单详情中可以安协议企业打折的部分统计得到
    public int internet_rebate;//tinyint	网单折扣
    public int internet_rebate_price;//int	网单折扣限额，订单中多少钱可以按网单打折，按订单详情中可以安网单打折的部分统计得到
    public int total_rebate;//int	整单折扣比例，可能是活动或者优惠，由店铺单独设置
    public int total_derate;//int	整单减免金额，可能是活动或者优惠，由店铺单独设置
    public int order_currency_id;//tinyint	下单币种

    public int user_client_coupon;//int	下单用户终端优惠金额，一天只有一单可以优惠
    public int order_price;//int	原价，按照点餐项目在菜单中的标准定价计算的总金额（11+22=33）
    public int total_price;//int	总价，按照点餐项目单项打折后，没有任何整单减免与折扣的总价金额（10+20=30）
    public int payable_price;//int	应付，在total_price基础上整单减免与折扣的总价金额（30*09=27）
    public int discount_price;//优惠了多少钱 order_price-payable_price

    public int actual_currency_id;//tinyint	实际支付币种
    public int actual_price;//int	实付，实际支付币种对应的金额
    public int pay_status;//tinyint	订单的支付状态：1=待支付；2=支付中；3=支付完成
    public int refund_status;//tinyint	1＝未退款，2＝用户全额退款：用户在支付完成之后，尚未发生实质交易时，发生的退款记录；3=商户全额退款：可能已发生交易，但由于异常情况需要做退款
    public boolean order_locked;//tinyint	是否订单锁定：0=未锁定、1=已锁定；例如，大额订单退款锁定
    public int trade_status;//tinyint	交易状态是对基础交易订单的交易状态进行扩展：1＝尚未交易；2＝已备货【订单已收到，并备货，用户不可以随意退款，一般用于大额订单】；3＝已开始加工【外送接单进入加工环节，不可自主退款】；4＝已送出【专用于外卖，表示餐已送出】；5＝已取号【堂食、外带时，用户已经取了小票】；6＝交易完成【已取餐，或者已经送达】
    public int invoice_status;//tinyint	开发票的状态：1＝不开发票；2＝有发票需求，尚未开发票；3＝已开发票
    public String invoice_demand;//varchar	发票需求：如果订单有发票需求，需要设置发票需求信息，发票需求信息的数据格式参考“订单发票需求”
    public int take_serial_number;//int	取餐流水号
    public int take_serial_seq; //int 取餐流水号序号
    //public char take_code	;//varchar	取餐验证码
    public int take_mode;//tinyint	取餐模式，订单的取餐模式：1＝堂食；2＝外带；3＝堂食+外带；4＝外送
    public int takeup_status;//tinyint	库存保留状态，库存保留是在下单到支付期间的一段时间内，是否给用户保留库存：1＝无保留；2＝保留；3＝已扣减【支付成功，要正式扣减库存】
    public long repast_date;//bigint	就餐日期，即菜单所在日期
    public long update_time;//bigint	更新时间
    public long create_time;//取号时间
    public long take_serial_time;
    public int take_client_type;//取餐类型（区分远网）
    public long meal_checkout_time = -1;//出餐时间

	/*public String update_time_str;
	public String create_time_str;*/

    public storeOrderDelivery store_order_delivery;
    public User user;
    public staff delivery_staff;
    public OrderPayInfo order_pay_info;
    public int delivery_fee = 0;//外送费
    public List<ChargItem> order_items;

    public List<StoreMealCheckout> getStore_meal_checkout() {
        return store_meal_checkout;
    }

    public void setStore_meal_checkout(List<StoreMealCheckout> store_meal_checkout) {
        this.store_meal_checkout = store_meal_checkout;
    }

    public List<StoreMealCheckout> store_meal_checkout;


    public StoreTimeBucket store_time_bucket;

    public StoreTimeBucket getstore_time_bucket() {
        return store_time_bucket;
    }

    public void setstore_time_bucket(StoreTimeBucket store_time_bucket) {
        this.store_time_bucket = store_time_bucket;
    }
}
