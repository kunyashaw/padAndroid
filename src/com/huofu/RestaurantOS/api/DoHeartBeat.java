package com.huofu.RestaurantOS.api;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.bean.storeOrder.StoreHeartBeatInfo;

/**
 * author: Created by zzl on 15/10/26.
 */
public class DoHeartBeat extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/heartbeat";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            JSONObject obj = response.jsonObject.getJSONObject("store_5wei_heartbeat_info");
            StoreHeartBeatInfo shbi = com.alibaba.fastjson.JSONObject.parseObject(
                    obj.toJSONString(),StoreHeartBeatInfo.class);
            response.parseData = shbi;
        }
        return response;
    }
}
