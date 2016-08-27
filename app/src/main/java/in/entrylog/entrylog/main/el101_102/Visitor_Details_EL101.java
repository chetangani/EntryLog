package in.entrylog.entrylog.main.el101_102;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.devkit.api.Misc;
import android.devkit.api.SerialPort;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorManualCheckout;
import in.entrylog.entrylog.main.CustomVolleyRequest;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.util.ArrayUtil;
import in.entrylog.entrylog.util.Encoder;
import in.entrylog.entrylog.util.FileUtil;
import in.entrylog.entrylog.util.HexDump;
import in.entrylog.entrylog.util.ImageProcessing;
import in.entrylog.entrylog.util.PrintPic;
import in.entrylog.entrylog.util.Printer;
import in.entrylog.entrylog.values.DataPrinting;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class Visitor_Details_EL101 extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int END_DLG = 6;
    private static boolean printing = false;
    static final int MSG_SUCCESS = 1;
    static final int MSG_FAIL = 2;
    static final int MSG_RECV = 3;
    static final int MSG_DRAW_TXT = 4;
    static final int SendCommand = 5;
    static final int PAPER_TEST = 6;
    static final int FEED = 7;

    private final int WORDNUM = 32;  //limit words in one line
    private final int WIDTH = 384;   //max image width

    String configCom = "/dev/ttyVK1";

    static byte[] recvBuf;

    long begin;

    SerialPort mSerialPort;

    static int recStatus = -1;

    static int printerStatus = 0;

    static Handler timehandler = new Handler();

    Runnable timerunnable = new Runnable() {

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
                    String temp = HexDump.dumpHex((byte[]) msg.obj);
                    if (temp.equals("0x20")) {
                        printerStatus = -1;
                    }
                    if (temp.equals("0x00")) {
                        printerStatus = 0;
                    }
                    if (recStatus == 1) {
                        Toast.makeText(Visitor_Details_EL101.this, "Ver: " + temp, Toast.LENGTH_SHORT).show();
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

    LinearLayout maincontent, checkoutlayout, checkoutuserlayout, emailayout, designationlayout, departmentlayout,
            purposelayout, housenolayout, flatnolayout, blocklayout, noofvisitorlayout, classlayout, sectionlayout,
            studentnamelayout, idcardlayout;
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String Visitor_Name, Visitor_Mobile, Visitor_Fromaddress, Visitor_ToMeet, Visitor_CheckinTime, Visitor_CheckoutTime,
            Visitor_VehicleNo, Visitor_EntryGate, Visitor_Photo, ContextView, Visitor_id, Organization_ID, CheckingUser,
            HeaderPath, DataPath, OrganizationPath, EmptyPath, OrganizationName, BarCodeValue, CheckinUser="", CheckoutUser="",
            Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass, Section,
            Student_Name, ID_Card, Email, Vehicleno;
    NetworkImageView Visitor_image;
    ImageLoader imageLoader;
    TextView tv_name, tv_mobile, tv_address, tv_tomeet, tv_checkintime, tv_checkouttime, tv_vehicleno, tv_entry, tv_exit,
            tv_email, tv_designation, tv_department, tv_purpose, tv_houseno, tv_flatno, tv_block, tv_noofvisitor, tv_class,
            tv_section, tv_studentname, tv_idcardno;
    Button Checkout_btn, Print_btn;
    ConnectingTask task;
    DetailsValue detailsValue;
    FunctionCalls functionCalls;
    DataPrinting dataPrinting;
    static ProgressDialog dialog = null;
    Thread checkingoutthread;
    boolean imageprinting = false, barcodeprinting = false;
    SharedPreferences settings;
    PrintingService printingService;
    FieldsService fieldsService;
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

        task = new ConnectingTask();
        detailsValue = new DetailsValue();
        functionCalls = new FunctionCalls();
        dataPrinting = new DataPrinting();

        fieldsService = new FieldsService();
        printingService = new PrintingService();

        functionCalls.OrientationView(Visitor_Details_EL101.this);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle bnd = intent.getExtras();
        //region Intent Values
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
        Vehicleno = bnd.getString("VehicleNo");
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
        //endregion

        //region Linear Layout Initialization
        checkoutlayout = (LinearLayout) findViewById(R.id.detailscheckoutdatelayout);
        checkoutuserlayout = (LinearLayout) findViewById(R.id.exitgate_layout);
        emailayout = (LinearLayout) findViewById(R.id.detailsemaillayout);
        designationlayout = (LinearLayout) findViewById(R.id.detailsvisitordesignation_layout);
        departmentlayout = (LinearLayout) findViewById(R.id.detailsdepartmentlayout);
        purposelayout = (LinearLayout) findViewById(R.id.detailspurposelayout);
        housenolayout = (LinearLayout) findViewById(R.id.detailshousenolayout);
        flatnolayout = (LinearLayout) findViewById(R.id.detailsflatnolayout);
        blocklayout = (LinearLayout) findViewById(R.id.detailsblocklayout);
        noofvisitorlayout = (LinearLayout) findViewById(R.id.detailsnoofvisitorlayout);
        classlayout = (LinearLayout) findViewById(R.id.detailsclasslayout);
        sectionlayout = (LinearLayout) findViewById(R.id.detailssectionlayout);
        studentnamelayout = (LinearLayout) findViewById(R.id.detailsstudentnamelayout);
        idcardlayout = (LinearLayout) findViewById(R.id.detailsidcardnolayout);
        //endregion

        //region TextView Initialization
        tv_name = (TextView) findViewById(R.id.visitor_name);
        tv_mobile = (TextView) findViewById(R.id.visitor_mobile);
        tv_address = (TextView) findViewById(R.id.visitor_fromaddress);
        tv_tomeet = (TextView) findViewById(R.id.visitor_tomeet);
        tv_checkintime = (TextView) findViewById(R.id.visitorcheckin_date);
        tv_checkouttime = (TextView) findViewById(R.id.visitorcheckout_date);
        tv_vehicleno = (TextView) findViewById(R.id.visitor_vehicleno);
        tv_entry = (TextView) findViewById(R.id.entry_gate);
        tv_exit = (TextView) findViewById(R.id.exit_gate);
        tv_email = (TextView) findViewById(R.id.visitor_email);
        tv_designation = (TextView) findViewById(R.id.visitor_designation);
        tv_department = (TextView) findViewById(R.id.visitor_department);
        tv_purpose = (TextView) findViewById(R.id.visitor_purpose);
        tv_houseno = (TextView) findViewById(R.id.visitor_houseno);
        tv_flatno = (TextView) findViewById(R.id.visitor_flatno);
        tv_block = (TextView) findViewById(R.id.visitor_block);
        tv_noofvisitor = (TextView) findViewById(R.id.noofvisitor);
        tv_class = (TextView) findViewById(R.id.visitor_class);
        tv_section = (TextView) findViewById(R.id.visitor_section);
        tv_studentname = (TextView) findViewById(R.id.visitor_section);
        tv_idcardno = (TextView) findViewById(R.id.visitor_idcardno);
        //endregion

        Checkout_btn = (Button) findViewById(R.id.checkout_btn);
        Print_btn = (Button) findViewById(R.id.detailsprint_btn);

        Visitor_image = (NetworkImageView) findViewById(R.id.visitor_image);
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();

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
        } else if (ContextView.equals("Visitors")) {
            Print_btn.setVisibility(View.VISIBLE);
            if (Visitor_CheckoutTime.equals("")) {
                Checkout_btn.setVisibility(View.VISIBLE);
            }
            LogStatus("Enabling Printer");
            EnableBtn(true);
        } else {
        }

        OrganizationPath = functionCalls.filepath("Textfile") + File.separator + "Organization.txt";
        HeaderPath = functionCalls.filepath("Textfile") + File.separator + "Header.txt";
        DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";
        EmptyPath = functionCalls.filepath("Textfile") + File.separator + "Empty.txt";

        OrganizationName = settings.getString("OrganizationName", "");

        /*Picasso.with(Visitor_Details_Bluetooth.this).load(Visitor_Photo).error(R.drawable.blankperson).into(Visitor_image);*/
        imageLoader.get(Visitor_Photo, ImageLoader.getImageListener(Visitor_image, R.drawable.blankperson,
                R.drawable.blankperson));
        Visitor_image.setImageUrl(Visitor_Photo, imageLoader);
        tv_name.setText(Visitor_Name);
        tv_mobile.setText(Visitor_Mobile);
        tv_address.setText(Visitor_Fromaddress);
        tv_tomeet.setText(Visitor_ToMeet);
        tv_checkintime.setText(Visitor_CheckinTime);
        tv_checkouttime.setText(Visitor_CheckoutTime);
        tv_vehicleno.setText(Visitor_VehicleNo);
        tv_entry.setText(CheckinUser);
        tv_exit.setText(CheckoutUser);
        DisplayFields();

        Checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitorManualCheckout visitorManualCheckout = task.new VisitorManualCheckout(detailsValue, Organization_ID,
                        Visitor_id, CheckingUser, Visitor_Details_EL101.this);
                dialog = ProgressDialog.show(Visitor_Details_EL101.this, "", "Checking Out Please wait...", true);
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
                PrintingData();
            }
        });
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
                        Toast.makeText(Visitor_Details_EL101.this, "Successfully Checked Out", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else if (detailsValue.isVisitorsCheckOutFailure()) {
                        detailsValue.setVisitorsCheckOutFailure(false);
                        dialog.dismiss();
                        Toast.makeText(Visitor_Details_EL101.this, "CheckOut Failed Please try once again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void SendCommad(byte[] order) {
        if (mSerialPort != null) {
            try {
                if (imageprinting) {
                    if (!(order.length == 3)) {
                        recvBuf = new byte[0];
                        mSerialPort.getOutputStream().write(order);
                        LogStatus("SendDataSize: " + order.length);
                        if (order.length < 200) {
                            // too much message show will affect the UI
                            LogStatus("Send: " + HexDump.dumpHex(order));
                        } else {
                            LogStatus("SendImageSize: " + order.length);
                        }
                    }
                } else {
                    recvBuf = new byte[0];
                    mSerialPort.getOutputStream().write(order);
                    LogStatus("SendDataSize: " + order.length);
                    if (order.length < 200) {
                        // too much message show will affect the UI
                        LogStatus("Send: " + HexDump.dumpHex(order));
                    } else {
                        LogStatus("SendImageSize: " + order.length);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogStatus("Port IO Err: " + e.toString());
            }

        } else {
            LogStatus("Send Err: " + HexDump.dumpHex(order));
        }
    }

    private void LogStatus(String str) {
        Log.d("debug", str);
    }

    private void EnableBtn(boolean enabled) {
        Misc.printerEnable(enabled);
        if (enabled) {
            try {
                recvBuf = new byte[0];
                mSerialPort = new SerialPort(configCom, 115200, 0);
                if (mSerialPort != null)
                    new readThread().start();
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(Visitor_Details_EL101.this, "Sorry this Device will not connect to EL 101/102.. " +
                        "Try for other device", Toast.LENGTH_SHORT).show();
                /*finish();*/
            } catch (IOException e) {
                e.printStackTrace();
                /*Toast.makeText(AddVisitors_EL101.this, "Port Err: " + configCom + " " + e.toString(), Toast.LENGTH_SHORT).show();*/
            }
        }
    }

    class readThread extends Thread {
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

    private void PrintingData() {
        dialog = ProgressDialog.show(Visitor_Details_EL101.this, "", "Printing Data...", true);
        Log.d("debug", "Saving Text");
        /*dataPrinting.SaveOrganization(OrganizationName);
        dataPrinting.SaveHeader();
        SaveData();
        dataPrinting.SaveEmpty();*/
        Log.d("debug", "Printing Header");
        SendCommad(new byte[]{0x1d, 0x21, 0x01});
        SendCommad(new byte[]{0x1b, 0x61, 0x01});
        /*printString(OrganizationPath);*/
        printString(OrganizationName/*+"\n"*/);
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        /*printString(HeaderPath);*/
        printString("VISITOR"/*+"\n"*/);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing Image");
                imageprinting = true;
                /*PrintImage2(Visitor_image);*/
                PrintHumanImage(Visitor_image);
            }
        }, 1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing BarCode");
                if (imageprinting) {
                    imageprinting = false;
                }
                /*printString(EmptyPath);*/
                if (settings.getString("Scannertype", "").equals("Barcode")) {
                    printString("    "+"\n");
                    barcodeprinting = true;
                    printBarCode(BarCodeValue);
                } else {
                    printQRcode(195, BarCodeValue);
                }
            }
        }, 4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing Header");
                if (barcodeprinting) {
                    barcodeprinting = false;
                    printString("    "+"\n");
                }
                /*printString(EmptyPath);*/
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                /*printString(DataPath);*/
                SaveData();
                /*printString(EmptyPath);
                printString(EmptyPath);*/
                printString("    "+"\n");
                printString("    "+"\n");
            }
        }, 6000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                showdialog(END_DLG);
            }
        }, 7500);
    }

    public void SaveData() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Data.txt";
        try {
            /*File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);*/

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
                    /*myOutWriter.append(Display+": ");
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
                        *//*printString(Display+": "+Email+"\n");*//*
                    }
                    if (Display.equals("Vehicle Number")) {
                        myOutWriter.append(Vehicleno + "\r\n");
                        *//*printString(Display+": "+Vehicleno+"\n");*//*
                    }*/
                    /*printString(Display+": ");*/
                    if (Display.equals("Name")) {
                        /*myOutWriter.append(Visitor_Name + "\r\n");*/
                        printString(Display+": "+Visitor_Name/*+"\n"*/);
                    }
                    if (Display.equals("Mobile")) {
                        /*myOutWriter.append(Visitor_Mobile + "\r\n");*/
                        printString(Display+": "+Visitor_Mobile/*+"\n"*/);
                    }
                    if (Display.equals("From")) {
                        /*myOutWriter.append(Visitor_Fromaddress + "\r\n");*/
                        printString(Display+": "+Visitor_Fromaddress/*+"\n"*/);
                    }
                    if (Display.equals("To Meet")) {
                        /*myOutWriter.append(Visitor_ToMeet + "\r\n");*/
                        printString(Display+": "+Visitor_ToMeet/*+"\n"*/);
                    }
                    if (Display.equals("Date")) {
                        /*myOutWriter.append(Visitor_CheckinTime);*/
                        printString(Display+": "+Visitor_CheckinTime/*+"\n"*/);
                    }
                    if (Display.equals("Visitor Designation")) {
                        /*myOutWriter.append(Visitor_Designation + "\r\n");*/
                        printString(Display+": "+Visitor_Designation/*+"\n"*/);
                    }
                    if (Display.equals("Department")) {
                        /*myOutWriter.append(Department + "\r\n");*/
                        printString(Display+": "+Department/*+"\n"*/);
                    }
                    if (Display.equals("Purpose")) {
                        /*myOutWriter.append(Purpose + "\r\n");*/
                        printString(Display+": "+Purpose/*+"\n"*/);
                    }
                    if (Display.equals("House No")) {
                        /*myOutWriter.append(House_number + "\r\n");*/
                        printString(Display+": "+House_number/*+"\n"*/);
                    }
                    if (Display.equals("Flat No")) {
                        /*myOutWriter.append(Flat_number + "\r\n");*/
                        printString(Display+": "+Flat_number/*+"\n"*/);
                    }
                    if (Display.equals("Block")) {
                        /*myOutWriter.append(Block + "\r\n");*/
                        printString(Display+": "+Block/*+"\n"*/);
                    }
                    if (Display.equals("No of Visitor")) {
                        /*myOutWriter.append(No_Visitor + "\r\n");*/
                        printString(Display+": "+No_Visitor/*+"\n"*/);
                    }
                    if (Display.equals("Class")) {
                        /*myOutWriter.append(aClass + "\r\n");*/
                        printString(Display+": "+aClass/*+"\n"*/);
                    }
                    if (Display.equals("Section")) {
                        /*myOutWriter.append(Section + "\r\n");*/
                        printString(Display+": "+Section/*+"\n"*/);
                    }
                    if (Display.equals("Student")) {
                        /*myOutWriter.append(Student_Name + "\r\n");*/
                        printString(Display+": "+Student_Name/*+"\n"*/);
                    }
                    if (Display.equals("Id Card")) {
                        /*myOutWriter.append(ID_Card + "\r\n");*/
                        printString(Display+": "+ID_Card/*+"\n"*/);
                    }
                    if (Display.equals("Entry")) {
                        /*myOutWriter.append(CheckinUser + "\r\n");*/
                        printString(Display+": "+CheckinUser/*+"\n"*/);
                    }
                    if (Display.equals("Email")) {
                        /*myOutWriter.append(Email + "\r\n");*/
                        printString(Display+": "+Email/*+"\n"*/);
                    }
                    if (Display.equals("Vehicle Number")) {
                        /*myOutWriter.append(Vehicleno + "\r\n");*/
                        printString(Display+": "+Vehicleno/*+"\n"*/);
                    }
                }
            }
            /*myOutWriter.append(" " + "\r\n");
            myOutWriter.close();
            fOut.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printString(String str) {
        if((str!=null)&&(str.getBytes().length!=0)){
            byte[] send = null;
            try {
                //send = addEnter(str.getBytes("ISO8859-16"));
                //打印包含中文字符的文字 只能使用GBK2312，其他外语特殊字符用UTF-8 ，测试匈牙利语需要用iso859-16
                //If want to print Chinese character use "GBK2312", others use "UTF-8";
                send = addEnter(str.getBytes("GB2312"));
                //send = str.getBytes("utf-8");
            }catch (Exception e) {
                e.printStackTrace();
            }
            SendCommad(send);
            //Message msg=Message.obtain();
            //msg.what=SendCommand;
            //msg.obj=send;
            //mHandler.sendMessageDelayed(msg,450);
        }
        /*String s1 = null;
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
                if ((s1 != null) && (s1.getBytes().length != 0)) {
                    byte[] send = null;
                    try {
                        send = addEnter(s1.getBytes("GB2312"));
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
        }*/
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

    public String PrintImage2(ImageView imageview) {
        Bitmap actualbitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        int width=256;
        int height=192;
        Bitmap bitMap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
        Bitmap bb = ImageProcessing.bitMaptoGrayscale(bitMap);
        int width1 = bb.getWidth();
        int height1 = bb.getHeight();
        LogStatus("width1: "+width1);
        LogStatus("height1: "+height1);
        saveBitmap(bb);
        Bitmap bitmap = ImageProcessing.convertGreyImgByFloyd(bb);
        saveBitmap(bitmap);
        String set = "";

        if (printerStatus == -1) {
            System.out.println("no paper");
            return "false";
        }

        PrintPic localPrintPic = new PrintPic();

        localPrintPic.initCanvas(bitmap.getWidth(), bitmap.getHeight());
        localPrintPic.initPaint();
        localPrintPic.drawImage(0.0F, 0.0F, bitmap);
        byte[] var4 = localPrintPic.printDraw();
        byte[] var2 = new byte[1160];

        int i = 0;
        if (localPrintPic.getLength() <= 0)
            return "";
        int index = 0;
        byte[] sendbytesNew = null;
        int var1 = 0;
        int var13 = 0;

        int length = localPrintPic.getLength() % 24 > 0 ? localPrintPic.getLength() / 24 + 1 : localPrintPic.getLength() / 24;

        begin = System.currentTimeMillis();
        System.out.println("begin:" + begin);
        for (int row = 0; row < length; row++) {
            if (printerStatus == -1) {
                System.out.println("no paper");
                return "false";
            }
            index = 0;
            var2[0] = 0x1d;
            var2[1] = 0x76;
            var2[2] = 0x30;
            var2[3] = 0;
            var2[4] = (byte) (localPrintPic.getWidth() / 8);
            var2[5] = 0;
            int line = 0;
            if (localPrintPic.getLength() % 24 > 0 && row == length - 1) {
                line = localPrintPic.getLength() % 24;
            } else {
                line = 24;
            }
            var2[6] = (byte) line;
            var2[7] = 0;
            var13 = 8;
            for (int var14 = 0; var14 < ((localPrintPic.getWidth() / 8) * line); var14++) {

                var2[var13] = var4[var1];
                var13 = var13 + 1;
                var1 = var1 + 1;
                index++;
            }
            sendbytesNew = new byte[8 + (localPrintPic.getWidth() / 8 * line)];
            for (i = 0; i < sendbytesNew.length; i++) {
                sendbytesNew[i] = var2[i];
            }
            //System.out.println("row:"+row+" send:"+HexDump.dumpHexString(sendbytesNew));
            LogStatus("row: "+row+"\n"+"send: "+HexDump.dumpHexString(sendbytesNew)+"\n"+"length: "+length);
            Message msg = Message.obtain();
            msg.what = SendCommand;
            msg.obj = sendbytesNew;
            /*Log.d("debug", "Message result: "+HexDump.dumpHex((byte[]) msg.obj));
            *//*String temp = HexDump.dumpHex((byte[]) msg.obj);*/
            mHandler.sendMessageDelayed(msg, 180 * row);
        }
        //feed paper
        LogStatus("Feeding Starting");
        mHandler.sendEmptyMessageDelayed(FEED, length * 180);
        LogStatus("Feeding completed");
        // test no paper
        LogStatus("Paper test Starting");
        mHandler.sendEmptyMessageDelayed(PAPER_TEST, length * 180 + 180);
        LogStatus("Paper test completed");
        return set;
    }

    public String PrintHumanImage(ImageView imageview){
        Bitmap actualbitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        int width=256;
        int height=192;
        Bitmap bitmap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
		/*Bitmap bb = ImageProcessing.bitMaptoGrayscale(bitMap);
		int width1 = bb.getWidth();
		int height1 = bb.getHeight();
		Log.d("debug", "width1: "+width1);
		Log.d("debug", "height1: "+height1);
		saveBitmap(bb);
		Bitmap bitmap = ImageProcessing.convertGreyImgByFloyd(bb);
		saveBitmap(bitmap);*/
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

        // test no paper
        mHandler.sendEmptyMessageDelayed(PAPER_TEST,1000+180);

        return set;
    }

    public void saveBitmap(Bitmap mBitmap) {

        Long fileName=System.currentTimeMillis();

        File folder = new File(Environment.getExternalStorageDirectory().getPath() +"/QRcode");
        if(!folder.exists()){
            boolean falg= FileUtil.createDirectory("QRcode");
            Log.d("debug", "createfolder:"+falg);
        }
        File f=new File(Environment.getExternalStorageDirectory().getPath() +"/QRcode/"+fileName+".png");
        FileUtil.createFile(folder.toString(), fileName+".png");

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printBarCode(String str) {
        //set barCode height   area 1-255  default 162
    	/*byte[] barHeight=new byte[]{0x1d,0x68,162-256};
    	SendCommad(barHeight);*/

    	 /*
    	 //set barCode width area  2-6  default 3
    	 byte[] barWidth=new byte[]{0x1d,0x77,0x02};
    	 SendCommad(barWidth);
    	 */
        //set HRI character postion
        //0:no  1:up   2:below    3:both up and below
        byte[] hri = new byte[]{0x1d, 0x48, 0x00};
        SendCommad(hri);
        //set align center
        SendCommad(new byte[]{0x1b, 0x61, 0x01});
        byte[] head = new byte[]{0x1d, 0x6b, 0x48, (byte) str.length()};

        byte[] body = ArrayUtil.stringToBytes(str, str.length());
        byte[] total = ArrayUtil.MergerArray(head, body);
        SendCommad(total);
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
        byte[] var2 = new byte[1160];// 宽度不能超过48个字节，也就是384的像素    一次发送24行，一行最大48个字节

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
            var2[4] = (byte)(localPrintPic.getWidth() / 8);  //xl  图片宽度字节 低位
            var2[5] = 0;    //xh  图片宽度字节 高位
            int line=0;
            if(localPrintPic.getLength()%24>0&&row==length-1){
                line=localPrintPic.getLength()%24;
            }else{
                line=24;
            }
            var2[6] = (byte)line;    //yl  图片高度点数  低位
            var2[7] = 0;   //yh	   图片高度点数  高位
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
                        PrintingData();
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;
        }
    }

    private void DisplayFields() {
        functionCalls.LogStatus("Display field Started");
        HashSet<String> hashSet = new HashSet<>();
        hashSet = fieldsService.fieldset;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(hashSet);
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                String value = arrayList.get(i).toString();
                if (value.equals("Email")) {
                    emailayout.setVisibility(View.VISIBLE);
                    tv_email.setText(Email);
                }
                if (value.equals("Visitor Designation")) {
                    designationlayout.setVisibility(View.VISIBLE);
                    tv_designation.setText(Visitor_Designation);
                }
                if (value.equals("Department")) {
                    departmentlayout.setVisibility(View.VISIBLE);
                    tv_department.setText(Department);
                }
                if (value.equals("Purpose")) {
                    purposelayout.setVisibility(View.VISIBLE);
                    tv_purpose.setText(Purpose);
                }
                if (value.equals("House No")) {
                    housenolayout.setVisibility(View.VISIBLE);
                    tv_houseno.setText(House_number);
                }
                if (value.equals("Flat No")) {
                    flatnolayout.setVisibility(View.VISIBLE);
                    tv_flatno.setText(Flat_number);
                }
                if (value.equals("Block")) {
                    blocklayout.setVisibility(View.VISIBLE);
                    tv_block.setText(Block);
                }
                if (value.equals("No of Visitor")) {
                    noofvisitorlayout.setVisibility(View.VISIBLE);
                    tv_noofvisitor.setText(No_Visitor);
                }
                if (value.equals("Class")) {
                    classlayout.setVisibility(View.VISIBLE);
                    tv_class.setText(aClass);
                }
                if (value.equals("Section")) {
                    sectionlayout.setVisibility(View.VISIBLE);
                    tv_section.setText(Section);
                }
                if (value.equals("Student Name")) {
                    studentnamelayout.setVisibility(View.VISIBLE);
                    tv_studentname.setText(Student_Name);
                }
                if (value.equals("ID Card No")) {
                    idcardlayout.setVisibility(View.VISIBLE);
                    tv_idcardno.setText(ID_Card);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        timehandler.removeCallbacks(timerunnable);
        if (mSerialPort != null)
            mSerialPort.close();
        mSerialPort = null;
        if (ContextView.equals("Visitors")) {
            EnableBtn(false);
            functionCalls.deleteTextfile("Organization.txt");
            functionCalls.deleteTextfile("Header.txt");
            functionCalls.deleteTextfile("Empty.txt");
            functionCalls.deleteTextfile("Data.txt");
        }
        super.onDestroy();
    }
}
