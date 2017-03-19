package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzl on 15/8/8.
 */

/****
 * 获取产品列表
 */
public class GetProductList extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/store/product/list";
    }

    @Override
    public ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<StoreProduct>  ls = new ArrayList<StoreProduct>();
            JSONArray list =  response.jsonObject.getJSONArray("list");
            for(int k=0;k<list.size();k++) {
                ls.add(list.getObject(k, StoreProduct.class));
            }
            response.parseData = ls;
        }
        return response;
    }

}
