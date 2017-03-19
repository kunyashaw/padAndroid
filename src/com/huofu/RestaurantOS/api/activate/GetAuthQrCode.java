package com.huofu.RestaurantOS.api.activate;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/8/24.
 */

/***
 * 获取授权二维码
 */
public class GetAuthQrCode extends BaseApi{

    @Override
    public String getApiAction() {
        return "license/qrcode/create";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        return super.responseObjectParse(response);
    }
}
