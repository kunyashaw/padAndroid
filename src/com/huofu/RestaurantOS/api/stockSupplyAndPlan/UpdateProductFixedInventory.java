package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

/**
 * author: Created by zzl on 15/8/13.
 */

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/****
 *当直接修改周期库存或者非周期库存时直接调用该接口
 * 产品日常库存盘点修改剩余
 */
public class UpdateProductFixedInventory extends BaseApi {

    public  String  getApiAction(){
        return "5wei/inventory/date_amount_update";
    }
    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<StoreProduct> ls = new ArrayList<StoreProduct>();
            JSONArray list =  response.jsonObject.getJSONArray("list");
            response.parseData = ls;
        }
        return response;
    }

}
