package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/20.
 */
public class QueryFixedInventory extends BaseApi {

    public  String  getApiAction(){
        return "5wei/inventory/product";
    }

    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response))
        {
            JSONObject oj = response.jsonObject.getJSONObject("store_product");
            response.parseData = oj;
        }
        return response;
    }
}
