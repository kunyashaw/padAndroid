package com.huofu.RestaurantOS.utils.mail;

import android.content.Context;

import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.ui.login.LoginActivity;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.ui.pannel.stockPlan.StockPlanActivity;
import com.huofu.RestaurantOS.ui.pannel.stockSupply.StockSupplyActivity;
import com.huofu.RestaurantOS.ui.pannel.takeUp.TakeUpActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.FIleUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.LogUtil;
import com.huofu.RestaurantOS.utils.NetWorkUtils;
import com.huofu.RestaurantOS.utils.mail.MultiMailsender.MultiMailSenderInfo;


public class sendMail {


    public static void sendEmail(final Context ctxt, final int k) {
        new Thread() {
            public void run() {

                MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
                mailInfo.setMailServerHost("smtp.126.com");
                mailInfo.setMailServerPort("25");
                mailInfo.setValidate(true);
                mailInfo.setUserName("zhang_zhong_lei@126.com");
                mailInfo.setPassword("wode521521");//您的邮箱密码

                mailInfo.setFromAddress("zhang_zhong_lei@126.com");
                mailInfo.setToAddress("zhang_zhong_lei@126.com");

                //LoginActivity.pathNetWork+"huoFuNetWork-"+CommonUtils.getDate(0)+"("+CommonUtils.getWeekDay(0)+").log";
                String fileInfo = "";
                String fileInfo1 = LogUtil.getInstance().getLogFileName(PushMealActivity.tag);//MealDoneActivity.tag);
                String fileInfo2 = LogUtil.getInstance().getLogFileName(TakeUpActivity.tag);
                String fileInfo3 = LogUtil.getInstance().getLogFileName(SettingsActivity.tag);
                String fileInfo4 = LogUtil.getInstance().getLogFileName(LoginActivity.tag);
                String fileInfo5 = LogUtil.getInstance().getLogFileName("");//(RequestServerData.tag);
                String fileInfo6 = LogUtil.getInstance().getLogFileName(LogUtil.LOG_ALL);
                String fileInfo7 = LogUtil.getInstance().getLogFileName(StockSupplyActivity.tag);
                String fileInfo8 = LogUtil.getInstance().getLogFileName(StockPlanActivity.tag);
                String fileInfo9 = LogUtil.getInstance().getLogFileName(LoginActivity.tag);

                String fileInfoList[] = {fileInfo1, fileInfo2, fileInfo3, fileInfo4, fileInfo5, fileInfo6,fileInfo7,fileInfo8,fileInfo9};


                String merchant_name = LocalDataDeal.readFromLocalMerchantName(ctxt);
                String store_name = LocalDataDeal.readFromLocalStoreName(ctxt);

                //  for(int k=0;k<fileInfoList.length;k++)
                String fileDetailTime = "发送日期:" + CommonUtils.getStrTime(Long.toString(System.currentTimeMillis()));
                String fileModle = "发送设备:" + CommonUtils.getModelInfo();
                String AppVersion = "V"+LocalDataDeal.readFromLocalVersion(ctxt);
                {

                    switch (k) {
                        case 0:
                            CommonUtils.LogWuwei("sendMail", "发送出餐日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")出餐(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 1:
                            CommonUtils.LogWuwei("sendMail", "发送取号日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")取号(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 2:
                            CommonUtils.LogWuwei("sendMail", "发送设置日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")设置(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 3:
                            CommonUtils.LogWuwei("sendMail", "发送登录日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")登录(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 4:
                            CommonUtils.LogWuwei("sendMail", "发送网络日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")网路(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 5:
                            CommonUtils.LogWuwei("sendMail", "发送所有日志");
                            mailInfo.setSubject("pad-" + AppVersion + "(" + store_name + ")(" + merchant_name + ")all(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                        case 6:
                            CommonUtils.LogWuwei("sendMail", "发送库存盘点日志");
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")库存盘点(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            break;
                        case 7:
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")库存计划(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            CommonUtils.LogWuwei("sendMail", "发送库存计划日志");
                            break;
                        case 8:
                            mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")登录(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));
                            CommonUtils.LogWuwei("sendMail", "发送登录日志");
                            break;
                    }

                    try {
                        fileInfo = fileInfoList[k];
                        FIleUtils fu = new FIleUtils(ctxt);
                        String fileContent = fu.readFile(fileInfo, "utf-8").toString();// fu.readSDFile(fileInfo);
                        if (fileContent == null || fileContent.equals("")) {
                            return;
                        }

                        mailInfo.setContent(
                                        AppVersion+"\n"+
                                        fileDetailTime + "\n" +
                                        fileModle + "\n路径:" + fileInfo + "\n" + "正文:" + fileContent);


                        String[] receivers = new String[]{"zhang_zhong_lei@126.com"};
                        String[] ccs = receivers;
                        mailInfo.setReceivers(receivers);
                        mailInfo.setCcs(ccs);

                        //这个类主要来发送邮件
                        MultiMailsender sms = new MultiMailsender();
                        sms.sendTextMail(mailInfo);//发送文体格式
                    } catch (Exception e) {
                        CommonUtils.LogWuwei("sendMail", "send mail failed:" + e.getMessage());
                    }
                }
            }

        }.start();
    }


    public static void sendEmail(final Context ctxt, final String msg) {
        new Thread() {
            public void run() {

                MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
                mailInfo.setMailServerHost("smtp.126.com");
                mailInfo.setMailServerPort("25");
                mailInfo.setValidate(true);
                mailInfo.setUserName("zhang_zhong_lei@126.com");
                mailInfo.setPassword("wode521521");//您的邮箱密码

                mailInfo.setFromAddress("zhang_zhong_lei@126.com");
                mailInfo.setToAddress("zhang_zhong_lei@126.com");

                String merchant_name = LocalDataDeal.readFromLocalMerchantName(ctxt);
                String store_name = LocalDataDeal.readFromLocalStoreName(ctxt);
                String fileDetailTime = "发送日期:" + CommonUtils.getStrTime(Long.toString(System.currentTimeMillis()));
                String fileModle = "发送设备:" + CommonUtils.getModelInfo();
                String AppVersion = "V"+LocalDataDeal.readFromLocalVersion(ctxt);
                mailInfo.setSubject("pad-"+AppVersion+"(" + store_name + ")(" + merchant_name + ")出餐(" + CommonUtils.getDate(0) + ") (" + NetWorkUtils.GetWifiIp(ctxt) + ")" + NetWorkUtils.get_conntected_wifi_name(ctxt));

                    try {
                        String fileContent = msg;
                        if (fileContent == null || fileContent.equals("")) {
                            return;
                        }
                        mailInfo.setContent(
                                AppVersion+"\n"+
                                        fileDetailTime + "\n" +
                                        fileModle  + "\n" + "正文:" + fileContent);

                        String[] receivers = new String[]{"zhang_zhong_lei@126.com"};
                        String[] ccs = receivers;
                        mailInfo.setReceivers(receivers);
                        mailInfo.setCcs(ccs);

                        //这个类主要来发送邮件
                        MultiMailsender sms = new MultiMailsender();
                        sms.sendTextMail(mailInfo);//发送文体格式
                    } catch (Exception e) {
                        CommonUtils.LogWuwei("sendMail", "send mail failed:" + e.getMessage());
                    }
                }
        }.start();
    }

}
