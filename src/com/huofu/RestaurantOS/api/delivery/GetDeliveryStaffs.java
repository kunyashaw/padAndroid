package com.huofu.RestaurantOS.api.delivery;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.user.DeliveryStaff;

import java.util.List;

/**
 * author: Created by zzl on 15/8/31.
 */
public class GetDeliveryStaffs extends BaseApi {

    @Override
    public String getApiAction() {
        return "5wei/delivery/staffs";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<DeliveryStaff> list = JSONArray.parseArray(response.jsonObject.getJSONArray("data_list").toJSONString(),
                    DeliveryStaff.class);
            response.parseData = list;
        }
        return response;
    }
}
