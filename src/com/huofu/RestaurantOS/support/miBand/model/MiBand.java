package com.huofu.RestaurantOS.support.miBand.model;

import java.util.Observable;

import com.huofu.RestaurantOS.utils.CommonUtils;



public class MiBand extends Observable {

	public String mBTAddress="";
	public int mSteps=0;
	public String mName="";
	public Battery mBattery= new Battery();
	public LeParams mLeParams=new LeParams();
	public int mRssi=0;
	public String tag = "miBand";
	
	
	
	
	public void setName(String name) {
		mName = name;
		CommonUtils.LogWuwei(tag,"setting "+name+" as BLE name");
		setChanged();
		notifyObservers();
	}
	
	public void setSteps(int steps) {
		mSteps = steps;
		CommonUtils.LogWuwei(tag,"setting "+steps+" steps");
		setChanged();
		notifyObservers();
	}
	
	public void setBattery(Battery battery) {
		mBattery = battery;
		CommonUtils.LogWuwei(tag,"battery is"+battery.toString());
		setChanged();
		notifyObservers();
	}

	public void setLeParams(LeParams params) {
		mLeParams = params;
	}
	
	public void setRssi(int rssi)
	{
		mRssi = rssi;
		CommonUtils.LogWuwei(tag,"mRssi is "+mRssi);
	}
	
	
}
