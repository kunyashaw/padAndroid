package com.huofu.RestaurantOS.bean.storeOrder;

import android.content.Context;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.OrderValue;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.printer.printerCmdUtils;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * 一个订单的所有信息（收费项目、订单信息）
 *
 * @author kunyashaw
 */


public class OrderDetailInfo  {

    public static String tag = "OrderDetailInfo";
    public static int PORT = 9100;
    public static int time_printer_test_connect_time = 5000;
    public long portCount = -1;//出餐口数量
    public int take_mode = 1;//1堂食 2外带 3堂食+外带 4外送
    public String order_id = ""; //订单id
    public String orderContent = "";
    public String orderContentList = "";//打包清单或者外送清单内容

    public Integer packaged ;//在获取出餐详情时用
    public StoreOrder store_order = null;//订单的具体信息

    public StoreOrder getStore_order() {
        return store_order;
    }

    public void setStore_order(StoreOrder store_order) {
        this.store_order = store_order;
    }

    public List<ChargItem> list_charge_items_all = null;//一个订单包含的收费项目具体信息
    public boolean flagPrintList = false;//是否打印清单
    public String timeBucketName = "";
    public String port_letter = "";
    public boolean flagPrintHistory = false;
    public int count;
    public int take_serial_number;
    public int take_serial_seq;
    public boolean flagLastItemInOrder=false;
    public long port_id;
    public String repast_date;


    /**
     * 获取应收价格
     * @return
     */
    public double getOrderPrice()
    {
        return  store_order.order_price/100.0;
    }

    /**
     * 获得实收价格
     * @return
     */
    public double getShouldPrice()
    {
        if(store_order.order_pay_info == null)
        {
            store_order.order_pay_info = new OrderPayInfo();
        }

        if(store_order.order_pay_info.cash_received_amount > 0)
        {
            return store_order.order_pay_info.cash_received_amount/100.0;
        }
        else
        {
            return store_order.payable_price/100.0;
        }
    }


    /**
     * 获得优惠或者找零
     */
    public double getGuestBackPrice()
    {

        if(store_order.order_pay_info == null)
        {
            store_order.order_pay_info = new OrderPayInfo();
        }

        double priceDiscountDouble = 0;
        if(store_order.client_type == 1)//现金
        {
            if((store_order.order_pay_info.cash_received_amount-store_order.payable_price>0) && (store_order.order_pay_info.cash_received_amount > 0))
            {
                priceDiscountDouble = (store_order.order_pay_info.cash_received_amount-store_order.payable_price)/100.0;
            }
        }
        else //网单
        {
            if((store_order.order_price-store_order.payable_price)>0)
            {
                priceDiscountDouble = (store_order.order_price-store_order.payable_price)/100.0;
            }
        }

        return  priceDiscountDouble;
    }


