package com.huofu.RestaurantOS.utils.net;

public class HttpRequestGet {
	
	static final String TAG = "HttpRequestGet";


	public HttpRequestGet() {

	}

	public byte[] asyncRequestResource(String urlPath) {
		byte[] result = null;
		try {
			result = new BaseRequest().requestResource(urlPath);
		} catch (NetworkException e) {
			e.printStackTrace();
		}
		return result;
	}
}
