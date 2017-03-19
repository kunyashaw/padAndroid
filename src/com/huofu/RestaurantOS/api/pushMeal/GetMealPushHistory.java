package com.huofu.RestaurantOS.api.pushMeal;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.pushMeal.PushHistoryFood;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author: Created by zzl on 15/10/10.
 */
public class GetMealPushHistory extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/meal/checkout/history";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if (this.isResponseOk(response)) {
            com.alibaba.fastjson.JSONArray objArrayTmp = response.jsonObject.getJSONArray("store_meals");
            com.alibaba.fastjson.JSONArray objArray = JSON.parseArray(
                    CommonUtils.converBooleanToInt(objArrayTmp.toString()).toString());
            List<PushHistoryFood> list_all_history = new ArrayList<PushHistoryFood>();
            list_all_history  = JSON.parseArray(objArray.toJSONString(),PushHistoryFood.class);
            Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();

            for(PushHistoryFood phf:list_all_history)
            {
                if(map.containsKey(phf.take_serial_number))
                {
                    Set<Integer> set = map.get(phf.take_serial_number);
                    set.add(phf.take_serial_seq);
                    map.put(phf.take_serial_number,set);
                }
                else
                {
                    Set<Integer> set = new HashSet<Integer>();
                    set.add(phf.take_serial_seq);
                    map.put(phf.take_serial_number,set);
                }
                if(phf.meal_charges != null)
                {
                    List<ChargItem> listCI = JSON.parseArray(phf.meal_charges.toJSONString(),ChargItem.class);
                    for(ChargItem ci:listCI)
                    {
                        phf.orderName+=ci.charge_item_name+"×"+CommonUtils.DoubleDeal(ci.charge_item_amount)+"\n";
                    }
                }
            }

            for(PushHistoryFood phf:list_all_history)
            {
                if(map.containsKey(phf.take_serial_number))
                {
                    Set<Integer> seqs = map.get(phf.take_serial_number);
                    if(seqs.size() ==1)
                    {
                        phf.flagOnly = true;
                    }
                }
            }
            response.parseData = list_all_history;
        }
        return response;
    }
}
