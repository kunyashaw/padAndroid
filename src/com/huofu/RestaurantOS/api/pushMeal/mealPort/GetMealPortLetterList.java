package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/10/27.
 */
public class GetMealPortLetterList extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport/letter/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            CommonUtils.LogWuwei("",response.jsonObject.toJSONString());
        }
        return response;
    }
}
