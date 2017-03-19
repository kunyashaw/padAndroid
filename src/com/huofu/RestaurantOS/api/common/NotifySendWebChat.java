package com.huofu.RestaurantOS.api.common;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/9/2.
 */
public class NotifySendWebChat extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/meal/notify";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {

        }
        return response;
    }
}
