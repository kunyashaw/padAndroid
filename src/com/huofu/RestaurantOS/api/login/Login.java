package com.huofu.RestaurantOS.api.login;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.user.LoginStaff;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/24.
 */
public class Login extends BaseApi{

    @Override
    public String getApiAction() {
        return "staff/login";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            String staffAccToken = response.jsonObject.getString("staff_access_token");
            LocalDataDeal.writeToLocalStaffAccessToken(staffAccToken,MainApplication.getContext());
            LoginStaff loginStaff = response.jsonObject.getObject("staff", LoginStaff.class);
            JSONArray arrayCodes = response.jsonObject.getJSONArray("codes");
            JSONArray arrayPermission = response.jsonObject.getJSONArray("permission_ids");
            List<String>  listCodes = new ArrayList<String>();
            List<String>  listPermission = new ArrayList<String>();

            for(int k=0;k<arrayCodes.size();k++)
            {
                listCodes.add(arrayCodes.get(k).toString());
            }

            for(int k=0;k<arrayPermission.size();k++)
            {
                listPermission.add(arrayPermission.get(k).toString());
            }
            LocalDataDeal.writeToLocalNowLoginUserInfo(loginStaff.head,loginStaff.name,listPermission,listCodes, MainApplication.getContext());
       }
        return response;
    }
}
