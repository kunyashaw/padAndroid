package com.huofu.RestaurantOS.utils.net;

import java.util.List;

import org.apache.http.NameValuePair;

public abstract class HttpRequest implements Runnable {

	protected JSONInterpret mJSONInterpret;// 返回数据回调的接口

	protected String mUrl;

	protected BaseRequest mRequest;

	protected List<NameValuePair> mParams;
	
//	protected Context mContext;

	public HttpRequest(List<NameValuePair> params,
			JSONInterpret xmlInterpret, String url) {		
//		this.mContext = context;
		this.mJSONInterpret = xmlInterpret;
		this.mUrl = url;
		this.mParams = params;
	}

	public void run() {
		if (mJSONInterpret != null) {
			mJSONInterpret.launchProgress();
		}
		if (mRequest == null){
			mRequest = new BaseRequest();
		}
		execute();
		if (mJSONInterpret != null) {
			mJSONInterpret.cancelProgress();
		}
	}
	public abstract void execute();
}
