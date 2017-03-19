package com.huofu.RestaurantOS.utils.net;

import org.apache.http.NameValuePair;

import java.util.List;

public class HttpRequestPost extends HttpRequest {

	static final String TAG = "HttpsRequestPost";

	public HttpRequestPost(List<NameValuePair> params, JSONInterpret jsonInterpret, String url)
	{
		super(params, jsonInterpret, url);
	}

	@Override
	public void execute() {
		try
		{
			String response = mRequest.requestUrl(mUrl, BaseRequest.HTTP_METHOD_POST, mParams);
			if (mJSONInterpret != null)
			{
				mJSONInterpret.interpret(response);
			}
		}
		catch (NetworkException e)
		{
			e.printStackTrace();
			if(mJSONInterpret != null)
			{
				mJSONInterpret.error(e);
			}

		}
	}

}

