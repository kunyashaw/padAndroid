package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetWorkUtils {

	public static String tag = "NetWorkUtils";

	/*
	 * 获取所连接移动网络的ip地址
	 */
	public static String GetGPRSIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {

						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			Log.e(tag, "WifiPreference IpAddress---error-" + e.toString());
		}

		return "";

	}

	/*
	 * 获取服务器本身的ip地址
	 * 
	 * @return：ip地址的字符串
	 */
	public static String GetWifiIp(Context context) {

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		// Log.e(tag,"SSID is "+wifiInfo.getSSID());

		int ipAddress = wifiInfo.getIpAddress();
		if (ipAddress == 0) {
			return "";
		} else {
			return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
					+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
		}
	}

	/*
	 * 获取设备所连接移动网络类型及名称
	 * 
	 * @return：网络类型和名称组包好的字符串
	 */
	public static String get_connected_mobile_name(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA) {
				return "联通 3G";
			} else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
				return "2G";
			} else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
				return "电信 2G";
			} else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A)

				return "电信 3G";
		}
		return "";
	}

	/*
	 * 获取已连接wifi的名称
	 * 
	 * @return：已连接wifi的名称
	 */

	public static String get_conntected_wifi_name(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// Log.e(tag,"SSID is "+wifiInfo.getSSID());
		return wifiInfo.getSSID();
	}

	/*
	 * 监测wifi网络是否可用
	 * 
	 * @param context：上下文
	 * 
	 * @return：true->wifi网络已连接 false->wifi网络未连接
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/*
	 * 得到移动网络是否连接
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/*
	 * 实时获取网络连接状态
	 */
	public static String getWifiStatus(Context ctxt) {
		if (isWifiConnected(ctxt)) {
			return "已连接";
		} else {
			return "断开连接";
		}
	}

	public static void showWifiStatus(Context ctxt) {
		String strWifiName = "Wifi 名称：" + get_conntected_wifi_name(ctxt);
		String strWifiIp = "\nWifi 地址:" + GetWifiIp(ctxt);
		String strWifiConnectedFlag = "\nWifi 连接状态:" + getWifiStatus(ctxt);
		String strWifiStatus = strWifiName + strWifiIp + strWifiConnectedFlag;
		HandlerUtils.showToast(ctxt, strWifiName+strWifiIp+strWifiStatus);
	}

	
	/***
	 * 检查一个ip地址是否处于alive状态
	 * @param ip
	 * @return
	 */
	public static boolean checkIpAlive(String ip)
	{
		CommonUtils.LogWuwei(tag, " executeCammand ip is "+ip);
		Runtime runtime = Runtime.getRuntime();
		String s = "";
		try {
			Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 3 " + ip);
			BufferedReader in = new BufferedReader(  new InputStreamReader(mIpAddrProcess.getInputStream()));  
			String line = null;
			while ((line = in.readLine()) != null) 
			{  
				s += line + "\n";
			}
			int mExitValue = mIpAddrProcess.waitFor();
			CommonUtils.LogWuwei(tag, " mExitValue " + mExitValue+" \n s is "+s);
			if (mExitValue == 0) 
			{
				return true;
			}
			else 
			{
				
				InetAddress addr = InetAddress.getByName(ip);
				CommonUtils.LogWuwei(tag, "ping failed ,now try function:isReachable");
				if(addr.isReachable(1500))
				{
					CommonUtils.LogWuwei(tag, "function:isReachable called result is true");
					return true;
				}
				else
				{
					return false;
				}
				
			}
		}
		catch (Exception ignore) 
		{
			ignore.printStackTrace();
			CommonUtils.LogWuwei(tag, " Exception:" + ignore);
			
		}
		return false;
	}
}
