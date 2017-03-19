package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.widget.Toast;


public class HandlerUtils {

	public static String tag = "HandlerUtils";

	public static final int SHOW_TOAST = 0;
	public static final int SHOW_THREAD_TOAST = 1;

	public static ThreadToast tt = null;
	
	public static Toast mtoast= null;


	public static void showToast(Context ctxt,String text)
	{
		
		if(mtoast!=null)
        {
            mtoast.setText(text);    
        }
        else
        {
        	
            mtoast=Toast.makeText(ctxt,text, Toast.LENGTH_LONG);
            
            CommonUtils.LogWuwei(tag, mtoast== null?"mtoast为空":"mtoast不为空");
        } 
        mtoast.show(); //显示toast信息
	}
}