    public String getSerialNumber(int purpose)
    {
        String serianlNum = "";
        if(flagPrintList && (purpose ==3|| purpose==4))//如果是打印历史出餐清单或者外送清单、打包清单，如果有多个出餐口，则显示出餐口标识，否则不显示
        {
            if(!StringUtils.isEmpty(port_letter) && portCount > 1)
            {
                serianlNum = port_letter+""+store_order.take_serial_number;
            }
            else
            {
                serianlNum = store_order.take_serial_number+"";
            }
            return serianlNum;
        }

        if(flagPrintHistory)//在出餐历史中重新打印某个分单，这时任意一个都不显示尾
        {
            if(!StringUtils.isEmpty(port_letter) && portCount > 1)//如果出餐口标识不为空且出餐口数量大于1
            {
                if(store_order.take_serial_seq >= 1)//如果流水号序号大于0
                {
                    if(purpose == 3 || purpose == 4)//如果是清单，则不显示序号
                    {
                        serianlNum = port_letter+""+store_order.take_serial_number;
                    }
                    else
                    {
                        serianlNum = port_letter+""+store_order.take_serial_number+"-"+store_order.take_serial_seq;
                    }

                }
                else
                {
                    serianlNum = port_letter+""+store_order.take_serial_number;
                }
            }
            else//如果出餐口标识为空，或者出餐口数量小于1，则不显示出餐口id
            {
                if(store_order.take_serial_seq >= 1)
                {
                    if(purpose == 3 || purpose == 4)
                    {
                        serianlNum = store_order.take_serial_number+"";
                    }
                    else
                    {
                        serianlNum = store_order.take_serial_number+"-"+store_order.take_serial_seq;
                    }

                }
                else
                {
                    serianlNum = store_order.take_serial_number+"";
                }
            }
        }
        else
        {
            if(!StringUtils.isEmpty(port_letter)&& portCount > 1)
            {
                    if(count == 0 )
                    {
                        if(store_order.take_serial_seq > 1)
                        {
                            serianlNum = port_letter+store_order.take_serial_number+"-"+store_order.take_serial_seq+"尾";
                        }
                        else
                        {
                            serianlNum = port_letter+store_order.take_serial_number+"";
                        }

                    }
                    else
                    {
                        if(store_order.take_serial_seq >= 1)
                        {
                            if(purpose == 4 || purpose == 3)
                            {
                                serianlNum = port_letter+store_order.take_serial_number;
                            }
                            else
                            {
                                serianlNum = port_letter+store_order.take_serial_number+"-"+store_order.take_serial_seq;
                            }

                        }
                        else
                        {
                            serianlNum = port_letter+store_order.take_serial_number;
                        }
                    }
            }
            else
            {
                if(count == 0)
                {
                    if(store_order.take_serial_seq > 1)
                    {
                        serianlNum = store_order.take_serial_number+"-"+store_order.take_serial_seq+"尾";
                    }
                    else
                    {
                        serianlNum = store_order.take_serial_number+"";
                    }

                }
                else
                {
                    if(store_order.take_serial_seq >= 1)
                    {
                        if(purpose == 3 || purpose == 4)
                        {
                            serianlNum = store_order.take_serial_number+"";
                        }
                        else
                        {
                            serianlNum = store_order.take_serial_number+"-"+store_order.take_serial_seq;
                        }

                    }
                    else
                    {
                        serianlNum = store_order.take_serial_number+"";
                    }

                }
            }
        }



        boolean netOrderFloag = true;
        if (store_order.client_type == 1) {
            netOrderFloag = false;
        }
        String FocusStr = "";
        if (netOrderFloag) {
            if (store_order.take_client_type != 1 && store_order.take_client_type != 0) {
                FocusStr = "远网"+serianlNum ;
            } else {
                FocusStr = "网"+serianlNum;
            }

        } else {
            FocusStr = serianlNum;
        }
        return  FocusStr;
    }


