package com.huofu.RestaurantOS.api.pushMeal;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/12/3.
 */
public class SetOrderAllCheckout extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/port/checkout/order";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            response = SetPortMealCheckout.dealResult(response,1);
        }
        return response;
    }
}
