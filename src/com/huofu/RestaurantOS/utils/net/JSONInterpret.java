package com.huofu.RestaurantOS.utils.net;

public interface JSONInterpret {

	public void interpret(String json);// 通知回来的数据

	public void error(NetworkException e);// 网络异常

	public void launchProgress();// 启动等待

	public void cancelProgress();// 取消等待

	// public void networkException(NetworkException e);//服务器异常
}
