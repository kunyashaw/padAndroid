package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ThreadToast extends Thread{
	
	public String msg = null;
	public Handler handler;
	public Context ctxt;
	
	public ThreadToast(Context ctxtTmp,String msgTmp,Handler handlerTmp)
	{
		msg = msgTmp;
		handler = handlerTmp;
		ctxt = ctxtTmp;
	}
	
	
	@Override
	public void run() 
	{
		 handler.post(new Runnable() 
		 {
			   @Override
			   public void run() 
			   {
				   Toast.makeText(ctxt, msg,Toast.LENGTH_LONG).show();
			   }
		 });
			
	}

}
