package com.huofu.RestaurantOS.ui.pannel.call;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.NetWorkUtils;
import com.huofu.RestaurantOS.utils.notify.NotifyUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/****
 *
 * 发送叫号请求
 *
 */
public class ClientSpeak {
	
	private static String tag = "";
	public static int CallNum = 0;
	public static final int POST = 0;
	public static Thread sndServerThread = null;
	public static Handler handlerSpeak = null;
	public static int historyNoUpdate = 0;
	public static String serverIpAddress = "";
	public static String Content = "";
	public static int flag_update_listview = 0;//当从悬浮叫号界面进行叫号时，不更新listview
	private static long post_time = 0;
	public static HttpClient httpClient = null;		// 创建httpclient
	public static String languageTypeList[]={"vixq","vinn","vimary","xiaoyan",
											"vixyun","vixying","vixm",
											"vixr","vixk","vixl"};
	public static Context ctxt = null;
	public static HttpPost httpPost;
	public static long pressedCallTime;
									
	public static void ClientSpeak(int take_serial_num,Context context)
	{
		NotifyUtils.webchatNotify(take_serial_num);
		CommonUtils.LogWuwei(tag,take_serial_num+"叫号中");
		if(take_serial_num <= 0)
		{
			return;
		}
		pressedCallTime = System.currentTimeMillis()/1000;
		CallNum = take_serial_num;
		ctxt = context;
		CommonUtils.sendMsg("",POST,handlerSpeak);
	}
	
	/*
    * 在线程中得到用户的请求信息并发送给服务器
    * @param flag_tmp：发送请求类型
    */
	public static void create_thread(final Context ctxt)
		{
		httpClient = new DefaultHttpClient();
		
		sndServerThread = new Thread(){
				public void run(){
					Looper.prepare();
					handlerSpeak = new Handler()
					{
						@Override
						public void handleMessage(Message msg) {
							// TODO Auto-generated method stub
							super.handleMessage(msg);
							
							serverIpAddress = LocalDataDeal.readFromLocalCallTvIp(ctxt);
							if(serverIpAddress.equals((String)""))
						    {
								//CommonUtils.sendMsg("叫号电视的IP地址未设置", MealDoneActivity.SHOW_ERROR_MESSAGE_SET, MealDoneActivity.handlerMealDone);
								return;
							}
							if(!CommonUtils.isIp(serverIpAddress))
							{
								//CommonUtils.sendMsg("叫号电视的IP地址格式不正确", MealDoneActivity.SHOW_ERROR_MESSAGE_SET, MealDoneActivity.handlerMealDone);
								return;
							}
							
							switch (msg.what)
							{
							
							case POST:
					                post_time = System.currentTimeMillis();
					                CommonUtils.LogWuwei(tag, "准备发起叫号请求");
					                doPost();
									CommonUtils.LogWuwei(tag, "结束叫号请求");
								break;
							}
						}
					};
					Looper.loop();
					}
			};
		sndServerThread.start();
			
		}
	
		/*
		 * 以post的方式发送数据到服务器
		 */
		public  static void doPost()
		{
			String path = "";
			Long timeSndFromMobile = System.currentTimeMillis()/1000;//发送请求时间
			path="http://"+serverIpAddress+":8080/?"+"NowCall="+CallNum+"&VoiceName="+"xiaoyan"+
					"&VoiceSpeed="+50+
					"&VoiceCounts="+2+
					"&HistoryNoUpdate="+0+"&LanguageType="+languageTypeList[3]+
					"&timeStampFromClient="+pressedCallTime+
					"&timeSendFromMobile="+timeSndFromMobile;
			
			httpPost = new HttpPost( path);        // 创建HTTP Post
	        
			CommonUtils.LogWuwei(tag, "path is "+path);
			
			try {
		        	if(httpPost != null)
		        	{
		        		httpClient.execute(httpPost);
						CommonUtils.LogWuwei(tag,"叫号请求成功时间为:"+CommonUtils.getStrTime(System.currentTimeMillis()/1000));
		        	}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CommonUtils.LogWuwei(tag, CallNum + " called failed,error is " + e.getMessage()+"\n\n");
				//CommonUtils.sendMsg("叫号失败", MealDoneActivity.SHOW_TOAST, MealDoneActivity.handlerMealDone);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CommonUtils.LogWuwei(tag, CallNum+" called failed,error is "+e.getMessage()+"\n\n");
				if(NetWorkUtils.isWifiConnected(ctxt))
				{
					//CommonUtils.sendMsg("叫号失败,请确认: 1、电视已经打开 2、电视ip在设置中设置正确", MealDoneActivity.SHOW_TOAST, MealDoneActivity.handlerMealDone);
				}
				else
				{
					//CommonUtils.sendMsg("叫号失败,请确认: 本机的wifi已经连接", MealDoneActivity.SHOW_TOAST, MealDoneActivity.handlerMealDone);
				}
				
			}
			catch(IllegalArgumentException e)
			{
				//CommonUtils.sendMsg("叫号失败,请确认: 电视ip地址输入正确", MealDoneActivity.SHOW_TOAST, MealDoneActivity.handlerMealDone);
			}
	        
	        path = null;
	        httpPost = null;
		}
		

}
