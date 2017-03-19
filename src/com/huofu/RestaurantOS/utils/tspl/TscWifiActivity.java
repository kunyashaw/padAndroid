package com.huofu.RestaurantOS.utils.tspl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huofu.RestaurantOS.utils.FIleUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * author: Created by zzl on 15/12/25.
 */

public class TscWifiActivity extends Activity {
    private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;
    private InputStream InStream = null;
    private OutputStream OutStream = null;
    private Socket socket = null;
    private String printerstatus = "";
    private int last_bytes = 0;
    private byte[] buffer = new byte[1024];
    private byte[] readBuf = new byte[1024];
    private Button connect = null;
    private Button closeport = null;
    private Button sendfile = null;
    private Button status = null;
    private TextView tv1 = null;

    public TscWifiActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(2130903040);
        this.tv1 = (TextView) this.findViewById(2131165187);
        this.connect = (Button) this.findViewById(2131165184);
        this.connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TscWifiActivity.this.openport("192.168.1.40", 9100);
                TscWifiActivity.this.sendcommand("SIZE 3,1\n");
                TscWifiActivity.this.sendcommand("PRINT 5\n");
            }
        });
        this.closeport = (Button) this.findViewById(2131165185);
        this.closeport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TscWifiActivity.this.closeport();
            }
        });
        this.sendfile = (Button) this.findViewById(2131165186);
        this.sendfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TscWifiActivity.this.openport("192.168.1.58", 9100);
                TscWifiActivity.this.downloadpcx("UL.PCX");
                TscWifiActivity.this.downloadbmp("Triangle.bmp");
                TscWifiActivity.this.downloadttf("ARIAL.TTF");
                TscWifiActivity.this.setup(70, 110, 4, 4, 0, 0, 0);
                TscWifiActivity.this.clearbuffer();
                TscWifiActivity.this.sendcommand("SET TEAR ON\n");
                TscWifiActivity.this.sendcommand("SET COUNTER @1 1\n");
                TscWifiActivity.this.sendcommand("@1 = \"0001\"\n");
                TscWifiActivity.this.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
                TscWifiActivity.this.sendcommand("PUTPCX 100,300,\"UL.PCX\"\n");
                TscWifiActivity.this.sendcommand("PUTBMP 100,520,\"Triangle.bmp\"\n");
                TscWifiActivity.this.sendcommand("TEXT 100,760,\"ARIAL.TTF\",0,15,15,\"THIS IS ARIAL FONT\"\n");
                TscWifiActivity.this.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
                TscWifiActivity.this.printerfont(100, 250, "3", 0, 1, 1, "987654321");
                TscWifiActivity.this.printlabel(10, 1);
                TscWifiActivity.this.sendfile("zpl.txt");
                TscWifiActivity.this.closeport();
            }
        });
        this.status = (Button) this.findViewById(2131165188);
        this.status.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String QQ = TscWifiActivity.this.batch();
                TscWifiActivity.this.tv1.setText(QQ);
            }
        });
    }

    public void onStart() {
        super.onStart();
        Log.e("THINBTCLIENT", "++ ON START ++");
    }

    public void onResume() {
        super.onResume();
        Log.e("THINBTCLIENT", "+ ON RESUME +");
        Log.e("THINBTCLIENT", "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
    }

    public void onPause() {
        super.onPause();
        Log.e("THINBTCLIENT", "- ON PAUSE -");
        if (this.OutStream != null) {
            try {
                this.OutStream.flush();
            } catch (IOException var3) {
                Log.e("THINBTCLIENT", "ON PAUSE: Couldn\'t flush output stream.", var3);
            }
        }

        try {
            this.socket.close();
        } catch (IOException var2) {
            Log.e("THINBTCLIENT", "ON PAUSE: Unable to close socket.", var2);
        }

    }

    public void onStop() {
        super.onStop();
        Log.e("THINBTCLIENT", "-- ON STOP --");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("THINBTCLIENT", "--- ON DESTROY ---");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(2131099648, menu);
        return true;
    }

    public void openport(String ipaddress, int portnumber) {
       // StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //StrictMode.setVmPolicy((new android.os.StrictMode.VmPolicy.Builder()).detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        try {
            this.socket = new Socket(ipaddress, portnumber);
            this.InStream = this.socket.getInputStream();
            this.OutStream = this.socket.getOutputStream();
        } catch (Exception var4) {
            ;
        }

    }

    public void sendcommand(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendcommand(byte[] message) {
        try {
            this.OutStream.write(message);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public String status() {
        byte[] message = new byte[]{(byte) 27, (byte) 33, (byte) 63};

        try {
            this.OutStream.write(message);
        } catch (IOException var4) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var4);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        int tim;
        try {
            while (this.InStream.available() > 0) {
                this.readBuf = new byte[1024];
                tim = this.InStream.read(this.readBuf);
                Log.e("", "tim is " + tim);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        if (this.readBuf[0] == 2 && this.readBuf[5] == 3) {
            for (tim = 0; tim <= 7; ++tim) {
                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ready";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 64 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 96 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Head Open";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 72 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 68 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Ribbon Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "No Paper";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 66 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Jam";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 65 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Paper Empty";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 67 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Cutting";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 75 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Press Print Key";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 76 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Waiting to Take Label";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 80 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Printing Batch";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 96 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }

                if (this.readBuf[tim] == 2 && this.readBuf[tim + 1] == 69 && this.readBuf[tim + 2] == 64 && this.readBuf[tim + 3] == 64 && this.readBuf[tim + 4] == 64 && this.readBuf[tim + 5] == 3) {
                    this.printerstatus = "Pause";
                    this.readBuf = new byte[1024];
                    break;
                }
            }
        }

        return this.printerstatus;
    }

    public String batch() {
        boolean printvalue = false;
        String printbatch = "";
        String stringbatch = "0";
        String message = "~HS";
        this.readBuf = new byte[1024];
        byte[] batcharray = new byte[]{(byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48};
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var9) {
            Log.e("THINBTCLIENT", "ON RESUME: Exception during write.", var9);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var8) {
            var8.printStackTrace();
        }

        int i;
        try {
            while (this.InStream.available() > 50) {
                this.readBuf = new byte[1024];
                i = this.InStream.read(this.readBuf);
            }
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        if (this.readBuf[0] == 2) {
            System.arraycopy(this.readBuf, 55, batcharray, 0, 8);

            for (i = 0; i <= 7; ++i) {
                if (batcharray[i] == 44) {
                    batcharray = new byte[]{(byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57};
                }
            }

            stringbatch = new String(batcharray);
            int var11 = Integer.parseInt(stringbatch);
            printbatch = Integer.toString(var11);
            if (printbatch == "99999999") {
                printbatch = "";
            }
        }

        return printbatch;
    }

    public void closeport() {
        try {
            this.socket.close();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message = "";
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }

        message = size + "\n" + speed_value + "\n" + density_value + "\n" + sensor_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }

    public void clearbuffer() {
        String message = "CLS\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void barcode(int x, int y, String type, int height, int human_readable, int rotation, int narrow, int wide, String string) {
        String message = "";
        String barcode = "BARCODE ";
        String position = x + "," + y;
        String mode = "\"" + type + "\"";
        String height_value = "" + height;
        String human_value = "" + human_readable;
        String rota = "" + rotation;
        String narrow_value = "" + narrow;
        String wide_value = "" + wide;
        String string_value = "\"" + string + "\"";
        message = barcode + position + " ," + mode + " ," + height_value + " ," + human_value + " ," + rota + " ," + narrow_value + " ," + wide_value + " ," + string_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var22) {
            var22.printStackTrace();
        }

    }

    public void printerfont(int x, int y, String size, int rotation, int x_multiplication, int y_multiplication, String string) {
        String message = "";
        String text = "TEXT ";
        String position = x + "," + y;
        String size_value = "\"" + size + "\"";
        String rota = "" + rotation;
        String x_value = "" + x_multiplication;
        String y_value = "" + y_multiplication;
        String string_value = "\"" + string + "\"";
        message = text + position + " ," + size_value + " ," + rota + " ," + x_value + " ," + y_value + " ," + string_value + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var18) {
            var18.printStackTrace();
        }

    }

    public void printlabel(int quantity, int copy) {
        String message = "";
        message = "PRINT " + quantity + ", " + copy + "\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void formfeed() {
        String message = "";
        message = "FORMFEED\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void nobackfeed() {
        String message = "";
        message = "SET TEAR OFF\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendfile(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(data);
            fis.close();
        } catch (Exception var4) {
            ;
        }

    }

    public void downloadpcx(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var6) {
            ;
        }

    }

    public void downloadbmp(String filename,Context ctxt) {
        try {
            //String name =  android.os.Environment.getExternalStorageState()+File.separator+filename;
            //FileInputStream fis = new FileInputStream(name);//("/sdcard/Download/" + filename);
            byte[] data = new FIleUtils(ctxt).readSDFile(filename).getBytes("gb2312");//new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            this.OutStream.write(download_head);
            this.OutStream.write(data);
        } catch (Exception var6) {
            Log.e("", "" + var6.getMessage());
        }

    }

    public void downloadbmp(String filename) {
        try {
            //String name =  android.os.Environment.getExternalStorageState()+File.separator+filename;
            //FileInputStream fis = new FileInputStream(name);//("/sdcard/Download/" + filename);
            byte[] data = new FIleUtils(getApplicationContext()).readSDFile(filename).getBytes("gb2312");//new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            this.OutStream.write(download_head);
            this.OutStream.write(data);
        } catch (Exception var6) {
            Log.e("", "" + var6.getMessage());
        }

    }

    public void downloadttf(String filename) {
        try {
            FileInputStream fis = new FileInputStream("/sdcard/Download/" + filename);
            byte[] data = new byte[fis.available()];
            String download = "DOWNLOAD F,\"" + filename + "\"," + data.length + ",";
            byte[] download_head = download.getBytes();

            while (fis.read(data) != -1) {
                ;
            }

            this.OutStream.write(download_head);
            this.OutStream.write(data);
            fis.close();
        } catch (Exception var6) {
            ;
        }

    }


    public  void writePackagedInfo(String takeSerialNum) {
        try {
            //String originalText = "TEXT 550,50,\"TSS24.BF2\",180," + font_size + "," + font_size + ",\"" + msg + "\"\n";
          /*  String originalText = "TEXT 260,210,\"TSS24.BF2\",180,1,1,\"万通中心店 午餐\"\n";
            sendcommand((originalText.getBytes("gb2312")));*/

            String originalText = "TEXT 270,200,\"TSS24.BF2\",180,4,4,\""+takeSerialNum+"\"\n";
            sendcommand((originalText.getBytes("gb2312")));

            originalText = "TEXT 300,110,\"TSS24.BF2\",180,3,3,\"(打包)\"\n";
            sendcommand((originalText.getBytes("gb2312")));
/*
            originalText = "TEXT 315,110,\"TSS24.BF2\",180,1,1,\"应收:20.5元\"\n";
            sendcommand((originalText.getBytes("gb2312")));

            originalText = "TEXT 170,110,\"TSS24.BF2\",180,1,1,\"实收: 30.5元\"\n";
            sendcommand((originalText.getBytes("gb2312")));

            originalText = "TEXT 315,70,\"TSS24.BF2\",180,1,1,\"优惠: 10元\"\n";
            sendcommand((originalText.getBytes("gb2312")));*/

            printlabel(1, 1);//打印出缓冲区的数据,第一个参数是打印的分数，第二个是每份打印的张数
            clearbuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

