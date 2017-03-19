package com.huofu.RestaurantOS.api.setting;

/**
 * author: Created by zzl on 15/8/28.
 */

import com.huofu.RestaurantOS.api.BaseApi;

/***
 *"设置店铺营业时间段支持外送 5wei/store/timebucket/delivery_support/save"
 */
public class SetStoreDeliveryTimeBucket extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/timebucket/delivery_support/save";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {

        }
        return response;
    }
}
