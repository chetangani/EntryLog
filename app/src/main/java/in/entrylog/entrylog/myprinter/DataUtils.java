package in.entrylog.entrylog.myprinter;

import java.util.Locale;
import java.util.Random;

/**
 * Created by Admin on 20-Jun-16.
 */
public class DataUtils {
    DataUtils() {
    }

    public static byte[] getRandomByteArray(int nlength) {
        byte[] data = new byte[nlength];
        Random rmByte = new Random(System.currentTimeMillis());

        for(int i = 0; i < nlength; ++i) {
            data[i] = (byte)rmByte.nextInt(256);
        }

        return data;
    }

    public static boolean bytesEquals(byte[] d1, int offset1, byte[] d2, int offset2, int length) {
        if(d1 != null && d2 != null) {
            if(offset1 + length <= d1.length && offset2 + length <= d2.length) {
                for(int i = 0; i < length; ++i) {
                    if(d1[i + offset1] != d2[i + offset2]) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;

        for(int send = 0; send < data.length; ++send) {
            length += data[send].length;
        }

        byte[] var6 = new byte[length];
        int k = 0;

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                var6[k++] = data[i][j];
            }
        }

        return var6;
    }

    public static String bytesToStr(byte[] rcs) {
        if(rcs == null) {
            return "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < rcs.length; ++i) {
                String tmp = Integer.toHexString(rcs[i] & 255);
                tmp = tmp.toUpperCase(Locale.getDefault());
                if(tmp.length() == 1) {
                    stringBuilder.append("0x0" + tmp);
                } else {
                    stringBuilder.append("0x" + tmp);
                }

                if(i % 16 != 15) {
                    stringBuilder.append(" ");
                } else {
                    stringBuilder.append("\n");
                }
            }

            return stringBuilder.toString();
        }
    }
}

