package com.huofu.RestaurantOS.utils.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;

import com.huofu.RestaurantOS.utils.CommonUtils;

import android.content.Context;


public class ftpConfig {

	public static String tag = "ftpConfig";
	public static String pathFtp = null;
	public static Context ctxtFtp = null;
	
	
	/**
	 * ftp配置文件的初始化
	 * @param path:ftp配置文件所存放的地方
	 * @param ctxt：上下文
	 */
	private  ftpConfig(String path,Context ctxt) {
		// TODO Auto-generated method stub
		pathFtp = path;
		ctxtFtp = ctxt;
	}
	
	
	/**
	 * 开启ftp服务
	 * @return：服务开启成功返回server对象，否则返回null
	 */
	public static FtpServer ftpServiceStart()
	     {
	         FtpServerFactory serverFactory = new FtpServerFactory();
	         
	         ListenerFactory factory = new ListenerFactory();
	         
	         factory.setPort(12345);        // set the port of the listener
	        
	      /*  FIleUtils fu = new FIleUtils(ctxtFtp);
	        try {
	                File file = new File(pathFtp);
	                if(!file.isDirectory())
	                {
	                    file.mkdir();
	                }
	            fu.createFile(pathFtp+"ftpserver.properties");
	            String str = "" +
	                    "ftpserver.user.admin.username=admin\n"+
	                    "ftpserver.user.admin.userpassword=bff4d7685c1475b68c016c478a728b6e\n"+
	                    "ftpserver.user.admin.homedirectory=/mnt/sdcard\n"+
	                    "ftpserver.user.admin.enableflag=true\n"+  
	                    "ftpserver.user.admin.writepermission=true\n"+
	                    "ftpserver.user.admin.maxloginnumber=250\n"+
	                    "ftpserver.user.admin.maxloginperip=250\n"+
	                    "ftpserver.user.admin.idletime=300\n"+
	                    "ftpserver.user.admin.uploadrate=10000\n"+  
	                    "ftpserver.user.admin.downloadrate=10000\n";
	                                
	            fu.writeFile(str, pathFtp+"ftpserver.properties");
	            
	            File files=new File(pathFtp+"ftpserver.properties");
	            
	            PropertiesUserManagerFactory usermanagerfactory = new PropertiesUserManagerFactory();
	            usermanagerfactory.setFile(files);
	            serverFactory.setUserManager(usermanagerfactory.createUserManager());
	            
	        } catch (IOException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        }*/
	        
	         
	         serverFactory.addListener("default", factory.createListener());        // replace the default listener
	         
	         FtpServer server = serverFactory.createServer();
	         try 
	         {
	            server.start();
	         }
	         catch (FtpException e) 
	         {
	            // TODO Auto-generated catch block
	        	 	e.printStackTrace();
	        		CommonUtils.LogWuwei(tag, "ftpStart failed:"+e.getMessage());
	            server = null;
	         } 
	         return server;
	     }
	
}
