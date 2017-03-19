package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster.OpenHelper;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;
import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.support.greenDao.MenuTable;
import com.huofu.RestaurantOS.support.greenDao.MenuTableDao;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class ClientMenu extends Activity{
	
	private static String tag = "ClientMenu";
	public static String pathDataBase = Environment.getExternalStorageDirectory()+File.separator+"huofu"+ File.separator+"data"+ File.separator; 
	public static String pathFonts =  Environment.getExternalStorageDirectory()+File.separator+"huofu"+ File.separator+"fonts"+ File.separator;
	public static Context ctxt;
	public static String ip_address = "";
	
	public static ListView listviewChooseMenuType;
	private ArrayList<String>  listChooseMenuType;
	private LinearLayout linearLayoutAll;
    private WindowManager QManager;
    public String themeText = "（早点）";
	public String tips = "微信关注“五味”自助下单每天减1元";
	public static JSONObject json_menu = null;
	public static int width_screen = 0;
	public static int height_screen = 0;
	public static AlertDialog.Builder builder_no_view;
	public static int launch_choice = -1;//午餐菜单1-5代表周一到周五
	public static int dinner_choice = -1;//晚餐菜单1-5代表周一到周五
	public static String mondayDate = "";
	public static String tuesdayDate = "";
	public static String wednesdayDate = "";
	public static String thursdayDate = "";
	public static String fridayDate = "";
	
	public static String text_size="28";
	public static String theme_size="45";
	public static String tips_size="35";
	
	public static String distance_level_0 = "180";
	public static String distance_level_1 = "230";
	public static String distance_level_2 = "320";
	public static String distance_level_3 = "350";
	
	public static  boolean flag_load_module_new_menu = false;
	public static AlertDialog.Builder builderSetMenuIp;
	

	public static DaoMaster daoMaster;
	public static  DaoSession daoSession;
	public static MenuTableDao menuTableDao;
	public static MenuDetailDao menuDetailDao;
	
	public static boolean whetherUpdateAll = false;
	
    
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.client_menu);
		//ResideMenu.resideMenuSetWuWei(this);
		initClientMenu();
		widgetConfigureClientMenu();
	}
	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.client_main, menu);
			return super.onCreateOptionsMenu(menu);
		}
		
		private  void initClientMenu()
		{
			MainApplication.setmActivity(this);
			ctxt = getApplicationContext();
			flag_load_module_new_menu = false;
			
			daoMaster = getDaoMaster(getApplicationContext());
			daoSession = getDaoSession(getApplicationContext());
	        
	        menuTableDao = daoSession.getMenuTableDao();
	        menuDetailDao = daoSession.getMenuDetailDao();
	        
	        builderSetMenuIp =  new AlertDialog.Builder(this);//新建一个Alertdialog的builder
			builder_no_view =  new AlertDialog.Builder(this);
			width_screen = CommonUtils.getScreenWidth(ctxt);
			height_screen = CommonUtils.getScreenHeight(ctxt);
			CommonUtils.LogWuwei(tag, "width is "+width_screen+" and height is "+height_screen);
			json_menu = new JSONObject();
			listviewChooseMenuType = (ListView)findViewById(R.id.listview_choose_menu_type);
			listChooseMenuType =  new ArrayList<String>();
			ip_address = LocalDataDeal.readFromLocalMenuIp(ctxt);
			if(ip_address == null || ip_address == "")
			{
				HandlerUtils.showToast(ctxt, "请设置准备做为菜单使用的小米电视ip");
			}
			CommonUtils.getWeekOfDate();
		}
		
		private void widgetConfigureClientMenu()
		{
			CommonUtils.LogWuwei(tag, "widgetConfigureClientMenu");

			ArrayList<HashMap<String,Object>> menu_items = new ArrayList<HashMap<String,Object>>(); 
			
			HashMap<String,Object> menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "早餐");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "午餐");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "晚餐");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "存储到电视一周的菜单");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "设置");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "字体大小设置");
			menu_items.add(menu_item);
			

			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "包场菜单");
			menu_items.add(menu_item);
			
			menu_item= new HashMap<String, Object>();
			menu_item.put("menuName", "包场欢迎语");
			menu_items.add(menu_item);
			
			SimpleAdapter simple = new SimpleAdapter(this, menu_items,R.layout.listview_menu_item,new String[]{"menuName"} ,new int[]{R.id.button_menu_item});
			listviewChooseMenuType.setAdapter(simple);
			
			CommonUtils.LogWuwei(tag, "before setonitemclicklitner");
			listviewChooseMenuType.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					CommonUtils.LogWuwei(tag,arg2+"");
					chooseMenuType(arg2);
				}
			});
			CommonUtils.LogWuwei(tag, "after setonitemclicklitner");
		}
		
		/*
		 * 点击listview时判断是早餐、午餐、晚餐、设置ip、字体设置等
		 */
		public  void chooseMenuType(int choice)
		{
			//choice = 0;//0早餐 1午餐 2晚餐 3包场
			switch(choice)
			{
			case 0:
				modifyBreakfastMenu();
				break;
			case 1:
				modifyLaunchMenu();
				break;
			case 2:
				modifyDinnerMenu();
				break;
			case 3:
				modifyOtherMenu();
				break;
			case 4:
				//NetWorkUtils.menuSetting();
				break;
			case 5:
				menuFontSetting();
				break;
			case 6:
				diyMenu(0,1);
				break;
			case 7:
				diyWelcome(0,1);
				break;
			default:
				CommonUtils.LogWuwei(tag,"chooseMenuType wrong");
			}
			
		}
		
		public void menuFontSetting()
		{
			
			final String tmpStrTheme = theme_size;
			final String tmpStrTips = tips_size;
			final String tmpStrNormalText = text_size;
			
		      // TODO Auto-generated method stub
		      LayoutInflater factory = LayoutInflater.from(ClientMenu.this);
		      final View DialogView = factory.inflate(R.layout.client_menu_font_setting, null);
		      AlertDialog dlg = new AlertDialog.Builder(ClientMenu.this)
		        .setTitle("菜单字体大小设置")
		        .setView(DialogView)
		        .setPositiveButton("确定",
		          new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(
		             DialogInterface dialog,
		             int which) {
		            // TODO Auto-generated method
		        	   
		           }
		          })
		        .setNegativeButton("取消",
		          new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(
		             DialogInterface dialog,
		             int which) {
		            // TODO Auto-generated method
		            // stub
				        	  theme_size = tmpStrTheme;
				        	  tips_size = tmpStrTips;
				        	  text_size = tmpStrNormalText;
						dialog.dismiss();
		           }
		          }).create();
		    
		      dlg.show();
			
		      EditText et_theme = (EditText)dlg.findViewById(R.id.editTextThememFont);
		      EditText et_tips = (EditText)dlg.findViewById(R.id.editTextTipsFont);
		      EditText et_normal_text = (EditText)dlg.findViewById(R.id.editTextNormalItemFont);
		      
		      et_theme.addTextChangedListener(new TextWatcher() {
				
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
					theme_size = s.toString();
				}
			});
		      
		      et_tips.addTextChangedListener(new TextWatcher() {
				
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
					tips_size = s.toString();
				}
			});
		      
		      et_normal_text.addTextChangedListener(new TextWatcher() {
				
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
					text_size = s.toString();
				}
			});
		
		}
		
		
		
		/*
		 * 点击早餐的item时触发事件
		 */
		public  void modifyBreakfastMenu()
		{
			builder_no_view.setTitle("选择早餐菜单");
			builder_no_view.setMessage("五味，放心吃（早点）");
			builder_no_view.setPositiveButton("电视端预览",new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						makeMondayBreakfastMenuJson(0);
						dialog.dismiss();
					}
				});
			builder_no_view.setNeutralButton("存储到电视", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					makeMondayBreakfastMenuJson(0);
					dialog.dismiss();
				}
			});
			
            	AlertDialog alert = builder_no_view.create();
            	CommonUtils.LogWuwei(tag, "before  show");
            	alert.show();
            	CommonUtils.LogWuwei(tag, "after  show");
		}
		
		/*
		 * 显示午餐菜单
		 */
		public  void modifyLaunchMenu()
		{
			
			
			new AlertDialog.Builder(this)
			.setTitle("选择午餐菜单")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setSingleChoiceItems(
				     new String[] {"周一", "周二","周三","周四","周五"}, -1,
				     new DialogInterface.OnClickListener() {
				    	 public void onClick(DialogInterface dialog, int which) {
				    		 CommonUtils.LogWuwei(tag, "you choose "+which);
				    		 launch_choice = which+1;
				     }
				    })
			.setNegativeButton("存储到电视", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(launch_choice > 0 && launch_choice <6)
					{
						makeLaunchMenuJson(launch_choice,1);
						dialog.dismiss();
						launch_choice = -1;
					}
					else
					{
						HandlerUtils.showToast(ctxt, "请先选择午餐菜单");
					}
				}
			})
			.setPositiveButton("电视端预览", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(launch_choice > 0 && launch_choice <6)
					{
						makeLaunchMenuJson(launch_choice,0);
						dialog.dismiss();
						launch_choice = -1;
					}
					else
					{
						HandlerUtils.showToast(ctxt, "请先选择午餐菜单");
					}
					
				}
			})
			.show();
		}
		
		/*
		 * 显示晚餐菜单
		 */
		public  void modifyDinnerMenu()
		{
		
			new AlertDialog.Builder(this)
			.setTitle("选择晚餐菜单")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setSingleChoiceItems(
				     new String[] {"周一", "周二","周三","周四","周五"}, -1,
				     new DialogInterface.OnClickListener() {
				    	 public void onClick(DialogInterface dialog, int which) {
				    		 CommonUtils.LogWuwei(tag, "you choose "+which);
				    		 dinner_choice = which+1;
				     }
				    })
			.setNegativeButton("存储到电视", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(dinner_choice > 0 && dinner_choice <6)
					{
						makeDinnerMenuJson(dinner_choice,1);
						dialog.dismiss();
						dinner_choice = -1;
					}
					else
					{
						HandlerUtils.showToast(ctxt, "请先选择晚餐菜单");
					}
					
				}
			})
			.setPositiveButton("电视端预览", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(dinner_choice > 0 && dinner_choice <6)
					{
						makeDinnerMenuJson(dinner_choice,0);
						dialog.dismiss();
						dinner_choice = -1;
					}
					else
					{
						HandlerUtils.showToast(ctxt, "请先选择晚餐菜单");
					}
					
				}
			})
			.show();
			
		}
				
		/*
		 * 存储到电视
		 */
		public  void modifyOtherMenu()
		{
			
			builder_no_view.setTitle("存储到电视");
			builder_no_view.setMessage("五味，放心吃（本周菜单）");
			builder_no_view.setPositiveButton("取消",new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
			builder_no_view.setNeutralButton("存储到电视", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					whetherUpdateAll = true;
					updateDayDate(0);
					
					try {
						Thread.sleep(2000);
						
//						makeBreakfastMenuJson(1);
						makeMondayBreakfastMenuJson(1);
						Thread.sleep(2000);
						
						makeTuesdayBreakfastMenuJson(1);
						Thread.sleep(2000);
						
						makeWednesdayBreakfastMenuJson(1);
						Thread.sleep(2000);
						
						makeThursdayBreakfastMenuJson(1);
						Thread.sleep(2000);
						
						makeFridayBreakfastMenuJson(1);
						Thread.sleep(2000);
						
						makeLaunchMenuJson(1, 1);
						Thread.sleep(2000);
						
						makeLaunchMenuJson(2, 1);
						Thread.sleep(2000);
						
						makeLaunchMenuJson(3, 1);
						Thread.sleep(2000);
						
						makeLaunchMenuJson(4, 1);
						Thread.sleep(2000);
						
						makeLaunchMenuJson(5, 1);
						Thread.sleep(2000);
						
						
						makeDinnerMenuJson(1, 1);
						Thread.sleep(2000);
						
						makeDinnerMenuJson(2, 1);
						Thread.sleep(2000);
						
						makeDinnerMenuJson(3, 1);
						Thread.sleep(2000);
						
						makeDinnerMenuJson(4, 1);
						Thread.sleep(2000);
						
						makeDinnerMenuJson(5, 1);
						Thread.sleep(2000);
						
				/*		diyMenu(1,0);
						Thread.sleep(2000);
						
						diyMenu(1,1);
						Thread.sleep(2000);
						
						diyMenu(1,2);
						Thread.sleep(2000);
						
						diyWelcome(1, 0);
						Thread.sleep(2000);

						diyWelcome(1, 1);
						Thread.sleep(2000);
						
						diyWelcome(1, 2);
						*/
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					whetherUpdateAll = false;
					dialog.dismiss();
				}
			});
			
            	AlertDialog alert = builder_no_view.create();
            	CommonUtils.LogWuwei(tag, "before  show");
            	alert.show();
            	CommonUtils.LogWuwei(tag, "after  show");
			
		}

		/*
		 * 用户选择午餐具体哪一天
		 */
		public static void makeLaunchMenuJson(int date_choice,int save_choice)
		{
			switch (date_choice)
			{
			case 1:
				mondayLaunchMenu(save_choice);
				break;
			case 2:
				tuesdayLaunchMenu(save_choice);
				break;
			case 3:
				wednesdayLaunchMenu(save_choice);
				break;
			case 4:
				thursdayLaunchMenu(save_choice);
				break;
			case 5:
				fridayLaunchMenu(save_choice);
				break;
			default :
				CommonUtils.LogWuwei(tag, "wrong choice in makeLaunchMenuJson ");
			}
		}
		
		/*
		 * 用户选择晚餐具体哪一天
		 */
		public static void makeDinnerMenuJson(int date_choice,int save_choice)
		{
			switch (date_choice)
			{
			case 1:
				mondayDinnerMenu(save_choice);
				break;
			case 2:
				tuesdayDinnerMenu(save_choice);
				break;
			case 3:
				wednesdayDinnerMenu(save_choice);
				break;
			case 4:
				thursdayDinnerMenu(save_choice);
				break;
			case 5:
				fridayDinnerMenu(save_choice);
				break;
			default :
				CommonUtils.LogWuwei(tag, "wrong choice in makeLaunchMenuJson ");
			}
			
		}
	
	
