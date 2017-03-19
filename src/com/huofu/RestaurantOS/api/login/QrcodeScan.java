package com.huofu.RestaurantOS.api.login;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * Created by tim on 6/8/15.
 */
public class QrcodeScan extends BaseApi{
    public  String  getApiAction(){
        return "staff/login/qrcode/scan";
    };

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        return super.responseObjectParse(response);
    }
}
