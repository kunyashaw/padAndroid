package com.huofu.RestaurantOS.ui.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

	String tag = "splash(MainActivity)";
	public Handler handler = null;
	public static Context mainCtxt = null;
	public static ExecutorService executorService = Executors.newFixedThreadPool(2);

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		overridePendingTransition(R.anim.in_translate_top,
				R.anim.in_translate_bottom);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		widgetConfigure();

		checkIn();// 签到

	}

	/**
	 * 控件初始化
	 */
	public void init() {
		MainApplication.setmActivity(this);
		handler = new Handler();
		mainCtxt = getApplicationContext();
	}

	/**
	 * 控件配置
	 */
	public void widgetConfigure() {

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = null;
				String clientIString = LocalDataDeal.readFromLocalClientId(getApplicationContext());
				
				CommonUtils.LogWuwei(tag, "clientIString is " + clientIString);
				
				if (clientIString == null || clientIString.equals("")) 
				{
					i = new Intent(MainActivity.this, activate.class);
				}
				else
				{
					i = new Intent(MainActivity.this, LoginActivity.class);
				}

				startActivity(i);
				finish();
			}
		}, 4000);
	}

	/**
	 * 签到
	 */
	public void checkIn() {
		ApisManager.checkin(new ApiCallback() {
			@Override
			public void success(Object object) {

			}

			@Override
			public void error(BaseApi.ApiResponse response) {

			}
		});
	}

}
