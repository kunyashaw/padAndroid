package com.huofu.RestaurantOS.support.miBand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.support.miBand.model.Battery;
import com.huofu.RestaurantOS.support.miBand.model.LeParams;
import com.huofu.RestaurantOS.support.miBand.model.MiBand;
import com.huofu.RestaurantOS.support.miBand.model.UserInfo;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;



public class listviewMiBandListAdapter extends BaseAdapter implements Observer{

	List<bueToothDevice> list ;
	Context ctxt;
	Handler handler;
	private BluetoothGatt mGatt;
	private BluetoothDevice mBluetoothMi;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	
	private MiBand mMiBand = new MiBand();
	private ImageView iv = null;
	private Button btnVibrateTest = null;
	
	private static final UUID UUID_MILI_SERVICE = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_pair = UUID.fromString("0000ff0f-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_CONTROL_POINT = UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_REALTIME_STEPS = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_ACTIVITY = UUID.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_LE_PARAMS = UUID.fromString("0000ff09-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_DEVICE_NAME = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
	private static final UUID UUID_CHAR_BATTERY = UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb");
	public static final UUID	UUID_CHAR_USER_INFO	= UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb");

	
	
	public static final byte[]	PAIR							= { 2 };
	public static final byte[]	VIBRATION_WITH_LED				= { 8, 0 };
	public static final byte[]	VIBRATION_UNTIL_CALL_STOP		= { 8, 1 };
	public static final byte[]	VIBRATION_WITHOUT_LED			= { 8, 2 };
	public static final byte[]	STOP_VIBRATION					= { 19 };
	public static final byte[]	ENABLE_REALTIME_STEPS_NOTIFY	= { 3, 1 };
	public static final byte[]	DISABLE_REALTIME_STEPS_NOTIFY	= { 3, 0 };
	public static final byte[]	SET_COLOR_RED					= { 14, 6, 1, 2, 1 };
	public static final byte[]	SET_COLOR_BLUE					= { 14, 0, 6, 6, 1 };
	public static final byte[]	SET_COLOR_ORANGE				= { 14, 6, 2, 0, 1 };
	public static final byte[]	SET_COLOR_GREEN					= { 14, 4, 5, 0, 1 };
	
	public static final byte[]	REBOOT							= { 12 };
	public static final byte[]	REMOTE_DISCONNECT				= { 1 };
	public static final byte[]	FACTORY_RESET					= { 9 };
	public static final byte[]	SELF_TEST						= { 2 };
	
	public int lastPosition = 0;
	public int state = 0;
	public static int yearMonthDay = 20110102;
	public int index = 0;
	public double nowMeter = 0;
	
	
	
	public listviewMiBandListAdapter(List<bueToothDevice> list,final Context ctxt,Handler handler)
	{
		this.list = list;
		this.ctxt = ctxt;
		this.handler = handler;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View grid = inflater.inflate(R.layout.setting_mi_band_list, null);
		
		iv = (ImageView)grid.findViewById(R.id.imageview_setting_mi_band_loading_whether_right);
		TextView tv = (TextView)grid.findViewById(R.id.textview_setting_mi_band_info);
		btnVibrateTest  = (Button)grid.findViewById(R.id.button_setting_mi_band_vibrate_test);
		
		if(list.get(position).flagVibrateEnable || list.get(position).address.equals(LocalDataDeal.readFromMiBand(ctxt)))
		{
			btnVibrateTest.setVisibility(View.VISIBLE);
			iv.clearAnimation();
			iv.setImageResource(R.drawable.checkbox2);
			iv.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
		}
		else
		{
			btnVibrateTest.setVisibility(View.INVISIBLE);
		}
		
		if(list.get(position).name == null || list.get(position).name.equals(""))
		{
			tv.setText("品牌:未知"+"  地址:"+list.get(position).address);
		}
		else
		{
			tv.setText("品牌:"+list.get(position).name+"  地址:"+list.get(position).address);	
		}
		
		grid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				lastPosition = position;
				
				mMiBand.addObserver(listviewMiBandListAdapter.this);
				mMiBand.mBTAddress = list.get(position).address;
				mBluetoothMi = SettingsActivity.mBluetoothAdapter.getRemoteDevice(list.get(position).address);

				
				mGatt = mBluetoothMi.connectGatt(ctxt, false, mGattCallback);
				mGatt.connect();
				CommonUtils.sendMsg("配对中。。", SettingsActivity.SHOW_LOADING_TEXT, handler);
				
				iv.setImageResource(R.drawable.loading);
				iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));
			}
		});
		
		btnVibrateTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mGatt == null)
				{
					lastPosition = position;
					
					mMiBand.addObserver(listviewMiBandListAdapter.this);
					mMiBand.mBTAddress = list.get(position).address;
					mBluetoothMi = SettingsActivity.mBluetoothAdapter.getRemoteDevice(list.get(position).address);

					
					mGatt = mBluetoothMi.connectGatt(ctxt, false, mGattCallback);
					mGatt.connect();
					CommonUtils.sendMsg("配对中。。", SettingsActivity.SHOW_LOADING_TEXT, handler);
					
					iv.setImageResource(R.drawable.loading);
					iv.startAnimation(AnimationUtils.loadAnimation(ctxt, R.anim.rotate_loading));
					return;
				}
				
				CommonUtils.sendMsg("震动测试", SettingsActivity.SHOW_TOAST, handler);
				setUserInfo();
				startVibrate();
				
				new Thread()
				{
					public void run()
					{
						for(int k=0;k<3;k++)
						{
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							setUserInfo();
							startVibrate();
						}
						
					}
				}.start();
				
			}
		});
		
		return grid;
	}

	
	private void setColor(int num) {
		
		BluetoothGattCharacteristic theme = getMiliService().getCharacteristic(UUID_CHAR_CONTROL_POINT);
		switch(num)
		{
		case 0:
			theme.setValue(SET_COLOR_RED);
			break;
		case 1:
			theme.setValue(SET_COLOR_GREEN);
			break;
		case 2:
			theme.setValue(SET_COLOR_ORANGE);
			break;
		case 3:
			theme.setValue(SET_COLOR_BLUE);
			break;
		}
		
		
		boolean result = mGatt.writeCharacteristic(theme);
		
		CommonUtils.LogWuwei("", result?"write success":"write failed");
	}
	
	private void startVibrate()
	{
		try
		{
			BluetoothGattCharacteristic chara = getMiliService().getCharacteristic(UUID_CHAR_CONTROL_POINT);
			chara.setValue(VIBRATION_WITH_LED);
			boolean result = mGatt.writeCharacteristic(chara);
			CommonUtils.LogWuwei("", result?"startVibrate write success":"write failed");
		}
		catch(NullPointerException e)
		{
			
		}
		
	}
	
	private void stopVibrate()
	{
		try
		{
			BluetoothGattCharacteristic chara = getMiliService().getCharacteristic(UUID_CHAR_CONTROL_POINT);
			chara.setValue(new byte[]{19});
			boolean result = mGatt.writeCharacteristic(chara);
			CommonUtils.LogWuwei("", result?"stopVibrate write success":"write failed");
		}
		catch(NullPointerException e)
		{
			
		}
	}

	
	private void setUserInfo()
	{
		try
		{
			BluetoothGattCharacteristic chara = getMiliService().getCharacteristic(UUID_CHAR_USER_INFO);
			UserInfo userInfo = new UserInfo(yearMonthDay++, 1, 27, 180, 55, "kunyashaw", 0);
			chara.setValue(userInfo.getBytes(mMiBand.mBTAddress));
			boolean result = mGatt.writeCharacteristic(chara);
			CommonUtils.LogWuwei("", result?"setUserInfo write success":"write failed");
		}
		catch(NullPointerException e)
		{
			
		}
		
	}

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) 
		{
			
			CommonUtils.LogWuwei("bandInfo", "rssi is "+rssi);
			mMiBand.setRssi(rssi);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mGatt.readRemoteRssi();
			
		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				pair();
			}

		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) 
			{
				gatt.discoverServices();
				
				CommonUtils.sendMsg(null, SettingsActivity.HIDE_LOADING, handler);
				CommonUtils.sendMsg("配对成功", SettingsActivity.SHOW_TOAST, handler);

				LocalDataDeal.writeToLocalMiBandAddress(list.get(lastPosition).address, ctxt);
				
				for(int k=0;k<list.size();k++)
				{
					list.get(k).flagVibrateEnable = false;
				}
				
				list.get(lastPosition).flagVibrateEnable = true;
				
				CommonUtils.sendMsg("", SettingsActivity.MIBAND_LIST_ADAPTER_NOTIFY, handler);
			}
			else if(newState == BluetoothProfile.STATE_DISCONNECTING)
			{
				CommonUtils.sendMsg("手环已经断开连接", SettingsActivity.SHOW_ERROR_MESSAGE, handler);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// this is called tight after pair()
			 request(UUID_CHAR_REALTIME_STEPS); // start with steps
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) 
		{
			byte[] b = characteristic.getValue();
			
			CommonUtils.LogWuwei(characteristic.getUuid().toString(), "state: " + state+ " value:" + Arrays.toString(b));

			// proceed with state machine (called in the beginning)
			
			switch (state) 
			{
			case 0:
				request(UUID_CHAR_REALTIME_STEPS);
				break;
			case 1:
				request(UUID_CHAR_BATTERY);
				break;
			case 2:
				request(UUID_CHAR_DEVICE_NAME);
				break;
			case 3:
				request(UUID_CHAR_LE_PARAMS);
				break;
			default:
				mGatt.readRemoteRssi();
				break;
			}
			
			// handle value
			if (characteristic.getUuid().equals(UUID_CHAR_REALTIME_STEPS))
			{
				mMiBand.setSteps(0xff & b[0] | (0xff & b[1]) << 8);
			}
			else if (characteristic.getUuid().equals(UUID_CHAR_BATTERY)) 
			{
				Battery battery = Battery.fromByte(b);
				mMiBand.setBattery(battery);  
			}
			else if (characteristic.getUuid().equals(UUID_CHAR_DEVICE_NAME)) 
			{
				mMiBand.setName(new String(b));
			}
			else if (characteristic.getUuid().equals(UUID_CHAR_LE_PARAMS)) 
			{
				LeParams params = LeParams.fromByte(b);
				mMiBand.setLeParams(params);
			}
			
			state++;

		}
	};
	
	private BluetoothGattService getMiliService() {
		return mGatt.getService(UUID_MILI_SERVICE);

	}
	
	private void pair() {

		BluetoothGattCharacteristic chrt = getMiliService().getCharacteristic(UUID_CHAR_pair);

		chrt.setValue(new byte[] { 2 });

		mGatt.writeCharacteristic(chrt);
		
		System.out.println("pair sent");
	}

	private void request(UUID what) {
		mGatt.readCharacteristic(getMiliService().getCharacteristic(what));	
	}

	@Override
	public void update(Observable observable, Object data) 
	{
			CommonUtils.LogWuwei("bandInfo", "name:"+mMiBand.mName+" steps is "+mMiBand.mSteps+" btAddress"+mMiBand.mBTAddress
					+"  \nbattery"+mMiBand.mBattery.mBatteryLevel+" rssi is"+mMiBand.mRssi);	
	}
}
