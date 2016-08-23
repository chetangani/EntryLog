package in.entrylog.entrylog.myprinter;

import android.util.Log;

/**
 * Created by Admin on 20-Jun-16.
 */
public class IO {
    private static final String TAG = "IO";
    private static final boolean CheckDes = false;

    public IO() {
    }

    public int Write(byte[] buffer, int offset, int count) {
        return -1;
    }

    public int Read(byte[] buffer, int offset, int count, int timeout) {
        return -1;
    }

    public boolean IsOpened() {
        return false;
    }

    private boolean POS_CheckKey() {
        byte[] key = "XSH-KCEC".getBytes();
        byte[] random = DataUtils.getRandomByteArray(8);
        boolean result = false;
        boolean HeaderSize = true;
        byte[] recHeader = new byte[5];
        Object recData = null;
        boolean rec = false;
        boolean recDataLen = false;
        byte[] randomlen = new byte[]{(byte)(random.length & 255), (byte)(random.length >> 8 & 255)};
        byte[] data = DataUtils.byteArraysToBytes(new byte[][]{{(byte)31, (byte)31, (byte)2}, randomlen, random});
        this.Write(data, 0, data.length);
        int rec1 = this.Read(recHeader, 0, 5, 1000);
        if(rec1 != 5) {
            return false;
        } else {
            int recDataLen1 = (recHeader[3] & 255) + (recHeader[4] << 8 & 255);
            byte[] recData1 = new byte[recDataLen1];
            rec1 = this.Read(recData1, 0, recDataLen1, 1000);
            if(rec1 != recDataLen1) {
                return false;
            } else {
                byte[] decrypted = new byte[recData1.length + 1];
                DES2 des2 = new DES2();
                des2.yxyDES2_InitializeKey(key);
                des2.yxyDES2_DecryptAnyLength(recData1, decrypted, recData1.length);
                result = DataUtils.bytesEquals(random, 0, decrypted, 0, random.length);
                if(!result) {
                    Log.v("IO random", DataUtils.bytesToStr(random));
                    Log.v("IO decryp", DataUtils.bytesToStr(decrypted));
                }

                return result;
            }
        }
    }

    protected void CheckKCPrinter() {
    }
}

