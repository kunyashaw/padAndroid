package com.huofu.RestaurantOS.api.delivery;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/31.
 */
public class GetOrderDetailInfoByOrderId extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/order/query_by_order_id";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            response.parseData = response.jsonObject;
        }
        return response;
    }
}
