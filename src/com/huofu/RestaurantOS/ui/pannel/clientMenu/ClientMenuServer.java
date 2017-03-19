package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Button;

import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.support.greenDao.MenuTable;
import com.huofu.RestaurantOS.ui.pannel.tvMenu.NanoHTTPD;
import com.huofu.RestaurantOS.ui.pannel.tvMenu.ShowMenuActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.FIleUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;



/****************************
 * 继承NanoHttpD的类，重新实现server这个方法
 * @author berryshine
 */
public class ClientMenuServer extends NanoHTTPD{

	public static String tag = "In　server";
	public static ClientMenuServer nanoHttpd = null;
	public static int margin_edge_min = 0;
	public static int margin_edge = 0;
	public static String themeText;
	public static String tips;
	public static String title0Text;
	public static String title1Text;
	public static String[] list0;
	public static String[] list1;
	public static String[] list2;
	public static String[] list3;
	public static WindowManager QManager;
    public static boolean flag_refresh_tv_screen = false;
    public static  Button new_button = null;
	public static boolean flag_override = false;
	public static int historyNoUpdateValue = 0;
	static long lastDateId = (long)0;
	public static String menuInfo ="";
	public static JSONObject jsonMenuInfo=null;
	public static Boolean flag_database_save = false;
	public static Context ctxt = null;
	

	
	public ClientMenuServer(int port) {
		super(port);
	}

