package com.huofu.RestaurantOS.api.delivery;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/31.
 */
public class SetDeliveryOrderDeliveried extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/delivery/order/finish";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {

        }
        return response;
    }
}
