package com.huofu.RestaurantOS.api.pushMeal;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;

/**
 * author: Created by zzl on 15/10/23.
 */
public class GetMealProductList extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/meal/product/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            com.alibaba.fastjson.JSONArray array = response.jsonObject.getJSONArray("store_products");
            CommonUtils.LogWuwei(tag, "array is " + array.toJSONString());
            List<StoreProduct> list_store_product_all = JSONObject.parseArray(array.toJSONString(),StoreProduct.class);
            response.parseData = list_store_product_all;
        }
        return response;
    }
}
