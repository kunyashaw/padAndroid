package com.huofu.RestaurantOS.api.pushMeal;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/12/3.
 * 设置出餐的统计产品
 */
public class SetMealProductList extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/product/setup";
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
