package com.huofu.RestaurantOS.api.pushMeal;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/10/27.
 */
public class SetPortMealCheckoutAuto extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/port/checkout_auto";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            CommonUtils.LogWuwei(PushMealActivity.tag,response.jsonObject.toJSONString());
            response.parseData = response.jsonObject;
        }
        return response;
    }
}
