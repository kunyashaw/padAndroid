package com.huofu.RestaurantOS.api.delivery;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.Amount;

/**
 * author: Created by zzl on 15/8/31.
 */
public class GetDeliveryOrderAnalysisInfo extends BaseApi
{
    @Override
    public String getApiAction() {
        return "5wei/order/delivery_info";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            Amount amount = com.alibaba.fastjson.JSONObject.parseObject(
                    response.jsonObject.toJSONString(),Amount.class);
            //CommonUtils.LogWuwei(tag, response.jsonObject.toJSONString());
            response.parseData = amount;
        }
        return response;
    }
}

