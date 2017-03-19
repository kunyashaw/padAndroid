package com.huofu.RestaurantOS.Enum;

/**
 * author: Created by zzl on 15/10/14.
 */
public enum TemplateTypeEnum {
    PRINTROW_TYPE_HEAD(1),//小票头部 //#TIMEBUCKET# #STORENAME#
    PRINTROW_TYPE_TAKE_NUMBER(2),//小票流水号 #TICKETNUMBER#
    PRINTROW_TYPE_TAKE_TIME(3),//取号时间 #TAKETIME#
    PRINTROW_TYPE_ORDER_ID(4),//订单id #ORDERID#
    PRINTROW_TYPE_ORDER_TIME(5),//下单时间 #CREATETIME#
    PRINTROW_TYPE_ORDER_BODY(6),//小票内容，比如说肉夹馍×1 #PRODUCTNAME# #PRODUCTNUM#
    PRINTROW_TYPE_ORDER_PRICE(7),//应收价格 #PRICE#
    PRINTROW_TYPE_ORDER_PAYMENT(8),//实收价格 #PAYMENT# #ODDCHARGE#
    PRINTROW_TYPE_FOOTER(9),//任意字符串
    PRINTROW_TYPE_ORDER_ODDCHARGE(10),//找零或者优惠
    PRINTROW_TYPE_ORDER_WAITING_TIME(11),//等待时间PRINTER #WAITTIME#
    PRINTROW_TYPE_ORDER_DELIVERY_CONTACT_INFO(12),//外送信息 #CONTACT_NAME# #CONTACT_PHONE_NUMBER# #CONTACT_ADDRESS# #CONTACT_REACH_TIME# #CONTACT_INVOICE#

    PRINTROW_TYPE_CLIENT_NAME(100);//设备副本名称

    private int vaule;
    public int num;

    private TemplateTypeEnum(int vaule)
    {
        this.vaule = vaule;
        this.num = vaule;
    }

    public int getVaule() {
        return vaule;
    }

}
