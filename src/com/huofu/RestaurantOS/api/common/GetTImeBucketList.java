package com.huofu.RestaurantOS.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/11.
 */

/****
 * 获取营业时间段列表
 */
public class GetTImeBucketList extends BaseApi {

    public  String  getApiAction(){
        return "5wei/store/timebucket/list";
    }
    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response)) {
            JSONArray list =  response.jsonObject.getJSONArray("list");
            List<MealBucket>  ls = new ArrayList<MealBucket>();
            /*for(int k=0;k<list.size();k++) {
                ls.add(list.getObject(k, MealBucket.class));
            }*/
            ls = JSONObject.parseArray(list.toJSONString(),MealBucket.class);
            response.parseData = ls;
        }
        return response;
    }
}
