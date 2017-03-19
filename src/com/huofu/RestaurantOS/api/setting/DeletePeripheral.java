package com.huofu.RestaurantOS.api.setting;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/27.
 */
public class DeletePeripheral extends BaseApi{

    @Override
    public String getApiAction() {
        return "peripheral/delete";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        return super.responseObjectParse(response);
    }
}
