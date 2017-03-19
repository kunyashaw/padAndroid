package com.huofu.RestaurantOS.api.version;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.AppInfo;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/9/21.
 */
public class GetAppInfo extends BaseApi{

    @Override
    public String getApiAction() {
        return "app/info";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            String str = response.jsonObject.toJSONString();
            AppInfo appInfo = com.alibaba.fastjson.JSONObject.parseObject(str,AppInfo.class);
            response.parseData = appInfo;
            CommonUtils.LogWuwei(LoginActivity.tag,"获取app信息如下："+str);
        }
        return response;
    }
}
