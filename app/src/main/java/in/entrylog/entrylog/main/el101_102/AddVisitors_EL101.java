package in.entrylog.entrylog.main.el101_102;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.devkit.api.Misc;
import android.devkit.api.SerialPort;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.database.DataBase;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.MobileAutoSuggest;
import in.entrylog.entrylog.dataposting.ConnectingTask.SMSOTP;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.main.services.StaffService;
import in.entrylog.entrylog.main.services.Updatedata;
import in.entrylog.entrylog.myprinter.Global;
import in.entrylog.entrylog.myprinter.WorkService;
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

public class AddVisitors_EL101 extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int START_DLG = 5;
    private static final int END_DLG = 6;
    private static final int MOBILE_DLG = 7;
    private static final int OTP_DLG = 8;
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
                    String temp1 = bytetoASCIIString((byte[]) msg.obj);
                    String temp = HexDump.dumpHex((byte[]) msg.obj);
                    LogStatus("Recv: " + temp);
                    LogStatus("Recv1: " + temp1);
                    if (temp.equals("0x20")) {
                        printerStatus = -1;
                        LogStatus("Recv: No Paper");
                    }
                    if (temp.equals("0x00")) {
                        printerStatus = 0;
                        LogStatus("Recv: OK");
                    }
                    if (recStatus == 1) {
                        Toast.makeText(AddVisitors_EL101.this, "Ver: " + temp, Toast.LENGTH_SHORT).show();
                        recStatus = -1;
                    }
                    recvBuf = new byte[0];
                    break;
                case MSG_DRAW_TXT:
                    LogStatus((String) msg.obj);
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

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;
    /*private static Handler mHandler = null;*/

    EditText name_et, email_et, mobile_et, address_et, vehicle_et;
    AutoCompleteTextView tomeet_et;
    ImageView photo_img;
    LinearLayout addvisitorslayout;
    Button submit_btn;
    String Name, Email="", FromAddress, ToMeet, Vehicleno = "", Organizationid, OrganizationName, UpdateVisitorImage="",
            Visitors_ImagefileName = "", GuardID, User, HeaderPath, DataPath, OrganizationPath, EmptyPath, DateTime="",
            BarCodeValue="", format, Visitor_Designation="", Department="", Purpose="", House_number="", Flat_number="",
            Block="", No_Visitor="", aClass="", Section="", Student_Name="", ID_Card="", Visitor_Entry="";
    int codevalue, digits;
    static String Mobile = "", Visitors_id;
    ConnectingTask task;
    DetailsValue details;
    ArrayList<DetailsValue> fieldvalues;
    Thread mythread, mobilesuggestthread;
    static ProgressDialog dialog = null;
    boolean Visitorsimage = false, textfileready = false, imageprinting = false, barcodeprinting = false, reprint = false,
            otpcheck = false, manualcheck = false, otpresent = false, mobilesuggestsuccess = false;
    static boolean completed = false;
    View mProgressBar;
    DataBase dataBase;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    FunctionCalls functionCalls;
    DataPrinting dataPrinting;
    FieldsService fieldsService;
    StaffService staffService;
    PrintingService printingService;
    TextInputLayout Til_field1, Til_field2, Til_field3, Til_field4, Til_field5, Til_field6, Til_field7, Til_field8,
            Til_field9, Til_field10, Til_field11, emailLayout;
    EditText Et_field1, Et_field2, Et_field3, Et_field4, Et_field5, Et_field6, Et_field7, Et_field8, Et_field9,
            Et_field10, Et_field11, etmobile;
    ArrayAdapter<String> Staffadapter;
    static ArrayList<String> stafflist, printingorder, printingdisplay;
    int otpcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_entrylog_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_add_visitors);

        details = new DetailsValue();
        task = new ConnectingTask();
        functionCalls = new FunctionCalls();
        dataPrinting = new DataPrinting();

        fieldsService = new FieldsService();
        staffService = new StaffService();
        printingService = new PrintingService();

        dataBase = new DataBase(this);
        dataBase.open();

        digits = 4;
        format = String.format("%%0%dd", digits);

        functionCalls.OrientationView(AddVisitors_EL101.this);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        LogStatus("Enabling Printer");
        EnableBtn(true);

        name_et = (EditText) findViewById(R.id.name_EtTxt);
        email_et = (EditText) findViewById(R.id.email_EtTxt);
        mobile_et = (EditText) findViewById(R.id.mobile_EtTxt);
        address_et = (EditText) findViewById(R.id.address_EtTxt);
        tomeet_et = (AutoCompleteTextView) findViewById(R.id.tomeet_EtTxt);
        vehicle_et = (EditText) findViewById(R.id.vehicle_EtTxt);
        photo_img = (ImageView) findViewById(R.id.cameraimage);
        if (settings.getString("ImageAccess","").equals("No")) {
            photo_img.setVisibility(View.GONE);
        }
        submit_btn = (Button) findViewById(R.id.submit_btn);

        emailLayout = (TextInputLayout) findViewById(R.id.email_Til);
        Til_field1 = (TextInputLayout) findViewById(R.id.field1_Til);
        Til_field2 = (TextInputLayout) findViewById(R.id.field2_Til);
        Til_field3 = (TextInputLayout) findViewById(R.id.field3_Til);
        Til_field4 = (TextInputLayout) findViewById(R.id.field4_Til);
        Til_field5 = (TextInputLayout) findViewById(R.id.field5_Til);
        Til_field6 = (TextInputLayout) findViewById(R.id.field6_Til);
        Til_field7 = (TextInputLayout) findViewById(R.id.field7_Til);
        Til_field8 = (TextInputLayout) findViewById(R.id.field8_Til);
        Til_field9 = (TextInputLayout) findViewById(R.id.field9_Til);
        Til_field10 = (TextInputLayout) findViewById(R.id.field10_Til);
        Til_field11 = (TextInputLayout) findViewById(R.id.field11_Til);

        Et_field1 = (EditText) findViewById(R.id.field1_EtTxt);
        Et_field2 = (EditText) findViewById(R.id.field2_EtTxt);
        Et_field3 = (EditText) findViewById(R.id.field3_EtTxt);
        Et_field4 = (EditText) findViewById(R.id.field4_EtTxt);
        Et_field5 = (EditText) findViewById(R.id.field5_EtTxt);
        Et_field6 = (EditText) findViewById(R.id.field6_EtTxt);
        Et_field7 = (EditText) findViewById(R.id.field7_EtTxt);
        Et_field8 = (EditText) findViewById(R.id.field8_EtTxt);
        Et_field9 = (EditText) findViewById(R.id.field9_EtTxt);
        Et_field10 = (EditText) findViewById(R.id.field10_EtTxt);
        Et_field11 = (EditText) findViewById(R.id.field11_EtTxt);

        addvisitorslayout = (LinearLayout) findViewById(R.id.addvisitors_layout);

        mProgressBar = findViewById(R.id.addvisitors_progress);

        MobileNoSuggestThread();

        Organizationid = settings.getString("OrganizationID", "");
        GuardID = settings.getString("GuardID", "");
        OrganizationName = settings.getString("OrganizationName", "");
        User = settings.getString("User", "");

        OrganizationPath = functionCalls.filepath("Textfile") + File.separator + "Organization.txt";
        HeaderPath = functionCalls.filepath("Textfile") + File.separator + "Header.txt";
        DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";
        EmptyPath = functionCalls.filepath("Textfile") + File.separator + "Empty.txt";

        if (settings.getString("BarCode", "").equals("")) {
            codevalue = 1;
            String value = String.format(format, codevalue);
            BarCodeValue = value + Organizationid;
            editor.putString("BarCode", BarCodeValue);
            editor.commit();
        } else {
            String code = settings.getString("BarCode", "");
            String barvalue = code.substring(0, 4);
            codevalue = Integer.parseInt(barvalue);
            if (codevalue == 9999) {
                codevalue = 1;
                String value = String.format(format, codevalue);
                BarCodeValue = value + Organizationid;
                editor.putString("BarCode", BarCodeValue);
                editor.commit();
            } else {
                codevalue = codevalue + 1;
                String value = String.format(format, codevalue);
                BarCodeValue = value + Organizationid;
                editor.putString("BarCode", BarCodeValue);
                editor.commit();
            }
        }

        showdialog(START_DLG);

        photo_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Checking camera availability
                if (!isDeviceSupportCamera()) {
                    Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
                    // will close the app if the device does't have camera
                    finish();
                } else {
                    // capture picture
                    captureImage();
                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.getString("ImageAccess","").equals("Yes")) {
                    if (Visitorsimage) {
                        CheckInDetails();
                    } else {
                        Toast.makeText(AddVisitors_EL101.this, "Please take Visitors Photo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    CheckInDetails();
                }
            }
        });

        tomeet_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    vehicle_et.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        fieldvalues = new ArrayList<DetailsValue>();
        super.onResume();
    }

    private void CheckInDetails() {
        Name = name_et.getText().toString();
        if (!name_et.getText().toString().equals("")) {
            Email = email_et.getText().toString();
            Mobile = mobile_et.getText().toString();
            if (!mobile_et.getText().toString().equals("")) {
                if (Mobile.length() == 10) {
                    FromAddress = address_et.getText().toString();
                    if (!address_et.getText().toString().equals("")) {
                        ToMeet = tomeet_et.getText().toString();
                        if (!tomeet_et.getText().toString().equals("")) {
                            Vehicleno = vehicle_et.getText().toString();
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            Visitors_ImagefileName = Mobile+""+num + ".jpg";
                            if (!Visitors_ImagefileName.equals("")) {
                                getExtraFields();
                                if (UpdateVisitorImage.equals("Yes")) {
                                    dataBase.insertentrylogdata(Name, Email, Mobile, FromAddress, ToMeet, Vehicleno,
                                            Visitors_ImagefileName, fileUri.getPath(), BarCodeValue, Organizationid, GuardID,
                                            UpdateVisitorImage, Visitor_Designation, Department, Purpose, House_number,
                                            Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card,
                                            settings.getString("Device", ""), Visitor_Entry);
                                } else {
                                    dataBase.insertentrylogdata(Name, Email, Mobile, FromAddress, ToMeet, Vehicleno,
                                            "", "", BarCodeValue, Organizationid, GuardID,
                                            UpdateVisitorImage, Visitor_Designation, Department, Purpose, House_number,
                                            Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card,
                                            settings.getString("Device", ""), Visitor_Entry);
                                }/*
                                dataPrinting.SaveOrganization(OrganizationName);
                                dataPrinting.SaveHeader();
                                PrintData();
                                dataPrinting.SaveEmpty();*/
                                functionCalls.LogStatus("Update Data Service: "+settings.getString("UpdateData", ""));
                                if (!settings.getString("UpdateData", "").equals("Running")) {
                                    Log.d("debug", "Service Started");
                                    Intent intent = new Intent(AddVisitors_EL101.this, Updatedata.class);
                                    startService(intent);
                                }
                                PrintingData();
                            } else {
                                Toast.makeText(AddVisitors_EL101.this, "Please take a Photo of Visitor", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            tomeet_et.setError("Please Enter To Meet Person");
                        }
                    } else {
                        address_et.setError("Please Enter From Address");
                    }
                } else {
                    mobile_et.setError("Please Enter Correct Mobile Number");
                }
            } else {
                mobile_et.setError("Please Enter Mobile Number");
            }
        } else {
            name_et.setError("Please Enter Name");
        }
    }

    private void PrintingData() {
        dialog = ProgressDialog.show(AddVisitors_EL101.this, "", "Updating file...", true);
        Log.d("debug", "Saving Text");
        /*dataPrinting.SaveOrganization(OrganizationName);
        dataPrinting.SaveHeader();
        SaveData();
        dataPrinting.SaveEmpty();*/
        Log.d("debug", "Printing Header");
        SendCommad(new byte[]{0x1d, 0x21, 0x01});
        SendCommad(new byte[]{0x1b, 0x61, 0x01});
        /*printString(OrganizationPath);*/
        printString(OrganizationName);
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        SendCommad(new byte[]{0x1d, 0x21, 0x00});
        /*printString(HeaderPath);*/
        printString("VISITOR");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing Image");
                imageprinting = true;
                PrintHumanImage(photo_img);
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
                    printString("   "+"\n");
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
                    printString("   "+"\n");
                }
                /*printString(EmptyPath);*/
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                SendCommad(new byte[]{0x1b, 0x61, 0x00});
                PrintData();
                printString("   "+"\n");
                printString("   "+"\n");
                /*printString(DataPath);
                printString(EmptyPath);
                printString(EmptyPath);*/
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

    private void MobileNoSuggestThread() {
        Log.d("debug", "MobileNo Suggest Timer Started");
        mobilesuggestthread = null;
        Runnable runnable = new SuggestTimer();
        mobilesuggestthread = new Thread(runnable);
    }

    class SuggestTimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    suggesting();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void suggesting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (details.isMobileAutoSuggestSuccess()) {
                        details.setMobileAutoSuggestSuccess(false);
                        Successview();
                        Extrafields();
                        mobilesuggestsuccess = true;
                        if (otpcheck) {
                            otpcheck = false;
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            editor.putString("OTP", ""+num);
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showToast("OTP Sent");
                            showdialog(OTP_DLG);
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                    SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            mobile_et.setText(Mobile);
                            name_et.requestFocus();
                        }
                        mobilesuggestthread.interrupt();
                    }
                    if (details.isMobileAutoSuggestFailure()) {
                        details.setMobileAutoSuggestFailure(false);
                        Extrafields();
                        if (otpcheck) {
                            otpcheck = false;
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            editor.putString("OTP", ""+num);
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showToast("OTP Sent");
                            showdialog(OTP_DLG);
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                    SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            mobile_et.setText(Mobile);
                            name_et.requestFocus();
                        }
                        mobilesuggestthread.interrupt();
                    }
                    if (details.isMobileNoExist()) {
                        details.setMobileNoExist(false);
                        showdialog(MOBILE_DLG);
                        mobilesuggestthread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Capturing Camera Image will launch camera app request image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        previewCapturedImage();
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                functionCalls.checkimage_and_delete("Hello Camera", Mobile, fileUri.getPath());
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            /*options.inSampleSize = 8;*/

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            LogStatus("Image Size: "+sizeOf(bitmap));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError outOfMemoryError) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            LogStatus("Image Size: "+sizeOf(bitmap));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        // create new matrix
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == 1) {
            bmp = src;
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(),
                "Entrylog" + File.separator + IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        Random rand = new Random();
        int num = rand.nextInt(9000) + 1000;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + Mobile+""+num + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public String compressImage(String imageUri) {

        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = mediaFile.getPath();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public void PrintData() {

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
                    /*myOutWriter.append(Display+": ");
                    if (Display.equals("Name")) {
                        myOutWriter.append(Name + "\r\n");*//*
                        printString(Display+": "+Name+"\n");*//*
                    }
                    if (Display.equals("Mobile")) {
                        myOutWriter.append(Mobile + "\r\n");*//*
                        printString(Display+": "+Mobile+"\n");*//*
                    }
                    if (Display.equals("From")) {
                        myOutWriter.append(FromAddress + "\r\n");*//*
                        printString(Display+": "+FromAddress+"\n");*//*
                    }
                    if (Display.equals("To Meet")) {
                        myOutWriter.append(ToMeet + "\r\n");*//*
                        printString(Display+": "+ToMeet+"\n");*//*
                    }
                    if (Display.equals("Date")) {
                        if (!reprint) {
                            DateTime = CurrentDate() + " " + CurrentTime() + "\r\n";
                            myOutWriter.append(DateTime);*//*
                            printString(Display+": "+DateTime+"\n");*//*
                        } else {
                            myOutWriter.append(DateTime);*//*
                            printString(Display+": "+DateTime+"\n");*//*
                        }
                    }
                    if (Display.equals("Visitor Designation")) {
                        myOutWriter.append(Visitor_Designation + "\r\n");*//*
                        printString(Display+": "+Visitor_Designation+"\n");*//*
                    }
                    if (Display.equals("Department")) {
                        myOutWriter.append(Department + "\r\n");*//*
                        printString(Display+": "+Department+"\n");*//*
                    }
                    if (Display.equals("Purpose")) {
                        myOutWriter.append(Purpose + "\r\n");*//*
                        printString(Display+": "+Purpose+"\n");*//*
                    }
                    if (Display.equals("House No")) {
                        myOutWriter.append(House_number + "\r\n");*//*
                        printString(Display+": "+House_number+"\n");*//*
                    }
                    if (Display.equals("Flat No")) {
                        myOutWriter.append(Flat_number + "\r\n");
                        *//*printString(Display+": "+Flat_number+"\n");*//*
                    }
                    if (Display.equals("Block")) {
                        myOutWriter.append(Block + "\r\n");*//*
                        printString(Display+": "+Block+"\n");*//*
                    }
                    if (Display.equals("No of Visitor")) {
                        myOutWriter.append(No_Visitor + "\r\n");*//*
                        printString(Display+": "+No_Visitor+"\n");*//*
                    }
                    if (Display.equals("Class")) {
                        myOutWriter.append(aClass + "\r\n");
                        *//*printString(Display+": "+aClass+"\n");*//*
                    }
                    if (Display.equals("Section")) {
                        myOutWriter.append(Section + "\r\n");
                        *//*printString(Display+": "+Section+"\n");*//*
                    }
                    if (Display.equals("Student")) {
                        myOutWriter.append(Student_Name + "\r\n");*//*
                        printString(Display+": "+Student_Name+"\n");*//*
                    }
                    if (Display.equals("Id Card")) {
                        myOutWriter.append(ID_Card + "\r\n");*//*
                        printString(Display+": "+ID_Card+"\n");*//*
                    }
                    if (Display.equals("Entry")) {
                        myOutWriter.append(User + "\r\n");*//*
                        printString(Display+": "+User+"\n");*//*
                    }
                    if (Display.equals("Email")) {
                        myOutWriter.append(Email + "\r\n");
                        *//*printString(Display+": "+Email+"\n");*//*
                    }
                    if (Display.equals("Vehicle Number")) {
                        myOutWriter.append(Vehicleno + "\r\n");
                        *//*printString(Display+": "+Vehicleno+"\n");*//*
                    }*/
                    /*myOutWriter.append(Display+": ");*/
                    if (Display.equals("Name")) {
                        /*myOutWriter.append(Name + "\r\n");*/
                        printString(Display+": "+Name/*+"\n"*/);
                    }
                    if (Display.equals("Mobile")) {
                        /*myOutWriter.append(Mobile + "\r\n");*/
                        printString(Display+": "+Mobile/*+"\n"*/);
                    }
                    if (Display.equals("From")) {
                        /*myOutWriter.append(FromAddress + "\r\n");*/
                        printString(Display+": "+FromAddress/*+"\n"*/);
                    }
                    if (Display.equals("To Meet")) {
                        /*myOutWriter.append(ToMeet + "\r\n");*/
                        printString(Display+": "+ToMeet/*+"\n"*/);
                    }
                    if (Display.equals("Date")) {
                        if (!reprint) {
                            DateTime = CurrentDate() + " " + CurrentTime()/* + "\r\n"*/;
                            /*myOutWriter.append(DateTime);*/
                            printString(Display+": "+DateTime/*+"\n"*/);
                        } else {
                            /*myOutWriter.append(DateTime);*/
                            printString(Display+": "+DateTime/*+"\n"*/);
                        }
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
                        /*myOutWriter.append(User + "\r\n");*/
                        printString(Display+": "+User/*+"\n"*/);
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

    @Override
    protected void onDestroy() {
        timehandler.removeCallbacks(timerunnable);
        if (mSerialPort != null)
            mSerialPort.close();
        mSerialPort = null;
        EnableBtn(false);
        functionCalls.deleteTextfile("Organization.txt");
        functionCalls.deleteTextfile("Header.txt");
        functionCalls.deleteTextfile("Empty.txt");
        functionCalls.deleteTextfile("Data.txt");
        super.onDestroy();
    }

    private String CurrentDate() {
        Calendar cal = Calendar.getInstance();
        int curyear = cal.get(Calendar.YEAR);
        int curmonth = cal.get(Calendar.MONTH);
        int curdate = cal.get(Calendar.DAY_OF_MONTH);
        String Currentdate = "" + curdate + "/" + "" + (curmonth + 1) + "/" + curyear;
        Date Starttime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Starttime = new SimpleDateFormat("dd/MM/yyyy").parse(Currentdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Date = sdf.format(Starttime);
        return Date;
    }

    private String CurrentTime() {
        Calendar cal = Calendar.getInstance();
        int curhour = cal.get(Calendar.HOUR_OF_DAY);
        int curminute = cal.get(Calendar.MINUTE);
        String minute = "" + curminute;
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        String Currenttime = "" + curhour + ":" + minute;
        Date Starttime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        try {
            Starttime = new SimpleDateFormat("HH:mm").parse(Currenttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Time = sdf.format(Starttime);
        return Time;
    }

    private void showdialog(int id) {
        switch (id) {
            case START_DLG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Mobile Number");
                LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogview, null);
                builder.setView(ll);
                builder.setCancelable(false);
                etmobile = (EditText) ll.findViewById(R.id.dialogmobile_etTxt);

                etmobile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String number = editable.toString();
                        int test = number.length();
                        if (test >= 1) {
                            String trimnumber = number.substring(0, 1);
                            int num = Integer.parseInt(trimnumber);
                            if (num == 7 || num == 8 || num == 9) {

                            } else {
                                etmobile.setText("");
                            }
                        }
                    }
                });
                if (settings.getString("OTPAccess", "").equals("Yes")) {
                    builder.setPositiveButton("OTP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "1";
                            otpcheck = true;
                            checkmobilesuggest(etmobile);
                        }
                    });
                } else {
                    builder.setPositiveButton("MANUAL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "2";
                            manualcheck = true;
                            checkmobilesuggest(etmobile);
                        }
                    });
                }
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alert1 = builder.create();
                alert1.show();
                break;

            case END_DLG:
                AlertDialog.Builder endbuilder = new AlertDialog.Builder(this);
                endbuilder.setTitle("Printing Details");
                endbuilder.setCancelable(false);
                endbuilder.setMessage("Did a Data got a printed correctly...??");
                endbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reprint = true;
                        PrintingData();
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;

            case MOBILE_DLG:
                AlertDialog.Builder existbuilder = new AlertDialog.Builder(this);
                existbuilder.setTitle("Visitor Details");
                existbuilder.setCancelable(false);
                existbuilder.setMessage("Entered Mobile is already Logged In.. To Check In again please checkout it manually..");
                existbuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog existalert = existbuilder.create();
                existalert.show();
                break;

            case OTP_DLG:
                final AlertDialog.Builder otpbuilder = new AlertDialog.Builder(this);
                otpbuilder.setTitle("Mobile Number");
                if (!otpresent) {
                    otpbuilder.setMessage(Mobile);
                } else {
                    if (otpcount == 2) {
                        otpbuilder.setMessage("OTP has been resent 2 times"+"\n"+Mobile);
                    } else {
                        otpbuilder.setMessage("OTP has been resent"+"\n"+Mobile);
                    }
                }
                LinearLayout otpll = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogview, null);
                otpbuilder.setView(otpll);
                otpbuilder.setCancelable(false);
                TextInputLayout tilmobile = (TextInputLayout) otpll.findViewById(R.id.timer_Til);
                tilmobile.setVisibility(View.GONE);
                TextInputLayout tilotp = (TextInputLayout) otpll.findViewById(R.id.otp_Til);
                tilotp.setVisibility(View.VISIBLE);
                final EditText otpetTxt = (EditText) otpll.findViewById(R.id.dialogotp_etTxt);

                otpbuilder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!otpetTxt.getText().toString().equals("")) {
                            String OTP = otpetTxt.getText().toString();
                            String SavedOTP = settings.getString("OTP", "");
                            if (OTP.equals(SavedOTP)) {
                                if (mobilesuggestsuccess) {
                                    AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                            SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    addvisitorslayout.setVisibility(View.VISIBLE);
                                } else {
                                    addvisitorslayout.setVisibility(View.VISIBLE);
                                    mobile_et.setText(Mobile);
                                    name_et.requestFocus();
                                }
                            } else {
                                otpetTxt.setError("Entered OTP is not matching please enter correct one..");
                                otpetTxt.setText("");
                                showToast("Entered OTP is not matching please enter correct one..");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showdialog(OTP_DLG);
                                    }
                                }, 500);
                            }
                        } else {
                            otpetTxt.setError("Please enter OTP");
                            showToast("Please enter OTP");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showdialog(OTP_DLG);
                                }
                            }, 500);
                        }
                    }
                });
                otpbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                if (otpcount == 2) {
                    otpbuilder.setNeutralButton("MANUAL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "2";
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            if (mobilesuggestsuccess) {
                                AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                        SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            } else {
                                mobile_et.setText(Mobile);
                                name_et.requestFocus();
                            }
                        }
                    });
                } else {
                    otpbuilder.setNeutralButton("RESEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            otpcount = otpcount + 1;
                            otpresent = true;
                            otpbuilder.setMessage("OTP has been resent"+"\n"+Mobile);
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showdialog(OTP_DLG);
                            showToast("OTP Resent");
                        }
                    });
                }
                AlertDialog alert2 = otpbuilder.create();
                alert2.show();
                /*if (otpresent) {
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                }*/
                break;
        }
    }

    private void checkmobilesuggest(EditText etmobile) {
        if (!etmobile.getText().toString().equals("")) {
            Mobile = etmobile.getText().toString();
            if (Mobile.length() == 10) {
                etmobile.setText("");
                MobileAutoSuggest mobile = task.new MobileAutoSuggest(details, Organizationid, Mobile, mProgressBar,
                        AddVisitors_EL101.this);
                mobile.execute();
                mobilesuggestthread.start();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Please Enter Valid Mobile Number");
                        showdialog(START_DLG);
                    }
                }, 1000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast("Enter Mobile Number");
                    showdialog(START_DLG);
                }
            }, 1000);
        }
    }

    private void Successview() {
        name_et.setText(details.getVisitors_Name());
        email_et.setText(details.getVisitors_Email());
        mobile_et.setText(Mobile);
        address_et.setText(details.getVisitors_Address());
        tomeet_et.setText(details.getVisitors_tomeet());
        vehicle_et.setText(details.getVisitors_VehicleNo());
        String Image_Url = "http://www.tellservice.com/entrylog/visitor_images/";
        String Image = details.getVisitors_Photo();
        String Image_Path = Image_Url + Image;
        Picasso.with(AddVisitors_EL101.this).load(Image_Path).into(photo_img);
        Et_field1.setText(details.getVisitor_Designation());
        Et_field2.setText(details.getDepartment());
        Et_field3.setText(details.getPurpose());
        Et_field4.setText(details.getHouse_number());
        Et_field5.setText(details.getFlat_number());
        Et_field6.setText(details.getBlock());
        Et_field7.setText(details.getNo_Visitor());
        Et_field8.setText(details.getaClass());
        Et_field9.setText(details.getSection());
        Et_field10.setText(details.getStudent_Name());
        Et_field11.setText(details.getID_Card());
        UpdateVisitorImage = "No";
        Visitorsimage = true;
    }

    private void Extrafields() {
        LogStatus("Fetch field Started");
        HashSet<String> hashSet = new HashSet<>();
        hashSet = fieldsService.fieldset;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(hashSet);
        if (arrayList.size() > 0) {
            LogStatus("Size is more than 1");
            for (int i = 0; i < arrayList.size(); i++) {
                String value = arrayList.get(i).toString();
                LogStatus("Value["+i+"]: "+value);
                if (value.equals("Email")) {
                    emailLayout.setVisibility(View.VISIBLE);
                    emailLayout.setHint("Email Address");
                }
                if (value.equals("Visitor Designation")) {
                    Til_field1.setVisibility(View.VISIBLE);
                    Til_field1.setHint(value);
                }
                if (value.equals("Department")) {
                    Til_field2.setVisibility(View.VISIBLE);
                    Til_field2.setHint(value);
                }
                if (value.equals("Purpose")) {
                    Til_field3.setVisibility(View.VISIBLE);
                    Til_field3.setHint(value);
                }
                if (value.equals("House No")) {
                    Til_field4.setVisibility(View.VISIBLE);
                    Til_field4.setHint(value);
                }
                if (value.equals("Flat No")) {
                    Til_field5.setVisibility(View.VISIBLE);
                    Til_field5.setHint(value);
                }
                if (value.equals("Block")) {
                    Til_field6.setVisibility(View.VISIBLE);
                    Til_field6.setHint(value);
                }
                if (value.equals("No of Visitor")) {
                    Til_field7.setVisibility(View.VISIBLE);
                    Til_field7.setHint("No. of Visitor");
                }
                if (value.equals("Class")) {
                    Til_field8.setVisibility(View.VISIBLE);
                    Til_field8.setHint(value);
                }
                if (value.equals("Section")) {
                    Til_field9.setVisibility(View.VISIBLE);
                    Til_field9.setHint(value);
                }
                if (value.equals("Student Name")) {
                    Til_field10.setVisibility(View.VISIBLE);
                    Til_field10.setHint(value);
                }
                if (value.equals("ID Card No")) {
                    Til_field11.setVisibility(View.VISIBLE);
                    Til_field11.setHint(value);
                }
            }
        } else {
            LogStatus("No Fields Available");
            showToast("No Fields Available");
        }
        LogStatus("Staff field Started");
        HashSet<String> StaffSet = new HashSet<>();
        StaffSet = staffService.staffset;
        stafflist = new ArrayList<>();
        stafflist.addAll(StaffSet);
        if (stafflist.size() > 0) {
            LogStatus("Staff list Available");
            Staffadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stafflist);
            tomeet_et.setAdapter(Staffadapter);
            Collections.sort(stafflist);
            Staffadapter.notifyDataSetChanged();
            tomeet_et.setThreshold(1);
        } else {
            LogStatus("Staff list not Available");
        }
    }

    private void getExtraFields() {
        if (emailLayout.getVisibility() == View.VISIBLE) {
            Email = email_et.getText().toString();
        }
        if (Til_field1.getVisibility() == View.VISIBLE) {
            Visitor_Designation = Et_field1.getText().toString();
        }
        if (Til_field2.getVisibility() == View.VISIBLE) {
            Department = Et_field2.getText().toString();
        }
        if (Til_field3.getVisibility() == View.VISIBLE) {
            Purpose = Et_field3.getText().toString();
        }
        if (Til_field4.getVisibility() == View.VISIBLE) {
            House_number = Et_field4.getText().toString();
        }
        if (Til_field5.getVisibility() == View.VISIBLE) {
            Flat_number = Et_field5.getText().toString();
        }
        if (Til_field6.getVisibility() == View.VISIBLE) {
            Block = Et_field6.getText().toString();
        }
        if (Til_field7.getVisibility() == View.VISIBLE) {
            No_Visitor = Et_field7.getText().toString();
        }
        if (Til_field8.getVisibility() == View.VISIBLE) {
            aClass = Et_field8.getText().toString();
        }
        if (Til_field9.getVisibility() == View.VISIBLE) {
            Section = Et_field9.getText().toString();
        }
        if (Til_field10.getVisibility() == View.VISIBLE) {
            Student_Name = Et_field10.getText().toString();
        }
        if (Til_field11.getVisibility() == View.VISIBLE) {
            ID_Card = Et_field11.getText().toString();
        }
    }

    private void showToast(String message) {
        Toast.makeText(AddVisitors_EL101.this, message, Toast.LENGTH_SHORT).show();
    }

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

        byte[] body=ArrayUtil.stringToBytes(str, str.length());
        byte[] total=ArrayUtil.MergerArray(head, body);
        // total=ArrayUtil.MergerArray(total, new byte[]{0x0A,0x1b,0x4a,0x30});
        // byte[] test=new byte[]{0x1e,0x42,0x34,0x50,0x32,0x30,0x0a,0x31,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x41,0x42,0x0a};

        // mHandler.sendEmptyMessageDelayed(0x16, 200);
        SendCommad(total);


        //add HRI text
        /*
        try {
        	 SendCommad(new byte[]{0x1b,0x64,6});
			SendCommad(addEnter(str.getBytes("GB2312")));
			//set align left
			SendCommad(new byte[]{0x1b,0x61,0x00});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        */
        //feed paper
        /*mHandler.sendEmptyMessageDelayed(FEED, 100);
        //return printer status
        mHandler.sendEmptyMessageDelayed(PAPER_TEST, 200);*/
    }

    public void printString(String str) {
        if((str!=null)&&(str.getBytes().length!=0)){
            byte[] send = null;
            try {
                //send = addEnter(str.getBytes("ISO8859-16"));
                //??????????? ????GBK2312,?????????UTF-8 ,?????????iso859-16
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
                        //send = addEnter(str.getBytes("ISO8859-16"));
                        //??????????? ????GBK2312,?????????UTF-8 ,?????????iso859-16
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
                Toast.makeText(AddVisitors_EL101.this, "Sorry this Device will not connect to EL 101/102.. " +
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

    public String PrintImage2(ImageView imageview) {
        Bitmap actualbitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        int width=256;
        int height=192;
        Bitmap bitMap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
        /*Drawable drawable = this.getResources().getDrawable(R.drawable.abcde);
        Bitmap actualbitmap = ((BitmapDrawable) drawable).getBitmap();
        int width = 256;
        int height = 192;
        Bitmap bitMap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);*/
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
            Message msg = Message.obtain();
            msg.what = SendCommand;
            msg.obj = sendbytesNew;
            /*Log.d("debug", "Message result: "+HexDump.dumpHex((byte[]) msg.obj));
            *//*String temp = HexDump.dumpHex((byte[]) msg.obj);*/
            mHandler.sendMessageDelayed(msg, 180 * row);
        }
        //feed paper
        mHandler.sendEmptyMessageDelayed(FEED, length * 180);
        // test no paper
        mHandler.sendEmptyMessageDelayed(PAPER_TEST, length * 180 + 180);
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

        //feed paper
        mHandler.sendEmptyMessageDelayed(FEED, 180);
        // test no paper
        mHandler.sendEmptyMessageDelayed(PAPER_TEST, 180 + 180);

        return set;
    }

    private void LogStatus(String str) {
        Log.d("debug", str);
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
}