	public ClientMenuServer(String hostName,int port,Context ctxt){
		super(hostName,port);
		this.ctxt = ctxt;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.ShanFuBao.SmartCall.main.NanoHTTPD#serve(com.ShanFuBao.SmartCall.main.NanoHTTPD.IHTTPSession)
	 */
	public Response serve(IHTTPSession session)
	 {
		CommonUtils.LogWuwei(tag, "msg received");
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>五味<br>俱全</title></head>");
        sb.append("<body>");

        FIleUtils fileHelper = new FIleUtils(ctxt);
        
        //String content = fileHelper.readSDFile(pathTvMenuCache+"menu.log");
        
        sb.append("请叫我帅哥");
        
        sb.append("</body>");
        sb.append("</html>");
        

        try {
            infoDeal(session);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(sb.toString());
	 }
	
	
	/**
	 * 对接收到的信息进行处理
	 * @param session
	 */
	public static void infoDeal(IHTTPSession session)
	 {
		
        try {
           
        		Map<String, String> files = new HashMap<String, String>();
        
            session.parseBody(files);
            
            
            //从json中获取具体的菜单信息
            menuInfo = session.getParms().get("menuInfo");
            
            
            if(menuInfo != "" && menuInfo != null)//从json数据中解析是否选择存储到电视端数据库
            {
            		CommonUtils.LogWuwei(tag, "menuInfo is "+menuInfo);
                String whetherSave = session.getParms().get("whetherSaveInTv");
                parseMenuInfo(menuInfo,whetherSave);
                
            }else
            {
            		String queryParams = session.getQueryParameterString();
            		
            		//CommonUtils.LogWuwei(tag, "queryParams is "+queryParams);
            		
                if(queryParams != null && queryParams != "")
                {
                		parseParams(queryParams,session);
                		//parsePar1ams(queryParams,session);
                }
                else
                {
                		CommonUtils.LogWuwei(tag, "you can do nothiing in infoDeal");
                }
            }
    			
        } catch (Exception e) {
        		CommonUtils.LogWuwei(tag,"error:"+ e.getMessage());
            e.printStackTrace();
        }
        
    }

	 
	 /**
	  * 功能1：通过得到要预览的各个参数并发送给显示程序（对应手机端的本地更新菜单）
	  * 功能2：更新菜单时得到菜单数据并存储在本地数据库
	  * @param queryParams
	  * @param session
	  */
	public static void parseParams(String queryParams,IHTTPSession session)
	 {
		  flag_database_save = true;
		  
		  
		  
		  String str_preview = session.getParms().get("preview");
		  String widgetIdRemove = session.getParms().get("remove_widget");
		  String menu_date_id = session.getParms().get("date_id");
		  
		  //CommonUtils.LogWuwei(tag,"widgetIdRemove is "+widgetIdRemove+"\nqueryParams is "+queryParams+"  \n\n"+session.getParms()+"\n\n");
		  
		//  CommonUtils.LogWuwei(tag+":parseParams", "str_preview is "+str_preview);
		  
		  String nowIndex = Long.toString(ShowMenuActivity.keyboard_date_menu_index)+ ShowMenuActivity.keyboard_menu_type_index;
		  
		  if(menu_date_id != null && widgetIdRemove != null)
		  {
			  if(Long.parseLong(menu_date_id) != Long.parseLong(nowIndex))
			  {
				  
				  ShowMenuActivity.keyboard_date_menu_index = Long.parseLong(menu_date_id)/10;
				  ShowMenuActivity.keyboard_menu_type_index = (int)Long.parseLong(menu_date_id)%10;
				  	CommonUtils.LogWuwei(tag, "当前菜单和欲显示菜单不是同一菜单，接下来会切换到欲显示菜单");
				  //切换到客户端所修改的菜单
					QueryBuilder qbUp = ShowMenuActivity.menuDetailDao.queryBuilder();
					qbUp.where(MenuDetailDao.Properties.MenuDateId.eq(menu_date_id));
				    List<MenuDetail> listResultUp = qbUp.list();
				    
				    int size = listResultUp.size();
				    
				    if(size > 0)
				    {
				    	
				    		Message msg_clean = new Message();
				    		msg_clean.what = ShowMenuActivity.CLEAN_VIEWS;
				    		ShowMenuActivity.handlerShowMenu.sendMessage(msg_clean);
				    		
				    	  	for(int i=0;i<listResultUp.size();i++)
				    	  	{
				    	  		MenuDetail menu_detail_entity = (MenuDetail) listResultUp.get(i);
				    	  		String menu_id_str = menu_detail_entity.getMenuDateId();
			     			String date_id = "";
			     			String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
			     			String type = menu_detail_entity.getType();
			     			String name = menu_detail_entity.getName();
			     			String x = Double.toString(menu_detail_entity.getX());
			     			String y = Double.toString(menu_detail_entity.getY());
			     			String price = Integer.toString(menu_detail_entity.getPrice());
			     			String font_size = Integer.toString(menu_detail_entity.getFontSize());
			     			String font_color = menu_detail_entity.getFontColor();
			     			String background_color = menu_detail_entity.getBackgroundColor();
			     			double distance = menu_detail_entity.getDistance();
			     			String additiontal = menu_detail_entity.getAddtiontal();
			     			
			     			//MsgUtils.SendSingleMsg(splash.handlerTools, "name is "+name, HandlerUtils.SHOW_NORMAL_TOAST);
			     			
				    			Message msg = new Message();
				    			Bundle bundle = new Bundle(); 
				    			bundle.putString("date_id", menu_id_str);//0
				    			bundle.putString("menu_id",date_id);//1
				    			bundle.putString("widget_id",widget_id);//2
				    			bundle.putString("type", type);//3
				    			bundle.putString("name",name);//4
				    			bundle.putString("x",x);  //5
				    			bundle.putString("y",y);  //6   
				    			bundle.putString("price",price);  //7  
				    			bundle.putString("font_size",font_size);//8
				    			bundle.putString("font_color",font_color);  //9  
				    			bundle.putString("background_color",background_color); //10
				    			bundle.putString("distance", Double.toString(distance));
				    			bundle.putString("additiontal", additiontal);
				    			
				    			msg.setData(bundle);//msg利用Bundle传递数据
				    			ShowMenuActivity.handlerShowMenu.sendMessage(msg);
				    	  	}
			 				
				  
			  }
		  
			  }
		  }
		  
		  if(str_preview != null)
		  {
			  if(CommonUtils.isNumeric(str_preview))
			  {
				  if(Integer.parseInt(str_preview) == 1)//清空屏幕
				  {
					  flag_database_save = false;
					  flag_refresh_tv_screen = true;
					  CommonUtils.LogWuwei("update", "str_preview is "+str_preview);
					  return;
				  }else if(Integer.parseInt(str_preview) == 0)
				  {
					  flag_database_save = false;
				  }else if(Integer.parseInt(str_preview) == 2)
				  {
					  flag_database_save = true;
				  }
			  }
		  }
		  
		  
		 
		 Map<String, String> str = session.getParms();
		 
		 Message msg = new Message();
		 msg.what = ShowMenuActivity.SHOW_MENU_WIDGET;
		 Bundle bundle = new Bundle(); 
		 bundle.putString("menu_id", str.get("menu_id_str"));//0
		 bundle.putString("date_id",str.get("date_id"));//1
		 bundle.putString("widget_id",str.get("widget_id"));//2
		 bundle.putString("type", str.get("type"));//3
		 bundle.putString("name",str.get("name"));//4
		 bundle.putString("x",str.get("x"));  //5
		 bundle.putString("y",str.get("y"));  //6   
		 bundle.putString("price",str.get("price"));  //7  
		 bundle.putString("font_size",str.get("font_size"));//8
		 bundle.putString("font_color",str.get("font_color"));  //9  
		 bundle.putString("background_color",str.get("background_color")); //10
		 bundle.putString("distance",str.get("distance"));  //11
		 bundle.putString("additiontal",str.get("additiontal"));  //11
		 bundle.putString("BindToItemServerId", str.get("BindToItemServerId"));
		 bundle.putString("redundance1", str.get("redundance1"));
		 bundle.putString("redundance2", str.get("redundance2"));
		 bundle.putString("redundance3", str.get("redundance3"));
		 bundle.putString("redundance4", str.get("redundance4"));
		 bundle.putString("redundance5", str.get("redundance5"));

		 msg.setData(bundle);//msg利用Bundle传递数据
		 
		 
		 if(widgetIdRemove == null)
		 {
			 ShowMenuActivity.handlerShowMenu.sendMessage(msg);
		 }
		 
		 if(flag_database_save)
		 {
			 int price = 0;
			 double x = 0;
			 double y = 0;
			 double distance = 0;
			 long menu_id = 0;
			 int bindToServerIntId = 0;
			 
		 	if(str.get("price") != null)
		 	{
		 		price = Integer.parseInt(str.get("price"));
		 	}
		 	
		 	if(str.get("x") != null)
		 	{
		 		x = Double.parseDouble(str.get("x"));
		 	}
		 	
			if(str.get("y") != null)
		 	{
		 		y = Double.parseDouble(str.get("y"));
		 	}
			
			if(str.get("distance") != null)
		 	{
		 		distance = Double.parseDouble(str.get("distance"));
		 	}
			
			if(str.get("menu_id_str") != null)
			{
				menu_id = Long.parseLong(str.get("menu_id_str"));
			}
			
			if(str.get("BindToItemServerId") == null || str.get("BindToItemServerId").equals(""))
			{
				bindToServerIntId = 0;
			}
			else
			{
				bindToServerIntId = Integer.parseInt(str.get("BindToItemServerId"));
			}
			
			
				 MenuDetail menu_detail_entity = new MenuDetail(
					 str.get("date_id"),
					 menu_id, 
					 Integer.parseInt(str.get("widget_id")), 
					 str.get("type"),
					 str.get("name"), 
					 x,
					 y,
					 price,
					 Integer.parseInt(str.get("font_size")),
					 str.get("font_color"), 
					 str.get("background_color"),
					 distance,
					 str.get("additiontal"),
					 bindToServerIntId,
					 str.get("redundance1"),
					 str.get("redundance2"),
					 str.get("redundance3"),
					 str.get("redundance4"),
					 str.get("redundance5")
					 );
				 
				 
				 if(ShowMenuActivity.menuDetailDao != null)
				 {
					 
					 Long date_id = Long.parseLong(str.get("date_id"));
					 QueryBuilder qb = ShowMenuActivity.menuDetailDao.queryBuilder();
					 
					 if(widgetIdRemove != null)
					 {
						 DeleteQuery<MenuDetail> dq = 
		          					qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_id)),
		          					 MenuDetailDao.Properties.WidgetId.eq(menu_detail_entity.getWidgetId()))
		          					.buildDelete();
		          			
		              	dq.executeDeleteWithoutDetachingEntities();
		              	ShowMenuActivity.menuDetailDao.insertOrReplace(menu_detail_entity);
		              	
		              	HandlerUtils.showToast(ctxt, "准备显示菜单");
						showTmpMenu(Long.parseLong(menu_date_id),Integer.parseInt(widgetIdRemove));
					 }
					 else
					 {
						 if(lastDateId != date_id)
						 {
							 CommonUtils.LogWuwei("zzlInClientMenuServer", "new insert module and ready to delete date_id;lastDateId is"+lastDateId+"  date_id is "+date_id
									 +"widgetId is "+str.get("widget_id"));
							 
								 DeleteQuery<MenuDetail> dq = 
				          					qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_id)),
				          							MenuDetailDao.Properties.WidgetId.ge("0"))
				          							.buildDelete();
								 
				              	dq.executeDeleteWithoutDetachingEntities();
						 }
						 lastDateId = date_id;
						 ShowMenuActivity.menuDetailDao.insertOrReplace(menu_detail_entity);
					 }
					
					 
					 //CommonUtils.LogWuwei(tag, "insert data now menu_id is "+menu_id+" date_id  is "+ str.get("date_id"));
					
					 menu_detail_entity = null;
					 
						  
				 }
				 else
				 {
					 CommonUtils.LogWuwei(tag, "ShowMenuActivity.menuDetailDao is null");
				 }
		 }
		  
	 }
	
	
	
	 /**
	  * 功能1：通过得到要预览的各个参数并发送给显示程序（对应手机端的本地更新菜单）
	  * 功能2：更新菜单时得到菜单数据并存储在本地数据库
	  * @param queryParams
	  * @param session
	  */
	public static void parsePar1ams(String queryParams,IHTTPSession session)
	 {
		  flag_database_save = true;
		  String str_preview = session.getParms().get("preview");
		  CommonUtils.LogWuwei(tag+":parseParams", "str_preview is "+str_preview);
		  if(str_preview != null)
		  {
			  if(CommonUtils.isNumeric(str_preview))
			  {
				  if(Integer.parseInt(str_preview) == 1)//清空屏幕
				  {
					  flag_database_save = false;
					  flag_refresh_tv_screen = true;	  
					  return;
				  }else if(Integer.parseInt(str_preview) == 0)
				  {
					  flag_database_save = false;
				  }
			  }
		  }
		   
		 
		 Map<String, String> str = session.getParms();
		 
		 Message msg = new Message();
		 msg.what = ShowMenuActivity.SHOW_MENU_WIDGET;
		 Bundle bundle = new Bundle(); 
		 bundle.putString("menu_id", str.get("menu_id_str"));//0
		 bundle.putString("date_id",str.get("date_id"));//1
		 bundle.putString("widget_id",str.get("widget_id"));//2
		 bundle.putString("type", str.get("type"));//3
		 bundle.putString("name",str.get("name"));//4
		 bundle.putString("x",str.get("x"));  //5
		 bundle.putString("y",str.get("y"));  //6   
		 bundle.putString("price",str.get("price"));  //7  
		 bundle.putString("font_size",str.get("font_size"));//8
		 bundle.putString("font_color",str.get("font_color"));  //9  
		 bundle.putString("background_color",str.get("background_color")); //10
		 bundle.putString("distance",str.get("distance"));  //11
		 bundle.putString("additiontal",str.get("additiontal"));  //11
		 bundle.putString("BindToItemServerId", str.get("BindToItemServerId"));
		 bundle.putString("redundance1", str.get("redundance1"));
		 bundle.putString("redundance2", str.get("redundance2"));
		 bundle.putString("redundance3", str.get("redundance3"));
		 bundle.putString("redundance4", str.get("redundance4"));
		 bundle.putString("redundance5", str.get("redundance5"));

		 msg.setData(bundle);//msg利用Bundle传递数据
		 
		 
		 if(!flag_database_save)
		 {
			 //ShowMenuActivity.handlerShowMenu.sendMessage(msg);
		 }
		 else if(flag_database_save)
		 {
			 //ShowMenuActivity.handlerShowMenu.sendMessage(msg);
			 int price = 0;
			 double x = 0;
			 double y = 0;
			 double distance = 0;
			 long menu_id = 0;
			 int bindToServerIntId = 0;
			 
		 	if(str.get("price") != null)
		 	{
		 		price = Integer.parseInt(str.get("price"));
		 	}
		 	
		 	if(str.get("x") != null)
		 	{
		 		x = Double.parseDouble(str.get("x"));
		 	}
		 	
			if(str.get("y") != null)
		 	{
		 		y = Double.parseDouble(str.get("y"));
		 	}
			
			if(str.get("distance") != null)
		 	{
		 		distance = Double.parseDouble(str.get("distance"));
		 	}
			
			if(str.get("menu_id_str") != null)
			{
				menu_id = Long.parseLong(str.get("menu_id_str"));
			}
			
			if(str.get("BindToItemServerId") == null || str.get("BindToItemServerId").equals(""))
			{
				bindToServerIntId = 0;
			}
			else
			{
				bindToServerIntId = Integer.parseInt(str.get("BindToItemServerId"));
			}
			
			
				 MenuDetail menu_detail_entity = new MenuDetail(
					 str.get("date_id"),
					 menu_id, 
					 Integer.parseInt(str.get("widget_id")), 
					 str.get("type"),
					 str.get("name"), 
					 x,
					 y,
					 price,
					 Integer.parseInt(str.get("font_size")),
					 str.get("font_color"), 
					 str.get("background_color"),
					 distance,
					 str.get("additiontal"),
					 bindToServerIntId,
					 str.get("redundance1"),
					 str.get("redundance2"),
					 str.get("redundance3"),
					 str.get("redundance4"),
					 str.get("redundance5")
					 );
				 
				 
				 if(ShowMenuActivity.menuDetailDao != null)
				 {
					 
					 Long date_id = Long.parseLong(str.get("date_id"));
					 if(lastDateId != date_id)
					 {
						 CommonUtils.LogWuwei("zzlInClientMenuServer", "new insert module and ready to delete date_id;lastDateId is"+lastDateId+"  date_id is "+date_id );
						 
						 QueryBuilder qb = ShowMenuActivity.menuDetailDao.queryBuilder();
						 
						 DeleteQuery<MenuDetail> dq = 
		          					qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_id)),
		          					 MenuDetailDao.Properties.WidgetId.ge("0"))
		          					.buildDelete();
		          			
		              		dq.executeDeleteWithoutDetachingEntities();
					 }
					 lastDateId = date_id;
					 //CommonUtils.LogWuwei(tag, "insert data now menu_id is "+menu_id+" date_id  is "+ str.get("date_id"));
					 ShowMenuActivity.menuDetailDao.insertOrReplace(menu_detail_entity);
					 menu_detail_entity = null;
				 }
				 else
				 {
					 CommonUtils.LogWuwei(tag, "ShowMenuActivity.menuDetailDao is null");
				 }
		 }
		 
	 }
	 
		 
	 
	 
	 /**
	  * 功能1：通过得到要预览的各个参数并发送给显示程序（对应手机端的本地更新菜单（老版））
	  * 功能2：更新菜单时得到菜单数据并存储在本地数据库
	  * @param menuInfo
	  * @param whetherSave
	  */
	public static void parseMenuInfo(String menuInfo,String whetherSave)
	 {
		 CommonUtils.LogWuwei(tag+":parseMenuInfo", "in parseMenuInfo");
		 if( menuInfo == "")
		 {
			 return;
		 }
		 try {
			 	int i=0;
			 	
			 	flag_database_save = true;
	            if(Integer.parseInt(whetherSave) == 0)
	            {
	            		flag_database_save = false;
	            }
			 
			 jsonMenuInfo = new JSONObject(menuInfo);
			 JSONArray jsonArray = jsonMenuInfo.getJSONArray("menu"); 
          	 flag_refresh_tv_screen = true;
          	 Boolean flag_override = false;
          	 
          	 //判断menuDetailTable是否为空
          	 CommonUtils.LogWuwei(tag, "----------------------");
          	 Long count = ShowMenuActivity.menuDetailDao.count();
          	 CommonUtils.LogWuwei(tag, "----------------------now count is "+count);
          	CommonUtils.LogWuwei(tag, "-----------------------now count is "+ ShowMenuActivity.menuTableDao.count());
          	 
          	JSONObject jsonTmp = (JSONObject) jsonArray.opt(0); 
          	String dateIdTmp = jsonTmp.getString("date_id");
          	Long dateLongId  = Long.parseLong(dateIdTmp);
          	
          	
          	QueryBuilder qb = ShowMenuActivity.menuDetailDao.queryBuilder();
          	qb.where(MenuDetailDao.Properties.MenuDateId.eq(dateIdTmp));
          	List listResult = qb.list();

          	CommonUtils.LogWuwei(tag,"\ndateIdTmp is "+dateIdTmp+"\n"+"1"+"listResult.size()  is "+listResult.size() );
          	
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
//              				MsgUtils.SendSingleMsg(splash.handlerTools,dateIdTmp+name+"将被覆盖", HandlerUtils.SHOW_NORMAL_TOAST);
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
				 Long menu_id = ShowMenuActivity.menuTableDao.count();
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
				 
				 if(bindToItemServerId.equals(""))
				 {
					 bindToItemServerId = "0";
				 }
				 
				 
				 if(redundance1 != null && !redundance1.equals(""))
	    			{
	    				if(Integer.parseInt(redundance1) == 1)
	    				{
	    					CommonUtils.LogWuwei("bindType", name+"被绑定到套餐 id:"+bindToItemServerId);	
	    				}
	    				else
	    				{
	    					CommonUtils.LogWuwei("bindType", name+"被绑定到单品 id:"+bindToItemServerId);
	    				}
	    				
	    			}
				 else
				 {
					 CommonUtils.LogWuwei("bindType", "无绑定信息");
				 }
				 
				 CommonUtils.LogWuwei(tag, "distance is "+distance);
				 
				 double distance_double = 0;
				 if(distance != null && !distance.equals(""))
				 {
					 distance_double = Double.parseDouble(distance);
				 }
				 
				 /*try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				 
				 new_button = new Button(ctxt);
				 Message msg = new Message();
				 msg.what = ShowMenuActivity.SHOW_MENU_WIDGET;
				 Bundle bundle = new Bundle(); 
				 bundle.putString("menu_id", menu_id_str);//0
				 bundle.putString("date_id",date_id);//1
				 bundle.putString("widget_id",widget_id);//2
				 bundle.putString("type", type);//3
				 bundle.putString("name",name);//4
				 bundle.putString("x",x);  //5
				 bundle.putString("y",y);  //6   
				 bundle.putString("price",price);  //7  
				 bundle.putString("font_size",font_size);//8
				 bundle.putString("font_color",font_color);  //9  
				 bundle.putString("background_color",background_color); //10
				 bundle.putString("distance", distance);
				 bundle.putString("additiontal",additiontal);
				 bundle.putString("BindToItemServerId", bindToItemServerId);
				 bundle.putString("redundance1", redundance1);
				 bundle.putString("redundance2", redundance2);
				 bundle.putString("redundance3", redundance3);
				 bundle.putString("redundance4", redundance4);
				 bundle.putString("redundance", redundance5);
				 
				 
				 msg.setData(bundle);//msg利用Bundle传递数据
				 if(!flag_database_save)
				 {
					 ShowMenuActivity.handlerShowMenu.sendMessage(msg);
				 }
				 new_button = null;

				 
				if(price.equals("") || price.equals(" ") || price.equals(null))
				{
					price="888";
				}
				
				if(font_size.equals("") || font_size.equals(" ") || font_size.equals(null))
				{
					font_size="0";
				}
				
				if(bindToItemServerId == null || bindToItemServerId.equals(""))
				{
					bindToItemServerId = "0";
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
							 redundance5
							 );
					/* CommonUtils.LogWuwei(tag,  "1、"+Long.toString(dateLongId)+
							 "2、"+menu_id+
							 "3、"+Integer.parseInt(widget_id)+ 
							 "4、"+type+
							 "5、"+name+ 
							 "6、"+Double.parseDouble(x)+
							 "7、"+Double.parseDouble(y)+
							 "8、"+Integer.parseInt(price)+
							 "9、"+Integer.parseInt(font_size)+
							 "10、"+font_color+ 
							 "11、"+background_color);*/
						 ShowMenuActivity.menuDetailDao.insert(menu_detail_entity);
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
				 ShowMenuActivity.menuTableDao.insert(menu_table_entity);
				 Long menu_id = menu_table_entity.getMenuId();
				 CommonUtils.LogWuwei(tag, "\n------Menu table insert data now---index is "+menu_id+"\n"+"|"+"\n");
//				 MsgUtils.SendSingleMsg(splash.handlerTools,dateIdTmp+ "菜单存储成功", HandlerUtils.SHOW_NORMAL_TOAST);
			 }
			 //判断menuDetailTable是否为空
          	 CommonUtils.LogWuwei(tag, "----------------------");
          	 count = ShowMenuActivity.menuDetailDao.count();
          	 CommonUtils.LogWuwei(tag, "----------------------after insert count is "+count);
          	CommonUtils.LogWuwei(tag, "-----------------------after insert count is "+ ShowMenuActivity.menuTableDao.count());
          	 
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			CommonUtils.LogWuwei(tag, "json error:"+e.getMessage());
			e.printStackTrace();		
		}
	 }

	 
	public static void showTmpMenu(long menuDateId,int widgetId)
	{
		
		long keyboard_date_menu_index = menuDateId;
		
		
		QueryBuilder qbUp = ShowMenuActivity.menuDetailDao.queryBuilder();
		qbUp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index)),
				MenuDetailDao.Properties.WidgetId.eq(widgetId));
	    List<MenuDetail> listResultUp = qbUp.list();
	    
	    int size = listResultUp.size();
	    
	      if(size > 0)
	      {
			
	    	    //MsgUtils.SendSingleMsg(splash.handlerTools, "listResultUp.size() is "+listResultUp.size()+" 刷新屏幕...", HandlerUtils.SHOW_NORMAL_TOAST);
//	    	  	CommonUtils.LogWuwei(tag, "接收到键盘上的请求，刷新屏幕中。。。。");
	    	    
	    	  //移除要修改的控件
	    	    Message msg_clean = new Message();
	    	    msg_clean.what = ShowMenuActivity.CLEAN_VIEW;
	    	    msg_clean.obj = widgetId;
	    	    ShowMenuActivity.handlerShowMenu.sendMessage(msg_clean);
	    	  	
	    	  	for(int i=0;i<listResultUp.size();i++)
	    	  	{
	    	  		MenuDetail menu_detail_entity = (MenuDetail) listResultUp.get(i);
 				
 				String menu_id_str = menu_detail_entity.getMenuDateId();
     			String date_id = "";
     			String widget_id = Integer.toString(menu_detail_entity.getWidgetId());
     			String type = menu_detail_entity.getType();
     			String name = menu_detail_entity.getName();
     			String x = Double.toString(menu_detail_entity.getX());
     			String y = Double.toString(menu_detail_entity.getY());
     			String price = Integer.toString(menu_detail_entity.getPrice());
     			String font_size = Integer.toString(menu_detail_entity.getFontSize());
     			String font_color = menu_detail_entity.getFontColor();
     			String background_color = menu_detail_entity.getBackgroundColor();
     			double distance = menu_detail_entity.getDistance();
     			String additiontal = menu_detail_entity.getAddtiontal();
     			
     			//MsgUtils.SendSingleMsg(splash.handlerTools, "name is "+name, HandlerUtils.SHOW_NORMAL_TOAST);
     			
	    			Message msg = new Message();
	    			Bundle bundle = new Bundle(); 
	    			bundle.putString("date_id", menu_id_str);//0
	    			bundle.putString("menu_id",date_id);//1
	    			bundle.putString("widget_id",widget_id);//2
	    			bundle.putString("type", type);//3
	    			bundle.putString("name",name);//4
	    			bundle.putString("x",x);  //5
	    			bundle.putString("y",y);  //6   
	    			bundle.putString("price",price);  //7  
	    			bundle.putString("font_size",font_size);//8
	    			bundle.putString("font_color",font_color);  //9  
	    			bundle.putString("background_color",background_color); //10
	    			bundle.putString("distance", Double.toString(distance));
	    			bundle.putString("additiontal", additiontal);
	    			
	    			msg.setData(bundle);//msg利用Bundle传递数据
	    			if(ShowMenuActivity.rl_server != null)
	    			{
	    				//MsgUtils.SendSingleMsg(splash.handlerTools, "ShowMenuActivity.rl_server != null", HandlerUtils.SHOW_NORMAL_TOAST);
	    				CommonUtils.LogWuwei(tag, "准备发消息，更新新的修改修改");
	    				Message msg_update_menu = new Message();
	    				msg_update_menu.what = ShowMenuActivity.UPDATE_MENU_IN_SERVER;
	    				msg_update_menu.obj = msg;
	    				ShowMenuActivity.handlerShowMenu.sendMessage(msg_update_menu);
	    			}
	    			else
	    			{
	    				HandlerUtils.showToast(ctxt, "ShowMenuActivity.rl_server == null");
	    			}
	    			
	    			msg = null;
	    			bundle = null;
	    	  	}
	      }
   }
	 
}