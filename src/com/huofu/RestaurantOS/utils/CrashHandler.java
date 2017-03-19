package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.support.DialogCaller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * author: Created by zzl on 15/11/3.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler instance; // 单例模式

    private Context context; // 程序Context对象
    private Thread.UncaughtExceptionHandler defalutHandler; // 系统默认的UncaughtException处理类
    private DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);

    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     *
     * @return CrashHandler
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }

        return instance;
    }

    /**
     * 异常处理初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        // 获取系统默认的UncaughtException处理器
        defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        // 自定义错误处理
        boolean res = handleException(ex);
        if (!res && defalutHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            defalutHandler.uncaughtException(thread, ex);

        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();

                ex.printStackTrace();

                /*1、Throwable类中的getStackTrace()方法，根据这个方法可以得到函数的逐层调用地址，
                其返回值为StackTraceElement[]；
                2、StackTraceElement类，
                其中四个方法getClassName()，getFileName()，getLineNumber()，getMethodName()在调试程序打印Log时非常有用；*/
                StackTraceElement[] k = ex.getStackTrace();
                String err = "[错误原因: " + ex.getMessage() + "]"+
                            " 文件："+k[0].getFileName()+" 行号: "+k[0].getLineNumber()+" 方法名 "+k[0].getMethodName();

                Toast.makeText(context, "程序出现异常." + err, Toast.LENGTH_LONG).show();
                CommonUtils.LogWuwei(TAG, "程序出现异常:" + err);
                showAlertDialog("程序出现异常:" + err);
                Looper.loop();
            }

        }.start();

        // 收集设备参数信息 \日志信息
        return true;
    }

    /**
     * 显示错误弹窗
     * @param msg
     */
    private void showAlertDialog(final String msg)
    {
        try
        {
            MainApplication.getmActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogCaller.showDialog("程序遇到了问题", msg, "反馈给开发者", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CommonUtils.sendMailToMe(MainApplication.getContext(), msg);
                        }
                    }, "", null);

                }
            });

        }
        catch (Exception e)
        {
            Toast.makeText(context, "显示错误窗口失败" + e.getMessage(), Toast.LENGTH_LONG).show();
            CommonUtils.LogWuwei(TAG, "显示错误窗口失败" + e.getMessage());
        }


    }
}

