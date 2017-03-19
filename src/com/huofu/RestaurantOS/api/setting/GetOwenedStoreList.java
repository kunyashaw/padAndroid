package com.huofu.RestaurantOS.api.setting;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/28.
 */
public class GetOwenedStoreList extends BaseApi{

    @Override
    public String getApiAction() {
        return "store/list_for_permission";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {

        }
        return response;
    }
}
