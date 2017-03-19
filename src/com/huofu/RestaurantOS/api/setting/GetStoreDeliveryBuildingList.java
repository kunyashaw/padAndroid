package com.huofu.RestaurantOS.api.setting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.DeliveryBuilding;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/28.
 */
public class GetStoreDeliveryBuildingList extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/delivery/building/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            List<DeliveryBuilding> list = new ArrayList<DeliveryBuilding>();
            JSONArray array  = (JSONArray)(response.jsonObject.getJSONArray("store_delivery_buildings"));
            list = JSON.parseArray(array.toJSONString(), DeliveryBuilding.class);
            response.parseData = list;
        }
        return response;
    }
}
