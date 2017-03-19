package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/10/17.
 */
public class DetailMealPort extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport";
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
