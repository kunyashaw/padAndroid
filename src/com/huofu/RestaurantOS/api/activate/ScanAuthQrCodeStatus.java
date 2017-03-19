package com.huofu.RestaurantOS.api.activate;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/8/24.
 */

/***
 *
 * 检查授权二维码扫码状态
 *
 */
public class ScanAuthQrCodeStatus extends BaseApi{

    @Override
    public String getApiAction() {
        return "license/qrcode/scan";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            CommonUtils.LogWuwei(tag,"ScanAuthQrCodeStatus result is "+response.jsonObject.toString());

        }
        return super.responseObjectParse(response);
    }
}
