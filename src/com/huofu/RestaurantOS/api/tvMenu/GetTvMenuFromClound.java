package com.huofu.RestaurantOS.api.tvMenu;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/11/20.
 */
public class GetTvMenuFromClound extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/store/tvmenu";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            response.parseData = response.jsonObject;
        }
        return  response;
    }
}
