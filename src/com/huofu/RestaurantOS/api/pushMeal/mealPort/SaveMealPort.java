package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/10/17.
 */
public class SaveMealPort extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport/save";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            response.parseData = response.jsonObject;
            CommonUtils.LogWuwei(tag,"保存成功:\n"+CommonUtils.logInfoFormatTojson(response.jsonObject.toJSONString()));
        }
        return response;
    }
}
