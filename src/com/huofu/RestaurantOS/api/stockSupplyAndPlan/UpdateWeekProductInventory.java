package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/17.
 */
public class UpdateWeekProductInventory extends BaseApi{

    public  String  getApiAction(){
        return "5wei/inventory/week_update_by_product";
    }
    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<StoreProduct> ls = new ArrayList<StoreProduct>();
        }
        return response;
    }
}
