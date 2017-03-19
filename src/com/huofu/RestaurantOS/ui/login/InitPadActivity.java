package com.huofu.RestaurantOS.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.ui.funcSplash.AllFuncSplashActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/****
 *
 *pad初始化 目前只是设置名称和id，需要管理员权限
 *
 */
public class InitPadActivity extends Activity{

	public Context ctxt = null;
	public static boolean active = false;
	public Handler handler = null;
	
	public EditText etStoreName = null;
	public EditText etCheckStandName = null;
	
	public Button btnFinish = null;
	
	public String newStoreName = "";
	public String newCheckStandName = "";
	
	PopupWindow dialog_show_error = null;
	PopupWindow pop_loading = null;
	
	public static final int SHOW_ERROR_MESSAGE = 1;
	public static final int SHOW_LOADING_TEXT = 2;
	public static final int HIDE_LOADING = 3;
	public static final int SAVE_SUCCESS = 4;
	public static final int SHOW_TOAST = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_store_checkstand_name);
		
		init();
		
		widgetConfigure();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		active = false;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		active = true;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		active = false;
	}


	/***
	 * 初始化
	 */
	public void init()
	{
		MainApplication.setmActivity(this);
		ctxt = getApplicationContext();
		
		String storeName = LocalDataDeal.readFromLocalStoreName(ctxt);
		String checkStandName  = LocalDataDeal.readFromLocalAppCopyName(ctxt);
		
		
		etStoreName = (EditText)findViewById(R.id.et_set_store_name);
		etCheckStandName = (EditText)findViewById(R.id.et_set_checkstand_name);
		btnFinish = (Button)findViewById(R.id.btn_finish_init);
		
		etStoreName.setText(storeName);
		etCheckStandName.setText(checkStandName);
	}
	
	
	/***
	 * 控件配置
	 */
	public void widgetConfigure()
	{
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		etStoreName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				newStoreName = s.toString();
			}
		});
		
		etCheckStandName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				newCheckStandName = s.toString();
			}
		});
		
		btnFinish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				CommonUtils.sendMsg("保存名称...", SHOW_LOADING_TEXT, handler);
				if(newStoreName.equals("") || newStoreName == null)
				{
					newStoreName = LocalDataDeal.readFromLocalStoreName(ctxt);
				}
				
				if(newCheckStandName.equals("") || newCheckStandName == null)
				{
					newCheckStandName = LocalDataDeal.readFromLocalAppCopyName(ctxt);
				}


				ApisManager.SaveStoreNewInfo(newCheckStandName, newStoreName, new ApiCallback() {
					@Override
					public void success(Object object) {
						CommonUtils.sendMsg("",SAVE_SUCCESS,handler);
					}

					@Override
					public void error(BaseApi.ApiResponse response) {
						CommonUtils.sendMsg("",HIDE_LOADING,handler);
						if(response.error_code == 210001)// 副本名称重复
						{
							response.error_message = "收银台名称已存在，再起个更好的名字吧";
						}
						CommonUtils.sendMsg(response.error_message,SHOW_ERROR_MESSAGE,handler);
					}
				});

				
			}
		});
		
		handler = new Handler()
		{
			public void handleMessage(android.os.Message msg) 
			{
				switch(msg.what)
				{
				case SHOW_LOADING_TEXT:
					showLoadingDialog((String)msg.obj);
					break;
				case HIDE_LOADING:
					hideLoadingDialog();
					break;
				case SHOW_ERROR_MESSAGE:
					showDialogError((String)msg.obj);
					break;
				case SAVE_SUCCESS:
					LocalDataDeal.writeToLocalFlagFinishInitPad(ctxt, true);
					Intent intent = new Intent(InitPadActivity.this,AllFuncSplashActivity.class);
					startActivity(intent);
					finish();
					break;
				case SHOW_TOAST:
					HandlerUtils.showToast(ctxt, (String)msg.obj);
					break;
				}
			};
		};
	}


	/**
	 * 显示加载中的窗口
	 */
	public void showLoadingDialog(String text)
	{
		try
		{
			LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View grid = inflater.inflate(R.layout.loading_layout, null);

			TextView tv = (TextView)grid.findViewById(R.id.textview_loading_content);
			tv.setText(text);

			ImageView iv = (ImageView)grid.findViewById(R.id.imageview_loading_pic);
			iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));

			int width = CommonUtils.getScreenWidth(ctxt);
			int height = CommonUtils.getScreenHeight(ctxt);


			if(pop_loading == null)
			{
				pop_loading =  new PopupWindow(grid,width,height,true);
			}
			else
			{
				pop_loading.setContentView(grid);
			}

			pop_loading.setFocusable(true);
			pop_loading.setBackgroundDrawable(new BitmapDrawable());
			pop_loading.setOutsideTouchable(true);
			pop_loading.setAnimationStyle(R.style.AutoDialogAnimation);
			pop_loading.showAtLocation(etCheckStandName,Gravity.NO_GRAVITY,0,0);

		}
		catch(Exception e)
		{

		}


	}


	/**
	 * 隐藏加载中的窗口
	 */
	public void hideLoadingDialog()
	{

		if(pop_loading != null)
		{
			if(pop_loading.isShowing())
			{
				pop_loading.dismiss();
			}
		}

	}

	/**
	 * 显示异常的窗口
	 */
	public void showDialogError(String msg)
	{
		if(!active)
		{
			return;
		}
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View grid = inflater.inflate(R.layout.dialog_show_error_one_option, null);

		TextView tvContent = (TextView)grid.findViewById(R.id.tv_dialog_error_content);
		final Button btn_close = (Button)grid.findViewById(R.id.btn_dialog_error_close);

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
			}
		};
		btn_close.setOnClickListener(ocl);

		int width = CommonUtils.getScreenWidth(ctxt);
		int height = CommonUtils.getScreenHeight(ctxt);


		if(dialog_show_error == null)
		{
			dialog_show_error =  new PopupWindow(grid,width,height,true);
		}

		dialog_show_error.setBackgroundDrawable(new BitmapDrawable());
		dialog_show_error.setContentView(grid);
		dialog_show_error.setFocusable(true);
		dialog_show_error.setOutsideTouchable(true);
		dialog_show_error.setAnimationStyle(R.style.AutoDialogAnimation);
		dialog_show_error.showAtLocation(etCheckStandName,Gravity.NO_GRAVITY,0,0);
	}




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK)
		{
			Intent intent = new Intent(InitPadActivity.this,LoginActivity.class);
			startActivity(intent);
		}
		
		return super.onKeyDown(keyCode, event);
	}

	
	
}
