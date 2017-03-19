package com.huofu.RestaurantOS.api.pushMeal;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/10/26.
 */
public class GetPortTakeUpList extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/port/takeup";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            try
            {
                com.alibaba.fastjson.JSONArray array  = response.jsonObject.getJSONArray("store_meals");
                CommonUtils.LogWuwei(tag, "array is " + array.toJSONString());
                response.parseData = array;
            }
            catch(Exception e)
            {
                CommonUtils.LogWuwei(PushMealActivity.tag,"获取已取号未出餐错误"+e.getMessage());
            }
        }
        return response;
    }
}
