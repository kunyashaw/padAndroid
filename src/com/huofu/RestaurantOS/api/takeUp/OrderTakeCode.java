package com.huofu.RestaurantOS.api.takeUp;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.ui.pannel.takeUp.TakeUpActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/8/29.
 */
public class OrderTakeCode extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/order/takecode";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            try
            {
                OrderDetailInfo odi = new OrderDetailInfo();
                String result = response.jsonObject.getJSONObject("store_order").toJSONString();

                StoreOrder so = JSON.parseObject(result, StoreOrder.class);
                odi.store_order = so;
                odi.take_serial_number = so.take_serial_number;
                odi.order_id = odi.store_order.order_id;
                odi.take_mode = odi.store_order.take_mode;
                odi.timeBucketName = odi.store_order.store_time_bucket.name;
                odi.list_charge_items_all = so.order_items;
                odi.packaged = 0;
                for(ChargItem ci:so.order_items)
                {
                    if(ci.packed_amount > 0)
                    {
                        odi.packaged = 1;
                        break;
                    }
                }

                Message msg = new Message();
                msg.what = TakeUpActivity.SHOW_LOADING_TEXT;
                msg.obj = "获取订单详情成功,准备打印小票";
                TakeUpActivity.handler.sendMessage(msg);

                CommonUtils.LogWuwei(tag, "获取订单详情成功,准备打印小票");


                msg = new Message();
                msg.what = TakeUpActivity.PRINT_RECEIPT;
                msg.obj = odi;
                TakeUpActivity.handlerThread.sendMessage(msg);

                response.parseData = odi;
            } catch (Exception e) {

            }
        }
        return response;
    }

}
