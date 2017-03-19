package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/14.
 */

/****
 *
 * 产品更新固定库存
 *
 */
public class UpdateProductInventory extends BaseApi {

    public  String  getApiAction(){
        return "5wei/inventory/update_of_product";
    }
    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<StoreProduct> ls = new ArrayList<StoreProduct>();
            if(response.jsonObject.toString().contains("list"))
            {
                JSONArray list =  response.jsonObject.getJSONArray("list");
                response.parseData = ls;
            }


        }
        return response;
    }
}