    /**
     * 根据收费项目，获取点餐备注信息
     * @param chargeItemObject
     * @return
     */
    public String getRemarkInfo(ChargItem chargeItemObject)
    {

        String remarkDetail = "";
        try
        {
            if(chargeItemObject.listSubChargeItem != null)
            {
                for (int x = 0; x < chargeItemObject.listSubChargeItem.size(); x++) {
                    ChargeItemSub ciSub = chargeItemObject.listSubChargeItem.get(x);
                    if (!ciSub.remark.equals("") && ciSub.remark != null) {
                        if (chargeItemObject.listSubChargeItem.size() > 1) {
                            remarkDetail += (ciSub.mp.product_name + ":" + ciSub.remark+" ");
                        } else {
                            remarkDetail += (ciSub.remark);
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {

        }

        if (!remarkDetail.equals("") && !remarkDetail.equals(" ") && remarkDetail != null) {
            if (remarkDetail.length() >= 1) {
                remarkDetail = "(" + remarkDetail + ")";
            }
        }
        return  remarkDetail;
    }


    public static byte[] test(int num) {
        byte[] result = null;


        boolean netOrderFloag = true;
        int take_serial_number = num;
        String menuId = "1";
        String content = "这是一个测试单子\b";

        try {

            byte[] fontSize1Small = printerCmdUtils.fontSizeSetSmall(1);

            // ---------------------title----------------
            byte[] next2Line = printerCmdUtils.nextLine(2);
            byte[] title = null;
            if (menuId.equals("1")) {
                title = "打包单（午餐）五味万通中心店".getBytes("gb2312");
            } else if (menuId.equals("2")) {
                title = "打包单（晚餐）五味万通中心店".getBytes("gb2312");
            }

            byte[] nextLine = printerCmdUtils.nextLine(1);

            // ---------------------Terminal SerinalNo.----------------
            byte[] boldOn = printerCmdUtils.boldOn();
            byte[] center = printerCmdUtils.alignCenter();
            byte[] Focus = null;
            if (netOrderFloag) {
                Focus = ("网 " + take_serial_number).getBytes("gb2312");
            } else {
                Focus = (Integer.toString(take_serial_number)).getBytes("gb2312");
            }
            byte[] boldOff = printerCmdUtils.boldOff();
            byte[] fontSize2Small = printerCmdUtils.fontSizeSetSmall(2);

            // ---------------------Kitchen Flag----------------
            byte[] fontSize3Big = printerCmdUtils.fontSizeSetBig(3);
            byte[] fontSize2Big = printerCmdUtils.fontSizeSetBig(2);
            byte[] fontSize1Big = printerCmdUtils.fontSizeSetBig(1);
            String orderId = "987654321";
            byte[] size3text = ("字号3 " + orderId).getBytes("gb2312");
            byte[] size2text = ("字号2 " + orderId).getBytes("gb2312");
            byte[] size1text = ("字号1 " + orderId).getBytes("gb2312");
            byte[] order_id = ("订单编号:" + orderId).getBytes("gb2312");
            byte[] ketchen = "** 后厨 **".getBytes("gb2312");
            byte[] fontSize3Small = printerCmdUtils.fontSizeSetSmall(3);


            // ----------------------Package Tips------------------
            byte[] packageTips = "打包出餐单".getBytes("gb2312");

            // ----------------------UnderLine-----------------------
            byte[] underLineHand = ("-----------------------------")
                    .getBytes("gb2312");

            // -----------------------Content-------------------------
            byte[] FocusOrderContent = content.getBytes("gb2312");

            byte[] breakPartial = printerCmdUtils.feedPaperCutPartial();
            byte[] left = printerCmdUtils.alignLeft();
            byte[] orderSerinum = order_id;
            byte[] priceInfoTips = ("应收: ").getBytes("gb2312");
            byte[] priceInfo = ("300元 ").getBytes("gb2312");
            byte[] priceShouldPay = ("实收:300元 ").getBytes("gb2312");
            byte[] waitingTimeByte = ("等待时间：10s").getBytes("gb2312");
            byte[] takeRealTime = "取号时间:2013-3-3 10:10:10".getBytes("gb2312");
            byte[] setOrderTime = "下单时间:2013-3-3 10:9:10".getBytes("gb2312");
            byte[] tips_1 = "去你妈的3000".getBytes("gb2312");


            byte[][] cmdBytes1 = {
                    tips_1, nextLine,
                    fontSize1Big, title, nextLine, center, boldOn, fontSize1Small,
                    fontSize3Big, Focus, boldOff, fontSize2Small, next2Line,
                    left, orderSerinum, nextLine,
                    left, boldOn, fontSize2Big, FocusOrderContent, boldOff, fontSize2Small,
                    left, boldOn, boldOff, priceInfoTips, priceInfo, nextLine, priceShouldPay,
                    next2Line, waitingTimeByte, nextLine, takeRealTime, nextLine, setOrderTime, next2Line,
                    center, tips_1, nextLine, center, next2Line, fontSize1Small};

            byte[][] cmdBytes2 = {
                    fontSize1Big, title, nextLine,
                    center, boldOn, fontSize3Big, Focus, fontSize3Small, nextLine,
                    boldOn, FocusOrderContent, boldOff, fontSize2Small, next2Line,
                    center, nextLine, fontSize1Small, nextLine,
                    boldOn, fontSize1Big, size1text, fontSize1Small, nextLine,
                    fontSize2Big, size2text, fontSize1Small, nextLine,
                    fontSize3Big, size3text, fontSize1Small, nextLine,
                    fontSize3Big, tips_1, next2Line};
           /* if (num == 1) {
                return printerCmdUtils.byteMerger(cmdBytes1);
            } else {
                return printerCmdUtils.byteMerger(cmdBytes2);
            }*/
            return printerCmdUtils.byteMerger(cmdBytes1);


        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 向打印机写入控制命令的字节流
     *
     * @param printInfoTmp
     */
    public static synchronized int writeToPrinterInfo(Context ctxt, byte[] printInfoTmp, String ip) {
        if(StringUtils.isEmpty(ip))
        {
            CommonUtils.LogWuwei(PushMealActivity.tag,"writeToPrinterInfo中ip为空");
            return 0;
        }
        final byte[] printInfo = printInfoTmp;
        String printerIp = ip;
        InetSocketAddress remoteAddr = new InetSocketAddress(printerIp, PORT); //获取sockaddress对象
        Socket socket = null;
        OutputStream out = null;

        try {

            Thread.sleep(1000);

            //第二步：创建套接字
            CommonUtils.LogWuwei(tag, "1、new socket");
            socket = new Socket();

            //第三步：连接打印机，同时设置连接超时时间
            CommonUtils.LogWuwei(tag, "2、connect");
            socket.connect(remoteAddr, 5000);

            //第三步：设置打印机的读写超时时间
            CommonUtils.LogWuwei(tag, "3、setSoTimeout");
            socket.setSoTimeout(5000);

            //第四步：获取可写流
            CommonUtils.LogWuwei(tag, "4、is null over");
            out = socket.getOutputStream();

            if (out != null) {
                //第五步：通过可写流，向打印机写控制命令
                CommonUtils.LogWuwei(tag, "3、out is not null,ready to write");
                out.write(printerCmdUtils.byteMerger(printInfo, "\n".getBytes("gb2312")));

                //第六步：刷新缓冲区
                CommonUtils.LogWuwei(tag, "4、out.flush();");
                out.flush();


                return 0;
            } else {
                return BaseApi.PRINTER_ERROR;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommonUtils.LogWuwei(tag, "_______________writeToPrinterInfo failed__________" + e.getMessage());
            return BaseApi.PRINTER_ERROR;


        } finally {
            if (out != null) {
                try {
                    //第七步：关闭可写流
                    CommonUtils.LogWuwei(tag, "5、socket.shutdownOutput();");
                    socket.shutdownOutput();
                    out.close();
                } catch (Exception e) {
                    CommonUtils.LogWuwei(tag, "out close failed" + e.getMessage());
                }

            }
            if (socket != null) {
                try {
                    //第八步：关闭套接字
                    CommonUtils.LogWuwei(tag, "7、socket.close()");
                    socket.close();
                    CommonUtils.LogWuwei(tag, "-------done------");

                } catch (Exception e) {
                    CommonUtils.LogWuwei(tag, "socket close failed:" + e.getMessage());
                }
            }


            socket = null;
            out = null;
        }

    }

    public OrderValue setOrderVaule(int type,String vaule)
    {
        OrderValue ov = new OrderValue();
        ov.type = type;
        ov.vaule = vaule;
        return  ov;
    }

    /**
     * 连接打印机测试
     *
     * @param ctxt
     * @return 0->success -1->failure
     */
    public static int writeTest(final Context ctxt, final String ip) {

        if (CommonUtils.executeCammand(ip)) {
            return 0;
        } else {
            return -1;
        }
    }

}
