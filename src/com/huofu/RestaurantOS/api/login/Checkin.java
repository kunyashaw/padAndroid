package com.huofu.RestaurantOS.api.login;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * Created by tim on 6/8/15.
 */
public class Checkin extends BaseApi {
    public  String  getApiAction(){
        return "appcopy/checkin";
    }
    @Override
    public ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            response.parseData = response.jsonObject.getString("work_key");
        }
        return response;
    }



}
