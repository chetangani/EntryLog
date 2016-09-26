package in.entrylog.entrylog.values;

import android.devkit.api.Misc;
import android.devkit.api.SerialPort;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.util.ArrayUtil;
import in.entrylog.entrylog.util.Encoder;
import in.entrylog.entrylog.util.FileUtil;
import in.entrylog.entrylog.util.HexDump;
import in.entrylog.entrylog.util.ImageProcessing;
import in.entrylog.entrylog.util.PrintPic;
import in.entrylog.entrylog.util.Printer;

/**
 * Created by Admin on 29-Aug-16.
 */
public class EL101_102 {
    static final int MSG_RECV = 3;
    static final int MSG_DRAW_TXT = 4;
    static final int SendCommand = 5;
    static final int PAPER_TEST = 6;
    static final int FEED = 7;

    String configCom = "/dev/ttyVK1";
    static byte[] recvBuf;
    long begin;
    public SerialPort mSerialPort;
    public static int recStatus = -1;
    public static int printerStatus = 0;
    public static Handler timehandler = new Handler();
    public static boolean imageprinting = false;
    PrintingService printingService = new PrintingService();
    static ArrayList<String> printingdisplay;
    FunctionCalls functionCalls = new FunctionCalls();

    public Runnable timerunnable = new Runnable() {

        @Override
        public void run() {
            int size = recvBuf.length;
            if (size == 0) {
                String vstrMsg = "Recv uart data time out !";
                mHandler.obtainMessage(MSG_DRAW_TXT, vstrMsg).sendToTarget();
            } else {
                mHandler.obtainMessage(MSG_RECV, recvBuf).sendToTarget();
            }
        }
    };

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECV:
                    timehandler.removeCallbacks(timerunnable);
                    String temp1 = bytetoASCIIString((byte[]) msg.obj);
                    String temp = HexDump.dumpHex((byte[]) msg.obj);
                    if (temp.equals("0x20")) {
                        printerStatus = -1;
                    }
                    if (temp.equals("0x00")) {
                        printerStatus = 0;
                    }
                    if (recStatus == 1) {
                        recStatus = -1;
                    }
                    recvBuf = new byte[0];
                    break;

                case MSG_DRAW_TXT:
                    break;

                case SendCommand:
                    System.out.println("printerStatus:" + printerStatus);
                    if (printerStatus == 0) {
                        SendCommad((byte[]) msg.obj);
                    }
                    break;

                case FEED:    //feed paper one line	    0-255
                    SendCommad(new byte[]{0x1b, 0x64, -125});
                    break;

