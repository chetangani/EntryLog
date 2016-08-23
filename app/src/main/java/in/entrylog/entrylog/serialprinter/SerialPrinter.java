package in.entrylog.entrylog.serialprinter;

import android.content.Context;
import android.devkit.api.Misc;
import android.devkit.api.SerialPort;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import in.entrylog.entrylog.myprinter.Pos;

/**
 * Created by Admin on 01-Jul-16.
 */
public class SerialPrinter {
    String configCom  = "/dev/ttyVK1";
    static byte[] recvBuf;
    SerialPort mSerialPort;
    static int recStatus = -1;

    public void EnableBtn(boolean enabled, Context context){
        Misc.printerEnable(enabled);

        if(enabled){
            try {
                recvBuf = new byte[0];
                mSerialPort = new SerialPort(configCom, 115200, 0);
                /*if(mSerialPort!=null)
                    new readThread().start();*/
            } catch (SecurityException e) {
                e.printStackTrace();
                setShowMsg(context, "Port Err: "+configCom+" " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                setShowMsg(context, "Port Err: "+configCom+" " + e.toString());
            }
        }
    }

    public byte[] addEnter(byte[] buf){
        int i;
        byte[] bufret = new byte[buf.length+1];

        for(i=0;i<buf.length;i++){

            bufret[i] = buf[i];
        }
        bufret[bufret.length-1] = 0x0a;

        return bufret;
    }

    public void printString(String str){
        String s1 = null;
        try {
            Log.d("debug", "Send Data Initialzing");
            //Read and Display from text file and print
            File myFile = new File(str);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                Log.d("debug", "OutputStream Started");
                String s = reader.nextLine();
                s1 = s;
                Log.d("debug", s1);
                if ((s1.equals("**bc"))) {
                    /*BarCode(2);*/
                    /*BarCode();*/
                    printBarCode("12345678abc");
                    Thread.sleep(500);
                } else if (s1.equals("**qr" + "\n")) {
                    /*QRCode();*/
                    Thread.sleep(500);
                } else if (s1.equals("**pic" + "\n")) {
                    /*PrintImage(photo_img);*/
                    Thread.sleep(1000);
                } else if((s1!=null)&&(s1.getBytes().length!=0)){
                    byte[] send = null;
                    try {
                        //send = addEnter(str.getBytes("ISO8859-16"));
                        //打印包含中文字符的文字 只能使用GBK2312，其他外语特殊字符用UTF-8 ，测试匈牙利语需要用iso859-16
                        //If want to print Chinese character use "GBK2312", others use "UTF-8";
                        send = addEnter(s1.getBytes("GB2312"));
                        //send = str.getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    SendCommad(send);
                }
            }
        } catch (IndexOutOfBoundsException e) {

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SendCommad(byte[] order){
        if(mSerialPort!=null){
            try {
                recvBuf = new byte[0];
                mSerialPort.getOutputStream().write(order);
                /*Log.d("debug","Send:"+HexDump.dumpHex(order));*/
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{

        }

    }

    public void printBarCode(String str){

        byte[] head=new byte[]{0x1E,0x42,0x34,0x40,0x32,0x31,(byte)str.length()};

        byte[] body=ArrayUtil.stringToBytes(str, str.length());
        byte[] total=ArrayUtil.MergerArray(head, body);
        total=ArrayUtil.MergerArray(total, new byte[]{0x0A,0x1b,0x4a,0x30});
        //byte[] test=new byte[]{0x1e,0x42,0x34,0x50,0x32,0x30,0x0a,0x31,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x41,0x42,0x0a};
        SendCommad(total);
        //feepaper
        mHandler.sendEmptyMessageDelayed(0x15, 800);
    }

    public static void setShowMsg(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public void SaveText() {

        String path = filepath("Textfile");
        String filename= "Test.txt";
        try
        {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append("------------------------------"+"\r\n");
            myOutWriter.append("ASKDIAL"+"\r\n");
            myOutWriter.append("VISITOR"+"\r\n");
            myOutWriter.append("Name: ");
            myOutWriter.append("Chetan"+"\r\n");
            myOutWriter.append("From: ");
            myOutWriter.append("Bengaluru"+"\r\n");
            myOutWriter.append("To Meet: ");
            myOutWriter.append("Vinayaka"+"\r\n");
            myOutWriter.append("Date: ");
            myOutWriter.append("07/07/2016"+"\r\n");
            myOutWriter.append("Time: ");
            myOutWriter.append("12:33 PM"+"\r\n");
            myOutWriter.append("Vehicle No: ");
            myOutWriter.append("KA 04 HW 9032"+"\r\n");
            myOutWriter.append("Entry: ");
            myOutWriter.append("gate1"+"\r\n");
            /*myOutWriter.append("**pic"+"\r\n");*/
            /*myOutWriter.append("**sp"+"\r\n");*/
            /*myOutWriter.append("**qr"+"\r\n");
            myOutWriter.append("**sp"+"\r\n");*/
            /*myOutWriter.append("**bc"+"\r\n");*/
            myOutWriter.append(line(30)+"\r\n");
            /*myOutWriter.append("**sp"+"\r\n");
            myOutWriter.append("**sp"+"\r\n");*/
            myOutWriter.close();
            fOut.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Dotted line
    public String line(int length) {
        StringBuilder sb5 = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb5.append("-");
        }
        return (sb5.toString());
    }

    public String filepath(String value){
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), "EntryLog"
                + File.separator + value);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        String pathname = ""+dir;
        return pathname;
    }

    Handler mHandler = new Handler(){

        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                case 0x15:
                    try {
                        //SendCommad(" \n".getBytes("UTF-8"));
                        SendCommad(new byte[]{0x1b,0x4a,0x30});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void BarCode() {
        Pos pos = new Pos();
        String strBarcode = "0"+"9972452488"+"0";
        int nOrgx = 0 * 12;
        int nType = 0x41 + 2;
        int nWidthX = 1 + 2;
        int nHeight = (2 + 1) * 24;
        int nHriFontType = 0;
        int nHriFontPosition = 2;
        pos.POS_S_SetBarcode(strBarcode, nOrgx, nType, nWidthX,
                nHeight, nHriFontType, nHriFontPosition);
    }

    public String PrintImage(Bitmap bitmap){

        String set = "";
        try
        {
            PrintPic localPrintPic = new PrintPic();

            /*localPrintPic.initCanvas(bitmap.getWidth(), bitmap.getHeight());*/  //image width cannot be larger than 280px
            localPrintPic.initCanvas(256, 192);
            localPrintPic.initPaint();
            localPrintPic.drawImage(0.0F, 0.0F, bitmap);
            byte[] var4  = localPrintPic.printDraw();
            byte[] var2 = new byte[56];

            int i = 0;
            if (localPrintPic.getLength()<=0)
                return "";
            int index =0;
            byte [] sendbytesNew = null;
            int var1 = 0;
            int var13=0;

            for(int row=0;row<localPrintPic.getLength();row++)
            {
                index = 0;
                var2[0] = 0x1c;
                var2[1] = 0x39;
                var2[2] = 0;
                var2[3] = (byte)(localPrintPic.getWidth() / 8);
                var2[4] = 0;
                var2[5] = 0;
                var2[6] = 0;
                var13  =7;			;
                for(int var14 = 0; var14 < localPrintPic.getWidth() / 8; var14++){

                    var2[var13] = var4[var1];

                    var13 = var13+1;
                    var1 =  var1 + 1;
                    index++;
                }

                // var2[localPrintPic.getWidth()/8+1+6]=0x0d;
                sendbytesNew = new byte[7+(localPrintPic.getWidth() / 8)];
                for(i=0;i<sendbytesNew.length;i++)
                {
                    sendbytesNew[i] = var2[i];
                }
                //

                SendCommad(sendbytesNew);

            }
        }catch(Exception ex)
        {
            set = ex.getMessage();
        }

        mHandler.sendEmptyMessageDelayed(0x15, 10);

        return set;
    }
}
