package com.huofu.RestaurantOS.api.request;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * Created by tim on 6/8/15.
 */
public interface ApiCallback {

    public void success(Object object);
    public void error(BaseApi.ApiResponse response);
}
