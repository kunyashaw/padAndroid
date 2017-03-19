package com.huofu.RestaurantOS.bean.storeOrder;


/***
 * 
 * 			user_account_amount": "0", 用户账户余额
            "yjpay_amount": "0", 一键支付
            "ipos_amount": "0", 智能pos
            "cash_amount": "0", 现金
            "coupon_amount": "0",优惠券
            "prepaid_card_amount": "2942", 预付费卡
            "wechat_amount": "0" 微信支付
 * @author kunyashaw
 *
 */

/****
 *
 * 支付信息
 *
 */

public class OrderPayInfo {
	
	public long user_account_amount = 0;//现金账户
	public long yjpay_amount = 0;//一键支付
	public long ipos_amount = 0;//pos支付
	public long cash_amount = 0;//现金支付
	public long coupon_amount = 0;//优惠券
	public long prepaid_card_amount = 0;//预付费卡
	public long wechat_amount = 0;//微信支付
	
	public long cash_received_amount = 0;//用户交给你的钱

}
