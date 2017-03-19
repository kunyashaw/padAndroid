package com.huofu.RestaurantOS.api;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.AESUtils;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.NetWorkUtils;
import com.huofu.RestaurantOS.utils.RSAUtils;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.net.JSONInterpret;
import com.huofu.RestaurantOS.utils.net.NetworkException;
import com.huofu.RestaurantOS.utils.net.NetworkManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tim on 6/8/15.
 */

public abstract class BaseApi {
    /**
     * api return class
     */
    public boolean flagPrintNetWorkLog = true;

    public static class ApiResponse {

        public String data;
        public int error_code;
        public String error_message;
        public JSONObject jsonObject;
        public Object parseData;
        public String origialData;
    }

    public enum EncodeKeyType {
        KEYTYPE_PUBLICKEY, KEYTYPE_MAIN, KEYTYPE_WORK
    }


    //public static  String API_DOMAIN = "http://192.168.1.231:8080/api/cashier/";
//    public static String API_DOMAIN = "http://test.api.huofu.com/api/cashier/";
    //public static String API_DOMAIN  = "http://beta.api.huofu.com/api/cashier/";
    public static  String API_DOMAIN = "http://api.huofu.com/api/cashier/";
    //public static String API_DOMAIN = "http://54.223.143.247:8100/api/cashier/";

    public static final int PRINTER_ERROR = -1;//记得把此值改为-1
    public static final boolean CONNECT_FAILED = false;//记得把此值改为false
    public static final boolean flagActivateService = true;//记得改为true
    public static final boolean flagMealDoneTimeTaskRun = true;//记得改为true
    public static final boolean flagdebugSwitchAPI = true;//记得改为false


    public static final String ENC_DATA_KEY = "data";
    public static final String tag = "BaseApi";

    public abstract String getApiAction();


    /**
     * 设置key类型
     *
     * @param type
     */
    public void setEncKeyType(EncodeKeyType type) {
        this.encodeKeyType = type;
    }

    /**
     * 重写解析
     *
     * @param response
     * @return
     */
    protected ApiResponse responseObjectParse(ApiResponse response) {
        response.parseData = response.jsonObject;
        return response;
    }


    /**
     * 是否要对参数及返回值进行加密
     *
     * @param enable
     */
    public void setParamsEnc(boolean enable) {
        this._paramsEnc = enable;
    }

    /***
     * 是否将服务器的返回的数据直接返回
     *
     * @param enable
     */
    public void setReturnPrimaryData(boolean enable) {
        this._returnPrimaryData = enable;
    }

    /**
     * 异步请求
     *
     * @param params(当参数中包含数组时有问题，已经遗弃)
     * @param callback
     */
    @Deprecated
    public void requestWithParams(List<NameValuePair> params, ApiCallback callback) {

        this._callback = callback;
        NetworkManager.getInstance().asyncPostRequest(this._getRequestUrl(), this._encodeApiParams(params), this._responseParser());
    }

    /**
     * 异步请求
     *
     * @param map
     * @param callback
     */
    public void requestWithParams(Map<String, Object> map, ApiCallback callback) {

        this._callback = callback;
        NetworkManager.getInstance().asyncPostRequest(this._getRequestUrl(), this._encodeApiParams(map), this._responseParser());
    }

    /****
     * 请求时不传入参数
     *
     * @param callback
     */
    public void request(ApiCallback callback) {
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
        requestWithParams(new HashMap<String, Object>(), callback);
    }

    /**
     * 请求是否OK
     *
     * @param response
     * @return
     */
    protected boolean isResponseOk(ApiResponse response) {
        return response != null && response.error_code == 0;
    }