                case PAPER_TEST:  //test whether no paper
                    SendCommad(new byte[]{0x10, 0x04, 0x02});
                    break;

            }
        }
    };

    public void SendCommad(byte[] order) {
        if (mSerialPort != null) {
            try {
                if (imageprinting) {
                    if (!(order.length == 3)) {
                        recvBuf = new byte[0];
                        mSerialPort.getOutputStream().write(order);
                        if (order.length < 200) {
                            // too much message show will affect the UI
                        } else {
                        }
                    }
                } else {
                    recvBuf = new byte[0];
                    mSerialPort.getOutputStream().write(order);
                    if (order.length < 200) {
                        // too much message show will affect the UI
                    } else {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    private static String bytetoASCIIString(byte[] bytearray) {
        String result = "";
        char temp;

        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }

    public void printBarCode(String str){

    	/*
    	set barCode height   area 1-255  default 162
    	byte[] barHeight=new byte[]{0x1d,0x68,162-256};
    	SendCommad(barHeight);
        */

    	 /*
    	 //set barCode width area  2-6  default 3
    	 byte[] barWidth=new byte[]{0x1d,0x77,0x02};
    	 SendCommad(barWidth);
    	 */
        //set HRI character postion
        //0:no  1:up   2:below    3:both up and below
        byte[] hri=new byte[]{0x1d,0x48,0x00};
        SendCommad(hri);
        //set align center
        SendCommad(new byte[]{0x1b,0x61,0x01});

        /**
         * code type
         * UPC-A    65
         * UPC-E    66
         * JAN13    67
         * JAN8     68
         * code39   69   0x45
         * ITF      70
         * codabar  71
         * code93   72
         * code128  73
         */

        byte[] head=new byte[]{0x1d,0x6b,0x48,(byte)str.length()};

        byte[] body= ArrayUtil.stringToBytes(str, str.length());
        byte[] total=ArrayUtil.MergerArray(head, body);
        SendCommad(total);
    }

    public void printString(String str) {
        if((str!=null)&&(str.getBytes().length!=0)){
            byte[] send = null;
            try {
                send = addEnter(str.getBytes("GB2312"));
            }catch (Exception e) {
                e.printStackTrace();
            }
            SendCommad(send);
        }
    }

    public void Dataappend(StringBuilder sb, String Display, String data) {
        sb.append(Display+": "+data+"\n");
    }

    public byte[] addEnter(byte[] buf) {
        int i;
        byte[] bufret = new byte[buf.length + 1];

        for (i = 0; i < buf.length; i++) {

            bufret[i] = buf[i];
        }
        bufret[bufret.length - 1] = 0x0a;

        return bufret;
    }

    public void EnableBtn(boolean enabled) {
        Misc.printerEnable(enabled);
        if (enabled) {
            try {
                recvBuf = new byte[0];
                mSerialPort = new SerialPort(configCom, 115200, 0);
                if (mSerialPort != null)
                    new readThread().start();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean EnablePrinter(boolean enabled) {
        boolean result = false;
        Misc.printerEnable(enabled);
        if (enabled) {
            try {
                recvBuf = new byte[0];
                mSerialPort = new SerialPort(configCom, 115200, 0);
                if (mSerialPort != null)
                    new readThread().start();
                result = true;
                EnableBtn(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public class readThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if ((mSerialPort == null) || (mSerialPort.getInputStream() == null))
                    return;
                byte[] buffer = new byte[65536];
                int size = 0;
                try {
                    while (size == 0) {
                        if (mSerialPort == null)
                            return;
                        size = mSerialPort.getInputStream().available();
                    }
                    size = mSerialPort.getInputStream().read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (size > 0) {
                    byte[] recv = new byte[size];
                    for (int i = 0; i < size; i++)
                        recv[i] = buffer[i];

                    recvBuf = ArrayUtil.MergerArray(recvBuf, recv);
                    timehandler.removeCallbacksAndMessages(null);
                    timehandler.postDelayed(timerunnable, 300);
                }
            }
        }
    }

    public void printQRcode(int width,String str){
        //generate a qrcode bitmap
        final int dimension = width;
        Encoder encoder = new Encoder.Builder()
                .setBackgroundColor(0xFFFFFF)
                .setCodeColor(0xFF000000)
                .setOutputBitmapWidth(dimension)
                .setOutputBitmapHeight(dimension)
                .setOutputBitmapPadding(0) //set no padding
                .build();
        final Bitmap QRCodeImage = encoder.encode(str);
        //save the qrimage
        //saveBitmap(QRCodeImage);
        //print image
        QRCodeImage(QRCodeImage);
    }

    public String QRCodeImage(Bitmap bitmap){
        String set = "";


        if(printerStatus==-1){
            System.out.println("no paper");
            return "false";
        }

        PrintPic localPrintPic = new PrintPic();

        localPrintPic.initCanvas(bitmap.getWidth(), bitmap.getHeight());  //
        localPrintPic.initPaint();
        localPrintPic.drawImage(0.0F, 0.0F, bitmap);
        byte[] var4  = localPrintPic.printDraw();
        byte[] var2 = new byte[1160];// ??????48???,???384???    ????24?,????48???

        int i = 0;
        if (localPrintPic.getLength()<=0)
            return "";
        int index =0;
        byte [] sendbytesNew = null;
        int var1 = 0;
        int var13=0;

        int length=localPrintPic.getLength()%24>0?localPrintPic.getLength()/24+1:localPrintPic.getLength()/24;

        begin =System.currentTimeMillis();
        System.out.println("begin:"+begin);
        for(int row=0;row<length;row++)
        {
            if(printerStatus==-1){
                System.out.println("no paper");
                return "false";
            }
            index = 0;
            var2[0] = 0x1d;
            var2[1] = 0x76;
            var2[2] = 0x30;
            var2[3] = 0;
            var2[4] = (byte)(localPrintPic.getWidth() / 8);  //xl  ?????? ??
            var2[5] = 0;    //xh  ?????? ??
            int line=0;
            if(localPrintPic.getLength()%24>0&&row==length-1){
                line=localPrintPic.getLength()%24;
            }else{
                line=24;
            }
            var2[6] = (byte)line;    //yl  ??????  ??
            var2[7] = 0;   //yh	   ??????  ??
            var13  =8;
            for(int var14 = 0; var14 <( (localPrintPic.getWidth() / 8)*line); var14++){

                var2[var13] = var4[var1];
                var13 = var13+1;
                var1 =  var1 + 1;
                index++;
            }
            sendbytesNew = new byte[8+(localPrintPic.getWidth() / 8*line)];
            for(i=0;i<sendbytesNew.length;i++)
            {
                sendbytesNew[i] = var2[i];
            }
            //System.out.println("row:"+row+" send:"+HexDump.dumpHexString(sendbytesNew));

            Message msg=Message.obtain();
            msg.what=SendCommand;
            msg.obj=sendbytesNew;
            mHandler.sendMessageDelayed(msg,180*row);
        }

        //feed paper

        /*mHandler.sendEmptyMessageDelayed(FEED, length*180);
        // test no paper
        mHandler.sendEmptyMessageDelayed(PAPER_TEST,length*180+180);*/

        return set;
    }

    public String PrintHumanImage(ImageView imageview){
        Bitmap actualbitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        int width=256;
        int height=192;
        Bitmap bitmap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
        String set = "";
        if(printerStatus==-1){
            System.out.println("no paper");
            return "false";
        }
        byte[] sendbytes = Printer.POS_PrintPicture(bitmap, bitmap.getWidth(), 0);

        Message msg=Message.obtain();
        msg.what=SendCommand;
        msg.obj=sendbytes;
        mHandler.sendMessageDelayed(msg,20);

        //feed paper
        mHandler.sendEmptyMessageDelayed(FEED, 180);
        // test no paper
        mHandler.sendEmptyMessageDelayed(PAPER_TEST, 180 + 180);

        return set;
    }

    public void SaveData(StringBuilder printdetails, String Visitor_Name, String Visitor_Mobile, String Visitor_Fromaddress,
                         String Visitor_ToMeet, String Visitor_CheckinTime, String Visitor_Designation, String Department,
                         String Purpose, String House_number, String Flat_number, String Block, String No_Visitor,
                         String aClass, String Section, String Student_Name, String ID_Card, String CheckinUser, String Email,
                         String Vehicleno, boolean reprint) {
        try {
            HashSet<String> Printdisplay = new HashSet<>();
            Printdisplay = printingService.printingset;
            printingdisplay = new ArrayList<>();
            printingdisplay.addAll(Printdisplay);
            Collections.sort(printingdisplay);
            if (printingdisplay.size() > 0) {
                functionCalls.LogStatus("Printing Display Size: "+printingdisplay.size());
                for (int i = 0; i < printingdisplay.size(); i++) {
                    String PrintOrder = printingdisplay.get(i).toString();
                    functionCalls.LogStatus("Print Order: "+PrintOrder);
                    String Display = PrintOrder.substring(2, PrintOrder.length());
                    functionCalls.LogStatus("Display: "+Display);
                    if (Display.equals("Name")) {
                        Dataappend(printdetails, Display, Visitor_Name);
                    }
                    if (Display.equals("Mobile")) {
                        Dataappend(printdetails, Display, Visitor_Mobile);
                    }
                    if (Display.equals("From")) {
                        Dataappend(printdetails, Display, Visitor_Fromaddress);
                    }
                    if (Display.equals("To Meet")) {
                        Dataappend(printdetails, Display, Visitor_ToMeet);
                    }
                    if (Display.equals("Date")) {
                        /*if (!reprint) {
                            Visitor_CheckinTime = functionCalls.CurrentDate() + " " + functionCalls.CurrentTime();
                            Dataappend(printdetails, Display, Visitor_CheckinTime);
                        } else {
                            Dataappend(printdetails, Display, Visitor_CheckinTime);
                        }*/
                        Dataappend(printdetails, Display, Visitor_CheckinTime);
                    }
                    if (Display.equals("Visitor Designation")) {
                        Dataappend(printdetails, Display, Visitor_Designation);
                    }
                    if (Display.equals("Department")) {
                        Dataappend(printdetails, Display, Department);
                    }
                    if (Display.equals("Purpose")) {
                        Dataappend(printdetails, Display, Purpose);
                    }
                    if (Display.equals("House No")) {
                        Dataappend(printdetails, Display, House_number);
                    }
                    if (Display.equals("Flat No")) {
                        Dataappend(printdetails, Display, Flat_number);
                    }
                    if (Display.equals("Block")) {
                        Dataappend(printdetails, Display, Block);
                    }
                    if (Display.equals("No of Visitor")) {
                        Dataappend(printdetails, Display, No_Visitor);
                    }
                    if (Display.equals("Class")) {
                        Dataappend(printdetails, Display, aClass);
                    }
                    if (Display.equals("Section")) {
                        Dataappend(printdetails, Display, Section);
                    }
                    if (Display.equals("Student")) {
                        Dataappend(printdetails, Display, Student_Name);
                    }
                    if (Display.equals("Id Card")) {
                        Dataappend(printdetails, Display, ID_Card);
                    }
                    if (Display.equals("Entry")) {
                        Dataappend(printdetails, Display, CheckinUser);
                    }
                    if (Display.equals("Email")) {
                        Dataappend(printdetails, Display, Email);
                    }
                    if (Display.equals("Vehicle Number")) {
                        Dataappend(printdetails, Display, Vehicleno);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
