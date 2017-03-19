package com.huofu.RestaurantOS.api.activate;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/21.
 */
public class GetPublicKey extends BaseApi{

    @Override
    public String getApiAction() {
        return "pubkey";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        return super.responseObjectParse(response);
    }
}
