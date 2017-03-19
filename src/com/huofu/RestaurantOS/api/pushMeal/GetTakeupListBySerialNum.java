package com.huofu.RestaurantOS.api.pushMeal;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/11/27.
 */
public class GetTakeupListBySerialNum extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/port/takeup_by_number";
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
