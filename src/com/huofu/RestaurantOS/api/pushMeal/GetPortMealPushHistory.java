package com.huofu.RestaurantOS.api.pushMeal;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.pushMeal.PushHistoryFood;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/10.
 */
public class GetPortMealPushHistory extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/meal/port/checkout/history";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if (this.isResponseOk(response)) {
            com.alibaba.fastjson.JSONArray objArrayTmp = response.jsonObject.getJSONArray("store_meals");
            com.alibaba.fastjson.JSONArray objArray = JSON.parseArray(
                    CommonUtils.converBooleanToInt(objArrayTmp.toString()).toString());
            List<PushHistoryFood> list_all_history = new ArrayList<PushHistoryFood>();
            list_all_history  = JSON.parseArray(objArray.toJSONString(),PushHistoryFood.class);
            for(PushHistoryFood phf:list_all_history)
            {
                if(phf.meal_charges != null)
                {
                    List<ChargItem> listCI = JSON.parseArray(phf.meal_charges.toJSONString(),ChargItem.class);
                    for(ChargItem ci:listCI)
                    {
                        phf.orderName+=ci.charge_item_name+"×"+CommonUtils.DoubleDeal(ci.charge_item_amount)+"\n";
                    }
                }
            }
            response.parseData = list_all_history;
        }
        return response;
    }
}
