package com.huofu.RestaurantOS.utils;

import android.os.Handler;
import android.os.Message;

public class MsgUtils {

	public static String tag = "MsgUtils";
	
	/*
	 * 发送一个消息到消息队列中
	 * @param str:发送的字符串
	 * @param direction:方向
	 */
	public static void SendSingleMsg(Handler handler,String str,int direction)
	{
		Message msg = new Message();
		msg.what = direction;
		msg.obj = str;
		if(handler != null)
		{
			//CommonUtils.LogWuwei(tag, "message ready to send is "+msg.obj);
			handler.sendMessage(msg);	
		}
		else
		{
			CommonUtils.LogWuwei("msgUtils","handler  is null" );
		}
		
	}
}
