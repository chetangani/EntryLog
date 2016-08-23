package in.entrylog.entrylog.myprinter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Admin on 16-Jun-16.
 */
public class WorkThread extends Thread {

    private static final String TAG = "WorkThread";
    public static Handler workHandler = null;
    private static Looper mLooper = null;
    public static Handler targetHandler = null;
    private static boolean threadInitOK = false;
    private static boolean isConnecting = false;
    private static BTPrinting bt = null;
    private static Pos pos = new Pos();

    public WorkThread(Handler handler) {
        threadInitOK = false;
        targetHandler = handler;
        if (bt == null)
            bt = new BTPrinting();
    }

    @Override
    public void start() {
        super.start();
        while (!threadInitOK);
    }

    @Override
    public void run() {
        Looper.prepare();
        mLooper = Looper.myLooper();
        if (null == mLooper)
            Log.v(TAG, "mLooper is null pointer");
        else
            Log.v(TAG, "mLooper is valid");
        workHandler = new WorkHandler();
        threadInitOK = true;
        Looper.loop();
    }

    private static class WorkHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Global.MSG_WORKTHREAD_HANDLER_CONNECTBT: {
                    isConnecting = true;

                    pos.Set(bt);

                    String BTAddress = (String) msg.obj;
                    boolean result = bt.Open(BTAddress);

                    Message smsg = targetHandler
                            .obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT);
                    smsg.arg1 = result ? 1 : 0;
                    targetHandler.sendMessage(smsg);

                    isConnecting = false;
                    break;
                }

                // USB写入自带流控制，不需要读取状态。
                case Global.CMD_POS_WRITE: {
                    Bundle data = msg.getData();
                    byte[] buffer = data.getByteArray(Global.BYTESPARA1);
                    int offset = data.getInt(Global.INTPARA1);
                    int count = data.getInt(Global.INTPARA2);

                    Message smsg = targetHandler
                            .obtainMessage(Global.CMD_POS_WRITERESULT);

                    boolean result = false;
                    if (result) {
                        if (pos.IO.Write(buffer, offset, count) == count)
                            smsg.arg1 = 1;
                        else
                            smsg.arg1 = 0;
                    } else {
                        smsg.arg1 = 0;
                    }
                    targetHandler.sendMessage(smsg);

                    break;
                }

                case Global.CMD_POS_PRINTPICTURE: {
                    Bundle data = msg.getData();
                    Bitmap mBitmap = (Bitmap) data.getParcelable(Global.PARCE1);
                    int nWidth = data.getInt(Global.INTPARA1);
                    int nMode = data.getInt(Global.INTPARA2);

                    boolean result = false;
                    Message smsg = targetHandler
                            .obtainMessage(Global.CMD_POS_PRINTPICTURERESULT);
                    if (!result) {
                        pos.POS_PrintPicture(mBitmap, nWidth, nMode);
                        smsg.arg1 = 1;
                    } else {
                        smsg.arg1 = 0;
                    }
                    targetHandler.sendMessage(smsg);

                    break;
                }

                case Global.CMD_POS_SETBARCODE: {
                    Bundle data = msg.getData();
                    Log.d("debug", "WorkThread Barcode Data: "+data);
                    String strBarcode = data.getString(Global.STRPARA1);
                    Log.d("debug", "WorkThread Barcode: "+strBarcode);
                    int nOrgx = data.getInt(Global.INTPARA1);
                    Log.d("debug", "WorkThread Barcode nOrgx: "+nOrgx);
                    int nType = data.getInt(Global.INTPARA2);
                    Log.d("debug", "WorkThread Barcode nType: "+nType);
                    int nWidthX = data.getInt(Global.INTPARA3);
                    Log.d("debug", "WorkThread Barcode nWidthX: "+nWidthX);
                    int nHeight = data.getInt(Global.INTPARA4);
                    Log.d("debug", "WorkThread Barcode nHeight: "+nHeight);
                    int nHriFontType = data.getInt(Global.INTPARA5);
                    Log.d("debug", "WorkThread Barcode nHriFontType: "+nHriFontType);
                    int nHriFontPosition = data.getInt(Global.INTPARA6);
                    Log.d("debug", "WorkThread Barcode nHriFontPosition: "+nHriFontPosition);
                    boolean result = false;
                    Message smsg = targetHandler.obtainMessage(Global.CMD_POS_SETBARCODERESULT);
                    Log.d("debug", "WorkThread Barcode smsg: "+smsg);
                    if (!result) {
                        Log.d("debug", "WorkThread Barcode Result: "+result);
                        pos.POS_S_SetBarcode(strBarcode, nOrgx, nType, nWidthX,
                                nHeight, nHriFontType, nHriFontPosition);
                        smsg.arg1 = 1;
                        Log.d("debug", "WorkThread Barcode smsg: "+smsg.arg1);
                    } else {
                        smsg.arg1 = 0;
                        Log.d("debug", "WorkThread Barcode smsg: "+smsg.arg1);
                    }
                    targetHandler.sendMessage(smsg);

                    break;
                }

                case Global.CMD_POS_SETQRCODE: {
                    Bundle data = msg.getData();
                    Log.d("debug", "data: "+data);
                    String strQrcode = data.getString(Global.STRPARA1);
                    Log.d("debug", "strQrcode: "+strQrcode);
                    int nWidthX = data.getInt(Global.INTPARA1);
                    Log.d("debug", "nWidthX: "+nWidthX);
                    int nVersion = data.getInt(Global.INTPARA2);
                    Log.d("debug", "nVersion: "+nVersion);
                    int necl = data.getInt(Global.INTPARA3);
                    Log.d("debug", "necl: "+necl);
                    boolean result = false;
                    Message smsg = targetHandler.obtainMessage(Global.CMD_POS_SETQRCODERESULT);
                    Log.d("debug", "smsg: "+smsg);
                    if (!result) {
                        pos.POS_S_SetQRcode(strQrcode, nWidthX, nVersion, necl);
                        smsg.arg1 = 1;
                        Log.d("debug", "smsg.arg1: "+smsg.arg1);
                    } else {
                        smsg.arg1 = 0;
                        Log.d("debug", "smsg.arg1: "+smsg.arg1);
                    }
                    targetHandler.sendMessage(smsg);
                    break;
                }
            }

        }

    }

    public void quit() {
        try {
            if (null != mLooper) {
                mLooper.quit();
                mLooper = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectBt() {
        try {
            bt.Close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectBt(String BTAddress) {
        if ((null != workHandler) && (null != mLooper)) {
            Message msg = workHandler.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBT);
            msg.obj = BTAddress;
            workHandler.sendMessage(msg);
        } else {
            if (null == workHandler)
                Log.v(TAG, "workHandler is null pointer");

            if (null == mLooper)
                Log.v(TAG, "mLooper is null pointer");
        }
    }

    public boolean isConnected() {
        if (bt.IsOpened()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void handleCmd(int cmd, Bundle data) {
        Log.d("debug", "Handle CMD started");
        if ((null != workHandler) && (null != mLooper)) {
            Message msg = workHandler.obtainMessage(cmd);
            Log.d("debug", "Msg: "+msg);
            msg.setData(data);
            workHandler.sendMessage(msg);
        } else {
            if (null == workHandler)
                Log.v(TAG, "workHandler is null pointer");

            if (null == mLooper)
                Log.v(TAG, "mLooper is null pointer");
        }
    }
}
