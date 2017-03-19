package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreMealPort;

import java.util.List;

/**
 * author: Created by zzl on 15/10/23.
 */
public class RegistMealPortTaskRelation extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport/task_relation_regist";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            JSONArray array = response.jsonObject.getJSONArray("data_list");
            List<StoreMealPort> list = JSONArray.parseArray(array.toJSONString(), StoreMealPort.class);
            response.parseData = list;
        }
        return response;
    }
}
