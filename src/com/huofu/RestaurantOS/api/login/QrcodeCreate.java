package com.huofu.RestaurantOS.api.login;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * Created by tim on 6/8/15.
 */
public class QrcodeCreate extends BaseApi{
    public  String  getApiAction(){
        return "staff/login/qrcode/create";
    };

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if(this.isResponseOk(response))
        {}

        return super.responseObjectParse(response);
    }
}
