package com.huofu.RestaurantOS.api;

/**
 * author: Created by zzl on 15/9/2.
 */
public class SaveTvMenuToClound extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/tvmenu/save";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {

        }
        return response;
    }
}
