package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreInventoryDate;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/11.
 */
public class inventoryDateQuery extends  BaseApi{
    public  String  getApiAction(){
        return "5wei/inventory/date_query";
    }

    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        try
        {
            if (this.isResponseOk(response))
            {
                List<StoreInventoryDate>  ls = new ArrayList<StoreInventoryDate>();
                JSONArray list =  response.jsonObject.getJSONArray("store_inventory_dates");
                for(int k=0;k<list.size();k++)
                {
                    StoreInventoryDate sid = list.getObject(k, StoreInventoryDate.class);
                    ls.add(sid);
                }
                response.parseData = ls;
            }
        }
        catch (Exception e) {
            StackTraceElement[] listException = e.getStackTrace();
            for (int k = 0; k < listException.length; k++) {
                CommonUtils.LogWuwei(StockSupplyActivity.tag, listException[k].getFileName() + "-方法:"
                        + listException[k].getMethodName() + "-行号:" + listException[k].getLineNumber());
            }
        }

        return response;
    }
}