    /**
     * default parser
     *
     * @return JSONInterpret
     */
    private JSONInterpret _responseParser() {

        return new JSONInterpret() {

            @Override
            public void interpret(String result) {

                if (!_returnPrimaryData) {
                    ApiResponse response = responseObjectParse(_decodeResponse(result));
                    if (isResponseOk(response)) {
                        _callback.success(response.parseData);
                    } else {
                        if (response.error_code == 100010) {
                            //response.error_message = "错误码:"+response.error_code+"\n请重试，我刚才去签到了";
                            response.error_message = "请重试(" + response.error_code + ")";
                            ApisManager.checkin(new ApiCallback() {
                                @Override
                                public void success(Object object) {
                                    String workKey = (String) object;
                                    LocalDataDeal.writeToLocalWorkKey(workKey, 1, MainApplication.getContext());
                                }

                                @Override
                                public void error(ApiResponse response) {

                                }
                            });
                        } else if (response.error_code == 100013) {
                            //response.error_message = "错误码:"+response.error_code+"\n副本不存在";
                            response.error_message = "副本不存在(" + response.error_code + ")";
                        } else if (response.error_code == 130003) {
                            if (MainApplication.getmActivity() != null) {
                                MainApplication.getmActivity().startActivity(new Intent(MainApplication.getmActivity(), LoginActivity.class));
                            } else {
                                response.error_message = "员工登录过期(" + response.error_code + ")";
                            }
                        }
                        else if(response.error_code == -1)
                        {
                            CommonUtils.LogWuwei(tag,"---wrong msg Not in Error-----");
                            response.error_message = "网络连接不稳定，请重试(100)";
                        }
                        CommonUtils.LogWuwei(tag, getApiAction() + " 网络请求发生错误:code-" + response.error_code + " msg-" + response.error_message);
                        _callback.error(response);
                    }
                } else {
                    _callback.success(result);
                }
            }

            @Override
            public void error(NetworkException e) {

                Log.e("error","BaseApis"+e.getMessage());

                ApiResponse response = new ApiResponse();
                response.error_code = e.getStatusCode();
                String errMsg = e.getLocalizedMessage();
                if (errMsg == null) {
                    response.error_message = "";
                    _callback.error(response);
                }
                //else if (!errMsg.equals("") && errMsg.contains(":")) {
                else if (!StringUtils.isEmpty(errMsg)) {
                    //errMsg = CommonUtils.getSringBeforeChar(errMsg, ':');
                    if (NetWorkUtils.isWifiConnected(MainApplication.getContext())) {
                        if (errMsg.contains("SocketTimeoutException")) {
                            //errMsg = "错误码:100\n抱歉，服务器响应超时了";
                            errMsg = "网络连接不稳定，请重试(100)";
                        } else if (errMsg.contains("ConnectTimeoutException")) {
                            //errMsg = "错误码:101\n连接超时，本地网络不太稳定";
                            errMsg = "网络连接不稳定，请检查网络(101)";
                        } else if (errMsg.contains("ClientProtocolException")) {
                            //errMsg = "错误码:102\n数据通讯异常";
                            errMsg = "网络连接不稳定，请重试(102)";
                        } else if (errMsg.contains("IOException")) {
                            //errMsg = "错误码:103\n数据操作异常";
                            errMsg = "网络连接不稳定，请稍后重试(103)";
                        } else if (errMsg.contains("UnknownHostException") || errMsg.contains("HttpHostConnectException")) {
                            //errMsg = "错误码:104\n本地网络异常，请检查网络连接";
                            errMsg = "网络连接不可用，请检查网络(104)";
                        } else if (errMsg.contains("rsa")) {
                            ApisManager.checkin(new ApiCallback() {
                                @Override
                                public void success(Object object) {
                                    String workKey = (String) object;
                                    LocalDataDeal.writeToLocalWorkKey(workKey, 1, MainApplication.getContext());
                                }

                                @Override
                                public void error(ApiResponse response) {

                                }
                            });
                        }

                        int code = e.getStatusCode();
                        switch (code) {
                            case -1:
                                CommonUtils.LogWuwei(tag,"---wrong msg in Error-----");
                                errMsg = "网络连接不稳定，请重试(100)";
                                break;
                            case 100003:// 副本不存在
                                //errMsg = "错误码:"+response.error_code+"\n副本不存在";
                                errMsg = "副本不存在(" + response.error_code + ")";
                                break;
                            case 100010:// rsa 解密失败
                                errMsg = "错误码:" + response.error_code + "\n数据加解密异常，请重试";
                                ApisManager.checkin(new ApiCallback() {
                                    @Override
                                    public void success(Object object) {
                                        String workKey = (String) object;
                                        LocalDataDeal.writeToLocalWorkKey(workKey, 1, MainApplication.getContext());
                                    }

                                    @Override
                                    public void error(ApiResponse response) {

                                    }
                                });
                                break;
                            case 100008:// 工作密钥过期
                                errMsg = "错误码:" + response.error_code + "\n密钥过期，请重试";
                                ApisManager.checkin(new ApiCallback() {
                                    @Override
                                    public void success(Object object) {
                                        String workKey = (String) object;
                                        LocalDataDeal.writeToLocalWorkKey(workKey, 1, MainApplication.getContext());
                                    }

                                    @Override
                                    public void error(ApiResponse response) {

                                    }
                                });
                                break;
                            case 100006:
                                if (flagPrintNetWorkLog) {
                                    CommonUtils.LogWuwei(tag, getApiAction() + " 错误码:" + response.error_code + "\n服务器异常");
                                }
                                errMsg = "服务器异常";
                                break;
                            case 130003:// 员工登录过期
                                MainApplication.getmActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HandlerUtils.showToast(MainApplication.getContext(), "员工登录过期，请重新登录");
                                    }
                                });
                                Intent intent = new Intent(MainApplication.getmActivity(), LoginActivity.class);
                                MainApplication.getContext().startActivity(intent);
                                break;
                            case 288001:// 出餐数量大于订单剩余产品数量
                               /* CommonUtils.sendMsg("", MealDoneActivity.HIDE_LOADING, MealDoneActivity.handlerMealDone);
                                Map<String, Object> mapUpdateView = new HashMap<String, Object>();
                                //mapUpdateView.put("array", array);
                                //mapUpdateView.put("packaged", intArg);
                                mapUpdateView.put("flagLoncClicked", false);

                                Message msgUpdateView = new Message();
                                msgUpdateView.what = MealDoneActivity.UPDATE_ALL_AFTER_PUSHED_MEAL;
                                msgUpdateView.obj = mapUpdateView;
                                MealDoneActivity.handlerMealDone.sendMessage(msgUpdateView);*/
                                break;
                            default:
                                if (flagPrintNetWorkLog) {
                                    CommonUtils.LogWuwei(tag, getApiAction() + " 错误码:" + response.error_code + "\n" + response.error_message);
                                }
                                break;
                        }
                        if (flagPrintNetWorkLog) {
                            CommonUtils.LogWuwei(tag, getApiAction() + " 错误码:" + response.error_code + "\n" + response.error_message);
                        }
                    } else {
                        //errMsg = "错误码:000\n请打开wifi，并接入网络";
                        errMsg = "网络连接不可用，请检查网络(000)";
                    }
                    CommonUtils.LogWuwei(tag, getApiAction() + " 网络请求发生错误:" + errMsg);
                    response.error_message = errMsg;
                    _callback.error(response);
                }
            }


            @Override
            public void launchProgress() {
            }

            @Override
            public void cancelProgress() {
            }
        };
    }


    /**
     * 请求参数拼接
     *
     * @return
     */
    private String _getRequestUrl() {
        return API_DOMAIN + this.getApiAction();
    }

    private List<NameValuePair> _encodeApiParams
            (Map<String, Object> map) {
        Gson gson = new Gson();

        
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<NameValuePair> realParams = new ArrayList<NameValuePair>();

        params.putAll(this._getBaseRequestParam());
        for (String key : map.keySet()) {
            params.put(key, map.get(key));
        }

        String str = params.toString();

        realParams.add(new BasicNameValuePair("client_id", LocalDataDeal.readFromLocalClientId(MainApplication.getContext())));
        realParams.add(new BasicNameValuePair("app_id", "2"));
        realParams.add(new BasicNameValuePair("timestamp", "" + System.currentTimeMillis() / 1000));
        if (flagPrintNetWorkLog) {
            CommonUtils.LogWuwei(tag, "API is" + _getRequestUrl());
            CommonUtils.LogWuwei(tag, "params is " + str);
        }
        if (this._paramsEnc) {
            this._aeskey = CommonUtils.getRandomAESKey();
            realParams.add(new BasicNameValuePair(ENC_DATA_KEY, this._encData(params)));
            realParams.add(new BasicNameValuePair("encryptkey", this._aesEncode(this._aeskey)));
        } else {
            realParams.add(new BasicNameValuePair(ENC_DATA_KEY, gson.toJson(params)));
        }
        return realParams;
    }

    private List<NameValuePair> _encodeApiParams(List<NameValuePair> allParams) {

        Gson gson = new Gson();
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<NameValuePair> realParams = new ArrayList<NameValuePair>();
        params.putAll(this._getBaseRequestParam());
        for (NameValuePair value : allParams) {
            params.put(value.getName(), value.getValue());
        }

        //realParams.add(new BasicNameValuePair("client_id", LocalDataDeal.readFromLocalClientId(MainApplication.getContext())));
        realParams.add(new BasicNameValuePair("app_id", "2"));
        realParams.add(new BasicNameValuePair("version", LocalDataDeal.readFromLocalClientId(MainApplication.getContext())));
        realParams.add(new BasicNameValuePair("timestamp", "" + System.currentTimeMillis() / 1000));
        if (flagPrintNetWorkLog) {
            String msg = params.toString();
            //CommonUtils.LogWuwei(tag, "\n\n");
            //CommonUtils.LogWuwei(tag, "params is " + msg);
        }

        if (this._paramsEnc) {
            this._aeskey = CommonUtils.getRandomAESKey();
            realParams.add(new BasicNameValuePair(ENC_DATA_KEY, this._encData(params)));
            realParams.add(new BasicNameValuePair("encryptkey", this._aesEncode(this._aeskey)));
        } else {
            realParams.add(new BasicNameValuePair(ENC_DATA_KEY, gson.toJson(params)));
        }
        return realParams;
    }

    private String _aesEncode(String aesKey) {
        String encodeKey = "";
        switch (this.encodeKeyType) {
            case KEYTYPE_MAIN:
                encodeKey = LocalDataDeal.readFromLocalMasterKey(MainApplication.getContext());
                break;
            case KEYTYPE_PUBLICKEY:
                encodeKey = LocalDataDeal.readFromLocalPublicKey(MainApplication.getContext());
                break;
            case KEYTYPE_WORK:
                encodeKey = (String) LocalDataDeal.readFromLocalWorkKey(MainApplication.getContext()).get("workKey");
                break;
        }

        RSAUtils rsaUtils = new RSAUtils(Base64.decode(encodeKey, 1), null, null);//使用工作/主/工作密钥进行RSA加密
        byte[] encAESKey = rsaUtils.encrypt(aesKey.getBytes());//对AES密钥进行加密
        byte[] BASE64EncAESKey = Base64.encode(encAESKey, 1);//对加密后的AES密钥进行BASE64处理
        return new String(BASE64EncAESKey, StandardCharsets.UTF_8);
    }

    private String _encData(Object data) {

        AESUtils aesUtils = new AESUtils(this._aeskey.getBytes());
        Gson gson = new Gson();

        byte[] encData = aesUtils.encrypt(gson.toJson(data).getBytes());//AES加密
        byte[] BASE64EncData = Base64.encode(encData, 1);//base64处理
        return new String(BASE64EncData, StandardCharsets.UTF_8);


    }

    private ApiResponse _decodeResponse(String response) {

        //ApiResponse res = new Gson().fromJson(response, ApiResponse.class);
        ApiResponse res = new ApiResponse();
        boolean flagParseSuccess = true;
        try {
            res = JSON.parseObject(response, ApiResponse.class);
            if (res != null) {
                if (this._paramsEnc && this.isResponseOk(res)) {
                    AESUtils aesUtils = new AESUtils(this._aeskey.getBytes());
                    byte[] dataAfterBase64 = Base64.decode(res.data, 1);
                    String decData = new String(aesUtils.decrypt(dataAfterBase64));
                    res.origialData = decData;
                    res.jsonObject = JSON.parseObject(decData);
                    if (flagPrintNetWorkLog) {
                        String msg = CommonUtils.logInfoFormatTojson(res.jsonObject.toString());
                        msg = "\n" + CommonUtils.TOP_BORDER + "\n" + msg + "\n" + CommonUtils.BOTTOM_BORDER;
                        //CommonUtils.LogWuwei(tag, msg);
                    }
                }
            }
        } catch (Exception e) {
            flagParseSuccess = false;
            CommonUtils.LogWuwei("MealDoneActivity",
                    "faield\n" + "faield\n" + "faield\n" + _getRequestUrl() + " parseObject failed:now response is " + response + "faield\n" + "faield\n" + "faield\n" + "faield\n" + "faield\n");
        } finally {
            if (!flagParseSuccess) {
                res.error_code = 104;
                res.error_message = "网络连接不可用，请检查网络(104)";
            }
            return res;
        }

    }

    /**
     * base params
     *
     * @return
     */
    private HashMap<String, Object> _getBaseRequestParam() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("lang", "zh_cn");
        params.put("staff_access_token", LocalDataDeal.readFromLocalStaffAccessToken(MainApplication.getContext()));
        return params;
    }


    private ApiCallback _callback;
    private boolean _paramsEnc = true;
    private boolean _returnPrimaryData = false;
    private String _aeskey;
    private EncodeKeyType encodeKeyType = EncodeKeyType.KEYTYPE_WORK;


}