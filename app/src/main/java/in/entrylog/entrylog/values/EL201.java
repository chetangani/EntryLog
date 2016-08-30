package in.entrylog.entrylog.values;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.POSD.controllers.PrinterController;
import com.POSD.util.MachineVersion;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Admin on 29-Aug-16.
 */
public class EL201 {

    PrinterController printerController = null;
    SharedPreferences settings;

    public EL201(PrinterController printerController, SharedPreferences settings) {
        this.printerController = printerController;
        this.settings = settings;
    }

    public void printString(String str) {
        String s1 = null;
        try {
            Log.d("debug", "Send Data Initialzing");
            //Read and Display from text file and print
            File myFile = new File(str);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                Log.d("debug", "OutputStream Started");
                String s = reader.nextLine();
                s1 = s + "\n";
                Log.d("debug", s1);
                printerController.PrinterController_Print(s1.getBytes());
            }
        } catch (IndexOutOfBoundsException e) {

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stringtocode(String value) {
        try {
            printerController.PrinterController_PrinterLanguage(2);
            Bitmap btm = Create2DCode(value);
            if (0 == printerController.Write_Command(decodeBitmap(btm)))
                System.out.println("printerController.Write_Commandok");
            else {
                System.out.println("printerController.Write_Commandno");
            }
            printerController.PrinterController_Linefeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap Create2DCode(String str) throws WriterException {

        BitMatrix matrix = null;
        try {
            if (settings.getString("Scannertype", "").equals("Barcode")) {
                printerController.PrinterController_Linefeed();
                if ("T001(Q)".equals(MachineVersion.getMachineVersion())){
                    System.out.println("508");
                    matrix = new MultiFormatWriter().encode(new String(str.getBytes("GBK"),"ISO-8859-1"),
                            BarcodeFormat.CODE_128, 200, 100);
                }else {
                    System.out.println("762");
                    matrix = new MultiFormatWriter().encode(new String(str.getBytes("GB2312"),"ISO-8859-1"),
                            BarcodeFormat.CODE_128, 200, 100);
                }
            } else {
                if ("T001(Q)".equals(MachineVersion.getMachineVersion())){
                    System.out.println("508");
                    matrix = new MultiFormatWriter().encode(new String(str.getBytes("GBK"),"ISO-8859-1"),
                            BarcodeFormat.QR_CODE, 200, 200);
                }else {
                    System.out.println("762");
                    matrix = new MultiFormatWriter().encode(new String(str.getBytes("GB2312"),"ISO-8859-1"),
                            BarcodeFormat.QR_CODE, 200, 200);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }


        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); // binaryString list
        StringBuffer sb;

        @SuppressWarnings("unused")
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);
        byte[] bytes = hexList2Byte(commandList);
        return bytes;
    }

    private byte[] hexList2Byte(List<String> list) {

        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    private byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    private String hexStr = "0123456789ABCDEF";
    private String[] binaryArray = { "0000", "0001", "0010", "0011", "0100",
            "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100",
            "1101", "1110", "1111" };

    private String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
