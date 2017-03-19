package com.huofu.RestaurantOS.api.delivery;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;
import com.huofu.RestaurantOS.bean.storeOrder.MealProduct;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.bean.storeOrder.storeOrderDelivery;
import com.huofu.RestaurantOS.bean.user.DeliveryStaff;
import com.huofu.RestaurantOS.bean.user.User;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/31.
 */
public class GetDeliveryOrderListInfo extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/delivery/order/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        List<OrderDetailInfo> list = new ArrayList<OrderDetailInfo>();
        if(this.isResponseOk(response))
        {
            try {
                org.json.JSONObject jobj = new org.json.JSONObject(""+response.origialData);
                org.json.JSONArray array = jobj.getJSONArray("list");
                DeliveryActivity.nowTotalPage = jobj.getInt("total_page");

                if (array == null) {
                    return response;
                }

                CommonUtils.LogWuwei(tag, "array size is " + array.length());

                for (int k = 0; k < array.length(); k++) {
                    org.json.JSONObject obj = array.getJSONObject(k);
                    OrderDetailInfo odi = new OrderDetailInfo();
                    odi.store_order = new StoreOrder();

                    odi.store_order.store_order_delivery = new storeOrderDelivery();
                    odi.list_charge_items_all = new ArrayList<ChargItem>();
                    odi.store_order.user = new User();

                    org.json.JSONObject objSOD = obj.getJSONObject("store_order_delivery");
                    org.json.JSONObject objUser = obj.getJSONObject("user");
                    org.json.JSONArray arrayCI = obj.getJSONArray("order_items");


                    odi.order_id = obj.getString("order_id");
                    odi.take_serial_number = obj.getInt("take_serial_number");//订单流水号
                    //odi.take_serial_seq =  obj.getInt("take_serial_seq");//订单流水号序号

                    odi.packaged = 1;

                    //odi.store_order.payable_price = obj.getInt("payable_price");//应付金额
                    odi.store_order.order_price = obj.getInt("order_price");//实际支付金额
                    odi.store_order.actual_price = obj.getInt("actual_price");//实付

                    odi.store_order.create_time = obj.getLong("create_time");//下单时间
                    odi.store_order.update_time = obj.getLong("update_time");//取号时间
                    odi.store_order.take_serial_number = obj.getInt("take_serial_number");//流水号
                    odi.store_order.take_mode = obj.getInt("take_mode");
                    odi.store_order.time_bucket_id = obj.getInt("time_bucket_id");

                    odi.store_order.store_order_delivery.merchant_id = objSOD.getInt("merchant_id");
                    odi.store_order.store_order_delivery.delivery_building_id = objSOD.getInt("delivery_building_id");
                    odi.store_order.store_order_delivery.delivery_status = objSOD.getInt("delivery_status");


                    odi.store_order.store_order_delivery.order_id = objSOD.getString("order_id");
                    odi.store_order.store_order_delivery.contact_name = objSOD.getString("contact_name");
                    odi.store_order.store_order_delivery.contact_phone = objSOD.getString("contact_phone");
                    odi.store_order.store_order_delivery.delivery_building_name = objSOD.getString("delivery_building_name");
                    odi.store_order.store_order_delivery.delivery_building_address = objSOD.getString("delivery_building_address");
                    odi.store_order.store_order_delivery.user_address = objSOD.getString("user_address");

                    odi.store_order.store_order_delivery.delivery_finish_time = objSOD.getLong("delivery_finish_time");
                    odi.store_order.store_order_delivery.delivery_start_time = objSOD.getLong("delivery_start_time");
                    odi.store_order.store_order_delivery.delivery_assign_time = objSOD.getLong("delivery_assign_time");
                    odi.store_order.store_order_delivery.store_id = objSOD.getLong("store_id");

                    if (objSOD.toString().contains("delivery_staff")) {
                        org.json.JSONObject objDeliveryStaff = objSOD.getJSONObject("delivery_staff");
                        odi.store_order.store_order_delivery.delivery_staff = new DeliveryStaff();
                        odi.store_order.store_order_delivery.delivery_staff.name = objDeliveryStaff.getString("name");
                        odi.store_order.store_order_delivery.delivery_staff.head = objDeliveryStaff.getString("head");
                        odi.store_order.store_order_delivery.delivery_staff.user_id = objDeliveryStaff.getInt("user_id");
                        odi.store_order.store_order_delivery.delivery_staff.merchant_id = objDeliveryStaff.getInt("merchant_id");

                    }

                    odi.store_order.user.name = objUser.getString("name");
                    odi.store_order.user.head = objUser.getString("head");
                    odi.store_order.user.mobile = objUser.getString("mobile");
                    odi.store_order.user.create_time = objUser.getLong("create_time");
                    odi.store_order.user.user_id = objUser.getLong("user_id");


                    if (arrayCI != null) {
                        for (int t = 0; t < arrayCI.length(); t++) {
                            org.json.JSONObject objCI = arrayCI.getJSONObject(t);
                            ChargItem chargeItemObject = new ChargItem();

                            chargeItemObject.listSubChargeItem = new ArrayList<ChargeItemSub>();
                            chargeItemObject.charge_item_id = objCI.getLong("charge_item_id");//获取订单中一个收费项目
                            chargeItemObject.charge_item_amount = objCI.getInt("amount");//获取订单中某一个收费项目的份数
                            chargeItemObject.packaged = 1;
                            chargeItemObject.charge_item_name = objCI.getString("charge_item_name");//收费项目名称
                            chargeItemObject.orderId = odi.order_id;

                            if (t == arrayCI.length() - 1) {
                                odi.orderContent += chargeItemObject.charge_item_name + "×" +
                                        CommonUtils.DoubleDeal(chargeItemObject.charge_item_amount) + "\t";
                            } else {
                                odi.orderContent += chargeItemObject.charge_item_name + "×" +
                                        CommonUtils.DoubleDeal(chargeItemObject.charge_item_amount) + ",\t";
                            }


                            org.json.JSONArray subItems = objCI.getJSONArray("subitems");

                            for (int p = 0; p < subItems.length(); p++)//遍历收费子项目，得到每个收费子项目
                            {
                                org.json.JSONObject subItem = subItems.getJSONObject(p);
                                org.json.JSONObject product = subItem.getJSONObject("product");

                                ChargeItemSub subChangeItem = new ChargeItemSub();//创建收费子项目
                                subChangeItem.mp = new MealProduct();

                                subChangeItem.charge_item_id = subItem.getLong("charge_item_id");//收费项目id
                                subChangeItem.amount = subItem.getDouble("amount");
                                subChangeItem.product_id = subItem.getLong("product_id");

                                subChangeItem.mp.product_id = product.getLong("product_id");
                                subChangeItem.mp.unit = product.getString("unit");
                                subChangeItem.mp.product_name = product.getString("product_name");
                            }

                            odi.list_charge_items_all.add(chargeItemObject);
                        }
                    }

                    list.add(odi);
                }
            }
            catch(Exception e)
            {

            }
            response.parseData = list;
        }
        return response;
    }
}
