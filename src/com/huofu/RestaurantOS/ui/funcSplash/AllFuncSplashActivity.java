package com.huofu.RestaurantOS.ui.funcSplash;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.service.autoPushMealIntentService;
import com.huofu.RestaurantOS.service.getTakeupListIntentService;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.utils.launchPadUtils;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;
import com.huofu.RestaurantOS.widget.SimpleSideDrawer;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/****
 *
 *launchPad
 *
 */
public class AllFuncSplashActivity extends BaseActivity {
	public static Context ctxt = null;
	public static String tag = "AllFuncSplashActivity";
	
	public BitmapUtils bitmapUtils;
	public BitmapDisplayConfig bigPicDisplayConfig;
	
	public DefaultBitmapLoadCallBack<ImageView> callback;// 图片加载的回调函数
	public String pathLoginStaffCache = Environment.getExternalStorageDirectory()+File.separator+ "huofu"
												+ File.separator + "ImageCache" + File.separator;
	
	TextView tv;
	Handler threadHandler = null;
	PopupWindow dialog_show_error = null;
	Dialog dialog_loading = null;
	PopupWindow dialog_show_print_error = null;
	PopupWindow pop_loading = null;
	public static boolean active = false;
		
	public static ActivityManager  mActivityManager = null;

	public boolean hasFocus = false;
	
	public static final int SHOW_LOADING = 0;
	public static final int HIDE_LOADING = 1;
	public static final int SHOW_DIALOG_ERROR= 2;
	public static final int SHOW_PRINTER_CONNECT_ERROR= 3;
	public static final int SERVICE_CONFIGURE= 4;//配置自动出餐的service
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		active = true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		ViewServer.get(this).addWindow(this);
		
		setContentView(R.layout.all_func_splash);
		
		init();

		launchPadConfigure();


		mUiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				showAllFuncDialog();
				mslidingMenu.toggleLeftDrawer();
				CommonUtils.sendMsg(null, SERVICE_CONFIGURE, threadHandler);
			}
		},500);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mUiHandler.removeMessages(SHOW_LOADING);
		mUiHandler.removeMessages(HIDE_LOADING);
		mUiHandler.removeMessages(SHOW_DIALOG_ERROR);
		mUiHandler.removeMessages(SHOW_PRINTER_CONNECT_ERROR);
		
		ViewServer.get(this).removeWindow(this);
		mUiHandler = null;
		dialog_show_error = null;
		dialog_loading = null;
		dialog_show_print_error = null;

		if(mslidingMenu != null)
		{
			if(mslidingMenu.flagLeftOpen)
			{
				mslidingMenu.closeLeftSide();
			}
			if(mslidingMenu.flagRightOpen)
			{
				mslidingMenu.closeRightSide();
			}
		}
		
		if(pop_loading != null)
		{
			if(pop_loading.isShowing())
			{
				pop_loading.dismiss();
			}
		}
		mslidingMenu = null;
		popwindowAllFunc = null; 
		
		bitmapUtils = null;
		bigPicDisplayConfig = null;
		
		callback = null;// 图片加载的回调函数
		pathLoginStaffCache = null; 
		listAllFuncMap = null;// 所有功能集合
		
		tv = null;
		mUiHandler = null;
		dialog_show_error = null;
		dialog_loading = null;
		dialog_show_print_error = null;
		
		System.gc();
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		this.hasFocus = hasFocus;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		active = false;
		if(popwindowAllFunc != null)
		{
			popwindowAllFunc.dismiss();
		}
		if(dialog_loading != null)
		{
			dialog_loading.dismiss();
		}
		
		if(dialog_show_error != null)
		{
			dialog_show_error.dismiss();
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		ViewServer.get(this).setFocusedWindow(this);
		
		MobclickAgent.onResume(this);
		
		if(popwindowAllFunc != null)
		{
			if(!popwindowAllFunc.isShowing())
			{
				popwindowAllFunc.showAtLocation(findViewById(R.id.tv_all_func_splash_test_for_popup_window),Gravity.NO_GRAVITY, 198, 0);
			}
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 初始化
	 */
	public void init()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		MainApplication.setmActivity(this);
		ctxt = getApplicationContext();
		mslidingMenu = new SimpleSideDrawer(AllFuncSplashActivity.this);
		mslidingMenu.setLeftBehindContentView(R.layout.navigation_bar_vertical_layout);
		
		popwindowAllFunc = new PopupWindow(ctxt);
		
		bitmapUtils = new BitmapUtils(getApplicationContext(),pathLoginStaffCache, 150 * 300 * 300, 150 * 300 * 300);
		bigPicDisplayConfig = new BitmapDisplayConfig();
		bigPicDisplayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
		
		listAllFuncMap = new ArrayList<Map>();
		listAllFuncMap = launchPadUtils.getLauchpadFuncMap();

		showPopupWindowInit(mslidingMenu);

		ApisManager.GetTimeBucketList(new ApiCallback() {
			@Override
			public void success(Object object) {
				TemplateModulsParse.mealBucketList = (List<MealBucket>) object;
			}

			@Override
			public void error(BaseApi.ApiResponse response) {

			}
		});

		new Thread()
		{
			public void run()
			{
				Looper.prepare();
				threadHandler = new Handler()
				{
					public void handleMessage(Message msg) 
					{
						switch(msg.what)
						{
						case SERVICE_CONFIGURE:
							serviceConfigure();
							break;
						}
						
					};
				};
				Looper.loop();
			}
		}.start();



	}


	@Override
	protected void dealWithmessage(Message msg) {
		switch (msg.what)
		{
			case SHOW_LOADING:
				showLoadingDialog((String)msg.obj);
				break;
			case HIDE_LOADING:
				hideLoadingDialog();
				break;
			case SHOW_DIALOG_ERROR:
				showDialogError((String)msg.obj, 0);
				break;
			case SHOW_PRINTER_CONNECT_ERROR:
				Map map = (Map)msg.obj;
				showDialogPrintTestFailedError((String)map.get("errorMsg"), (Integer)map.get("position"), (Integer)map.get("from"));
				break;
			default:
				break;
		}
	}


	/**
	 * 自动出餐的服务配置
	 */
	public void serviceConfigure()
	{
		startService(new Intent(this,autoPushMealIntentService.class));

		if(!getTakeupListIntentService.flagActivate)
		{
			CommonUtils.LogWuwei(getTakeupListIntentService.tag, "service going to run");
			startService(new Intent(this,getTakeupListIntentService.class));
		}
		else
		{
			CommonUtils.LogWuwei(getTakeupListIntentService.tag, "service alread in service ");
		}

		getTakeupListIntentService.flagServiceOn = true;
	}


	/**
	 * 显示异常的窗口
	 * @param msg:错误内容
	 * @param result 1->退回到设置界面 0-》停留在当前窗口
	 */
	public void showDialogError(String msg,final int result)
	{
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);
		
		TextView tvContent = (TextView)grid.findViewById(R.id.tv_dialog_error_content);
		final Button btn_close = (Button)grid.findViewById(R.id.btn_dialog_error_close);
		
		if(result == 1)
		{
			btn_close.setText("设置");
		}
		tvContent.setText(msg);
		
		OnClickListener ocl= new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId() == btn_close.getId())
				{
					if(dialog_show_error != null)
					{
						if(dialog_show_error.isShowing())
						{
							dialog_show_error.dismiss();
						}
					}
				}
				if(result == 1)
				{
					startActivity(new Intent(AllFuncSplashActivity.this, com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity.class));
					finish();
				}
			}
		};
		btn_close.setOnClickListener(ocl);

		int width = CommonUtils.getScreenWidth(ctxt);
		int height = CommonUtils.getScreenHeight(ctxt);
		
		if(dialog_show_error == null)
		{
			dialog_show_error =  new PopupWindow(grid,width,height,true);
		}
		else
		{
			dialog_show_error.setContentView(grid);
		}
		dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
		dialog_show_error.setFocusable(true);
		dialog_show_error.setOutsideTouchable(true);
		dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
		dialog_show_error.showAtLocation(findViewById(R.id.tv_all_func_splash_test_for_popup_window),Gravity.NO_GRAVITY, 198, 0);
		
	}

	/***
	 * 打印机连接失败的弹窗
	 * @param msg
	 * @param position 在listview或者gridview点击的第几个item
	 * @param from :  0->listview  1->gridview
	 */
	public void showDialogPrintTestFailedError(String msg,final int position,final int from)
	{
		LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View grid = inflater.inflate(R.layout.dialg_show_error,null);
		
		
		TextView tvContent = (TextView) grid.findViewById(R.id.tv_dialog_error_content);
		tvContent.setText(msg);

		final Button btn_set = (Button) grid.findViewById(R.id.btn_dialog_error_close);
		final Button btn_print_test_again = (Button) grid.findViewById(R.id.btn_dialog_error_sure);

		OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == btn_set.getId()) 
				{
					if (dialog_show_print_error != null) 
					{
						if (dialog_show_print_error.isShowing()) 
						{
							dialog_show_print_error.dismiss();
						}
					}
					startActivity(new Intent(AllFuncSplashActivity.this, com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity.class));
					finish();
					
				}

			}
		};
		btn_set.setText("修改设置");
		btn_set.setOnClickListener(ocl);

		btn_print_test_again.setText("重试");
		btn_print_test_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				CommonUtils.LogWuwei(tag, "btn_print_test_again is clicked and from is "+from+"(0 is listview,1 is gridview)");
				
				if (dialog_show_print_error != null) 
				{
					if (dialog_show_print_error.isShowing()) 
					{
						dialog_show_print_error.dismiss();
					}
				}
			}
		});
		
		int width = CommonUtils.getScreenWidth(ctxt);
		int height = CommonUtils.getScreenHeight(ctxt);
	
		
		if(dialog_show_print_error == null)
		{
			dialog_show_print_error =  new PopupWindow(grid,width,height,true);
		}
		
		dialog_show_print_error.setBackgroundDrawable(new BitmapDrawable());
		dialog_show_print_error.setContentView(grid);
		dialog_show_print_error.setFocusable(true);
		dialog_show_print_error.setAnimationStyle(R.style.AutoDialogAnimation);
		dialog_show_print_error.showAtLocation(findViewById(R.id.tv_all_func_splash_test_for_popup_window),Gravity.NO_GRAVITY, 198, 0);


	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == event.KEYCODE_BACK)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
