package com.huofu.RestaurantOS.api.pushMeal.mealPort;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/17.
 */
public class GetAllMealPorts extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/mealport/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            JSONArray array = response.jsonObject.getJSONArray("data_list");
            CommonUtils.LogWuwei(SettingsActivity.tag,"出餐口列表如下:"+CommonUtils.logInfoFormatTojson(array.toJSONString()));
            List<StoreMealPort> list = new ArrayList<StoreMealPort>();
            for(int k=0;k<array.size();k++)
            {
                StoreMealPort smp = JSON.parseObject(array.getJSONObject(k).toJSONString(),StoreMealPort.class);
                list.add(smp);
            }

            response.parseData = list;
        }
        return response;
    }
}