/*
 * *********************************************早餐json*******************************************
 */
		
		public static  void makeMondayBreakfastMenuJson(int choice)//周一早餐
		{
				CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String date_id = CommonUtils.getNowDateString()+0;
				int list_y = 150;
				int step = 60;
				

				if(whetherUpdateAll)
				{
					date_id = mondayDate+0;
				}
				 
				
				//步骤1：添加item到这里先定义信息
				String[] theme = {date_id,"","0","theme","（早点）","400","0","",theme_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item0 = {date_id,"","2","pic","","500","0","","","","","","0","","","","","",""};
				
				String[] item1 = {date_id,"","3","text","白菜馅饼",left_list_x,Integer.toString(list_y),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item2 = {date_id,"","4","text","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item3 = {date_id,"","5","text","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item4 = {date_id,"","6","text","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				
				
				String[] item5 = {date_id,"","7","text","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item6 = {date_id,"","8","text","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item7 = {date_id,"","9","text","五谷豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5",text_size,"#ff98fb98","0x0000",distance_level_1,"0","","","","","",""};
				
				String[] item8 = {date_id,"","10","text","猪肉大葱",right_list_x,Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item21 = {date_id,"","23","text","/猪肉白菜包子",Integer.toString(Integer.parseInt(right_list_x)+160),Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item9 = {date_id,"","11","text","   >个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item10 = {date_id,"","12","text","   >份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item11 = {date_id,"","13","text","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item12 = {date_id,"","14","text","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
			
				String[] item13 = {date_id,"","15","text","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item14 = {date_id,"","16","text","手工馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item15 = {date_id,"","17","text","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
	
				
				String[] item16 = {date_id,"","18","text","干",left_list_title_x,Integer.toString(list_y+step),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item17 = {date_id,"","19","text","货",left_list_title_x,Integer.toString(list_y+step*2),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				
				String[] item18 = {date_id,"","20","text","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item19 = {date_id,"","21","text","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				String[] item20 = {date_id,"","22","line","",left_list_title_x,Integer.toString((int) (list_y+step*3.5)),Integer.toString((int) (list_y+step*3.5)),"25","#ffffffff","#ff969696","","0","","","","","",""};
				
			
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					CommonUtils.LogWuwei(tag, "now add caption is "+item_caption[i]+item5[i]+","+item14[i]);
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
					//CommonUtils.LogWuwei(tag, "json data is "+jsonObject_array[i].toString());
				}
				
				json_menu = new JSONObject();
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e)
			{
				
			}
			sendHttpRequest(choice);
		}

		public static  void makeTuesdayBreakfastMenuJson(int choice)//周二早餐
		{
				CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String date_id = CommonUtils.getNowDateString()+0;
				int list_y = 150;
				int step = 60;
				

				if(whetherUpdateAll)
				{
					date_id = tuesdayDate+0;
				}
				 
				
				//步骤1：添加item到这里先定义信息
				//步骤1：添加item到这里先定义信息
				String[] theme = {date_id,"","0","theme","（早点）","400","0","",theme_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item0 = {date_id,"","2","pic","","500","0","","","","","","0","","","","","",""};
				
				String[] item1 = {date_id,"","3","text","白菜馅饼",left_list_x,Integer.toString(list_y),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item2 = {date_id,"","4","text","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item3 = {date_id,"","5","text","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item4 = {date_id,"","6","text","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				
				
				String[] item5 = {date_id,"","7","text","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item6 = {date_id,"","8","text","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item7 = {date_id,"","9","text","五谷豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5",text_size,"#ff98fb98","0x0000",distance_level_1,"0","","","","","",""};
				
				String[] item8 = {date_id,"","10","text","猪肉大葱",right_list_x,Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item21 = {date_id,"","23","text","/猪肉白菜包子",Integer.toString(Integer.parseInt(right_list_x)+160),Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item9 = {date_id,"","11","text","   >个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item10 = {date_id,"","12","text","   >份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item11 = {date_id,"","13","text","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item12 = {date_id,"","14","text","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
			
				String[] item13 = {date_id,"","15","text","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item14 = {date_id,"","16","text","手工馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item15 = {date_id,"","17","text","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
	
				
				String[] item16 = {date_id,"","18","text","干",left_list_title_x,Integer.toString(list_y+step),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item17 = {date_id,"","19","text","货",left_list_title_x,Integer.toString(list_y+step*2),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				
				String[] item18 = {date_id,"","20","text","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item19 = {date_id,"","21","text","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				String[] item20 = {date_id,"","22","line","",left_list_title_x,Integer.toString((int) (list_y+step*3.5)),Integer.toString((int) (list_y+step*3.5)),"25","#ffffffff","#ff969696","","0","","","","","",""};
				
				
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
		}

		public static   void makeWednesdayBreakfastMenuJson(int choice)//周三早餐
		{
				CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String date_id = CommonUtils.getNowDateString()+0;
				int list_y = 150;
				int step = 60;
				

				if(whetherUpdateAll)
				{
					date_id = wednesdayDate+0;
				}
				 
				
				//步骤1：添加item到这里先定义信息
				//步骤1：添加item到这里先定义信息
				String[] theme = {date_id,"","0","theme","（早点）","400","0","",theme_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item0 = {date_id,"","2","pic","","500","0","","","","","","0","","","","","",""};
				
				String[] item1 = {date_id,"","3","text","白菜馅饼",left_list_x,Integer.toString(list_y),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item2 = {date_id,"","4","text","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item3 = {date_id,"","5","text","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item4 = {date_id,"","6","text","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				
				
				String[] item5 = {date_id,"","7","text","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item6 = {date_id,"","8","text","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item7 = {date_id,"","9","text","五谷豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5",text_size,"#ff98fb98","0x0000",distance_level_1,"0","","","","","",""};
				
				String[] item8 = {date_id,"","10","text","猪肉大葱",right_list_x,Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item21 = {date_id,"","23","text","/猪肉白菜包子",Integer.toString(Integer.parseInt(right_list_x)+160),Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item9 = {date_id,"","11","text","   >个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item10 = {date_id,"","12","text","   >份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item11 = {date_id,"","13","text","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item12 = {date_id,"","14","text","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
			
				String[] item13 = {date_id,"","15","text","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item14 = {date_id,"","16","text","手工馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item15 = {date_id,"","17","text","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
	
				
				String[] item16 = {date_id,"","18","text","干",left_list_title_x,Integer.toString(list_y+step),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item17 = {date_id,"","19","text","货",left_list_title_x,Integer.toString(list_y+step*2),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				
				String[] item18 = {date_id,"","20","text","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item19 = {date_id,"","21","text","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				String[] item20 = {date_id,"","22","line","",left_list_title_x,Integer.toString((int) (list_y+step*3.5)),Integer.toString((int) (list_y+step*3.5)),"25","#ffffffff","#ff969696","","0","","","","","",""};
				
			
				/*no sqlite
				 * CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String text_size="25";
				String theme_size="45";
				String tips_size="35";
				int list_y = 150;
				int step = 60;
				
				//步骤1：添加item到这里先定义信息
				String[] theme = {"theme","0","五味，放心吃（早点）","400","0","","#ffffffff","0x0000",theme_size};;
				String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
				String[] item0 = {"pic","2","","400","0","","","",""};
				String[] item1 = {"text","3","白菜馅饼",left_list_x,Integer.toString(list_y),"4","#ffffffff","0x0000",text_size};
				String[] item2 = {"text","4","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4","#ffffffff","0x0000",text_size};
				String[] item3 = {"text","5","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1","#ffffffff","0x0000",text_size};
				String[] item4 = {"text","6","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4","#ffffffff","0x0000",text_size};
				
				
				String[] item5 = {"text","7","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3","#ffffffff","0x0000",text_size};
				String[] item6 = {"text","8","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3","#ffffffff","0x0000",text_size};
				String[] item7 = {"text","9","现磨豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5","#ffffffff","0x0000",text_size};
				
				String[] item8 = {"text","10","猪肉大葱/猪肉白菜包子",right_list_x,Integer.toString(list_y),"0","#ffffffff","0x0000",text_size};
				String[] item9 = {"text","11",">个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2","#ffffffff","0x0000",text_size};
				String[] item10 = {"text","12",">份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4","#ffffffff","0x0000",text_size};
				String[] item11 = {"text","13","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2","#ffffffff","0x0000",text_size};
				String[] item12 = {"text","14","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1","#ffffffff","0x0000",text_size};
				
				String[] item13 = {"text","15","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6","#ffffffff","0x0000",text_size};
				String[] item14 = {"text","16","原味馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7","#ffffffff","0x0000",text_size};
				String[] item15 = {"text","17","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6","#ffffffff","0x0000",text_size};
	
				
				String[] item16 = {"text","18","干",left_list_title_x,Integer.toString(list_y+step),"","#ffffffff","0x0000",tips_size};
				String[] item17 = {"text","19","货",left_list_title_x,Integer.toString(list_y+step*2),"","#ffffffff","0x0000",tips_size};
				
				
				String[] item18 = {"text","20","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"","#ffffffff","0x0000",tips_size};
				String[] item19 = {"text","21","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"","#ffffffff","0x0000",tips_size};
				
				String[] item20 = {"line","22","",left_list_title_x,Integer.toString((int) (list_y+step*4)),Integer.toString((int) (list_y+step*3.5)),"#ffffffff","#ff969696",text_size};
				*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
		}
 
		public static   void makeThursdayBreakfastMenuJson(int choice)//周四早餐
		{
				CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String date_id = CommonUtils.getNowDateString()+0;
				int list_y = 150;
				int step = 60;
				

				if(whetherUpdateAll)
				{
					date_id = thursdayDate+0;
				}
				 
				
				
				//步骤1：添加item到这里先定义信息
				//步骤1：添加item到这里先定义信息
				String[] theme = {date_id,"","0","theme","（早点）","400","0","",theme_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item0 = {date_id,"","2","pic","","500","0","","","","","","0","","","","","",""};
				
				String[] item1 = {date_id,"","3","text","白菜馅饼",left_list_x,Integer.toString(list_y),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item2 = {date_id,"","4","text","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item3 = {date_id,"","5","text","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item4 = {date_id,"","6","text","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				
				
				String[] item5 = {date_id,"","7","text","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item6 = {date_id,"","8","text","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item7 = {date_id,"","9","text","五谷豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5",text_size,"#ff98fb98","0x0000",distance_level_1,"0","","","","","",""};
				
				String[] item8 = {date_id,"","10","text","猪肉大葱",right_list_x,Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item21 = {date_id,"","23","text","/猪肉白菜包子",Integer.toString(Integer.parseInt(right_list_x)+160),Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item9 = {date_id,"","11","text","   >个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item10 = {date_id,"","12","text","   >份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item11 = {date_id,"","13","text","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item12 = {date_id,"","14","text","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
			
				String[] item13 = {date_id,"","15","text","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item14 = {date_id,"","16","text","手工馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item15 = {date_id,"","17","text","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
	
				
				String[] item16 = {date_id,"","18","text","干",left_list_title_x,Integer.toString(list_y+step),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item17 = {date_id,"","19","text","货",left_list_title_x,Integer.toString(list_y+step*2),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				
				String[] item18 = {date_id,"","20","text","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item19 = {date_id,"","21","text","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				String[] item20 = {date_id,"","22","line","",left_list_title_x,Integer.toString((int) (list_y+step*3.5)),Integer.toString((int) (list_y+step*3.5)),"25","#ffffffff","#ff969696","","0","","","","","",""};
				
			
				/*no sqlite
				 * CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String text_size="25";
				String theme_size="45";
				String tips_size="35";
				int list_y = 150;
				int step = 60;
				
				//步骤1：添加item到这里先定义信息
				String[] theme = {"theme","0","五味，放心吃（早点）","400","0","","#ffffffff","0x0000",theme_size};;
				String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
				String[] item0 = {"pic","2","","400","0","","","",""};
				String[] item1 = {"text","3","白菜馅饼",left_list_x,Integer.toString(list_y),"4","#ffffffff","0x0000",text_size};
				String[] item2 = {"text","4","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4","#ffffffff","0x0000",text_size};
				String[] item3 = {"text","5","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1","#ffffffff","0x0000",text_size};
				String[] item4 = {"text","6","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4","#ffffffff","0x0000",text_size};
				
				
				String[] item5 = {"text","7","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3","#ffffffff","0x0000",text_size};
				String[] item6 = {"text","8","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3","#ffffffff","0x0000",text_size};
				String[] item7 = {"text","9","现磨豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5","#ffffffff","0x0000",text_size};
				
				String[] item8 = {"text","10","猪肉大葱/猪肉白菜包子",right_list_x,Integer.toString(list_y),"0","#ffffffff","0x0000",text_size};
				String[] item9 = {"text","11",">个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2","#ffffffff","0x0000",text_size};
				String[] item10 = {"text","12",">份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4","#ffffffff","0x0000",text_size};
				String[] item11 = {"text","13","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2","#ffffffff","0x0000",text_size};
				String[] item12 = {"text","14","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1","#ffffffff","0x0000",text_size};
				
				String[] item13 = {"text","15","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6","#ffffffff","0x0000",text_size};
				String[] item14 = {"text","16","原味馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7","#ffffffff","0x0000",text_size};
				String[] item15 = {"text","17","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6","#ffffffff","0x0000",text_size};
	
				
				String[] item16 = {"text","18","干",left_list_title_x,Integer.toString(list_y+step),"","#ffffffff","0x0000",tips_size};
				String[] item17 = {"text","19","货",left_list_title_x,Integer.toString(list_y+step*2),"","#ffffffff","0x0000",tips_size};
				
				
				String[] item18 = {"text","20","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"","#ffffffff","0x0000",tips_size};
				String[] item19 = {"text","21","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"","#ffffffff","0x0000",tips_size};
				
				String[] item20 = {"line","22","",left_list_title_x,Integer.toString((int) (list_y+step*4)),Integer.toString((int) (list_y+step*3.5)),"#ffffffff","#ff969696",text_size};
				*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
		}

		public static   void makeFridayBreakfastMenuJson(int choice)//周五早餐
		{
				CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String date_id = CommonUtils.getNowDateString()+0;
				int list_y = 150;
				int step = 60;
				

				if(whetherUpdateAll)
				{
					date_id = fridayDate+0;
				}
				 
				
				//步骤1：添加item到这里先定义信息
				String[] theme = {date_id,"","0","theme","（早点）","400","0","",theme_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item0 = {date_id,"","2","pic","","500","0","","","","","","0","","","","","",""};
				
				String[] item1 = {date_id,"","3","text","白菜馅饼",left_list_x,Integer.toString(list_y),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item2 = {date_id,"","4","text","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item3 = {date_id,"","5","text","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item4 = {date_id,"","6","text","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				
				
				String[] item5 = {date_id,"","7","text","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item6 = {date_id,"","8","text","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item7 = {date_id,"","9","text","五谷豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5",text_size,"#ff98fb98","0x0000",distance_level_1,"0","","","","","",""};
				
				String[] item8 = {date_id,"","10","text","猪肉大葱",right_list_x,Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item21 = {date_id,"","23","text","/猪肉白菜包子",Integer.toString(Integer.parseInt(right_list_x)+160),Integer.toString(list_y),"0",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item9 = {date_id,"","11","text","   >个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item10 = {date_id,"","12","text","   >份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item11 = {date_id,"","13","text","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item12 = {date_id,"","14","text","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
			
				String[] item13 = {date_id,"","15","text","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item14 = {date_id,"","16","text","手工馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
				String[] item15 = {date_id,"","17","text","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"0","","","","","",""};
	
				
				String[] item16 = {date_id,"","18","text","干",left_list_title_x,Integer.toString(list_y+step),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item17 = {date_id,"","19","text","货",left_list_title_x,Integer.toString(list_y+step*2),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				
				String[] item18 = {date_id,"","20","text","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				String[] item19 = {date_id,"","21","text","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"",tips_size,"#ffffffff","0x0000","","0","","","","","",""};
				
				String[] item20 = {date_id,"","22","line","",left_list_title_x,Integer.toString((int) (list_y+step*3.5)),Integer.toString((int) (list_y+step*3.5)),"25","#ffffffff","#ff969696","","0","","","","","",""};
				
			
				/*no sqlite
				 * CommonUtils.LogWuwei(tag, "显示早餐中...");
				JSONArray content = new JSONArray();
				String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
				String left_list_x = "200";
				String right_list_x = "700";
				String left_list_title_x = "100";
				
				String text_size="25";
				String theme_size="45";
				String tips_size="35";
				int list_y = 150;
				int step = 60;
				
				//步骤1：添加item到这里先定义信息
				String[] theme = {"theme","0","五味，放心吃（早点）","400","0","","#ffffffff","0x0000",theme_size};;
				String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
				String[] item0 = {"pic","2","","400","0","","","",""};
				String[] item1 = {"text","3","白菜馅饼",left_list_x,Integer.toString(list_y),"4","#ffffffff","0x0000",text_size};
				String[] item2 = {"text","4","椒盐酥饼",left_list_x,Integer.toString(list_y+step),"4","#ffffffff","0x0000",text_size};
				String[] item3 = {"text","5","麻酱花卷",left_list_x,Integer.toString(list_y+step*2),"1","#ffffffff","0x0000",text_size};
				String[] item4 = {"text","6","无矾牛奶油条",left_list_x,Integer.toString(list_y+step*3),"4","#ffffffff","0x0000",text_size};
				
				
				String[] item5 = {"text","7","精熬白粥",left_list_x,Integer.toString((int) (list_y+step*4.8)),"3","#ffffffff","0x0000",text_size};
				String[] item6 = {"text","8","雪梨银耳汤",left_list_x,Integer.toString((int) (list_y+step*5.8)),"3","#ffffffff","0x0000",text_size};
				String[] item7 = {"text","9","现磨豆浆",left_list_x,Integer.toString((int) (list_y+step*6.8)),"5","#ffffffff","0x0000",text_size};
				
				String[] item8 = {"text","10","猪肉大葱/猪肉白菜包子",right_list_x,Integer.toString(list_y),"0","#ffffffff","0x0000",text_size};
				String[] item9 = {"text","11",">个",right_list_x,Integer.toString((int) (list_y+step*0.8)),"2","#ffffffff","0x0000",text_size};
				String[] item10 = {"text","12",">份(3个)",right_list_x,Integer.toString((int) (list_y+step*1.6)),"4","#ffffffff","0x0000",text_size};
				String[] item11 = {"text","13","茶叶蛋",right_list_x,Integer.toString((int) (list_y+step*2.5)),"2","#ffffffff","0x0000",text_size};
				String[] item12 = {"text","14","麻辣小黄瓜",right_list_x,Integer.toString((int) (list_y+step*3.3)),"1","#ffffffff","0x0000",text_size};
				
				String[] item13 = {"text","15","疙瘩汤",right_list_x,Integer.toString((int) (list_y+step*4.8)),"6","#ffffffff","0x0000",text_size};
				String[] item14 = {"text","16","原味馄饨",right_list_x,Integer.toString((int) (list_y+step*5.8)),"7","#ffffffff","0x0000",text_size};
				String[] item15 = {"text","17","豆腐脑",right_list_x,Integer.toString((int) (list_y+step*6.8)),"6","#ffffffff","0x0000",text_size};
	
				
				String[] item16 = {"text","18","干",left_list_title_x,Integer.toString(list_y+step),"","#ffffffff","0x0000",tips_size};
				String[] item17 = {"text","19","货",left_list_title_x,Integer.toString(list_y+step*2),"","#ffffffff","0x0000",tips_size};
				
				
				String[] item18 = {"text","20","水",left_list_title_x,Integer.toString((int) (list_y+step*5.5)),"","#ffffffff","0x0000",tips_size};
				String[] item19 = {"text","21","货",left_list_title_x,Integer.toString((int) (list_y+step*6.5)),"","#ffffffff","0x0000",tips_size};
				
				String[] item20 = {"line","22","",left_list_title_x,Integer.toString((int) (list_y+step*4)),Integer.toString((int) (list_y+step*3.5)),"#ffffffff","#ff969696",text_size};
				*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
		}
		
		
/*
 * *********************************************午餐json*******************************************
 */
		
		public static  void mondayLaunchMenu(int choice)//周一午餐
		{
			CommonUtils.LogWuwei(tag, "显示周一午餐中...");
			JSONArray content = new JSONArray();
//			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			String date_id = CommonUtils.getNowDateString()+1;
			int list_y = 150;
			int step = 80;
			
			if(whetherUpdateAll)
			{
				date_id = mondayDate+1;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(午餐:周一)","400","0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};;
			String[] item1 = {date_id,"","3","text","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item2 = {date_id,"","4","text","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item3 = {date_id,"","5","text","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item4 = {date_id,"","6","text","排骨香菇面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"28",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};;
			String[] item5 = {date_id,"","7","text","懒人拌面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"24",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			
			String[] item6 = {date_id,"","8","text","西红柿牛腩饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"24",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item7 = {date_id,"","9","text","肉末豆腐饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"22",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item8 = {date_id,"","10","text","炝炒土豆丝饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"16",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item9 = {date_id,"","11","text","干煸豆角饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"22",text_size,"#ff98fb98","1",distance_level_3,"1","","","","","",""};;
			
			String[] item10 = {date_id,"","12","text"," 例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item11 = {date_id,"","13","text"," 小菜",right_list_x,Integer.toString((int) (list_y+step*2.2)),"3",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item19 = {date_id,"","21","text"," 米饭",right_list_x,Integer.toString((int) (list_y+step*3.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item20 = {date_id,"","22","text"," 半碗米饭",right_list_x,Integer.toString((int) (list_y+step*4.2)),"1",text_size,"#ff98fb98","0x0000",distance_level_0,"","","","","","",""};;
	
			
			String[] item12 = {date_id,"","14","text","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item13 = {date_id,"","15","text","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
			
			
			String[] item14 = {date_id,"","16","text","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item15 = {date_id,"","17","text","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
			
			String[] item16 = {date_id,"","18","text","其他",right_list_x,Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			
			String[] item17 = {date_id,"","19","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			
			String[] item21 = {date_id,"","23","text","午餐天天变",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString(list_y-30),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
		}
	
		
		public static   void tuesdayLaunchMenu(int choice)//周二午餐
		{
			CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "985";
			String left_list_title_x = "30";
			String date_id = CommonUtils.getNowDateString()+1;
			int list_y = 150;
			int step = 80;
			

			if(whetherUpdateAll)
			{
				date_id = tuesdayDate+1;
			}
			
			//String date_id = CommonUtils.getNowDateString()+1;
			//String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color"};
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			//步骤1：添加item到这里先定义信息//130
			String[] theme = {date_id,"","0","theme","(午餐:周二)","400","0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};;
			String[] item1 = {date_id,"","3","text","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item2 = {date_id,"","4","text","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item3 = {date_id,"","5","text","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item4 = {date_id,"","6","text","三丁小炒肉面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"24",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};;
			String[] item5 = {date_id,"","7","text","肉酱面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"22",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			
			String[] item6 = {date_id,"","8","text","鸡腿饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_3,"1","","","","","",""};;
			String[] item7 = {date_id,"","9","text","口蘑炒肉片饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"26",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item8 = {date_id,"","10","text","醋溜白菜饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"18",text_size,"#ff98fb98","0x0000",distance_level_3,"","","","","","",""};;
			String[] item9 = {date_id,"","11","text","地三鲜饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"20",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
	
			String[] item10 = {date_id,"","12","text"," 例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item11 = {date_id,"","13","text"," 小菜",right_list_x,Integer.toString((int) (list_y+step*2.2)),"3",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item19 = {date_id,"","21","text"," 米饭",right_list_x,Integer.toString((int) (list_y+step*3.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item20 = {date_id,"","22","text"," 半碗米饭",right_list_x,Integer.toString((int) (list_y+step*4.2)),"1",text_size,"#ff98fb98","0x0000",distance_level_0,"","","","","","",""};;
	

			
			String[] item12 = {date_id,"","14","text","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item13 = {date_id,"","15","text","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
		
			
			String[] item14 = {date_id,"","16","text","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item15 = {date_id,"","17","text","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
			
			String[] item16 = {date_id,"","18","text","其他",right_list_x,Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item17 = {date_id,"","19","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-40),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			
			String[] item21 = {date_id,"","23","text","午餐天天变",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString(list_y-30),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			
		/*	CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(午餐:周二)","400","0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","卤肉汤面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"24","#ff98fb98","0x0000",text_size};
			String[] item5 = {"text","7","肉酱面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"22","#ffffffff","0x0000",text_size};
			
			String[] item6 = {"text","8","鸡腿饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","口蘑炒肉片",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"26","#ffffffff","0x0000",text_size};
			String[] item8 = {"text","10","番茄炒丝瓜饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"22","#ff98fb98","0x0000",text_size};
			String[] item9 = {"text","11","地三鲜饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"20","#ffffffff","0x0000",text_size};
			
			String[] item10 = {"text","12"," -例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13"," -小菜",right_list_x,Integer.toString((int) (list_y+step*2)),"3","#ffffffff","0x0000",text_size};
	
			
			String[] item12 = {"text","14","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item13 = {"text","15","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item14 = {"text","16","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item15 = {"text","17","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item16 = {"text","18","套餐",right_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"line_vertical","19","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8",Integer.toString(step),"#ff969696",text_size};
			*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}
				
		
		public static   void wednesdayLaunchMenu(int choice)//周三午餐
		{
			//String date_id = CommonUtils.getNowDateString()+1;
			//String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color"};
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			
			CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			String date_id = CommonUtils.getNowDateString()+1;
			int list_y = 150;
			int step = 80;
			

			if(whetherUpdateAll)
			{
				date_id = wednesdayDate+1;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(午餐:周三)","400","0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};;
			String[] item1 = {date_id,"","3","text","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item2 = {date_id,"","4","text","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item3 = {date_id,"","5","text","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item4 = {date_id,"","6","text","牛肉面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"24",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};;
			String[] item5 = {date_id,"","7","text","茄丁面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			
			String[] item6 = {date_id,"","8","text","红烧肉土豆饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"24",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item7 = {date_id,"","9","text","肉末四季豆饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"26",text_size,"#ff98fb98","0x0000",distance_level_3,"","","","","","",""};;
			String[] item8 = {date_id,"","10","text","鱼香茄条饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"22",text_size,"#ffffffff","1",distance_level_3,"1","","","","","",""};;
			String[] item9 = {date_id,"","11","text","西红柿鸡蛋饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"18",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			
			String[] item10 = {date_id,"","12","text"," 例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item11 = {date_id,"","13","text"," 小菜",right_list_x,Integer.toString((int) (list_y+step*2.2)),"3",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item19 = {date_id,"","21","text"," 米饭",right_list_x,Integer.toString((int) (list_y+step*3.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item20 = {date_id,"","22","text"," 半碗米饭",right_list_x,Integer.toString((int) (list_y+step*4.2)),"1",text_size,"#ff98fb98","0x0000",distance_level_0,"","","","","","",""};;
	
			
			String[] item12 = {date_id,"","14","text","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item13 = {date_id,"","15","text","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
	
			String[] item14 = {date_id,"","16","text","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item15 = {date_id,"","17","text","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
			
			String[] item16 = {date_id,"","18","text","其他",right_list_x,Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item17 = {date_id,"","19","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			
			String[] item21 = {date_id,"","23","text","午餐天天变",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString(list_y-30),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			
			
			
		/*	CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味,放心吃(午餐:周三)","400","0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","排骨香菇面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"28","#ff98fb98","0x0000",text_size};
			String[] item5 = {"text","7","茄丁面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item6 = {"text","8","红烧肉土豆饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"24","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","酱爆鸡丁饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"24","#ff98fb98","0x0000",text_size};
			String[] item8 = {"text","10","鱼香茄条饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"22","#ff98fb98","0x0000",text_size};
			String[] item9 = {"text","11","西红柿鸡蛋饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item10 = {"text","12"," -例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13"," -小菜",right_list_x,Integer.toString((int) (list_y+step*2)),"3","#ffffffff","0x0000",text_size};
	
			
			String[] item12 = {"text","14","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item13 = {"text","15","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item14 = {"text","16","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item15 = {"text","17","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item16 = {"text","18","套餐",right_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"line_vertical","19","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8",Integer.toString(step),"#ff969696",text_size};
			
			*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		
		}
		
		
		public static   void thursdayLaunchMenu(int choice)//周四午餐
		{
			//String date_id = CommonUtils.getNowDateString()+1;
			//String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color"};
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			String date_id = CommonUtils.getNowDateString()+1;
			int list_y = 150;
			int step = 80;
			
			

			if(whetherUpdateAll)
			{
				date_id = thursdayDate+1;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(午餐:周四)","400","0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};;
			String[] item1 = {date_id,"","3","text","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item2 = {date_id,"","4","text","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item3 = {date_id,"","5","text","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item4 = {date_id,"","6","text","西红柿牛腩面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"28",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			String[] item5 = {date_id,"","7","text","什锦打卤面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"22",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};;
			
			String[] item6 = {date_id,"","8","text","冬瓜排骨饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"28",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item7 = {date_id,"","9","text","娃娃菜小炒肉饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"24",text_size,"#ff98fb98","0x0000",distance_level_3,"","","","","","",""};;
			String[] item8 = {date_id,"","10","text","香菇油菜饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"20",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};;
			String[] item9 = {date_id,"","11","text","醋溜白菜饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"18",text_size,"#ff98fb98","0x0000",distance_level_3,"","","","","","",""};;
			
			String[] item10 = {date_id,"","12","text"," 例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item11 = {date_id,"","13","text"," 小菜",right_list_x,Integer.toString((int) (list_y+step*2.2)),"3",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item19 = {date_id,"","21","text"," 米饭",right_list_x,Integer.toString((int) (list_y+step*3.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};;
			String[] item20 = {date_id,"","22","text"," 半碗米饭",right_list_x,Integer.toString((int) (list_y+step*4.2)),"1",text_size,"#ff98fb98","0x0000",distance_level_0,"","","","","","",""};;
	
			
			String[] item12 = {date_id,"","14","text","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item13 = {date_id,"","15","text","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
		
			String[] item14 = {date_id,"","16","text","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item15 = {date_id,"","17","text","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};;
			
			String[] item16 = {date_id,"","18","text","其他",right_list_x,Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			String[] item17 = {date_id,"","19","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};;
			
			String[] item21 = {date_id,"","23","text","午餐天天变",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString(list_y-30),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};;
			
			/*CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "30";
			String middle_list_x = "500";
			String right_list_x = "1000";
			String left_list_title_x = "30";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味,放心吃(午餐:周四)","400","0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2.2)),"12","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*3.2)),"21","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","西红柿牛腩面",left_list_x,Integer.toString((int) (list_y+step*4.2)),"28","#ff98fb98","0x0000",text_size};
			String[] item5 = {"text","7","什锦打卤面",left_list_x,Integer.toString((int) (list_y+step*5.2)),"22","#ffffffff","0x0000",text_size};
			
			String[] item6 = {"text","8","冬瓜炖排骨饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"28","#ff98fb98","0x0000",text_size};
			String[] item7 = {"text","9","鸡丁饭",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"20","#ffffffff","0x0000",text_size};
			String[] item8 = {"text","10","香菇油菜饭",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"20","#ffffffff","0x0000",text_size};
			String[] item9 = {"text","11","剁椒鸡蛋饭",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"22","#ffffffff","0x0000",text_size};
			
			String[] item10 = {"text","12"," -例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13"," -小菜",right_list_x,Integer.toString((int) (list_y+step*2)),"3","#ffffffff","0x0000",text_size};

			
			String[] item12 = {"text","14","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item13 = {"text","15","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item14 = {"text","16","饭类",Integer.toString(Integer.parseInt(middle_list_x)-60),Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item15 = {"text","17","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+30),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item16 = {"text","18","套餐",right_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"line_vertical","19","",Integer.toString(Integer.parseInt(middle_list_x)-125),Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(right_list_x)-60),Integer.toString((int) (list_y)),"8",Integer.toString(step),"#ff969696",text_size};
			//步骤2：实例化
*/			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();	
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
			
		}
		
	
		public static   void fridayLaunchMenu(int choice)//周五午餐
		{
			//String date_id = CommonUtils.getNowDateString()+1;
			//String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color"};
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};	
			//	String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "30";
			String middle_list_x = "450";
			String right_list_x = "990";
			String left_list_title_x = "30";
			String date_id = CommonUtils.getNowDateString()+1;
			int list_y = 150;
			int step = 80;
			
			
			
			if(whetherUpdateAll)
			{
				date_id = fridayDate+1;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(午餐:周五)","400","0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			String[] item1 = {date_id,"","3","text","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*2.8)),"21",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","猪肉白菜/",left_list_x,Integer.toString((int) (list_y+step*3.8)),"0",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item5 = {date_id,"","7","text","   >小(15个)",left_list_x,Integer.toString((int) (list_y+step*4.6)),"22",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","   >大(20个)",left_list_x,Integer.toString((int) (list_y+step*5.4)),"28",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item7 = {date_id,"","9","text", "  酱香卤肉饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"26",text_size,"#ff98fb98","0x0000",distance_level_3,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","  土豆炖鸡块饭",middle_list_x,Integer.toString((int) (list_y+step*2.4)),"24",text_size,"#ffffffff","1",distance_level_3,"1","","","","","",""};
			String[] item9 = {date_id,"","11","text","  包菜炒番茄饭",middle_list_x,Integer.toString((int) (list_y+step*3.6)),"20",text_size,"#ffffffff","0x0000",distance_level_3,"","","","","","",""};
			
			
			String[] item10 = {date_id,"","12","text"," 例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};
			String[] item11 = {date_id,"","13","text"," 小菜",right_list_x,Integer.toString((int) (list_y+step*2.2)),"3",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};
			String[] item21 = {date_id,"","23","text"," 米饭",right_list_x,Integer.toString((int) (list_y+step*3.2)),"2",text_size,"#ffffffff","0x0000",distance_level_0,"","","","","","",""};
			String[] item22 = {date_id,"","24","text"," 半碗米饭",right_list_x,Integer.toString((int) (list_y+step*4.2)),"1",text_size,"#ff98fb98","0x0000",distance_level_0,"","","","","","",""};
			
			
			String[] item12 = {date_id,"","14","text","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item13 = {date_id,"","15","text","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y+40)),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			
			String[] item14 = {date_id,"","16","text","饭类",Integer.toString(Integer.parseInt(middle_list_x)),Integer.toString((int) (list_y)+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item15 = {date_id,"","17","text","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+90),Integer.toString((int) (list_y)+40),"",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			
			String[] item16 = {date_id,"","18","text","其他",right_list_x,Integer.toString(list_y+40),"",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","韭菜鸡蛋",Integer.toString(Integer.parseInt(left_list_x)+160),Integer.toString((int) (list_y+step*3.8)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item18 = {date_id,"","20","text","饺子",Integer.toString(Integer.parseInt(left_list_x)+305),Integer.toString((int) (list_y+step*3.8)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-40),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item20 = {date_id,"","22","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-50),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			
			String[] item23 = {date_id,"","25","text","午餐天天变",Integer.toString(Integer.parseInt(middle_list_x)+60),Integer.toString(list_y-30),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			
			//步骤2：实例化
			
			
			/*CommonUtils.LogWuwei(tag, "显示午餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "30";
			String middle_list_x = "450";
			String right_list_x = "990";
			String left_list_title_x = "30";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味,放心吃(午餐:周五)","400","0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元","900","600","1","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉夹馍",left_list_x,Integer.toString((int) (list_y+step*1.2)),"9","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","   >小套餐",left_list_x,Integer.toString((int) (list_y+step*2)),"12","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","   >大套餐",left_list_x,Integer.toString((int) (list_y+step*2.8)),"21","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","猪肉芹菜/",left_list_x,Integer.toString((int) (list_y+step*3.8)),"0","#ffffffff","0x0000",text_size};
			String[] item5 = {"text","7","   >小(15个)",left_list_x,Integer.toString((int) (list_y+step*4.6)),"22","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","   >大(20个)",left_list_x,Integer.toString((int) (list_y+step*5.4)),"28","#ffffffff","0x0000",text_size};
			
			String[] item7 = {"text","9", "  酱香卤肉饭",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"26","#ff98fb98","0x0000",text_size};
			String[] item8 = {"text","10","  土豆炖鸡块饭",middle_list_x,Integer.toString((int) (list_y+step*2.4)),"24","#ffffffff","0x0000",text_size};
			String[] item9 = {"text","11","  包菜炒番茄饭",middle_list_x,Integer.toString((int) (list_y+step*3.6)),"20","#ffffffff","0x0000",text_size};
			
			String[] item10 = {"text","12"," -例汤",right_list_x,Integer.toString((int) (list_y+step*1.2)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13"," -小菜",right_list_x,Integer.toString((int) (list_y+step*2)),"3","#ffffffff","0x0000",text_size};

			
			String[] item12 = {"text","14","面食",Integer.toString(Integer.parseInt(left_list_x)-10),Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item13 = {"text","15","(套餐+3元）",Integer.toString(Integer.parseInt(left_list_x)+80),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item14 = {"text","16","饭类",Integer.toString(Integer.parseInt(middle_list_x)),Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item15 = {"text","17","(可双拼，套餐+3元)",Integer.toString(Integer.parseInt(middle_list_x)+90),Integer.toString((int) (list_y)),"","#ff98fb98","0x0000",tips_size};
			
			String[] item16 = {"text","18","套餐",right_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","韭菜鸡蛋",Integer.toString(Integer.parseInt(left_list_x)+150),Integer.toString((int) (list_y+step*3.8)),"0","#ff98fb98","0x0000",text_size};
			String[] item18 = {"text","20","饺子",Integer.toString(Integer.parseInt(left_list_x)+280),Integer.toString((int) (list_y+step*3.8)),"0","#ffffffff","0x0000",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			String[] item20 = {"line_vertical","22","",Integer.toString(Integer.parseInt(right_list_x)-50),Integer.toString((int) (list_y)),"8",Integer.toString(step),"#ff969696",text_size};
			//步骤2：实例化
			 */			JSONObject json_theme = new JSONObject();
			 JSONObject json_tips = new JSONObject();
			 JSONObject json_item0 = new JSONObject();
			 JSONObject json_item1 = new JSONObject();
			 JSONObject json_item2 = new JSONObject();
			 JSONObject json_item3 = new JSONObject();
			 JSONObject json_item4 = new JSONObject();
			 JSONObject json_item5 = new JSONObject();
			 JSONObject json_item6 = new JSONObject();
			 JSONObject json_item7 = new JSONObject();
			 JSONObject json_item8 = new JSONObject();
			 JSONObject json_item9 = new JSONObject();
			 JSONObject json_item10 = new JSONObject();
			 JSONObject json_item11 = new JSONObject();
			 JSONObject json_item12 = new JSONObject();
			 JSONObject json_item13 = new JSONObject();
			 JSONObject json_item14 = new JSONObject();
			 JSONObject json_item15 = new JSONObject();
			 JSONObject json_item16 = new JSONObject();
			 JSONObject json_item17 = new JSONObject();
			 JSONObject json_item18 = new JSONObject();
			 JSONObject json_item19 = new JSONObject();
			 JSONObject json_item20 = new JSONObject();
			 JSONObject json_item21 = new JSONObject();
			 JSONObject json_item22 = new JSONObject();
			 JSONObject json_item23 = new JSONObject();
			 
			 //步骤3：添加到数组
			 JSONObject[] jsonObject_array = {
					 json_theme,
					 json_tips,
					 json_item0,
					 json_item1,
					 json_item2,
					 json_item3,
					 json_item4,
					 json_item5,
					 json_item6,
					 json_item7,
					 json_item8,
					 json_item9,
					 json_item10,
					 json_item11,
					 json_item12,
					 json_item13,
					 json_item14,
					 json_item15,
					 json_item16,
					 json_item17,
					 json_item18,
					 json_item19,
					 json_item20,
					 json_item21,
					 json_item22,
					 json_item23
			 };
			 
			 try {
				 int i = 0;
				 //步骤4:添加到jsonarray
				 for(i=0;i<item_caption.length;i++)
				 {
					 json_theme.put(item_caption[i], theme[i]);
					 json_tips.put(item_caption[i], tips[i]);
					 json_item0.put(item_caption[i], item0[i]);
					 json_item1.put(item_caption[i], item1[i]);
					 json_item2.put(item_caption[i], item2[i]);
					 json_item3.put(item_caption[i], item3[i]);
					 json_item4.put(item_caption[i], item4[i]);
					 json_item5.put(item_caption[i], item5[i]);
					 json_item6.put(item_caption[i], item6[i]);
					 json_item7.put(item_caption[i], item7[i]);
					 json_item8.put(item_caption[i], item8[i]);
					 json_item9.put(item_caption[i], item9[i]);
					 json_item10.put(item_caption[i], item10[i]);
					 json_item11.put(item_caption[i], item11[i]);
					 json_item12.put(item_caption[i], item12[i]);
					 json_item13.put(item_caption[i], item13[i]);
					 json_item14.put(item_caption[i], item14[i]);
					 json_item15.put(item_caption[i], item15[i]);
					 json_item16.put(item_caption[i], item16[i]);
					 json_item17.put(item_caption[i], item17[i]);
					 json_item18.put(item_caption[i], item18[i]);
					 json_item19.put(item_caption[i], item19[i]);
					 json_item20.put(item_caption[i], item20[i]);
					 json_item21.put(item_caption[i], item21[i]);
					 json_item22.put(item_caption[i], item22[i]);
					 json_item23.put(item_caption[i], item23[i]);
				 }
				 
				 for(i=0;i<jsonObject_array.length;i++)
				 {
					 content.put(jsonObject_array[i]);	
				 }
				 
				 json_menu.put("menu",content);
				 
			 } catch (JSONException e) {
				 // TODO Auto-generated catch block
				 CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				 e.printStackTrace();
			 }
			 sendHttpRequest(choice);
			 
		}
		
		
		public static   void diyMenu(int whether_save,int show_time)
		{
			CommonUtils.LogWuwei(tag, "显示早餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "200";
			String right_list_x = "700";
			String left_list_title_x = "100";
			
			String date_id = "20141129"+show_time;
			int list_y = 150;
			int step = 60;
			
			String text_size="30";
			String theme_size="35";
			String tips_size="28";
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","五味,放心吃","400","0","",theme_size,"#ffffffff","0x0000","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元","900","600","1",tips_size,"#ffffffff","0x0000","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","",""};
			String[] item1 = {date_id,"","3","text","1、肉夹馍套餐",left_list_x,Integer.toString(list_y),"",text_size,"#ffffffff","0x0000",distance_level_1,""};
			String[] item2 = {date_id,"","4","text","2、懒人拌面套餐",left_list_x,Integer.toString((int) (list_y+step*1.5)),"",text_size,"#ffffffff","0x0000",distance_level_1,""};
			String[] item3 = {date_id,"","5","text","3、醋溜土豆丝饭套餐",left_list_x,Integer.toString(list_y+step*3),"",text_size,"#ffffffff","0x0000",distance_level_1,""};
			String[] item4 = {date_id,"","6","text","4、西红柿牛腩拼土豆丝饭",left_list_x,Integer.toString((int) (list_y+step*4.5)),"",text_size,"#ffffffff","0x0000",distance_level_1,""};
			String[] item5 = {date_id,"","7","text","5、娃娃菜小炒肉拼土豆丝饭",left_list_x,Integer.toString((int) (list_y+step*6)),"",text_size,"#ffffffff","0x0000",distance_level_1,""};
			
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(whether_save);
		}
		
		
		public static void diyWelcome(int whether_save,int show_time)
		{
			
			JSONArray content = new JSONArray();
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "160";
			String right_list_x = "330";
			String left_list_title_x = "100";
			
			String date_id = "20141130"+show_time;
			int list_y = 150;
			int step = 60;
			
			String text_size="80";
			String theme_size="35";
			String tips_size="35";
			
			
			
			//步骤1：添加item到这里先定义信息
			String[] item1 = {date_id,"","1","text","恩施高中北京校友会",left_list_x,Integer.toString((int) (list_y*1)),"",text_size,"#ffffffff","0x0000","",""};
			//String[] item2 = {date_id,"","2","text"," 2014迎新会",right_list_x,Integer.toString((int) (list_y+step*2.5)),"",text_size,"#ffffffff","0x0000"};
			String[] item2 = {date_id,"","2","text","迎新会","620",Integer.toString((int) (list_y+step*2.5)),"",text_size,"#ffffffff","0x0000","",""};
			String[] item3 = {date_id,"","3","text"," 2014",right_list_x,Integer.toString((int) (list_y+step*2.5)),"",text_size,"#ffffffff","0x0000","",""};
			String[] item4 =  {date_id,"","4","text","   2014.11.29","930","600","0",tips_size,"#ffffffff","0x0000","",""};
			
			//步骤2：实例化
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_item1,
					json_item2,
					json_item3,
					json_item4
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(whether_save);
		}
		
		
/*
 * *********************************************晚餐json*******************************************
 */
		
		public static   void mondayDinnerMenu(int choice)//周一晚餐
		{
			//String date_id = CommonUtils.getNowDateString()+1;
			//String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color"};
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			String date_id = CommonUtils.getNowDateString()+2;
			int list_y = 150;
			int step = 80;
			

			if(whetherUpdateAll)
			{
				date_id = mondayDate+2;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(晚餐:周一)",left_list_x,"0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			String[] item1 = {date_id,"","3","text","红椒鸡柳",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","木须肉",left_list_x,Integer.toString((int) (list_y+step*2.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","回锅腊肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"28",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item5 = {date_id,"","7","text","葱花脂油饼",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","锅贴(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item7 = {date_id,"","9","text","懒龙(2个)",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"8",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","鸡蛋羹",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item9 = {date_id,"","11","text","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item10 = {date_id,"","12","text","半碗米饭",right_list_x,Integer.toString((int) (list_y+step*1.7)),"1",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item11 = {date_id,"","13","text","米饭",right_list_x,Integer.toString((int) (list_y+step*2.4)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item23 = {date_id,"","25","text","戗面馒头",right_list_x,Integer.toString((int) (list_y+step*3.1)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item12 = {date_id,"","14","text","香芹鸡蛋干",right_list_x,Integer.toString((int) (list_y+step*3.8)),"6",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item13 = {date_id,"","15","text","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.5)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item14 = {date_id,"","16","text","小米南瓜粥",right_list_x,Integer.toString((int) (list_y+step*5.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
		
			
			
			String[] item15 = {date_id,"","17","text","小炒",left_list_x,Integer.toString(list_y),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item16 = {date_id,"","18","text","小吃",middle_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","主食及其他",right_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","#ffffff","","","","","","","",""};
			
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			//String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7","25",Integer.toString(step),"#ff969696
			//String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696"};
			
			String[] item20 = {date_id,"","22","text","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item21 = {date_id,"","23","text","9元",Integer.toString(Integer.parseInt(left_list_x)+185),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item22 = {date_id,"","24","text","(米饭+小菜+鸡蛋羹+粥)",Integer.toString(Integer.parseInt(left_list_x)+245),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			//步骤2：实例化
			
			
			/*CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(晚餐:周一)",left_list_x,"0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","红椒鸡柳",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","木须肉",left_list_x,Integer.toString((int) (list_y+step*2.2)),"20","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","孜然羊肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"28","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item5 = {"text","7","葱花脂油饼",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"6","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","锅贴(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","懒龙(2个)",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"8","#ff98fb98","0x0000",text_size};
			String[] item8 = {"text","10","鸡蛋羹",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3","#ffffffff","0x0000",text_size};
			
			String[] item9 = {"text","11","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18","#ffffffff","0x0000",text_size};
			String[] item10 = {"text","12","戗面馒头",right_list_x,Integer.toString((int) (list_y+step*1.8)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13","米饭",right_list_x,Integer.toString((int) (list_y+step*2.6)),"2","#ffffffff","0x0000",text_size};
			String[] item12 = {"text","14","凉拌蕨根粉",right_list_x,Integer.toString((int) (list_y+step*3.4)),"6","#ff98fb98","0x0000",text_size};
			String[] item13 = {"text","15","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.2)),"4","#ffffffff","0x0000",text_size};
			String[] item14 = {"text","16","小米南瓜粥",right_list_x,Integer.toString((int) (list_y+step*5)),"3","#ffffffff","0x0000",text_size};
			
			String[] item15 = {"text","17","小炒",left_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item16 = {"text","18","小吃",middle_list_x,Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","主食及其他",right_list_x,Integer.toString((int) (list_y)),"","#ffffffff","#ffffff",tips_size};
			
			
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7",Integer.toString(step),"#ff969696",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			
			String[] item20 = {"text","22","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			String[] item21 = {"text","23","9元",Integer.toString(Integer.parseInt(left_list_x)+170),Integer.toString((int) (list_y+step*5.1)),"0","#ff98fb98","0x0000",text_size};
			String[] item22 = {"text","24","(米饭+小菜+鸡蛋羹+粥)",Integer.toString(Integer.parseInt(left_list_x)+230),Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			//步骤2：实例化
*/			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			JSONObject json_item22 = new JSONObject();
			JSONObject json_item23 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21,
					json_item22,
					json_item23
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
					json_item22.put(item_caption[i], item22[i]);
					json_item23.put(item_caption[i], item23[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}

		
		public static   void tuesdayDinnerMenu(int choice)//周二晚餐
		{
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			String date_id = CommonUtils.getNowDateString()+2;
			int list_y = 150;
			int step = 80;
			

			if(whetherUpdateAll)
			{
				date_id = tuesdayDate+2;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(晚餐:周二)",left_list_x,"0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			String[] item1 = {date_id,"","3","text","红椒鸡柳",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","木须肉",left_list_x,Integer.toString((int) (list_y+step*2.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","回锅腊肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"28",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item5 = {date_id,"","7","text","葱花脂油饼",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"6",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","锅贴(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item7 = {date_id,"","9","text","懒龙(2个)",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"8",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","鸡蛋羹",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item9 = {date_id,"","11","text","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item10 = {date_id,"","12","text","半碗米饭",right_list_x,Integer.toString((int) (list_y+step*1.7)),"1",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item11 = {date_id,"","13","text","米饭",right_list_x,Integer.toString((int) (list_y+step*2.4)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item23 = {date_id,"","25","text","戗面馒头",right_list_x,Integer.toString((int) (list_y+step*3.1)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item12 = {date_id,"","14","text","香芹鸡蛋干",right_list_x,Integer.toString((int) (list_y+step*3.8)),"6",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item13 = {date_id,"","15","text","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.5)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item14 = {date_id,"","16","text","小米南瓜粥",right_list_x,Integer.toString((int) (list_y+step*5.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
		
			
			
			String[] item15 = {date_id,"","17","text","小炒",left_list_x,Integer.toString(list_y),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item16 = {date_id,"","18","text","小吃",middle_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","主食及其他",right_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","#ffffff","","","","","","","",""};
			
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			//String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7","25",Integer.toString(step),"#ff969696
			//String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696"};
			
			String[] item20 = {date_id,"","22","text","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item21 = {date_id,"","23","text","9元",Integer.toString(Integer.parseInt(left_list_x)+185),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item22 = {date_id,"","24","text","(米饭+小菜+鸡蛋羹+粥)",Integer.toString(Integer.parseInt(left_list_x)+245),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			
		//步骤2：实例化
			/*CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(晚餐:周二)",left_list_x,"0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","红椒鸡柳",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","木须肉",left_list_x,Integer.toString((int) (list_y+step*2.2)),"20","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","孜然羊肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"28","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item5 = {"text","7","葱花脂油饼",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"6","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","锅贴(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","懒龙(2个)",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"8","#ff98fb98","0x0000",text_size};
			String[] item8 = {"text","10","鸡蛋羹",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3","#ffffffff","0x0000",text_size};
			
			String[] item9 = {"text","11","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18","#ffffffff","0x0000",text_size};
			String[] item10 = {"text","12","戗面馒头",right_list_x,Integer.toString((int) (list_y+step*1.8)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13","米饭",right_list_x,Integer.toString((int) (list_y+step*2.6)),"2","#ffffffff","0x0000",text_size};
			String[] item12 = {"text","14","凉拌蕨根粉",right_list_x,Integer.toString((int) (list_y+step*3.4)),"6","#ff98fb98","0x0000",text_size};
			String[] item13 = {"text","15","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.2)),"4","#ffffffff","0x0000",text_size};
			String[] item14 = {"text","16","小米南瓜粥",right_list_x,Integer.toString((int) (list_y+step*5)),"3","#ffffffff","0x0000",text_size};
			
			String[] item15 = {"text","17","小炒",left_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item16 = {"text","18","小吃",middle_list_x,Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","主食及其他",right_list_x,Integer.toString((int) (list_y)),"","#ffffffff","#ffffff",tips_size};
			
			
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7",Integer.toString(step),"#ff969696",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			
			String[] item20 = {"text","22","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			String[] item21 = {"text","23","9元",Integer.toString(Integer.parseInt(left_list_x)+170),Integer.toString((int) (list_y+step*5.1)),"0","#ff98fb98","0x0000",text_size};
			String[] item22 = {"text","24","(米饭+小菜+鸡蛋羹+粥)",Integer.toString(Integer.parseInt(left_list_x)+230),Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			JSONObject json_item22 = new JSONObject();
			JSONObject json_item23 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21,
					json_item22,
					json_item23,
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
					json_item22.put(item_caption[i], item22[i]);
					json_item23.put(item_caption[i], item23[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}

	
		public static   void wednesdayDinnerMenu(int choice)//周三晚餐
		{
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			String date_id = CommonUtils.getNowDateString()+2;
			int list_y = 150;
			int step = 80;
			
			if(whetherUpdateAll)
			{
				date_id = wednesdayDate+2;
			}
			
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(晚餐:周三)",left_list_x,"0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			
			String[] item1 = {date_id,"","3","text","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","土豆片炒肉 ",left_list_x,Integer.toString((int) (list_y+step*4.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item5 = {date_id,"","7","text","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item7 = {date_id,"","9","text","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item9 = {date_id,"","11","text","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item10 = {date_id,"","12","text","半碗米饭",right_list_x,Integer.toString((int) (list_y+step*1.7)),"1",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item11 = {date_id,"","13","text","米饭",right_list_x,Integer.toString((int) (list_y+step*2.4)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item23 = {date_id,"","25","text","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*3.1)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item12 = {date_id,"","14","text","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.8)),"6",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item13 = {date_id,"","15","text","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.5)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item14 = {date_id,"","16","text","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			
			
			String[] item15 = {date_id,"","17","text","小炒",left_list_x,Integer.toString(list_y),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item16 = {date_id,"","18","text","小吃",middle_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","主食及其他",right_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","#ffffff","","","","","","","",""};
			
			
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			
			String[] item20 = {date_id,"","22","text","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item21 = {date_id,"","23","text","9元",Integer.toString(Integer.parseInt(left_list_x)+185),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item22 = {date_id,"","24","text","(米饭+小菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+245),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
		/*	//步骤2：实例化
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(晚餐:周三)",left_list_x,"0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item5 = {"text","7","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12","#ffffffff","0x0000",text_size};
			String[] item8 = {"text","10","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3","#ffffffff","0x0000",text_size};
			
			String[] item9 = {"text","11","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18","#ffffffff","0x0000",text_size};
			String[] item10 = {"text","12","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*1.8)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13","米饭",right_list_x,Integer.toString((int) (list_y+step*2.6)),"2","#ffffffff","0x0000",text_size};
			String[] item12 = {"text","14","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.4)),"6","#ffffffff","0x0000",text_size};
			String[] item13 = {"text","15","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.2)),"4","#ffffffff","0x0000",text_size};
			String[] item14 = {"text","16","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5)),"3","#ffffffff","0x0000",text_size};
			
			String[] item15 = {"text","17","小炒",left_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item16 = {"text","18","小吃",middle_list_x,Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","主食及其他",right_list_x,Integer.toString((int) (list_y)),"","#ffffffff","#ffffff",tips_size};
			
			
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7",Integer.toString(step),"#ff969696",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			
			String[] item20 = {"text","22","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			String[] item21 = {"text","23","9元",Integer.toString(Integer.parseInt(left_list_x)+170),Integer.toString((int) (list_y+step*5.1)),"0","#ff98fb98","0x0000",text_size};
			String[] item22 = {"text","24","(米饭+凉菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+230),Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			//步骤2：实例化
*/			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			JSONObject json_item22 = new JSONObject();
			JSONObject json_item23 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21,
					json_item22,
					json_item23
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
					json_item22.put(item_caption[i], item22[i]);
					json_item23.put(item_caption[i], item23[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}

		
		public static   void thursdayDinnerMenu(int choice)//周四晚餐
		{
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			String date_id = CommonUtils.getNowDateString()+2;
			int list_y = 150;
			int step = 80;
			
			if(whetherUpdateAll)
			{
				date_id = thursdayDate+2;
			}
			
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(晚餐:周四)",left_list_x,"0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			
			String[] item1 = {date_id,"","3","text","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","土豆片炒肉 ",left_list_x,Integer.toString((int) (list_y+step*4.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item5 = {date_id,"","7","text","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item7 = {date_id,"","9","text","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item9 = {date_id,"","11","text","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item10 = {date_id,"","12","text","半碗米饭",right_list_x,Integer.toString((int) (list_y+step*1.7)),"1",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item11 = {date_id,"","13","text","米饭",right_list_x,Integer.toString((int) (list_y+step*2.4)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item23 = {date_id,"","25","text","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*3.1)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item12 = {date_id,"","14","text","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.8)),"6",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item13 = {date_id,"","15","text","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.5)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item14 = {date_id,"","16","text","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			
			
			String[] item15 = {date_id,"","17","text","小炒",left_list_x,Integer.toString(list_y),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item16 = {date_id,"","18","text","小吃",middle_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","主食及其他",right_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","#ffffff","","","","","","","",""};
			
			
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			
			String[] item20 = {date_id,"","22","text","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item21 = {date_id,"","23","text","9元",Integer.toString(Integer.parseInt(left_list_x)+185),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item22 = {date_id,"","24","text","(米饭+小菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+245),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
		
			/*	//步骤2：实例化
			/*CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(晚餐:周四)",left_list_x,"0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item5 = {"text","7","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12","#ffffffff","0x0000",text_size};
			String[] item8 = {"text","10","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3","#ffffffff","0x0000",text_size};
			
			String[] item9 = {"text","11","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18","#ffffffff","0x0000",text_size};
			String[] item10 = {"text","12","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*1.8)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13","米饭",right_list_x,Integer.toString((int) (list_y+step*2.6)),"2","#ffffffff","0x0000",text_size};
			String[] item12 = {"text","14","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.4)),"6","#ffffffff","0x0000",text_size};
			String[] item13 = {"text","15","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.2)),"4","#ffffffff","0x0000",text_size};
			String[] item14 = {"text","16","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5)),"3","#ffffffff","0x0000",text_size};
			
			String[] item15 = {"text","17","小炒",left_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item16 = {"text","18","小吃",middle_list_x,Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","主食及其他",right_list_x,Integer.toString((int) (list_y)),"","#ffffffff","#ffffff",tips_size};
			
			
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7",Integer.toString(step),"#ff969696",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			
			String[] item20 = {"text","22","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			String[] item21 = {"text","23","9元",Integer.toString(Integer.parseInt(left_list_x)+170),Integer.toString((int) (list_y+step*5.1)),"0","#ff98fb98","0x0000",text_size};
			String[] item22 = {"text","24","(米饭+凉菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+230),Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			//步骤2：实例化
*/			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			JSONObject json_item22 = new JSONObject();
			JSONObject json_item23 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21,
					json_item22,
					json_item23
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
					json_item22.put(item_caption[i], item22[i]);
					json_item23.put(item_caption[i], item23[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}

	
		public static   void fridayDinnerMenu(int choice)//周五晚餐
		{
			CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			//String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String[] item_caption = {"date_id","menu_id","widget_id","type","name","x","y","price","font_size","font_color","background_color","distance","additiontal","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			String date_id = CommonUtils.getNowDateString()+2;
			int list_y = 150;
			int step = 80;
			
			if(whetherUpdateAll)
			{
				date_id = fridayDate+2;
			}
			
			//String[] theme = {date_id,"","0","theme","五味，放心吃（早点）","400","0","",theme_size,"#ffffffff","0x0000"};
			
			
			//步骤1：添加item到这里先定义信息
			//步骤1：添加item到这里先定义信息
			String[] theme = {date_id,"","0","theme","(晚餐:周五)",left_list_x,"0","",theme_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] tips =  {date_id,"","1","tips","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item0 = {date_id,"","2","pic","","500","0","","","","","","","","","","","",""};
			
			String[] item1 = {date_id,"","3","text","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item2 = {date_id,"","4","text","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item3 = {date_id,"","5","text","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item4 = {date_id,"","6","text","土豆片炒肉 ",left_list_x,Integer.toString((int) (list_y+step*4.2)),"20",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item5 = {date_id,"","7","text","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item6 = {date_id,"","8","text","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item7 = {date_id,"","9","text","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item8 = {date_id,"","10","text","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			String[] item9 = {date_id,"","11","text","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item10 = {date_id,"","12","text","半碗米饭",right_list_x,Integer.toString((int) (list_y+step*1.7)),"1",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item11 = {date_id,"","13","text","米饭",right_list_x,Integer.toString((int) (list_y+step*2.4)),"2",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item23 = {date_id,"","25","text","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*3.1)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item12 = {date_id,"","14","text","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.8)),"6",text_size,"#ff98fb98","0x0000",distance_level_1,"","","","","","",""};
			String[] item13 = {date_id,"","15","text","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.5)),"4",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			String[] item14 = {date_id,"","16","text","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5.2)),"3",text_size,"#ffffffff","0x0000",distance_level_1,"","","","","","",""};
			
			
			
			String[] item15 = {date_id,"","17","text","小炒",left_list_x,Integer.toString(list_y),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item16 = {date_id,"","18","text","小吃",middle_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item17 = {date_id,"","19","text","主食及其他",right_list_x,Integer.toString((int) (list_y)),"",tips_size,"#ffffffff","#ffffff","","","","","","","",""};
			
			
			String[] item18 = {date_id,"","20","line_vertical","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"8","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			String[] item19 = {date_id,"","21","line_vertical","",Integer.toString(Integer.parseInt(right_list_x)-70),Integer.toString((int) (list_y)),"9","25",Integer.toString(step),"#ff969696","","","","","","","",""};
			
			String[] item20 = {date_id,"","22","text","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
			String[] item21 = {date_id,"","23","text","9元",Integer.toString(Integer.parseInt(left_list_x)+185),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ff98fb98","0x0000","","","","","","","",""};
			String[] item22 = {date_id,"","24","text","(米饭+小菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+245),Integer.toString((int) (list_y+step*5.1)),"0",text_size,"#ffffffff","0x0000","","","","","","","",""};
		
			/*	//步骤2：实例化
			
	/*		CommonUtils.LogWuwei(tag, "显示晚餐中...");
			JSONArray content = new JSONArray();
			String[] item_caption = {"type","widget_id","name","x","y","price","font_color","background_color","font_size"};
			String left_list_x = "50";
			String middle_list_x = "450";
			String right_list_x = "850";
			String left_list_title_x = "100";
			
			String text_size="25";
			String theme_size="45";
			String tips_size="35";
			int list_y = 150;
			int step = 80;
			
			//步骤1：添加item到这里先定义信息
			String[] theme = {"theme","0","五味，放心吃(晚餐:周五)",left_list_x,"0","","#ffffffff","0x0000",theme_size};;
			String[] tips =  {"tips","1","微信关注\"五味\"自助下单每天减1元",left_list_x,"600","0","#ffffffff","0x0000",tips_size};
			String[] item0 = {"pic","2","","500","0","","","",""};
			String[] item1 = {"text","3","肉末小油菜",left_list_x,Integer.toString((int) (list_y+step*1.2)),"20","#ffffffff","0x0000",text_size};
			String[] item2 = {"text","4","豌豆牛柳",left_list_x,Integer.toString((int) (list_y+step*2.2)),"28","#ffffffff","0x0000",text_size};
			String[] item3 = {"text","5","娃娃菜小炒肉",left_list_x,Integer.toString((int) (list_y+step*3.2)),"20","#ffffffff","0x0000",text_size};
			String[] item4 = {"text","6","醋溜土豆丝",left_list_x,Integer.toString((int) (list_y+step*4.2)),"18","#ffffffff","0x0000",text_size};
			
			String[] item5 = {"text","7","特色锅盔",middle_list_x,Integer.toString((int) (list_y+step*1.2)),"12","#ffffffff","0x0000",text_size};
			String[] item6 = {"text","8","素贴饼(3个)",middle_list_x,Integer.toString((int) (list_y+step*2.2)),"8","#ffffffff","0x0000",text_size};
			String[] item7 = {"text","9","土家酱香饼",middle_list_x,Integer.toString((int) (list_y+step*3.2)),"12","#ffffffff","0x0000",text_size};
			String[] item8 = {"text","10","双皮奶",middle_list_x,Integer.toString((int) (list_y+step*4.2)),"3","#ffffffff","0x0000",text_size};
			
			String[] item9 = {"text","11","万能的龙须面",right_list_x,Integer.toString((int) (list_y+step*1)),"18","#ffffffff","0x0000",text_size};
			String[] item10 = {"text","12","红枣窝头",right_list_x,Integer.toString((int) (list_y+step*1.8)),"2","#ffffffff","0x0000",text_size};
			String[] item11 = {"text","13","米饭",right_list_x,Integer.toString((int) (list_y+step*2.6)),"2","#ffffffff","0x0000",text_size};
			String[] item12 = {"text","14","爽口木耳",right_list_x,Integer.toString((int) (list_y+step*3.4)),"6","#ffffffff","0x0000",text_size};
			String[] item13 = {"text","15","香菜丸子汤",right_list_x,Integer.toString((int) (list_y+step*4.2)),"4","#ffffffff","0x0000",text_size};
			String[] item14 = {"text","16","玉米粥",right_list_x,Integer.toString((int) (list_y+step*5)),"3","#ffffffff","0x0000",text_size};
			
			String[] item15 = {"text","17","小炒",left_list_x,Integer.toString(list_y),"","#ffffffff","0x0000",tips_size};
			String[] item16 = {"text","18","小吃",middle_list_x,Integer.toString((int) (list_y)),"","#ffffffff","0x0000",tips_size};
			String[] item17 = {"text","19","主食及其他",right_list_x,Integer.toString((int) (list_y)),"","#ffffffff","#ffffff",tips_size};
			
			
			String[] item18 = {"line_vertical","20","",Integer.toString(Integer.parseInt(middle_list_x)-70),Integer.toString((int) (list_y)),"7",Integer.toString(step),"#ff969696",text_size};
			String[] item19 = {"line_vertical","21","",Integer.toString(Integer.parseInt(right_list_x)-70), Integer.toString((int) (list_y)),"9",Integer.toString(step),"#ff969696",text_size};
			
			String[] item20 = {"text","22","套餐=小炒+",left_list_x,Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};
			String[] item21 = {"text","23","9元",Integer.toString(Integer.parseInt(left_list_x)+170),Integer.toString((int) (list_y+step*5.1)),"0","#ff98fb98","0x0000",text_size};
			String[] item22 = {"text","24","(米饭+凉菜+双皮奶+粥)",Integer.toString(Integer.parseInt(left_list_x)+230),Integer.toString((int) (list_y+step*5.1)),"0","#ffffffff","0x0000",text_size};*/
			//步骤2：实例化
			JSONObject json_theme = new JSONObject();
			JSONObject json_tips = new JSONObject();
			JSONObject json_item0 = new JSONObject();
			JSONObject json_item1 = new JSONObject();
			JSONObject json_item2 = new JSONObject();
			JSONObject json_item3 = new JSONObject();
			JSONObject json_item4 = new JSONObject();
			JSONObject json_item5 = new JSONObject();
			JSONObject json_item6 = new JSONObject();
			JSONObject json_item7 = new JSONObject();
			JSONObject json_item8 = new JSONObject();
			JSONObject json_item9 = new JSONObject();
			JSONObject json_item10 = new JSONObject();
			JSONObject json_item11 = new JSONObject();
			JSONObject json_item12 = new JSONObject();
			JSONObject json_item13 = new JSONObject();
			JSONObject json_item14 = new JSONObject();
			JSONObject json_item15 = new JSONObject();
			JSONObject json_item16 = new JSONObject();
			JSONObject json_item17 = new JSONObject();
			JSONObject json_item18 = new JSONObject();
			JSONObject json_item19 = new JSONObject();
			JSONObject json_item20 = new JSONObject();
			JSONObject json_item21 = new JSONObject();
			JSONObject json_item22 = new JSONObject();
			JSONObject json_item23 = new JSONObject();
			
			//步骤3：添加到数组
			JSONObject[] jsonObject_array = {
					json_theme,
					json_tips,
					json_item0,
					json_item1,
					json_item2,
					json_item3,
					json_item4,
					json_item5,
					json_item6,
					json_item7,
					json_item8,
					json_item9,
					json_item10,
					json_item11,
					json_item12,
					json_item13,
					json_item14,
					json_item15,
					json_item16,
					json_item17,
					json_item18,
					json_item19,
					json_item20,
					json_item21,
					json_item22,
					json_item23
					};
			
			try {
				int i = 0;
				//步骤4:添加到jsonarray
				for(i=0;i<item_caption.length;i++)
				{
					json_theme.put(item_caption[i], theme[i]);
					json_tips.put(item_caption[i], tips[i]);
					json_item0.put(item_caption[i], item0[i]);
					json_item1.put(item_caption[i], item1[i]);
					json_item2.put(item_caption[i], item2[i]);
					json_item3.put(item_caption[i], item3[i]);
					json_item4.put(item_caption[i], item4[i]);
					json_item5.put(item_caption[i], item5[i]);
					json_item6.put(item_caption[i], item6[i]);
					json_item7.put(item_caption[i], item7[i]);
					json_item8.put(item_caption[i], item8[i]);
					json_item9.put(item_caption[i], item9[i]);
					json_item10.put(item_caption[i], item10[i]);
					json_item11.put(item_caption[i], item11[i]);
					json_item12.put(item_caption[i], item12[i]);
					json_item13.put(item_caption[i], item13[i]);
					json_item14.put(item_caption[i], item14[i]);
					json_item15.put(item_caption[i], item15[i]);
					json_item16.put(item_caption[i], item16[i]);
					json_item17.put(item_caption[i], item17[i]);
					json_item18.put(item_caption[i], item18[i]);
					json_item19.put(item_caption[i], item19[i]);
					json_item20.put(item_caption[i], item20[i]);
					json_item21.put(item_caption[i], item21[i]);
					json_item22.put(item_caption[i], item22[i]);
					json_item23.put(item_caption[i], item23[i]);
				}
				
				for(i=0;i<jsonObject_array.length;i++)
				{
					content.put(jsonObject_array[i]);	
				}
				
				json_menu.put("menu",content);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				CommonUtils.LogWuwei(tag, "jsonException: "+e.getMessage());
				e.printStackTrace();
			}
			sendHttpRequest(choice);
			
		}
		
		
		/**
		 * 向服务器发送请求
		 * @param choice：0--》电视端预览 1--》存储到电视端的数据库
		 */
		public static void sendHttpRequest(int choice)
		{
	     
			final  String choice_str = Integer.toString(choice);
			
			if(!flag_load_module_new_menu)
			{
				new Thread()
				{
					public void run()
					{
						//MsgUtils.SendSingleMsg(splash.handlerTools,splash.ip_address,HandlerUtils.SHOW_NORMAL_TOAST);
						//CommonUtils.LogWuwei(tag, "ip_address is "+splash.ip_address);
						String uriAPI = "http://"+ip_address+":8081"; //
				        /*建立HTTPost对象*/
				        HttpPost httpRequest = new HttpPost(uriAPI); 
				        /*
				         * NameValuePair实现请求参数的封装
				        */
				        List <NameValuePair> params = new ArrayList <NameValuePair>(); 
	                   
				        try 
				        { 
			                    params.add(new BasicNameValuePair("menuInfo", json_menu.toString()));
			                    params.add(new BasicNameValuePair("whetherSaveInTv",choice_str));
				        	
				          /* 添加请求参数到请求对象*/
				          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
				          /*发送请求并等待响应*/
				          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); 
				          /*若状态码为200 ok*/
				          if(httpResponse.getStatusLine().getStatusCode() == 200)  
				          { 
				            /*读返回数据*/
				        	  CommonUtils.LogWuwei(tag, "post success");
				            String strResult = EntityUtils.toString(httpResponse.getEntity());
//				            MsgUtils.SendSingleMsg(splash.handlerTools, "post success", HandlerUtils.SHOW_NORMAL_TOAST);
				          } 
				          else 
				          { 
				        	   CommonUtils.LogWuwei(tag, "post failure "+httpResponse.getStatusLine().getStatusCode());
				          //  MsgUtils.SendSingleMsg(splash.handlerTools, "Error Response: "+httpResponse.getStatusLine().toString(),HandlerUtils.SHOW_NORMAL_TOAST); 
				          } 
				        } 
				        catch (ClientProtocolException e) 
				        {
				        		
				         //   MsgUtils.SendSingleMsg(splash.handlerTools, "ClientProtocolException : "+e.getMessage(),HandlerUtils.SHOW_NORMAL_TOAST);
				            e.printStackTrace();
				            CommonUtils.LogWuwei(tag, "ClientProtocolException is "+e.getMessage());
				        } 
				        catch (IOException e) 
				        {
				        //    MsgUtils.SendSingleMsg(splash.handlerTools, "IOException : "+e.getMessage(),HandlerUtils.SHOW_NORMAL_TOAST);
				            CommonUtils.LogWuwei(tag, "IOException is "+e.getMessage());
				            e.printStackTrace(); 
				        } 
				        catch (Exception e) 
				        {  
				        		CommonUtils.LogWuwei(tag,"Exception is "+e.getMessage());
				        	//	MsgUtils.SendSingleMsg(splash.handlerTools, "Exception : "+e.getMessage(),HandlerUtils.SHOW_NORMAL_TOAST);
				        		e.printStackTrace();  
				        }  
					}
				}.start();
				
			}
			
			insertToMobileDataBase(json_menu.toString(),choice_str);
//			CommonUtils.LogWuwei(tag, "json_menu.toString() is "+json_menu.toString());
		}
			
	
		
		public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	
	        	if (linearLayoutAll != null) {
	        		QManager.removeView(linearLayoutAll);
	    			linearLayoutAll = null;
	        		listviewChooseMenuType.setVisibility(View.VISIBLE);
	        }
	        return super.onKeyDown(keyCode, event);
		 }
	    	return true;
		}

		
	
		/**
		 * 
		 * flag->0 更新本周的日期 flag->1更新下周的日期
		 * @param flag
		 */
		public static void updateDayDate(int flag)
	    {
	    		int[] weekDay = {7,1,2,3,4,5,6};
	    		int today = 0;
			Date date=new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
			dateFm.format(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
			    w = 0;
			today = weekDay[w];
			
			if(flag == 0)
			{
				switch (today)
				{
					case 1:
					 	mondayDate = CommonUtils.getNowDateString();
					    	tuesdayDate = CommonUtils.getDate(1);//Long.toString(Long.parseLong(mondayDate)+1);
					    	wednesdayDate =CommonUtils.getDate(2);// Long.toString(Long.parseLong(mondayDate)+2);
					    	thursdayDate =CommonUtils.getDate(3);//Long.toString(Long.parseLong(mondayDate)+3);
					    	fridayDate = CommonUtils.getDate(4);//Long.toString(Long.parseLong(mondayDate)+4);
						break;
					case 2:
						tuesdayDate = CommonUtils.getNowDateString();
						mondayDate = CommonUtils.getDate(-1);//Long.toString(Long.parseLong(tuesdayDate)-1);
					    	wednesdayDate =CommonUtils.getDate(1);// Long.toString(Long.parseLong(tuesdayDate)+1);
					    	thursdayDate =CommonUtils.getDate(2);//Long.toString(Long.parseLong(tuesdayDate)+2);
					    	fridayDate = CommonUtils.getDate(3);//Long.toString(Long.parseLong(tuesdayDate)+3);
						break;
					case 3:
						wednesdayDate = CommonUtils.getNowDateString();
						
						mondayDate = CommonUtils.getDate(-2);//Long.toString(Long.parseLong(wednesdayDate)-2);
					    	tuesdayDate =CommonUtils.getDate(-1);// Long.toString(Long.parseLong(wednesdayDate)-1);
					    	thursdayDate =CommonUtils.getDate(1);//Long.toString(Long.parseLong(wednesdayDate)+1);
					    	fridayDate = CommonUtils.getDate(2);//Long.toString(Long.parseLong(wednesdayDate)+2);
						break;
					case 4:
				    		thursdayDate =CommonUtils.getNowDateString();
				    	
						mondayDate = CommonUtils.getDate(-3);//Long.toString(Long.parseLong(thursdayDate)-3);
					    	tuesdayDate = CommonUtils.getDate(-2);//Long.toString(Long.parseLong(thursdayDate)-2);
					    	wednesdayDate =CommonUtils.getDate(-1);//Long.toString(Long.parseLong(thursdayDate)-1);
					    	fridayDate = CommonUtils.getDate(1);//Long.toString(Long.parseLong(thursdayDate)+1);
						break;
					case 5:
					 	fridayDate =CommonUtils.getNowDateString();
					 	
						mondayDate =CommonUtils.getDate(-4);// Long.toString(Long.parseLong(fridayDate)-4);
					    	tuesdayDate =CommonUtils.getDate(-3);// Long.toString(Long.parseLong(fridayDate)-3);
					    	wednesdayDate =CommonUtils.getDate(-2);//Long.toString(Long.parseLong(fridayDate)-2);
					    	thursdayDate =CommonUtils.getDate(-1);//Long.toString(Long.parseLong(fridayDate)-1);
						break;
					case 6:
						String saturdayDate = CommonUtils.getNowDateString();//
						mondayDate = CommonUtils.getDate(-5);//Long.toString(Long.parseLong(saturdayDate)+2);
					    	tuesdayDate =CommonUtils.getDate(-4);// Long.toString(Long.parseLong(saturdayDate)+3);
					    	wednesdayDate =CommonUtils.getDate(-3);//Long.toString(Long.parseLong(saturdayDate)+4);
					    	thursdayDate =CommonUtils.getDate(-2);//Long.toString(Long.parseLong(saturdayDate)+5);
					    	fridayDate =CommonUtils.getDate(-1);//Long.toString(Long.parseLong(saturdayDate)+6);
						break;
					case 7:
						String sundayDate = CommonUtils.getNowDateString();
						mondayDate = CommonUtils.getDate(-6);//Long.toString(Long.parseLong(sundayDate)+1);
						tuesdayDate = CommonUtils.getDate(-5);//Long.toString(Long.parseLong(sundayDate)+2);
				    		wednesdayDate =CommonUtils.getDate(-4);//Long.toString(Long.parseLong(sundayDate)+3);
				    		thursdayDate =CommonUtils.getDate(-3);//Long.toString(Long.parseLong(sundayDate)+4);
				    		fridayDate =CommonUtils.getDate(-2);//Long.toString(Long.parseLong(sundayDate)+5);
						break;
					default:
						CommonUtils.LogWuwei(tag, "updateDayDate wrong msg:not weekday");
				}
				
			}
			else if(flag == 1)
			{
				switch (today)
				{
					case 1:
					 	mondayDate =CommonUtils.getDate(7);
					    	tuesdayDate = CommonUtils.getDate(8);//Long.toString(Long.parseLong(mondayDate)+1);
					    	wednesdayDate =CommonUtils.getDate(9);// Long.toString(Long.parseLong(mondayDate)+2);
					    	thursdayDate =CommonUtils.getDate(10);//Long.toString(Long.parseLong(mondayDate)+3);
					    	fridayDate = CommonUtils.getDate(11);//Long.toString(Long.parseLong(mondayDate)+4);
						break;
					case 2:
						tuesdayDate = CommonUtils.getDate(7);
						mondayDate = CommonUtils.getDate(6);//Long.toString(Long.parseLong(tuesdayDate)-1);
					    	wednesdayDate =CommonUtils.getDate(8);// Long.toString(Long.parseLong(tuesdayDate)+1);
					    	thursdayDate =CommonUtils.getDate(9);//Long.toString(Long.parseLong(tuesdayDate)+2);
					    	fridayDate = CommonUtils.getDate(10);//Long.toString(Long.parseLong(tuesdayDate)+3);
						break;
					case 3:
						wednesdayDate = CommonUtils.getDate(7);
						
						mondayDate = CommonUtils.getDate(5);//Long.toString(Long.parseLong(wednesdayDate)-2);
					    	tuesdayDate =CommonUtils.getDate(6);// Long.toString(Long.parseLong(wednesdayDate)-1);
					    	thursdayDate =CommonUtils.getDate(8);//Long.toString(Long.parseLong(wednesdayDate)+1);
					    	fridayDate = CommonUtils.getDate(9);//Long.toString(Long.parseLong(wednesdayDate)+2);
						break;
					case 4:
				    		thursdayDate = CommonUtils.getDate(7);
				    	
						mondayDate = CommonUtils.getDate(4);//Long.toString(Long.parseLong(thursdayDate)-3);
					    	tuesdayDate = CommonUtils.getDate(5);//Long.toString(Long.parseLong(thursdayDate)-2);
					    	wednesdayDate =CommonUtils.getDate(6);//Long.toString(Long.parseLong(thursdayDate)-1);
					    	fridayDate = CommonUtils.getDate(8);//Long.toString(Long.parseLong(thursdayDate)+1);
						break;
					case 5:
					 	fridayDate = CommonUtils.getDate(7);
					 	
						mondayDate =CommonUtils.getDate(3);// Long.toString(Long.parseLong(fridayDate)-4);
					    	tuesdayDate =CommonUtils.getDate(4);// Long.toString(Long.parseLong(fridayDate)-3);
					    	wednesdayDate =CommonUtils.getDate(5);//Long.toString(Long.parseLong(fridayDate)-2);
					    	thursdayDate =CommonUtils.getDate(6);//Long.toString(Long.parseLong(fridayDate)-1);
						break;
					case 6:
						String saturdayDate = CommonUtils.getDate(7);
						mondayDate = CommonUtils.getDate(2);//Long.toString(Long.parseLong(saturdayDate)+2);
					    	tuesdayDate =CommonUtils.getDate(3);// Long.toString(Long.parseLong(saturdayDate)+3);
					    	wednesdayDate =CommonUtils.getDate(4);//Long.toString(Long.parseLong(saturdayDate)+4);
					    	thursdayDate =CommonUtils.getDate(5);//Long.toString(Long.parseLong(saturdayDate)+5);
					    	fridayDate =CommonUtils.getDate(6);//Long.toString(Long.parseLong(saturdayDate)+6);
						break;
					case 7:
						String sundayDate = CommonUtils.getNowDateString();
						mondayDate = CommonUtils.getDate(1);//Long.toString(Long.parseLong(sundayDate)+1);
						tuesdayDate = CommonUtils.getDate(2);//Long.toString(Long.parseLong(sundayDate)+2);
				    		wednesdayDate =CommonUtils.getDate(3);//Long.toString(Long.parseLong(sundayDate)+3);
				    		thursdayDate =CommonUtils.getDate(4);//Long.toString(Long.parseLong(sundayDate)+4);
				    		fridayDate =CommonUtils.getDate(5);//Long.toString(Long.parseLong(sundayDate)+5);
						break;
					default:
						CommonUtils.LogWuwei(tag, "updateDayDate wrong msg:not weekday");
				}
				
			}
			
			
			CommonUtils.LogWuwei(tag, "mondayDate is "+mondayDate+
					"tuesdayDate is "+tuesdayDate+
					"wednesdayDate is "+wednesdayDate+
					"thursdayDate is "+thursdayDate+
					"fridayDate is "+fridayDate
					);
	    }
	    
	    
	  
		public static void insertToMobileDataBase(String menuInfo,String choice_str)
	    {
			JSONObject jsonMenuInfo=null;
			
			String whetherSave="";
			Boolean flag_database_save = false;
	        try {
	            Map<String, String> files = new HashMap<String, String>();
	            
	            //从json中获取具体的菜单信息
	            jsonMenuInfo = new JSONObject(menuInfo);
	            
	            //从json数据中解析是否选择存储到电视端数据库
	            whetherSave = choice_str;
	            CommonUtils.LogWuwei(tag, "whetherSave is "+whetherSave );
	            if(Integer.parseInt(whetherSave) == 0)
	            {
	            		flag_database_save = false;
	            }
	            else
	            {
	            		flag_database_save = true;
	            }
	            //CommonUtils.LogWuwei(tag, menuInfo);
	        } catch (Exception e) {
	        		CommonUtils.LogWuwei(tag,"error:"+ e.getMessage());
	            e.printStackTrace();
	        }
	        
	        if(menuInfo != "")
	        {
				try {
					 int i=0;
					 JSONArray jsonArray = jsonMenuInfo.getJSONArray("menu"); 
	              	 Boolean flag_override = false;
	              	 
	              	 //判断menuDetailTable是否为空
	              	 CommonUtils.LogWuwei(tag, "----------------------");
	              	 
	              	if(menuDetailDao == null)
	              	{
	              		daoMaster = getDaoMaster(NewFromHistoryMenuActivity.context);
	              		daoSession = getDaoSession(NewFromHistoryMenuActivity.context);
	        	        
	              		menuTableDao = daoSession.getMenuTableDao();
	              		menuDetailDao = daoSession.getMenuDetailDao();
	              	}
	              	 
	              	 Long count = menuDetailDao.count();
	              	 CommonUtils.LogWuwei(tag, "----------------------now count is "+count);
	              	CommonUtils.LogWuwei(tag, "-----------------------now count is "+ menuTableDao.count());
	              	 
	              	JSONObject jsonTmp = (JSONObject) jsonArray.opt(0); 
	              	String dateIdTmp = jsonTmp.getString("date_id");
	              	Long dateLongId  = Long.parseLong(dateIdTmp);
	              	
	              	
	              	QueryBuilder qb = menuDetailDao.queryBuilder();
	              	qb.where(MenuDetailDao.Properties.MenuDateId.eq(dateIdTmp));
	              	List listResult = qb.list();

	              	CommonUtils.LogWuwei("dataIdTmp","\ndateIdTmp is "+dateIdTmp+"\n"+"1"+"listResult.size()  is "+listResult.size() );
	              	
	              	if(listResult.size() != 0)//覆盖
	              	{
	              		flag_override = true;
	              		if(flag_database_save)
	              		{

	              			DeleteQuery<MenuDetail> dq = qb.where(MenuDetailDao.Properties.MenuDateId.eq(dateIdTmp)).buildDelete();
	                  		dq.executeDeleteWithoutDetachingEntities();
	                  		for(i=0;i<jsonArray.length();i++)
	                  		{
	                  			JSONObject jsonobj = (JSONObject) jsonArray.opt(i);
	                  			 String type = jsonobj.getString("type");
	                  			 if(type.equals("theme"))
	                  			 {
	                  				String name =jsonobj.getString("name");
	                  				HandlerUtils.showToast(ctxt, dateIdTmp+name+"将被覆盖");
	                  				 break;
	                  			 }
	                  			
	                  		}
	              			
	              		}
	              		
	              	}
	              	else
	              	{
	              		flag_override = false;
	              	}
	        		
					
	              	for(i=0;i<jsonArray.length();i++)
					 {
						 JSONObject jsonobj = (JSONObject) jsonArray.opt(i);
						 
						 String menu_id_str = jsonobj.getString("menu_id");
						 Long menu_id = menuTableDao.count();
						 String date_id = jsonobj.getString("date_id");//1-->date_id是包含菜单类型的，举例201411210
						 String widget_id = jsonobj.getString("widget_id");//2
						 String type = jsonobj.getString("type");//3
						 String name =jsonobj.getString("name");//4
						 String x = jsonobj.getString("x");//5
						 String y = jsonobj.getString("y");//6
						 String price = jsonobj.getString("price");//7
						 String font_size = jsonobj.getString("font_size");//8
						 String font_color = jsonobj.getString("font_color");//9
						 String background_color = jsonobj.getString("background_color");//10
						 String distance = jsonobj.getString("distance");
						 String additiontal = jsonobj.getString("additiontal");
						 String bindToItemServerId = jsonobj.getString("BindToItemServerId");
						 String redundance1 = jsonobj.getString("redundance1");
						 String redundance2 = jsonobj.getString("redundance2");
						 String redundance3 = jsonobj.getString("redundance3");
						 String redundance4 = jsonobj.getString("redundance4");
						 String redundance5 = jsonobj.getString("redundance5");
						 
						 if(bindToItemServerId.equals("") || bindToItemServerId == null)
						 {
							 bindToItemServerId = "0";
						 }
						 
						 //","BindToItemServerId","redundance1","redundance2","redundance3","redundance4","redundance5"};
						 double distance_double = 0;
						 if(distance != null && !distance.equals(""))
						 {
							 distance_double = Double.parseDouble(distance);
						 }
						 
						 try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
						 
						if(price.equals("") || price.equals(" ") || price.equals(null))
						{
							price="888";
						}
						
						if(font_size.equals("") || font_size.equals(" ") || font_size.equals(null))
						{
							font_size="0";
						}
						
						if(flag_database_save)
						{
							 MenuDetail menu_detail_entity = new MenuDetail(
									 Long.toString(dateLongId),
									 menu_id, 
									 Integer.parseInt(widget_id), 
									 type, 
									 name, 
									 Double.parseDouble(x),
									 Double.parseDouble(y),
									 Integer.parseInt(price),
									 Integer.parseInt(font_size),
									 font_color, 
									 background_color,
									 distance_double,
									 additiontal,
									 Integer.parseInt(bindToItemServerId),
									 redundance1,
									 redundance2,
									 redundance3,
									 redundance4,
									 redundance5);
								 menuDetailDao.insert(menu_detail_entity);
						}
						
						
						 
					 }
					 if(flag_database_save && !flag_override)
					 {
						 MenuTable menu_table_entity = new MenuTable(
								 "",//菜单的名称String
								0,//早餐、午餐、晚餐 int
								null,//菜单id 	Long
								CommonUtils.getNowDateString(),//菜单创建时间 String
								Long.parseLong("0")//店铺编号
								 );
						 menuTableDao.insert(menu_table_entity);
						 Long menu_id = menu_table_entity.getMenuId();
						 CommonUtils.LogWuwei(tag, "\n------Menu table insert data now---index is "+menu_id+"\n"+"|"+"\n");
						 //MsgUtils.SendSingleMsg(splash.handlerTools,dateIdTmp+ "菜单存储成功", HandlerUtils.SHOW_NORMAL_TOAST);
					 }
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					CommonUtils.LogWuwei(tag, "json error:"+e.getMessage());
					e.printStackTrace();		
				}
	        }
	        
	    }
	  
		
	/***
	 * 
	 * 取得DaoMaster 
     * @param context 
     * @return 
     */  
     public static  DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {  
            OpenHelper helper = new DaoMaster.DevOpenHelper(context,pathDataBase+"menu_5wei_db", null);  
            daoMaster = new DaoMaster(helper.getWritableDatabase());  
        }  
        return daoMaster;  
     }  
	      
	    
    /** 
     * 取得DaoSession 
     *  
     * @param context 
     * @return 
     */  
     public  static DaoSession getDaoSession(Context context) {  
        if (daoSession == null) {  
            if (daoMaster == null) {  
                daoMaster = getDaoMaster(context);  
            }  
            daoSession = daoMaster.newSession();  
        }  
        return daoSession;  
     }  
	
		
}



