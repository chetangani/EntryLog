package in.entrylog.entrylog.main.el201;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.POSD.controllers.PrinterController;
import com.POSD.util.MachineVersion;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorManualCheckout;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.util.ImageProcessing;
import in.entrylog.entrylog.values.DataPrinting;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class Visitor_Details_EL201 extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int END_DLG = 6;
    private PrinterController printerController = null;
    LinearLayout maincontent, checkoutlayout, checkoutuserlayout;
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String Visitor_Name, Visitor_Mobile, Visitor_Fromaddress, Visitor_ToMeet, Visitor_CheckinTime, Visitor_CheckoutTime,
            Visitor_VehicleNo, Visitor_EntryGate, Visitor_Photo, ContextView, Visitor_id, Organization_ID, CheckingUser,
            HeaderPath, DataPath, OrganizationPath, OrganizationName, BarCodeValue, CheckinUser="", CheckoutUser="",
            Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass, Section,
            Student_Name, ID_Card, Email, VehicleNo, BuildManu;
    ImageView Visitor_image;
    TextView tv_name, tv_mobile, tv_address, tv_tomeet, tv_checkintime, tv_checkouttime, tv_vehicleno, tv_entry, tv_exit;
    Button Checkout_btn, Print_btn;
    ConnectingTask task;
    DetailsValue detailsValue;
    ProgressDialog dialog = null;
    Thread checkingoutthread;
    SharedPreferences settings;
    int flag;
    FunctionCalls functionCalls;
    DataPrinting dataPrinting;
    boolean imageprinting = false, barcodeprinting = false;
    PrintingService printingService;
    static ArrayList<String> printingorder, printingdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenSize1 = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        int rotation1 = this.getWindowManager().getDefaultDisplay().getRotation();
        switch(screenSize1) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                setTheme(R.style.AppTheme);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                switch (rotation1) {
                    case Surface.ROTATION_0:
                        setTheme(R.style.MyTheme);
                        break;
                    case Surface.ROTATION_90:
                        setTheme(R.style.AppTheme);
                        break;
                    case Surface.ROTATION_270:
                        setTheme(R.style.AppTheme);
                        break;
                }
                break;
        }
        setContentView(R.layout.activity_visitor_details);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        task = new ConnectingTask();
        detailsValue = new DetailsValue();
        functionCalls = new FunctionCalls();
        dataPrinting = new DataPrinting();

        printingService = new PrintingService();

        functionCalls.OrientationView(Visitor_Details_EL201.this);

        OrganizationPath = functionCalls.filepath("Textfile") + File.separator + "Organization.txt";
        HeaderPath = functionCalls.filepath("Textfile") + File.separator + "Header.txt";
        DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";

        OrganizationName = settings.getString("OrganizationName", "");

        Intent intent = getIntent();
        Bundle bnd = intent.getExtras();
        ContextView = bnd.getString("View");
        Visitor_Photo = bnd.getString("Image");
        Organization_ID = bnd.getString("OrganizationID");
        Visitor_id = bnd.getString("VisitorID");
        Visitor_Name = bnd.getString("Name");
        Visitor_Mobile = bnd.getString("Mobile");
        Visitor_Fromaddress = bnd.getString("From");
        Visitor_ToMeet = bnd.getString("ToMeet");
        Visitor_CheckinTime = bnd.getString("CheckinTime");
        Visitor_CheckoutTime = bnd.getString("CheckoutTime");
        BarCodeValue = bnd.getString("BarCode");
        Visitor_VehicleNo = bnd.getString("VehicleNo");
        Visitor_EntryGate = bnd.getString("Entry");
        CheckingUser = bnd.getString("CheckingUser");
        CheckinUser = bnd.getString("CheckinUser");
        CheckoutUser = bnd.getString("CheckoutUser");
        Email = bnd.getString("Email");
        VehicleNo = bnd.getString("VehicleNo");
        if (CheckoutUser.equals("null")) {
            CheckoutUser = "";
        }
        Visitor_Designation = bnd.getString("visitor_designation");
        Department = bnd.getString("department");
        Purpose = bnd.getString("purpose");
        House_number = bnd.getString("house_number");
        Flat_number = bnd.getString("flat_number");
        Block = bnd.getString("block");
        No_Visitor = bnd.getString("no_visitor");
        aClass = bnd.getString("class");
        Section = bnd.getString("section");
        Student_Name = bnd.getString("student_name");
        ID_Card = bnd.getString("id_card_number");

        checkoutlayout = (LinearLayout) findViewById(R.id.detailscheckoutdatelayout);
        checkoutuserlayout = (LinearLayout) findViewById(R.id.exitgate_layout);

        tv_name = (TextView) findViewById(R.id.visitor_name);
        tv_mobile = (TextView) findViewById(R.id.visitor_mobile);
        tv_address = (TextView) findViewById(R.id.visitor_fromaddress);
        tv_tomeet = (TextView) findViewById(R.id.visitor_tomeet);
        tv_checkintime = (TextView) findViewById(R.id.visitorcheckin_date);
        tv_checkouttime = (TextView) findViewById(R.id.visitorcheckout_date);
        tv_vehicleno = (TextView) findViewById(R.id.visitor_vehicleno);
        tv_entry = (TextView) findViewById(R.id.entry_gate);
        tv_exit = (TextView) findViewById(R.id.exit_gate);

        Checkout_btn = (Button) findViewById(R.id.checkout_btn);
        Print_btn = (Button) findViewById(R.id.detailsprint_btn);

        Visitor_image = (ImageView) findViewById(R.id.visitor_image);

        BuildManu = Build.MANUFACTURER;

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                switch (rotation) {
                    case Surface.ROTATION_0:
                        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_content);
                        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
                        collapsingToolbarLayout.setTitle(Visitor_Name);
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case Surface.ROTATION_90:
                        maincontent = (LinearLayout) findViewById(R.id.main_content);
                        break;
                    case Surface.ROTATION_270:
                        maincontent = (LinearLayout) findViewById(R.id.main_content);
                        break;
                }
                break;
        }

        if (ContextView.equals("Manually Checkout")) {
            Checkout_btn.setVisibility(View.VISIBLE);
            checkoutlayout.setVisibility(View.GONE);
            checkoutuserlayout.setVisibility(View.GONE);
        } else if (ContextView.equals("Visitors")) {
            Print_btn.setVisibility(View.VISIBLE);
            if (Visitor_CheckoutTime.equals("")) {
                Checkout_btn.setVisibility(View.VISIBLE);
            }
            if (BuildManu.equals("LS888")) {
                printerController = PrinterController.getInstance(this);
                flag = printerController.PrinterController_Open();
                if (flag == 0) {
                    Toast.makeText(Visitor_Details_EL201.this, "connect_Success", Toast.LENGTH_SHORT).show();
                } else if (flag == -1){
                    Toast.makeText(Visitor_Details_EL201.this, "Will not Connect to this Device", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Visitor_Details_EL201.this, "connect_Failure", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
        }

        Picasso.with(Visitor_Details_EL201.this).load(Visitor_Photo).error(R.drawable.blankperson).into(Visitor_image);
        tv_name.setText(Visitor_Name);
        tv_mobile.setText(Visitor_Mobile);
        tv_address.setText(Visitor_Fromaddress);
        tv_tomeet.setText(Visitor_ToMeet);
        tv_checkintime.setText(Visitor_CheckinTime);
        tv_checkouttime.setText(Visitor_CheckoutTime);
        tv_vehicleno.setText(Visitor_VehicleNo);
        tv_entry.setText(CheckinUser);
        tv_exit.setText(CheckoutUser);

        Checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitorManualCheckout visitorManualCheckout = task.new VisitorManualCheckout(detailsValue, Organization_ID,
                        Visitor_id, CheckingUser, Visitor_Details_EL201.this);
                dialog = ProgressDialog.show(Visitor_Details_EL201.this, "", "Checking Out Please wait...", true);
                checkingoutthread = null;
                Runnable runnable = new TestCheckOut();
                checkingoutthread = new Thread(runnable);
                checkingoutthread.start();
                visitorManualCheckout.execute();
            }
        });

        Print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildManu.equals("LS888")) {
                    PrintingData();
                } else {
                    showdialog(END_DLG);
                }
            }
        });
    }

    private void PrintingData() {
        try {
            dialog = ProgressDialog.show(Visitor_Details_EL201.this, "", "Printing Data...", true);
            Log.d("debug", "Saving Text");
            dataPrinting.SaveOrganization(OrganizationName);
            dataPrinting.SaveHeader();
            SaveData();
            Log.d("debug", "Printing Header");
            printerController.PrinterController_PrinterLanguage(0);
            printerController.PrinterController_Font_Times();
            printerController.PrinterController_Set_Center();
            printString(OrganizationPath);
            printerController.PrinterController_Font_Normal_mode();
            printString(HeaderPath);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing Image");
                    imageprinting = true;
                    Bitmap actualbitmap = ((BitmapDrawable) Visitor_image.getDrawable()).getBitmap();
                    int width=256;
                    int height=192;
                    Bitmap bitMap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
                    Bitmap bb = ImageProcessing.bitMaptoGrayscale(bitMap);
                    Bitmap bitmap = ImageProcessing.convertGreyImgByFloyd(bb);
                    printerController.PrinterController_Bitmap(bitmap);
                }
            }, 500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing BarCode");
                    if (imageprinting) {
                        imageprinting = false;
                    }
                    barcodeprinting = true;
                    new Thread() {
                        public void run() {
                            stringtocode(BarCodeValue);
                        };
                    }.start();
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing Header");
                    if (barcodeprinting) {
                        barcodeprinting = false;
                    }
                    printerController.PrinterController_PrinterLanguage(0);
                    printerController.PrinterController_Set_Left();
                    printerController.PrinterController_Font_Normal_mode();
                    printString(DataPath);
                    printerController.PrinterController_Take_The_Paper(1);
                }
            }, 3500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    showdialog(END_DLG);
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Visitor_Details_EL201.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void SaveData() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Data.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

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
                        myOutWriter.append(Visitor_Name + "\r\n");
                    }
                    if (Display.equals("Mobile")) {
                        myOutWriter.append(Visitor_Mobile + "\r\n");
                    }
                    if (Display.equals("From")) {
                        myOutWriter.append(Visitor_Fromaddress + "\r\n");
                    }
                    if (Display.equals("To Meet")) {
                        myOutWriter.append(Visitor_ToMeet + "\r\n");
                    }
                    if (Display.equals("Date")) {
                        myOutWriter.append(Visitor_CheckinTime + "\r\n");
                    }
                    if (Display.equals("Visitor Designation")) {
                        myOutWriter.append(Visitor_Designation + "\r\n");
                    }
                    if (Display.equals("Department")) {
                        myOutWriter.append(Department + "\r\n");
                    }
                    if (Display.equals("Purpose")) {
                        myOutWriter.append(Purpose + "\r\n");
                    }
                    if (Display.equals("House No")) {
                        myOutWriter.append(House_number + "\r\n");
                    }
                    if (Display.equals("Flat No")) {
                        myOutWriter.append(Flat_number + "\r\n");
                    }
                    if (Display.equals("Block")) {
                        myOutWriter.append(Block + "\r\n");
                    }
                    if (Display.equals("No of Visitor")) {
                        myOutWriter.append(No_Visitor + "\r\n");
                    }
                    if (Display.equals("Class")) {
                        myOutWriter.append(aClass + "\r\n");
                    }
                    if (Display.equals("Section")) {
                        myOutWriter.append(Section + "\r\n");
                    }
                    if (Display.equals("Student")) {
                        myOutWriter.append(Student_Name + "\r\n");
                    }
                    if (Display.equals("ID Card No")) {
                        myOutWriter.append(ID_Card + "\r\n");
                    }
                    if (Display.equals("Entry")) {
                        myOutWriter.append(CheckinUser + "\r\n");
                    }
                    if (Display.equals("Email")) {
                        myOutWriter.append(Email + "\r\n");
                    }
                    if (Display.equals("Vehicle Number")) {
                        myOutWriter.append(VehicleNo + "\r\n");
                    }
                }
            }
            myOutWriter.append(" " + "\r\n");
            myOutWriter.append(" "+"\r\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    class TestCheckOut implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    dochecking();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void dochecking() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detailsValue.isVisitorsCheckOutSuccess()) {
                        detailsValue.setVisitorsCheckOutSuccess(false);
                        dialog.dismiss();
                        Toast.makeText(Visitor_Details_EL201.this, "Successfully Checked Out", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else if (detailsValue.isVisitorsCheckOutFailure()) {
                        detailsValue.setVisitorsCheckOutFailure(false);
                        dialog.dismiss();
                        Toast.makeText(Visitor_Details_EL201.this, "CheckOut Failed Please try once again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap Create2DCode(String str) throws WriterException {

        BitMatrix matrix = null;
        try {
            if ("T001(Q)".equals(MachineVersion.getMachineVersion())){
                System.out.println("508");
                matrix = new MultiFormatWriter().encode(new String(str.getBytes("GBK"),"ISO-8859-1"),
                        BarcodeFormat.QR_CODE, 200, 200);
            }else {
                System.out.println("762");
                matrix = new MultiFormatWriter().encode(new String(str.getBytes("GB2312"),"ISO-8859-1"),
                        BarcodeFormat.QR_CODE, 200, 200);
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



    private void showdialog(int id) {
        switch (id) {
            case END_DLG:
                AlertDialog.Builder endbuilder = new AlertDialog.Builder(this);
                endbuilder.setTitle("Printing Details");
                endbuilder.setCancelable(false);
                endbuilder.setMessage("Did a Data got a printed correctly...??");
                endbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (BuildManu.equals("LS888")) {
                            PrintingData();
                        } else {
                            showdialog(END_DLG);
                        }
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (ContextView.equals("Visitors")) {
            if (BuildManu.equals("LS888")) {
                printerController.PrinterController_Close();
            }
            functionCalls.deleteTextfile("Organization.txt");
            functionCalls.deleteTextfile("Header.txt");
            functionCalls.deleteTextfile("Empty.txt");
            functionCalls.deleteTextfile("Data.txt");
        }
        super.onDestroy();
    }
}


