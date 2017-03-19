package com.huofu.RestaurantOS.api.common;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/11/20.
 */
public class SetOrderRefund extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/order/refund";
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
