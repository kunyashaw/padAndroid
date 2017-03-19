package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreMealPort;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/27.
 * 查询空闲出餐口
 */
public class ChecktIdlePorts extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport/idle_ports";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
           JSONArray array = response.jsonObject.getJSONArray("data_list");
            List<StoreMealPort> list = new ArrayList<StoreMealPort>();
            for(int k=0;k<array.size();k++)
            {
                StoreMealPort smp = JSONObject.parseObject(array.getJSONObject(k).toJSONString(), StoreMealPort.class);
                list.add(smp);
            }
            response.parseData = list;

        }
        return response;
    }
}
