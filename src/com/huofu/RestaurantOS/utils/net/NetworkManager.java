package com.huofu.RestaurantOS.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {

	static final String TAG = "NetworkManager";

	private static NetworkManager networkManager;

//	private Context mContext;

	private ExecutorService mThreadPool;// 启动线程池

	private NetworkManager(){
//		this.mContext = context;
		// 启动通信线程池
		mThreadPool = Executors.newFixedThreadPool(5);
	}

//	public static NetworkManager getInstance(Context context) {
	public static NetworkManager getInstance() {
		if (networkManager == null) {
			networkManager = new NetworkManager();
		}
		return networkManager;
	}

	// 回收资源，以免内存泄漏
	public void recycle() {
		shutdownThreadPool();
		networkManager = null;
//
//		if (mContext != null) {
//
//			mContext = null;
//		}
	}

	/**
	 * 搜索可用的网络
	 * 
	 * @param context
	 * @return
	 */
	public static int searchNetworkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = manager.getActiveNetworkInfo();
		int commType = BaseRequest.NETTYPE_NULL;
		if (active != null) {
			int type = active.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				commType = BaseRequest.NETTYPE_WIFI;
			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				commType = BaseRequest.NETTYPE_MOBILE;
			}
		}
		return commType;
	}

	/**
	 * post、put提交数据 HTTP方式
	 * 
	 * @param url
	 *            请求url
	 * @param params
	 *            请求参数
	 * @param jsonInterpret
	 *            (指定返回数据的回调接口
	 * @return
	 */

	public void asyncPostRequest(String url, List<NameValuePair> params,
			JSONInterpret jsonInterpret) {
		try {

			mThreadPool.execute(new HttpRequestPost(params,jsonInterpret, url));
//			if (searchNetworkType(mContext) != BaseRequest.NETTYPE_NULL) {
//			} else {
//				Toast.makeText(mContext, /* R.string.network_null */
//				"亲，网络不通，请配置网络哦~", Toast.LENGTH_SHORT).show();
//			}
		} catch (Exception e) {
			Log.e(TAG, "ThreadPool Exception:" + e.getMessage());
		}
	}

	public byte[] requestResource(String url) {
		return new HttpRequestGet().asyncRequestResource(url);
	}

	/**
	 * 重新启动线程池
	 */
	public void restartThreadPool() {
		if (mThreadPool != null) {
			shutdownThreadPool();
		}
		mThreadPool = Executors.newFixedThreadPool(5);
	}

	/**
	 * 关闭线程池
	 */
	public void shutdownThreadPool() {
		if (mThreadPool != null) {
			mThreadPool.shutdown();
			mThreadPool = null;
		}
	}
}
