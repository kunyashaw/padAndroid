package com.huofu.RestaurantOS.api.version;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.AppVersionInfo;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/9/21.
 */
public class GetAppVersionInfo extends BaseApi{

    @Override
    public String getApiAction() {
        return "app/version/info";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            String str = response.jsonObject.toJSONString();
            CommonUtils.LogWuwei(LoginActivity.tag,"获取app对应版本信息如下："+str);
            AppVersionInfo info = (AppVersionInfo)com.alibaba.fastjson.JSONObject.parseObject(str,AppVersionInfo.class);
            response.parseData = info;
        }
        return response;
    }
}
