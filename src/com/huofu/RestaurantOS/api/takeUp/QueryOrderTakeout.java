package com.huofu.RestaurantOS.api.takeUp;

import android.os.Message;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;
import com.huofu.RestaurantOS.bean.storeOrder.MealProduct;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.ui.pannel.takeUp.TakeUpActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * author: Created by zzl on 15/8/29.
 */
public class QueryOrderTakeout extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/order/query_by_take_code";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if (this.isResponseOk(response)) {
            //StoreOrder so = com.alibaba.fastjson.JSON.parseObject(response.jsonObject.toJSONString(), StoreOrder.class);

            //response.origialData

            try {
                JSONObject jobj = new JSONObject(response.origialData);

                String store_order = jobj.getString("store_order");
                JSONObject json_store_oder = new JSONObject(store_order);
                int pay_status = json_store_oder.getInt("pay_status");
                int refund_status = json_store_oder.getInt("refund_status");
                boolean order_locked = json_store_oder.getBoolean("order_locked");
                int trade_status = json_store_oder.getInt("trade_status");

                int take_serial_number = json_store_oder.getInt("take_serial_number");

                if (pay_status == 3 && refund_status == 1
                        && !order_locked
                        && trade_status != 5 && take_serial_number == 0)//如果已经支付且未退款且未锁定
                {
                    CommonUtils.LogWuwei(tag, "状态正常:如果已经支付且未退款且未锁定");
                    OrderDetailInfo odi = new OrderDetailInfo();

                    odi.list_charge_items_all = new ArrayList<ChargItem>();
                    odi.store_order = new StoreOrder();

                    odi.store_order.take_client_type = json_store_oder.getInt("take_client_type");

                    odi.store_order.client_type = json_store_oder.getInt("client_type");//下单类型
                    odi.store_order.payable_price = json_store_oder.getInt("payable_price");//应付金额
                    odi.store_order.order_price = json_store_oder.getInt("order_price");//实际支付金额
                    odi.store_order.actual_price = json_store_oder.getInt("actual_price");//实付

                    odi.store_order.create_time = json_store_oder.getLong("create_time");///下单时间
                    odi.store_order.update_time = json_store_oder.getLong("update_time");//取号时间
                    odi.store_order.take_serial_number = json_store_oder.getInt("take_serial_number");//流水号
                    odi.store_order.take_mode = json_store_oder.getInt("take_mode");
                    odi.store_order.time_bucket_id = json_store_oder.getInt("time_bucket_id");

                    odi.order_id = json_store_oder.getString("order_id");//订单编号
                    odi.take_serial_number = json_store_oder.getInt("take_serial_number");//订单流水号
                    odi.order_id = json_store_oder.getString("order_id");

                    odi.orderContent = "";
                    JSONArray json_order_items = json_store_oder.getJSONArray("order_items");
                    for (int p = 0; p < json_order_items.length(); p++) {
                        JSONObject json_order_item = json_order_items.getJSONObject(p);

                        if (json_order_item.getInt("amount") > 0) {
                            odi.orderContent += json_order_item.get("charge_item_name") + "×" +
                                    CommonUtils.DoubleDeal(json_order_item.getDouble("amount")) + "\n";
                        } else {
                            odi.orderContent += json_order_item.get("charge_item_name") + "×" +
                                    CommonUtils.DoubleDeal(json_order_item.getDouble("packed_amount")) + "\n";
                        }


                    }

                    for (int k = 0; k < json_order_items.length(); k++)//遍历收费项目列表，得到收费项目名称和数量
                    {
                        JSONObject meal_charge = json_order_items.getJSONObject(k);// 得到一个订单收费项目
                        ChargItem chargeItemObject = new ChargItem();
                        chargeItemObject.listSubChargeItem = new ArrayList<ChargeItemSub>();
                        chargeItemObject.charge_item_id = meal_charge.getLong("charge_item_id");//获取订单中一个收费项目
                        chargeItemObject.charge_item_amount = meal_charge.getInt("amount");//获取订单中某一个收费项目的份数

                        int packed_amount = meal_charge.getInt("packed_amount");
                        if (packed_amount > 0) {
                            chargeItemObject.packaged = 1;
                        } else {
                            chargeItemObject.packaged = 0;
                        }


                        chargeItemObject.charge_item_name = meal_charge.getString("charge_item_name");//收费项目名称
                        chargeItemObject.orderId = odi.order_id;


                        JSONArray subItems = meal_charge.getJSONArray("subitems");

                        for (int p = 0; p < subItems.length(); p++)//遍历收费子项目，得到每个收费子项目
                        {
                            JSONObject subItem = subItems.getJSONObject(p);
                            JSONObject product = subItem.getJSONObject("product");

                            ChargeItemSub subChangeItem = new ChargeItemSub();//创建收费子项目
                            subChangeItem.mp = new MealProduct();

                            subChangeItem.charge_item_id = subItem.getLong("charge_item_id");//收费项目id
                            subChangeItem.amount = subItem.getDouble("amount");
                            subChangeItem.product_id = subItem.getLong("product_id");

                            subChangeItem.mp.product_id = product.getLong("product_id");
                            subChangeItem.mp.unit = product.getString("unit");
                            subChangeItem.mp.product_name = product.getString("product_name");

                            chargeItemObject.listSubChargeItem.add(subChangeItem);
                        }


                        odi.list_charge_items_all.add(chargeItemObject);
                    }

                    Message msg = new Message();
                    msg.what = TakeUpActivity.SHOW_CHOOSE_PACKAGED_DIALOG;
                    msg.obj = odi;
                    TakeUpActivity.handler.sendMessage(msg);
                    //CommonUtils.sendMsg(odi, TakeUpActivity.SHOW_CHOOSE_PACKAGED_DIALOG, TakeUpActivity.handler);

                } else if (trade_status == 5 || take_serial_number > 0) {
                    Message msg = new Message();
                    msg.what = TakeUpActivity.SHOW_ERROR_MESSAGE;
                    msg.obj = "订单" + "已取号";
                    TakeUpActivity.handler.sendMessage(msg);
                    CommonUtils.sendMsg(null, TakeUpActivity.CLEAR_INPUT_CONTENT, TakeUpActivity.handler);
                    CommonUtils.LogWuwei(tag, "状态异常:订单已取号");
                } else if (order_locked) {
                    Message msg = new Message();
                    msg.what = TakeUpActivity.SHOW_ERROR_MESSAGE;
                    msg.obj = "订单已锁定";
                    TakeUpActivity.handler.sendMessage(msg);
                    CommonUtils.sendMsg(null, TakeUpActivity.CLEAR_INPUT_CONTENT, TakeUpActivity.handler);
                    CommonUtils.LogWuwei(tag, "状态异常:订单已锁定");
                } else if (pay_status != 3) {
                    Message msg = new Message();
                    msg.what = TakeUpActivity.SHOW_ERROR_MESSAGE;
                    msg.obj = "订单没有完成支付";
                    TakeUpActivity.handler.sendMessage(msg);
                    CommonUtils.sendMsg(null, TakeUpActivity.CLEAR_INPUT_CONTENT, TakeUpActivity.handler);
                    CommonUtils.LogWuwei(tag, "状态异常:订单没有完成支付");
                } else if (refund_status == 2) {
                    Message msg = new Message();
                    msg.what = TakeUpActivity.SHOW_ERROR_MESSAGE;
                    msg.obj = "用户已经全额退款";
                    TakeUpActivity.handler.sendMessage(msg);
                    CommonUtils.sendMsg(null, TakeUpActivity.CLEAR_INPUT_CONTENT, TakeUpActivity.handler);
                    CommonUtils.LogWuwei(tag, "状态异常:用户已经全额退款");
                }


                Message msg = new Message();
                msg.what = TakeUpActivity.HIDE_LOADING;
                TakeUpActivity.handler.sendMessage(msg);
            } catch (Exception e) {

            }
        }
        return response;
    }
}
