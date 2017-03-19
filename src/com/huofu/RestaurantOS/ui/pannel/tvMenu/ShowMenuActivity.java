package com.huofu.RestaurantOS.ui.pannel.tvMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.support.greenDao.DaoMaster;
import com.huofu.RestaurantOS.support.greenDao.DaoSession;
import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.support.greenDao.MenuTableDao;
import com.huofu.RestaurantOS.support.niftyDialog.Effectstype;
import com.huofu.RestaurantOS.support.niftyDialog.NiftyDialogBuilder;
import com.huofu.RestaurantOS.ui.pannel.clientMenu.ClientMenuServer;
import com.huofu.RestaurantOS.ui.pannel.clientMenu.NewFromHistoryMenuActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.FIleUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.NetWorkUtils;
import com.huofu.RestaurantOS.utils.ViewServer;
import com.huofu.RestaurantOS.utils.gesture;
import com.huofu.RestaurantOS.widget.RateTextCircularProgressBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class ShowMenuActivity extends Activity {

  	public static ClientMenuServer nanoHttpd = null;
  	public static Context ctxt;
	private static  String tag = "showMenu";
 
	public static String pathFonts =  Environment.getExternalStorageDirectory()+File.separator+"huofu"+ File.separator+"fonts"+ File.separator;

	
	private static long  mExitTime = 0;
	public static RelativeLayout rl_server;
	public static Handler handlerShowMenu = null;
	public static final int  SHOW_MENU_WIDGET = 0;
	public static final int  CLEAN_VIEWS = 1;
	public static final int  REGISTER_FOR_CONTEXT_MENU = 2;
	public static final int  UPDATE_TEXT_SOLD_ALL = 3;
	public static final int  CANCEL_TEXT_SOLD = 4;
	public static final int	 UPDATE_PROGRESS_BAR = 5;
	public static final int	 SHOW_PROPER_MENU = 6;
	public static final int	 UPDATE_ALL = 7;
	public static final int	 UPDATE_MENU_IN_SERVER = 8;
	public static final int  CLEAN_VIEW = 9;
	public static final int  SHOW_VIEW = 10;
	public static final int SEND_CCD_MORNING = 11;
	public static final int SEND_CCD_LAUNCH = 12;
	public static final int SEND_CCD_DINNER = 13;
	

	public static final int PORT = 9100;

	public static final int THREAD_SET_ORDER_TAKEN = 0;
	public static final int THREAD_GET_XIAOCHAO_ID = 1;
	public static final int THREAD_GET_DUMPLING_ID = 2;
	public static final int THREAD_WRITE_TO_PRINTER = 3;
	public static final int THREAD_PRINT_TEST = 4;
	
	public static Point size;
	public static Typeface fontFaceChalkBoard;//黑板体（普通字体）
	public static Typeface fontFacePianPian;//翩翩体（title、tips）
	public static Typeface fontFaceVerticalLine;//画竖线用的字体（|）
	public static int width_screen=0;
	public static int height_screen=0;
	public static double x_fraction = 0;
	public static double y_fraction = 0;
	public static double font_fraction = 0;
	public static Button logoButton = null;
	public static Button button_menu_hiden = null;
	public static Long save=(long) 0;
	
	public static Long keyboard_date_menu_index = Long.parseLong(CommonUtils.getNowDateString());//按下小米按键时候，菜单跟随变化(左右按键)---》日期
	public static Long biggest_margin = null;
	public static Long smallest_margin = null;
	int dayFromToday = 0;//距离今天的第几天，用来计算前一天、后一天时候的准确日期
    
	private  SQLiteDatabase db;
    private  DaoMaster daoMaster;
    private  DaoSession daoSession;
    public static MenuTableDao menuTableDao;
    public static MenuDetailDao menuDetailDao;
    public static int keyboard_menu_type_index = 0;//0->早餐 1->午餐 2->晚餐 
    private GestureDetector gestureDetector;  
    public static int step = 0;
    public static int day_menu_list_now_id = -1;
    public static boolean flag_stop_get_now_id = true;
    
    public static String[][] emptyReserveListFromServer = new String[50][2];//0->名字 1->库存数量 当此时库存为0时会更新此数组
    
    public static int[] NowEmptyList = new int[100];//控件id 这个是保存已经设置为售罄的控件id的数组 
    
    public static String[][] nowNameIdFromDatabase = new String[100][2];//0->名字 1->控件id 此二维数组记录的是此时（早/中/晚餐）显示菜单的对应名称和控件id
    public static int[][] nowBindSfoodSingleId = new int[100][2];//绑定到服务器的单品对应的id（读取本地数据库即可）
    //绑定到服务器的套餐id和对应的sfood的id、amount（读取本地数据库得到套餐id，从网络获取sfood的信息）以及被绑定的控件id
    public static int[][] nowBindComboId_SfoodId_Sfood_Amount_WidgetId = new int[100][4];
    public static int[][] sfoodInfo = new int[100][4];//0 comboId 1 sfoodId 2 sfoodAmount
    
    private Timer timerScanReserves;
    private TimerTask timerTaskScanReserves ;
	Timer timerModify = null;
    TimerTask timerTaskModify = null;
    long timeScan = 0;
	long timeModify = 0;
	
    private boolean flagTimerTaskRunning = false;
    public static int[][] ComboId = new int[100][2];
    public static int menu_list_int[] = new int[3];
    public static long sale_on_off_long[][] = new long[3][2];//[早、中、晚] [0->营业时间戳 1->停业时间戳]
    RateTextCircularProgressBar mRateTextCircularProgressBar = null;
    Dialog dlg_prrgress_bar = null;
    int progress = 0;
    boolean flagUpdateSuccess[][] = new boolean[7][3];
    public static List listResultRequestServerData = null;
    public static Handler handlerThreadSetOrderTaken = null;
    

    public static List<Integer> takeSerianlNumSuccessList = new ArrayList<Integer>();
	public static List<Integer> xiaoChaoId = new ArrayList<Integer>();
	public static List<String> xiaoChaoName = new ArrayList<String>();
	public static List<Integer> dumplingId = new ArrayList<Integer>();
	public static List<String> dumplingIdName = new ArrayList<String>();
	
	public static Socket socket = null;
	public static String printerIp = null;
	public static OutputStream out = null;
	public static TimerTask taskGetRemotePrintData = null;//
	public static Timer timerGetRemotePrintData = null;//
	public static Boolean flagPrintEnable = true;
	public static Boolean flagSendEmail = false;
    
    
	public static List<Integer> OrderIdHistoryFromServer = null;//从服务器每次获取到的order历史都存在这,不允许重复（避免从服务器获取到的出餐单还没来得及打印，又重复发送到打印队列）
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewServer.get(this).addWindow(this);
		
		setContentView(R.layout.showmenu);
		
		initShowMenu();
		
		widgetConfigureShowMenu();
		
		startServer(8081);
		
		showProperMennu();
		
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		ViewServer.get(this).setFocusedWindow(this);
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		flagSendEmail = false;
		
		cancelTimerScanReserves();//关闭查询库存的定时器
		
		// TODO Auto-generated method stub
		ViewServer.get(this).removeWindow(this);
		
		super.onDestroy();
	}
	
	/**
	 * 1、电视菜单的初始化
	 */
	void initShowMenu()
	{
		MainApplication.setmActivity(this);
		timerGetRemotePrintData = new Timer();
		CommonUtils.LogWuwei(tag, "printerIp is "+printerIp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	//应用运行时，保持屏幕高亮，不锁屏
	
		daoMaster = CommonUtils.getDaoMaster(getApplicationContext(), daoMaster);
		daoSession = CommonUtils.getDaoSession(getApplicationContext(), daoSession, daoMaster);
        
		ctxt = getApplicationContext();
		
        menuTableDao = daoSession.getMenuTableDao();
        menuDetailDao = daoSession.getMenuDetailDao();

		width_screen = CommonUtils.getScreenWidth(ctxt);
		height_screen = CommonUtils.getScreenHeight(ctxt);
		
		//在手机端存储的时候，x除了1.5，y除了1.533
		x_fraction = 1.5*(width_screen/1920.0);
		y_fraction = 1.533*(height_screen/1104.0);
		

		DisplayMetrics dm = new DisplayMetrics();  
		dm = getResources().getDisplayMetrics();  
		float density  = dm.density;
		font_fraction =1;// density/1.33;
		
		CommonUtils.LogWuwei(tag,
				"density is "+density+" width is "+width_screen+
				" and height_screen is "+height_screen+
				" x_fraction is "+Double.toString(x_fraction)+
				" y_fraction is "+Double.toString(y_fraction));
		
		//翩翩体
		CommonUtils.LogWuwei(tag, "example font path is "+pathFonts+"pianpina.ttf");
		CommonUtils.LogWuwei(tag, (new FIleUtils(getApplicationContext()).isExist(pathFonts+"pianpina.ttf")?"翩翩体存在":"翩翩体不存在"));
		fontFacePianPian = Typeface.createFromFile(pathFonts+"pianpina.ttf");
		
		//黑板字
		CommonUtils.LogWuwei(tag, "example font path is "+pathFonts+"Chalkboard.ttf");
		fontFaceChalkBoard = Typeface.createFromFile(pathFonts+"Chalkboard.ttf");
		
		//画竖线用的字体
		CommonUtils.LogWuwei(tag, "example font path is "+pathFonts+"Chalkduster.ttf");
		fontFaceVerticalLine = Typeface.createFromFile(pathFonts+"Chalkduster.ttf");
		
		
		rl_server = (RelativeLayout) findViewById(R.id.rl_server);
		button_menu_hiden = (Button)findViewById(R.id.button_menu_hide);
		logoButton = new Button(this);
		logoButton.setBackgroundResource(R.drawable.wuwei);
		
		Boolean flag = true;
		CommonUtils.LogWuwei(tag, "检查本周菜单，得到有那些天有菜单");
		
		
		for(int i=0;i<menu_list_int.length;i++)
		{
			menu_list_int[i] = -1;
		}
		 
		
	}
	
	
	/**
	 * 2、电视菜单的控件配置
	 */
	void widgetConfigureShowMenu()
	{
		
		keyboard_menu_type_index = getMenuType();
		
		handlerShowMenu = new Handler()
		{
			public void handleMessage(android.os.Message msg) 
			{
				switch (msg.what)
				{
				case SHOW_MENU_WIDGET:
					if(ClientMenuServer.flag_refresh_tv_screen)
					{
						 ShowMenuActivity.rl_server.removeAllViews();
					}
					paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
					break;
				case CLEAN_VIEWS:
					CommonUtils.LogWuwei(tag, "views are being cleaned..");
					ShowMenuActivity.rl_server.removeAllViews();
					break;
				case REGISTER_FOR_CONTEXT_MENU:
					View v = (View)msg.obj;
					registerForContextMenu(v);
					v.setOnTouchListener(new NewFromHistoryMenuActivity());
					break;
				case UPDATE_TEXT_SOLD_ALL:
					if(dayFromToday == 0)// && keyboard_menu_type_index == getMenuType())
					{
						Button button = (Button)msg.obj;
						CommonUtils.LogWuwei(tag, "————————————————————————显示售罄菜品："+button.getText()+"——————————————————————");
						button.setText(button.getText().toString());
						button.setAlpha(0.2f);
						Button buttonPrice = (Button)findViewById(button.getId()+1000);
						if(buttonPrice != null )
						{
							String price_str = buttonPrice.getText().toString();
							buttonPrice.setAlpha(0.2f);
							
							boolean flag = true;
							
							Button btn_soldAll = (Button)findViewById(button.getId()+2000);
							if(btn_soldAll != null)
							{
								if(btn_soldAll.getParent() != null)
								{
									flag = false;
								}
							}
							
							if(flag)
							{
								Button button_soldAll = new Button(getApplicationContext());
								button_soldAll.setId(button.getId()+2000);
								Drawable drawable = ctxt.getResources().getDrawable(R.drawable.sold_empty);
								
								int location[] = new int[2];
			    					button.getLocationOnScreen(location);
								button_soldAll.setX(location[0]+button.getWidth()-40);
								button_soldAll.setY(location[1]+20);
								button_soldAll.setBackgroundDrawable(drawable);
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(110, 82);
								rl_server.addView(button_soldAll,lp);
							}
							
						}
	    					
						
					}
					break;
				case CANCEL_TEXT_SOLD:
					if(dayFromToday == 0);// && keyboard_menu_type_index == getMenuType())
					{
						Button btn= (Button)findViewById((Integer)msg.obj);
						Button btn_price= (Button)findViewById((Integer)msg.obj+1000);
						Button btn_soldAll = (Button)findViewById((Integer)msg.obj+2000);
						if(btn != null )
						{
							CommonUtils.LogWuwei(tag,"     " +btn.getText()+"UI准备取消售罄状态");
							btn.setAlpha(1f);
							if(btn_price != null)
							{
								btn_price.setAlpha(1f);	
							}
							if(btn_soldAll != null)
							{
								if(btn_soldAll.getParent() != null)
								{
									rl_server.removeView(btn_soldAll);
									CommonUtils.LogWuwei(tag, "     " +btn.getText()+"UI准备取消售罄成功");
								}
							}
						}
					}
					break;
				case UPDATE_PROGRESS_BAR:
					int progress_now = Integer.parseInt((String)msg.obj);
					
					CommonUtils.LogWuwei(tag,"progress is "+progress);
					
					if(mRateTextCircularProgressBar != null)
					{
						mRateTextCircularProgressBar.setProgress(progress_now);
						
						if(progress_now >= 99)
						{
							dlg_prrgress_bar.dismiss();
							progress = 0;
							showUpdateResult();
						}
					}
					break;
				case SHOW_PROPER_MENU:
					showProperMennu();
					break;
				case UPDATE_ALL:
					updateAll();
					break;
				case UPDATE_MENU_IN_SERVER:
					Message MsgFromServer = (Message)msg.obj;
					ShowMenuActivity.paintTv(MsgFromServer, rl_server, true, ctxt);
					break;
				case CLEAN_VIEW:
					Button button = (Button)findViewById((Integer)msg.obj);
					if(button != null)
					{
						ShowMenuActivity.rl_server.removeView(button);
					}
					
					Button button_price = (Button)findViewById((Integer)msg.obj+1000);
					if(button_price != null)
					{
						ShowMenuActivity.rl_server.removeView(button_price);
					}
					break;
				case SHOW_VIEW:
					Button button_show = (Button)findViewById((Integer)msg.obj);
					if(button_show != null)
					{
						ShowMenuActivity.rl_server.addView(button_show);
					}
					break;
				}
			}
		};
	   
		gestureDetector = new gesture(this,new gesture.OnGestureResult() {  
			@Override
            public void onGestureResult(int direction) {
				
			 	QueryBuilder qb = ShowMenuActivity.menuDetailDao.queryBuilder();
			    	qb.orderAsc(MenuDetailDao.Properties.MenuDateId);
			    	List<MenuDetail> listResult = qb.list();
			    	if(listResult.size() != 0)
			    	{
			    			smallest_margin= Long.parseLong(listResult.get(0).getMenuDateId());
			    			biggest_margin=Long.parseLong(listResult.get(listResult.size()-1).getMenuDateId());
			    			for(int i=0;i<NowEmptyList.length;i++)
			    			{
		    					NowEmptyList[i] = 0;
			    			}
		    			   
		    			   for(int i =0;i<emptyReserveListFromServer.length;i++)
		    			   {
		    					   emptyReserveListFromServer[i][0] = null;
		    					   emptyReserveListFromServer[i][1] = null;
		    			   }
		    			   
		    			   switch (direction)
		                  {
		                  case 0:
		                	  	down();
		                	  	if(dayFromToday == 0)
			        			{
			        				nowNameIdFromDatabase = getNameIdFromDataBase();
				        			getNowBindComboInfo(1);
				        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
			        			}
		                	  	break;			                	  
		                  case 1:
		                	  	up();
		                	  	if(dayFromToday == 0)
			        			{
			        				nowNameIdFromDatabase = getNameIdFromDataBase();
				        			getNowBindComboInfo(1);
				        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
			        			}
		                	  	break;
		                  case 2:
		                	  	right();
		                	  	if(dayFromToday == 0)
		   	       			 {
		   	        				nowNameIdFromDatabase = getNameIdFromDataBase();
		   		        			getNowBindComboInfo(1);
		   		        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
		   	       			 }
		                	  	break;			                	  
		                  case 3:
		                	  	left();
		                	  	if(dayFromToday == 0)
		   	       			 {
		   	        				nowNameIdFromDatabase = getNameIdFromDataBase();
		   		        			getNowBindComboInfo(1);
		   		        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
		   	       			 }
		                	  	break;
		                  default:
		                  }
		        }
            }  
        }  
        ).Buile();  
		
		button_menu_hiden.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Runtime runtime = Runtime.getRuntime();
				try {
					CommonUtils.LogWuwei(tag, "pressed success");
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_MENU);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CommonUtils.LogWuwei(tag, "pressed failed "+e.getMessage());
				}
			}
		});
		
		nowNameIdFromDatabase = getNameIdFromDataBase();
		getNowBindComboInfo(1);
		nowBindSfoodSingleId = getNowBindSfoodSingleId();
	  
		launcherTimerScanReserves();	   
	   for(int i=0;i<NowEmptyList.length;i++)
	   {
			NowEmptyList[i] = 0;
	   }
	   
	   for(int i =0;i<emptyReserveListFromServer.length;i++)
	   {
		   emptyReserveListFromServer[i][0] = null;
		   emptyReserveListFromServer[i][1] = null;
	   }
	   
	   FrameLayout.LayoutParams lp =  (FrameLayout.LayoutParams) rl_server.getLayoutParams();
	   lp.width = CommonUtils.getScreenWidth(ctxt);
	   lp.height =  CommonUtils.getScreenHeight(ctxt);
		
	   
	   for(int i=0;i<flagUpdateSuccess.length;i++)
	   {
		   flagUpdateSuccess[i][0] = false;
		   flagUpdateSuccess[i][1] = false;
		   flagUpdateSuccess[i][2] = false;
	   }
	   
	}
	
	
	/**
	 * 3、开启接受手机端菜单数据的服务并指定端口号
	 * @param port
	 */
	void startServer(int port)
	{
		String ip_address = NetWorkUtils.GetWifiIp(ctxt);
		CommonUtils.LogWuwei(tag, "ip_address is "+ip_address);
		nanoHttpd = new ClientMenuServer(NetWorkUtils.GetWifiIp(ctxt),port,ctxt);
		
		try{
			nanoHttpd.start();
			HandlerUtils.showToast(ctxt, "服务器开启成功");
			CommonUtils.LogWuwei(tag, " 	 starts success!");
		}catch(Exception e){
			e.printStackTrace();
			HandlerUtils.showToast(ctxt, "服务器开启失败"+e.getMessage());
			CommonUtils.LogWuwei(tag, "server starts failure!");
		}
	}
   
	
    /**
     * 4、当电视端选择显示菜单的时候，查询数据库看是否有适合现在的菜单，有的话直接显示出来，没有什么都不做
     */
    public  void showProperMennu()
    {
    	
    		Long menuDetailTableCount = menuDetailDao.count();
    		Long menuTableCount = menuTableDao.count();
    		if(menuDetailTableCount == 0 && menuTableCount == 0)//两张表中有一个为空，则跳出该函数
    		{
    			return;
    		}

    		//获取现在应该的菜单类型，用于在数据中进行查询
    		int menu_type = getMenuType();//0->早餐 1->午餐 2->晚餐
    		keyboard_date_menu_index = Long.parseLong(CommonUtils.getNowDateString());
    		keyboard_menu_type_index = menu_type;
    		
    		//查看对应的menuTable表是否存在
    		QueryBuilder qb = menuDetailDao.queryBuilder();
    		qb.where(MenuDetailDao.Properties.MenuDateId.eq(CommonUtils.getNowDateString()+menu_type));
    		List listResult = qb.list();
    		
    		QueryBuilder qbTmp = menuDetailDao.queryBuilder();
    		qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index+2000)+menu_type));
    		List listResultTmp = qbTmp.list();
    		if(listResultTmp.size() > 0)
    		{
    			listResult = listResultTmp;
    		}
    		
    		listResultRequestServerData = listResult;
    		
    		if(listResult.size() == 0)
    		{
    			cleanScreenWhenNoMenu();
    		}
    		else
    		{
    			rl_server.removeAllViews();
    			for(int i=0;i<listResult.size();i++)
    			{
    				MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
    				
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
	    			paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
    			}
    			
    		}
    		
  			button_menu_hiden = new Button(this);
			
			button_menu_hiden.setBackgroundResource(android.R.color.transparent);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 50);
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
			rl_server.addView(button_menu_hiden,lp);
			
			button_menu_hiden.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Runtime runtime = Runtime.getRuntime();
					try {
						CommonUtils.LogWuwei(tag, "pressed success");
						runtime.exec("input keyevent " + KeyEvent.KEYCODE_MENU);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						CommonUtils.LogWuwei(tag, "pressed failed "+e.getMessage());
					}
				}
			});
    		
    }
	
    
	/**
	 * 指定按下遥控器菜单键时的布局文件
	 */
    @Override
   	public boolean onCreateOptionsMenu(Menu menu) {
  		getMenuInflater().inflate(R.menu.show_menu, menu);
  		return super.onCreateOptionsMenu(menu);
  	}
      

	/**
	 * 对创建的菜单经常switch的选择
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.MenuScanSwitch:
			if(flagTimerTaskRunning)//如果已经开启
			{
				HandlerUtils.showToast(ctxt,  "已关闭库存同步功能\n 将无法看到售罄菜品");
				cancelTimerScanReserves();
			}
			else
			{
				HandlerUtils.showToast(ctxt,  "已开启库存同步功能\n 可以实时更新菜品售罄情况");
				launcherTimerScanReserves();
			}
			break;
		case R.id.MenuOptionWifi:
			NetWorkUtils.showWifiStatus(ctxt);
			return true;
		case R.id.MenuDeleteMenu:
			askDeleteMenu();
			break;
		case R.id.MenuUpdateMenu:
			askUpdateMenu();
			break;
		}
		return false;
	}
	
	
	/**
	 * 询问用户是选择删除本周菜单还是下周菜单
	 */
	public void askDeleteMenu()
	{
		final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(ShowMenuActivity.this);
        Effectstype effect;
        effect=Effectstype.Shake;
        String[] thisWeek = CommonUtils.getThisWeekDate();
        String[] nextWeek = CommonUtils.getNextWeekDate();
        
        String thisWeekStr = CommonUtils.parseDate(Long.parseLong(thisWeek[0]))+"~"+CommonUtils.parseDate(Long.parseLong(thisWeek[6]));
        String nextWeekStr = CommonUtils.parseDate(Long.parseLong(nextWeek[0]))+"~"+CommonUtils.parseDate(Long.parseLong(nextWeek[6]));
        
        dialogBuilder
                .withTitle("删除菜单")//.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessageColor("#FFFFFF")                                //def
                .withIcon(R.drawable.wuwei)
                .withMessage("\n\n请选择要删除的菜单:\n本周菜单("+thisWeekStr+")\n下周菜单("+nextWeekStr+")"+"\n\n")
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .onkeyAnswer()
                .withDuration(500)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("删除本周")                                      //def gone
                .withButton2Text("删除下周")
                .setButton1Click(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogBuilder.dismiss();
						deleteWeekMenu(0);
					}
				})
				.setButton2Click(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogBuilder.dismiss();
						deleteWeekMenu(1);
					}
				})
                .show();
        
	}
	
	
	/**
	 * 执行删除菜单过程
	 * @param flag->0 删除本周  flag->1 删除下周
	 */
	void deleteWeekMenu(final int flag)
	{
		
		String dateId[] = null;
		String str = null;
		String tips = null;
		final Boolean[] flag_user_delete = new Boolean[]{true,false};
		if(flag == 0)
		{
			//dateId = CommonUtils.getThisWeekDate();
			str = "本周菜单删除完毕";
			tips = "确定删除本周菜单?";
		}
		else if(flag == 1)
		{
			//dateId = CommonUtils.getNextWeekDate();
			str = "下周菜单删除完毕";
			tips = "确定删除下周菜单?";
		}
		
		
		AlertDialog dlg = new AlertDialog.Builder(ShowMenuActivity.this)
		.setTitle(tips)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				flag_user_delete[0] = true;
				String dateId[] = null;
				
				if(flag == 0)
				{
					dateId = CommonUtils.getThisWeekDate();
				}
				else if(flag == 1)
				{
					dateId = CommonUtils.getNextWeekDate();
				}
				
				for(int i=0;i<7;i++)
				{
					for(int k=0;k<3;k++)
					{
							Long date_tmp = Long.parseLong(dateId[i])*10+k;//201412190
							 QueryBuilder qb = menuDetailDao.queryBuilder();
							 DeleteQuery<MenuDetail> dq =
									 qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp)),MenuDetailDao.Properties.WidgetId.ge("0"))
			          				.buildDelete();
			              	dq.executeDeleteWithoutDetachingEntities();
			              	 
			              	QueryBuilder qb_tmp = menuDetailDao.queryBuilder();				              	
			              	DeleteQuery<MenuDetail> dq_tmp =
									 qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp+20000)),MenuDetailDao.Properties.WidgetId.ge("0"))
			          				.buildDelete();
			              	dq_tmp.executeDeleteWithoutDetachingEntities();
			              	CommonUtils.LogWuwei(tag, "delete tmp date "+(date_tmp+20000));
			              	
			              	dq = null;
					}
				}
				
				if(keyboard_date_menu_index > Long.parseLong(dateId[0]) && keyboard_date_menu_index < Long.parseLong(dateId[6]))
		      	{
		      		rl_server.removeAllViews();	
		      	}
				cleanScreenWhenNoMenu();
				
				
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				flag_user_delete[0] = false;
			}
		}).create();
		dlg.show();
		
		if(flag_user_delete[0] == false)
		{
			return;
		}
		
		}
	
	
	/**
	 * 询问用户是选择同步本周菜单还是下周菜单
	 */
	public void askUpdateMenu()
	{
		final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(ShowMenuActivity.this);
        Effectstype effect;
        effect=Effectstype.Slidetop;
        String[] thisWeek = CommonUtils.getThisWeekDate();
        String[] nextWeek = CommonUtils.getNextWeekDate();
        
        String thisWeekStr = CommonUtils.parseDate(Long.parseLong(thisWeek[0]))+"~"+CommonUtils.parseDate(Long.parseLong(thisWeek[6]));
        String nextWeekStr = CommonUtils.parseDate(Long.parseLong(nextWeek[0]))+"~"+CommonUtils.parseDate(Long.parseLong(nextWeek[6]));
        
        dialogBuilder
                .withTitle("同步菜单")//.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessageColor("#FFFFFF")                                //def
                .withIcon(R.drawable.wuwei)
                .withMessage("\n\n请选择要从服务器同步到电视的菜单:\n本周菜单("+thisWeekStr+")\n下周菜单("+nextWeekStr+")"+"\n\n")
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .onkeyAnswer()
                .withDuration(500)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("同步本周")                                      //def gone
                .withButton2Text("同步下周")
                .setButton1Click(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogBuilder.dismiss();
						dialog_progress_bar_show(0);
						new Thread()
						{
							public void run()
							{
								updateWeekMenu(0);		
							}
						}.start();
						
					}
				})
				.setButton2Click(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogBuilder.dismiss();
						dialog_progress_bar_show(1);
						new Thread()
						{
							public void run()
							{
								updateWeekMenu(1);		
							}
						}.start();
					}
				})
                .show();
	}
	
	
	/**
	 * 执行同步菜单（从服务器到电视本地）的过程
	 * @param flag->0 同步本周菜单 flag->1 同步下周菜单
	 */
	public void updateWeekMenu(int flag)
	{
		
		//1、获取选择的周一到周五的日期
		String date[] = null;
		if(flag == 0)
		{
			date = CommonUtils.getThisWeekDate();
		}
		else if(flag == 1)
		{
			date = CommonUtils.getNextWeekDate();
		}
		
		for(int i=0;i<date.length;i++)
		{
			try {
				
				Thread.sleep(50);
				
				//1、得到每天对应的时间戳
				long timeStamp = CommonUtils.getTimeStamp(Long.parseLong(date[i]));

				//2、得到每天的菜单json数据
				//String dayMenuDisplayJson = RequestServerData.SendPostRequest(-1,9,timeStamp,null);

				//3、将json数据写入数据库(注意点：1、是否为临时日期id 2、写入到临时数据中)
				//updateMenuDataByJson(dayMenuDisplayJson,i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		Message msg = new Message();
		msg.what = SHOW_PROPER_MENU;
		handlerShowMenu.sendMessage(msg);
		
	}
	
	
	/**
	 * 将从服务器获取到的json数据写入电视本地
	 */
	public void updateMenuDataByJson(String dayMenuDisplayJson,int nowWeekDay)
	{
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("\\\\\\\"","^");//将（\\"）转义为(^)
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("\\\"","\"");//将（\"）转义为(")
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("^","\\\\\"");//将（^）转义为(\\")
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("\"[","[");//将（"[）转义为([)
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("]\"","]");//将（]"）转义为(])
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("}\"","}");//将（}"）转义为(})
		
		dayMenuDisplayJson = dayMenuDisplayJson.replace("\\\\\"","\\\"");//将（\\"）转义为(\")
		
		
		String strBefore = dayMenuDisplayJson;
		dayMenuDisplayJson = dayMenuDisplayJson.replace("\\/","/");//将（\/）转义为(/)
		String strAfter = dayMenuDisplayJson;
		
		
		/*String tag = "jsonTestUpdateMenu";*/
		JSONTokener parser_json = new JSONTokener(dayMenuDisplayJson);
		JSONArray test_object = null;
		
		try {
			
			test_object = new JSONObject(dayMenuDisplayJson).getJSONArray("daymenudisplays");
			
				
			for(int i=0;i<test_object.length();i++)
			{
				
				progress++;
				Message msg = new Message();
				msg.what = UPDATE_PROGRESS_BAR;
				msg.obj = Integer.toString((int)(progress*100/21));
					//mRateTextCircularProgressBar.setProgress((100*progress/21));
				handlerShowMenu.sendMessage(msg);
				
				JSONObject testItem = (JSONObject) test_object.get(i);
				
				JSONArray paramArray = testItem.getJSONArray("parameters");
				
				String MenuName = testItem.getString("name");
				if(MenuName.contains("早餐"))
				{
					CommonUtils.LogWuwei("now_update", "现在更新的是周"+(nowWeekDay+1)+"早餐");
					flagUpdateSuccess[nowWeekDay][0] = true; 
				}
				else if(MenuName.contains("午餐"))
				{
					CommonUtils.LogWuwei("now_update", "现在更新的是周"+(nowWeekDay+1)+"午餐");
					flagUpdateSuccess[nowWeekDay][1] = true;
				}
				else if(MenuName.contains("晚餐"))
				{
					CommonUtils.LogWuwei("now_update", "现在更新的是周"+(nowWeekDay+1)+"晚餐");
					flagUpdateSuccess[nowWeekDay][2] = true;
				}
				
				for(int k=0;k<paramArray.length();k++)
				{
					 JSONObject jsonobj = paramArray.getJSONObject(k);
					 String menu_id_str = jsonobj.getString("menu_id_str");
					 String date_id = jsonobj.getString("date_id");//1-->date_id是包含菜单类型的，举例201411210
					 String widget_id = jsonobj.getString("widget_id");//2
					 String type = jsonobj.getString("type");//3
					 
					 String name =jsonobj.getString("name");//4
					 name = name.replace("\\/", "/");
					 //CommonUtils.LogWuwei(tag, "name is "+name);
					 
					 String x = jsonobj.getString("x");//5
					 String y = jsonobj.getString("y");//6
					 String price = jsonobj.getString("price");//7
					 String font_size = jsonobj.getString("font_size");//8
					 String font_color = jsonobj.getString("font_color");//9
					 String background_color = jsonobj.getString("background_color");//10
					 String distance = jsonobj.getString("distance");
					 String additiontal = jsonobj.getString("additiontal");
					 String bindType = jsonobj.getString("bindType");
					 String bindToItemServerId = jsonobj.getString("bindServerItemId");
					 String redundance1 = jsonobj.getString("redundance1");
					 String redundance2 = jsonobj.getString("redundance2");
					 String redundance3 = jsonobj.getString("redundance3");
					 String redundance4 = jsonobj.getString("redundance4");
					 String redundance5 = jsonobj.getString("redundance5");
					 
					 if(k == 0)
					 {
						 	
						 	Long date = Long.parseLong(menu_id_str);
						 	Long dateTmp = Long.parseLong(menu_id_str)+20000;
						 	
						 	QueryBuilder qb = menuDetailDao.queryBuilder();
							qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)));
							List listResult = qb.list();
							
							QueryBuilder qbTmp = menuDetailDao.queryBuilder();
							qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)));
							List listResultTmp = qbTmp.list();
							
							
							DeleteQuery<MenuDetail> dq = 
									qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
									 MenuDetailDao.Properties.WidgetId.ge(0))
									.buildDelete();
								
					  		dq.executeDeleteWithoutDetachingEntities();
					  		
					  		DeleteQuery<MenuDetail> dqTmp = 
									qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date)),
									 MenuDetailDao.Properties.WidgetId.ge(0))
									.buildDelete();
								
					  		dqTmp.executeDeleteWithoutDetachingEntities();
							
					 }
					 
					date_id = "0";
					
					 if(bindToItemServerId.equals(""))
					 {
						bindToItemServerId = "0";
					 }
					 
					 double distance_double = 0;
					 if(distance != null && !distance.equals(""))
					 {
						 distance_double = Double.parseDouble(distance);
					 }
					 
					 MenuDetail menu_detail_entity = new MenuDetail(
							 menu_id_str,
							 Long.parseLong(date_id), 
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
					 ShowMenuActivity.menuDetailDao.insertOrReplace(menu_detail_entity);
				}
				
				 
				//String testParams = testItem.getString("parameters");
				//CommonUtils.LogWuwei(tag, "\n|\n|\n|testParams is "+testParams);
			}
			
			
			try {
				if(nowWeekDay == 6)
				{
					if(test_object.length() == 0)
					{
						Thread.sleep(100);
						progress = 19;
						Message msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
						
						Thread.sleep(100);
						progress = 20;
						msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
						
						Thread.sleep(100);
						progress = 21;
						msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
					}
					else if((test_object.length() == 1))
					{
						Thread.sleep(100);
						progress = 20;
						Message msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
						
						Thread.sleep(100);
						progress = 21;
						msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
					}
					else if((test_object.length() == 2))
					{
						Thread.sleep(100);
						progress = 21;
						Message msg = new Message();
						msg.what = UPDATE_PROGRESS_BAR;
						msg.obj = Integer.toString((int)(progress*100/21));
							//mRateTextCircularProgressBar.setProgress((100*progress/21));
						handlerShowMenu.sendMessage(msg);
						
					}
					
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommonUtils.LogWuwei(tag, "JSONException error:"+e.getMessage());
		}
	}
	
	
	/**
	 * 支持通过手势更新菜单
	 */
	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
	        return gestureDetector.onTouchEvent(event);  
	    }
	
	
	/**
	 * 显示同步的结果
	 */
	public void showUpdateResult()
	{
		boolean flag = true;
		int flag_true = 0;
		int flag_false = 0;
		
		for(int i=0;i<flagUpdateSuccess.length;i++)
		{
			for(int k=0;k<3;k++)
			{
				if(flagUpdateSuccess[i][k] == true)
				{
					flag_true++;
				}else
				{
					flag_false++;
				}
			}
			
		}

		if(flag_true == 0)
		{
			HandlerUtils.showToast(ctxt, "没有菜单可以同步");
		}
		else
		{
			HandlerUtils.showToast(ctxt, "菜单同步成功");
		}
		
		for(int i=0;i<flagUpdateSuccess.length;i++)
		{
		   flagUpdateSuccess[i][0] = false;
		   flagUpdateSuccess[i][1] = false;
		   flagUpdateSuccess[i][2] = false;
		}
	}
	
	
	 /**
	   * 弹出窗口用于显示同步的进度
	   * @param flag->0 本周 1->下周   cloundUpdate->true 同步到云端 false->同步到电视
	   */
	public void dialog_progress_bar_show(int flag)
	  {
		  LayoutInflater factory = LayoutInflater.from(getApplicationContext());
	      
		  final View DialogView = factory.inflate(R.layout.circular_progress_bar, null);
		  
		  dlg_prrgress_bar = new Dialog(ShowMenuActivity.this);
		  dlg_prrgress_bar.setContentView(DialogView);
		  dlg_prrgress_bar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		  dlg_prrgress_bar.setCanceledOnTouchOutside(false);
		  if(flag == 0)
		  {
			  dlg_prrgress_bar.setTitle("正在同步\"本周菜单\"到电视");	  
			  
		  }
		  else
		  {
			  dlg_prrgress_bar.setTitle("正在同步\"下周菜单\"到电视");	  			  
		  }
		  dlg_prrgress_bar.show();
		  dlg_prrgress_bar.setCancelable(false);
		  
		mRateTextCircularProgressBar = (RateTextCircularProgressBar)dlg_prrgress_bar.findViewById(R.id.client_menu_update_progress_bar);
		
		 
		/*  RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mRateTextCircularProgressBar.getLayoutParams();
		  layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		  mRateTextCircularProgressBar.setLayoutParams(layoutParams);*/
		
		mRateTextCircularProgressBar.setMax(100);
		mRateTextCircularProgressBar.setTextColor(Color.WHITE);
		mRateTextCircularProgressBar.setProgress(0);
		mRateTextCircularProgressBar.getCircularProgressBar().setCircleWidth(20);
	  }
	
	
	/**
	 * 在屏幕上绘制菜单
	 * @param msg:传递过来的信息，可以解析得到具体item的信息
	 * @param rl：一个相对布局，用来添加item
	 * @param flag：flag->true 不可编辑; flag->false 可编辑
	 */
	public static void paintTv(Message msg,RelativeLayout rl,Boolean flag,Context ctxt)
	{
		//EditText new_button = new EditText(splash.contextTools);
		//if(flag)
		/*{
			new_button.setFocusable(false);
			new_button.clearFocus();
		}*/
		
		Button new_button = new Button(ctxt);
		
		if(!flag)
		{
			new_button.setLongClickable(false);
			new_button.setOnLongClickListener(null);
			new_button.setOnDragListener(null);
			new_button.setOnEditorActionListener(null);
			new_button.setOnFocusChangeListener(null);
			if(NewFromHistoryMenuActivity.ocl != null)
			{
				new_button.setOnClickListener(NewFromHistoryMenuActivity.ocl);
				new_button.setOnTouchListener(new NewFromHistoryMenuActivity());
				new_button.setCustomSelectionActionModeCallback(NewFromHistoryMenuActivity.ac);
			}
			new_button.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

			Message msg_register = new Message();
			msg_register.what = REGISTER_FOR_CONTEXT_MENU;
			msg_register.obj = new_button;
			if(handlerShowMenu != null)
			{
				handlerShowMenu.sendMessage(msg_register);
			}
		}
		else
		{
			//CommonUtils.LogWuwei(tag, "不可以点击，不可以拖拽");
			new_button.setFocusable(false);
			new_button.setOnTouchListener(null);
		}
		
		View view = new_button;
		
		//new_button.addTextChangedListener(new textWatcherMobile(view));
		/* 
        * no sql*/
		String date_id = msg.getData().getString("date_id");
        String x_str = msg.getData().getString("x");//接受msg传递过来的参数
		double x = 0;
		if(x_str != null)
		{
			x = Double.parseDouble(x_str)*x_fraction;
		}
			
        String y_str = msg.getData().getString("y");//接受msg传递过来的参数
        double y = 0;
        if(y_str != null )
        {
        	 	y =  Double.parseDouble(y_str)*y_fraction;
        }
        
        String name = msg.getData().getString("name");
        String id = msg.getData().getString("widget_id");
        String font_color = msg.getData().getString("font_color");
        String font_size = msg.getData().getString("font_size");
        String price = msg.getData().getString("price");
        String type = msg.getData().getString("type");
        String background_color = msg.getData().getString("background_color");
        String distance = msg.getData().getString("distance");
        String additiontal = msg.getData().getString("additiontal");
        String bindToItemServerId = msg.getData().getString("BindToItemServerId");
		String redundance1 = msg.getData().getString("redundance1");
		String redundance2 = msg.getData().getString("redundance2");
		String redundance3 = msg.getData().getString("redundance3");
		String redundance4 = msg.getData().getString("redundance4");
		String redundance5 = msg.getData().getString("redundance5");
		 
		if(bindToItemServerId == null || bindToItemServerId.equals(""))
		{
			 bindToItemServerId = "0";
		}
		
        if(id != null)
        {
        		new_button.setId(Integer.parseInt(id));
        }
        else
        {
        		return;
        }
		

        if(font_color != null && font_color != "" && (font_color.length() == 9))
        {
        		char sb = font_color.charAt(0);
	        if(sb == '#')
	        {
	        		//LocalDataDeal.writeToLocalColor(font_color);
	        }
        }
        
		if(!type.equals("pic") && !type.equals("line") && !type.equals("line_vertical"))
		{
		    if(type.equals("tips"))//tips放在右下角
			{
				RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				
				if(price.equals("0"))
				{
					lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
					lp1.setMargins((int) (x), 0, 0, CommonUtils.px2dip(ctxt, 64));	
				}
				else
				{
				     lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				     lp1.setMargins(0, 0, 0, CommonUtils.px2dip(ctxt, 64));
				}
		   
		        
		        rl.addView((View)new_button,lp1);
				
		        SpannableStringBuilder newText = new SpannableStringBuilder();  
		        newText.append(name);
		        new_button.setText(newText);  
		        new_button.setTextSize(CommonUtils.px2dip(ctxt,50));
		        new_button.setTypeface(fontFaceChalkBoard);
				
			}
		    else if(type.equals("text")) //text
			{
				new_button.setX((float) x);
				new_button.setY((float) y);
				if(price.length()!= 0)
				{
					if(Integer.parseInt(price) == 888)//标题
					{
						new_button.setText(name);
						new_button.setTypeface(fontFacePianPian);
						new_button.setText(name);
						//CommonUtils.LogWuwei(tag, "addview price is 888");
						new_button.setTextSize(CommonUtils.px2dip(ctxt,60));
						rl.addView((View)new_button);
					}
					else
					{
						new_button.setTextSize(CommonUtils.px2dip(ctxt,50));
						
						if(Integer.parseInt(price) == 0)//无价格菜品(猪肉大葱/猪肉白菜包子)
						{
							new_button.setText(name);
							rl.addView((View)new_button);
						}
						else if(Integer.parseInt(price) != 0)//有价格菜品
						{
							new_button.setText(name);
							new_button.setTypeface(fontFaceChalkBoard);//自定义显示字体
							rl.addView((View)new_button);
							
							Button button_price = new Button(ctxt);
							button_price.clearFocus();
							button_price.setId(Integer.parseInt(id)+1000);//对价格的edittext设置id，id为item本身自增1000
							button_price.setFocusable(false);
							
							
							if( background_color != null && !background_color.equals("0x000") && !background_color.equals("0x0000") )
							{
								if(Integer.parseInt(background_color) == 1)
								{
									Drawable drawable = ctxt.getResources().getDrawable(R.drawable.pepper);
								    drawable.setBounds(0, 0, 24,75);
								    new_button.setCompoundDrawables(null, null, drawable, null);
								    new_button.setCompoundDrawablePadding(35);
								}
							}
							
							if(additiontal != null && !additiontal.equals(""))
							{
								if(CommonUtils.isNumeric(additiontal))
								{
									if(Integer.parseInt(additiontal) == 1)
									{
										Drawable drawable = ctxt.getResources().getDrawable(R.drawable.pepper);
									    drawable.setBounds(0, 0, 24,75);
									    new_button.setCompoundDrawables(null, null, drawable, null);
									    new_button.setCompoundDrawablePadding(35);
									}
										
								}
							}
							
							if(distance != null)
							{
								if(!distance.equals("") && distance != null )
								{
									button_price.setX((float) ((x)+Double.parseDouble(distance)*x_fraction));	
								}
							}
							else
							{
								CommonUtils.LogWuwei(tag, "distance is "+distance);
							}
							
							
							button_price.setY((float) (y));
							button_price.setText(price+"元");
							button_price.setTextSize(CommonUtils.px2dip(ctxt,50));//(float) (Float.parseFloat(font_size)*font_fraction));
							button_price.setBackgroundColor(Color.TRANSPARENT);
							button_price.setTypeface(fontFaceChalkBoard);//自定义显示字体
							rl.addView((View)button_price);
						}
					}
				}
				else
				{
					new_button.setText(name);
					new_button.setTypeface(fontFacePianPian);
					new_button.setText(name);
					//CommonUtils.LogWuwei(tag, "addview name is "+name);
					rl.addView((View)new_button);
				}
				
			}
		    else if(type.equals("theme"))//theme放在上边中间位置
		    {
		    		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);  
		        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);  
		        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		        lp1.setMargins(0, CommonUtils.px2dip(ctxt,64), 0, 0);
		        
		        Drawable drawable = ctxt.getResources().getDrawable(R.drawable.fang_xin_chi);
			    drawable.setBounds(0, 0, CommonUtils.px2dip(ctxt, 799),CommonUtils.px2dip(ctxt,120));
			    new_button.setCompoundDrawables(drawable, null, null, null);
			    new_button.setCompoundDrawablePadding(CommonUtils.px2dip(ctxt, 52));
		        
			    rl.addView((View)new_button,lp1);
				
		        new_button.setText(name);
				new_button.setTextSize(CommonUtils.px2dip(ctxt,60));
				new_button.setTypeface(fontFaceChalkBoard);//自定义显示字体
			}
			
		    new_button.setTextColor(Color.parseColor(font_color));
			new_button.setBackgroundColor(Color.TRANSPARENT);
			
		}
		else if(type.equals("line"))//划横线
		{
			
			new_button.setY((float) ((y)));//-20*y_fraction));
			new_button.setAlpha(0.3f);
			new_button.setTypeface(fontFaceVerticalLine);//自定义显示字体
			new_button.setTextSize((float) ((float) (Integer.parseInt(font_size)*font_fraction)*1.4 ));
			String text ="  ";
			for(int i=0;i<14;i++)
			{
				text = text+"---";
			}
			new_button.setText(text);
		    new_button.setBackgroundColor(Color.TRANSPARENT);
		    rl.addView((View)new_button);//,lp1);
			
		}
		else if(type.equals("line_vertical"))//划竖线，price代表行数，font_colot代表每行的间隙
		{
			new_button.setX((float) ((x)));//+15*x_fraction));
			new_button.setY((float) y);
			
			new_button.setAlpha(0.3f);
			new_button.setTypeface(fontFaceVerticalLine);//自定义显示字体
			new_button.setTextSize((float) ((float) (Integer.parseInt(font_size)*font_fraction)*1));
			String text ="|";
			for(int i=0;i<Integer.parseInt(price);i++)
			{
				text = text+"|";
			}
			
			new_button.setText(text);
			new_button.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
					(int) (5*x_fraction*10),(int) (Float.parseFloat(price)*Integer.parseInt(font_color)));//
			rl.addView((View)new_button,lp1);
		}
		else //logo 
		{	
			/*int width = (int) (100*x_fraction);
			int height = (int)(100*y_fraction);
			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(width,height);  
	        lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);  
	        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
	        lp1.setMargins(0, 50, 80, 0);
	        //CommonUtils.LogWuwei(tag, "addview logo button ");
	       if(logoButton.getParent() == null)
	       {
	    	   		rl.addView((View)logoButton,lp1);
	       }
	       else
	       {
	    	   		rl.removeView(logoButton);
	    	   		rl.addView(logoButton,lp1);
	       }*/
	       

		}
		
		ClientMenuServer.flag_refresh_tv_screen = false;
	}
	
	
    
	
    public void updateAll()
    {
		if(emptyReserveListFromServer[0][0] != null)
		{
			updateTvEmptyMenuStatus();
			cancelFromNowEmpty();
			
		}
		else
		{
			//CommonUtils.LogWuwei(tag, "准备处理数据。。。。emptyReserveListFromServer为空");
			int NowEmptyListLength = NowEmptyList.length;
			for(int k=0;k<NowEmptyListLength;k++)
			{
				
				if(NowEmptyList[k] == 0)
				{
					break;
				}
				
				Button v= (Button)findViewById(NowEmptyList[k]);
				CommonUtils.LogWuwei(tag, v.getText().toString()+"取消售罄状态(所有已售罄菜品都将标记为正常售卖状态)");
				
				Message msg = new Message();
				msg.obj =  NowEmptyList[k];
				msg.what = CANCEL_TEXT_SOLD;
				handlerShowMenu.sendMessage(msg);
				
			
				
				NowEmptyList[k] = 0;
				msg = null;
			}
				
		}
    }
   
    
	/**
	 * 启动一个定时器，用来轮询菜品库存
	 */
	void launcherTimerScanReserves()
	{
		nowBindSfoodSingleId = getNowBindSfoodSingleId();
		getNowBindComboInfo(0);
		
		nowNameIdFromDatabase = getNameIdFromDataBase();

		flagTimerTaskRunning = true;
		
		
		timerScanReserves = new Timer();
		timerTaskScanReserves = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
				boolean flag_run = false;
				timeScan = System.currentTimeMillis()/1000;
				
				Time time1 = new Time("GMT+8");       
		        time1.setToNow();      
		        int minute = time1.minute;      
		        int hour = time1.hour+8;      
		        int sec = time1.second; 
		        
		        if(!flagSendEmail)
		        {
		        		if(hour == 9 && minute == 50)
			        {
			        		//sendMail.sendEmail(3);
			        		flagSendEmail = true;
			        }
			        else if(hour == 13 && minute == 50)
			        {
			        		//sendMail.sendEmail(3);
			        		flagSendEmail = true;
			        }
			        else if(hour == 20 && minute == 20)
			        {
			        		//sendMail.sendEmail(3);
			        		flagSendEmail = true;
			        }
		        }
		        
		        
		        //CommonUtils.LogWuwei(tag," scan  unixTimeStamp:"+timeScan+"  now:  "+hour+":"+minute+":"+ sec+"[test]");;
				time1 = null;
				
				if(menu_list_int[0] == -1)
				{
					CommonUtils.LogWuwei(tag,"菜单列表为空");
					//RequestServerData.SendPostRequest(-1, 4, -1,null);
				}
				else
				{
					long UnixTimestamp = System.currentTimeMillis()/1000;
					
					//如果在当前菜单对应营业时间段，则发送获取库存的请求
					//if(UnixTimestamp > sale_on_off_long[keyboard_menu_type_index][0] && UnixTimestamp< sale_on_off_long[keyboard_menu_type_index][1])
					{
						flag_run = true;
						Time time = new Time("GMT+8");       
				        time.setToNow();      
				        minute = time.minute;      
				        hour = time.hour;      
				        sec = time.second;      
				    //    CommonUtils.LogWuwei(tag,(hour+8)+":"+minute+":"+ sec);
				        time = null;
						//RequestServerData.SendPostRequest(-1,3,-1,null);//获取是否有菜品售罄
					}
				}
		}
		};
		timerScanReserves.schedule(timerTaskScanReserves,1000, 15000);
		
		 timerModify = new Timer();
	     timerTaskModify = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeModify = System.currentTimeMillis()/1000;
				
				Time time1 = new Time("GMT+8");       
		        time1.setToNow();      
		        int minute = time1.minute;      
		        int hour = time1.hour;      
		        int sec = time1.second;    
		        								
				//CommonUtils.LogWuwei(tag, "modify unixTimeStamp:"+timeModify+" now:  "+(hour+8)+":"+minute+":"+ sec+"<br><br<br>>"+"[test]");
				
				time1 = null;
				
				if((timeModify - timeScan)>90)
				{
					CommonUtils.LogWuwei(tag, "\n1\n2\n3————————————"+"售罄定时器挂了，准备重启中"+"\n1\n2\n3"+"[test]");
					
					cancelTimerScanReserves();
					
					
					launcherTimerScanReserves();
					
					
				}
			}
		};
		timerModify.schedule(timerTaskModify,1000, 30000);
		
	}
	
	
	/**
	 * 取消查询库存的定时器
	 */
	void cancelTimerScanReserves()
	{
		
		if(timerScanReserves != null)
		{
			timerScanReserves.cancel();
			timerScanReserves = null;
		}
		
		if(timerTaskScanReserves != null)
		{
			timerTaskScanReserves.cancel();
			flagTimerTaskRunning = false;
			timerTaskScanReserves = null;
		}
		
		if(timerModify != null)
		{
			timerModify.cancel();
			timerModify = null;
		}
		
		if(timerTaskModify != null)
		{
			timerTaskModify.cancel();
			timerTaskModify = null;
		}
		
		if(nowBindComboId_SfoodId_Sfood_Amount_WidgetId != null)
		{
			int length = nowBindComboId_SfoodId_Sfood_Amount_WidgetId.length;
			for(int i=0;i<length;i++)
			{
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][0] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][1] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][2] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][3] = 0;
			}
		}
		if(nowBindSfoodSingleId != null)
		{
			int length = nowBindSfoodSingleId.length;
			for(int i=0;i<nowBindSfoodSingleId.length;i++)
			{
				nowBindSfoodSingleId[i][0] = 0;
				nowBindSfoodSingleId[i][1] = 0;
			}
		}
		
		//定时器重启时，不应该情况现有的售罄列表，要不然，不知道下次获取到售罄列表时，不知道如何
	/*	if( NowEmptyList != null)
		{
			int length = NowEmptyList.length;
			for(int i=0;i<length;i++)
			{
				NowEmptyList[i] = 0;
			}
		}*/
	
		if( nowNameIdFromDatabase != null)
		{
			int length = nowNameIdFromDatabase.length;
			for(int i=0;i<length;i++)
			{
				nowNameIdFromDatabase[i][0] = null;
				nowNameIdFromDatabase[i][1] = null;
			}
		}
		
	}
	
	
	/**
	 * 将本地所有菜品列表A和服务器得到的售罄列表B（包含套餐和单品）进行对比，如果A中元素在B中，则将此元素插入到现有的售罄列表
	 */
	void updateTvEmptyMenuStatus()
	{
		//CommonUtils.LogWuwei(tag, "updateTvEmptyMenuStatus");

		int length = emptyReserveListFromServer.length;
		int nowNameIdFromDatabaseLength = nowNameIdFromDatabase.length;

		for(int i=0;i<length;i++)
		{
			if(emptyReserveListFromServer[i][0] == null)
			{
				break;
			}
			

			
			for(int k=0;k<nowNameIdFromDatabaseLength;k++)
			{
				if(nowNameIdFromDatabase[k][0] == null)
				{
					break;
				}
				if(emptyReserveListFromServer[i][0].equals(nowNameIdFromDatabase[k][0]))
				{
					if( !nowNameIdFromDatabase[k][1].equals("") &&nowNameIdFromDatabase[k][0].length() > 1)
					{
						insertToNowEmpty(nowNameIdFromDatabase[k][0],nowNameIdFromDatabase[k][1]);
						break;
					}
				}
			}
		}
	}
	
	
	/**
	 * 对比现有的售罄列表A和从服务器拿到的新的售罄列表B，如果A的元素不在B中，那么此元素对应的控件恢复正常供应状态
	 */
	void cancelFromNowEmpty()
	{
		int new_id_from_server_index = 0;
		int new_empty_list[] = new int[50];
		
		
		//从库存数据和现有菜单数据读到售罄的新的控件id数组
		int length = nowNameIdFromDatabase.length;
		int emptyReserveListFromServerLength = emptyReserveListFromServer.length;		
		
		
		for(int i=0;i<length;i++)
		{
			if(nowNameIdFromDatabase[i][0] == null)
			{
				break;
			}

			for(int k=0;k<emptyReserveListFromServerLength;k++)
			{
				if(emptyReserveListFromServer[k][0] == null || emptyReserveListFromServer[k][0].equals("") || nowNameIdFromDatabase[i][0].equals(""))
				{
					break;
				}
				
				if(emptyReserveListFromServer[k][0].equals(nowNameIdFromDatabase[i][0]))
				{
					new_empty_list[new_id_from_server_index] = Integer.parseInt(nowNameIdFromDatabase[i][1]);
					new_id_from_server_index++;
					//CommonUtils.LogWuwei(tag, "查询中。。。id is "+Integer.parseInt(nowNameIdFromDatabase[i][1])+"name is "+nowNameIdFromDatabase[i][0]);
				}
			}
		}
		
		
		
		//将已经标记为售罄的控件id与上一步获取到的最新的id数组每一个id进行对比，如果id不在数组中，则可以恢复正常供应
		
	//	CommonUtils.LogWuwei(tag, "cancelFromNowEmpty 准备将已经保存的售罄控件id-A和新的到控件id数组的每一个id-b进行对比，如果a不在b中，则恢复正常供应，否则结束");

		int NowEmptyListLength = NowEmptyList.length;
		int new_empty_list_length = new_empty_list.length;
		
		for(int i=0;i<NowEmptyListLength;i++)
		{
			boolean flag_cancel = true;
			
			for(int k=0;k<new_empty_list_length;k++)
			{
			//	CommonUtils.LogWuwei(tag, "对比中 。。。。。NowEmptyList["+i+"] is "+NowEmptyList[i]+" "+"new_empty_list["+k+"] is "+new_empty_list[k]);
				if(NowEmptyList[i] == new_empty_list[k])
				{
					flag_cancel = false;
					break;
				}
			}
			
			if(flag_cancel)
			{
				Button v= (Button)findViewById(NowEmptyList[i]);
				
				Message msg = new Message();
				msg.obj =  NowEmptyList[i];
				msg.what = CANCEL_TEXT_SOLD;
				handlerShowMenu.sendMessage(msg);
				NowEmptyList[i] = 0;
				msg = null;
			}
		}
		
		new_empty_list = null;
	}

	
	/**
	 * 将要插入的名字和控件id插入到现有的售罄列表
	 * @param name
	 * @param id
	 */
	void insertToNowEmpty(String name,String id)
	{
		boolean flag_new_sold_empty_item = true;//新的售罄菜品标志位
		
	//	CommonUtils.LogWuwei(tag, "insertToNowEmpty  准备查询"+id+"是否已经在本地的NowEmptyList");
		int NowEmptyListLength = NowEmptyList.length;
		
		for(int i=0;i<NowEmptyListLength;i++)//遍历现在的售罄菜品列表
		{
			if(NowEmptyList[i] == 0)
			{
				break;
			}
			
			if(NowEmptyList[i] == Integer.parseInt(id))
			{
				flag_new_sold_empty_item = false;//如果有一个相同，则不是新的售罄菜品
				return;
			}
		}

		
		
		if(flag_new_sold_empty_item)//新的售罄菜品
		{
			
			CommonUtils.LogWuwei("sold_empty", "insertToNowEmpty  查询结束，为新的售罄菜品 ready to insert info is name is "+name+" id is "+id);
			
			//排序
			NowEmptyListLength = NowEmptyList.length-1;
			
			for(int i=0;i<NowEmptyListLength;i++)
			{
				for(int k=0;k<NowEmptyListLength-i;k++)
				{
					if(NowEmptyList[k] < NowEmptyList[k+1])
					{
						int temp = NowEmptyList[k];
						NowEmptyList[k] = NowEmptyList[k+1];
						NowEmptyList[k+1] = temp;
					}
				}
			}
			
			NowEmptyListLength = NowEmptyList.length;
			
			/*for(int i=0;i<NowEmptyListLength;i++)
			{
				if(NowEmptyList[i] == 0)
				{
					NowEmptyList[i] = Integer.parseInt(id);
					CommonUtils.LogWuwei(tag, "button id is "+id);
					Button v = (Button)rl_server.findViewById(Integer.parseInt(id));
					if(v != null)
					{
						Message msg = new Message();
						msg.what = UPDATE_TEXT_SOLD_ALL;
						msg.obj = v;
						handlerShowMenu.sendMessage(msg);
						msg = null;
					}
					break;					
				}
			}
			*/
			
			//添加到
			int index_array = 0;
			for(int p=0;p<NowEmptyListLength;p++)
			{
				boolean flag = true;
				if(NowEmptyList[p] != 0)
				{
					index_array++;
					if(Integer.parseInt(id) == NowEmptyList[p])
					{
						flag = false;
						break;
					}
				}
				else if(NowEmptyList[p] == 0 && flag)
				{
					NowEmptyList[index_array] = Integer.parseInt(id);
					//CommonUtils.LogWuwei(tag, "button id is "+id);
					Button v = (Button)rl_server.findViewById(Integer.parseInt(id));
					if(v != null)
					{
						Message msg = new Message();
						msg.what = UPDATE_TEXT_SOLD_ALL;
						msg.obj = v;
						handlerShowMenu.sendMessage(msg);
						msg = null;
					}
					break;		
				}
				
			}
			
			
			NowEmptyListLength = NowEmptyList.length-1;
			//排序
			for(int i=0;i<NowEmptyListLength;i++)
			{
				for(int k=0;k<NowEmptyListLength-i;k++)
				{
					if(NowEmptyList[k] < NowEmptyList[k+1])
					{
						int temp = NowEmptyList[k];
						NowEmptyList[k] = NowEmptyList[k+1];
						NowEmptyList[k+1] = temp;
					}
				}
			}
			
			NowEmptyListLength = NowEmptyList.length;
			//输出验证
			for(int i=0;i<NowEmptyListLength;i++)
			{
				if(NowEmptyList[i] == 0)
				{
					break;
				}
				//CommonUtils.LogWuwei("NowEmptyList", "NowEmptyList["+i+"] is "+NowEmptyList[i] );
			}
			//CommonUtils.LogWuwei("NowEmptyList", "1\n\n__________\n\n1" );
			
		}
	}
	
	
	/**
	 * 从本地数据库读取到菜品名称和对应的控件id
	 * @return
	 */
	public String[][] getNameIdFromDataBase()
	{
		
		String nameId[][] = new String[100][2];
		long date_id = Long.parseLong(CommonUtils.getNowDateString());
		int menu_type = keyboard_menu_type_index;//getMenuType();
		QueryBuilder qb = menuDetailDao.queryBuilder();
		
		long date_tmp = date_id*10+menu_type;
		
		qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp)));
		List listResult = qb.list();
		
		QueryBuilder qbTmp = menuDetailDao.queryBuilder();
		qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp+20000)));
		//CommonUtils.LogWuwei(tag, "Long.toString(date_tmp+200000) is "+Long.toString(date_tmp+20000));
		List listResultTmp = qbTmp.list();
		if(listResultTmp.size() > 0)
		{
			listResult = listResultTmp;
		}
		
		for(int i=0;i<listResult.size();i++)
		{
			MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
			nameId[i][0] = menu_detail_entity.getName();
			nameId[i][1] = Integer.toString(menu_detail_entity.getWidgetId());
			//CommonUtils.LogWuwei(tag, "存到本地getNameIdFromDataBase:name is "+nameId[i][0]+" id is "+nameId[i][1]);
		}
		return nameId;
	}
	
	
	/**
	 * 获取绑定的单品id以及名字
	 * @return
	 */
	public int[][] getNowBindSfoodSingleId()
	{
		if(nowBindSfoodSingleId != null)
		{
			for(int i=0;i<nowBindSfoodSingleId.length;i++)
			{
				nowBindSfoodSingleId[i][0] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][1] = 0;
			}
		}
		
		
		int[][] singleServerId_WidgetId = new int[50][2];
		int index = -1;
		long date_id = Long.parseLong(CommonUtils.getNowDateString());
		int menu_type = keyboard_menu_type_index;
		long date_tmp = date_id*10+menu_type;
		/*String tag = "bindSingle";*/
		
		QueryBuilder qb = menuDetailDao.queryBuilder();
		qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp)));
		List listResult = qb.list();
		
		QueryBuilder qbTmp = menuDetailDao.queryBuilder();
		qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp+20000)));
		List listResultTmp = qbTmp.list();
		
		if(listResultTmp.size() > 0)
		{
			listResult = listResultTmp;
		}
		
		for(int i=0;i<listResult.size();i++)
		{
			MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
			String bindType = menu_detail_entity.getRedundance1();
			if(bindType != null && !bindType.equals(""))
			{
				if(bindType.equals("0"))
				{
					index++;
//					CommonUtils.LogWuwei(tag,""+menu_detail_entity.getBindToItemServerId());
					singleServerId_WidgetId[index][0] = menu_detail_entity.getBindToItemServerId();
					singleServerId_WidgetId[index][1] = menu_detail_entity.getWidgetId();
//					CommonUtils.LogWuwei(tag, "获取绑定的单品:"+ menu_detail_entity.getName());
				}
			}
			
		}
		if(index == -1)
		{
			//CommonUtils.LogWuwei(tag, "没有绑定单品");
			return null;
		}
		else
		{
			return singleServerId_WidgetId;	
		}
	}
	
	
	/**
	 * 获取绑定的套餐id以及对应的原材料sfood的id、amount，只需获取一次（除非切换菜单）
	 * @return
	 */
	public void getNowBindComboInfo(int flag)
	{
		if(nowBindComboId_SfoodId_Sfood_Amount_WidgetId != null && flag == 1)
		{
			for(int i=0;i<nowBindComboId_SfoodId_Sfood_Amount_WidgetId.length;i++)
			{
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][0] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][1] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][2] = 0;
				nowBindComboId_SfoodId_Sfood_Amount_WidgetId[i][3] = 0;
			}
		}
		
		int index = -1;
		long date_id = Long.parseLong(CommonUtils.getNowDateString());
		int menu_type = keyboard_menu_type_index;//getMenuType();
		QueryBuilder qb = menuDetailDao.queryBuilder();
		
		long date_tmp = date_id*10+menu_type;
		
		qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp)));
		List listResult = qb.list();
		
		QueryBuilder qbTmp = menuDetailDao.queryBuilder();
		qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(date_tmp+20000)));
		//CommonUtils.LogWuwei("jsonTest", "Long.toString(date_tmp+200000) is "+Long.toString(date_tmp+20000));
		List listResultTmp = qbTmp.list();
		if(listResultTmp.size() > 0)
		{
			CommonUtils.LogWuwei("jsonTest", "准备使用临时数据");
			listResult = listResultTmp;
		}
		else
		{
			CommonUtils.LogWuwei("jsonTest", "没有临时数据");
		}
		
		for(int i=0;i<listResult.size();i++)
		{
			MenuDetail menu_detail_entity = (MenuDetail) listResult.get(i);
			String bindType = menu_detail_entity.getRedundance1();
			if(bindType!= null && ! bindType.equals(""))
			{
				if(bindType.equals("1"))
				{
					index++;
					ComboId[index][0] = menu_detail_entity.getBindToItemServerId();
					ComboId[index][1] = menu_detail_entity.getWidgetId();
					CommonUtils.LogWuwei(tag, "发现一个绑定套餐菜品"+menu_detail_entity.getName());
					//CommonUtils.LogWuwei(tag, "获取绑定的套餐:"+menu_detail_entity.getName());
				}
			}
				
		}
		
			new Thread()
			{
				public void run()
				{
					//RequestServerData.SendPostRequest(-1,4,-1,null);;//获取菜单列表
					//RequestServerData.SendPostRequest(-1,5,-1,null);//获取原材料信息
					//RequestServerData.SendPostRequest(-1,3,-1,null);//获取是否有菜品售罄
					//CommonUtils.LogWuwei(tag, "准备处理数据。。。。emptyReserveListFromServer[0][0] is "+emptyReserveListFromServer[0][0]);
					
					if(emptyReserveListFromServer[0][0] != null)
					{
						/*updateTvEmptyMenuStatus();
						cancelFromNowEmpty();*/
					}
						
				}
			}.start();
		
	}
	
	
	/**
	 * 获取此时对应的菜单类型
	 * @return
	 */
	public static int getMenuType()
	{
		int menu_type = -1;
		Date date = new Date();
		int hour = date.getHours();
		if(hour >=0 && hour <=10)//显示早餐
		{
			menu_type = 0;
		}
		else if(hour >= 11 && hour<15)//显示午餐
		{
			menu_type = 1;
		}
		else if(hour >= 15 && hour<=23)//显示晚餐
		{
			menu_type = 2;
		}
		return menu_type;

	}
	
	
	/**
	 * 如果没有菜单，则清空屏幕原有菜单并显示“*年*月*日无*餐菜单”
	 */
	public void cleanScreenWhenNoMenu()
	{
		if(rl_server != null)
		{
			rl_server.removeAllViews();
			//MsgUtils.SendSingleMsg(splash.handlerTools, "无菜单", HandlerUtils.SHOW_NORMAL_TOAST);
			Button button = new Button(this);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  
		    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		    String errorMsg = CommonUtils.parseDate(keyboard_date_menu_index)+"("+CommonUtils.getWeekDay(dayFromToday)+")"+"无";
		    switch (keyboard_menu_type_index)
		    {
		    case 0:
		    		errorMsg = errorMsg+"早餐菜单";
		    		break;
		    case 1:
		    		errorMsg = errorMsg+"午餐菜单";
		    		break;
		    case 2:
		    		errorMsg = errorMsg+"晚餐菜单";
		    		break;
		    }
		    button.setText(errorMsg);
		    button.setTextSize(50f);
		    button.setTextColor(Color.WHITE);
		    button.setBackgroundColor(Color.TRANSPARENT);
			rl_server.addView(button,lp);
			button = null;
		}
	}
	
	
	/**
	 * 接受按键操作并做出响应
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
     	if (keyCode == KeyEvent.KEYCODE_BACK)
	  	{
     		  if ((System.currentTimeMillis() - mExitTime) > 2000) 
     		   {
     	           Object mHelperUtils;
     	           //Toast.makeText(this, "再按一次退出app", Toast.LENGTH_SHORT).show();
     	           mExitTime = System.currentTimeMillis();

     		   }
     		   else 
     		   {
     	  	 	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
     	  	 	 builder.setPositiveButton("确定离开？", new DialogInterface.OnClickListener() {
     					
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						// TODO Auto-generated method stub
     						nanoHttpd.stop();
     						cancelTimerScanReserves();
     						
     						timerModify = null;
     						timerScanReserves = null;
     						timerTaskModify = null;
     						timerTaskScanReserves = null;
     						
     						finish();
     					}
     	  	 	 });
     	  	 	 
     	  	 	 builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {
     					
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						// TODO Auto-generated method stub
     					}
     	   	 	 });
     	  	 	 builder.setTitle("提示窗口");
     	  	 	 builder.setIcon(android.R.drawable.ic_dialog_info);
     	  	 	 builder.show();
     	   }
     		 return true;
        }
        else 
        {
	        	QueryBuilder qb = ShowMenuActivity.menuDetailDao.queryBuilder();
		    	qb.orderAsc(MenuDetailDao.Properties.MenuDateId);
		    	List<MenuDetail> listResult = qb.list();
		    	if(listResult.size() != 0)
		    	{
		    		smallest_margin= Long.parseLong(listResult.get(0).getMenuDateId());
		        	biggest_margin=Long.parseLong(listResult.get(listResult.size()-1).getMenuDateId());
		        	 for(int i=0;i<NowEmptyList.length;i++)
		      	   {
		      			NowEmptyList[i] = 0;
		      	   }
		      	   
		      	   for(int i =0;i<emptyReserveListFromServer.length;i++)
		      	   {
		      			 emptyReserveListFromServer[i][0] = null;
		      			 emptyReserveListFromServer[i][1] = null;
		      	   }
		      	 
		        	switch (keyCode)
	        		{
	        		case KeyEvent.KEYCODE_DPAD_LEFT:
	        		case KeyEvent.KEYCODE_VOLUME_UP:
	        			left();
	        			if(dayFromToday == 0)
	       			 {
	        				nowNameIdFromDatabase = getNameIdFromDataBase();
		        			getNowBindComboInfo(1);
		        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
	       			 }
	        			break;
	        		case KeyEvent.KEYCODE_DPAD_RIGHT:
	        		case KeyEvent.KEYCODE_VOLUME_DOWN:
	        			if(dayFromToday == 0)
		       			 {
		        				nowNameIdFromDatabase = getNameIdFromDataBase();
			        			getNowBindComboInfo(1);
			        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
		       			 }
	        			right();
	        			break;
	        		case KeyEvent.KEYCODE_DPAD_UP:
	        			up();
	        			if(dayFromToday == 0)
	        			{
	        				nowNameIdFromDatabase = getNameIdFromDataBase();
		        			getNowBindComboInfo(1);
		        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
	        			}
	        			break;
	        		case KeyEvent.KEYCODE_DPAD_DOWN:
	        			down();
	        			if(dayFromToday == 0)
	        			{
	        				nowNameIdFromDatabase = getNameIdFromDataBase();
		        			getNowBindComboInfo(1);
		        			nowBindSfoodSingleId = getNowBindSfoodSingleId();
	        			}
	        			break;
	        		}
		        	
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplicationContext(), "无数据", Toast.LENGTH_SHORT).show();
		    		return super.onKeyDown(keyCode, event);
		    	}
        }
     	CommonUtils.LogWuwei(tag, "dayFromToday is "+dayFromToday);
        return super.onKeyDown(keyCode, event);
	 }

	
	/**
	 * 对向上的按键或者手势做出响应：早餐-》早餐 午餐-》早餐 完成-》早餐
	 */
	private void up()
	   {
			keyboard_menu_type_index--;
			if(keyboard_menu_type_index < 0)
			{
				keyboard_menu_type_index = 0;
				return;
			}
			
			keyboard_date_menu_index = Long.parseLong(CommonUtils.getDate(dayFromToday));
			
			CommonUtils.LogWuwei(tag, "up---- keyboard_date_menu_index is "+keyboard_date_menu_index);
			
			QueryBuilder qbUp = ShowMenuActivity.menuDetailDao.queryBuilder();
			qbUp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index)+keyboard_menu_type_index));
		    List<MenuDetail> listResultUp = qbUp.list();
		     
		      
		      
		      QueryBuilder qbUpTmp = ShowMenuActivity.menuDetailDao.queryBuilder();
		      qbUpTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index+2000)+keyboard_menu_type_index));
		      List<MenuDetail> listResultUpTmp = qbUpTmp.list();
		     
		      if(listResultUpTmp.size() > 0)
		      {
		    	  	listResultUp = listResultUpTmp;
		      }
		      
		      listResultRequestServerData = listResultUp;
		      if(listResultUp.size() != 0)
		      {
//		    	  	CommonUtils.LogWuwei(tag, "接收到键盘上的请求，刷新屏幕中。。。。");
		    	  	ShowMenuActivity.rl_server.removeAllViews();
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
		    			paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
		    			msg = null;
		    			bundle = null;
		    	  	}
		      }
		      else
		      {
		    	  		cleanScreenWhenNoMenu();
		      }
	   }
	   
	   
	/**
	 * 对向下的按键或者手势做出响应：早餐-》早餐 午餐-》早餐 完成-》早餐
	 */
	private void down()
	   {
		 	keyboard_menu_type_index++;
			if(keyboard_menu_type_index > 2)
			{
				keyboard_menu_type_index = 2;
				return;
			}
			
			keyboard_date_menu_index = Long.parseLong(CommonUtils.getDate(dayFromToday));
			
			CommonUtils.LogWuwei(tag, "keyboard_date_menu_index is "+keyboard_date_menu_index);
			
			 QueryBuilder qbDown = ShowMenuActivity.menuDetailDao.queryBuilder();
			 qbDown.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index)+keyboard_menu_type_index));
		      List<MenuDetail> listResultDown = qbDown.list();
		     
		      
		      QueryBuilder qbDownTmp = ShowMenuActivity.menuDetailDao.queryBuilder();
		      qbDownTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index+2000)+keyboard_menu_type_index));
		      List<MenuDetail> listResultDownTmp = qbDownTmp.list();
		      if(listResultDownTmp.size() > 0)
		      {
		    	  	listResultDown = listResultDownTmp;
		      }
		      
		      listResultRequestServerData = listResultDown;
		      if(listResultDown.size() != 0)
		      {
		    	  	ShowMenuActivity.rl_server.removeAllViews();
		    	  	//CommonUtils.LogWuwei(tag, "接收到键盘下的请求，刷新屏幕中。。。。");
		    	  	for(int i=0;i<listResultDown.size();i++)
		    	  	{
		    	  		MenuDetail menu_detail_entity = (MenuDetail) listResultDown.get(i);
	   				
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
		    			paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
		    			msg = null;
		    			bundle = null;
		    	  	}
		      }
		      else
		      {
		    	  	cleanScreenWhenNoMenu();
		      }
	   }
	  
	
	/**
	 * 对向左的按键或者手势做出响应：当日早餐-》昨日早餐  当日午餐-》昨日午餐  当日晚餐-》昨日晚餐
	 */
	private void left()
	   {
		    	dayFromToday--;
		    	keyboard_date_menu_index = Long.parseLong(CommonUtils.getDate(dayFromToday));
		    	
			CommonUtils.LogWuwei(tag, "left  ----keyboard_date_menu_index is "+keyboard_date_menu_index);
			
			if((keyboard_date_menu_index*10+keyboard_menu_type_index) < smallest_margin)
			{
				//keyboard_date_menu_index = smallest_margin/10;
				//MsgUtils.SendSingleMsg(splash.handlerTools, "没有菜单", HandlerUtils.SHOW_NORMAL_TOAST);
				cleanScreenWhenNoMenu();
			}
			else
			{
				 
				  QueryBuilder qbLeft = ShowMenuActivity.menuDetailDao.queryBuilder();
				  qbLeft.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index)+keyboard_menu_type_index));
			      List<MenuDetail> listResultLeft = qbLeft.list();
			      
			      QueryBuilder qbLeftTmp = ShowMenuActivity.menuDetailDao.queryBuilder();
				  qbLeftTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index+2000)+keyboard_menu_type_index));
			      List<MenuDetail> listResultLeftTmp = qbLeftTmp.list();
			      if(listResultLeftTmp.size() > 0)
			      {
			    	  	listResultLeft = listResultLeftTmp;
			      }
			      
			      listResultRequestServerData = listResultLeft;
			      
			      if(listResultLeft.size() != 0)
			      {
			    	  	ShowMenuActivity.rl_server.removeAllViews();
			    	  	for(int i=0;i<listResultLeft.size();i++)
			    	  	{
			    	  		MenuDetail menu_detail_entity = (MenuDetail) listResultLeft.get(i);
		    				
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
			    			
			    			paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
			    			msg = null;
			    			bundle = null;
			    	  	}
			      }
			      else
			      {
			    	  	cleanScreenWhenNoMenu();
			      }
			}
	   }
	   

	/**
	 *对向右的按键或者手势做出响应：当日早餐-》明日早餐  当日午餐-》明日午餐  当日晚餐-》明日晚餐
	 */
	private void right()
	   {
		
		    	dayFromToday++;
		    	keyboard_date_menu_index = Long.parseLong(CommonUtils.getDate(dayFromToday));
		    	
		    	CommonUtils.LogWuwei(tag, "right  ----keyboard_date_menu_index is "+keyboard_date_menu_index+
		    			"biggest_margin is "+biggest_margin);
			
		    	if((keyboard_date_menu_index*10+keyboard_menu_type_index) > biggest_margin)
			{
				//keyboard_date_menu_index = biggest_margin/10;
				cleanScreenWhenNoMenu();
			}
			else
			{
				QueryBuilder qbRight = ShowMenuActivity.menuDetailDao.queryBuilder();
				qbRight.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index)+keyboard_menu_type_index));
			    List<MenuDetail> listResultRight = qbRight.list();
			      
			    
			      QueryBuilder qbRightTmp = ShowMenuActivity.menuDetailDao.queryBuilder();
			      qbRightTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(keyboard_date_menu_index+2000)+keyboard_menu_type_index));
			      List<MenuDetail> listResultRightTmp = qbRightTmp.list();
			      if(listResultRightTmp.size() > 0)
			      {
			    	  	listResultRight = listResultRightTmp;
			      }
			      
			      listResultRequestServerData = listResultRight;
			      
			      if(listResultRight.size() != 0)
			      {
			    	  	ShowMenuActivity.rl_server.removeAllViews();
			    	  	for(int i=0;i<listResultRight.size();i++)
			    	  	{
			    	  		MenuDetail menu_detail_entity = (MenuDetail) listResultRight.get(i);
		    				
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
		        			
		   //     			CommonUtils.LogWuwei(tag, "name is "+name+" type is "+type);
		        			
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
			    			paintTv(msg, ShowMenuActivity.rl_server,true,ctxt);
			    			
			    			msg = null;
			    			bundle = null;
			    	  	}
			      }
			      else
			      {
			    	  	cleanScreenWhenNoMenu();
			      }
			}
	   }

	
}