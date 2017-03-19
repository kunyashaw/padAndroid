package com.huofu.RestaurantOS.api.initPad;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/25.
 */
public class ModifyStoreInfo extends BaseApi{

    @Override
    public String getApiAction() {
        return "/appcopystore/save_name";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        return super.responseObjectParse(response);
    }
}
